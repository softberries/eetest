package pl.cube.eetest.controller;

import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateful;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.logging.Logger;
import org.jboss.seam.solder.logging.Category;

import pl.cube.eetest.model.Stock;
import pl.cube.eetest.model.SessionFile;
import au.com.bytecode.opencsv.CSVReader;

@Stateful
@Model
public class StockMgr {

	private static final String BASE_FOLDER = "/home/kris/Desktop/bosbaza/";

	private static final BigDecimal HUNDRED = new BigDecimal(100.00);
	private static final BigDecimal ONE = new BigDecimal(1.00);
	
	@Inject
	private EntityManager em;

	@Inject
	@Category("eetest")
	private Logger log;
	
	private long counter = 0L;

	public long importStocksCount(){
		this.importStocks();
		return this.counter;
	}
	public void updateStocks(){
		log.info("updateStocks");
		this.counter = 0;
		try {
			List<String> tickerFiles = getTickerFiles(".prn");
			Set<SessionFile> existingTickers = getExistingTickers();
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
						List<Stock> lastStocks = findLastStocks(stock.getTicker(), 14);
						lastStocks.add(stock);
						calculateRSI(lastStocks);
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
		System.out.println("CounteR: " + counter);
	}
	public void importStocks() {
		log.info("importStocks");
		try {
			List<String> tickerFiles = getTickerFiles(".mst");
			Set<SessionFile> existingTickers = getExistingTickers();
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
		System.out.println("CounteR: " + counter);
	}

	public void calculateSMA(List<Stock> stocks, int timePeriods){
		if(stocks.size() > timePeriods){
			
		}
	}
	public void calculateEMA(List<Stock> stocks, int timePeriods){
		
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
				System.out.println("RSI: " + rsi);
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
			BigDecimal lastClose = findLastClose(nextLine[0]);
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
			counter++;
			result.add(stock);
		}
		return result;
	}

	private Set<SessionFile> getExistingTickers() {
		Query query = em.createQuery("SELECT t FROM SessionFile t");
	    return new HashSet<SessionFile>(query.getResultList());
	}
	private BigDecimal findLastClose(String ticker) {
		Query query = em.createQuery("SELECT s.priceClose FROM Stock s where s.ticker = :tick ORDER BY s.stockDate DESC");
		query.setMaxResults(1);
		query.setParameter("tick", ticker);
		List<BigDecimal> res = query.getResultList();
		if(res != null && res.size() > 0){
			return res.get(0);
		}
		return null;
	}
	private List<Stock> findLastStocks(String ticker, int size) {
		Query query = em.createQuery("SELECT s FROM Stock s where s.ticker = :tick ORDER BY s.stockDate DESC");
		query.setParameter("tick", ticker);
		query.setMaxResults(size);
		return query.getResultList();
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
			counter++;
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
