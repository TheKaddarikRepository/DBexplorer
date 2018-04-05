package view;

import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * the dialog box to ask for login & password information into the database.
 * 
 * @author cedric ferrand
 *
 */
public class LoginMessage {

	private Optional<Pair<String, String>> result;
	private Dialog<Pair<String, String>> dialog;
	private Node loginButton;
	private TextField username;
	private PasswordField password;
	private ButtonType loginButtonType;

	public LoginMessage() {
		dialog = new Dialog<>();
		dialog.setTitle("Login Dialog");
		// Set the button types.
		loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		username = new TextField();
		username.setPromptText("Username");
		password = new PasswordField();
		password.setPromptText("Password");
		grid.add(new Label("Username:"), 0, 0);
		grid.add(username, 1, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(password, 1, 1);
		// Enable/Disable login button depending on whether a username was entered.
		loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);
		// Do some validation (using the Java 8 lambda syntax).
		username.textProperty().addListener(new LoginChange());
		dialog.getDialogPane().setContent(grid);
		// Request focus on the username field by default.
		Platform.runLater(new TakeFocus());
		// Convert the result to a username-password-pair when the login button is
		// clicked.
		dialog.setResultConverter(new OnAction());
		result = dialog.showAndWait();
	}

	/**
	 * the user must enter a login value at least.
	 * 
	 * @author cedric
	 *
	 */
	class LoginChange implements ChangeListener<String> {

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			loginButton.setDisable(newValue.trim().isEmpty());
		}
	}

	/**
	 * put the priamryStage on wait untill the dialog is closed.
	 * 
	 * @author cedric
	 *
	 */
	class TakeFocus implements Runnable {

		@Override
		public void run() {
			username.requestFocus();

		}
	}

	/**
	 * the part of the class that makes the connection with the View object to get
	 * informations back.
	 * 
	 * @author cedric
	 *
	 */
	class OnAction implements Callback<ButtonType, Pair<String, String>> {

		@Override
		public Pair<String, String> call(ButtonType param) {
			if (param == loginButtonType) {
				return new Pair<>(username.getText(), password.getText());
			}
			return null;
		}
	}

	public Optional<Pair<String, String>> getResult() {
		return result;
	}

}
