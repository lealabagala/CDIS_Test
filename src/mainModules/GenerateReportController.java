package mainModules;

import JDBC.Database;
import generateReport.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Class that provides functions for the Generate Report UI
 * @author LeaMarie
 */
public class GenerateReportController {
	
	@FXML private MenuItem closeMenu;
	@FXML private MenuItem mngDiseaseMenu;
	@FXML private MenuItem mngSolutionsMenu;
	@FXML private MenuItem diagDiseMenu;
	@FXML private MenuItem viewReportsMenu;
	@FXML private MenuItem viewGraphsMenu;
	@FXML private MenuItem aboutMenu;
	
	@FXML private Button backToMainBtn;
	@FXML private Button viewReportsBtn;
	@FXML private Button viewGraphsBtn;
	
	int report_maxId;
	
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
	
	public void initData() {
		try {
			Database db = new Database();
       	 	db.loadDriver();
            
            String maxID1 = db.selectValuesNoCond("cdisdb", "COUNT(*)", "report");
            db.closeConn();
            report_maxId = Integer.parseInt(maxID1);
		} catch (Exception ex) {
			viewGraphsBtn.setDisable(true);
			viewReportsBtn.setDisable(true);
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
		else if(viewReportsBtn == event.getSource()) {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("generateReport/ViewReportsUI.fxml"));
			Parent root = loader.load();
			ViewReportsController controller = loader.<ViewReportsController>getController();
			controller.initData();
			
			Scene scene = new Scene(root);
			Stage stage = (Stage) backToMainBtn.getScene().getWindow();
			stage.setTitle("Coconut Disease Image Scanner");
			stage.setScene(scene);
			stage.show();
		}
		else if(viewGraphsBtn == event.getSource()) {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("generateReport/ViewGraphsUI.fxml"));
			Parent root = loader.load();
			ViewGraphsController controller = loader.<ViewGraphsController>getController();
			controller.initData();
			
			Scene scene = new Scene(root);
			Stage stage = (Stage) backToMainBtn.getScene().getWindow();
			stage.setTitle("Coconut Disease Image Scanner");
			stage.setScene(scene);
			stage.show();
		}
	}
}
