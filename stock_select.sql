select * from STOCK where year(stockDate)>=2011 and month(stockDate)>=11 and day(stockDate)>=9 and priceClose < sma and macdLine < macdSignalLine and rsi is not null order by rsi asc limit 10;
