package fasti.sh.model.aws.cognito.userpool;

/**
 * Configuration record for Cognito User Pool group settings.
 *
 * <p>
 * Defines user groups within Cognito User Pools including identification, description, and precedence settings.
 *
 * @param name
 *          Group name identifier
 * @param description
 *          Human-readable description of the group
 * @param precedence
 *          Group precedence value for priority ordering
 */
public record Group(
  String name,
  String description,
  int precedence
) {}
