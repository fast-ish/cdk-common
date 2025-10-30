package fasti.sh.model.aws.codebuild;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CodeStarConnectionSourceTest {

  @Test
  public void testRecordCreation() {
    CodeStarConnectionSource source = new CodeStarConnectionSource(
      "owner",
      "repo",
      "main",
      "arn:aws:codestar:us-east-1:123456789012:connection/abc",
      true);

    assertNotNull(source);
    assertEquals("owner", source.owner());
    assertEquals("repo", source.repo());
    assertEquals("main", source.branch());
    assertEquals("arn:aws:codestar:us-east-1:123456789012:connection/abc", source.connection());
    assertTrue(source.triggerOnPush());
  }

  @Test
  public void testRecordEquality() {
    CodeStarConnectionSource source1 = new CodeStarConnectionSource("owner", "repo", "main", "conn", true);
    CodeStarConnectionSource source2 = new CodeStarConnectionSource("owner", "repo", "main", "conn", true);

    assertEquals(source1, source2);
    assertEquals(source1.hashCode(), source2.hashCode());
  }

  @Test
  public void testRecordToString() {
    CodeStarConnectionSource source = new CodeStarConnectionSource("owner", "repo", "main", "conn", true);
    String toString = source.toString();

    assertNotNull(toString);
    assertTrue(toString.contains("owner"));
    assertTrue(toString.contains("repo"));
  }
}
