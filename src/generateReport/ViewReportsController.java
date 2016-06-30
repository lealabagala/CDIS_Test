package generateReport;

import java.io.File;
import java.util.Optional;

import JDBC.Database;
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
import mainModules.DiagnoseDiseaseController;
import mainModules.ManageDiseaseInfoController;
import mainModules.ManageSolutionsController;

/**
 * Class that provides functions for the View Reports UI
 * @author LeaMarie
 */
public class ViewReportsController {
	public ObservableList<Report> data = FXCollections.observableArrayList();
	public ObservableList<String> reportIds = FXCollections.observableArrayList();
	Database db = new Database();
	static final String dbname = "cdisdb", tbname = "report", tbname2 = "disease", tbname3 = "submitted_image",
			tbname4 = "municipality", tbname5 = "province";
	int maxId;
	
	@FXML private TableView<Report> reportTable;
	@FXML private TableColumn<Report, String> reportIdCol;
	@FXML private TableColumn<Report, String> clientNameCol;
	@FXML private TableColumn<Report, String> municipalityCol;
	@FXML private TableColumn<Report, String> provinceCol;
	@FXML private TableColumn<Report, String> dateTimeCol;
	@FXML private TableColumn<Report, String> diseaseCol;
	@FXML private TableColumn<Report, String> statusCol;
	
	@FXML private MenuItem closeMenu;
	@FXML private MenuItem mngDiseaseMenu;
	@FXML private MenuItem mngSolutionsMenu;
	@FXML private MenuItem diagDiseMenu;
	@FXML private MenuItem viewReportsMenu;
	@FXML private MenuItem viewGraphsMenu;
	@FXML private MenuItem aboutMenu;
	
	@FXML private Button backToMainBtn;
	@FXML private Button viewBtn;
	@FXML private Button deleteBtn;
	@FXML private Button validateBtn;
	
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
	 * Function to handle events in the body of UI
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
		else if(deleteBtn == event.getSource()) {
			Report report = reportTable.getSelectionModel().getSelectedItem();
			
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Delete Record");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure you want to delete this record?");

			Optional<ButtonType> result = alert.showAndWait();
			
            if(result.get() == ButtonType.OK)
            {
                db.loadDriver();
                
                String submitted_image_id = db.selectValues(dbname, "submitted_image_id", tbname, "report_id = " + reportIds.get(Integer.parseInt(report.getReportId())-1));
                String image_directory = db.selectValues(dbname, "image_directory", tbname3, "submitted_image_id = " + submitted_image_id);
                
                String fileName = image_directory;
                new File(fileName).delete();
                
                String cond2 = "report_id = " + reportIds.get(Integer.parseInt(report.getReportId())-1);
                db.deleteValues(dbname, tbname, cond2);
                
                String cond = "submitted_image_id = " + submitted_image_id;
                db.deleteValues(dbname, tbname3, cond);
                db.closeConn();
                
                Stage stage = (Stage) backToMainBtn.getScene().getWindow();
                
                Alert alert2 = new Alert(AlertType.INFORMATION);
				alert2.setTitle("Delete Report Success");
				alert2.setHeaderText(null);
				alert2.setContentText("Report deleted successfully.");
				alert2.showAndWait();
				
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("generateReport/ViewReportsUI.fxml"));
				Parent root = loader.load();
				ViewReportsController controller = loader.<ViewReportsController>getController();
				controller.initData();
				Scene scene = new Scene(root);
				stage.setScene(scene);
            }
		}
		else if(viewBtn == event.getSource()) {
			Report report = reportTable.getSelectionModel().getSelectedItem();

			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("generateReport/FullReportUI.fxml"));
			Parent root = loader.load();
			FullReportController controller = loader.<FullReportController>getController();
			controller.initData(reportIds.get(Integer.parseInt(report.getReportId())-1), report.getClientName(), report.getMunicipality(),
					report.getProvince(), report.getDateTime(), report.getDiseaseName(), report.getSubmittedImageId());
			Stage stage = (Stage) backToMainBtn.getScene().getWindow();
			Scene scene = new Scene(root);
			stage.setScene(scene);
		}
		else if(validateBtn == event.getSource()) {
			Report report = reportTable.getSelectionModel().getSelectedItem();
			
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("generateReport/ValidateDiagnosisUI.fxml"));
			Parent root = loader.load();
			ValidateDiagnosisController controller = loader.<ValidateDiagnosisController>getController();
			controller.initData(reportIds.get(Integer.parseInt(report.getReportId())-1), report.getDiseaseName(), report.getSubmittedImageId());
			Stage stage = (Stage) backToMainBtn.getScene().getWindow();
			Scene scene = new Scene(root);
			stage.setScene(scene);
		}
	}
	
	/**
     * Gets the max ID of the disease table
     */
    public void getMaxId() {
        try {
            db.loadDriver();
            String maxID = db.selectValuesNoCond(dbname, "MAX(report_id)", tbname);
            db.closeConn();
            if(maxID == null) 
                maxId = 0;
            else 
                maxId = Integer.parseInt(maxID);
        } catch (Exception ex) {
            
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
			String repid, cname, munic_id, munic, prov_id, prov, dtime, disid, dname, subid, status;
			repid = Integer.toString(i+1);
            cname = db.selectValues(dbname, "report_client", tbname, "report_id = " + repid);
            
            if("".equals(cname)) continue;
            
            munic_id = db.selectValues(dbname, "municipality_id", tbname, "report_id = " + repid);
            munic = db.selectValues(dbname, "municipality_name", tbname4, "municipality_id = " + munic_id);
            db.closeConn();
            prov_id = db.selectValues(dbname, "province_id", tbname4, "municipality_id = " + munic_id);
            prov = db.selectValues(dbname, "province_name", tbname5, "province_id = " + prov_id);
            db.closeConn();
            dtime = db.selectValues(dbname, "report_date", tbname, "report_id = " + repid);
            disid = db.selectValues(dbname, "disease_id", tbname, "report_id = " + repid);
            dname = db.selectValues(dbname, "disease_name", tbname2, "disease_id = " + disid);
            subid = db.selectValues(dbname, "submitted_image_id", tbname, "report_id = " + repid);
            status = db.selectValues(dbname, "validate_status", tbname, "report_id = " + repid);
            if(status.equals("1")) status = "Validated";
            else status = "Unvalidated";
            
            db.closeConn();
            reportIds.add(repid);
            count++;
            data.add(new Report(Integer.toString(count), cname, munic, prov, dtime, dname, subid, status));
		}
		
		reportIdCol.setSortType(TableColumn.SortType.ASCENDING);
		clientNameCol.setSortType(TableColumn.SortType.ASCENDING);
		municipalityCol.setSortType(TableColumn.SortType.ASCENDING);
		provinceCol.setSortType(TableColumn.SortType.ASCENDING);
		dateTimeCol.setSortType(TableColumn.SortType.ASCENDING);
		diseaseCol.setSortType(TableColumn.SortType.ASCENDING);
		statusCol.setSortType(TableColumn.SortType.ASCENDING);
		
		reportIdCol.setCellValueFactory(new PropertyValueFactory<Report, String>("reportId"));
		clientNameCol.setCellValueFactory(new PropertyValueFactory<Report, String>("clientName"));
		municipalityCol.setCellValueFactory(new PropertyValueFactory<Report, String>("municipality"));
		provinceCol.setCellValueFactory(new PropertyValueFactory<Report, String>("province"));
		dateTimeCol.setCellValueFactory(new PropertyValueFactory<Report, String>("dateTime"));
		diseaseCol.setCellValueFactory(new PropertyValueFactory<Report, String>("diseaseName"));
		statusCol.setCellValueFactory(new PropertyValueFactory<Report, String>("status"));
		
		viewBtn.setDisable(true);
		deleteBtn.setDisable(true);
		validateBtn.setDisable(true);
		
		reportTable.setRowFactory( tv -> {
		    TableRow<Report> row = new TableRow<Report>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
		        	viewBtn.setDisable(false);
		    		deleteBtn.setDisable(false);
		    		validateBtn.setDisable(false);
		        }
		        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
		        	Report report = reportTable.getSelectionModel().getSelectedItem();
					try {
						FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("generateReport/FullReportUI.fxml"));
						Parent root = loader.load();
						FullReportController controller = loader.<FullReportController>getController();
						controller.initData(reportIds.get(Integer.parseInt(report.getReportId())-1), report.getClientName(), report.getMunicipality(),
								report.getProvince(), report.getDateTime(), report.getDiseaseName(), report.getSubmittedImageId());
						Stage stage = (Stage) backToMainBtn.getScene().getWindow();
						Scene scene = new Scene(root);
						stage.setScene(scene);
					} catch (Exception ex) {}
		        }
		    });
		    return row ;
		});
		
		reportTable.setItems(data);
	}
	
	/**
	 * Class for the Report records
	 * @author LeaMarie
	 */
	public static class Report {
		 
		private final SimpleStringProperty reportId;
	    private final SimpleStringProperty clientName;
	    private final SimpleStringProperty municipality;
	    private final SimpleStringProperty province;
	    private final SimpleStringProperty dateTime;
	    private final SimpleStringProperty diseaseName;
	    private final SimpleStringProperty submittedImageId;
	    private final SimpleStringProperty status;

	    public Report (String reportId, String clientName, String municipality, String province, String dateTime, String diseaseName, String submittedImageId, String status) {
	    	this.reportId = new SimpleStringProperty(reportId);
	    	this.clientName = new SimpleStringProperty(clientName);
	        this.municipality = new SimpleStringProperty(municipality);
	        this.province = new SimpleStringProperty(province);
	        this.dateTime = new SimpleStringProperty(dateTime);
	        this.diseaseName = new SimpleStringProperty(diseaseName);
	        this.submittedImageId = new SimpleStringProperty(submittedImageId);
	        this.status = new SimpleStringProperty(status);
	    }

	    public String getReportId() {
	        return reportId.get();
	    }

	    public void setReportId(String repId) {
	        reportId.set(repId);
	    }
	    
	    public String getClientName() {
	        return clientName.get();
	    }

	    public void setClientName(String cName) {
	        clientName.set(cName);
	    }

	    public String getMunicipality() {
	        return municipality.get();
	    }

	    public void setMunicipality(String loc) {
	        municipality.set(loc);
	    }

	    public String getProvince() {
	        return province.get();
	    }

	    public void setProvince(String prov) {
	        province.set(prov);
	    }
	    
	    public String getDateTime() {
	        return dateTime.get();
	    }

	    public void setDateTime(String dTime) {
	    	dateTime.set(dTime);
	    }
	    
	    public String getDiseaseName() {
	        return diseaseName.get();
	    }

	    public void setDiseaseName(String dName) {
	    	diseaseName.set(dName);
	    }
	    
	    public String getSubmittedImageId() {
	        return submittedImageId.get();
	    }
	    
	    public void setSubmittedImageId(String subImgId) {
	    	submittedImageId.set(subImgId);
	    }
	    
	    public String getStatus() {
	        return status.get();
	    }
	    
	    public void setStatus(String stat) {
	    	status.set(stat);
	    }
	}
}