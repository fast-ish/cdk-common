package fasti.sh.model.aws.s3;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Configuration record for AWS S3 bucket policy statement settings.
 *
 * <p>
 * Represents an IAM policy statement with specialized handling for principals. The structure follows AWS IAM policy format to facilitate
 * copying, manipulating, and parsing existing policies without conversion to other formats.
 *
 * <p>
 * Reference: <a href="https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_examples.html">AWS IAM Policy Examples</a>
 *
 * @param sid
 *          Statement identifier for the policy statement
 * @param effect
 *          Policy effect (Allow or Deny)
 * @param actions
 *          List of IAM actions permitted or denied by this statement
 * @param resources
 *          List of AWS resource ARNs affected by this statement
 * @param conditions
 *          Map of condition operators and their associated conditions
 */
public record BucketPolicyStatementConf(
  @JsonProperty("Sid") String sid,
  @JsonProperty("Effect") String effect,
  @JsonProperty("Action") List<String> actions,
  @JsonProperty("Resource") List<String> resources,
  @JsonProperty("Condition") Map<String, Object> conditions
) {}
