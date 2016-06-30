package diagnoseDisease;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;

import JDBC.Database;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 
 * @author LeaMarie
 */
public class InputImageInfoController 
{    
	static final String dbname = "cdisdb", tbname = "province", tbname2 = "municipality", tbname3 = "disease_image";
	public ObservableList<String> municipalities = FXCollections.observableArrayList();
	public ObservableList<String> provinces = FXCollections.observableArrayList();
	String municipality_name;
	
	Mat img_1;
	BufferedImage image;
	WritableImage wrImg;
	String clientName, municipality, province;
	Stage mainStage;
	Database db = new Database();
	
	@FXML private TextField clientNameTxf;
	@FXML private TextField locationTxf;
	
	@FXML private ComboBox<String> municipalityCbx;
	@FXML private ComboBox<String> provinceCbx;
	
	@FXML private Button importImageBtn;
	@FXML private Button importCSVBtn;
	@FXML private Button submitBtn;
	@FXML private Button cancelBtn;
	
	@FXML private ImageView imgThumb;
	
	@FXML private ProgressBar progressBar;
	private BufferedReader br;
	
	/**
	 * Function to handle button events
	 * @param event
	 * @throws Exception
	 */
	@FXML
	private void buttonEventsHandler(ActionEvent event) throws Exception {
		if(importImageBtn == event.getSource()) {
			try {
				Stage stage = (Stage) cancelBtn.getScene().getWindow();
				FileChooser fc = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JPEG files (*.jpg)", "*.jpg");
                fc.getExtensionFilters().add(extFilter);
                
                File result = fc.showOpenDialog(stage);
                if (result != null) {
                    image = ImageIO.read(result);
                	image = resizeBufferedImage(image, 640, 480);
                    wrImg = convertBufferedImage(image);
                    imgThumb.setImage(wrImg);
                    importImageBtn.setText("Image attached");
                    importImageBtn.setDisable(true);
                }
        	} catch (Exception ex) {
        		ex.printStackTrace();
        	}
		}
		else if(submitBtn == event.getSource()) {
			
		}
		else if(cancelBtn == event.getSource()) {
			Stage stage = (Stage) cancelBtn.getScene().getWindow();
			stage.close();
		}
		else if(importCSVBtn == event.getSource()) {
			Stage stage = (Stage) cancelBtn.getScene().getWindow();
			FileChooser fc = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            fc.getExtensionFilters().add(extFilter);
            
            File result = fc.showOpenDialog(stage);
            if (result != null) {
                br = new BufferedReader(new FileReader(result));
                String line;
                while((line = br.readLine()) != null) {
                	int commaCount = line.length() - line.replace(",", "").length();
                	if(commaCount == 3) {
	                	String values[] = line.split(",");
	                	clientNameTxf.setText(values[0]);
	                	municipalityCbx.setValue(values[1]);
	                	provinceCbx.setValue(values[2]);
	                	
	                	image = ImageIO.read(new File(values[3]));
	                	image = resizeBufferedImage(image, 640, 480);
	                	wrImg = convertBufferedImage(image);
	                    imgThumb.setImage(wrImg);
	                    importImageBtn.setText("Attach Another Image");
                	}
                	else {
                		Alert alert = new Alert(AlertType.WARNING);
        				alert.setTitle("Invalid CSV File");
        				alert.setHeaderText(null);
        				alert.setContentText("The CSV file format is \'clientName,municipality,province,imageDirectory\'.");
        				alert.showAndWait();
                	}
                }
            }
		}
	}
	
	/**
	 * Function to handle ComboBox events
	 * @param event
	 * @throws Exception
	 */
	@FXML
	private void comboBoxHandler(ActionEvent event) throws Exception {
		if(municipalityCbx == event.getSource()) {
			Platform.runLater(new Runnable() {
			    @Override public void run() {
					String mun = municipalityCbx.getSelectionModel().getSelectedItem();
					String prov_id;
					try {
						prov_id = db.selectValues(dbname, "province_id", tbname2, "municipality_name = \"" + mun + "\"");
						String prov_name = db.selectValues(dbname, "province_name", tbname, "province_id = " + prov_id);
						provinceCbx.getSelectionModel().select(prov_name);
						municipalityCbx.getSelectionModel().select(mun);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}});
		}
		else if(provinceCbx == event.getSource()) {
			String prov = provinceCbx.getSelectionModel().getSelectedItem();
			String prov_id = db.selectValues(dbname, "province_id", tbname, "province_name = \"" + prov + "\"");
			String mun_name = db.selectOneValue(dbname, "municipality_name", tbname2, "province_id = " + prov_id);
			municipalityCbx.getSelectionModel().select(mun_name);
		}
	}
	
	/**
	 * Initializes data used in the UI
	 * @throws Exception 
	 */
	public void initData(Stage mainStage) {
		for(int i=0; i<5; i++) {
			String province_name = db.selectValues(dbname, "province_name", tbname, "province_id = " + Integer.toString(i+1));
			if(!"".equals(province_name)) provinces.add(province_name);
		}
		java.util.Collections.sort(provinces);
		provinceCbx.setItems(provinces);
		for(int i=0; i<49; i++) {
			String municipality_name = db.selectValues(dbname, "municipality_name", tbname2, "municipality_id = " + Integer.toString(i+1));
			if(!"".equals(municipality_name)) municipalities.add(municipality_name);
		}
		java.util.Collections.sort(municipalities);
		municipalityCbx.setItems(municipalities);
		this.mainStage = mainStage;
		
		int totalImagesCount = Integer.parseInt(db.selectValuesNoCond(dbname, "COUNT(*)", tbname3));
		submitBtn.setOnAction(e -> {
			clientName = clientNameTxf.getText();
			municipality = (String) municipalityCbx.getValue();
			province = (String) provinceCbx.getValue();
			
			if(!isValidInput(clientName) || municipality == null || province == null || image == null) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Missing Field/s");
				alert.setHeaderText(null);
				alert.setContentText("No field must be left blank or exceed the number of characters indicated.");
				alert.showAndWait();
			}
			else {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("diagnoseDisease/ImageProcessUI.fxml"));
				Parent root = loader.load();
				ImageProcessController controller = loader.<ImageProcessController>getController();
				controller.processImage(image);
				controller.initData(mainStage, clientName, municipality, province);
				
				Stage stage = (Stage) cancelBtn.getScene().getWindow();
		    	Scene scene = new Scene(root);
				stage.setTitle("Coconut Disease Image Scanner");
				stage.setScene(scene);
				stage.setResizable(false);
				stage.show();
				
	            Task<Void> task = new Task<Void>() {
	                @Override
	                public Void call() {
	                    try {
//	                    	Alert alert = new Alert(AlertType.WARNING);
//	            			alert.setTitle("Failed to OpenCV library");
//	            			alert.setHeaderText(null);
//	            			alert.setContentText("Failed to OpenCV library.");
//	            			alert.showAndWait();
	                	while(controller.getImageCount() < totalImagesCount) {
	                    	updateProgress(controller.getImageCount(), totalImagesCount);
	                    	Thread.sleep(200);
	                    }
	                    updateProgress(totalImagesCount, totalImagesCount);
	                    } catch(Exception ex) {
	                    	Alert alert = new Alert(AlertType.WARNING);
	            			alert.setTitle("Failed to OpenCV library");
	            			alert.setHeaderText(null);
	            			alert.setContentText("Failed to OpenCV library.");
	            			alert.showAndWait();
	                    }
	                    return null;
	                }
	            };
	            controller.activateProgressBar(task);
	            
	            task.setOnSucceeded(event -> {
	            	try {
		                FXMLLoader loader2 = new FXMLLoader(getClass().getClassLoader().getResource("diagnoseDisease/ResultScreenUI.fxml"));
	            		Parent root2 = loader2.load();
	            		ResultScreenController controller2 = loader2.<ResultScreenController>getController();
	            				controller2.initData(controller.getStage(), controller.getDisplayStr(), controller.getImage(), 
	            				controller.getClientName(), controller.getMunicipality(), controller.getProvince(),
	            				controller.getDiagnosedDisease(), controller.getDiagnosedDiseaseId());
	            		
	            		Stage stage2 = stage;
	                	Scene scene2 = new Scene(root2);
	            		stage2.setTitle("Coconut Disease Image Scanner");
	            		stage2.setScene(scene2);
	            		stage2.setResizable(false);
	            		stage2.show();
	            	} catch(Exception ex) {
	            		Alert alert = new Alert(AlertType.WARNING);
	        			alert.setTitle("Failed to OpenCV library");
	        			alert.setHeaderText(null);
	        			alert.setContentText("Failed to OpenCV library.");
	        			alert.showAndWait();
	            	}
	            });

	            submitBtn.setDisable(true);
	            controller.getDialogStage().show();

	            Thread thread = new Thread(task);
	            thread.start();
			} catch(Exception ex) {
				Alert alert = new Alert(AlertType.WARNING);
    			alert.setTitle("Failed to OpenCV library");
    			alert.setHeaderText(null);
    			alert.setContentText("Failed to OpenCV library.");
    			alert.showAndWait();
			}
			}
        });
	}
	
	/**
	 * Returns a boolean value if the string is a valid input or not
	 * @param clientName
	 * @return
	 */
	public boolean isValidInput(String clientName) {
    	return !("".equals(clientName) || clientName.length() > 45);
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
		BufferedImage newImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);

		Graphics g = newImage.createGraphics();
		g.drawImage(img, 0, 0, newW, newH, null);
		g.dispose();
    	
        return newImage;
    }
}