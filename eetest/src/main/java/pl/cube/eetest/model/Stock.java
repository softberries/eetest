package pl.cube.eetest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Stock implements Serializable, Comparable {
	/** Default value included to remove warning. Remove or modify at will. **/
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	private String ticker;
	@NotNull
	private Date stockDate;
	@NotNull
	private BigDecimal priceOpen;
	@NotNull
	private BigDecimal priceHigh;
	@NotNull
	private BigDecimal priceLow;
	@NotNull
	private BigDecimal priceClose;
	@NotNull
	private BigDecimal vol;
	
	
	//wskazniki obliczone
	private BigDecimal priceChange;
	private BigDecimal rsi;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public Date getStockDate() {
		return stockDate;
	}
	public void setStockDate(Date stockDate) {
		this.stockDate = stockDate;
	}
	public BigDecimal getPriceOpen() {
		return priceOpen;
	}
	public void setPriceOpen(BigDecimal priceOpen) {
		this.priceOpen = priceOpen;
	}
	public BigDecimal getPriceHigh() {
		return priceHigh;
	}
	public void setPriceHigh(BigDecimal priceHigh) {
		this.priceHigh = priceHigh;
	}
	public BigDecimal getPriceLow() {
		return priceLow;
	}
	public void setPriceLow(BigDecimal priceLow) {
		this.priceLow = priceLow;
	}
	public BigDecimal getPriceClose() {
		return priceClose;
	}
	public void setPriceClose(BigDecimal priceClose) {
		this.priceClose = priceClose;
	}
	public BigDecimal getVol() {
		return vol;
	}
	public void setVol(BigDecimal vol) {
		this.vol = vol;
	}
	public BigDecimal getPriceChange() {
		return priceChange;
	}
	public void setPriceChange(BigDecimal priceChange) {
		this.priceChange = priceChange;
	}
	public BigDecimal getRsi() {
		return rsi;
	}
	public void setRsi(BigDecimal rsi) {
		this.rsi = rsi;
	}
	@Override
	public int compareTo(Object arg0) {
		Stock temp = (Stock)arg0;
		return this.getStockDate().compareTo(temp.getStockDate());
	}
	
}