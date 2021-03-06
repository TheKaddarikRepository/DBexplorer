package view;

import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * 
 * @author cedric ferrand
 * @date 2018/04/20
 */
public class ConfirmCloseOperation {
	private Optional<Boolean> result;
	private Dialog<Boolean> dialog;
	private Node exitButton;
	private ButtonType exitButtonType;

	public ConfirmCloseOperation() {
		dialog = new Dialog<>();
		dialog.setTitle("Exit Dialog");
		// Set the button types.
		exitButtonType = new ButtonType("Exit", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(exitButtonType, ButtonType.CANCEL);
		// Create the label.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		grid.add(new Label("Do you Want to Exit the application?"), 0, 0);
		exitButton = dialog.getDialogPane().lookupButton(exitButtonType);

		dialog.getDialogPane().setContent(grid);
		// Request focus on the exitButton field by default.
		Platform.runLater(new TakeFocus());
		// Convert the result to a Boolean when the login button is
		// clicked.
		dialog.setResultConverter(new OnAction());
		result = dialog.showAndWait();
	}

	/**
	 * set focus on the exit button. untill the dialog is closed the primaryStage
	 * thread is waiting.
	 * 
	 * @author cedric
	 *
	 */
	class TakeFocus implements Runnable {

		@Override
		public void run() {
			exitButton.requestFocus();
		}
	}

	/**
	 * event handler to throw a boolean when a button is clicked to the principal
	 * class. it will be called when used in View.
	 * 
	 * @author cedric ferrand
	 *
	 */
	class OnAction implements Callback<ButtonType, Boolean> {

		@SuppressWarnings("deprecation")
		@Override
		public Boolean call(ButtonType param) {
			if (param == exitButtonType) {
				return new Boolean(true);
			}
			return new Boolean(false);
		}
	}

	public Optional<Boolean> getResult() {
		return result;
	}

}
