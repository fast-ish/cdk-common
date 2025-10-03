package fasti.sh.model.aws.cognito.client;

import java.util.List;

/**
 * Configuration record for Cognito OAuth settings.
 *
 * <p>
 * Defines OAuth 2.0 configuration for Cognito User Pool clients including grant types, callback URLs, logout URLs, and OAuth scopes.
 *
 * @param authorizationCodeGrant
 *          Whether to enable authorization code grant
 * @param callbackUrls
 *          List of callback URLs for OAuth flow
 * @param clientCredentials
 *          Whether to enable client credentials grant
 * @param implicitCodeGrant
 *          Whether to enable implicit code grant
 * @param logoutUrls
 *          List of logout URLs for OAuth flow
 * @param scopes
 *          List of OAuth scopes
 */
public record CognitoOAuth(
  boolean authorizationCodeGrant,
  List<String> callbackUrls,
  boolean clientCredentials,
  boolean implicitCodeGrant,
  List<String> logoutUrls,
  List<String> scopes
) {}
