package fasti.sh.model.aws.cognito.client;

import java.util.List;
import java.util.Map;

/**
 * Configuration record for Cognito User Pool client settings.
 *
 * <p>
 * Defines comprehensive client configuration for Cognito User Pool applications including authentication flows, token settings, OAuth
 * configuration, and user attributes.
 *
 * @param accessTokenValidity
 *          Access token validity period
 * @param authFlow
 *          Authentication flow configuration
 * @param authSessionValidity
 *          Authentication session validity period
 * @param preventUserExistenceErrors
 *          Whether to prevent user existence errors
 * @param disableOAuth
 *          Whether to disable OAuth functionality
 * @param enableTokenRevocation
 *          Whether to enable token revocation
 * @param generateSecret
 *          Whether to generate client secret
 * @param enablePropagateAdditionalUserContextData
 *          Whether to propagate additional user context
 * @param idTokenValidity
 *          ID token validity period
 * @param oAuth
 *          OAuth configuration settings
 * @param readAttributes
 *          Client read attribute permissions
 * @param writeAttributes
 *          Client write attribute permissions
 * @param customAttributes
 *          List of custom attributes
 * @param refreshTokenValidity
 *          Refresh token validity period
 * @param tags
 *          Resource tags for the client
 */
public record UserPoolClientConf(
  String accessTokenValidity,
  AuthFlow authFlow,
  String authSessionValidity,
  boolean preventUserExistenceErrors,
  boolean disableOAuth,
  boolean enableTokenRevocation,
  boolean generateSecret,
  boolean enablePropagateAdditionalUserContextData,
  String idTokenValidity,
  String name,
  CognitoOAuth oauth,
  ClientAttributesConf readAttributes,
  ClientAttributesConf writeAttributes,
  List<String> customAttributes,
  String refreshTokenValidity,
  Map<String, String> tags
) {}
