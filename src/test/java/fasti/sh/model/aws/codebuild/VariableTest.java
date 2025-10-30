package fasti.sh.model.aws.codebuild;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class VariableTest {

  @Test
  public void testVariableCreation() {
    // Test that Variable record can be created
    Variable variable = new Variable("MY_VAR", "default_value");

    assertNotNull(variable);
    assertEquals("MY_VAR", variable.name());
    assertEquals("default_value", variable.defaults());
  }

  @Test
  public void testVariableEquality() {
    // Test that Variables with same values are equal
    Variable var1 = new Variable("MY_VAR", "value");
    Variable var2 = new Variable("MY_VAR", "value");

    assertEquals(var1, var2);
    assertEquals(var1.hashCode(), var2.hashCode());
  }

  @Test
  public void testVariableToString() {
    // Test that Variable has toString
    Variable variable = new Variable("MY_VAR", "value");

    String toString = variable.toString();
    assertNotNull(toString);
    assertTrue(toString.contains("MY_VAR"));
    assertTrue(toString.contains("value"));
  }
}
