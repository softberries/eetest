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

import pl.cube.eetest.controller.StockMgr;
import pl.cube.eetest.model.Stock;

public class StockMgrTest {

	@Inject
	Logger log;
	
	@Test
	public void readHistoricalStockTest() throws Exception{
		System.out.println("readhistoricalstocks");
		StockMgr mgr = new StockMgr();
		List<String> tickers = mgr.getTickerFiles(".mst");
		for(String ticker : tickers){
			List<Stock> stocks = mgr.readHistoricalStock(ticker);
			assertNotNull(stocks);
			assertTrue(stocks.size() >= 1);
		}
	}
	@Test
	public void caluclateRSITest() throws ParseException{
		StockMgr mgr = new StockMgr();
		List<Stock> stocks = getTestStocks(mgr);
		mgr.calculateRSI(stocks);
	}
	
	
	@Test
	public void readLatestStock(){
		System.out.println("readcurrentstocks");
		StockMgr mgr = new StockMgr();
	}
	@Test
	public void getDateFromInput() throws ParseException{
		System.out.println("getdatefromstock");
		StockMgr mgr = new StockMgr();
		Date date = mgr.getDateFromInput("20101130");
		assertNotNull(date);
		System.out.println(date);
	}
	@Test
	public void getTickers(){
		StockMgr mgr = new StockMgr();
		List<String> list =  mgr.getTickerFiles(".mst");
		assertNotNull(list);
	}
	private List<Stock> getTestStocks(StockMgr mgr) throws ParseException {
		List<Stock> stocks = new ArrayList<Stock>();
		
		Stock s1 = new Stock();
		s1.setStockDate(mgr.getDateFromInput("20101001"));
		s1.setPriceChange(new BigDecimal("0.0"));
		stocks.add(s1);
		
		Stock s2 = new Stock();
		s2.setStockDate(mgr.getDateFromInput("20101002"));
		s2.setPriceChange(new BigDecimal("-0.25"));
		stocks.add(s2);
		
		Stock s3 = new Stock();
		s3.setStockDate(mgr.getDateFromInput("20101003"));
		s3.setPriceChange(new BigDecimal("0.06"));
		stocks.add(s3);
		
		Stock s4 = new Stock();
		s4.setStockDate(mgr.getDateFromInput("20101004"));
		s4.setPriceChange(new BigDecimal("-0.54"));
		stocks.add(s4);
		
		Stock s5 = new Stock();
		s5.setStockDate(mgr.getDateFromInput("20101005"));
		s5.setPriceChange(new BigDecimal("0.72"));
		stocks.add(s5);
		
		Stock s6 = new Stock();
		s6.setStockDate(mgr.getDateFromInput("20101006"));
		s6.setPriceChange(new BigDecimal("0.5"));
		stocks.add(s6);
		
		Stock s7 = new Stock();
		s7.setStockDate(mgr.getDateFromInput("20101007"));
		s7.setPriceChange(new BigDecimal("0.27"));
		stocks.add(s7);
		
		Stock s8 = new Stock();
		s8.setStockDate(mgr.getDateFromInput("20101008"));
		s8.setPriceChange(new BigDecimal("0.33"));
		stocks.add(s8);
		
		Stock s9 = new Stock();
		s9.setStockDate(mgr.getDateFromInput("20101009"));
		s9.setPriceChange(new BigDecimal("0.42"));
		stocks.add(s9);
		
		Stock s10 = new Stock();
		s10.setStockDate(mgr.getDateFromInput("20101010"));
		s10.setPriceChange(new BigDecimal("0.24"));
		stocks.add(s10);
		
		Stock s11= new Stock();
		s11.setStockDate(mgr.getDateFromInput("20101011"));
		s11.setPriceChange(new BigDecimal("-0.19"));
		stocks.add(s11);
		
		Stock s12 = new Stock();
		s12.setStockDate(mgr.getDateFromInput("20101012"));
		s12.setPriceChange(new BigDecimal("0.14"));
		stocks.add(s12);
		
		Stock s13 = new Stock();
		s13.setStockDate(mgr.getDateFromInput("20101013"));
		s13.setPriceChange(new BigDecimal("-0.42"));
		stocks.add(s13);
		
		Stock s14 = new Stock();
		s14.setStockDate(mgr.getDateFromInput("20101014"));
		s14.setPriceChange(new BigDecimal("0.67"));
		stocks.add(s14);
		
		Stock s15 = new Stock();
		s15.setStockDate(mgr.getDateFromInput("20101015"));
		s15.setPriceChange(new BigDecimal("0.0"));
		stocks.add(s15);
		
		Stock s16 = new Stock();
		s16.setStockDate(mgr.getDateFromInput("20101016"));
		s16.setPriceChange(new BigDecimal("-0.28"));
		stocks.add(s16);
		
		Stock s17 = new Stock();
		s17.setStockDate(mgr.getDateFromInput("20101017"));
		s17.setPriceChange(new BigDecimal("0.03"));
		stocks.add(s17);
		
		Stock s18 = new Stock();
		s18.setStockDate(mgr.getDateFromInput("20101018"));
		s18.setPriceChange(new BigDecimal("0.38"));
		stocks.add(s18);
		
		return stocks;
	}
}
