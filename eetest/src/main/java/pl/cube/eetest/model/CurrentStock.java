package pl.cube.eetest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@Table( name = "CURRENT_STOCK")
public class CurrentStock implements Serializable {

	/** Default value included to remove warning. Remove or modify at will. **/
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
    @JoinColumn(name="stock_fk")
	private Stock stock;
	
	@NotNull
	private BigDecimal buyPrice;
	@NotNull
	private BigDecimal stopLoss;
	@NotNull
	private BigDecimal stopGain;
	@NotNull
	private int quantity;
	@NotNull
	private Date openTransaction;
	
	private Date closeTransaction;
	
	private BigDecimal sellPrice;
	private BigDecimal totalGain;
	
	@Transient
	private BigDecimal lastClosePrice;
	@Transient
	private BigDecimal currentPrice;
	@Transient
	private BigDecimal gainFromLastClose;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public BigDecimal getLastClosePrice() {
		return lastClosePrice;
	}
	public void setLastClosePrice(BigDecimal lastClosePrice) {
		this.lastClosePrice = lastClosePrice;
	}
	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}
	public Date getOpenTransaction() {
		return openTransaction;
	}
	public void setOpenTransaction(Date openTransaction) {
		this.openTransaction = openTransaction;
	}
	public Date getCloseTransaction() {
		return closeTransaction;
	}
	public void setCloseTransaction(Date closeTransaction) {
		this.closeTransaction = closeTransaction;
	}
	public Stock getStock() {
		return stock;
	}
	public void setStock(Stock stock) {
		this.stock = stock;
	}
	public BigDecimal getGainFromLastClose() {
		return gainFromLastClose;
	}
	public void setGainFromLastClose(BigDecimal gainFromLastClose) {
		this.gainFromLastClose = gainFromLastClose;
	}
	public BigDecimal getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getTotalGain() {
		return totalGain;
	}
	public void setTotalGain(BigDecimal totalGain) {
		this.totalGain = totalGain;
	}
	
}
