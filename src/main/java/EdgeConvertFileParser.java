import java.io.*;
import java.util.*;
import javax.swing.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Abstract class for file parsing
public abstract class EdgeConvertFileParser {
		protected static final Logger fileLogger = LogManager.getLogger(EdgeConvertFileParser.class);
		protected static final Logger consoleLogger = LogManager.getLogger(EdgeConvertFileParser.class);

		protected File parseFile;
		private boolean isEntity, isAttribute, isUnderlined = false;
		protected BufferedReader br;
		protected String currentLine;
		protected ArrayList alTables;
		protected ArrayList alFields;
		protected ArrayList alConnectors;
		protected EdgeTable[] tables;
		protected EdgeField[] fields;
		protected EdgeField tempField;
		protected EdgeConnector[] connectors;
		protected int numFigure;
		protected int numConnector;
		protected int numFields;
		protected int numTables;
		protected int endPoint1;
		protected int endPoint2;
		protected int numLine;
		public static final String EDGE_ID = "EDGE Diagram File"; // first line of .edg files should be this
		public static final String SAVE_ID = "EdgeConvert Save File"; // first line of save files should be this
		public static final String DELIM = "|";
	    protected EdgeConvertFileParser(File constructorFile) {
				numFigure = 0;
				numConnector = 0;
				alTables = new ArrayList<>();
				alFields = new ArrayList<>();
				alConnectors = new ArrayList<>();
				parseFile = constructorFile;
				numLine = 0;
				this.openFile(parseFile);
				// Logging
				fileLogger.debug("EdgeConvertFileParser constructor called.");
				consoleLogger.debug("EdgeConvertFileParser constructor called.");
		}

		// Abstract method to be implemented by subclasses
		protected abstract void parseFile() throws IOException;



		protected void makeArrays() {
				if (alTables != null) {
						tables = (EdgeTable[]) alTables.toArray(new EdgeTable[alTables.size()]);
				}
				if (alFields != null) {
						fields = (EdgeField[]) alFields.toArray(new EdgeField[alFields.size()]);
				}
				if (alConnectors != null) {
						connectors = (EdgeConnector[]) alConnectors.toArray(new EdgeConnector[alConnectors.size()]);
				}
		}



		public EdgeTable[] getEdgeTables() {
				fileLogger.info("Retrieved EdgeTables.");
				consoleLogger.info("Retrieved EdgeTables.");
				return tables;
		}

		public EdgeField[] getEdgeFields() {
				fileLogger.info("Retrieved EdgeFields.");
				consoleLogger.info("Retrieved EdgeFields.");
				return fields;
		}

		public void openFile(File inputFile) {
    			FileReader fr;
				try {
						fr = new FileReader(inputFile);
						br = new BufferedReader(fr);
						// test for what kind of file we have
						currentLine = br.readLine().trim();
						numLine++;
						if (currentLine.startsWith(EDGE_ID)) {
								this.parseFile();
								br.close();
								this.makeArrays();
                this.resolveConnectors();
						} else {
								if (currentLine.startsWith(SAVE_ID)) {
										this.parseFile();
										br.close();
										this.makeArrays();
										this.resolveConnectors();
								} else {
										JOptionPane.showMessageDialog(null, "Unrecognized file format");
								}
						}
						fileLogger.debug("File opened successfully: " + inputFile.getName());
						consoleLogger.debug("File opened successfully: " + inputFile.getName());
				} catch (FileNotFoundException fnfe) {
						fileLogger.error("File not found: " + inputFile.getName(), fnfe);
						consoleLogger.error("File not found: " + inputFile.getName(), fnfe);
						System.out.println("Cannot find \"" + inputFile.getName() + "\".");
						System.exit(0);
				} catch (IOException ioe) {
						fileLogger.error("IO error: " + ioe.getMessage(), ioe);
						consoleLogger.error("IO error: " + ioe.getMessage(), ioe);
						System.out.println(ioe);
						System.exit(0);
				}
		}

		
		private void resolveConnectors() { // Identify nature of Connector endpoints
			int endPoint1, endPoint2;
			int fieldIndex = 0, table1Index = 0, table2Index = 0;
      System.out.println("Fields DEBUG: " + Arrays.toString(fields));
			for (int cIndex = 0; cIndex < connectors.length; cIndex++) {
				endPoint1 = connectors[cIndex].getEndPoint1();
				endPoint2 = connectors[cIndex].getEndPoint2();
				fieldIndex = -1;
				for (int fIndex = 0; fIndex < fields.length; fIndex++) { // search fields array for endpoints
          // if(true) throw new RuntimeException("line 124");
					if (endPoint1 == fields[fIndex].getNumFigure()) { // found endPoint1 in fields array
            // if(true) throw new RuntimeException("line 126");
						connectors[cIndex].setIsEP1Field(true); // set appropriate flag
						fieldIndex = fIndex; // identify which element of the fields array that endPoint1 was found in
					}
					if (endPoint2 == fields[fIndex].getNumFigure()) { // found endPoint2 in fields array
						connectors[cIndex].setIsEP2Field(true); // set appropriate flag
						fieldIndex = fIndex; // identify which element of the fields array that endPoint2 was found in
					}
				}
				for (int tIndex = 0; tIndex < tables.length; tIndex++) { // search tables array for endpoints
					if (endPoint1 == tables[tIndex].getNumFigure()) { // found endPoint1 in tables array
						connectors[cIndex].setIsEP1Table(true); // set appropriate flag
						table1Index = tIndex; // identify which element of the tables array that endPoint1 was found in
					}
					if (endPoint2 == tables[tIndex].getNumFigure()) { // found endPoint1 in tables array
						connectors[cIndex].setIsEP2Table(true); // set appropriate flag
						table2Index = tIndex; // identify which element of the tables array that endPoint2 was found in
					}
				}

				if (connectors[cIndex].getIsEP1Field() && connectors[cIndex].getIsEP2Field()) { // both endpoints are fields,
																																												// implies lack of normalization
					JOptionPane.showMessageDialog(null, "The Edge Diagrammer file\n" + parseFile
							+ "\ncontains composite attributes. Please resolve them and try again.");
					EdgeConvertGUI.setReadSuccess(false); // this tells GUI not to populate JList components
					break; // stop processing list of Connectors
				}

				if (connectors[cIndex].getIsEP1Table() && connectors[cIndex].getIsEP2Table()) { // both endpoints are tables
					if ((connectors[cIndex].getEndStyle1().indexOf("many") >= 0) &&
							(connectors[cIndex].getEndStyle2().indexOf("many") >= 0)) { // the connector represents a many-many
																																					// relationship, implies lack of normalization
						JOptionPane.showMessageDialog(null,
								"There is a many-many relationship between tables\n\"" + tables[table1Index].getName() + "\" and \""
										+ tables[table2Index].getName() + "\"" + "\nPlease resolve this and try again.");
						EdgeConvertGUI.setReadSuccess(false); // this tells GUI not to populate JList components
						break; // stop processing list of Connectors
					} else { // add Figure number to each table's list of related tables
						tables[table1Index].addRelatedTable(tables[table2Index].getNumFigure());
						tables[table2Index].addRelatedTable(tables[table1Index].getNumFigure());
						continue; // next Connector
					}
				}

				if (fieldIndex >= 0 && fields[fieldIndex].getTableID() == 0) { // field has not been assigned to a table yet
					if (connectors[cIndex].getIsEP1Table()) { // endpoint1 is the table
						tables[table1Index].addNativeField(fields[fieldIndex].getNumFigure()); // add to the appropriate table's field
																																										// list
						fields[fieldIndex].setTableID(tables[table1Index].getNumFigure()); // tell the field what table it belongs to
					} else { // endpoint2 is the table
						tables[table2Index].addNativeField(fields[fieldIndex].getNumFigure()); // add to the appropriate table's field
																																										// list
						fields[fieldIndex].setTableID(tables[table2Index].getNumFigure()); // tell the field what table it belongs to
					}
				} else if (fieldIndex >= 0) { // field has already been assigned to a table
					JOptionPane.showMessageDialog(null, "The attribute " + fields[fieldIndex].getName()
							+ " is connected to multiple tables.\nPlease resolve this and try again.");
					EdgeConvertGUI.setReadSuccess(false); // this tells GUI not to populate JList components
					break; // stop processing list of Connectors
				}
			} // connectors for() loop
			fileLogger.debug("Connector resolution completed.");
			consoleLogger.debug("Connector resolution completed.");
		} // resolveConnectors()
}


