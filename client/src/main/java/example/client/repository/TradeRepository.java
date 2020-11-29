package example.client.repository;

import example.client.domain.Trade;
import org.springframework.data.gemfire.repository.GemfireRepository;

public interface TradeRepository extends GemfireRepository<Trade, String> {
}