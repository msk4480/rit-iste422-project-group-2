import org.junit.Test;

import java.lang.reflect.Array;

import static org.junit.Assert.*;
//Author: Anthony Garcia (ajg3062)
public class EdgeTableTest {

    @Test
    public void testConstructorValidInput() {
        String valid = "1|TestTable";
        EdgeTable edgeTable = new EdgeTable(valid);

        assertEquals(1, edgeTable.getNumFigure());
        assertEquals("TestTable", edgeTable.getName());
        assertNull(edgeTable.getRelatedTablesArray());
        assertNull(edgeTable.getRelatedFieldsArray());
        assertNull(edgeTable.getNativeFieldsArray());
    }

    @Test(expected = NumberFormatException.class)
    public void testConstructorInvalidInput() {
        String bad = "One|Illegal";
        new EdgeTable(bad);
    }

    @Test
    public void testAccessAndMutate() {
        int[] expectedRel = new int[]{1, 3};
        int[] expectedRelField = new int[]{0, 0};
        int[] expectedNat = new int[]{2, 4};
        EdgeTable edgeTable = new EdgeTable("2|GetSet");

        edgeTable.addRelatedTable(1);
        edgeTable.addNativeField(2);
        edgeTable.addRelatedTable(3);
        edgeTable.addNativeField(4);
        edgeTable.makeArrays();

        assertArrayEquals(expectedNat, edgeTable.getNativeFieldsArray());
        assertArrayEquals(expectedRel, edgeTable.getRelatedTablesArray());
        assertArrayEquals(expectedRelField, edgeTable.getRelatedFieldsArray());

        Array.set(expectedRelField, 0, 20);
        edgeTable.setRelatedField(0, 20);
        assertArrayEquals(expectedRelField, edgeTable.getRelatedFieldsArray());

        Array.set(expectedNat, 0, 4);
        Array.set(expectedNat, 1, 2);
        Array.set(expectedRelField, 0, 0);
        Array.set(expectedRelField, 1, 20);
        edgeTable.moveFieldUp(1);
        assertArrayEquals(expectedNat, edgeTable.getNativeFieldsArray());
        assertArrayEquals(expectedRelField, edgeTable.getRelatedFieldsArray());

        Array.set(expectedNat, 1, 4);
        Array.set(expectedNat, 0, 2);
        Array.set(expectedRelField, 1, 0);
        Array.set(expectedRelField, 0, 20);
        edgeTable.moveFieldDown(0);
        assertArrayEquals(expectedNat, edgeTable.getNativeFieldsArray());
        assertArrayEquals(expectedRelField, edgeTable.getRelatedFieldsArray());
    }

    @Test
    public void testToString() {
        EdgeTable edgeTable = new EdgeTable("1|StringTest");
        edgeTable.addRelatedTable(1);
        edgeTable.addNativeField(2);
        edgeTable.addRelatedTable(3);
        edgeTable.addNativeField(4);
        edgeTable.makeArrays();
        String expected = """
                Table: 1\r
                {\r
                TableName: StringTest\r
                NativeFields: 2|4\r
                RelatedTables: 1|3\r
                RelatedFields: 0|0\r
                }\r
                """;
        assertEquals(expected, edgeTable.toString());
    }

    @Test
    public void testBadIndex(){
        int[] expectedRel = new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE};
        int[] expectedRelField = new int[]{Integer.MAX_VALUE, 0};
        int[] expectedNat = new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE};

        EdgeTable edgeTable = new EdgeTable("1|EdgeTest");
        edgeTable.addRelatedTable(Integer.MAX_VALUE);
        edgeTable.addNativeField(Integer.MIN_VALUE);
        edgeTable.addRelatedTable(Integer.MIN_VALUE);
        edgeTable.addNativeField(Integer.MAX_VALUE);
        edgeTable.makeArrays();
        edgeTable.setRelatedField(0, Integer.MAX_VALUE);

        //values should not move
        edgeTable.moveFieldUp(0);
        edgeTable.moveFieldDown(edgeTable.getNativeFieldsArray().length - 1);

        assertArrayEquals(expectedNat, edgeTable.getNativeFieldsArray());
        assertArrayEquals(expectedRel, edgeTable.getRelatedTablesArray());
        assertArrayEquals(expectedRelField, edgeTable.getRelatedFieldsArray());
    }

    @Test
    public void testInvalidIndex() {
        EdgeTable edgeTable = new EdgeTable("1|NoIndex");
        edgeTable.addRelatedTable(Integer.MAX_VALUE);
        edgeTable.addNativeField(Integer.MIN_VALUE);
        edgeTable.addRelatedTable(Integer.MIN_VALUE);
        edgeTable.addNativeField(Integer.MAX_VALUE);
        edgeTable.makeArrays();
        //setRelatedFields does not check if index is valid
        edgeTable.setRelatedField(5, Integer.MAX_VALUE);
    }

    @Test
    public void testNegIndex() {
        EdgeTable edgeTable = new EdgeTable("1|NoIndex");
        edgeTable.addRelatedTable(Integer.MAX_VALUE);
        edgeTable.addNativeField(Integer.MIN_VALUE);
        edgeTable.addRelatedTable(Integer.MIN_VALUE);
        edgeTable.addNativeField(Integer.MAX_VALUE);
        edgeTable.makeArrays();
        edgeTable.setRelatedField(-1, Integer.MAX_VALUE);
    }

    @Test
    public void testBigMove() {
        EdgeTable edgeTable = new EdgeTable("1|NoIndex");
        edgeTable.addRelatedTable(Integer.MAX_VALUE);
        edgeTable.addNativeField(Integer.MIN_VALUE);
        edgeTable.addRelatedTable(Integer.MIN_VALUE);
        edgeTable.addNativeField(Integer.MAX_VALUE);
        edgeTable.makeArrays();
        //program checks for edge moves but not out-of-bounds
        edgeTable.moveFieldDown(edgeTable.getNativeFieldsArray().length);
    }

}
