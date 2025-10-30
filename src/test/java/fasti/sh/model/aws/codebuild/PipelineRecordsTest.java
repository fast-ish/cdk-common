package fasti.sh.model.aws.codebuild;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.codepipeline.ExecutionMode;
import software.amazon.awscdk.services.codepipeline.PipelineType;

public class PipelineRecordsTest {

  @Test
  public void testPipelineRecord() {
    CodeStarConnectionSource cdkRepo = new CodeStarConnectionSource("owner", "cdk-repo", "main", "conn-arn", true);
    CodeStarConnectionSource deployment = new CodeStarConnectionSource("owner", "deploy-repo", "main", "conn-arn", true);

    Pipeline pipeline = new Pipeline(
      "my-pipeline",
      "Test pipeline",
      PipelineType.V2,
      ExecutionMode.QUEUED,
      List.of(new Variable("VAR1", "value1")),
      false,
      true,
      cdkRepo,
      deployment);

    assertNotNull(pipeline);
    assertEquals("my-pipeline", pipeline.name());
    assertEquals("Test pipeline", pipeline.description());
    assertEquals(PipelineType.V2, pipeline.pipelineType());
    assertEquals(ExecutionMode.QUEUED, pipeline.executionMode());
    assertEquals(1, pipeline.variables().size());
    assertEquals(false, pipeline.crossAccountKeys());
    assertEquals(true, pipeline.restartExecutionOnUpdate());
    assertEquals(cdkRepo, pipeline.cdkRepo());
    assertEquals(deployment, pipeline.deployment());
  }

  @Test
  public void testPipelineToString() {
    CodeStarConnectionSource source = new CodeStarConnectionSource("owner", "repo", "main", "conn", true);
    Pipeline pipeline = new Pipeline("name", "desc", PipelineType.V2, ExecutionMode.QUEUED, List.of(), false, false, source, source);

    String toString = pipeline.toString();
    assertNotNull(toString);
  }
}
