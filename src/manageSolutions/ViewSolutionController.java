package manageSolutions;

import JDBC.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ViewSolutionController {
	static final String dbname = "cdisdb", tbname1 = "solution", 
            tbname2 = "disease_solution", tbname3 = "disease";
    
	Database db = new Database();
	int solution_maxId, dissol_maxId;
	Boolean dataChange;
	String solutionId, description;
	Stage mainStage;
	
	@FXML private Label descriptionLbl;
	@FXML private Label diseasesLbl;
	@FXML private Button cancelBtn;
	
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
		String data = "";
		descriptionLbl.setText(description);
		for(int i=0; i<dissol_maxId; i++) {
            String s_id = db.selectValues(dbname, "solution_id", tbname2, "disease_solution_id = " + Integer.toString(i+1));
            db.closeConn();
            if("".equals(s_id)) continue;
            
            if(s_id.equals(solutionId)) {
                String d_id = db.selectValues(dbname, "disease_id", tbname2, "disease_solution_id = " + Integer.toString(i+1));
                String diseaseName = db.selectValues(dbname, "disease_name", tbname3, "disease_id = " + d_id);
                data += diseaseName + "\n";
            }
            db.closeConn();
        }
		diseasesLbl.setText(data);
	}
	
}
