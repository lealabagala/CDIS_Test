package mainModules;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import JDBC.Database;
import diagnoseDisease.InputImageInfoController;
import generateReport.ViewGraphsController;
import generateReport.ViewReportsController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

/**
 * Class that provides functions for the Diagnose Disease UI
 * @author LeaMarie
 */
public class DiagnoseDiseaseController {
	
	@FXML private MenuItem closeMenu;
	@FXML private MenuItem mngDiseaseMenu;
	@FXML private MenuItem mngSolutionsMenu;
	@FXML private MenuItem diagDiseMenu;
	@FXML private MenuItem viewReportsMenu;
	@FXML private MenuItem viewGraphsMenu;
	@FXML private MenuItem aboutMenu;
	
	@FXML private Button backToMainBtn;
	@FXML private Button startDiagnosisBtn;
	
	@FXML private ImageView imageView;
	
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
		else if(startDiagnosisBtn == event.getSource()) {
			Stage mainStage = (Stage) backToMainBtn.getScene().getWindow();
			Stage stage = new Stage();
			
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("diagnoseDisease/InputImageInfoUI.fxml"));
			Parent root = loader.load();
			InputImageInfoController controller = loader.<InputImageInfoController>getController();
			controller.initData(mainStage);
			
			Scene scene = new Scene(root);
			stage.setTitle("Coconut Disease Image Scanner");
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		}
	}
	
	/**
	 * Initializes data needed in the UI
	 * @throws IOException
	 */
	public void initData() throws IOException {
		File file = new File("diagnose_disease_inst.png");
		BufferedImage image = ImageIO.read(file);
		WritableImage wrImg = convertBufferedImage(image);
		imageView.setImage(wrImg);
		try {
			Database db = new Database();
       	 	db.loadDriver();
            
            String maxID1 = db.selectValuesNoCond("cdisdb", "MAX(report_id)", "report");
            if(maxID1 != null)
            	report_maxId = Integer.parseInt(maxID1);
            String maxID2 = db.selectValuesNoCond("cdisdb", "COUNT(*)", "disease");
            String maxID3 = db.selectValuesNoCond("cdisdb", "COUNT(*)", "disease_image");
            if(maxID2.equals("1") && maxID3.equals("0"))
            	startDiagnosisBtn.setDisable(true);
			
		} catch (Exception ex) {
			startDiagnosisBtn.setDisable(true);
			ex.printStackTrace();
        }
	}
	
	/**
	 * Converts the BufferedImage to WritableImage format
	 * @param bf
	 * @return
	 */
	public WritableImage convertBufferedImage(BufferedImage bf) {
		WritableImage wr = null;
        if (bf != null) {
            wr = new WritableImage(bf.getWidth(), bf.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < bf.getWidth(); x++) {
                for (int y = 0; y < bf.getHeight(); y++) {
                    pw.setArgb(x, y, bf.getRGB(x, y));
                }
            }
        }
        return wr;
	}
}
