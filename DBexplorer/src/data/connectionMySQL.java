package data;

import static data.interfaceMySQL.db;
import static data.interfaceMySQL.driver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.MyException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;
import view.View;

public class connectionMySQL extends connectionDB implements interfaceMySQL {
	private ObservableList<String> dataBases = FXCollections.observableArrayList();
	private ObservableList<String> tables = FXCollections.observableArrayList();
	private DataResults content;
	private View myView;

	public connectionMySQL(String address, String port, String login, String password, View myView) {
		super.setDb(db);
		super.setDriver(driver);
		try {
			super.connect(address, port, login, password);
		} catch (MyException e) {
			myView.showError(e);
		}
		this.myView = myView;
	}

	public void extractDataBases() {
		Statement stmt;
		ResultSet rs;
		try {
			stmt = getConnection().createStatement();
			rs = stmt.executeQuery("SHOW DATABASES");
			setDataBases(rs);
			stmt.close();
		} catch (SQLException e) {
			myView.showError(new MyException(e.getMessage(), AlertType.WARNING));
		}
	}

	public void extractTables(String dataBase) {
		Statement stmt;
		ResultSet rs;
		try {
			stmt = getConnection().createStatement();
			rs = stmt.executeQuery("SHOW TABLES FROM " + dataBase);
			setTables(rs);
			stmt.close();
		} catch (SQLException e) {
			myView.showError(new MyException(e.getMessage(), AlertType.WARNING));
		}
	}

	@Override
	public void extractContent(String databaseTable) {
		Statement stmt;
		ResultSet rs;
		try {
			stmt = getConnection().createStatement();
			rs = stmt.executeQuery("SELECT * FROM " + databaseTable);
			setContent(rs);
			stmt.close();
		} catch (SQLException e) {
			myView.showError(new MyException(e.getMessage(), AlertType.WARNING));
			content = new DataResults();
		}
	}

	public void executeSelect(String dataBase, String query) {
		Statement stmt;
		ResultSet rs;
		try {
			stmt = getConnection().createStatement();
			rs = stmt.executeQuery(query);
			setContent(rs);
			stmt.close();
		} catch (SQLException e) {
			myView.showError(new MyException(e.getMessage(), AlertType.WARNING));
			content = new DataResults();
		}
	}

	public void executeUpdate(String dataBase, String query) {
		Statement stmt;
		int rs;
		try {
			stmt = getConnection().createStatement();
			rs = stmt.executeUpdate(query);
			setUpdateContent(rs);
			stmt.close();
		} catch (SQLException e) {
			myView.showError(new MyException(e.getMessage(), AlertType.WARNING));
			content = new DataResults();
		}
	}

	public ObservableList<String> getDataBases() {
		return dataBases;
	}

	public void setDataBases(ResultSet dataBasesRs) {
		this.dataBases.removeAll(dataBases.sorted());
		try {
			while (dataBasesRs.next()) {
				this.dataBases.add(dataBasesRs.getString(1));
			}
		} catch (SQLException e) {
			myView.showError(new MyException(e.getMessage(), AlertType.WARNING));
		}
	}

	public ObservableList<String> getTables() {
		return tables;
	}

	public void setTables(ResultSet tablesRs) {
		this.tables.removeAll(tables.sorted());
		try {
			while (tablesRs.next()) {
				this.tables.add(tablesRs.getString(1));
			}
		} catch (SQLException e) {
			myView.showError(new MyException(e.getMessage(), AlertType.WARNING));
		}
	}

	public DataResults getContent() {
		return content;
	}

	public void setContent(ResultSet contentRs) {
		List<List<Object>> data = new ArrayList<>();
		List<String> columnNames = new ArrayList<>();

		try {
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

		} catch (SQLException e) {
			myView.showError(new MyException(e.getMessage(), AlertType.WARNING));
			content = new DataResults();
		}
	}

	public void setUpdateContent(int contentRs) {
		List<List<Object>> data = new ArrayList<>();
		List<String> columnNames = new ArrayList<>();
		List<Object> row = new ArrayList<>();

		columnNames.add("Updated Lines");
		row.add(new Integer(contentRs));
		data.add(row);
		content = new DataResults(columnNames, data);
	}

	public void stop() throws SQLException {
		super.stop();
	}
}
