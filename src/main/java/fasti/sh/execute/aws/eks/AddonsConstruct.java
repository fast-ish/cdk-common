package fasti.sh.execute.aws.eks;

import static fasti.sh.execute.serialization.Format.id;

import fasti.sh.execute.aws.eks.addon.AlloyOperatorConstruct;
import fasti.sh.execute.aws.eks.addon.AwsLoadBalancerConstruct;
import fasti.sh.execute.aws.eks.addon.AwsSecretsStoreConstruct;
import fasti.sh.execute.aws.eks.addon.CertManagerConstruct;
import fasti.sh.execute.aws.eks.addon.CsiSecretsStoreConstruct;
import fasti.sh.execute.aws.eks.addon.GrafanaConstruct;
import fasti.sh.execute.aws.eks.addon.KarpenterConstruct;
import fasti.sh.execute.serialization.Mapper;
import fasti.sh.execute.serialization.Template;
import fasti.sh.model.aws.eks.KubernetesConf;
import fasti.sh.model.aws.eks.addon.AddonsConf;
import fasti.sh.model.main.Common;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awscdk.services.eks.Cluster;
import software.constructs.Construct;

@Slf4j
@Getter
public class AddonsConstruct extends Construct {
  private final AlloyOperatorConstruct alloyOperator;
  private final GrafanaConstruct grafana;
  private final CertManagerConstruct certManager;
  private final CsiSecretsStoreConstruct csiSecretsStore;
  private final AwsSecretsStoreConstruct awsSecretsStore;
  private final KarpenterConstruct karpenter;
  private final AwsLoadBalancerConstruct awsLoadBalancer;

  @SneakyThrows
  public AddonsConstruct(Construct scope, Common common, KubernetesConf conf, Cluster cluster) {
    super(scope, id("eks.addons", conf.name()));

    log.debug("{} [common: {} conf: {}]", "AddonsConstruct", common, conf);

    var addons = Mapper.get().readValue(Template.parse(scope, conf.addons()), AddonsConf.class);

    this.alloyOperator = new AlloyOperatorConstruct(this, common, addons.alloyOperator(), cluster);

    this.grafana = new GrafanaConstruct(this, common, addons.grafana(), cluster);
    this.grafana().getNode().addDependency(this.alloyOperator());

    this.certManager = new CertManagerConstruct(this, common, addons.certManager(), cluster);
    this.certManager().getNode().addDependency(this.grafana());

    this.csiSecretsStore = new CsiSecretsStoreConstruct(this, common, addons.csiSecretsStore(), cluster);
    this.csiSecretsStore().getNode().addDependency(this.grafana(), this.certManager());

    this.awsSecretsStore = new AwsSecretsStoreConstruct(this, common, addons.awsSecretsStore(), cluster);
    this.awsSecretsStore().getNode().addDependency(this.grafana(), this.certManager(), this.csiSecretsStore());

    this.karpenter = new KarpenterConstruct(this, common, addons.karpenter(), cluster);
    this.karpenter().getNode().addDependency(this.grafana(), this.certManager(), this.csiSecretsStore(), this.awsSecretsStore());

    this.awsLoadBalancer = new AwsLoadBalancerConstruct(this, common, addons.awsLoadBalancer(), cluster);
    this
      .awsLoadBalancer()
      .getNode()
      .addDependency(
        this.grafana(),
        this.certManager(),
        this.csiSecretsStore(),
        this.awsSecretsStore(),
        this.karpenter());
  }
}
