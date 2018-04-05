package application;

import javafx.application.Application;
import javafx.stage.Stage;
import view.View;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		View affichage = new View(primaryStage);
		
	}

	public static void main(String[] args) {
		launch(args);
	}

}
