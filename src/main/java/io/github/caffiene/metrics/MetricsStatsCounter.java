package io.github.caffiene.metrics;

import static java.util.Objects.requireNonNull;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.github.benmanes.caffeine.cache.stats.StatsCounter;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

public class MetricsStatsCounter implements StatsCounter {
  private final Counter hitCount;
  private final Counter missCount;
  private final Counter loadSuccessCount;
  private final Counter loadFailureCount;
  private final Timer totalLoadTime;
  private final Counter evictionCount;
  private final Counter evictionWeight;
  @Getter
  private final Gauge hitRatio;
  private static MetricRegistry registry;

  /**
   * Constructs an instance for use by a single cache.
   *
   * @param metricsPrefix the prefix name for the metrics
   */
  public MetricsStatsCounter(String metricsPrefix) {
    requireNonNull(metricsPrefix);
    requireNonNull(registry,"Registry can not be null");
    hitCount = registry.counter(metricsPrefix + ".hits");
    missCount = registry.counter(metricsPrefix + ".misses");
    totalLoadTime = registry.timer(metricsPrefix + ".loads");
    loadSuccessCount = registry.counter(metricsPrefix + ".loads-success");
    loadFailureCount = registry.counter(metricsPrefix + ".loads-failure");
    evictionCount = registry.counter(metricsPrefix + ".evictions");
    evictionWeight = registry.counter(metricsPrefix + ".evictions-weight");
    hitRatio = registry.gauge(metricsPrefix + ".hit-ratio", () ->
        () -> {
          long hits = hitCount.getCount();
          long total = hitCount.getCount() + missCount.getCount();
          total = (total <= 0) ? 1 : total;
          return hits * 1.0 / total;
        }
    );
  }

  public static void init(MetricRegistry metricRegistry){
    registry = metricRegistry;
  }

  @Override
  public void recordHits(int count) {
    hitCount.inc(count);
  }

  @Override
  public void recordMisses(int count) {
    missCount.inc(count);
  }

  @Override
  public void recordLoadSuccess(long loadTime) {
    loadSuccessCount.inc();
    totalLoadTime.update(loadTime, TimeUnit.NANOSECONDS);
  }

  @Override
  public void recordLoadFailure(long loadTime) {
    loadFailureCount.inc();
    totalLoadTime.update(loadTime, TimeUnit.NANOSECONDS);
  }

  @Override
  public void recordEviction() {
    evictionCount.inc();
    evictionWeight.inc(1);
  }


  @Override
  public CacheStats snapshot() {
    return new CacheStats(
        hitCount.getCount(),
        missCount.getCount(),
        loadSuccessCount.getCount(),
        loadFailureCount.getCount(),
        totalLoadTime.getCount(),
        evictionCount.getCount(),
        evictionWeight.getCount());
  }

  @Override
  public String toString() {
    return snapshot().toString();
  }
}

