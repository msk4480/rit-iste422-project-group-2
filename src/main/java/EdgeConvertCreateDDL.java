import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class EdgeConvertCreateDDL {

  private static Logger logger = LogManager.getLogger(EdgeConvertCreateDDL.class.getName());

  static String[] products = { "MySQL" };
  protected EdgeTable[] tables; // master copy of EdgeTable objects
  protected EdgeField[] fields; // master copy of EdgeField objects
  protected int[] numBoundTables;
  protected int maxBound;
  protected StringBuffer sb;
  // protected int selected;

  public EdgeConvertCreateDDL(EdgeTable[] tables, EdgeField[] fields) {

    logger.info("EdgeCongertCreateDDL constructor called w/ tables and fields");
    
    this.tables = tables;
    this.fields = fields;

    this.numBoundTables = new int[this.tables.length];
    this.maxBound = 0;

    for(int i = 0; i < this.tables.length; i++) {

      int numBound = 0;
      int[] relatedFields = this.tables[i].getRelatedFieldsArray();

      for(int field : relatedFields) {

        if(field != 0) numBound++;
      }

      numBoundTables[i] = numBound;
      
      if(numBound > maxBound) maxBound = numBound;
    }
    
  } // EdgeConvertCreateDDL(EdgeTable[], EdgeField[])

  public EdgeConvertCreateDDL() { // default constructor with empty arg list for to allow output dir to be set
                                  // before there are table and field objects
    logger.info("EdgeCongertCreateDDL constructor called w/ 0 args");

  } // EdgeConvertCreateDDL()

  protected EdgeTable getTable(int numFigure) {
    for (int tIndex = 0; tIndex < tables.length; tIndex++) {
      if (numFigure == tables[tIndex].getNumFigure()) {
        return tables[tIndex];
      }
    }
    return null;
  }

  protected EdgeField getField(int numFigure) {
    for (int fIndex = 0; fIndex < fields.length; fIndex++) {
      if (numFigure == fields[fIndex].getNumFigure()) {
        return fields[fIndex];
      }
    }
    return null;
  }

  public abstract void setDatabaseName(String name);

  public abstract String getDatabaseName();

  public abstract String getProductName();

  // public abstract String getSQLString();
  public abstract String getDatabaseString();

  // public abstract void createDDL();
  public abstract void generateDDL();

}// EdgeConvertCreateDDL
