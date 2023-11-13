import org.junit.Test;
import static org.junit.Assert.*;

public class EdgeFieldTest {

		@Test
		public void testConstructorValidInput() {
				String validInput = "1|FieldName|0|0|false|true|Default";
				EdgeField edgeField = new EdgeField(validInput);

				assertEquals(1, edgeField.getNumFigure());
				assertEquals("FieldName", edgeField.getName());
				assertEquals(0, edgeField.getTableID());
				assertEquals(0, edgeField.getTableBound());
				assertFalse(edgeField.getDisallowNull());
				assertTrue(edgeField.getIsPrimaryKey());
				assertEquals("Default", edgeField.getDefaultValue());
				assertEquals(EdgeField.VARCHAR_DEFAULT_LENGTH, edgeField.getVarcharValue());
				assertEquals(0, edgeField.getDataType());
		}

		@Test(expected = NumberFormatException.class)
		public void testConstructorInvalidInput() {
				String invalidInput = "NotAnInteger|InvalidName";
				new EdgeField(invalidInput); // This should throw a NumberFormatException
		}

		@Test
		public void testGettersAndSetters() {
				EdgeField edgeField = new EdgeField("1|FieldName|0|0|false|true|Default");

				edgeField.setTableID(2);
				assertEquals(2, edgeField.getTableID());

				edgeField.setTableBound(3);
				assertEquals(3, edgeField.getTableBound());

				edgeField.setFieldBound(4);
				assertEquals(4, edgeField.getFieldBound());

				edgeField.setDisallowNull(true);
				assertTrue(edgeField.getDisallowNull());

				edgeField.setIsPrimaryKey(false);
				assertFalse(edgeField.getIsPrimaryKey());

				edgeField.setDefaultValue("NewDefault");
				assertEquals("NewDefault", edgeField.getDefaultValue());

				edgeField.setVarcharValue(10);
				assertEquals(10, edgeField.getVarcharValue());

				edgeField.setDataType(2);
				assertEquals(2, edgeField.getDataType());
		}

		@Test
		public void testToString() {
				EdgeField edgeField = new EdgeField("1|FieldName|0|0|false|true|Default");
				String expectedString = "1|FieldName|0|0|0|1|10|false|true|Default";
				assertEquals(expectedString, edgeField.toString());
		}

		@Test
		public void testBoundaryValues() {
				EdgeField edgeField = new EdgeField("1|FieldName|0|0|false|true|Default");

				// Test setting the maximum varchar value
				edgeField.setVarcharValue(Integer.MAX_VALUE);
				assertEquals(Integer.MAX_VALUE, edgeField.getVarcharValue());

				// Test setting the minimum varchar value
				edgeField.setVarcharValue(EdgeField.VARCHAR_DEFAULT_LENGTH);
				assertEquals(EdgeField.VARCHAR_DEFAULT_LENGTH, edgeField.getVarcharValue());
		}

		@Test(expected = IllegalArgumentException.class)
		public void testInvalidDataType() {
				EdgeField edgeField = new EdgeField("1|FieldName|0|0|false|true|Default");
				edgeField.setDataType(10); // This should throw an IllegalArgumentException
		}

		@Test
		public void testDefaultValueForDataType() {
				EdgeField edgeField = new EdgeField("1|FieldName|0|0|false|true|Default");

				// Test setting default value for different data types
				edgeField.setDataType(EdgeField.DATA_TYPE_BOOLEAN);
				edgeField.setDefaultValue("true");
				assertEquals("true", edgeField.getDefaultValue());

				edgeField.setDataType(EdgeField.DATA_TYPE_INTEGER);
				edgeField.setDefaultValue("42");
				assertEquals("42", edgeField.getDefaultValue());

				edgeField.setDataType(EdgeField.DATA_TYPE_DOUBLE);
				edgeField.setDefaultValue("3.14");
				assertEquals("3.14", edgeField.getDefaultValue());
		}
}
