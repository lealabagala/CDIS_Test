package mainModules;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class AboutController {
	
	@FXML private MenuItem closeMenu;
	@FXML private MenuItem mngDiseaseMenu;
	@FXML private MenuItem mngSolutionsMenu;
	@FXML private MenuItem diagDiseMenu;
	@FXML private MenuItem viewReportsMenu;
	@FXML private MenuItem viewGraphsMenu;
	@FXML private MenuItem aboutMenu;
	
	@FXML private Button backToMainBtn;
	
	/**
	 * Function to handle events in the body of the UI
	 * @param event
	 * @throws Exception
	 */
	@FXML
	private void bodyOptionsHandler(ActionEvent event) throws Exception {
		if(backToMainBtn == event.getSource()) {
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("mainModules/GenerateReportUI.fxml"));
			Scene scene = new Scene(root);
			Stage stage = (Stage) backToMainBtn.getScene().getWindow();
			stage.setTitle("Coconut Disease Image Scanner");
			stage.setScene(scene);
			stage.show();
		}
	}
}
