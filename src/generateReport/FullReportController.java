package generateReport;


import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.imageio.ImageIO;

import JDBC.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mainModules.DiagnoseDiseaseController;
import mainModules.ManageDiseaseInfoController;
import mainModules.ManageSolutionsController;

/**
 * Class that provides functions for the Full Report UI
 * @author LeaMarie
 */
public class FullReportController {

	static final String dbname = "cdisdb", tbname = "report", tbname2 = "disease", tbname3 = "submitted_image", 
			tbname4 = "disease_solution", tbname5 = "solution", tbname6 = "disease_image";
	public ObservableList<String> solutions = FXCollections.observableArrayList();
	String reportId, clientName, municipality, province, dateTime, diseaseName, submittedImageId, imageDirectory, validate_status;
	String solutionDescs[];
	BufferedImage image;
	Database db = new Database();
    
	@FXML private Label clientNameLbl;
	@FXML private Label municipalityLbl;
	@FXML private Label provinceLbl;
	@FXML private Label dateTimeLbl;
	@FXML private Label diseaseLbl;
	@FXML private Label statusLbl;

	@FXML private MenuItem closeMenu;
	@FXML private MenuItem mngDiseaseMenu;
	@FXML private MenuItem mngSolutionsMenu;
	@FXML private MenuItem diagDiseMenu;
	@FXML private MenuItem viewReportsMenu;
	@FXML private MenuItem viewGraphsMenu;
	@FXML private MenuItem aboutMenu;
	
	@FXML private ImageView imageView;
	@FXML private ListView<String> solutionLvw;
	
	@FXML private Button cancelBtn;
	@FXML private Button deleteBtn;
	@FXML private Button exportBtn;
	@FXML private Button addImageBtn;
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
		else if(deleteBtn == event.getSource()) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Delete Record");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure you want to delete this record?");

			Optional<ButtonType> result = alert.showAndWait();
			
            if(result.get() == ButtonType.OK)
            {
                db.loadDriver();
                
                String image_directory = db.selectValues(dbname, "image_directory", tbname3, "submitted_image_id = " + submittedImageId);
                
                String fileName = image_directory;
                new File(fileName).delete();
                
                String cond2 = "report_id = " + reportId;
                db.deleteValues(dbname, tbname, cond2);
                
                String cond = "submitted_image_id = " + submittedImageId;
                db.deleteValues(dbname, tbname3, cond);
                db.closeConn();
                
                Stage stage = (Stage) cancelBtn.getScene().getWindow();
                
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
		else if(exportBtn == event.getSource()) {
			Stage stage = (Stage) cancelBtn.getScene().getWindow();
            
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
			fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setTitle("Export Report to PDF");
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
            	FileOutputStream fileOS = new FileOutputStream(file);
        		Document document = new Document();
        		PdfWriter.getInstance(document, fileOS);

        		com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(imageDirectory);
        		com.itextpdf.text.Image image2 = com.itextpdf.text.Image.getInstance("pca_logo.png");
        		image2.scaleAbsolute(200, 100);
        		image2.setAlignment(com.itextpdf.text.Image.MIDDLE);
        		
        		image.scaleAbsolute(150f, 150f);
        		image.setAlignment(com.itextpdf.text.Image.MIDDLE);
        		
        		Font font = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        		Paragraph title = new Paragraph("Coconut Disease Image Scanner Analysis Report", font);
        		title.setAlignment(Element.ALIGN_CENTER);
        		Paragraph subImg = new Paragraph("Submitted Image");
        		subImg.setAlignment(Element.ALIGN_CENTER);
        		
        		Date dNow = new Date();
        		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        		
        		document.open();
        		document.add(image2);
        		document.add(Chunk.NEWLINE);
        		document.add(title);
        		document.add(Chunk.NEWLINE);
                document.add(Chunk.NEWLINE);
        		document.add(new Paragraph("                 Report ID: " + reportId + " (Date Generated: " + ft.format(dNow) + ")"));
        		document.add(image);
        		document.add(subImg);
        		document.add(Chunk.NEWLINE);
                
        		PdfPTable table = new PdfPTable(2);
        		PdfPCell cell = new PdfPCell(new Phrase("Suggested Solutions"));
                cell.setRowspan(solutionDescs.length);
                cell.setPadding(10);
                PdfPCell tableCell1 = new PdfPCell(new Phrase("Client Name"));
                PdfPCell tableCell2 = new PdfPCell(new Phrase(clientName));
                PdfPCell tableCell3 = new PdfPCell(new Phrase("Municipality"));
                PdfPCell tableCell4 = new PdfPCell(new Phrase(municipality));
                PdfPCell tableCell5 = new PdfPCell(new Phrase("Province"));
                PdfPCell tableCell6 = new PdfPCell(new Phrase(province));
                PdfPCell tableCell7 = new PdfPCell(new Phrase("Date/Time"));
                PdfPCell tableCell8 = new PdfPCell(new Phrase(dateTime));
                PdfPCell tableCell9 = new PdfPCell(new Phrase("Diagnosed Disease"));
                PdfPCell tableCell10 = new PdfPCell(new Phrase(diseaseName));
                
                tableCell1.setPadding(5);
                tableCell2.setPadding(5);
                tableCell3.setPadding(5);
                tableCell4.setPadding(5);
                tableCell5.setPadding(5);
                tableCell6.setPadding(5);
                tableCell7.setPadding(5);
                tableCell8.setPadding(5);
                tableCell9.setPadding(5);
                tableCell10.setPadding(5);
                
                table.addCell(tableCell1);
                table.addCell(tableCell2);
                table.addCell(tableCell3);
                table.addCell(tableCell4);
                table.addCell(tableCell5);
                table.addCell(tableCell6);
                table.addCell(tableCell7);
                table.addCell(tableCell8);
                table.addCell(tableCell9);
                table.addCell(tableCell10);
                
                table.addCell(cell);
                for(int i=0; i<solutionDescs.length; i++) {
        			table.addCell(solutionDescs[i]);
        		}
                
                table.getDefaultCell().setPadding(5);
                document.add(table);
        		document.close();
                fileOS.close();
                
                Alert alert2 = new Alert(AlertType.INFORMATION);
				alert2.setTitle("Export PDF Success (see reports folder)");
				alert2.setHeaderText(null);
				alert2.setContentText("PDF exported successfully.");
				alert2.showAndWait();
            }
		}
		else if(addImageBtn == event.getSource()) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Add Image to Training Images");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure of this action?");
			
			Optional<ButtonType> result = alert.showAndWait();
			
            if(result.get() == ButtonType.OK)
            {
	            db.loadDriver();
	            int maxId;
	            String maxID = db.selectValuesNoCond(dbname, "MAX(disease_image_id)", tbname6);
	            db.closeConn();
	            if(maxID == null)
	                maxId = 0;
	            else
	                maxId = Integer.parseInt(maxID);
	
	            String imgdir = "images/" + diseaseName + "/im" + Integer.toString(maxId+1) + ".jpg";
	            File outputfile = new File(imgdir);
	            ImageIO.write(image, "jpg", outputfile);
	            
            	Alert alert2 = new Alert(AlertType.INFORMATION);
				alert2.setTitle("Add Image Success");
				alert2.setHeaderText(null);
				alert2.setContentText("Image added successfully.");
				alert2.showAndWait();
				addImageBtn.setDisable(true);
            }
		}
		else if(validateBtn == event.getSource()) {
			FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("generateReport/ValidateDiagnosisUI.fxml"));
			Parent root = loader.load();
			ValidateDiagnosisController controller = loader.<ValidateDiagnosisController>getController();
			controller.initData(reportId, diseaseName, submittedImageId);
			Stage stage = (Stage) cancelBtn.getScene().getWindow();
			Scene scene = new Scene(root);
			stage.setScene(scene);
		}
	}

	/**
	 * Initializes the data needed in the UI
	 * @param reportId
	 * @param clientName
	 * @param municipality
	 * @param province
	 * @param dateTime
	 * @param diseaseName
	 * @param submittedImageId
	 * @throws Exception
	 */
	public void initData(String reportId, String clientName, String municipality, String province, String dateTime,
			String diseaseName, String submittedImageId) throws Exception {
		this.reportId = reportId;
		this.clientName = clientName;
		this.municipality = municipality;
		this.dateTime = dateTime;
		this.province = province;
		this.diseaseName = diseaseName;
		this.submittedImageId = submittedImageId;
		
		String imgdir = db.selectValues(dbname, "image_directory", tbname3, "submitted_image_id = " + submittedImageId);
		image = ImageIO.read(new File(imgdir));
		this.imageDirectory = imgdir;
		image = resizeBufferedImage(image, 300, 300);
		WritableImage wr = convertBufferedImage(image);
		
		clientNameLbl.setText(clientName);
		municipalityLbl.setText(municipality);
		dateTimeLbl.setText(dateTime);
		provinceLbl.setText(province);
		diseaseLbl.setText(diseaseName);
		
		String validate_status = db.selectValues(dbname, "validate_status", tbname, "report_id = " + reportId);
		this.validate_status = validate_status;
		if(validate_status.equals("0")) {
			statusLbl.setText("Unvalidated");
			statusLbl.setTextFill(Color.web("#FF0000"));
			addImageBtn.setDisable(true);
			exportBtn.setDisable(true);
		}
		else {
			statusLbl.setText("Validated");
			statusLbl.setTextFill(Color.web("#008000"));
		}
		
		imageView.setImage(wr);
		
		int maxId_diseSol = Integer.parseInt(db.selectValuesNoCond(dbname, "MAX(disease_solution_id)", tbname4));
		String disease_id = db.selectValues(dbname, "disease_id", tbname2, "disease_name = \"" + diseaseName + "\"");
		int solutionCount = Integer.parseInt(db.selectValues(dbname, "COUNT(*)", tbname4, "disease_id = " + disease_id));
		db.closeConn();
		solutionDescs = new String[solutionCount];
        int ctr = 0;
        for(int j=0; j<maxId_diseSol; j++) {
        	String d_id = db.selectValues(dbname, "disease_id", tbname4, "disease_solution_id = " + Integer.toString(j+1));
        	db.closeConn();
        	if(d_id.equals(disease_id)) {
        		String s_id = db.selectValues(dbname, "solution_id", tbname4, "disease_solution_id = " + Integer.toString(j+1));
        		solutionDescs[ctr] = db.selectValues(dbname, "solution_description", tbname5, "solution_id = " + s_id);
        		solutions.add(solutionDescs[ctr]);
        		ctr++;
        	}
        	db.closeConn();
        }
        solutionLvw.setItems(solutions);
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
