package fasti.sh.execute.aws.eks.addon;

import static fasti.sh.execute.serialization.Format.id;

import com.fasterxml.jackson.core.type.TypeReference;
import fasti.sh.execute.serialization.Mapper;
import fasti.sh.execute.serialization.Template;
import fasti.sh.model.aws.eks.addon.core.secretprovider.CsiSecretsStoreAddon;
import fasti.sh.model.main.Common;
import java.util.Map;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.eks.HelmChart;
import software.amazon.awscdk.services.eks.ICluster;
import software.constructs.Construct;

@Slf4j
@Getter
public class CsiSecretsStoreConstruct extends Construct {
  private final HelmChart chart;

  @SneakyThrows
  public CsiSecretsStoreConstruct(Construct scope, Common common, CsiSecretsStoreAddon conf, ICluster cluster) {
    super(scope, id("csisecretsstore", conf.chart().release()));

    log.debug("{} [common: {} conf: {}]", "CsiSecretsStoreConstruct", common, conf);

    var parsed = Template.parse(scope, conf.chart().values());
    var values = Mapper.get().readValue(parsed, new TypeReference<Map<String, Object>>() {});

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
      .values(values)
      .build();
  }
}
