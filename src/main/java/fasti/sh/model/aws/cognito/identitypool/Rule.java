package fasti.sh.model.aws.cognito.identitypool;

import software.amazon.awscdk.services.cognito.identitypool.alpha.RoleMappingMatchType;

/**
 * Configuration record for Cognito Identity Pool role mapping rule settings.
 *
 * <p>
 * Defines individual rules for role mapping in Cognito Identity Pools including claim matching and role resolution logic.
 *
 * @param claim
 *          The claim to match against
 * @param claimValue
 *          The value of the claim to match
 * @param matchType
 *          The type of matching to perform
 */
public record Rule(
  String claim,
  String claimValue,
  RoleMappingMatchType matchType
) {}
