// MILES KRASSEN
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CreateDDLMySQLTest {

  // util for seeing the characters in the DDL output
  public static String unEscapeString(String s){
    
      StringBuilder sb = new StringBuilder();
    
      for (int i=0; i<s.length(); i++) {

        switch (s.charAt(i)){
            case '\n': sb.append("\\n"); break;
            case '\t': sb.append("\\t"); break;
            case '\r': sb.append("\\r"); break;
            default: sb.append(s.charAt(i));
        }
      }
    
      return sb.toString();
  }


  @Test
  public void testEmptyConstructorDefaultValues() {

    CreateDDLMySQL testObj = new CreateDDLMySQL();

    //only public getters
    assertNull(testObj.getDatabaseName());
    assertEquals(testObj.getProductName(), "MySQL");
    
  }

  @Test
  public void testConstructorWithValidParams() {

    EdgeTable table = new EdgeTable("1|Table1");
    EdgeField field = new EdgeField("1|Field1");

    table.addNativeField(1);
    field.setTableID(1);

    table.makeArrays();

    EdgeTable[] tables = new EdgeTable[]{table};
    EdgeField[] fields = new EdgeField[]{field};

    CreateDDLMySQL testObj = new CreateDDLMySQL(tables, fields);

    assertEquals(
      testObj.getDatabaseString(),
      "CREATE DATABASE MySQLDB;\r\nUSE MySQLDB;\r\nCREATE TABLE Table1 (\r\n\tField1 VARCHAR(1)\r\n);\r\n\r\n"
    );
  }

  @Test
  public void testConstructorWithFieldAndEmptyTableArray() {
    
    EdgeTable table = new EdgeTable("1|Table1");
    EdgeField field = new EdgeField("1|Field1");

    table.addNativeField(1);
    field.setTableID(1);

    table.makeArrays();

    EdgeTable[] tables = new EdgeTable[0];
    EdgeField[] fields = new EdgeField[]{field};

    CreateDDLMySQL testObj = new CreateDDLMySQL(tables, fields);

    assertEquals(
      testObj.getDatabaseString(),
      "CREATE DATABASE MySQLDB;\r\nUSE MySQLDB;\r\n"
    );
    
  }

  @Test(expected = NullPointerException.class)
  public void testConstructorWithValidParamsNoMakeArraysCalled() {

    EdgeTable table = new EdgeTable("1|Table1");
    EdgeField field = new EdgeField("1|Field1");

    table.addNativeField(1);
    field.setTableID(1);

    EdgeTable[] tables = new EdgeTable[]{table};
    EdgeField[] fields = new EdgeField[]{field};

    CreateDDLMySQL testObj = new CreateDDLMySQL(tables, fields);

    testObj.createDDL();
  }

  @Test
  public void testProductNameIsMySQL() {
    
    CreateDDLMySQL testObj = new CreateDDLMySQL();

    assertEquals(testObj.getProductName(), "MySQL");
  }

  @Test
  public void testValidDatabaseName() {
    
    CreateDDLMySQL testObj = new CreateDDLMySQL();

    String name = "MySQLDB";
    testObj.setDatabaseName(name);

    assertEquals(testObj.getDatabaseName(), name);
  }

  @Test
  public void testInvalidDatabaseName() {

    CreateDDLMySQL testObj = new CreateDDLMySQL();
    
    assertNotEquals(testObj.getDatabaseName(), "");
  }

  // unable to test convertStrBooleanToInt() as it's protected?
  
  
}