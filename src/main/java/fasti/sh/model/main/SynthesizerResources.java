package fasti.sh.model.main;

import fasti.sh.model.aws.ecr.EcrRepository;
import fasti.sh.model.aws.iam.IamRole;
import fasti.sh.model.aws.kms.Kms;
import fasti.sh.model.aws.s3.S3Bucket;

public record SynthesizerResources(
  Kms kms,
  S3Bucket assets,
  EcrRepository ecr,
  IamRole cdkExec,
  IamRole cdkDeploy,
  IamRole cdkLookup,
  IamRole cdkAssets,
  IamRole cdkImages
) {}
