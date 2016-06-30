package diagnoseDisease;

import java.awt.image.BufferedImage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * This class provides functions for the Result Screen UI
 * @author LeaMarie
 */
public class ResultScreenController {
	String clientName, municipality, province, diagnosedDisease, diagnosedDiseaseId, imgdir, dateTime;
	BufferedImage image;
	Stage mainStage;
	
	@FXML private TextArea displayTxa;
	
	@FXML private Label diseaseLbl;
	
	@FXML private Button cancelBtn;
	@FXML private Button submitBtn;
	
	/**
	 * Function to handle events inside the UI body
	 * @param event
	 * @throws Exception
	 */
    @FXML
    private void buttonEventsHandler (ActionEvent event) throws Exception {
    	if(submitBtn == event.getSource()) {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("diagnoseDisease/DiagnosisSummaryUI.fxml"));
			Parent root = loader.load();
			DiagnosisSummaryController controller = loader.<DiagnosisSummaryController>getController();
			controller.initData(image, clientName, municipality, province, diagnosedDisease, diagnosedDiseaseId);
			
			Stage stage = (Stage) cancelBtn.getScene().getWindow();
			stage.close();
	    	Scene scene = new Scene(root);
			mainStage.setTitle("Coconut Disease Image Scanner");
			mainStage.setScene(scene);
			mainStage.show();
		}
		else if(cancelBtn == event.getSource()) {
			Stage stage = (Stage) cancelBtn.getScene().getWindow();
			stage.close();
		}
    }
    
    /**
     * Initializes the data displayed on the interface
     * @param mainStage
     * @param displayStr
     * @param image
     * @param clientName
     * @param municipality
     * @param province
     * @param disease
     * @param id
     */
    public void initData(Stage mainStage, String displayStr, BufferedImage image, String clientName, String municipality, String province, String disease, String id) {
    	this.mainStage = mainStage;
    	this.image = image;
		this.clientName = clientName;
		this.municipality = municipality;
		this.province = province;
		this.diagnosedDisease = disease;
		this.diagnosedDiseaseId = id;

		displayTxa.setText(displayStr);
		diseaseLbl.setText("Diagnosed Disease: " + disease);
    }
}
