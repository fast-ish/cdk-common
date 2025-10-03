package fasti.sh.execute.aws.msk;

import static fasti.sh.execute.serialization.Format.id;

import fasti.sh.model.aws.msk.Msk;
import fasti.sh.model.main.Common;
import fasti.sh.model.main.Common.Maps;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awscdk.services.ec2.ISubnet;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.msk.CfnServerlessCluster;
import software.constructs.Construct;

@Slf4j
@Getter
public class MskConstruct extends Construct {
  private final CfnServerlessCluster msk;

  public MskConstruct(Construct scope, Common common, Msk conf, Vpc vpc, List<String> securityGroupIds) {
    super(scope, id("msk", conf.name()));

    log.debug("{} [common: {} conf: {}]", "MskConstruct", common, conf);

    this.msk = CfnServerlessCluster.Builder
      .create(this, conf.name())
      .clusterName(conf.name())
      .vpcConfigs(
        List
          .of(
            CfnServerlessCluster.VpcConfigProperty
              .builder()
              .subnetIds(vpc.getPrivateSubnets().stream().map(ISubnet::getSubnetId).toList())
              .securityGroups(securityGroupIds)
              .build()))
      .clientAuthentication(
        CfnServerlessCluster.ClientAuthenticationProperty
          .builder()
          .sasl(CfnServerlessCluster.SaslProperty.builder().iam(CfnServerlessCluster.IamProperty.builder().enabled(true).build()).build())
          .build())
      .tags(Maps.from(common.tags(), conf.tags()))
      .build();
  }
}
