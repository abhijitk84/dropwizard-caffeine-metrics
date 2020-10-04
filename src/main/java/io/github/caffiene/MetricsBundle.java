package io.github.caffiene;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.github.caffiene.metrics.MetricsStatsCounter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class MetricsBundle<T extends Configuration> implements ConfiguredBundle<T> {


  @Override
  public void run(T configuration, Environment environment){
    MetricsStatsCounter.init(environment.metrics());
  }

  @Override
  public void initialize(Bootstrap<?> bootstrap) {

  }
}
