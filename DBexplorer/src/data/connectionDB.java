package data;

import java.sql.DriverManager;
import java.sql.SQLException;

import application.MyException;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;

public abstract class connectionDB {
	private static java.sql.Connection connection;
	private static String driver;
	private static String db;

	public connectionDB() {

	}

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

	public abstract void extractDataBases();

	public abstract void extractTables(String dataBase);

	public abstract void extractContent(String databaseTable);

	public abstract ObservableList<String> getDataBases();

	public abstract ObservableList<String> getTables();

	public abstract DataResults getContent();

	public abstract void executeSelect(String dataBase, String query);

	public abstract void executeUpdate(String dataBase, String query);

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
}
