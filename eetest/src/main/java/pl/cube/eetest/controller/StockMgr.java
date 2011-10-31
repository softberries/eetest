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
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.logging.Logger;
import org.jboss.seam.solder.logging.Category;

import pl.cube.eetest.model.Stock;
import au.com.bytecode.opencsv.CSVReader;

@Stateful
@Model
public class StockMgr {

	private static final String BASE_FOLDER = "/home/kris/Desktop/bosbaza/";

	@Inject
	private EntityManager em;

	@Inject
	@Category("eetest")
	private Logger log;
	
	private long counter = 0L;

	public void updateStocks() {
		log.info("updateStocks");
		try {
			List<String> tickers = getTickers();
			for (String ticker : tickers) {
				if(counter < 10000){
					log.info("Ticker: " + ticker);
					List<Stock> stocks = readHistoricalStock(ticker);
					System.out.println("Stocks size: " + stocks.size());
					log.info("persisting data for ticker: " + ticker);
					calculateRSI(stocks);
					for (Stock stock : stocks) {
						em.persist(stock);
					}
				}else{
					continue;
				}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("CounteR: " + counter);
	}

	public void calculateRSI(List<Stock> stocks) {
		//order by date
		Collections.sort(stocks);
		BigDecimal lastAvgGain = null;
		if(stocks.size() > 14){
			for(int i=0;i<stocks.size();++i){
				List<Stock> sublist = stocks.subList(i, i+14);
				System.out.println("sublist size: " + sublist.size());
				BigDecimal currentGain = stocks.get(i+14).getPriceChange();
				if(currentGain.doubleValue() < 0.0){
					currentGain = new BigDecimal(0.0);
				}
				if(i == 0){
					lastAvgGain = getAverageGain(sublist, null, currentGain);
					System.out.println("suma avg gain first: " + lastAvgGain);
				}else{
					lastAvgGain = getAverageGain(sublist, lastAvgGain, currentGain);
					System.out.println("suma avg gain: " + lastAvgGain);
				}
			}
		}
		
	}

	private BigDecimal getAverageGain(List<Stock> sublist, BigDecimal previous, BigDecimal currentGain) {
		BigDecimal result = new BigDecimal("0.0");
		if(previous == null){
			for(Stock s : sublist){
				if(s.getPriceChange().doubleValue() > 0.0){
					System.out.println("Gain: " + s.getPriceChange());
					result = result.add(s.getPriceChange());
				}
			}
			System.out.println("Gain suma: " + result);
			return result.divide(new BigDecimal(sublist.size()), RoundingMode.HALF_UP);
		}else{
			System.out.println("prev: " + previous + ", current: " + currentGain);
			return ((previous.multiply(new BigDecimal(13))).add(currentGain)).divide(new BigDecimal(14), RoundingMode.HALF_UP);
		}
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

	public List<Stock> readLatestStock(String ticker) {
		log.debug("Read latest stock: " + ticker);
		List<Stock> result = new ArrayList<Stock>();

		return result;
	}

	public Date getDateFromInput(String inputDate) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		return formatter.parse(inputDate);
	}

	public List<String> getTickers() {
		List<String> res = new ArrayList<String>();
		File dir = new File(BASE_FOLDER);
		String[] children = dir.list();
		for (String str : children) {
			if (str.contains(".mst")) {
				String tmp = str.replace(".mst", "");
				res.add(tmp);
				System.out.println(tmp);
			}
		}
		return res;
	}
}
