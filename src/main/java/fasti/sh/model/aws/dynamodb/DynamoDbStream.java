package fasti.sh.model.aws.dynamodb;

/**
 * Configuration record for DynamoDB stream settings.
 *
 * <p>
 * Defines DynamoDB Streams configuration including enablement and stream view type for change data capture.
 *
 * @param enabled
 *          Whether DynamoDB Streams is enabled
 * @param type
 *          Stream view type (KEYS_ONLY, NEW_IMAGE, OLD_IMAGE, NEW_AND_OLD_IMAGES)
 */
public record DynamoDbStream(
  boolean enabled,
  String type
) {}
