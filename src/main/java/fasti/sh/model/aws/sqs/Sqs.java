package fasti.sh.model.aws.sqs;

import fasti.sh.model.aws.iam.PolicyConf;
import java.util.List;
import java.util.Map;

public record Sqs(
  String name,
  int retention,
  List<SqsRule> rules,
  List<PolicyConf> customPolicies,
  Map<String, String> tags
) {}
