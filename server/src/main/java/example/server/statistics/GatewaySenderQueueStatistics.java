package example.server.statistics;

import org.apache.geode.StatisticDescriptor;
import org.apache.geode.Statistics;
import org.apache.geode.StatisticsFactory;
import org.apache.geode.StatisticsType;
import org.apache.geode.StatisticsTypeFactory;

import org.apache.geode.internal.statistics.StatisticsTypeFactoryImpl;

import java.util.concurrent.atomic.AtomicBoolean;

public class GatewaySenderQueueStatistics {

  private static final StatisticsType type;

  private static final String QUEUED_EVENTS = "queuedEvents";
  private static final String TRANSMITTED_EVENTS = "transmittedEvents";
  private static final String ACKNOWLEDGED_EVENTS = "acknowledgedEvents";
  private static final String MINIMUM_QUEUE_TIME= "minimumQueueTime";
  private static final String MAXIMUM_QUEUE_TIME= "maximumQueueTime";
  private static final String TOTAL_QUEUE_TIME= "totalQueueTime";
  private static final String MINIMUM_TRANSMISSION_TIME= "minimumTransmissionTime";
  private static final String MAXIMUM_TRANSMISSION_TIME= "maximumTransmissionTime";
  private static final String TOTAL_TRANSMISSION_TIME = "totalTransmissionTime";
  private static final String MINIMUM_PROCESSING_TIME= "minimumProcessingTime";
  private static final String MAXIMUM_PROCESSING_TIME= "maximumProcessingTime";
  private static final String TOTAL_PROCESSING_TIME = "totalProcessingTime";

  private final static int queuedEventsId;
  private final static int transmittedEventsId;
  private final static int acknowledgedEventsId;
	private final static int minimumQueueTimeId;
	private final static int maximumQueueTimeId;
  private final static int totalQueueTimeId;
  private final static int minimumTransmissionTimeId;
  private final static int maximumTransmissionTimeId;
  private final static int totalTransmissionTimeId;
  private final static int minimumProcessingTimeId;
  private final static int maximumProcessingTimeId;
  private final static int totalProcessingTimeId;

  private AtomicBoolean minimumQueueTimeNotSet = new AtomicBoolean(false);
  private AtomicBoolean minimumTransmissionTimeNotSet = new AtomicBoolean(false);
  private AtomicBoolean minimumProcessingTimeNotSet = new AtomicBoolean(false);

  static {
    StatisticsTypeFactory f = StatisticsTypeFactoryImpl.singleton();
    type = f.createType("GatewaySenderQueueStatistics", "GatewaySender queue statistics",
      new StatisticDescriptor[] {
				f.createLongCounter(QUEUED_EVENTS, "The number of events queued by this gateway sender", "events"),
				f.createLongCounter(TRANSMITTED_EVENTS, "The number of events transmitted by this gateway sender", "events"),
				f.createLongCounter(ACKNOWLEDGED_EVENTS, "The number of events acknowledged by this gateway sender", "events"),
				f.createLongGauge(MINIMUM_QUEUE_TIME, "The minimum time an event spent in the gateway sender queue", "milliseconds", false),
				f.createLongGauge(MAXIMUM_QUEUE_TIME, "The maximum time an event spent in the gateway sender queue", "milliseconds", false),
				f.createLongCounter(TOTAL_QUEUE_TIME, "The total time events spent in the gateway sender queue", "milliseconds", false),
        f.createLongGauge(MINIMUM_TRANSMISSION_TIME, "The minimum time an event spent in transmission including processing time on the remote site", "milliseconds", false),
        f.createLongGauge(MAXIMUM_TRANSMISSION_TIME, "The maximum time an event spent in transmission including processing time on the remote site", "milliseconds", false),
        f.createLongCounter(TOTAL_TRANSMISSION_TIME, "The total time events spent in transmission including processing time on the remote site", "milliseconds", false),
        f.createLongGauge(MINIMUM_PROCESSING_TIME, "The minimum time an event spent being processed including queue time on the local site and processing time on the remote site", "milliseconds", false),
        f.createLongGauge(MAXIMUM_PROCESSING_TIME, "The maximum time an event spent being processed including queue time on the local site and processing time on the remote site", "milliseconds", false),
        f.createLongCounter(TOTAL_PROCESSING_TIME, "The total time events spent being processed including queue time on the local site and processing time on the remote site", "milliseconds", false)
			}
		);

    queuedEventsId = type.nameToId(QUEUED_EVENTS);
    transmittedEventsId = type.nameToId(TRANSMITTED_EVENTS);
    acknowledgedEventsId = type.nameToId(ACKNOWLEDGED_EVENTS);
    minimumQueueTimeId = type.nameToId(MINIMUM_QUEUE_TIME);
    maximumQueueTimeId = type.nameToId(MAXIMUM_QUEUE_TIME);
    totalQueueTimeId = type.nameToId(TOTAL_QUEUE_TIME);
    minimumTransmissionTimeId = type.nameToId(MINIMUM_TRANSMISSION_TIME);
    maximumTransmissionTimeId = type.nameToId(MAXIMUM_TRANSMISSION_TIME);
    totalTransmissionTimeId = type.nameToId(TOTAL_TRANSMISSION_TIME);
    minimumProcessingTimeId = type.nameToId(MINIMUM_PROCESSING_TIME);
    maximumProcessingTimeId = type.nameToId(MAXIMUM_PROCESSING_TIME);
    totalProcessingTimeId = type.nameToId(TOTAL_PROCESSING_TIME);
  }

  private final Statistics stats;

  public GatewaySenderQueueStatistics(StatisticsFactory factory, String senderId) {
    this.stats = factory.createAtomicStatistics(type, "gatewaySenderQueueStatistics-" + senderId);
  }
  
  public void close() {
    this.stats.close();
  }

  public void beforeEnqueue() {
    this.stats.incLong(queuedEventsId, 1);
  }
  
  public long beforeTransmit(long queueStartTime, long currentTime) {
    long queueTime = currentTime - queueStartTime;
    this.stats.incLong(transmittedEventsId, 1);

    // Update queue time statistics
		this.stats.incLong(totalQueueTimeId, queueTime);
		if (this.minimumQueueTimeNotSet.compareAndSet(false, true)
      || this.stats.getLong(minimumQueueTimeId) > queueTime) {
			this.stats.setLong(minimumQueueTimeId, queueTime);
		}
		if (this.stats.getLong(maximumQueueTimeId) < queueTime) {
			this.stats.setLong(maximumQueueTimeId, queueTime);
		}
		return currentTime;
  }
  
  public void afterAcknowledgement(long transmissionStartTime, long queueStartTime) {
    long currentTime = System.currentTimeMillis();
    long transmissionTime = currentTime - transmissionStartTime;
    long processTime = currentTime - queueStartTime;

    // Update acknowledged events
    this.stats.incLong(acknowledgedEventsId, 1);

    // Update transmission time statistics
    this.stats.incLong(totalTransmissionTimeId, transmissionTime);
		if (this.minimumTransmissionTimeNotSet.compareAndSet(false, true)
      || this.stats.getLong(minimumTransmissionTimeId) > transmissionTime) {
			this.stats.setLong(minimumTransmissionTimeId, transmissionTime);
		}
		if (this.stats.getLong(maximumTransmissionTimeId) < transmissionTime) {
			this.stats.setLong(maximumTransmissionTimeId, transmissionTime);
		}

    // Update processing time statistics
    this.stats.incLong(totalProcessingTimeId, processTime);
    if (this.minimumProcessingTimeNotSet.compareAndSet(false, true)
      || this.stats.getLong(minimumProcessingTimeId) > processTime) {
      this.stats.setLong(minimumProcessingTimeId, processTime);
    }
    if (this.stats.getLong(maximumProcessingTimeId) < processTime) {
      this.stats.setLong(maximumProcessingTimeId, processTime);
    }
  }
}
