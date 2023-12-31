import java.util.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EdgeField {
   private int numFigure, tableID, tableBound, fieldBound, dataType, varcharValue;
   private String name, defaultValue;
   private boolean disallowNull, isPrimaryKey;
   private static String[] strDataType = {"Varchar", "Boolean", "Integer", "Double"};
   public static final int VARCHAR_DEFAULT_LENGTH = 1;
	 private static final Logger logger = LogManager.getLogger(EdgeField.class);
   public EdgeField(String inputString) {
      StringTokenizer st = new StringTokenizer(inputString, EdgeConvertFileParser.DELIM);
      numFigure = Integer.parseInt(st.nextToken());
      name = st.nextToken();
      if (st.hasMoreTokens()){
         tableID = Integer.parseInt(st.nextToken());
      }else{
         tableID = 0;
      }
      if (st.hasMoreTokens()){
         tableBound = Integer.parseInt(st.nextToken());
      }else{
         tableBound = 0;
      }
      fieldBound = 0;
      if (st.hasMoreTokens()){
         disallowNull = Boolean.parseBoolean(st.nextToken());
      }else{
         disallowNull = false;
      }
      if (st.hasMoreTokens()){
         isPrimaryKey = Boolean.parseBoolean(st.nextToken());
      }else{
         isPrimaryKey = false;
      }
      if (st.hasMoreTokens()){
         defaultValue = st.nextToken();
      }else{
         defaultValue = "";
      }
      varcharValue = VARCHAR_DEFAULT_LENGTH;
      dataType = 0;

		  logger.debug("EdgeField constructor called with input: " + inputString);
   }

   
   public int getNumFigure() {
      return numFigure;
   }
   
   public String getName() {
      return name;
   }
   

   public int getTableID() {
			logger.debug("TableID set to: " + tableID);
		 return tableID;
		}
   
   public void setTableID(int value) {
      tableID = value;
	    }		 
   
   
   public int getTableBound() {
      return tableBound;
   }
   
   public void setTableBound(int value) {
      tableBound = value;
   }

   public int getFieldBound() {
      return fieldBound;
   }
   
   public void setFieldBound(int value) {
      fieldBound = value;
   }

   public boolean getDisallowNull() {
      return disallowNull;
   }
   
   public void setDisallowNull(boolean value) {
      disallowNull = value;
   }
   
   public boolean getIsPrimaryKey() {
      return isPrimaryKey;
   }
   
   public void setIsPrimaryKey(boolean value) {
      isPrimaryKey = value;
   }
   
   public String getDefaultValue() {
      return defaultValue;
   }
   
   public void setDefaultValue(String value) {
      defaultValue = value;
   }
   
   public int getVarcharValue() {
      return varcharValue;
   }
   
   public void setVarcharValue(int value) {
      if (value > 0) {
         varcharValue = value;
      }
   }
   public int getDataType() {
      return dataType;
   }
   
   public void setDataType(int value) {
      if (value >= 0 && value < strDataType.length) {
         dataType = value;
      }
   }
   
   public static String[] getStrDataType() {
      return strDataType;
   }
   
   public String toString() {
      return numFigure + EdgeConvertFileParser.DELIM +
      name + EdgeConvertFileParser.DELIM +
      tableID + EdgeConvertFileParser.DELIM +
      tableBound + EdgeConvertFileParser.DELIM +
      fieldBound + EdgeConvertFileParser.DELIM +
      dataType + EdgeConvertFileParser.DELIM +
      varcharValue + EdgeConvertFileParser.DELIM +
      isPrimaryKey + EdgeConvertFileParser.DELIM +
      disallowNull + EdgeConvertFileParser.DELIM +
      defaultValue;
   }
}
