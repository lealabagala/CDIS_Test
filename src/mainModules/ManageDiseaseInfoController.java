package mainModules;

import manageDiseaseInfo.*;

import java.io.File;
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

public class ManageDiseaseInfoController {
	static final int UNDISEASED_INDEX = 1;
	public ObservableList<Disease> data = FXCollections.observableArrayList();
	Database db = new Database();
	static final String dbname = "cdisdb", tbname = "disease", tbname2 = "disease_image", tbname3 = "disease_solution", tbname4 = "report";
	int maxId;
	
	@FXML private TableView<Disease> diseaseTable;
	@FXML private TableColumn<Disease, String> dIdCol;
	@FXML private TableColumn<Disease, String> dNameCol;
	@FXML private TableColumn<Disease, String> dDescCol;
	@FXML private TableColumn<Disease, String> dSympCol;
	
	@FXML private MenuItem closeMenu;
	@FXML private MenuItem mngDiseaseMenu;
	@FXML private MenuItem mngSolutionsMenu;
	@FXML private MenuItem diagDiseMenu;
	@FXML private MenuItem viewReportsMenu;
	@FXML private MenuItem viewGraphsMenu;
	@FXML private MenuItem aboutMenu;
	
	@FXML private Button backToMainBtn;
	@FXML private Button addNewDiseaseBtn;
	@FXML private Button editBtn;
	@FXML private Button deleteBtn;
	@FXML private Button trainingImagesBtn;
	@FXML private Button undiseasedBtn;
	
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
		else if(addNewDiseaseBtn == event.getSource()) {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("manageDiseaseInfo/AddDiseaseUI.fxml"));
			Parent root = loader.load();
			AddDiseaseController controller = loader.<AddDiseaseController>getController();
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
			Disease disease = diseaseTable.getSelectionModel().getSelectedItem();
			
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Delete Record");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure you want to delete this record?");

			Optional<ButtonType> result = alert.showAndWait();
			
            if(result.get() == ButtonType.OK)
            {
            	String reportOccur = db.selectValues(dbname, "COUNT(*)", tbname4, "disease_id = " + disease.getDiseaseId());
            	if(reportOccur.equals("0")) {
	                String cond = "disease_id = " + disease.getDiseaseId();
	                db.loadDriver();
	
	                db.deleteValues(dbname, tbname2, cond); // delete images
	                String dname = db.selectValues(dbname, "disease_name", tbname, "disease_id = " + disease.getDiseaseId());
	                deleteDirectory(new File("images/" + dname));
	
	                db.deleteValues(dbname, tbname3, cond); // delete solutions
	
	                db.deleteValues(dbname, tbname, cond); // delete disease
	                db.closeConn(); 
	                
	                Stage stage = (Stage) backToMainBtn.getScene().getWindow();
					
					Alert alert2 = new Alert(AlertType.INFORMATION);
					alert2.setTitle("Delete Disease Success");
					alert2.setHeaderText(null);
					alert2.setContentText("Disease deleted successfully.");
					alert2.showAndWait();
					
	                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainModules/ManageDiseaseInfoUI.fxml"));
					Parent root = loader.load();
					ManageDiseaseInfoController controller = loader.<ManageDiseaseInfoController>getController();
					controller.initData();
					Scene scene = new Scene(root);
					stage.setScene(scene);
            	}
            	else {
            		Alert alert2 = new Alert(AlertType.INFORMATION);
					alert2.setTitle("Deleting Disease Forbidden");
					alert2.setHeaderText(null);
					alert2.setContentText("Can't delete disease since it's associated with a report record.");
					alert2.showAndWait();
            	}
            }
		}
		else if(editBtn == event.getSource()) {
			Disease disease = diseaseTable.getSelectionModel().getSelectedItem();
			
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("manageDiseaseInfo/EditDiseaseUI.fxml"));
			Parent root = loader.load();
			EditDiseaseController controller = loader.<EditDiseaseController>getController();
			Stage stage = (Stage) backToMainBtn.getScene().getWindow();
			controller.initData(stage, disease.getDiseaseId(), disease.getDiseaseName(), disease.getDescription(), disease.getSymptoms());
			
			Scene scene = new Scene(root);
			stage = new Stage();
			stage.setTitle("Coconut Disease Image Scanner");
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
        }
		else if(trainingImagesBtn == event.getSource()) {
			Disease disease = diseaseTable.getSelectionModel().getSelectedItem();
			
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("manageDiseaseInfo/TrainingImagesUI.fxml"));
			Parent root = loader.load();
			TrainingImagesController controller = loader.<TrainingImagesController>getController();
			Stage stage = (Stage) backToMainBtn.getScene().getWindow();
			controller.initData(stage, disease.getDiseaseId(), disease.getDiseaseName(), 0);
			
			Scene scene = new Scene(root);
			stage = new Stage();
			stage.setTitle("Coconut Disease Image Scanner");
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
        }
		else if(undiseasedBtn == event.getSource()) {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("manageDiseaseInfo/TrainingImagesUI.fxml"));
			Parent root = loader.load();
			TrainingImagesController controller = loader.<TrainingImagesController>getController();
			Stage stage = (Stage) backToMainBtn.getScene().getWindow();
			controller.initData(stage, Integer.toString(UNDISEASED_INDEX), "Undiseased", 0);
			
			Scene scene = new Scene(root);
			stage = new Stage();
			stage.setTitle("Coconut Disease Image Scanner");
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		}
	}
	
	/**
	 * Function to delete the training images of
	 * the disease and its directory
	 * @param file
	 */
	public void deleteDirectory(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDirectory(f);
            }
        }
        file.delete();
    }
	
	/**
     * Gets the max ID of the disease table
     */
    public void getMaxId() {
        try {
            db.loadDriver();
            String maxID = db.selectValuesNoCond(dbname, "MAX(disease_id)", tbname);
            db.closeConn();
            if(maxID == null) 
                maxId = 0;
            else 
                maxId = Integer.parseInt(maxID);
        } catch (Exception ex) {
			addNewDiseaseBtn.setDisable(true);
        }
    }
	
    /**
     * Function to initialize data used in the UI
     * @throws Exception
     */
	public void initData() throws Exception {
		getMaxId();
		int count = 0;
		for(int i=0; i<maxId; i++) {
			String did, dname, ddesc, dsymp;
			did = Integer.toString(i+1);
            dname = db.selectValues(dbname, "disease_name", tbname, "disease_id = " + did);
            
            if("".equals(dname)) continue;
            ddesc = db.selectValues(dbname, "disease_description", tbname, "disease_id = " + did);
            dsymp = db.selectValues(dbname, "disease_symptoms", tbname, "disease_id = " + did);
            db.closeConn();
            if(!did.equals(Integer.toString(UNDISEASED_INDEX))) {
            	count++;
            	data.add(new Disease(Integer.toString(count), dname, ddesc, dsymp));
            }
		}

		dIdCol.setSortType(TableColumn.SortType.ASCENDING);
		dNameCol.setSortType(TableColumn.SortType.ASCENDING);
		dDescCol.setSortType(TableColumn.SortType.ASCENDING);
		dSympCol.setSortType(TableColumn.SortType.ASCENDING);
		
		dIdCol.setCellValueFactory(new PropertyValueFactory<Disease, String>("diseaseId"));
		dNameCol.setCellValueFactory(new PropertyValueFactory<Disease, String>("diseaseName"));
		dDescCol.setCellValueFactory(new PropertyValueFactory<Disease, String>("description"));
		dSympCol.setCellValueFactory(new PropertyValueFactory<Disease, String>("symptoms"));

		editBtn.setDisable(true);
		deleteBtn.setDisable(true);
		trainingImagesBtn.setDisable(true);
		
		diseaseTable.setRowFactory( tv -> {
		    TableRow<Disease> row = new TableRow<Disease>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
		    		editBtn.setDisable(false);
		    		deleteBtn.setDisable(false);
		    		trainingImagesBtn.setDisable(false);
		        }
		        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
		        	Disease disease = diseaseTable.getSelectionModel().getSelectedItem();
					
		        	try {
						FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("manageDiseaseInfo/ViewDiseaseUI.fxml"));
						Parent root = loader.load();
						ViewDiseaseController controller = loader.<ViewDiseaseController>getController();
						Stage stage = (Stage) backToMainBtn.getScene().getWindow();
						controller.initData(stage, disease.getDiseaseId(), disease.getDiseaseName(), disease.getDescription(), disease.getSymptoms());
			        	
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
		diseaseTable.setItems(data);
	}
	
	/**
	 * Class to define disease information
	 * @author LeaMarie
	 */
	public static class Disease {
		 
		private final SimpleStringProperty diseaseId;
	    private final SimpleStringProperty diseaseName;
	    private final SimpleStringProperty description;
	    private final SimpleStringProperty symptoms;

	    public Disease(String diseaseId, String diseaseName, String description, String symptoms) {
	    	this.diseaseId = new SimpleStringProperty(diseaseId);
	    	this.diseaseName = new SimpleStringProperty(diseaseName);
	        this.description = new SimpleStringProperty(description);
	        this.symptoms = new SimpleStringProperty(symptoms);
	    }

	    public String getDiseaseId() {
	        return diseaseId.get();
	    }

	    public void setDiseaseId(String dId) {
	        diseaseId.set(dId);
	    }
	    
	    public String getDiseaseName() {
	        return diseaseName.get();
	    }

	    public void setDiseaseName(String dName) {
	        diseaseName.set(dName);
	    }

	    public String getDescription() {
	        return description.get();
	    }

	    public void setDescription(String dDesc) {
	        description.set(dDesc);
	    }

	    public String getSymptoms() {
	        return symptoms.get();
	    }

	    public void setSymptoms(String dSymp) {
	        symptoms.set(dSymp);
	    }
	}
}