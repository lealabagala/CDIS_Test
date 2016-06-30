package mainModules;

import generateReport.ViewGraphsController;
import generateReport.ViewReportsController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
//import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Controller class for MainMenuUI.fxml
 * @author LeaMarie
 */
public class MainMenuController {
	
	@FXML private MenuItem closeMenu;
	@FXML private MenuItem mngDiseaseMenu;
	@FXML private MenuItem mngSolutionsMenu;
	@FXML private MenuItem diagDiseMenu;
	@FXML private MenuItem viewReportsMenu;
	@FXML private MenuItem viewGraphsMenu;
	@FXML private MenuItem aboutMenu;
	
	@FXML private Button mngDiseaseBtn;
	@FXML private Button mngSolutionsBtn;
	@FXML private Button diagDiseaseBtn;
	@FXML private Button genReportBtn;
	@FXML private Button backToMainBtn;

	@FXML private ImageView mainLogoImv;
	@FXML private ImageView mngDiseaseImv;
	@FXML private ImageView mngSolutionsImv;
	@FXML private ImageView diagDiseImv;
	@FXML private ImageView genReportImv;
	
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
				Stage stage = (Stage) mngDiseaseBtn.getScene().getWindow();
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
		Parent root;
		if(mngDiseaseBtn == event.getSource()) {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainModules/ManageDiseaseInfoUI.fxml"));
			root = loader.load();
			ManageDiseaseInfoController controller = loader.<ManageDiseaseInfoController>getController();
			controller.initData();
		}
		else if(mngSolutionsBtn == event.getSource()) {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainModules/ManageSolutionsUI.fxml"));
			root = loader.load();
			ManageSolutionsController controller = loader.<ManageSolutionsController>getController();
			controller.initData();
		}
		else if(diagDiseaseBtn == event.getSource()) {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainModules/DiagnoseDiseaseUI.fxml"));
			root = loader.load();
			DiagnoseDiseaseController controller = loader.<DiagnoseDiseaseController>getController();
			controller.initData();
		}
		else {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainModules/GenerateReportUI.fxml"));
			root = loader.load();
			GenerateReportController controller = loader.<GenerateReportController>getController();
			controller.initData();
		}
		
		Scene scene = new Scene(root);
		Stage stage = (Stage) mngDiseaseBtn.getScene().getWindow();
		stage.setTitle("Coconut Disease Image Scanner");
		stage.setScene(scene);
		stage.show();
	}
	
	public void initData() {
//		Image image = new Image("file:icons/coconut.png");
//		mainLogoImv.setImage(image);
//		image = new Image("file:icons/checklist.png");
//		mngDiseaseImv.setImage(image);
//		image = new Image("file:icons/lightbulb.png");
//		mngSolutionsImv.setImage(image);
//		image = new Image("file:icons/glass.png");
//		diagDiseImv.setImage(image);
//		image = new Image("file:icons/report.png");
//		genReportImv.setImage(image);
	}
}
