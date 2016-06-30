package manageDiseaseInfo;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.imageio.ImageIO;

import JDBC.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Class that provides functions for the Training Images UI
 * @author LeaMarie
 */
public class TrainingImagesController 
{    
	static final String dbname = "cdisdb", tbname = "disease_image", tbname2 = "disease";
	Database db = new Database();
	String diseaseId, diseaseName, maxID;
	int maxId;
	BufferedImage image;
	WritableImage wrImg;
	File file, files[];
	int size, currentImg = 0;
	Stage mainStage;
	
    @FXML private Label diseaseLbl;
    
    @FXML private ImageView imageView;
    
    @FXML private Button cancelBtn;
    @FXML private Button addNewImgBtn;
    @FXML private Button previousBtn;
    @FXML private Button nextBtn;
    @FXML private Button deleteImgBtn;
    
    /**
	 * Function to handle events inside the UI body
	 * @param event
	 * @throws Exception
	 */
    @FXML
    private void bodyOptionsHandler(ActionEvent event) throws Exception {
    	if(previousBtn == event.getSource()) {
    		if(currentImg != 0)
                currentImg--;
            else
                currentImg = size-1;
    		loadImage(currentImg + 1);
    	}
    	else if(nextBtn == event.getSource()) {
    		if(currentImg != size-1)
                currentImg++;
            else
            	currentImg = 0;
    		loadImage(currentImg + 1);
    	}
    	else if(addNewImgBtn == event.getSource()) {
    		Stage stage = (Stage) cancelBtn.getScene().getWindow();
    		FileChooser fc = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JPEG files (*.jpg)", "*.jpg");
            fc.getExtensionFilters().add(extFilter);
            
            File imageFile = fc.showOpenDialog(stage);
            if(imageFile != null) {
                image = ImageIO.read(imageFile);
                image = resizeBufferedImage(image, 640, 480);
                
                db.loadDriver();
                maxID = db.selectValuesNoCond(dbname, "MAX(disease_image_id)", tbname);
                db.closeConn();
                if(maxID == null)
                    maxId = 0;
                else
                    maxId = Integer.parseInt(maxID);

                String imgdir = "images/" + diseaseName + "/im" + Integer.toString(maxId+1) + ".jpg";

                String values = "VALUES(NULL, \"" + imgdir + "\", " + diseaseId + ");";
                db.insertValues(dbname, tbname, values);
                db.closeConn();
                
                File outputfile = new File(imgdir);
                ImageIO.write(image, "jpg", outputfile);
                
                Alert alert = new Alert(AlertType.INFORMATION);
    			alert.setTitle("Add New Image Success");
    			alert.setHeaderText(null);
    			alert.setContentText("Image was added successfully.");
    			
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("manageDiseaseInfo/TrainingImagesUI.fxml"));
				Parent root = loader.load();
				TrainingImagesController controller = loader.<TrainingImagesController>getController();
				controller.initData(stage, diseaseId, diseaseName, files.length);
				Scene scene = new Scene(root);
				stage.setScene(scene);
            }
    	}
    	else if(deleteImgBtn == event.getSource()) {
    		String cond = "image_directory = \"images/" + diseaseName + "/" + files[currentImg].getName() + "\"";
			
    		Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Delete Image");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure you want to delete this image?");

			Optional<ButtonType> result = alert.showAndWait();
			
            if(result.get() == ButtonType.OK)
            {
                db.deleteValues(dbname, tbname, cond);
                db.closeConn();
                String fileName = "images/" + diseaseName + "/" + files[currentImg].getName();
                boolean success = (new File(fileName)).delete();
                if(success) {
                	Alert alert2 = new Alert(AlertType.INFORMATION);
    				alert2.setTitle("Delete Image Success");
    				alert2.setHeaderText(null);
    				alert2.setContentText("Image deleted successfully.");
    				alert2.showAndWait(); 
    				currentImg--;
                }   
                
                Stage stage = (Stage) cancelBtn.getScene().getWindow();
				
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("manageDiseaseInfo/TrainingImagesUI.fxml"));
				Parent root = loader.load();
				TrainingImagesController controller = loader.<TrainingImagesController>getController();
				controller.initData(stage, diseaseId, diseaseName, currentImg);
				Scene scene = new Scene(root);
				stage.setScene(scene);
            }
    	}
    	else if(cancelBtn == event.getSource()) {
			Stage stage = (Stage) cancelBtn.getScene().getWindow();
			stage.close();
		}
    }
    
    /**
     * Initializes data used in the UI
     * 
     * @param mainStage
     * @param diseaseId
     * @param diseaseName
     * @throws IOException
     */
    public void initData(Stage mainStage, String diseaseId, String diseaseName, int currentImg) throws IOException {
    	this.mainStage = mainStage;
    	this.diseaseId = db.selectValues(dbname, "disease_id", tbname2, "disease_name = \"" + diseaseName + "\"");
    	this.diseaseName = diseaseName;
    	this.currentImg = currentImg;
    	
    	file = new File("images/" + diseaseName);
        files = file.listFiles();
        size = files.length;
        
        if(size == 0) {
        	previousBtn.setDisable(true);
        	nextBtn.setDisable(true);
        	deleteImgBtn.setDisable(true);
        	diseaseLbl.setText(diseaseName + " [No Images]");
        }
        else {
        	loadImage(this.currentImg + 1);
        	imageView.setImage(wrImg);
        }
    }
    
    /**
     * Loads the image in ImageView
     * @param imgNum
     * @throws IOException
     */
    public void loadImage(int imgNum) throws IOException {
    	diseaseLbl.setText(diseaseName + " [" + imgNum + "/" + size + "]");
        image = ImageIO.read(files[currentImg]);
        image = resizeBufferedImage(image, 300, 300);
        wrImg = convertBufferedImage(image);

        imageView.setImage(wrImg);
    }
    
    /**
     * Converts the BufferedImage to WritableImage
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