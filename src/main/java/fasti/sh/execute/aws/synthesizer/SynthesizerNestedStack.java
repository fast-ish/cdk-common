package fasti.sh.execute.aws.synthesizer;

import static fasti.sh.execute.serialization.Format.describe;

import fasti.sh.execute.aws.kms.KmsConstruct;
import fasti.sh.model.main.Common;
import fasti.sh.model.main.SynthesizerResources;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awscdk.NestedStack;
import software.amazon.awscdk.NestedStackProps;
import software.amazon.awscdk.Tags;
import software.amazon.awscdk.services.ssm.ParameterDataType;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.constructs.Construct;

@Slf4j
@Getter
public class SynthesizerNestedStack extends NestedStack {
  private final KmsConstruct key;
  private final StringParameter version;
  private final SynthesizerRolesNestedStack roles;
  private final SynthesizerStorageNestedStack storage;

  public SynthesizerNestedStack(Construct scope, Common common, SynthesizerResources root, NestedStackProps props) {
    super(scope, "synthesizer.owner", props);

    log.debug("synthesizer configuration [common: {} resources: {}]", common, root);

    this.key = new KmsConstruct(this, common, root.kms());

    var parent = scope.getNode().getContext("host:id").toString();
    this.version = StringParameter.Builder
      .create(this, "ssm")
      .parameterName(String.format("/cdk/%s-%s/version", parent, common.id()))
      .stringValue("21")
      .description("cdk version")
      .dataType(ParameterDataType.TEXT)
      .build();

    common.tags().forEach((k, v) -> Tags.of(this.version()).add(k, v));

    this.roles = new SynthesizerRolesNestedStack(this, common, root,
      NestedStackProps.builder().description(describe(common, "roles & policies")).build());

    this.storage = new SynthesizerStorageNestedStack(this, common, root,
      NestedStackProps.builder().description(describe(common, "ecr & s3 storage")).build());
  }
}
