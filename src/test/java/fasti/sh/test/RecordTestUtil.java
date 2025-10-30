package fasti.sh.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;

/**
 * Utility class for testing Java record classes with minimal boilerplate. Uses modern Java reflection APIs to validate record behavior.
 */
public final class RecordTestUtil {

  private RecordTestUtil() {}

  /**
   * Validates that a record instance is properly constructed and all accessors work.
   *
   * @param record
   *          the record instance to test
   * @param expectedValues
   *          the expected values for each record component in order
   */
  public static void assertRecordValid(Record record, Object... expectedValues) {
    assertNotNull(record, "Record should not be null");

    var components = record.getClass().getRecordComponents();
    assertEquals(
      expectedValues.length,
      components.length,
      "Number of expected values should match number of record components");

    for (var i = 0; i < components.length; i++) {
      var component = components[i];
      try {
        var accessor = component.getAccessor();
        var actualValue = accessor.invoke(record);
        assertEquals(expectedValues[i], actualValue, "Component " + component.getName() + " should match expected value");
      } catch (Exception e) {
        throw new RuntimeException("Failed to invoke accessor for " + component.getName(), e);
      }
    }
  }

  /**
   * Tests that two record instances with the same values are equal.
   *
   * @param record1
   *          first record instance
   * @param record2
   *          second record instance with same values
   */
  public static void assertRecordEquality(Record record1, Record record2) {
    assertNotNull(record1);
    assertNotNull(record2);
    assertEquals(record1, record2, "Records with same values should be equal");
    assertEquals(record1.hashCode(), record2.hashCode(), "Equal records should have same hashCode");
  }

  /**
   * Tests that the record's toString() method contains all component names.
   *
   * @param record
   *          the record instance to test
   */
  public static void assertRecordToString(Record record) {
    assertNotNull(record);
    var toString = record.toString();
    assertNotNull(toString, "toString() should not return null");
    assertTrue(toString.contains(record.getClass().getSimpleName()), "toString() should contain class name");

    // Verify all component names appear in toString
    for (var component : record.getClass().getRecordComponents()) {
      assertTrue(
        toString.contains(component.getName()),
        "toString() should contain component name: " + component.getName());
    }
  }

  /**
   * Gets the canonical constructor for a record class.
   *
   * @param recordClass
   *          the record class
   * @return the canonical constructor
   */
  @SuppressWarnings("unchecked")
  public static <T extends Record> Constructor<T> getCanonicalConstructor(Class<T> recordClass) {
    var components = recordClass.getRecordComponents();
    var paramTypes = Arrays.stream(components).map(RecordComponent::getType).toArray(Class<?>[]::new);

    try {
      return (Constructor<T>) recordClass.getDeclaredConstructor(paramTypes);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Failed to find canonical constructor for " + recordClass.getName(), e);
    }
  }

  /**
   * Creates a record instance using the canonical constructor.
   *
   * @param recordClass
   *          the record class
   * @param args
   *          constructor arguments
   * @return new record instance
   */
  public static <T extends Record> T createRecord(Class<T> recordClass, Object... args) {
    try {
      var constructor = getCanonicalConstructor(recordClass);
      return constructor.newInstance(args);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create record instance of " + recordClass.getName(), e);
    }
  }
}
