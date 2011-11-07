package pl.cube.eetest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@Table( name = "STOCK", uniqueConstraints = { @UniqueConstraint( columnNames = { "ticker", "stockDate" } ) } )
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
	private BigDecimal sma;
	private BigDecimal ema12;
	private BigDecimal ema26;
	private BigDecimal macdLine;
	private BigDecimal macdSignalLine;
	private BigDecimal macdHistogram;
	
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
	public BigDecimal getSma() {
		return sma;
	}
	public void setSma(BigDecimal sma) {
		this.sma = sma;
	}
	public BigDecimal getEma12() {
		return ema12;
	}
	public void setEma12(BigDecimal ema12) {
		this.ema12 = ema12;
	}
	public BigDecimal getEma26() {
		return ema26;
	}
	public void setEma26(BigDecimal ema26) {
		this.ema26 = ema26;
	}
	public BigDecimal getMacdLine() {
		return macdLine;
	}
	public void setMacdLine(BigDecimal macdLine) {
		this.macdLine = macdLine;
	}
	public BigDecimal getMacdSignalLine() {
		return macdSignalLine;
	}
	public void setMacdSignalLine(BigDecimal macdSignalLine) {
		this.macdSignalLine = macdSignalLine;
	}
	public BigDecimal getMacdHistogram() {
		return macdHistogram;
	}
	public void setMacdHistogram(BigDecimal macdHistogram) {
		this.macdHistogram = macdHistogram;
	}
	@Override
	public int compareTo(Object arg0) {
		Stock temp = (Stock)arg0;
		return this.getStockDate().compareTo(temp.getStockDate());
	}
	
}