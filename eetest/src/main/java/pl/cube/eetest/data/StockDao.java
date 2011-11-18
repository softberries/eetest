package pl.cube.eetest.data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateful;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import pl.cube.eetest.model.SessionFile;
import pl.cube.eetest.model.Stock;

@Stateful
@Model
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
		Query query = em.createQuery("SELECT s FROM Stock s where s.ticker = :tick ORDER BY s.stockDate DESC");
		query.setParameter("tick", ticker);
		query.setMaxResults(size);
		return query.getResultList();
	}
	public BigDecimal findLastClose(String ticker) {
		Query query = em.createQuery("SELECT s.priceClose FROM Stock s where s.ticker = :tick ORDER BY s.stockDate DESC");
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
}
