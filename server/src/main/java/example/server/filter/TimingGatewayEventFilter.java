package example.server.filter;

import example.server.statistics.GatewaySenderQueueStatistics;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.wan.GatewayEventFilter;
import org.apache.geode.cache.wan.GatewayQueueEvent;

import org.apache.geode.internal.cache.wan.GatewaySenderEventImpl;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

public class TimingGatewayEventFilter implements GatewayEventFilter {

  private final Cache cache;

  private final GatewaySenderQueueStatistics queueStatistics;

  private final Map<Long,Long> queueStartTimes;

  private final Map<Long,Long> transmissionStartTimes;

  // Note: This constuctor is necessary since gfsh does not support adding Declarable properties
  // while configuring GatewayEventFilters. If it did, these values could be set in Declarable.initialize
  public TimingGatewayEventFilter() {
    this.cache = CacheFactory.getAnyInstance();
    this.queueStatistics = new GatewaySenderQueueStatistics(this.cache.getDistributedSystem(), "ny");
    this.queueStartTimes = new ConcurrentHashMap<>();
    this.transmissionStartTimes = new ConcurrentHashMap<>();
  }

  public boolean beforeEnqueue(GatewayQueueEvent event) {
    // Increment the queued events
    this.queueStatistics.beforeEnqueue();

    // Set the queue start time for this event
    GatewaySenderEventImpl gsei = (GatewaySenderEventImpl) event;
    this.queueStartTimes.put(gsei.getShadowKey(), System.currentTimeMillis());

    return true;
  }

  public boolean beforeTransmit(GatewayQueueEvent event) {
    // This method can be called multiple times for the same batch if the remote site is
    // not connected.
    GatewaySenderEventImpl gsei = (GatewaySenderEventImpl) event;
    if (this.transmissionStartTimes.containsKey(gsei.getShadowKey())) {
      // This case means the batch is being re-attempted.
      // @TODO Decrement the previous time from the stats and add the new time.
      //this.cache.getLogger().info("Reattempting transmission event=" + event.getKey());
    } else {
      // Get the current time and update the statistics
      long currentTime = System.currentTimeMillis();
      this.queueStatistics.beforeTransmit(this.queueStartTimes.get(gsei.getShadowKey()), currentTime);

      // Set the transmit start time for this event
      this.transmissionStartTimes.put(gsei.getShadowKey(), currentTime);
    }

    return true;
  }

  public void afterAcknowledgement(GatewayQueueEvent event) {
    // Get transmit start time for this event
    GatewaySenderEventImpl gsei = (GatewaySenderEventImpl) event;
    Long queueStartTime = this.queueStartTimes.remove(gsei.getShadowKey());
    Long transmissionStartTime = this.transmissionStartTimes.remove(gsei.getShadowKey());

    // The before/after acknowledgement methods are invoked only for primary events
    // so transmitStartTime can be null.
    if (transmissionStartTime != null) {
      // Update the statistics
      this.queueStatistics.afterAcknowledgement(transmissionStartTime, queueStartTime);
    }
  }
}
