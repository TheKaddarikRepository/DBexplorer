package view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import applicationDB.MyException;
import data.DataResults;
import data.FileAction;
import data.connectionDB;
import data.connectionMySQL;
import data.databaseDrivers;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * this is the application controller.
 * 
 * @author cedric
 *
 */
public class View {
	private Parent root;
	private ConneDbController control;
	private connectionDB dataBase;
	private Stage primaryStage;

	/**
	 * It sets the graphical interface and its controller at the start of the
	 * application. And defines its behavior in the event of the close operation.
	 * 
	 * @param _primaryStage
	 */
	public View(Stage _primaryStage) {
		this.primaryStage = _primaryStage;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("ConneDB.fxml"));
			root = loader.load();
			control = loader.getController();
			primaryStage.setScene(new Scene(root));
			initialize();

			// To request a confirmation before closing.
			primaryStage.setOnCloseRequest(event -> {
				ConfirmCloseOperation myConfirmation = new ConfirmCloseOperation();
				myConfirmation.getResult().ifPresent(yesNo -> {
					if (yesNo.booleanValue()) {
						try {
							if (dataBase != null)
								dataBase.stop();
						} catch (SQLException e) {
							showError(new MyException(e.getMessage(), AlertType.WARNING));
						}
						primaryStage.close();
					} else {
						event.consume();
					}
					;
				});
			});

			primaryStage.show();
		} catch (Exception e) {
			showError(new MyException(e.getMessage(), AlertType.ERROR));
		}
	}

	/**
	 * This initialize the values for the connection. (particularly for my
	 * configuration, since I was lazy retyping those.)
	 */
	public void initialize() {
		control.setMyView(this);
		for (databaseDrivers db : databaseDrivers.values()) {
			control.getDataBaseCombo().getItems().add(db);
		}
		control.getDataBaseCombo().getSelectionModel().select(databaseDrivers.MySQL);
		control.getAddressText().setText("127.0.0.1");
		control.getPortText().setText("3306");
	}

	/**
	 * It initiate the connection to the database Engine and get the list of
	 * databases in it.
	 * 
	 * @param address
	 * @param port
	 * @throws MyException
	 */
	public void firstConnection(String address, String port) throws MyException {
		if (dataBase != null)
			try {
				dataBase.stop();
				control.getDataBaseList().getItems().clear();
				control.getTablesList().getItems().clear();
				control.getContentTable().getItems().clear();
				dataBase=null;
			} catch (SQLException e1) {
				showError(new MyException(e1.getMessage(), AlertType.WARNING));
			}

		switch (control.getDataBaseCombo().getValue()) {
		case MySQL:
			LoginMessage myLogin = new LoginMessage();
			myLogin.getResult().ifPresent(usernamePassword -> {
				try {
					dataBase = new connectionMySQL(address, port, usernamePassword.getKey(),
							usernamePassword.getValue());
					dataBase.extractDataBases();
					control.getDataBaseList().setItems(dataBase.getDataBases());
				} catch (MyException e) {
					showError(e);
				} catch (SQLException e) {
					showError(new MyException(e.getMessage(), AlertType.WARNING));
				}
			});
			break;
		case PostgreSQL:
			throw new MyException("This functionnality is not implemented yet!", AlertType.INFORMATION);
		case Oracle:
			throw new MyException("This functionnality is not implemented yet!", AlertType.INFORMATION);
		case Derby:
			throw new MyException("This functionnality is not implemented yet!", AlertType.INFORMATION);
		case MSSQL:
			throw new MyException("This functionnality is not implemented yet!", AlertType.INFORMATION);
		default:
		}
	}

	/**
	 * Fills the table List with all the tables of the selected database.
	 * 
	 * @param dbName
	 */
	public void tablesFill(String dbName) {
		try {
			dataBase.extractTables(dbName);
			control.getTablesList().setItems(dataBase.getTables());
		} catch (SQLException e) {
			showError(new MyException(e.getMessage(), AlertType.WARNING));
		}

	}

	/**
	 * This get every entry of a table to display it in the the TableView of the
	 * scene.
	 * 
	 * @param dbName
	 * @param tableName
	 */
	public void contentFill(String dbName, String tableName) {
		try {
			dataBase.extractContent(dbName + "." + tableName);
			intoTable();
		} catch (SQLException e) {
			showError(new MyException(e.getMessage(), AlertType.WARNING));
		}

	}

	/**
	 * Suppress every columns, then sets the columns according to the DataResult
	 * object. Finally it sets the data in the TableView.
	 * 
	 */
	private void intoTable() {
		control.getContentTable().getColumns().removeAll(control.getContentTable().getColumns());
		DataResults data = dataBase.getContent();

		for (int i = 0; i < data.getNumColumns(); i++) {
			TableColumn<List<Object>, Object> column = new TableColumn<>(data.getColumnName(i));
			int columnIndex = i;
			column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(columnIndex)));
			control.getContentTable().getColumns().add(column);
		}
		control.getContentTable().getItems().setAll(data.getData());
	}

	/**
	 * it dispatches the adhoc query typed by the user depending on its use. (mostly
	 * because of the return values).
	 * 
	 * @param database
	 * @param query
	 */
	public void adhocQuery(String query) {
		String upperQuery = query.toUpperCase();
		if (upperQuery.contains("SELECT")) {
			try {
				dataBase.executeSelect(query);
				intoTable();
			} catch (SQLException e) {
				showError(new MyException(e.getMessage(), AlertType.WARNING));
			}
		} else if (upperQuery.contains("INSERT") || upperQuery.contains("UPDATE") || upperQuery.contains("DELETE")
				|| upperQuery.contains("REPLACE")) {
			try {
				dataBase.executeUpdate(query);
				intoTable();
			} catch (SQLException e) {
				showError(new MyException(e.getMessage(), AlertType.WARNING));
			}

		} else if (upperQuery.contains("CREATE") || upperQuery.contains("ALTER") || upperQuery.contains("DROP")
				|| upperQuery.contains("RENAME") || upperQuery.contains("TRUNCATE")) {
			try {
				dataBase.executeAlter(query);
				intoTable();
			} catch (SQLException e) {
				showError(new MyException(e.getMessage(), AlertType.WARNING));
			}

		}
	}

	/**
	 * This enable to show every exceptions catched during the application run.
	 * 
	 * @param ex
	 */
	public void showError(MyException ex) {
		Alert alert = new Alert(ex.getAlert());
		alert.setTitle("Error Dialog");
		alert.setContentText(ex.getMessage());
		alert.showAndWait();
	}

	/**
	 * Open a file selection dialog to chose where to save the file.
	 * 
	 * @param query
	 */
	private File toFile(FileAction action) {
		FileChooser fileChooser = new FileChooser();
		File file = null;

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SQL files (*.amsql)", "*.amsql");
		fileChooser.getExtensionFilters().add(extFilter);
		switch (action) {
		case SAVE:
			file = fileChooser.showSaveDialog(primaryStage);
			break;
		case READ:
			file = fileChooser.showOpenDialog(primaryStage);
			break;
		}
		return file;
	}

	/**
	 * save the user adhoc query to an "*.amsql" file.
	 * 
	 * @param content
	 */
	public void saveFile(String content) {
		// Show file dialog
		File file = toFile(FileAction.SAVE);

		if (file != null) {
			try {
				FileWriter fileWriter = new FileWriter(file);
				fileWriter.write(content);
				fileWriter.close();
			} catch (IOException ex) {
				showError(new MyException(ex.getMessage(), AlertType.ERROR));
			}
		}
	}

	/**
	 * Read an adhoc query from an "*.amsql" file.
	 * 
	 * @return String
	 */
	public String readFile() {
		// Show file dialog
		File file = toFile(FileAction.READ);

		if (file != null) {
			try {
				BufferedReader lecteur = new BufferedReader(new FileReader(file));
				String line;
				StringBuilder query = new StringBuilder();

				while (lecteur.ready()) {
					line = lecteur.readLine();
					query.append(line);
				}
				lecteur.close();
				return query.toString();
			} catch (IOException e) {
				showError(new MyException(e.getMessage(), AlertType.ERROR));
			}
		}
		return new String("");
	}
	
	public void showDialogHelp() {
			HelpDialog myHelp = new HelpDialog();
	}

}
