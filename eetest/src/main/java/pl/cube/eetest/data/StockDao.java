package pl.cube.eetest.data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import pl.cube.eetest.model.CurrentStock;
import pl.cube.eetest.model.SessionFile;
import pl.cube.eetest.model.Stock;

@Stateless
public class StockDao {

	@Inject
	private EntityManager em;
	
	public List<Stock> calculateRecommendedStocks(Date stockDate, int limit){
		String sql = "SELECT s FROM Stock s WHERE s.stockDate = :stockDate AND s.priceClose < s.sma AND s.macdLine < s.macdSignalLine AND s.rsi IS NOT NULL ORDER BY s.rsi ASC";
	    Query query = em.createQuery(sql);
	    query.setParameter("stockDate", stockDate);
	    query.setMaxResults(limit);
	    List<Stock> result = (List<Stock>)query.getResultList();
    	return result;
	}
	public Date getLastDate(){
		Query query = em.createQuery("SELECT MAX(s.stockDate) FROM Stock s");
		Date result = (Date) query.getSingleResult();
		return result;
	}
	public List<Stock> findLastStocks(String ticker, int size) {
		Query query = em.createQuery("SELECT s FROM Stock s WHERE s.ticker = :tick ORDER BY s.stockDate DESC");
		query.setParameter("tick", ticker);
		query.setMaxResults(size);
		return query.getResultList();
	}
	public Stock findLastStock(Long id){
		Query query = em.createQuery("SELECT s FROM Stock s WHERE s.id = :id ORDER BY s.stockDate DESC");
		query.setParameter("id", id);
		List<Stock> stocks = (List<Stock>)query.getResultList();
		if(stocks != null && stocks.size() > 0){
			return stocks.get(0);
		}
		return null;
	}
	public BigDecimal findLastClose(String ticker) {
		Query query = em.createQuery("SELECT s.priceClose FROM Stock s WHERE s.ticker = :tick ORDER BY s.stockDate DESC");
		query.setMaxResults(1);
		query.setParameter("tick", ticker);
		List<BigDecimal> res = query.getResultList();
		if(res != null && res.size() > 0){
			return res.get(0);
		}
		return null;
	}
	public Set<SessionFile> getExistingTickers() {
		Query query = em.createQuery("SELECT t FROM SessionFile t");
	    return new HashSet<SessionFile>(query.getResultList());
	}
	public void buyStock(CurrentStock cstock){
		em.persist(cstock);
		em.flush();
	}
	public void sellStock(CurrentStock currentStock) {
		em.merge(currentStock);
		em.flush();
	}
	public List<CurrentStock> getCurrentStocks() {
		Query query = em.createQuery("SELECT cs FROM CurrentStock cs WHERE cs.closeTransaction is null");
		List<CurrentStock> result = (List<CurrentStock>)query.getResultList();
		for(CurrentStock s : result){
			Stock lastStock = this.findLastStock(s.getStock().getId());
			if(lastStock != null){
				s.setLastClosePrice(lastStock.getPriceClose());
				s.setGainFromLastClose(calculateGainFromLastClose(s.getBuyPrice(), s.getLastClosePrice()));
			}
		}
		return result;
	}
	public List<CurrentStock> getClosedStocks(Date date) {
		Query query = em.createQuery("SELECT cs FROM CurrentStock cs WHERE cs.closeTransaction >= :date ");
		query.setParameter("date", date);
		List<CurrentStock> result = (List<CurrentStock>)query.getResultList();
		return result;
	}
	/**
	 * Helper method
	 * @param buyPrice
	 * @param lastClosePrice
	 * @return
	 */
	private BigDecimal calculateGainFromLastClose(BigDecimal buyPrice, BigDecimal lastClosePrice) {
		return (lastClosePrice.subtract(buyPrice)).setScale(2);
	}
	
	
}
