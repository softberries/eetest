package pl.cube.eetest.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import org.jboss.logging.Logger;
import org.jboss.seam.solder.logging.Category;
import pl.cube.eetest.data.StockDao;
import pl.cube.eetest.data.StockProducer;
import pl.cube.eetest.model.Stock;

@Stateful
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
	private Date latestDate;

	@PostConstruct
	public void prepareBasicData(){
		setLatestDate(stockDao.getLastDate());
		this.recommendedStocks = stockDao.calculateRecommendedStocks(getLatestDate(), 20);
	}

	
	public void importStocks(){
		stockProducer.importStocks();
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
	
}
