package fasti.sh.model.aws.ses;

import fasti.sh.model.aws.ses.action.LambdaActionConf;
import fasti.sh.model.aws.ses.action.S3ActionConf;
import fasti.sh.model.aws.ses.action.SnsActionConf;
import java.util.List;

public record Rule(
  String name,
  boolean enabled,
  boolean scanEnabled,
  List<String> recipients,
  List<S3ActionConf> s3Actions,
  List<SnsActionConf> snsActions,
  List<LambdaActionConf> lambdaActions
) {}
