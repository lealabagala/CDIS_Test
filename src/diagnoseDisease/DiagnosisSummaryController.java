package diagnoseDisease;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import JDBC.Database;
import generateReport.ViewGraphsController;
import generateReport.ViewReportsController;
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
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import mainModules.DiagnoseDiseaseController;
import mainModules.ManageDiseaseInfoController;
import mainModules.ManageSolutionsController;

/**
 * This class contains the functions in the Diagnosis Summary UI
 * @author LeaMarie
 */
public class DiagnosisSummaryController 
{    
	static final String dbname = "cdisdb", tbname = "disease", tbname2 = "disease_image", tbname3 = "submitted_image", 
			tbname4 = "report", tbname5 = "disease_solution", tbname6 = "solution", tbname7 = "municipality", tbname8 = "province";
	public ObservableList<String> solutions = FXCollections.observableArrayList();
	String clientName, municipality, province, diagnosedDisease, diagnosedDiseaseId, imgdir, dateTime;
	BufferedImage image;
	Database db = new Database();
    
	@FXML private Label clientNameLbl;
	@FXML private Label municipalityLbl;
	@FXML private Label provinceLbl;
	@FXML private Label dateTimeLbl;
	@FXML private Label diseaseLbl;

	@FXML private MenuItem closeMenu;
	@FXML private MenuItem mngDiseaseMenu;
	@FXML private MenuItem mngSolutionsMenu;
	@FXML private MenuItem diagDiseMenu;
	@FXML private MenuItem viewReportsMenu;
	@FXML private MenuItem viewGraphsMenu;
	@FXML private MenuItem aboutMenu;
	
	@FXML private ImageView imageView;
	@FXML private ListView<String> solutionLvw;
	
	@FXML private Button backToMainBtn;
	@FXML private Button startNewDiagBtn;
	@FXML private Button submitBtn;
	
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
				stage.setResizable(false);
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
	private void buttonEventsHandler(ActionEvent event) throws Exception {
		if(submitBtn == event.getSource()) {
			db.loadDriver();
            String maxID = db.selectValuesNoCond(dbname, "MAX(submitted_image_id)", tbname3);
            int maxId;
            if(maxID == null)
                maxId = 0;
            else
                maxId = Integer.parseInt(maxID);

            String submitted_image_id = Integer.toString(maxId+1);
            imgdir = "submitted_images/im" + submitted_image_id + ".jpg";
            
            String values = "VALUES(" + submitted_image_id + ", \"" + imgdir + "\");";
            db.insertValues(dbname, tbname3, values);
            db.closeConn();
            
            // insert all data in report table
            maxID = db.selectValuesNoCond(dbname, "MAX(report_id)", tbname4);
            if(maxID == null)
                maxId = 0;
            else
                maxId = Integer.parseInt(maxID);
            
            String mun_id = db.selectValues(dbname, "municipality_id", tbname7, "municipality_name = \"" + municipality + "\"");
            
            values = "VALUES(NULL, \"" + clientName + "\", \"" + mun_id +  "\", \"" + dateTime + "\",0," +
            		diagnosedDiseaseId + ", " + submitted_image_id +");";
            db.insertValues(dbname, tbname4, values);
            db.closeConn();
            
            File outputfile = new File(imgdir);
            ImageIO.write(image, "jpg", outputfile);
        	
            Stage stage = (Stage) backToMainBtn.getScene().getWindow();
            Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Submit Report Success");
			alert.setHeaderText(null);
			alert.setContentText("Report was submitted successfully.");
			alert.showAndWait();
			
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainModules/DiagnoseDiseaseUI.fxml"));
			Parent root = loader.load();
			DiagnoseDiseaseController controller = loader.<DiagnoseDiseaseController>getController();
			controller.initData();
			
			Scene scene = new Scene(root);
			stage.setTitle("Coconut Disease Image Scanner");
			stage.setScene(scene);
			stage.show();
		}
		else if(startNewDiagBtn == event.getSource()) {
			Stage mainStage = (Stage) backToMainBtn.getScene().getWindow();
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("diagnoseDisease/InputImageInfoUI.fxml"));
			Parent root = loader.load();
			InputImageInfoController controller = loader.<InputImageInfoController>getController();
			controller.initData(mainStage);
			
			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.setTitle("Coconut Disease Image Scanner");
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
		}
		else if(backToMainBtn == event.getSource()) {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainModules/DiagnoseDiseaseUI.fxml"));
			Parent root = loader.load();
			DiagnoseDiseaseController controller = loader.<DiagnoseDiseaseController>getController();
			controller.initData();Scene scene = new Scene(root);
			Stage stage = (Stage) backToMainBtn.getScene().getWindow();
			stage.setTitle("Coconut Disease Image Scanner");
			stage.setScene(scene);
			stage.show();
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
    
	/**
     * Function to initialize data used in the UI
     * @throws Exception
     */
	public void initData(BufferedImage image, String clientName, String municipality, String province, String disease, String id) throws Exception{
    	this.image = image;
		this.clientName = clientName;
		this.municipality = municipality;
		this.province = province;
		this.diagnosedDisease = disease;
		this.diagnosedDiseaseId = id;
		
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat ft2 = new SimpleDateFormat ("MM-dd-yyyy HH:mm a");
		dateTime = ft.format(dNow);
		image = resizeBufferedImage(image, 300, 300);
		WritableImage wr = convertBufferedImage(image);
				
		clientNameLbl.setText(clientName);
		municipalityLbl.setText(municipality);
		provinceLbl.setText(province);
		dateTimeLbl.setText(ft2.format(dNow));
		diseaseLbl.setText(disease);
		imageView.setImage(wr);
		
		int maxId_diseSol = Integer.parseInt(db.selectValuesNoCond(dbname, "MAX(disease_solution_id)", tbname5));
		String disease_id = db.selectValues(dbname, "disease_id", tbname, "disease_name = \"" + diagnosedDisease + "\"");
		int solutionCount = Integer.parseInt(db.selectValues(dbname, "COUNT(*)", tbname5, "disease_id = " + disease_id));
		db.closeConn();
		String[] solutionDescs = new String[solutionCount];
        int ctr = 0;
        for(int j=0; j<maxId_diseSol; j++) {
        	String d_id = db.selectValues(dbname, "disease_id", tbname5, "disease_solution_id = " + Integer.toString(j+1));
        	db.closeConn();
        	if(d_id.equals(disease_id)) {
        		String s_id = db.selectValues(dbname, "solution_id", tbname5, "disease_solution_id = " + Integer.toString(j+1));
        		solutionDescs[ctr] = db.selectValues(dbname, "solution_description", tbname6, "solution_id = " + s_id);
        		solutions.add(solutionDescs[ctr]);
        		ctr++;
        	}
        	db.closeConn();
        }
        solutionLvw.setItems(solutions);
    }
}