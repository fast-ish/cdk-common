package fasti.sh.model.aws.secretsmanager;

import java.util.Map;

/**
 * Configuration record for AWS Secrets Manager secret credentials.
 *
 * <p>
 * Defines the configuration for storing and managing credentials in AWS Secrets Manager including username, password format, and metadata.
 *
 * @param name
 *          Secret name identifier
 * @param description
 *          Human-readable description of the secret
 * @param username
 *          Username component of the credentials
 * @param password
 *          Password format configuration
 * @param removalPolicy
 *          AWS CloudFormation removal policy
 * @param tags
 *          Resource tags for the secret
 */
public record SecretCredentials(
  String name,
  String description,
  String username,
  SecretFormat password,
  String removalPolicy,
  Map<String, String> tags
) {}
