package generateReport;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import mainModules.DiagnoseDiseaseController;
import mainModules.ManageDiseaseInfoController;
import mainModules.ManageSolutionsController;

/**
 * Class that provides functions for the Validate Diagnosis UI
 * @author LeaMarie
 */
public class ValidateDiagnosisController {

	static final String dbname = "cdisdb", tbname = "report", tbname2 = "disease", tbname3 = "submitted_image";
	public ObservableList<String> diseases = FXCollections.observableArrayList();
	String reportId, diseaseName, submittedImageId, imageDirectory;
	BufferedImage image;
	Database db = new Database();
    
	@FXML private Label diseaseLbl;

	@FXML private MenuItem closeMenu;
	@FXML private MenuItem mngDiseaseMenu;
	@FXML private MenuItem mngSolutionsMenu;
	@FXML private MenuItem diagDiseMenu;
	@FXML private MenuItem viewReportsMenu;
	@FXML private MenuItem viewGraphsMenu;
	@FXML private MenuItem aboutMenu;
	
	@FXML private ImageView imageView;
	@FXML private ListView<String> diseaseLvw;
	
	@FXML private Button cancelBtn;
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
				Stage stage = (Stage) cancelBtn.getScene().getWindow();
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
		if(cancelBtn == event.getSource()) {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("generateReport/ViewReportsUI.fxml"));
			Parent root = loader.load();
			ViewReportsController controller = loader.<ViewReportsController>getController();
			controller.initData();
			Scene scene = new Scene(root);
			Stage stage = (Stage) cancelBtn.getScene().getWindow();
            stage.setScene(scene);
		}
		else if(validateBtn == event.getSource()) {
			String disease_id = db.selectValues(dbname, "disease_id", tbname2, "disease_name = \"" + diseaseLvw.getSelectionModel().getSelectedItem() + "\"");
			String values = "disease_id = " + disease_id;
            String cond = "report_id = " + reportId;
            db.loadDriver();
            db.updateValues(dbname, tbname, values, cond);
            values = "validate_status = 1";
            db.updateValues(dbname, tbname, values, cond);
            db.closeConn();
            
            Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Validation Success");
			alert.setHeaderText(null);
			alert.setContentText("Diagnosis validated successfully.");
			alert.showAndWait();
			
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("generateReport/ViewReportsUI.fxml"));
			Parent root = loader.load();
			ViewReportsController controller = loader.<ViewReportsController>getController();
			controller.initData();
			Scene scene = new Scene(root);
			Stage stage = (Stage) cancelBtn.getScene().getWindow();
            stage.setScene(scene);
		}
	}

	/**
	 * Initializes the data needed in the UI
	 * @param reportId
	 * @param diseaseName
	 * @param submittedImageId
	 * @throws Exception
	 */
	public void initData(String reportId, String diseaseName, String submittedImageId) throws Exception {
		this.reportId = reportId;
		
		this.diseaseName = diseaseName;
		this.submittedImageId = submittedImageId;
		
		String imgdir = db.selectValues(dbname, "image_directory", tbname3, "submitted_image_id = " + submittedImageId);
		image = ImageIO.read(new File(imgdir));
		this.imageDirectory = imgdir;
		image = resizeBufferedImage(image, 300, 300);
		WritableImage wr = convertBufferedImage(image);
		
		diseaseLbl.setText(diseaseName);
		imageView.setImage(wr);
		
		int maxId = Integer.parseInt(db.selectValuesNoCond(dbname, "MAX(disease_id)", tbname2));
		for(int i=0; i<maxId; i++) {
			String disease = db.selectValues(dbname, "disease_name", tbname2, "disease_id = " + Integer.toString(i+1));
			if(!"".equals(disease)) diseases.add(disease);
		}
		diseaseLvw.setItems(diseases);
		validateBtn.setDisable(true);
		diseaseLvw.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		    	validateBtn.setDisable(false);
		    }
		});
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
	
	/**
     * Resizes the BufferedImage to preferred size
     * @param img
     * @param newW
     * @param newH
     * @return
     */
    public BufferedImage resizeBufferedImage(BufferedImage img, int newW, int newH) { 
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
}
