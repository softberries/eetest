package pl.cube.eetest.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.jboss.seam.solder.logging.Category;
import org.joda.time.DateTime;

import pl.cube.eetest.data.StockDao;
import pl.cube.eetest.data.StockProducer;
import pl.cube.eetest.model.CurrentStock;
import pl.cube.eetest.model.Stock;

@Model
public class StockMgr {

	@Inject
	private StockDao stockDao;
	
	@Inject 
	private StockProducer stockProducer;

	@Inject
	@Category("eetest")
	private Logger log;
	
	private List<Stock> recommendedStocks;
	private List<CurrentStock> currentStocks;
	private List<CurrentStock> closedStocks;
	
	private String chartData;
	private String invested;
	
	private Date latestDate;

	@PostConstruct
	public void prepareBasicData(){
		setLatestDate(stockDao.getLastDate());
		this.recommendedStocks = stockDao.calculateRecommendedStocks(getLatestDate(), 20);
		this.currentStocks = stockDao.getCurrentStocks();
		this.closedStocks = stockDao.getClosedStocks(getStartDate());
		this.invested = calculateInvestedStocks(this.currentStocks);
		this.chartData = prepareChartData(this.currentStocks);
	}
	private String prepareChartData(List<CurrentStock> currentSts) {
		StringBuilder sb = new StringBuilder();
		if(currentSts == null || currentSts.size() == 0){
			return "READY TO INVEST (%);100";
		}
		for(CurrentStock cs : currentSts){
			BigDecimal cost = cs.getBuyPrice().multiply(new BigDecimal(cs.getQuantity())).setScale(2, RoundingMode.HALF_UP);
			sb.append(cs.getStock().getTicker() + ";" + cost.setScale(2).toString() + "\\n");
		}
		return sb.toString();
	}
	private String calculateInvestedStocks(List<CurrentStock> currentSts) {
		BigDecimal sum = new BigDecimal("0.00");
		for(CurrentStock cs : currentSts){
			BigDecimal cost = cs.getBuyPrice().multiply(new BigDecimal(cs.getQuantity())).setScale(2, RoundingMode.HALF_UP);
			sum = sum.add(cost);
		}
		return sum.setScale(2).toString();
	}
	private Date getStartDate() {
		DateTime dt = new DateTime();
		DateTime earlier = dt.minusDays(30);
		return earlier.toDate();
	}
	public void updateStocks(){
		stockProducer.updateStocks();
	}
	public List<Stock> getRecommendedStocks() {
		return recommendedStocks;
	}
	public void setRecommendedStocks(List<Stock> recommendedStocks) {
		this.recommendedStocks = recommendedStocks;
	}
	public Date getLatestDate() {
		return latestDate;
	}
	public void setLatestDate(Date latestDate) {
		this.latestDate = latestDate;
	}
	public List<CurrentStock> getCurrentStocks() {
		return currentStocks;
	}
	public void setCurrentStocks(List<CurrentStock> currentStocks) {
		this.currentStocks = currentStocks;
	}
	public List<CurrentStock> getClosedStocks() {
		return closedStocks;
	}
	public void setClosedStocks(List<CurrentStock> closedStocks) {
		this.closedStocks = closedStocks;
	}
	public String getChartData() {
		return chartData;
	}
	public void setChartData(String chartData) {
		this.chartData = chartData;
	}
	public String getInvested() {
		return invested;
	}
	public void setInvested(String invested) {
		this.invested = invested;
	}
	
	
}
