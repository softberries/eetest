package pl.cube.eetest.data;

import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateful;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.logging.Logger;
import org.jboss.seam.solder.logging.Category;

import pl.cube.eetest.model.SessionFile;
import pl.cube.eetest.model.Stock;
import au.com.bytecode.opencsv.CSVReader;

@Stateful
@Model
public class StockProducer {

	private static final String BASE_FOLDER = "/home/kris/Desktop/bosbaza/";

	private static final BigDecimal HUNDRED = new BigDecimal(100.00);
	private static final BigDecimal ONE = new BigDecimal(1.00);
	
	@Inject
	private EntityManager em;
	
	@Inject
	private StockDao stockDao;

	@Inject
	@Category("eetest")
	private Logger log;

	public void updateStocks(){
		log.info("updateStocks");
		try {
			List<String> tickerFiles = getTickerFiles(".prn");
			Set<SessionFile> existingTickers = stockDao.getExistingTickers();
			for (String ticker : tickerFiles) {
					log.info("Ticker File: " + ticker);
					if(existingTickers.contains(new SessionFile(ticker))){
						log.info("ticker file already exists: " + ticker);
						continue;
					}
					List<Stock> stocks = readPRNStock(ticker);
					System.out.println("Stocks size: " + stocks.size());
					log.info("persisting data for ticker: " + ticker);
					for (Stock stock : stocks) {
						List<Stock> lastStocks = stockDao.findLastStocks(stock.getTicker(), 14);
						lastStocks.add(stock);
						calculateRSI(lastStocks);
						calculateSMA(stocks, 12);
						calculateEMA(stocks, 12);
						calculateEMA(stocks, 26);
						calculateMACDLine(stocks);
						calculateMACDSignalLine(stocks);
						em.persist(stock);
					}
					SessionFile tic = new SessionFile();
					tic.setName(ticker);
					em.persist(tic);
					break;
					
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void importStocks() {
		log.info("importStocks");
		try {
			List<String> tickerFiles = getTickerFiles(".mst");
			Set<SessionFile> existingTickers = stockDao.getExistingTickers();
			for (String ticker : tickerFiles) {
					log.info("Ticker: " + ticker);
					if(existingTickers.contains(new SessionFile(ticker))){
						log.info("ticker already exists: " + ticker);
						continue;
					}
					List<Stock> stocks = readHistoricalStock(ticker);
					System.out.println("Stocks size: " + stocks.size());
					log.info("persisting data for ticker: " + ticker);
					calculateRSI(stocks);
					calculateSMA(stocks, 12);
					calculateEMA(stocks, 12);
					calculateEMA(stocks, 26);
					calculateMACDLine(stocks);
					calculateMACDSignalLine(stocks);
					
					for (Stock stock : stocks) {
						em.persist(stock);
					}
					SessionFile tic = new SessionFile();
					tic.setName(ticker);
					em.persist(tic);
					break;
					
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void calculateSMA(List<Stock> stocks, int timePeriods){
		Collections.sort(stocks);
		if(stocks.size() > timePeriods){
			for(int i=0;i <stocks.size()-(timePeriods -1);i++){
				List<Stock> sublist = stocks.subList(i, i+timePeriods);
				System.out.println("sublist size: " + sublist.size() + ", i: " + i + ", stocks.size(): " + stocks.size());
				Stock currentStock = stocks.get(i+timePeriods-1);
				BigDecimal sma = calculateSingleSma(sublist);
				currentStock.setSma(sma);
				System.out.println("SMA: " + sma);
			}
		}
	}
	private BigDecimal calculateSingleSma(List<Stock> sublist) {
		BigDecimal result = new BigDecimal("0.0");
		for(Stock s : sublist){
			result = result.add(s.getPriceClose());
		}
		return result.divide(new BigDecimal(sublist.size()).setScale(2), RoundingMode.HALF_UP);
	}
	public void calculateMACDLine(List<Stock> stocks){
		for(Stock s : stocks){
			if(s.getEma12() != null && s.getEma26() != null){
				s.setMacdLine(s.getEma12().subtract(s.getEma26()));
				System.out.println("MACD Line: " + s.getMacdLine());
			}
		}
	}
	public void calculateMACDSignalLine(List<Stock> stocks){
		BigDecimal smoothing = new BigDecimal(2).setScale(4).divide(new BigDecimal(10).setScale(4), RoundingMode.HALF_UP).setScale(4);
		Collections.sort(stocks);
		BigDecimal lastEMA = null;
		if(stocks.size() > 9){
			for(int i=0;i <stocks.size()-8;i++){
				List<Stock> sublist = stocks.subList(i, i+9);
				System.out.println("sublist size: " + sublist.size() + ", i: " + i + ", stocks.size(): " + stocks.size());
				Stock currentStock = stocks.get(i+9-1);
				if(currentStock.getMacdLine() == null){
					System.out.println("macd line is null");
					continue;
				}
				if(lastEMA == null){
					lastEMA = calculateSingleSma(sublist);
				}else{
					lastEMA = calculateSingleEma(smoothing, currentStock.getMacdLine(), lastEMA).setScale(2, RoundingMode.HALF_UP);
				}
				System.out.println("MACD Signal: " + lastEMA);
				currentStock.setMacdSignalLine(lastEMA);
				currentStock.setMacdHistogram(currentStock.getMacdLine().subtract(currentStock.getMacdSignalLine()));
				System.out.println("MACD Histogram: " + currentStock.getMacdHistogram());
			}
		}
	}
	public void calculateEMA(List<Stock> stocks, int timePeriods){
		BigDecimal smoothing = new BigDecimal(2).setScale(4).divide(new BigDecimal(timePeriods + 1).setScale(4), RoundingMode.HALF_UP).setScale(4);
		System.out.println("Smoothing: " + smoothing);
		Collections.sort(stocks);
		BigDecimal lastEMA = null;
		if(stocks.size() > timePeriods){
			for(int i=0;i <stocks.size()-(timePeriods -1);i++){
				List<Stock> sublist = stocks.subList(i, i+timePeriods);
				System.out.println("sublist size: " + sublist.size() + ", i: " + i + ", stocks.size(): " + stocks.size());
				Stock currentStock = stocks.get(i+timePeriods-1);
				if(lastEMA == null){
					lastEMA = calculateSingleSma(sublist);
				}else{
					lastEMA = calculateSingleEma(smoothing, currentStock.getPriceClose(), lastEMA).setScale(2, RoundingMode.HALF_UP);
				}
				if(timePeriods == 12){
					System.out.println("EMA 12: " + lastEMA);
					currentStock.setEma12(lastEMA);
				}else if(timePeriods == 26){
					System.out.println("EMA 26: " + lastEMA);
					currentStock.setEma26(lastEMA);
				}else{
					System.out.println("EMA " + timePeriods + ": " + lastEMA);
				}
			}
		}
	}
	private BigDecimal calculateSingleEma(BigDecimal smoothing, BigDecimal priceClose, BigDecimal lastEMA) {
		System.out.println("Smoothing: " + smoothing + ", priceClose: " + priceClose + ", lastEma: " + lastEMA);
		return (smoothing.multiply(priceClose.subtract(lastEMA))).add(lastEMA);
	}
	public void calculateRSI(List<Stock> stocks) {
		//order by date
		Collections.sort(stocks);
		BigDecimal lastAvgGain = null;
		BigDecimal lastAvgLoss = null;
		if(stocks.size() > 14){
			for(int i=0;i <stocks.size()-14;i++){
				List<Stock> sublist = stocks.subList(i, i+14);
				System.out.println("sublist size: " + sublist.size() + ", i: " + i + ", stocks.size(): " + stocks.size());
				Stock currentStock = stocks.get(i+14);
				BigDecimal currentGain = stocks.get(i+14).getPriceChange();
				BigDecimal currentLoss = stocks.get(i+14).getPriceChange();
				if(currentGain.doubleValue() < 0.0){
					currentGain = new BigDecimal(0.0);
				}
				if(currentLoss.doubleValue() > 0.0){
					currentLoss = new BigDecimal(0.0);
				}
				if(i == 0){
					lastAvgGain = getAverageGain(sublist, null, currentGain);
					lastAvgLoss = getAvareageLoss(sublist, null, currentLoss);
//					System.out.println("suma avg gain first: " + lastAvgGain);
//					System.out.println("suma avg loss first: " + lastAvgLoss);
				}else{
					lastAvgGain = getAverageGain(sublist, lastAvgGain, currentGain);
					lastAvgLoss = getAvareageLoss(sublist, lastAvgLoss, currentLoss);
				}
				BigDecimal rsi = new BigDecimal(0.00);
				if(lastAvgLoss.abs().doubleValue() > 0){
					BigDecimal rs = (lastAvgGain).divide(lastAvgLoss.abs(),RoundingMode.HALF_UP);
					rsi = calculateSingleRSI(rs);
				}
				System.out.println("Close: " + currentStock.getPriceClose() + ", RSI: " + rsi);
				currentStock.setRsi(rsi);
			}
		}
		
	}

	private BigDecimal calculateSingleRSI(BigDecimal rs) {
		return HUNDRED.setScale(2).subtract(HUNDRED.setScale(2).divide(ONE.setScale(2).add(rs.setScale(2)).setScale(2),RoundingMode.HALF_UP)).setScale(2);
	}

	private BigDecimal getAvareageLoss(List<Stock> sublist, BigDecimal previous, BigDecimal currentLoss) {
		BigDecimal result = new BigDecimal("0.0");
		if(previous == null){
			for(Stock s : sublist){
				if(s.getPriceChange().doubleValue() < 0.0){
					result = result.add(s.getPriceChange());
				}
			}
			return result.divide(new BigDecimal(sublist.size()).setScale(2), RoundingMode.HALF_UP);
		}else{
			return ((previous.multiply(new BigDecimal(13.00))).add(currentLoss)).divide(new BigDecimal(14.00), RoundingMode.HALF_UP);
		}
	}

	private BigDecimal getAverageGain(List<Stock> sublist, BigDecimal previous, BigDecimal currentGain) {
		BigDecimal result = new BigDecimal("0.0");
		if(previous == null){
			for(Stock s : sublist){
				if(s.getPriceChange().doubleValue() > 0.00){
					result = result.add(s.getPriceChange());
				}
			}
			return result.divide(new BigDecimal(sublist.size()).setScale(2), RoundingMode.HALF_UP);
		}else{
			return ((previous.multiply(new BigDecimal(13.00))).add(currentGain)).divide(new BigDecimal(14.00), RoundingMode.HALF_UP);
		}
	}

	public List<Stock> readPRNStock(String ticker) throws Exception{
		List<Stock> result = new ArrayList<Stock>();
		CSVReader reader = new CSVReader(new FileReader(BASE_FOLDER + ticker + ".prn"), ',', '\'', 0);
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			Stock stock = new Stock();
			stock.setTicker(nextLine[0]);
			stock.setStockDate(getDateFromInput(nextLine[1]));
			stock.setPriceOpen(new BigDecimal(nextLine[2]));
			stock.setPriceHigh(new BigDecimal(nextLine[3]));
			stock.setPriceLow(new BigDecimal(nextLine[4]));
			stock.setPriceClose(new BigDecimal(nextLine[5]));
			stock.setVol(new BigDecimal(nextLine[6]));
			BigDecimal lastClose = stockDao.findLastClose(nextLine[0]);
			System.out.println("last close: " + lastClose);
			if (lastClose != null) {
				stock.setPriceChange(stock.getPriceClose().subtract(lastClose));
			}else{
				stock.setPriceChange(new BigDecimal("0.0"));
			}
			if(stock.getTicker() == null ||
					stock.getStockDate() == null ||
					stock.getPriceOpen() == null ||
					stock.getPriceHigh() == null || 
					stock.getPriceLow() == null ||
					stock.getPriceClose() == null ||
					stock.getVol() == null ||
					stock.getPriceChange() == null){
				throw new Exception("Stock object is invalid" + stock);
			}
			System.out.println("adding stock: " + stock);
			result.add(stock);
		}
		return result;
	}

	
	public List<Stock> readHistoricalStock(String ticker) throws Exception {
		List<Stock> result = new ArrayList<Stock>();
		CSVReader reader = new CSVReader(new FileReader(BASE_FOLDER + ticker + ".mst"), ',', '\'', 1);
		String[] nextLine;
		BigDecimal lastClose = null;
		while ((nextLine = reader.readNext()) != null) {
			Stock stock = new Stock();
			stock.setTicker(nextLine[0]);
			stock.setStockDate(getDateFromInput(nextLine[1]));
			stock.setPriceOpen(new BigDecimal(nextLine[2]));
			stock.setPriceHigh(new BigDecimal(nextLine[3]));
			stock.setPriceLow(new BigDecimal(nextLine[4]));
			stock.setPriceClose(new BigDecimal(nextLine[5]));
			stock.setVol(new BigDecimal(nextLine[6]));
			if (lastClose != null) {
				stock.setPriceChange(stock.getPriceClose().subtract(lastClose));
			}else{
				stock.setPriceChange(new BigDecimal("0.0"));
			}
			lastClose = stock.getPriceClose();
			if(stock.getTicker() == null ||
					stock.getStockDate() == null ||
					stock.getPriceOpen() == null ||
					stock.getPriceHigh() == null || 
					stock.getPriceLow() == null ||
					stock.getPriceClose() == null ||
					stock.getVol() == null ||
					stock.getPriceChange() == null){
				throw new Exception("Stock object is invalid" + stock);
			}
			System.out.println("adding stock: " + stock);
			result.add(stock);
		}
		return result;
	}
	public Date getDateFromInput(String inputDate) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		return formatter.parse(inputDate);
	}

	public List<String> getTickerFiles(final String extension) {
		List<String> res = new ArrayList<String>();
		File dir = new File(BASE_FOLDER);
		String[] children = dir.list();
		for (String str : children) {
			if (str.contains(extension)) {
				String tmp = str.replace(extension, "");
				res.add(tmp);
				System.out.println(tmp);
			}
		}
		return res;
	}
}
