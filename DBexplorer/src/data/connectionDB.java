package data;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.MyException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;

/**
 * Singleton class to manage connection and query to the databases.
 * It must be extended for each database type.
 * For each database type the driver and base url is passed through an interface.
 * @author Cedric Ferrand
 *
 */
public abstract class connectionDB {
	private static java.sql.Connection connection;
	private static String driver;
	private static String db;
	private ObservableList<String> dataBases = FXCollections.observableArrayList();
	private ObservableList<String> tables = FXCollections.observableArrayList();
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
	public static void connect(String address, String port, String login, String password) throws MyException {
		String url = db + address + ":" + port + "/" + "?verifyServerCertificate=false" + "&useSSL=false"
				+ "&requireSSL=false";
		if (connection == null) {
			try {
				Class.forName(driver).newInstance();
				connection = DriverManager.getConnection(url, login, password);
				System.out.println("Connected");
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				throw new MyException(e.getMessage(), AlertType.ERROR);
			} catch (SQLException e) {
				throw new MyException(e.getMessage(), AlertType.WARNING);
			}
		}

	}

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

	public static void setDriver(String _driver) {
		connectionDB.driver = _driver;
	}

	public static void setDb(String _db) {
		connectionDB.db = _db;
	}

	public void stop() throws SQLException {
		if (connection != null)
			connection.close();
	}

	/**
	 * SQL request to get every databases in the DBMS.
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

	public ObservableList<String> getDataBases() {
		return dataBases;
	}

	protected void setDataBases(ResultSet dataBasesRs) throws SQLException {
		this.dataBases.removeAll(dataBases.sorted());
		while (dataBasesRs.next()) {
			this.dataBases.add(dataBasesRs.getString(1));
		}
	}

	public ObservableList<String> getTables() {
		return tables;
	}

	protected void setTables(ResultSet tablesRs) throws SQLException {
		this.tables.removeAll(tables.sorted());
		while (tablesRs.next()) {
			this.tables.add(tablesRs.getString(1));
		}
	}

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
	 * @param contentRs
	 */
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
