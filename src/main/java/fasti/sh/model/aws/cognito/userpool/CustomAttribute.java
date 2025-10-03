package fasti.sh.model.aws.cognito.userpool;

/**
 * Configuration record for Cognito User Pool custom attribute settings.
 *
 * <p>
 * Defines custom user attributes for Cognito User Pools including name, data type, constraints, and mutability settings.
 *
 * @param name
 *          Custom attribute name identifier
 * @param type
 *          Attribute data type
 * @param min
 *          Minimum value/length constraint
 * @param max
 *          Maximum value/length constraint
 * @param mutable
 *          Whether the attribute can be modified after creation
 */
public record CustomAttribute(
  String name,
  String type,
  int min,
  int max,
  boolean mutable
) {}
