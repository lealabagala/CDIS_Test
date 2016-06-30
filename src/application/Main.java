/**
 * Coconut Disease Image Detection
 * Using ORB Feature Extraction and
 * FLANN based Matcher (c) 2016
 */
package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import mainModules.MainMenuController;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Main class that will launch the application
 * @author LeaMarie
 */
public class Main extends Application {
	
	/**
	 * Function that launches the stage
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainModules/MainMenuUI.fxml"));
		Parent root = loader.load();
		MainMenuController controller = loader.<MainMenuController>getController();
		controller.initData();
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setTitle("Coconut Disease Image Scanner");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	/**
	 * Main function
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
