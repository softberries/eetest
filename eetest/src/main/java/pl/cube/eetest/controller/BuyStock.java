package pl.cube.eetest.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.AroundInvoke;

import org.jboss.logging.Logger;
import org.jboss.seam.solder.logging.Category;

import pl.cube.eetest.data.StockDao;
import pl.cube.eetest.model.CurrentStock;
import pl.cube.eetest.model.Stock;

@SessionScoped
@Model
public class BuyStock implements Serializable{

	private Stock stock;
	private CurrentStock cStock;
	private BigDecimal buyPrice;
	private BigDecimal stopLoss;
	private BigDecimal stopGain;
	
	@Inject
	private StockDao stockDao;
	
	@Inject
	@Category("eetest")
	private Logger log;
	
	public String showStockProperties(Stock stock){
		log.debug("show stock properties: " + stock);
		this.stock = stock;
		this.cStock = new CurrentStock();
		return "buystock";
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}
	public String buyStock(Stock s){
		log.debug("buy stock");
		CurrentStock cstock = new CurrentStock();
		cstock.setCurrentPrice(s.getPriceClose());
		cstock.setLastClosePrice(s.getPriceClose());
		cstock.setStockId(s.getId());
		cstock.setOpenTransaction(new Date());
		cstock.setBuyPrice(this.getBuyPrice());
		cstock.setStopGain(this.getStopGain());
		cstock.setStopLoss(this.getStopLoss());
		stockDao.buyStock(cstock);
		return "index";
	}

	public CurrentStock getcStock() {
		return cStock;
	}

	public void setcStock(CurrentStock cStock) {
		this.cStock = cStock;
	}

	public BigDecimal getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}

	public BigDecimal getStopLoss() {
		return stopLoss;
	}

	public void setStopLoss(BigDecimal stopLoss) {
		this.stopLoss = stopLoss;
	}

	public BigDecimal getStopGain() {
		return stopGain;
	}

	public void setStopGain(BigDecimal stopGain) {
		this.stopGain = stopGain;
	}
}
