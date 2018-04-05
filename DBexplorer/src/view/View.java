package view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import application.MyException;
import data.DataResults;
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

public class View {
	private Parent root;
	private ConneDbController control;
	private connectionDB dataBase;
	private Stage primaryStage;

	public View(Stage _primaryStage) {
		this.primaryStage = _primaryStage;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("ConneDB.FXML"));
			root = loader.load();
			control = loader.getController();
			primaryStage.setScene(new Scene(root));
			initialize();

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
					}
					;
				});
			});

			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initialize() {
		control.setMyView(this);
		for (databaseDrivers db : databaseDrivers.values()) {
			control.getDataBaseCombo().getItems().add(db);
		}
		control.getDataBaseCombo().getSelectionModel().select(databaseDrivers.MySQL);
		control.getAddressText().setText("127.0.0.1");
		control.getPortText().setText("3306");
	}

	public void firstConnection(String address, String port) throws MyException {
		switch (control.getDataBaseCombo().getValue()) {
		case MySQL:
			LoginMessage myLogin = new LoginMessage();
			// myLogin.getResult().ifPresent(new UseLoginInfo());
			myLogin.getResult().ifPresent(usernamePassword -> {
				dataBase = new connectionMySQL(address, port, usernamePassword.getKey(), usernamePassword.getValue(),
						this);
			});
			// dataBase = new connectionMySQL(address, port, login, password, this);
			dataBase.extractDataBases();
			control.getDataBaseList().setItems(dataBase.getDataBases());
			break;
		case PostgreSQL:
			throw new MyException("This functionnality is not implemented yet!", AlertType.INFORMATION);
		case OracleDatabase:
			throw new MyException("This functionnality is not implemented yet!", AlertType.INFORMATION);
		case Db2:
			throw new MyException("This functionnality is not implemented yet!", AlertType.INFORMATION);
		default:
		}
	}

	public void tablesFill(String dbName) {
		dataBase.extractTables(dbName);
		control.getTablesList().setItems(dataBase.getTables());
	}

	public void contentFill(String dbName, String tableName) {
		dataBase.extractContent(dbName + "." + tableName);
		intoTable();
	}

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

	public void adhocQuery(String database, String query) {
		String upperQuery = query.toUpperCase();
		if (upperQuery.contains("SELECT")) {
			dataBase.executeSelect(database, query);
			intoTable();
		} else if (upperQuery.contains("INSERT") || upperQuery.contains("UPDATE") || upperQuery.contains("DELETE")) {
			dataBase.executeUpdate(database, query);
			intoTable();
		}
	}

	public void showError(MyException ex) {
		Alert alert = new Alert(ex.getAlert());
		alert.setTitle("Error Dialog");
		alert.setContentText(ex.getMessage());
		alert.showAndWait();
	}

	public void toFile(String query) {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SQL files (*.amsql)", "*.amsql");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		File file = fileChooser.showSaveDialog(primaryStage);

		if (file != null) {
			SaveFile(query, file);
		}
	}

	private void SaveFile(String content, File file) {
		try {
			FileWriter fileWriter = null;
			fileWriter = new FileWriter(file);
			fileWriter.write(content);
			fileWriter.close();
		} catch (IOException ex) {
			showError(new MyException(ex.getMessage(), AlertType.ERROR));
		}

	}

	public String fromFile() {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SQL files (*.amsql)", "*.amsql");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show save file dialog
		File file = fileChooser.showOpenDialog(primaryStage);

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

	// class UseLoginInfo implements Consumer<Pair<String, String>> {
	//
	// @Override
	// public void accept(Pair<String, String> usernamePassword) {
	// dataBase = new connectionMySQL(control.getAddressText().getText(),
	// control.getPortText().getText(), usernamePassword.getKey(),
	// usernamePassword.getValue(), this);
	// }
	// }

}
