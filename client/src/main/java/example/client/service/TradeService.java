package example.client.service;

import example.client.domain.CusipHelper;
import example.client.domain.Trade;
import example.client.repository.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

@Service
public class TradeService {

  @Autowired
  protected TradeRepository tradeRepository;

  protected static final Random random = new Random();

  protected static final Logger logger = LoggerFactory.getLogger(TradeService.class);

  public void load(int numEntries) {
    logger.info("Loading {} trades", numEntries);
    for (int i=0; i<numEntries; i++) {
      Trade trade = save(i);
      logger.info("Saved " + trade);
    }
  }

  public void loadForever(int numEntries) {
    logger.info("Loading {} trades forever", numEntries);
    int i = 0;
    long start, end;
    start = System.currentTimeMillis();
    while (true) {
      save(i);
      if (++i % 1000 == 0) {
        end = System.currentTimeMillis();
        logger.info("Loaded 1000 trades (total={}) in {} ms", i, end - start);
        start = System.currentTimeMillis();
      }
    }
  }

  private Trade save(int i) {
    Trade trade = new Trade(String.valueOf(i), CusipHelper.getCusip(), random.nextInt(100), new BigDecimal(BigInteger.valueOf(random.nextInt(100000)), 2));
    this.tradeRepository.save(trade);
    return trade;
  }
}
