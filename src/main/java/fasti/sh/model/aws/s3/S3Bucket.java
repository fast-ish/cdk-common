package fasti.sh.model.aws.s3;

import fasti.sh.model.aws.iam.Principal;
import fasti.sh.model.aws.kms.Kms;
import java.util.List;
import java.util.Map;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.s3.BucketAccessControl;
import software.amazon.awscdk.services.s3.ObjectOwnership;

/**
 * Configuration record for AWS S3 bucket infrastructure.
 *
 * <p>
 * Defines comprehensive S3 bucket configuration including security settings, lifecycle management, policies, encryption, and compliance
 * features.
 *
 * @param name
 *          Unique bucket name identifier
 * @param principal
 *          IAM principal configuration for bucket access
 * @param accessControl
 *          Bucket access control settings (BucketOwnerFullControl, etc.)
 * @param objectOwnership
 *          Object ownership settings (BucketOwnerPreferred, etc.)
 * @param lifecycleRules
 *          List of lifecycle rules for object management
 * @param bucketPolicies
 *          List of bucket policy configurations
 * @param eventBridgeEnabled
 *          Whether to enable EventBridge notifications
 * @param autoDeleteObjects
 *          Whether to automatically delete objects on bucket deletion
 * @param versioned
 *          Whether to enable object versioning
 * @param removalPolicy
 *          CloudFormation removal policy (DESTROY, RETAIN, etc.)
 * @param kms
 *          KMS encryption configuration
 * @param tags
 *          AWS resource tags for organization and billing
 */
public record S3Bucket(
  String name,
  Principal principal,
  BucketAccessControl accessControl,
  ObjectOwnership objectOwnership,
  List<BucketLifecycleRule> lifecycleRules,
  List<BucketPolicyConf> bucketPolicies,
  boolean eventBridgeEnabled,
  boolean autoDeleteObjects,
  boolean versioned,
  RemovalPolicy removalPolicy,
  Kms kms,
  Map<String, String> tags
) {}
