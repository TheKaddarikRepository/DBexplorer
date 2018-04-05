package application;

import javafx.scene.control.Alert.AlertType;

/**
 * Custom exception to trigger the AlertDialog.
 * 
 * @author Cedric Ferrand
 *
 */
public class MyException extends Exception {
	private static final long serialVersionUID = 1121003414465976088L;
	private AlertType alert = AlertType.INFORMATION;

	public MyException(String message, AlertType _alert) {
		super(message);
		this.alert = _alert;
	}

	public AlertType getAlert() {
		return alert;
	}

}
