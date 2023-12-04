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
						} else {
								if (currentLine.startsWith(SAVE_ID)) {
										this.parseFile();
										br.close();
										this.makeArrays();
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
}


