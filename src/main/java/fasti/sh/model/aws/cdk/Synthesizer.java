package fasti.sh.model.aws.cdk;

/**
 * Configuration record for AWS CDK synthesizer settings.
 *
 * <p>
 * Defines comprehensive CDK synthesizer configuration including IAM roles, asset publishing settings, and bootstrap parameters for CDK
 * deployment infrastructure.
 *
 * @param cloudFormationExecutionRole
 *          CloudFormation execution role ARN
 * @param deployRoleArn
 *          IAM role ARN for deployment operations
 * @param fileAssetPublishingRoleArn
 *          IAM role ARN for file asset publishing
 * @param imageAssetPublishingRoleArn
 *          IAM role ARN for container image asset publishing
 * @param lookupRoleArn
 *          IAM role ARN for context lookups
 * @param qualifier
 *          Bootstrap stack qualifier
 * @param bucketPrefix
 *          S3 bucket prefix for assets
 * @param deployRoleExternalId
 *          External ID for deploy role assumption
 * @param dockerTagPrefix
 *          Docker image tag prefix
 * @param fileAssetPublishingExternalId
 *          External ID for file asset publishing
 * @param fileAssetsBucketName
 *          S3 bucket name for file assets
 * @param imageAssetPublishingExternalId
 *          External ID for image asset publishing
 * @param imageAssetsRepositoryName
 *          ECR repository name for image assets
 * @param lookupRoleExternalId
 *          External ID for lookup role assumption
 * @param bootstrapStackVersionSsmParameter
 *          SSM parameter for bootstrap version
 * @param generateBootstrapVersionRule
 *          Whether to generate bootstrap version rule
 * @param useLookupRoleForStackOperations
 *          Whether to use lookup role for stack operations
 */
public record Synthesizer(
  String cloudFormationExecutionRole,
  String deployRoleArn,
  String fileAssetPublishingRoleArn,
  String imageAssetPublishingRoleArn,
  String lookupRoleArn,
  String qualifier,
  String bucketPrefix,
  String deployRoleExternalId,
  String dockerTagPrefix,
  String fileAssetPublishingExternalId,
  String fileAssetsBucketName,
  String imageAssetPublishingExternalId,
  String imageAssetsRepositoryName,
  String lookupRoleExternalId,
  String bootstrapStackVersionSsmParameter,
  boolean generateBootstrapVersionRule,
  boolean useLookupRoleForStackOperations
) {}
