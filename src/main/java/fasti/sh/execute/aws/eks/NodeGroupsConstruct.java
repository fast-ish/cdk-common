package fasti.sh.execute.aws.eks;

import static fasti.sh.execute.serialization.Format.id;

import fasti.sh.execute.aws.iam.RoleConstruct;
import fasti.sh.model.aws.eks.NodeGroup;
import fasti.sh.model.main.Common;
import fasti.sh.model.main.Common.Maps;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.eks.CapacityType;
import software.amazon.awscdk.services.eks.ICluster;
import software.amazon.awscdk.services.eks.Nodegroup;
import software.constructs.Construct;

@Slf4j
@Getter
public class NodeGroupsConstruct extends Construct {
  private final List<Nodegroup> nodeGroups;

  public NodeGroupsConstruct(Construct scope, String id, Common common, List<NodeGroup> conf, ICluster cluster) {
    super(scope, id("nodegroups", id));

    log.debug("{} [common: {} conf: {}]", "NodeGroupsConstruct", common, conf);

    this.nodeGroups = conf.stream().map(nodeGroup -> {
      var principal = nodeGroup.role().principal().iamPrincipal();
      var role = new RoleConstruct(this, common, principal, nodeGroup.role()).role();

      return Nodegroup.Builder
        .create(this, nodeGroup.name())
        .cluster(cluster)
        .nodegroupName(nodeGroup.name())
        .amiType(nodeGroup.amiType())
        .instanceTypes(List.of(InstanceType.of(nodeGroup.instanceClass(), nodeGroup.instanceSize())))
        .minSize(nodeGroup.minSize())
        .maxSize(nodeGroup.maxSize())
        .desiredSize(nodeGroup.desiredSize())
        .capacityType(CapacityType.valueOf(nodeGroup.capacityType().toUpperCase()))
        .nodeRole(role)
        .forceUpdate(nodeGroup.forceUpdate())
        .labels(nodeGroup.labels())
        .tags(Maps.from(common.tags(), nodeGroup.tags()))
        .build();
    }).toList();
  }
}
