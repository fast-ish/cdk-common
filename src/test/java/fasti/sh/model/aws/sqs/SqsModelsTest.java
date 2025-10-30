package fasti.sh.model.aws.sqs;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for SQS model records.
 */
class SqsModelsTest {

  @Test
  void testSqsBasic() {
    var sqs = new Sqs(
      "test-queue",
      86400, // 1 day
      List.of(),
      List.of(),
      Map.of());

    assertEquals("test-queue", sqs.name());
    assertEquals(86400, sqs.retention());
    assertTrue(sqs.rules().isEmpty());
    assertTrue(sqs.customPolicies().isEmpty());
  }

  @Test
  void testSqsWithLongRetention() {
    var sqs = new Sqs(
      "long-retention-queue",
      1209600, // 14 days (max)
      List.of(),
      List.of(),
      Map.of("Retention", "max"));

    assertEquals(1209600, sqs.retention());
    assertEquals(1, sqs.tags().size());
  }

  @Test
  void testSqsEquality() {
    var sqs1 = new Sqs("q1", 3600, List.of(), List.of(), Map.of());
    var sqs2 = new Sqs("q1", 3600, List.of(), List.of(), Map.of());

    assertRecordEquality(sqs1, sqs2);
    assertRecordToString(sqs1);
  }

  @Test
  void testSqsShortRetention() {
    var sqs = new Sqs(
      "quick-queue",
      60, // 1 minute
      List.of(),
      List.of(),
      Map.of());

    assertEquals(60, sqs.retention());
  }

  @Test
  void testSqsEventPatternBasic() {
    var pattern = new SqsEventPattern(
      List.of("aws.ec2"),
      List.of("EC2 Instance State-change Notification"));

    assertEquals(1, pattern.source().size());
    assertEquals("aws.ec2", pattern.source().get(0));
    assertEquals(1, pattern.detailType().size());
  }

  @Test
  void testSqsEventPatternMultipleSources() {
    var pattern = new SqsEventPattern(
      List.of("aws.ec2", "aws.s3", "aws.lambda"),
      List.of("State Change"));

    assertEquals(3, pattern.source().size());
    assertTrue(pattern.source().contains("aws.ec2"));
    assertTrue(pattern.source().contains("aws.s3"));
    assertTrue(pattern.source().contains("aws.lambda"));
  }

  @Test
  void testSqsEventPatternMultipleDetailTypes() {
    var pattern = new SqsEventPattern(
      List.of("aws.dynamodb"),
      List.of("Stream Record", "Insert", "Modify", "Remove"));

    assertEquals(4, pattern.detailType().size());
    assertTrue(pattern.detailType().contains("Insert"));
    assertTrue(pattern.detailType().contains("Modify"));
  }

  @Test
  void testSqsEventPatternEmpty() {
    var pattern = new SqsEventPattern(List.of(), List.of());

    assertTrue(pattern.source().isEmpty());
    assertTrue(pattern.detailType().isEmpty());
  }

  @Test
  void testSqsRuleBasic() {
    var pattern = new SqsEventPattern(
      List.of("aws.ec2"),
      List.of("State Change"));

    var rule = new SqsRule(
      "ec2-state-rule",
      "Rule for EC2 state changes",
      true,
      pattern);

    assertEquals("ec2-state-rule", rule.name());
    assertEquals("Rule for EC2 state changes", rule.description());
    assertTrue(rule.enabled());
    assertNotNull(rule.eventPattern());
  }

  @Test
  void testSqsRuleDisabled() {
    var pattern = new SqsEventPattern(
      List.of("aws.s3"),
      List.of("Object Created"));

    var rule = new SqsRule(
      "s3-rule",
      "S3 object creation rule",
      false,
      pattern);

    assertFalse(rule.enabled());
    assertEquals("s3-rule", rule.name());
  }

  @Test
  void testSqsRuleEquality() {
    var pattern = new SqsEventPattern(List.of("aws.sns"), List.of("Notification"));
    var rule1 = new SqsRule("rule1", "desc", true, pattern);
    var rule2 = new SqsRule("rule1", "desc", true, pattern);

    assertRecordEquality(rule1, rule2);
    assertRecordToString(rule1);
  }

  @Test
  void testSqsEventPatternEquality() {
    var pattern1 = new SqsEventPattern(List.of("aws.rds"), List.of("Backup"));
    var pattern2 = new SqsEventPattern(List.of("aws.rds"), List.of("Backup"));

    assertRecordEquality(pattern1, pattern2);
    assertRecordToString(pattern1);
  }

  @Test
  void testSqsWithRules() {
    var pattern = new SqsEventPattern(
      List.of("aws.cloudwatch"),
      List.of("Alarm State Change"));

    var rule = new SqsRule(
      "alarm-rule",
      "CloudWatch alarm rule",
      true,
      pattern);

    var sqs = new Sqs(
      "queue-with-rules",
      86400,
      List.of(rule),
      List.of(),
      Map.of());

    assertEquals(1, sqs.rules().size());
    assertEquals("alarm-rule", sqs.rules().get(0).name());
  }

  @Test
  void testSqsWithMultipleRules() {
    var pattern1 = new SqsEventPattern(List.of("aws.ec2"), List.of("State"));
    var pattern2 = new SqsEventPattern(List.of("aws.s3"), List.of("Created"));

    var rule1 = new SqsRule("ec2-rule", "EC2", true, pattern1);
    var rule2 = new SqsRule("s3-rule", "S3", false, pattern2);

    var sqs = new Sqs(
      "multi-rule-queue",
      86400,
      List.of(rule1, rule2),
      List.of(),
      Map.of("Purpose", "multi-rule"));

    assertEquals(2, sqs.rules().size());
    assertTrue(sqs.rules().stream().anyMatch(r -> r.name().equals("ec2-rule")));
    assertTrue(sqs.rules().stream().anyMatch(r -> r.name().equals("s3-rule")));
  }

  @Test
  void testSqsRuleWithComplexPattern() {
    var pattern = new SqsEventPattern(
      List.of("aws.ecs", "aws.fargate"),
      List.of("Task State Change", "Container State Change", "Service State Change"));

    var rule = new SqsRule(
      "ecs-monitoring",
      "ECS and Fargate monitoring rule",
      true,
      pattern);

    assertEquals("ecs-monitoring", rule.name());
    assertEquals(2, rule.eventPattern().source().size());
    assertEquals(3, rule.eventPattern().detailType().size());
  }

  @Test
  void testSqsWithCustomTags() {
    var sqs = new Sqs(
      "tagged-queue",
      86400,
      List.of(),
      List.of(),
      Map.of("Environment", "production", "Team", "platform", "CostCenter", "engineering"));

    assertEquals(3, sqs.tags().size());
    assertEquals("production", sqs.tags().get("Environment"));
    assertEquals("platform", sqs.tags().get("Team"));
  }

  @Test
  void testSqsEventPatternSingleValues() {
    var pattern = new SqsEventPattern(
      List.of("aws.codepipeline"),
      List.of("Pipeline Execution State Change"));

    assertEquals(1, pattern.source().size());
    assertEquals(1, pattern.detailType().size());
    assertEquals("aws.codepipeline", pattern.source().get(0));
  }

  @Test
  void testSqsRuleWithEmptyDescription() {
    var pattern = new SqsEventPattern(List.of("aws.logs"), List.of("Log Event"));
    var rule = new SqsRule("logs-rule", "", true, pattern);

    assertEquals("", rule.description());
    assertTrue(rule.enabled());
  }
}
