package applicationDB;

import javafx.application.Application;
import javafx.stage.Stage;
import view.View;

/**
 * A simple database explorer, with modularity for multiples DBMS. An excercise
 * given on the 30/03/2018 at AFPA Arras by M. Azma.
 * 
 * @author Cedric Ferrand
 * @version 1.0
 * @date 2018-04-05
 */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		View affichage = new View(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}

}
