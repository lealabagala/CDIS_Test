package manageDiseaseInfo;

import JDBC.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ViewDiseaseController {

	static final String dbname = "cdisdb", tbname = "disease";
	String diseaseId, diseaseName, description, symptoms;
	Database db = new Database();
	Stage mainStage;
	
	@FXML private Label diseaseNameLbl;
    @FXML private Label descriptionLbl;
    @FXML private Label symptomsLbl;
    
    @FXML private Button cancelBtn;
	
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
    	
    	diseaseNameLbl.setText(diseaseName);
    	descriptionLbl.setText(description);
    	symptomsLbl.setText(symptoms);
    }
    
    /**
	 * Function to handle events inside the UI body
	 * @param event
	 * @throws Exception
	 */
    @FXML
    private void bodyOptionsHandler(ActionEvent event) throws Exception {
    	if(cancelBtn == event.getSource()) {
			Stage stage = (Stage) cancelBtn.getScene().getWindow();
			stage.close();
		}
    }
}
