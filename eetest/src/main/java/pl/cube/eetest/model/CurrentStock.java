package pl.cube.eetest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
	@GeneratedValue
	private Long id;
	
	@NotNull
	private Long stockId;
	@NotNull
	private BigDecimal buyPrice;
	@NotNull
	private BigDecimal stopLoss;
	@NotNull
	private BigDecimal stopGain;
	@NotNull
	private Date openTransaction;
	private Date closeTransaction;
	
	@Transient
	private BigDecimal lastClosePrice;
	@Transient
	private BigDecimal currentPrice;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getStockId() {
		return stockId;
	}
	public void setStockId(Long stockId) {
		this.stockId = stockId;
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
	
	
}
