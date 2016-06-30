package generateReport;

import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import JDBC.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mainModules.DiagnoseDiseaseController;
import mainModules.ManageDiseaseInfoController;
import mainModules.ManageSolutionsController;

/**
 * Class that provides functions for the View Graphs UI
 * @author LeaMarie
 */
public class ViewGraphsController {
	int UNDISEASED_INDEX = 1;
	public ObservableList<String> provinces = FXCollections.observableArrayList();
	static final String dbname = "cdisdb", tbname = "report", tbname2 = "disease", tbname3 = "municipality", tbname4 = "province";
	Database db = new Database();
	int disease_count[];
	CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
	
	@FXML private MenuItem closeMenu;
	@FXML private MenuItem mngDiseaseMenu;
	@FXML private MenuItem mngSolutionsMenu;
	@FXML private MenuItem diagDiseMenu;
	@FXML private MenuItem viewReportsMenu;
	@FXML private MenuItem viewGraphsMenu;
	@FXML private MenuItem aboutMenu;
	
	@FXML private Tab totalDiseasesTab;
	@FXML private Tab provinceTab;
	
	@FXML private Button backToMainBtn;
	@FXML private Button exportBtn;
	
	@FXML private Label label;
	@FXML private Label label1;
	
	@FXML private PieChart pieChart;
	@FXML private ComboBox<String> provinceCbx;
	
	@FXML private TabPane tabPane;
	
	@FXML private BarChart<String,Number> barChart = new BarChart<String,Number>(xAxis,yAxis);
	
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
	 * Function to handle events in the body of the UI
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
		else if(exportBtn == event.getSource()) {
	        Stage stage = (Stage) backToMainBtn.getScene().getWindow();
            
			String imageDirectory = "graph.png";
			File file2 = new File(imageDirectory);
			
			SnapshotParameters sp =  new SnapshotParameters();
			sp.setTransform(Transform.scale(.75, .75));
			
			Font font = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    		
			WritableImage fxImage;
			Paragraph title;
			if(tabPane.getSelectionModel().getSelectedIndex() == 0) {
				fxImage = pieChart.snapshot(sp, null);
				title = new Paragraph("CDIS Disease Occurences Per Region XI Province", font);
			}
			else {
				fxImage = barChart.snapshot(sp, null);
				title = new Paragraph("CDIS Total Disease Occurences in Region XI", font);
			}
			ImageIO.write(SwingFXUtils.fromFXImage(fxImage, null), "png", file2);
			com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(imageDirectory);
			image.setAlignment(com.itextpdf.text.Image.MIDDLE);
			
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
			fileChooser.getExtensionFilters().add(extFilter);
            fileChooser.setTitle("Export Report to PDF");
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
    			FileOutputStream fileOS = new FileOutputStream(file);
    			Document document = new Document();
    			PdfWriter.getInstance(document, fileOS);
    			
    			com.itextpdf.text.Image image2 = com.itextpdf.text.Image.getInstance("pca_logo.png");
        		image2.scaleAbsolute(200, 100);
        		image2.setAlignment(com.itextpdf.text.Image.MIDDLE);
        		
    			title.setAlignment(Element.ALIGN_CENTER);
    			
    			document.open();
    			document.add(image2);
        		document.add(Chunk.NEWLINE);
    			document.add(title);
    			document.add(Chunk.NEWLINE);
    			document.add(image);
    			document.close();
                fileOS.close();
            }
		}
	}
	
	/**
	 * Initializes pie graph contents and returns pie chart data
	 * @param province
	 * @param start
	 * @param length
	 * @return
	 * @throws Exception
	 */
	public ObservableList<PieChart.Data> initPieGraph(String province, int start, int length) throws Exception {
		db.loadDriver();
		
		String municipality_ids[] = new String[length];
		String municipalities[] = new String[length];
		for(int i=0; i<length; i++) {
			municipality_ids[i] = Integer.toString(i+1+start);
			municipalities[i] = db.selectValues(dbname, "municipality_name", tbname3, "municipality_id = " + municipality_ids[i]);
			db.closeConn();
		}
	    	
		float municipality_count[] = new float[length];
		
		String maxID = db.selectValues(dbname, "COUNT(*)", tbname, "disease_id != " + Integer.toString(UNDISEASED_INDEX));
		int maxId_report;
		if(maxID.equals(""))
			maxId_report = Integer.parseInt(maxID);
		else
			maxId_report = 1;
    	
    	for(int i=0; i<length; i++) {
    		String count = db.selectValues(dbname, "COUNT(*)", tbname, "municipality_id = " +  municipality_ids[i] + "&& disease_id != " + Integer.toString(UNDISEASED_INDEX));
    		db.closeConn();
    		if(count.equals("")) continue;
    		municipality_count[i] = Integer.parseInt(count);
    		municipality_count[i] /= maxId_report;
    		municipality_count[i] *= 100;
    	}
    	ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    	Boolean flag = false;
        for(int i=0; i<length; i++) {
        	if(municipality_count[i] > 0) {
        		pieChartData.add(new PieChart.Data(municipalities[i], municipality_count[i]));
        		flag = true;
        	}
        }
        
        if(!flag) {
        	label.setVisible(true);
        	label.setText("No Data Available");
        }
        else {
        	label.setVisible(false);
        }
        return pieChartData;
	}
	
	/**
	 * Function to handle comboBox events
	 * @param event
	 * @throws Exception
	 */
	@FXML
	private void comboBoxHandler(ActionEvent event) throws Exception {
		String prov = provinceCbx.getSelectionModel().getSelectedItem();
		ObservableList<PieChart.Data> pieChartData = null;
		if(prov.equals("Compostela Valley"))
			pieChartData = initPieGraph("Compostela Valley", 0, 11);
		if(prov.equals("Davao del Norte"))
			pieChartData = initPieGraph("Davao del Norte", 11, 11);
		if(prov.equals("Davao del Sur"))
			pieChartData = initPieGraph("Davao del Sur", 22, 11);
		if(prov.equals("Davao Occidental"))
			pieChartData = initPieGraph("Davao Occidental", 33, 5);
		if(prov.equals("Davao Oriental"))
			pieChartData = initPieGraph("Davao Oriental", 38, 11);
		
		pieChart.setData(pieChartData);
        pieChart.setPrefSize(804, 471);
        pieChart.setTitle("Municipalities with the Most Disease Reports in " + prov);
	}
	
	/**
     * Function to initialize data used in the UI
     * @throws Exception
     */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void initData() {
		try {
			for(int i=0; i<5; i++) {
				String province_name = db.selectValues(dbname, "province_name", tbname4, "province_id = " + Integer.toString(i+1));
				db.closeConn();
				if(!"".equals(province_name)) provinces.add(province_name);
			}
			java.util.Collections.sort(provinces);
			provinceCbx.setItems(provinces);
			
			String maxID = db.selectValuesNoCond(dbname, "MAX(report_id)", tbname);
			if(maxID != null) {
				ObservableList<PieChart.Data> pieChartData = null;
				provinceCbx.getSelectionModel().select("Compostela Valley");
				pieChartData = initPieGraph("Compostela Valley", 0, 11);
				pieChart.setData(pieChartData);
		        pieChart.setPrefSize(804, 471);
		        pieChart.setTitle("Municipalities with the Most Disease Reports in Compostela Valley");

		        totalDiseasesTab.setOnSelectionChanged(
	        	    event -> {
		        	    	barChart.getData().clear();
	        	    		int maxId_disease = Integer.parseInt(db.selectValuesNoCond(dbname, "COUNT(*)", tbname2));
	        		    	String diseases[] = new String[maxId_disease];
	        		    	disease_count = new int[maxId_disease];

	        				CategoryAxis xAxis = new CategoryAxis();
	        		        NumberAxis yAxis = new NumberAxis();
	        		        barChart.setTitle("Disease Occurences in Region XI");
	        		        barChart.setBarGap(10);
	        		        barChart.setCategoryGap(100);
	        		        barChart.setAnimated(true);
	        		        xAxis.setLabel("Diseases");       
	        		        yAxis.setLabel("Occurences");
	        		        
	        				XYChart.Series series;
	        		        
	        		        String startYear = db.selectValues(dbname, "MIN(EXTRACT(YEAR FROM report_date))", tbname, "1 ORDER BY report_date");
	        		        String endYear = db.selectValues(dbname, "MAX(EXTRACT(YEAR FROM report_date))", tbname, "1 ORDER BY report_date");
	        				for(int i=Integer.parseInt(startYear); i<=Integer.parseInt(endYear); i++) {
	        					for(int j=0; j<maxId_disease; j++) {
		        		    		diseases[j] = db.selectValues(dbname, "disease_name", tbname2, "disease_id = " + Integer.toString(j+1));
		        		    		disease_count[j] = Integer.parseInt(db.selectValues(dbname, "COUNT(*)", tbname, "disease_id = " + Integer.toString(j+1) 
		        		    		+ " AND EXTRACT(YEAR FROM report_date) = \"" + Integer.toString(i) + "\""));
		        		    	}
	        					series = new XYChart.Series();
	        					series.setName(Integer.toString(i));       
		        		        for(int j=0; j<maxId_disease; j++) {
		        		        	if(!diseases[j].equals("Undiseased"))
		        		        		series.getData().add(new XYChart.Data(diseases[j], disease_count[j]));  
		        		        }
		        		        barChart.getData().add(series);
	        				}
	        	    }
	        	);
	    	} else {
	    		provinceCbx.setDisable(true);
	    		label.setVisible(true);
	        	label.setText("No Data Available");
	        	label1.setVisible(true);
	        	label1.setText("No Data Available");
	        	exportBtn.setDisable(true);
	    	}
		} catch (Exception ex) {
			exportBtn.setDisable(true);
			provinceCbx.setDisable(true);
			ex.printStackTrace();
		}
	}
}