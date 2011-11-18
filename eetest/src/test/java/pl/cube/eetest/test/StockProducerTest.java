package pl.cube.eetest.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.junit.Test;

import pl.cube.eetest.data.StockProducer;
import pl.cube.eetest.model.Stock;

public class StockProducerTest {

	@Inject
	Logger log;
	
	@Test
	public void readHistoricalStockTest() throws Exception{
		System.out.println("readhistoricalstocks");
		StockProducer mgr = new StockProducer();
		List<String> tickers = mgr.getTickerFiles(".mst");
		for(String ticker : tickers){
			List<Stock> stocks = mgr.readHistoricalStock(ticker);
			assertNotNull(stocks);
			assertTrue(stocks.size() >= 1);
		}
	}
	@Test
	public void caluclateRSITest() throws ParseException{
		StockProducer mgr = new StockProducer();
		List<Stock> stocks = getTestStocks(mgr);
		mgr.calculateRSI(stocks);
	}
	
	@Test
	public void calculateSMA() throws Exception{
		StockProducer mgr = new StockProducer();
		List<Stock> stocks = getTestStocks(mgr);
		mgr.calculateSMA(stocks, 10);
	}
	
	@Test
	public void calculateEMA() throws Exception{
		StockProducer mgr = new StockProducer();
		List<Stock> stocks = getTestStocks(mgr);
		mgr.calculateEMA(stocks, 10);
		mgr.calculateMACDLine(stocks);
//		mgr.calculateMACDSignalLine(stocks);
		
	}
	@Test
	public void readLatestStock(){
		System.out.println("readcurrentstocks");
		StockProducer mgr = new StockProducer();
	}
	@Test
	public void getDateFromInput() throws ParseException{
		System.out.println("getdatefromstock");
		StockProducer mgr = new StockProducer();
		Date date = mgr.getDateFromInput("20101130");
		assertNotNull(date);
		System.out.println(date);
	}
	@Test
	public void getTickers(){
		StockProducer mgr = new StockProducer();
		List<String> list =  mgr.getTickerFiles(".mst");
		assertNotNull(list);
	}
	private List<Stock> getTestStocks(StockProducer mgr) throws ParseException {
		List<Stock> stocks = new ArrayList<Stock>();
		
		Stock s1 = new Stock();
		s1.setStockDate(mgr.getDateFromInput("20101001"));
		s1.setPriceChange(new BigDecimal("0.0"));
		s1.setPriceClose(new BigDecimal("22.27"));
		stocks.add(s1);
		
		Stock s2 = new Stock();
		s2.setStockDate(mgr.getDateFromInput("20101002"));
		s2.setPriceChange(new BigDecimal("-0.25"));
		s2.setPriceClose(new BigDecimal("22.19"));
		stocks.add(s2);
		
		Stock s3 = new Stock();
		s3.setStockDate(mgr.getDateFromInput("20101003"));
		s3.setPriceChange(new BigDecimal("0.06"));
		s3.setPriceClose(new BigDecimal("22.08"));
		stocks.add(s3);
		
		Stock s4 = new Stock();
		s4.setStockDate(mgr.getDateFromInput("20101004"));
		s4.setPriceChange(new BigDecimal("-0.54"));
		s4.setPriceClose(new BigDecimal("22.17"));
		stocks.add(s4);
		
		Stock s5 = new Stock();
		s5.setStockDate(mgr.getDateFromInput("20101005"));
		s5.setPriceChange(new BigDecimal("0.72"));
		s5.setPriceClose(new BigDecimal("22.18"));
		stocks.add(s5);
		
		Stock s6 = new Stock();
		s6.setStockDate(mgr.getDateFromInput("20101006"));
		s6.setPriceChange(new BigDecimal("0.5"));
		s6.setPriceClose(new BigDecimal("22.13"));
		stocks.add(s6);
		
		Stock s7 = new Stock();
		s7.setStockDate(mgr.getDateFromInput("20101007"));
		s7.setPriceChange(new BigDecimal("0.27"));
		s7.setPriceClose(new BigDecimal("22.23"));
		stocks.add(s7);
		
		Stock s8 = new Stock();
		s8.setStockDate(mgr.getDateFromInput("20101008"));
		s8.setPriceChange(new BigDecimal("0.33"));
		s8.setPriceClose(new BigDecimal("22.43"));
		stocks.add(s8);
		
		Stock s9 = new Stock();
		s9.setStockDate(mgr.getDateFromInput("20101009"));
		s9.setPriceChange(new BigDecimal("0.42"));
		s9.setPriceClose(new BigDecimal("22.24"));
		stocks.add(s9);
		
		Stock s10 = new Stock();
		s10.setStockDate(mgr.getDateFromInput("20101010"));
		s10.setPriceChange(new BigDecimal("0.24"));
		s10.setPriceClose(new BigDecimal("22.29"));
		stocks.add(s10);
		
		Stock s11= new Stock();
		s11.setStockDate(mgr.getDateFromInput("20101011"));
		s11.setPriceChange(new BigDecimal("-0.19"));
		s11.setPriceClose(new BigDecimal("22.15"));
		stocks.add(s11);
		
		Stock s12 = new Stock();
		s12.setStockDate(mgr.getDateFromInput("20101012"));
		s12.setPriceChange(new BigDecimal("0.14"));
		s12.setPriceClose(new BigDecimal("22.39"));
		stocks.add(s12);
		
		Stock s13 = new Stock();
		s13.setStockDate(mgr.getDateFromInput("20101013"));
		s13.setPriceChange(new BigDecimal("-0.42"));
		s13.setPriceClose(new BigDecimal("22.38"));
		stocks.add(s13);
		
		Stock s14 = new Stock();
		s14.setStockDate(mgr.getDateFromInput("20101014"));
		s14.setPriceChange(new BigDecimal("0.67"));
		s14.setPriceClose(new BigDecimal("22.61"));
		stocks.add(s14);
		
		Stock s15 = new Stock();
		s15.setStockDate(mgr.getDateFromInput("20101015"));
		s15.setPriceChange(new BigDecimal("0.0"));
		s15.setPriceClose(new BigDecimal("23.36"));
		stocks.add(s15);
		
		Stock s16 = new Stock();
		s16.setStockDate(mgr.getDateFromInput("20101016"));
		s16.setPriceChange(new BigDecimal("-0.28"));
		s16.setPriceClose(new BigDecimal("24.05"));
		stocks.add(s16);
		
		Stock s17 = new Stock();
		s17.setStockDate(mgr.getDateFromInput("20101017"));
		s17.setPriceChange(new BigDecimal("0.03"));
		s17.setPriceClose(new BigDecimal("23.75"));
		stocks.add(s17);
		
		Stock s18 = new Stock();
		s18.setStockDate(mgr.getDateFromInput("20101018"));
		s18.setPriceChange(new BigDecimal("0.38"));
		s18.setPriceClose(new BigDecimal("23.83"));
		stocks.add(s18);
		
		return stocks;
	}
	@Test
    public void testSplitList() throws Exception
    {
        List<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i<1234; i++){
        	list.add(new Integer(i));
        }
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        splitList(list, result, 10);
        System.out.println("result size(): " + result.size());
        for(List<Integer> li : result){
        	System.out.println("r: " + li.size());
        }
    }
    
    private void splitList(List<Integer> startList, List<List<Integer>> resultList, 
	        int subListSize) {
	    if(startList.size() < subListSize){
	    	resultList.add(startList);
	    	return;
	    }else{
	    	final int numberOfLists = startList.size() / subListSize;
	    	int remainingItems = startList.size() - (numberOfLists * subListSize);
	    	int startIndex = 0;
	    	int stopIndex = 0;
	    	for(int i = 1; i < numberOfLists+1; i++){
	    		stopIndex = i * subListSize;
	    		resultList.add(startList.subList(startIndex, stopIndex));
	    		startIndex = stopIndex;
	    	}
	    	if(remainingItems != 0){
	    		resultList.add(startList.subList(startIndex, startIndex + remainingItems));
	    	}
	    }
	}
}
