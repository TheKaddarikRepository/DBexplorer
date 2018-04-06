package data;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import applicationDB.MyException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;

/**
 * Singleton class to manage connection and query to the databases. It must be
 * extended for each database type. For each database type the driver and base
 * url is passed through an interface.
 * 
 * @author Cedric Ferrand
 *
 */
public abstract class connectionDB {
	private static java.sql.Connection connection;
	private static String driver;
	private static String db;
	private ObservableList<String> dataBases;
	private ObservableList<String> tables;
	protected DataResults content;

	/**
	 * Creation of a single connection object to the database.
	 * 
	 * @param address
	 * @param port
	 * @param login
	 * @param password
	 * @throws MyException
	 */
	@SuppressWarnings("deprecation")
	public static void connect(String address, String port, String login, String password) throws MyException {
		String url = db + address + ":" + port + "/" + "?verifyServerCertificate=false" + "&useSSL=false"
				+ "&requireSSL=false";
		if (connection == null) {
			try {
				Class.forName(driver).newInstance();
				connection = DriverManager.getConnection(url, login, password);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				throw new MyException(e.getMessage(), AlertType.ERROR);
			} catch (SQLException e) {
				throw new MyException(e.getMessage(), AlertType.WARNING);
			}
		}

	}

	/**
	 * Methode to get the static connection to the database.
	 * 
	 * @return
	 */
	public static java.sql.Connection getConnection() {
		return connection;
	}

	/**
	 * to execute an adhoc data definition query typed by the user.
	 * 
	 * @param query
	 * @throws SQLException
	 */
	public void executeAlter(String query) throws SQLException {
		Statement stmt;

		stmt = getConnection().createStatement();
		if (stmt.execute(query))
			setContent(stmt.getResultSet());
		stmt.close();
	}

	/**
	 * to use the driver path from the interface.
	 * 
	 * @param _driver
	 */
	public static void setDriver(String _driver) {
		connectionDB.driver = _driver;
	}

	/**
	 * to use the database url root from the interface.
	 * 
	 * @param _db
	 */
	public static void setDb(String _db) {
		connectionDB.db = _db;
	}

	/**
	 * to close the connection before exiting the application or changing Engine.
	 * 
	 * @throws SQLException
	 */
	public void stop() throws SQLException {
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}

	/**
	 * SQL request to get every databases in the DBMS.
	 * 
	 * @throws SQLException
	 */
	public void extractDataBases() throws SQLException {
		Statement stmt;
		ResultSet rs;

		stmt = getConnection().createStatement();
		rs = stmt.executeQuery("SHOW DATABASES");
		setDataBases(rs);
		stmt.close();
	}

	/**
	 * SQL request to get every tables of a database in the DBMS.
	 * 
	 * @throws SQLException
	 */
	public void extractTables(String dataBase) throws SQLException {
		Statement stmt;
		ResultSet rs;

		stmt = getConnection().createStatement();
		rs = stmt.executeQuery("SHOW TABLES FROM " + dataBase);
		setTables(rs);
		stmt.close();
	}

	/**
	 * SQL request to get every tuples in a table of a database in the DBMS.
	 * 
	 * @throws SQLException
	 */
	public void extractContent(String databaseTable) throws SQLException {
		Statement stmt;
		ResultSet rs;

		stmt = getConnection().createStatement();
		rs = stmt.executeQuery("SELECT * FROM " + databaseTable);
		// put the results in a DataResults object.
		setContent(rs);
		stmt.close();
	}

	/**
	 * to execute an ahdoc selection query typed by the user.
	 * 
	 * @param query
	 * @throws SQLException
	 */
	public void executeSelect(String query) throws SQLException {
		Statement stmt;
		ResultSet rs;

		stmt = getConnection().createStatement();
		rs = stmt.executeQuery(query);
		setContent(rs);
		stmt.close();
	}

	/**
	 * to execute an adhoc data modification query typed by the user.
	 * 
	 * @param query
	 * @throws SQLException
	 */
	public void executeUpdate(String query) throws SQLException {
		Statement stmt;
		int rs;

		stmt = getConnection().createStatement();
		rs = stmt.executeUpdate(query);
		setUpdateContent(rs);
		stmt.close();
	}

	/**
	 * the list of database names.
	 * 
	 * @return
	 */
	public ObservableList<String> getDataBases() {
		return dataBases;
	}

	/**
	 * conversion of the resultset containing the list of databases names to an
	 * observableList.
	 * 
	 * @param dataBasesRs
	 * @throws SQLException
	 */
	protected void setDataBases(ResultSet dataBasesRs) throws SQLException {
		dataBases = FXCollections.observableArrayList();
		this.dataBases.removeAll(dataBases.sorted());
		while (dataBasesRs.next()) {
			this.dataBases.add(dataBasesRs.getString(1));
		}
	}

	/**
	 * list of tables names.
	 * 
	 * @return
	 */
	public ObservableList<String> getTables() {
		return tables;
	}

	/**
	 * conversion of the resultset containing the list of tables names to an
	 * observableList.
	 * 
	 * @param tablesRs
	 * @throws SQLException
	 */
	protected void setTables(ResultSet tablesRs) throws SQLException {
		tables = FXCollections.observableArrayList();
		this.tables.removeAll(tables.sorted());
		while (tablesRs.next()) {
			this.tables.add(tablesRs.getString(1));
		}
	}

	/**
	 * data from an SQL query.
	 * 
	 * @return
	 */
	public DataResults getContent() {
		return content;
	}

	/**
	 * wrap a ResultSet into a DataResult. (Metadata names to Lis<String> and data
	 * objects to List<object>)
	 * 
	 * @param contentRs
	 * @throws SQLException
	 */
	protected void setContent(ResultSet contentRs) throws SQLException {
		List<List<Object>> data = new ArrayList<>();
		List<String> columnNames = new ArrayList<>();

		int columnCount = contentRs.getMetaData().getColumnCount();

		for (int i = 1; i <= columnCount; i++) {
			columnNames.add(contentRs.getMetaData().getColumnName(i));
		}
		while (contentRs.next()) {
			List<Object> row = new ArrayList<>();
			for (int i = 1; i <= columnCount; i++) {
				row.add(contentRs.getObject(i));
			}
			data.add(row);
		}
		content = new DataResults(columnNames, data);
	}

	/**
	 * Create a DataResult to show the number of elements updated by the query.
	 * 
	 * @param contentRs
	 */
	@SuppressWarnings("deprecation")
	private void setUpdateContent(int contentRs) {
		List<List<Object>> data = new ArrayList<>();
		List<String> columnNames = new ArrayList<>();
		List<Object> row = new ArrayList<>();

		columnNames.add("Updated Lines");
		row.add(new Integer(contentRs));
		data.add(row);
		content = new DataResults(columnNames, data);
	}
}
