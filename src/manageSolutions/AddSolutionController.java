package manageSolutions;

import JDBC.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import mainModules.ManageSolutionsController;

/**
 * Class that provides functions for the Add Solution UI
 * @author LeaMarie
 */
public class AddSolutionController 
{    
    static final String dbname = "cdisdb", tbname1 = "solution", 
            tbname2 = "disease_solution", tbname3 = "disease";
    Database db = new Database();
	String maxID;
	int solution_maxId, dissol_maxId, disease_maxId;
	
	public ObservableList<String> descriptionList = FXCollections.observableArrayList();
	public ObservableList<String> diseaseList = FXCollections.observableArrayList();
	
	Stage mainStage;
	
    @FXML private TextArea descriptionTxa;
    
    @FXML private ComboBox<String> descriptionCbx;
    @FXML private ComboBox<String> diseaseCbx;
    
    @FXML private Button submitBtn;
    @FXML private Button cancelBtn;
    
    /**
     * Function to handle events inside the UI body
     * @param event
     * @throws Exception
     */
    @FXML private void bodyOptionsHandler(ActionEvent event) throws Exception {
    	if(submitBtn == event.getSource()) {
    		Boolean missingFields = true;
    		if(!isValidInput(descriptionTxa.getText())) {
    			missingFields = false;
    			String values = "VALUES (" + Integer.toString(solution_maxId+1) + ", \"" + descriptionTxa.getText() + "\");";
    			
                db.insertValues(dbname, tbname1, values);
                db.closeConn();
                
                Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Add New Solution Success");
				alert.setHeaderText(null);
				alert.setContentText("New solution added successfully.");
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
    		
    		if(!"Select description...".equals(descriptionCbx.getValue()) && !"Select disease...".equals(diseaseCbx.getValue())) {
    			missingFields = false;
    			String d_id = db.selectValues(dbname, "disease_id", tbname3, "disease_name = \"" + diseaseCbx.getValue() + "\"");
                String s_id = db.selectValues(dbname, "solution_id", tbname1, "solution_description = \"" + descriptionCbx.getValue() + "\"");
                db.closeConn();
                
                int flag = 0;
                for(int i=0; i<dissol_maxId; i++) {
                    String temp1 = db.selectValues(dbname, "disease_id", tbname2, "disease_solution_id = " + Integer.toString(i+1));
                    String temp2 = db.selectValues(dbname, "solution_id", tbname2, "disease_solution_id = " + Integer.toString(i+1));
                    db.closeConn();
                    
                    if(d_id.equals(temp1) && s_id.equals(temp2)) {
                        flag = 1;
                        break;
                    }
                }

                if(flag != 1) {
                    String values = "VALUES (NULL, " + d_id + "," + s_id + ");";

                    db.insertValues(dbname, tbname2, values);
                    Alert alert = new Alert(AlertType.INFORMATION);
    				alert.setTitle("Add New Pairing Success");
    				alert.setHeaderText(null);
    				alert.setContentText("New pairing added successfully.");
    				alert.showAndWait();
                    db.closeConn();
                }
                else {
                    Alert alert = new Alert(AlertType.INFORMATION);
    				alert.setTitle("Adding Pair Error");
    				alert.setHeaderText(null);
    				alert.setContentText("Pair is already existing.");
    				alert.showAndWait();
                }
                
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainModules/ManageSolutionsUI.fxml"));
				Parent root = loader.load();
				ManageSolutionsController controller = loader.<ManageSolutionsController>getController();
				controller.initData();
				Scene scene = new Scene(root);
				mainStage.setScene(scene);
    		}
    		
    		if(missingFields){
    			Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Missing Field/s");
				alert.setHeaderText(null);
				alert.setContentText("At least one of the fields must be filled and/or not exceed the number of characters indicated.");
				alert.showAndWait();
    		}
    	}
    	else if(cancelBtn == event.getSource()) {
			Stage stage = (Stage) cancelBtn.getScene().getWindow();
			stage.close();
		}
    }
    
    /**
     * Gets the max Ids for different tables
     */
    public void getMaxIds() {
        try {
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
            
            String maxID3 = db.selectValuesNoCond(dbname, "MAX(disease_id)", tbname3);
            if(maxID3 == null)
                disease_maxId = 0;
            else
                disease_maxId = Integer.parseInt(maxID3);
            db.closeConn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Initializes the data used in the UI
     * @param mainStage
     * @throws Exception
     */
    public void initData(Stage mainStage) throws Exception {
    	this.mainStage = mainStage;
    	getMaxIds();
    	for(int i=0; i<solution_maxId; i++) {
            String strSolu = db.selectValues(dbname, "solution_description", tbname1, "solution_id = " + Integer.toString(i+1));
            if(!"".equals(strSolu)) descriptionList.add(strSolu);
            db.closeConn();
        }
    	descriptionCbx.setItems(descriptionList);
    	descriptionCbx.setValue("Select description...");
    	for(int i=0; i<disease_maxId; i++) {
            String strDise = db.selectValues(dbname, "disease_name", tbname3, "disease_id = " + Integer.toString(i+1));
            if(!("".equals(strDise) || "Undiseased".equals(strDise))) diseaseList.add(strDise);
            db.closeConn();
        }
        diseaseCbx.setItems(diseaseList);
        diseaseCbx.setValue("Select disease...");
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