package fasti.sh.model.aws.codebuild;

import fasti.sh.model.aws.s3.S3Bucket;

/**
 * Configuration record for SSL/TLS certificate settings in CodeBuild environment.
 *
 * <p>
 * Defines certificate location in S3 for secure build environments that require custom certificates for accessing private repositories or
 * services.
 *
 * @param bucket
 *          S3 bucket configuration containing the certificate file
 * @param objectKey
 *          S3 object key (path) to the certificate file
 */
public record Certificate(
  S3Bucket bucket,
  String objectKey
) {}
