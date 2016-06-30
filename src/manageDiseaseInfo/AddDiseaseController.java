package manageDiseaseInfo;

import java.io.File;

import JDBC.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import mainModules.ManageDiseaseInfoController;

/**
 * Class that provides functions for the Add Disease UI
 * @author LeaMarie
 */
public class AddDiseaseController 
{    
	static final String dbname = "cdisdb", tbname = "disease";
	
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
				alert.setContentText("No field must be left blank or exceed the number of characters indicated.");
				alert.showAndWait();
        	}
        	else {
            	String values = "VALUES (NULL, \"" + diseaseNameTxa.getText() + "\", \"" + descriptionTxa.getText() + "\", \"" + symptomsTxa.getText() + "\");";
                db.loadDriver();
                db.insertValues(dbname, tbname, values);
                db.closeConn();
                
                new File("images/" + diseaseNameTxa.getText()).mkdir();
                Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Add New Disease Success");
				alert.setHeaderText(null);
				alert.setContentText("New disease added successfully.");
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
     * Evaluates if the given strings are valid inputs
     * @param diseaseName
     * @param description
     * @param symptoms
     * @return
     */
    public boolean isValidInput(String diseaseName, String description, String symptoms) {
    	if("".equals(diseaseName) || "".equals(description) || "".equals(symptoms))
    		return false;
    	if(diseaseName.length() > 45 || description.length() > 250 || symptoms.length() > 150)
    		return false;
    	return true;
    }
    
    /**
     * Initializes data used in the UI
     * @param mainStage
     */
    public void initData(Stage mainStage) {
    	this.mainStage = mainStage;
    }
}