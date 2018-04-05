package view;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import applicationDB.MyException;
import data.databaseDrivers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ConneDbController {
	private final String IP = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
	private final String PORT = "^[0-9]{2,4}$";
	private View myView;

	public ConneDbController() {

	}

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private MenuItem help;

	@FXML
	private TextField portText;

	@FXML
	private Button executeButton;

	@FXML
	private ListView<String> dataBaseList;

	@FXML
	private TableView<List<Object>> contentTable;

	@FXML
	private ListView<String> tablesList;

	@FXML
	private TextArea queryText;

	@FXML
	private Button loadButton;

	@FXML
	private TextField addressText;

	@FXML
	private ComboBox<databaseDrivers> databaseCombo;

	@FXML
	private Button saveButton;

	@FXML
	private Button connectButton;

	@FXML
	void connectDB(ActionEvent event) {
		if (databaseCombo.getValue() instanceof databaseDrivers) {
			if (addressText.getText().matches(IP) && portText.getText().matches(PORT)) {
				{
					try {
						myView.firstConnection(addressText.getText(), portText.getText());
					} catch (MyException e) {
						myView.showError(e);
					}
				}
			}
		}
	}

	@FXML
	void showTables() {
		if (dataBaseList.getSelectionModel().getSelectedIndex() >= 0)
			myView.tablesFill(dataBaseList.getSelectionModel().getSelectedItem());
	}

	@FXML
	void showContent() {
		if (dataBaseList.getSelectionModel().getSelectedIndex() >= 0
				&& tablesList.getSelectionModel().getSelectedIndex() >= 0)
			myView.contentFill(dataBaseList.getSelectionModel().getSelectedItem(),
					tablesList.getSelectionModel().getSelectedItem());
	}

	@FXML
	void executeQuery(ActionEvent event) {
		if (queryText.getText() != "") {
			myView.adhocQuery(queryText.getText());
		}
	}

	@FXML
	void saveFile(ActionEvent event) {
		myView.saveFile(queryText.getText());
		queryText.setText("");
	}

	@FXML
	void loadFile(ActionEvent event) {
		queryText.setText(myView.readFile());
	}

	public ComboBox<databaseDrivers> getDataBaseCombo() {
		return databaseCombo;
	}

	public void setMyView(View myView) {
		this.myView = myView;
	}

	public ListView<String> getDataBaseList() {
		return dataBaseList;
	}

	public TableView<List<Object>> getContentTable() {
		return contentTable;
	}

	public ListView<String> getTablesList() {
		return tablesList;
	}

	public TextField getPortText() {
		return portText;
	}

	public TextField getAddressText() {
		return addressText;
	}
}
