package pl.cube.eetest.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.jboss.seam.solder.logging.Category;

import pl.cube.eetest.data.StockDao;
import pl.cube.eetest.model.CurrentStock;

@SessionScoped
@Model
public class SellStock implements Serializable{
	
	@Inject
	private StockDao stockDao;
	
	@Inject
	@Category("eetest")
	private Logger log;
	
	private CurrentStock currentStock;
	
	public String showCurrentStock(CurrentStock currentStock){
		log.debug("currentStock: " + currentStock);
		this.currentStock = currentStock;
		this.currentStock.setSellPrice(currentStock.getLastClosePrice());
		return "sellstock";
	}
	public String sellStock(CurrentStock cs){
		log.debug("sell current stock");
		cs.setCloseTransaction(new Date());
		cs.setTotalGain(calculateTotalGain(cs));
		stockDao.sellStock(cs);
		this.currentStock = null;
		return "index";
	}
	

	private BigDecimal calculateTotalGain(CurrentStock cs) {
		BigDecimal sell = cs.getSellPrice().multiply(new BigDecimal(cs.getQuantity())).setScale(2, RoundingMode.HALF_UP);
		BigDecimal buy = cs.getBuyPrice().multiply(new BigDecimal(cs.getQuantity())).setScale(2, RoundingMode.HALF_UP);
		return sell.subtract(buy);
	}
	public CurrentStock getCurrentStock() {
		return currentStock;
	}

	public void setCurrentStock(CurrentStock currentStock) {
		this.currentStock = currentStock;
	}
	
}
