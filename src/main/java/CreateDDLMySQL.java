import java.awt.*;
import java.awt.event.*;
import javax.swing.*;   
import javax.swing.event.*;
import java.io.*;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateDDLMySQL extends EdgeConvertCreateDDL {

   private static Logger logger = LogManager.getLogger(CreateDDLMySQL.class.getName());

   protected String databaseName;
   //this array is for determining how MySQL refers to datatypes
   protected String[] strDataType = {"VARCHAR", "BOOL", "INT", "DOUBLE"};
   protected StringBuffer sb;

   public CreateDDLMySQL(EdgeTable[] inputTables, EdgeField[] inputFields) {
     
      super(inputTables, inputFields);
      sb = new StringBuffer();
     
      logger.info("****Constructor Called with inputTables[] and EdgeField[]****");
      logger.debug("EdgeTables: " + Arrays.toString(inputTables) + "\n\nEdgeFields: " + Arrays.toString(inputFields));
     
   } //CreateDDLMySQL(EdgeTable[], EdgeField[])
   
   public CreateDDLMySQL() { //default constructor with empty arg list for to allow output dir to be set before there are table and field objects

      logger.info("CreateDDLMySQL constructor called w/ 0 args");
   }
   
   public void createDDL() {
     
      logger.info("Creating DDL...");
     
      EdgeConvertGUI.setReadSuccess(true);
     
      if(databaseName == null || databaseName.equals("")) databaseName = "MySQLDB";
     
      sb.append("CREATE DATABASE " + databaseName + ";\r\n");
      sb.append("USE " + databaseName + ";\r\n");

     
      for (int boundCount = 0; boundCount <= maxBound; boundCount++) {//process tables in order from least dependent (least number of bound tables) to most dependent
        
         for (int tableCount = 0; tableCount < numBoundTables.length; tableCount++) { //step through list of tables
           
            if (numBoundTables[tableCount] == boundCount) { //
              
               sb.append("CREATE TABLE " + tables[tableCount].getName() + " (\r\n");
              
               int[] nativeFields = tables[tableCount].getNativeFieldsArray();
               int[] relatedFields = tables[tableCount].getRelatedFieldsArray();
               boolean[] primaryKey = new boolean[nativeFields.length];
              
               int numPrimaryKey = 0;
               int numForeignKey = 0;
              
               for (int nativeFieldCount = 0; nativeFieldCount < nativeFields.length; nativeFieldCount++) { //print out the fields
                 
                  EdgeField currentField = getField(nativeFields[nativeFieldCount]);
                 
                  sb.append("\t" + currentField.getName() + " " + strDataType[currentField.getDataType()]);
                 
                  if (currentField.getDataType() == 0) { //varchar
                    
                     sb.append("(" + currentField.getVarcharValue() + ")"); //append varchar length in () if data type is varchar
                  }
                 
                  if (currentField.getDisallowNull()) {
                    
                     sb.append(" NOT NULL");
                  }
                 
                  if (!currentField.getDefaultValue().equals("")) {

                    sb.append(" DEFAULT ");

                     switch(currentField.getDataType()) {
                       case 0: sb.append("'" + currentField.getDefaultValue() + "'"); break;
                       case 1: sb.append(convertStrBooleanToInt(currentField.getDefaultValue())); break;
                       case 2:
                       case 3: sb.append(currentField.getDefaultValue());
                     }
                  }
                 
                  if (currentField.getIsPrimaryKey()) {
                    
                     primaryKey[nativeFieldCount] = true;
                     numPrimaryKey++;
                  } else {
                    
                     primaryKey[nativeFieldCount] = false;
                  }
                 
                  if (currentField.getFieldBound() != 0) {
                    
                     numForeignKey++;
                  }

                  // only add if not the last field in the table
                  if(nativeFieldCount < nativeFields.length - 1 || numPrimaryKey > 0 || numForeignKey > 0) {
                    sb.append(",");
                  }
                 
                  sb.append("\r\n"); //end of field
               }
              
               if (numPrimaryKey > 0) { //table has primary key(s)
                 
                  sb.append("CONSTRAINT " + tables[tableCount].getName() + "_PK PRIMARY KEY (");
                  // TODO: Check if this is affected with the ','
                  for (int i = 0; i < primaryKey.length; i++) {
                    
                     if (primaryKey[i]) {
                       
                        sb.append(getField(nativeFields[i]).getName());
                        numPrimaryKey--;
                       
                        if (numPrimaryKey > 0) {
                          
                           sb.append(", ");
                        }
                     }
                  }
                 
                  sb.append(")");
                 
                  if (numForeignKey > 0) {
                    // TODO: Check if this is affected with the ','
                     sb.append(",");
                  }
                 
                  sb.append("\r\n");
               }
               if (numForeignKey > 0) { //table has foreign keys
                 
                  int currentFK = 1;
                 
                  for (int i = 0; i < relatedFields.length; i++) {
                    
                     if (relatedFields[i] != 0) {
                       
                        sb.append("CONSTRAINT " + tables[tableCount].getName() + "_FK" + currentFK +
                                  " FOREIGN KEY(" + getField(nativeFields[i]).getName() + ") REFERENCES " +
                                  getTable(getField(nativeFields[i]).getTableBound()).getName() + "(" + getField(relatedFields[i]).getName() + ")");
                       
                        if (currentFK < numForeignKey) {
                          // TODO: Check if this is affected with the ','
                           sb.append(",\r\n");
                        }
                       
                        currentFK++;
                     }
                  }
                 
                  sb.append("\r\n");
               }
              
               sb.append(");\r\n\r\n"); //end of table
            }
           
            logger.debug("Created table: {} ", tables[tableCount].getName());
         }
      }
     
     logger.info("****Creating DDL COMPLETED****");
     logger.debug("createDDL()\n\n" + sb);
   }

   protected int convertStrBooleanToInt(String input) { //MySQL uses '1' and '0' for boolean types
      if (input.equals("true")) {
         return 1;
      } else {
         return 0;
      }
   }

  public void setDatabaseName(String name) {
    this.databaseName = name;
  }
   
   public String getDatabaseName() {
      return databaseName;
   }
   
   public String getProductName() {
      return "MySQL";
   }
  
  public String getDatabaseString() {
    createDDL();
    return sb.toString();
  }
   
}//EdgeConvertCreateDDL
