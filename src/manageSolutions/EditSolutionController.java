package manageSolutions;

import JDBC.Database;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import mainModules.ManageSolutionsController;

/**
 * Class that provides functions for the Edit Solution UI
 * @author LeaMarie
 */
public class EditSolutionController {
    static final String dbname = "cdisdb", tbname1 = "solution", 
            tbname2 = "disease_solution", tbname3 = "disease";
    public ObservableList<String> data = FXCollections.observableArrayList();
    
	Database db = new Database();
	int solution_maxId, dissol_maxId;
	Boolean dataChange;
	String solutionId, description;
	Stage mainStage;
	
	@FXML private TextArea descriptionTxa;
	@FXML private ListView<String> diseaseLvw;
	@FXML private Button cancelBtn;
	@FXML private Button submitBtn;
	@FXML private Button deleteDiseaseBtn;
	
	/**
	 * Function to handle events inside the UI body
	 * @param event
	 * @throws Exception
	 */
	@FXML
	private void bodyOptionsHandler(ActionEvent event) throws Exception {
		if(submitBtn == event.getSource()) {
    		if(!isValidInput(descriptionTxa.getText())) {
    			String values = "solution_description = \"" + descriptionTxa.getText() + "\"";
                String cond = "solution_id = " + solutionId;
                db.loadDriver();
                db.updateValues(dbname, tbname1, values, cond);
                db.closeConn();
                
                Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Edit Solution Success");
				alert.setHeaderText(null);
				alert.setContentText("Solution edited successfully.");
				alert.showAndWait();
				
				Stage stage = (Stage) cancelBtn.getScene().getWindow();
                stage.close();
				
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainModules/ManageSolutionsUI.fxml"));
				Parent root = loader.load();
				ManageSolutionsController controller = loader.<ManageSolutionsController>getController();
				controller.initData();
				Scene scene = new Scene(root);
				mainStage.setScene(scene);
    		}
    		else {
    			Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Missing Field/s");
				alert.setHeaderText(null);
				alert.setContentText("At least one of the fields must be filled and/or not exceed the number of characters indicated.");
				alert.showAndWait();
    		}
		}
		else if(deleteDiseaseBtn == event.getSource()) {
			String d_id = db.selectValues(dbname, "disease_id", tbname3, "disease_name = \"" + diseaseLvw.getSelectionModel().getSelectedItem() + "\"");
			String cond = "solution_id = " + solutionId + " AND disease_id = " + d_id;
            db.deleteValues(dbname, tbname2, cond);
            db.closeConn();

            Stage stage = (Stage) cancelBtn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("manageSolutions/EditSolutionUI.fxml"));
			Parent root = loader.load();
			EditSolutionController controller = loader.<EditSolutionController>getController();
			controller.initData(stage, solutionId, description);

			Scene scene = new Scene(root);
            stage.setScene(scene);

			loader = new FXMLLoader(getClass().getClassLoader().getResource("mainModules/ManageSolutionsUI.fxml"));
			root = loader.load();
			ManageSolutionsController controller2 = loader.<ManageSolutionsController>getController();
			controller2.initData();
			scene = new Scene(root);
			mainStage.setScene(scene);
		}
		else if(cancelBtn == event.getSource()) {
			Stage stage = (Stage) cancelBtn.getScene().getWindow();
			stage.close();
		}
	}
	
	/**
     * Gets the max Ids for different tables
     */
	public void getMaxIds() throws Exception {
        db.loadDriver();
        
        String maxID1 = db.selectValuesNoCond(dbname, "MAX(solution_id)", tbname1);
        if(maxID1 == null)
            solution_maxId = 0;
        else
            solution_maxId = Integer.parseInt(maxID1);
        
        String maxID2 = db.selectValuesNoCond(dbname, "MAX(disease_solution_id)", tbname2);
        if(maxID2 == null)
            dissol_maxId = 0;
        else
            dissol_maxId = Integer.parseInt(maxID2);
    }
	
	/**
	 * Initializes the data used in the UI
	 * @param mainStage
	 * @param solutionId
	 * @param description
	 * @throws Exception
	 */
	public void initData(Stage mainStage, String solutionId, String description) throws Exception {
		this.mainStage = mainStage;
		this.solutionId = db.selectValues(dbname, "solution_id", tbname1, "solution_description = \"" + description + "\"");
		this.description = description;
		
		getMaxIds();
		descriptionTxa.setText(description);
		for(int i=0; i<dissol_maxId; i++) {
            String s_id = db.selectValues(dbname, "solution_id", tbname2, "disease_solution_id = " + Integer.toString(i+1));
            db.closeConn();
            if("".equals(s_id)) continue;
            
            if(s_id.equals(solutionId)) {
                String d_id = db.selectValues(dbname, "disease_id", tbname2, "disease_solution_id = " + Integer.toString(i+1));
                String diseaseName = db.selectValues(dbname, "disease_name", tbname3, "disease_id = " + d_id);
                data.add(diseaseName);
            }
            db.closeConn();
        }
		
		deleteDiseaseBtn.setDisable(true);
		diseaseLvw.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	deleteDiseaseBtn.setDisable(false);
		    }
		});
		diseaseLvw.setItems(data);
	}
	
	/**
	 * Evaluates if the given string is a valid input
	 * @param description
	 * @return
	 */
	public boolean isValidInput(String description) {
    	return ("".equals(description) || description.length() > 250);
    }
}
