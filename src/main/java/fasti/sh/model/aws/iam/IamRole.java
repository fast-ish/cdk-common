package fasti.sh.model.aws.iam;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;

/**
 * Configuration record for AWS IAM Role infrastructure.
 *
 * <p>
 * Defines comprehensive IAM role configuration including trust policies, managed policies, custom policies, and principal associations.
 *
 * @param name
 *          Unique role name identifier
 * @param description
 *          Human-readable description of the role purpose
 * @param principal
 *          Principal configuration for role assumption
 * @param managedPolicyNames
 *          List of AWS managed policy ARNs to attach
 * @param customPolicies
 *          List of custom policy configurations
 * @param tags
 *          AWS resource tags for organization and billing
 */
public record IamRole(
  String name,
  String description,
  Principal principal,
  List<String> managedPolicyNames,
  List<PolicyConf> customPolicies,
  Map<String, String> tags
) {

  public static void addAssumeRoleStatements(Role role, List<Role> principals) {
    Optional
      .ofNullable(role.getAssumeRolePolicy())
      .ifPresentOrElse(
        p -> p
          .addStatements(
            PolicyStatement.Builder.create().effect(Effect.ALLOW).actions(List.of("sts:AssumeRole")).principals(principals).build()),
        () -> {
          throw new IllegalStateException("AssumeRolePolicy is null for role: " + role.getRoleId());
        });
  }
}
