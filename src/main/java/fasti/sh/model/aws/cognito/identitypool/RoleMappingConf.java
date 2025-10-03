package fasti.sh.model.aws.cognito.identitypool;

import java.util.List;

/**
 * Configuration record for Cognito Identity Pool role mapping settings.
 *
 * <p>
 * Defines role mapping configuration for Cognito Identity Pools including key identification, token usage, and rule resolution.
 *
 * @param key
 *          Role mapping key identifier
 * @param useToken
 *          Whether to use tokens for role resolution
 * @param resolveAmbiguousRoles
 *          Whether to resolve ambiguous role mappings
 * @param rules
 *          List of rules for role mapping
 */
public record RoleMappingConf(
  String key,
  boolean useToken,
  boolean resolveAmbiguousRoles,
  List<Rule> rules
) {}
