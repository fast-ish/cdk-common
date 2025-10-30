package fasti.sh.model.aws.ses.action;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.fn.Lambda;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for SES action model records.
 */
class SesActionModelsTest {

  @Test
  void testSnsActionConfBasic() {
    var action = new SnsActionConf("arn:aws:sns:us-east-1:123456789012:ses-notifications");

    assertEquals("arn:aws:sns:us-east-1:123456789012:ses-notifications", action.topic());
    assertRecordToString(action);
  }

  @Test
  void testSnsActionConfDifferentTopic() {
    var action = new SnsActionConf("arn:aws:sns:eu-west-1:987654321098:email-alerts");

    assertEquals("arn:aws:sns:eu-west-1:987654321098:email-alerts", action.topic());
  }

  @Test
  void testS3ActionConfBasic() {
    var action = new S3ActionConf(
      "emails/",
      "arn:aws:sns:us-east-1:123456789012:s3-notifications");

    assertEquals("emails/", action.prefix());
    assertEquals("arn:aws:sns:us-east-1:123456789012:s3-notifications", action.topic());
    assertRecordToString(action);
  }

  @Test
  void testS3ActionConfWithPath() {
    var action = new S3ActionConf(
      "incoming/2024/",
      "arn:aws:sns:us-east-1:123456789012:storage-notifications");

    assertEquals("incoming/2024/", action.prefix());
  }

  @Test
  void testS3ActionConfRootPrefix() {
    var action = new S3ActionConf(
      "",
      "arn:aws:sns:us-east-1:123456789012:root-notifications");

    assertEquals("", action.prefix());
  }

  @Test
  void testLambdaActionConfBasic() {
    var lambda = new Lambda(
      "email-processor",
      "Email processing function",
      "target/lambda.zip",
      "index.handler",
      "PRIVATE_WITH_EGRESS",
      60,
      512,
      null,
      null,
      List.of(),
      List.of(),
      Map.of());

    var action = new LambdaActionConf(
      "arn:aws:sns:us-east-1:123456789012:lambda-notifications",
      lambda,
      "Event");

    assertEquals("arn:aws:sns:us-east-1:123456789012:lambda-notifications", action.topic());
    assertNotNull(action.function());
    assertEquals("email-processor", action.function().name());
    assertEquals("Event", action.invocationType());
    assertRecordToString(action);
  }

  @Test
  void testLambdaActionConfRequestResponse() {
    var lambda = new Lambda(
      "sync-processor",
      "Synchronous processor",
      "target/lambda.zip",
      "sync.handler",
      "PRIVATE_WITH_EGRESS",
      30,
      256,
      null,
      null,
      List.of(),
      List.of(),
      Map.of());

    var action = new LambdaActionConf(
      "arn:aws:sns:us-east-1:123456789012:sync-notifications",
      lambda,
      "RequestResponse");

    assertEquals("RequestResponse", action.invocationType());
  }

  @Test
  void testLambdaActionConfDifferentMemory() {
    var lambda = new Lambda(
      "high-memory-processor",
      "High memory function",
      "target/lambda.zip",
      "heavy.handler",
      "PRIVATE_WITH_EGRESS",
      300,
      3008,
      null,
      null,
      List.of(),
      List.of(),
      Map.of());

    var action = new LambdaActionConf(
      "arn:aws:sns:us-east-1:123456789012:heavy-notifications",
      lambda,
      "Event");

    assertEquals(3008, action.function().memorySize());
  }
}
