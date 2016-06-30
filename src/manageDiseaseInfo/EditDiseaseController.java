package manageDiseaseInfo;

import java.io.File;

import JDBC.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import mainModules.ManageDiseaseInfoController;

/**
 * Class that provides functions for the Edit Disease UI
 * @author LeaMarie
 */
public class EditDiseaseController {

	static final String dbname = "cdisdb", tbname = "disease";
	String diseaseId, diseaseName, description, symptoms;
	Database db = new Database();
	Stage mainStage;
	
    @FXML private TextArea diseaseNameTxa;
    @FXML private TextArea descriptionTxa;
    @FXML private TextArea symptomsTxa;
    
    @FXML private Button cancelBtn;
    @FXML private Button submitBtn;
    
    /**
	 * Function to handle events inside the UI body
	 * @param event
	 * @throws Exception
	 */
    @FXML
    private void bodyOptionsHandler(ActionEvent event) throws Exception {
    	if(submitBtn == event.getSource()) {
    		if(!isValidInput(diseaseNameTxa.getText(), descriptionTxa.getText(), symptomsTxa.getText())) {
    			Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Missing Field/s");
				alert.setHeaderText(null);
				alert.setContentText("No field must be left blank.");
				alert.showAndWait();
        	}
        	else {
                String values = "disease_name = \"" + diseaseNameTxa.getText() + "\", disease_description = \"" + descriptionTxa.getText() 
                        + "\", disease_symptoms = \"" + symptomsTxa.getText() + "\"";
                String cond = "disease_id = " + diseaseId;
                db.loadDriver();
                db.updateValues(dbname, tbname, values, cond);
                db.closeConn();
                
                File dir = new File("images/" + diseaseName);
                File dir2 = new File("images/" + diseaseNameTxa.getText());
                if (dir.isDirectory()) {
                    dir.renameTo(dir2);
                } 
                else {
                    dir.mkdir();
                    dir.renameTo(dir2);
                }
                Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Edit Disease Success");
				alert.setHeaderText(null);
				alert.setContentText("Disease was edited successfully.");
				alert.showAndWait();
				
				Stage stage = (Stage) cancelBtn.getScene().getWindow();
                stage.close();
				
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainModules/ManageDiseaseInfoUI.fxml"));
				Parent root = loader.load();
				ManageDiseaseInfoController controller = loader.<ManageDiseaseInfoController>getController();
				controller.initData();
				Scene scene = new Scene(root);
				mainStage.setScene(scene);
        	}
    	}
    	else if(cancelBtn == event.getSource()) {
			Stage stage = (Stage) cancelBtn.getScene().getWindow();
			stage.close();
		}
    }
    
    /**
     * Initializes data used in the UI
     * 
     * @param mainStage
     * @param diseaseId
     * @param diseaseName
     * @param description
     * @param symptoms
     */
    public void initData(Stage mainStage, String diseaseId, String diseaseName, String description, String symptoms) {
    	this.mainStage = mainStage;
    	this.diseaseId = db.selectValues(dbname, "disease_id", tbname, "disease_name = \"" + diseaseName + "\"");
    	this.diseaseName = diseaseName;
    	this.description = description;
    	this.symptoms = symptoms;
    	
    	diseaseNameTxa.setText(diseaseName);
    	descriptionTxa.setText(description);
    	symptomsTxa.setText(symptoms);
    }
    
    public boolean isValidInput(String diseaseName, String description, String symptoms) {
    	if("".equals(diseaseName) || "".equals(description) || "".equals(symptoms))
    		return false;
    	if(diseaseName.length() > 45 || description.length() > 250 || symptoms.length() > 150)
    		return false;
    	return true;
    }
}
