package mainModules;

import manageSolutions.*;

import java.util.Optional;

import JDBC.Database;
import generateReport.ViewGraphsController;
import generateReport.ViewReportsController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Class that provides functions for the Manage Solutions UI
 * @author LeaMarie
 */
public class ManageSolutionsController {
	public ObservableList<Solution> data = FXCollections.observableArrayList();
	Database db = new Database();
	static final String dbname = "cdisdb", tbname = "solution", tbname2 = "disease_solution", tbname3 = "disease";
	int solution_maxId, dissol_maxId;
	
	@FXML private MenuItem closeMenu;
	@FXML private MenuItem mngDiseaseMenu;
	@FXML
	public MenuItem mngSolutionsMenu;
	@FXML private MenuItem diagDiseMenu;
	@FXML private MenuItem viewReportsMenu;
	@FXML private MenuItem viewGraphsMenu;
	@FXML private MenuItem aboutMenu;
	
	@FXML
	public Button backToMainBtn;
	@FXML private Button addNewSolutionBtn;
	@FXML private Button addDiseaseBtn;
	@FXML private Button editBtn;
	@FXML private Button deleteBtn;
	
	@FXML private TableView<Solution> solutionTable;
	@FXML private TableColumn<Solution, String> solutionIdCol;
	@FXML private TableColumn<Solution, String> descriptionCol;
	@FXML private TableColumn<Solution, String> diseasesCol;
	
	/**
	 * Function to handle menu bar events
	 * @param event
	 * @throws Exception
	 */
	@FXML
	private void menuOptionsHandler(ActionEvent event) throws Exception {
		if(closeMenu == event.getSource()) {
			System.exit(0);
		}
		else {
			Parent root = null;
			if(mngDiseaseMenu == event.getSource()) {
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainModules/ManageDiseaseInfoUI.fxml"));
				root = loader.load();
				ManageDiseaseInfoController controller = loader.<ManageDiseaseInfoController>getController();
				controller.initData();
			}
			else if(mngSolutionsMenu == event.getSource()) {
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainModules/ManageSolutionsUI.fxml"));
				root = loader.load();
				ManageSolutionsController controller = loader.<ManageSolutionsController>getController();
				controller.initData();
			}
			else if(diagDiseMenu == event.getSource()) {
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainModules/DiagnoseDiseaseUI.fxml"));
				root = loader.load();
				DiagnoseDiseaseController controller = loader.<DiagnoseDiseaseController>getController();
				controller.initData();
			}
			else if(viewReportsMenu == event.getSource()){
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("generateReport/ViewReportsUI.fxml"));
				root = loader.load();
				ViewReportsController controller = loader.<ViewReportsController>getController();
				controller.initData();
			}
			else if(viewGraphsMenu == event.getSource()){
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("generateReport/ViewGraphsUI.fxml"));
				root = loader.load();
				ViewGraphsController controller = loader.<ViewGraphsController>getController();
				controller.initData();
			}
			
			if(aboutMenu == event.getSource()){
				root = FXMLLoader.load(getClass().getClassLoader().getResource("mainModules/AboutUI.fxml"));
				Scene scene = new Scene(root);
				Stage stage = new Stage();
				stage.setTitle("Coconut Disease Image Scanner");
				stage.setScene(scene);
				stage.setResizable(false);
				stage.show();
			}
			else {
				Scene scene = new Scene(root);
				Stage stage = (Stage) backToMainBtn.getScene().getWindow();
				stage.setTitle("Coconut Disease Image Scanner");
				stage.setScene(scene);
				stage.show();
			}
		}
	}
	
	/**
	 * Function to handle events inside the UI body
	 * @param event
	 * @throws Exception
	 */
	@FXML
	private void bodyOptionsHandler(ActionEvent event) throws Exception {
		if(backToMainBtn == event.getSource()) {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainModules/MainMenuUI.fxml"));
			Parent root = loader.load();
			MainMenuController controller = loader.<MainMenuController>getController();
			Stage stage = (Stage) backToMainBtn.getScene().getWindow();
			controller.initData();
			
			Scene scene = new Scene(root);
			stage.setTitle("Coconut Disease Image Scanner");
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		}
		else if(addNewSolutionBtn == event.getSource()) {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("manageSolutions/AddSolutionUI.fxml"));
			Parent root = loader.load();
			AddSolutionController controller = loader.<AddSolutionController>getController();
			Stage stage = (Stage) backToMainBtn.getScene().getWindow();
			controller.initData(stage);
			
			Scene scene = new Scene(root);
			stage = new Stage();
			stage.setTitle("Coconut Disease Image Scanner");
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		}
		else if(deleteBtn == event.getSource()) {
			Solution solution = solutionTable.getSelectionModel().getSelectedItem();
			
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Delete Record");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure you want to delete this record?");

			Optional<ButtonType> result = alert.showAndWait();
			if(result.get() == ButtonType.OK)
            {
				String cond = "solution_id = " + solution.getSolutionId();
                db.deleteValues(dbname, tbname2, cond);
                db.deleteValues(dbname, tbname, cond);
                db.closeConn();
                
                Alert alert2 = new Alert(AlertType.INFORMATION);
				alert2.setTitle("Delete Solution Success");
				alert2.setHeaderText(null);
				alert2.setContentText("Solution deleted successfully.");
				alert2.showAndWait();
				
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainModules/ManageSolutionsUI.fxml"));
				Parent root = loader.load();
				ManageSolutionsController controller = loader.<ManageSolutionsController>getController();
				controller.initData();
				Scene scene = new Scene(root);
				Stage stage = (Stage) backToMainBtn.getScene().getWindow();
				stage.setScene(scene);
            }
		}
		else if(editBtn == event.getSource()) {
			Solution solution = solutionTable.getSelectionModel().getSelectedItem();
			
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("manageSolutions/EditSolutionUI.fxml"));
			Parent root = loader.load();
			EditSolutionController controller = loader.<EditSolutionController>getController();
			Stage stage = (Stage) backToMainBtn.getScene().getWindow();
			controller.initData(stage, solution.getSolutionId(), solution.getDescription());
			
			Scene scene = new Scene(root);
			stage = new Stage();
			stage.setTitle("Coconut Disease Image Scanner");
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		}
	}
	
	/**
     * Gets the max ID of the disease table
     */
    public void getMaxId() {
        try {
        	 db.loadDriver();
             
             String maxID1 = db.selectValuesNoCond(dbname, "MAX(solution_id)", tbname);
             if(maxID1 == null)
                 solution_maxId = 0;
             else
                 solution_maxId = Integer.parseInt(maxID1);
             
             String maxID2 = db.selectValuesNoCond(dbname, "MAX(disease_solution_id)", tbname2);
             if(maxID2 == null)
                 dissol_maxId = 0;
             else
                 dissol_maxId = Integer.parseInt(maxID2);
             
             db.closeConn();
        } catch (Exception ex) {
			addNewSolutionBtn.setDisable(true);
        }
    }
	
    /**
     * Function to initialize data used in the UI
     * @throws Exception
     */
	public void initData() throws Exception {
		getMaxId();

		solutionIdCol.setSortType(TableColumn.SortType.ASCENDING);
		descriptionCol.setSortType(TableColumn.SortType.ASCENDING);
		diseasesCol.setSortType(TableColumn.SortType.ASCENDING);
		int solCount = 0;
		for(int i=0; i<solution_maxId; i++) {
			String solid, desc, dise = "";
			solid = Integer.toString(i+1);
			desc = db.selectValues(dbname, "solution_description", tbname, "solution_id = " + solid);
            
			int maxNum = Integer.parseInt(db.selectValues(dbname, "COUNT(*)", tbname2, "solution_id = " + solid));
            db.closeConn();
            
            int count = 0;
            for(int j=0; j<dissol_maxId; j++) {
                String s_id = db.selectValues(dbname, "solution_id", tbname2, "disease_solution_id = " + Integer.toString(j+1));
                db.closeConn();
                if("".equals(s_id)) continue;
                
                if(Integer.parseInt(s_id) == (i+1)) {
                    String d_id = db.selectValues(dbname, "disease_id", tbname2, "disease_solution_id = " + Integer.toString(j+1));
                    dise += db.selectValues(dbname, "disease_name", tbname3, "disease_id = " + d_id);
                    if(count < maxNum-1) dise += " • ";
                    count++;
                }
                db.closeConn();
            }
            db.closeConn();
            solCount++;
            data.add(new Solution(Integer.toString(solCount), desc, dise));
		}
		solutionIdCol.setCellValueFactory(new PropertyValueFactory<Solution, String>("solutionId"));
		descriptionCol.setCellValueFactory(new PropertyValueFactory<Solution, String>("description"));
		diseasesCol.setCellValueFactory(new PropertyValueFactory<Solution, String>("diseases"));
		
		editBtn.setDisable(true);
		deleteBtn.setDisable(true);
		
		solutionTable.setRowFactory( tv -> {
		    TableRow<Solution> row = new TableRow<Solution>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
		    		editBtn.setDisable(false);
		    		deleteBtn.setDisable(false);
		        }
		        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
		        	Solution solution = solutionTable.getSelectionModel().getSelectedItem();
		        	try {
						FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("manageSolutions/ViewSolutionUI.fxml"));
						Parent root = loader.load();
						ViewSolutionController controller = loader.<ViewSolutionController>getController();
						Stage stage = (Stage) backToMainBtn.getScene().getWindow();
						controller.initData(stage, solution.getSolutionId(), solution.getDescription());
						
						Scene scene = new Scene(root);
						stage = new Stage();
						stage.setTitle("Coconut Disease Image Scanner");
						stage.setScene(scene);
						stage.setResizable(false);
						stage.show();
		        	} catch (Exception ex) {}
		        }
		    });
		    return row ;
		});
		solutionTable.setItems(data);
	}
	
	/**
	 * Class to define solution information
	 * @author LeaMarie
	 */
	public static class Solution {
		 
		private final SimpleStringProperty solutionId;
		private final SimpleStringProperty description;
		private final SimpleStringProperty diseases;

	    public Solution(String solutionId, String description, String diseases) {
	    	this.solutionId = new SimpleStringProperty(solutionId);
	    	this.diseases = new SimpleStringProperty(diseases);
	        this.description = new SimpleStringProperty(description);
	    }

	    public String getSolutionId() {
	        return solutionId.get();
	    }

	    public void setSolutionId(String dId) {
	        solutionId.set(dId);
	    }
	    
	    public String getDescription() {
	        return description.get();
	    }

	    public void setDescription(String dDesc) {
	        description.set(dDesc);
	    }

	    public String getDiseases() {
	        return diseases.get();
	    }

	    public void setDiseases(String dName) {
	        diseases.set(dName);
	    }
	}
}
