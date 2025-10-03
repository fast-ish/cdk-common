package fasti.sh.execute.aws.cognito;

import fasti.sh.execute.aws.iam.RoleConstruct;
import fasti.sh.execute.serialization.Mapper;
import fasti.sh.execute.serialization.Template;
import fasti.sh.model.aws.cognito.userpool.Sns;
import fasti.sh.model.aws.cognito.userpool.UserPoolConf;
import fasti.sh.model.main.Common;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awscdk.services.iam.Role;
import software.constructs.Construct;

@Slf4j
@Getter
public class UserPoolSnsConstruct {
  private final Role role;
  private final String externalId;

  @SneakyThrows
  public UserPoolSnsConstruct(Construct scope, Common common, UserPoolConf conf) {
    var snsYaml = Template.parse(scope, conf.sns());
    var snsConf = Mapper.get().readValue(snsYaml, Sns.class);

    log.debug("{} [common: {} conf: {}]", "UserPoolSnsConstruct", common, conf);

    this.role = new RoleConstruct(scope, common, snsConf.role()).role();
    this.externalId = snsConf.externalId();
  }
}
