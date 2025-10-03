package fasti.sh.model.aws.dynamodb;

import fasti.sh.model.aws.kinesis.KinesisStream;

public record Streams(
  KinesisStream kinesis,
  DynamoDbStream dynamoDb
) {}
