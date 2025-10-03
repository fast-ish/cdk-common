package fasti.sh.execute.aws.eks.addon;

import static fasti.sh.execute.serialization.Format.id;

import fasti.sh.model.aws.eks.addon.core.secretprovider.AwsSecretsStoreAddon;
import fasti.sh.model.main.Common;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.eks.HelmChart;
import software.amazon.awscdk.services.eks.ICluster;
import software.constructs.Construct;

@Slf4j
@Getter
public class AwsSecretsStoreConstruct extends Construct {
  private final HelmChart chart;

  public AwsSecretsStoreConstruct(Construct scope, Common common, AwsSecretsStoreAddon conf, ICluster cluster) {
    super(scope, id("awssecretsstore", conf.chart().release()));

    log.debug("{} [common: {} conf: {}]", "AwsSecretsStoreConstruct", common, conf);

    this.chart = HelmChart.Builder
      .create(this, conf.chart().name())
      .cluster(cluster)
      .wait(true)
      .timeout(Duration.minutes(15))
      .skipCrds(false)
      .createNamespace(true)
      .chart(conf.chart().name())
      .namespace(conf.chart().namespace())
      .repository(conf.chart().repository())
      .release(conf.chart().release())
      .version(conf.chart().version())
      .build();
  }
}
