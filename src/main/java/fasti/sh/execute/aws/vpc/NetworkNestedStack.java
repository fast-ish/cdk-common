package fasti.sh.execute.aws.vpc;

import static fasti.sh.execute.serialization.Format.describe;
import static fasti.sh.execute.serialization.Format.exported;
import static fasti.sh.execute.serialization.Format.id;

import fasti.sh.model.aws.vpc.NetworkConf;
import fasti.sh.model.main.Common;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.NestedStack;
import software.amazon.awscdk.NestedStackProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.constructs.Construct;

@Slf4j
@Getter
public class NetworkNestedStack extends NestedStack {
  private final Vpc vpc;

  public NetworkNestedStack(Construct scope, Common common, NetworkConf conf, NestedStackProps props) {
    super(scope, "network", props);

    log.debug("network configuration [common: {} network: {}]", common, conf);

    this.vpc = new VpcConstruct(this, common, conf).vpc();

    CfnOutput.Builder
      .create(this, id(common.id(), "vpc.id"))
      .exportName(exported(scope, "vpcid"))
      .value(this.vpc().getVpcId())
      .description(describe(common))
      .build();
  }
}
