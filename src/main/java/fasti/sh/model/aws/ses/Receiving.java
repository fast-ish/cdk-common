package fasti.sh.model.aws.ses;

import fasti.sh.model.aws.s3.S3Bucket;
import java.util.List;

public record Receiving(
  String name,
  boolean dropSpam,
  S3Bucket bucket,
  List<Rule> rules
) {}
