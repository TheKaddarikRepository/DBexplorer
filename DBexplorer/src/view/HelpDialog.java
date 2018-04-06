package view;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * the dialog box to show the help.
 * @author cedric
 *
 */
public class HelpDialog {
	private TextArea textArea;
	private final String theText = "1 - Serveur BD : liste d�roulante permettant de choisir entre MySQL, Oracle, MSSQL, Derby et PostgreSQL.\r\n"
			+ "Seul le choix MySQL sera autoris� dans cet exercice. Les autres choix provoqueront l�affichage d�une alerte avec le message : �Fonction Non Impl�ment�e encore.�\r\n"
			+ "\r\n"
			+ "2 - IP Serveur et le port du serveur (pour MySQL 127.0.0.1 ou localhost et le port par d�faut : 3306)\r\n"
			+ "\r\n"
			+ "3 - Le bouton �connexion� ouvre une boite de dialogue pour saisir le login et le mot de passe.\r\n"
			+ "Une fois la connexion �tablie, remplir la liste BD par les bases de donn�es disponibles sur le serveur.\r\n"
			+ "Un clic sur le nom d�une BD met � jour la liste Tables avec les tables de cette BD.\r\n"
			+ "Un clic sur le nom d�une table, affiche toutes les donn�es dans le tableau �Contenu� avec les noms de champs de la table comme ent�tes des colonnes.\r\n"
			+ "\r\n"
			+ "4 - La zone de texte en bas, permet de saisir une requ�te SQL � ex�cuter par le bouton �ex�cuter�. Mettre le tableau �contenu� � jour avec le r�sultat et les ent�tes des colonnes.\r\n"
			+ "\r\n"
			+ "5 - Le bouton sauver permet de sauvegarder la zone de la requ�te dans un fichier (extension :  .amsql) pour pouvoir l�ex�cuter ult�rieurement. Une requ�te par fichier\r\n"
			+ "\r\n"
			+ "6 - Le bouton �Charger� permet de r�cup�rer cette requ�te en choisissant un fichier   .amsql par une boite  de dialogue de s�lection de fichier et la rentrer dans la zone requ�te.\r\n"
			+ "\r\n"
			+ "7 � Un menu sera ajouter pour quitter l�application avec une boite de dialogue demandant confirmation.";

	public HelpDialog() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Help Content");
		alert.setHeaderText("Here is some help on the application behavior.");
		alert.setContentText("Here is the exercise subject.");

		textArea = new TextArea(theText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane helpContent = new GridPane();
		helpContent.setMaxWidth(Double.MAX_VALUE);
		helpContent.add(textArea, 0, 0);

		alert.getDialogPane().setExpandableContent(helpContent);
		Platform.runLater(new TakeFocus());
		alert.showAndWait();
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
			textArea.requestFocus();
		}
	}
}
