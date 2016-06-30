package diagnoseDisease;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.opencv.core.*;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import JDBC.Database;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * This class contains the main Image Processing functions of the system
 * @author LeaMarie
 */
public class ImageProcessController 
{   
	static final String dbname = "cdisdb", tbname = "disease", tbname2 = "disease_image", tbname3 = "submitted_image", 
		tbname4 = "report", tbname5 = "disease_solution", tbname6 = "solution";
    int UNDISEASED_INDEX;
	BufferedImage image, image2;
	Mat img_1, img_2;
	Database db = new Database();
	int disease_rowCount, disease_maxId, images_gmatches[][], max_imagesCount, disease_gmatches[], disease_imgMatches[], imageCount = 0;
	double images_hmatches[][], disease_hmatches[];
	String disease_id[], disease_name[], disease_imagesCount[], displayStr = "";
	String clientName, municipality, province, diagnosedDisease, diagnosedDiseaseId;
	Stage mainStage;
	
	@FXML private Button cancelBtn;
	@FXML private ProgressBar progressBar;
	@FXML private ProgressIndicator progressInd;
	
    /**
     * Function to initialize data used in the UI
     */
    public void initData(Stage mainStage, String clientName, String municipality, String province) {
		this.mainStage = mainStage;
    	this.clientName = clientName;
		this.municipality = municipality;
		this.province = province;
	}
    
    /**
	 * Function to handle events inside the UI body
	 * @param event
	 * @throws Exception
	 */
    @FXML
    private void buttonEventsHandler (ActionEvent event) throws Exception {
    	if(cancelBtn == event.getSource()) {
			Stage stage = (Stage) cancelBtn.getScene().getWindow();
			stage.close();
		}
    }
    
    /**
     * Calls the necessary functions to perform image matching
     * @param image
     * @throws Exception
     */
    public void processImage (BufferedImage image) throws Exception {
    	this.image = image;
    	Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
            	try {
            		String osName = System.getProperty("os.name");
            		int bitness = Integer.parseInt(System.getProperty("sun.arch.data.model"));
	                if(osName.contains("Windows")) {
            			if(bitness == 32){
	                    	System.load("/opencv/build/java/x86/opencv_java2411.dll");
	                    }
	                    else if (bitness == 64){
	                    	System.load("/opencv/build/java/x64/opencv_java2411.dll");
	                    }
            		}
                    else	
                    	System.load("/Users/LeaMarie/OpenCV/opencv-2.4.11/build/lib/libopencv_java2411.dylib");
	                
	            	img_1 = bufferedImageToMat(image);
	            	initValues();
	            	CBIR();
	            	computeTotalMatches();
	        		computeAverageHistMatch();
	            	displayDiagnosis();
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
        new Thread(task).start();
    }
    
    /**
     * Initialize values needed for image processing
     * @throws Exception
     */
    public void initValues() throws Exception {
    	db.loadDriver();
        disease_rowCount = Integer.parseInt(db.selectValuesNoCond(dbname, "COUNT(*)", tbname));                
        disease_maxId = Integer.parseInt(db.selectValuesNoCond(dbname, "MAX(disease_id)", tbname));

        disease_id = new String[disease_rowCount];
        disease_name = new String[disease_rowCount];
    	disease_imagesCount = new String[disease_rowCount];
        int count = 0;
        max_imagesCount = 0;
        
        // getting the disease ids and images per disease available
        for(int i=0; i<disease_maxId; i++) {
        	String d_name = db.selectValues(dbname, "disease_name", tbname, "disease_id = " + Integer.toString(i+1));
        	if("".equals(d_name)) {
        		continue;
        	}
        	
        	if("Undiseased".equals(d_name)) {
            	UNDISEASED_INDEX = i + 1;
        	}
        	
        	disease_id[count] = Integer.toString(i+1);
        	disease_name[count] = d_name;
        	disease_imagesCount[count] = db.selectValues(dbname, "COUNT(*)", tbname2, "disease_id = " + Integer.toString(i+1));
        	
        	if(max_imagesCount < Integer.parseInt(disease_imagesCount[count])) {
        		max_imagesCount = Integer.parseInt(disease_imagesCount[count]);
        	}
        	count++;
        }
    }
    
    /**
     * Returns the current image count being processed
     * @return
     */
    public int getImageCount() {
    	return imageCount;
    }
    
    /**
     * Implements Content-based Image Retrieval process
     * @throws Exception
     */
    public void CBIR () throws Exception {
        images_gmatches = new int[disease_rowCount][max_imagesCount];
        images_hmatches = new double[disease_rowCount][max_imagesCount];
        int imageCount = 0;
        for(int i=0; i<disease_rowCount; i++) {
            String folderName = db.selectValues(dbname, "disease_name", tbname, "disease_id = " + disease_id[i]);
            
        	File file_images = new File("images/" + folderName);
            File files_images[] = file_images.listFiles();
            int images_count = files_images.length;
            
            for (int j=0; j<images_count; j++) {
            	BufferedImage image2;
                Mat preproc_1, preproc_2, desc_1, desc_2;
                MatOfKeyPoint keyp_1, keyp_2;
                MatOfDMatch matches, good_matches;
            	
                displayStr += "Disease [" + folderName + "] image [" + files_images[j].getName() + "]\n\tFeature Matches: ";
                
            	image2 = ImageIO.read(files_images[j]);
            	image2 = resizeBufferedImage(image2, 640, 480);
                img_2 = bufferedImageToMat(image2);
                
                preproc_1 = imagePreprocessing(img_1);
                preproc_2 = imagePreprocessing(img_2);
                
                keyp_1 = featureDetection(preproc_1);
                keyp_2 = featureDetection(preproc_2);
                
                desc_1 = featureDescription(img_1, keyp_1);
                desc_2 = featureDescription(img_2, keyp_2);
                
                matches = featureMatching(desc_1, desc_2);
                good_matches = computeGoodMatches(desc_1, matches);
                
                images_gmatches[i][j] = computeNumberOfMatchedKeypoints(good_matches);
//                images_gmatches[i][j] = computeNumberOfGoodMatches(good_matches);
                displayStr += images_gmatches[i][j];
                images_hmatches[i][j] = histogramMatching(img_1, img_2)*100;
                displayStr += "\n\tHistogram Matching Percentage: " + String.format("%.2f", images_hmatches[i][j]) + "\n";
                
                imageCount++;
                this.imageCount = imageCount;
            }
        }
        displayStr += "\n";
    }
    
    /**
     * Activates the progress bar and indicator in the UI
     * @param task
     */
    public void activateProgressBar(final Task<?> task)  {
        progressBar.progressProperty().bind(task.progressProperty());
        progressInd.progressProperty().bind(task.progressProperty());
    }
    
    /**
     * Returns the value indicated
     * @return
     */
    public Stage getDialogStage() { return mainStage; }
    
    public Stage getStage() { return mainStage; }
    
    public String getDisplayStr() { return displayStr; }
    
    public BufferedImage getImage() { return image; }
    
    public String getClientName() { return clientName; }
    
    public String getMunicipality() { return municipality; }
    
    public String getProvince() { return province; }
    
    public String getDiagnosedDisease() { return diagnosedDisease; }
    
    public String getDiagnosedDiseaseId() { return diagnosedDiseaseId; }
    
    /**
     * Computes for the total matches for each disease
     */
    public void computeTotalMatches() {
    	disease_gmatches = new int[disease_rowCount];
    	disease_imgMatches = new int[disease_rowCount];
    	
    	for(int i=0; i<disease_rowCount; i++) {
        	disease_gmatches[i] = 0;
        	disease_imgMatches[i] = 0;
        	for(int j=0; j<Integer.parseInt(disease_imagesCount[i]); j++) {	
    			disease_gmatches[i] += images_gmatches[i][j];
        		disease_imgMatches[i]++;
        	}
//        	disease_gmatches[i] /= Integer.parseInt(disease_imagesCount[i]);
        	displayStr += "Total Disease [" + disease_name[i] + "] Feature Matches: "+ disease_gmatches[i] + "\n";
        }
        displayStr += "\n";
    }
    
    /**
     * Computes the average histogram match percentage for each disease
     */
    public void computeAverageHistMatch() {
    	disease_hmatches = new double[disease_rowCount];
    	for(int i=0; i<disease_rowCount; i++) {
    		int count = 0, count2 = 0;
        	for(int j=0; j<Integer.parseInt(disease_imagesCount[i]); j++) {
        		if(images_hmatches[i][j] > 0) {
        			count += images_hmatches[i][j];
        			count2++;
        		}
        	}
            if(count2 > 0) {
            	disease_hmatches[i] = count / count2;
            	displayStr += "Histogram Match Percentage For Disease [" + disease_name[i] + "]: " + disease_hmatches[i] + "\n";
            }
            else {
            	disease_hmatches[i] = 0;
            	displayStr += "Histogram Match Percentage For Disease [" + disease_name[i] + "]: " + 0.0 + "\n";
            }
        }
    	displayStr += "\n";
    }
    
    /**
     * Determines the disease diagnosed
     */
    public void displayDiagnosis() {
    	int disease_index = UNDISEASED_INDEX-1;
    	double best_percentage = 0, matchPercent;
        int totalMatches = 0;
        
    	for(int i=0; i<disease_rowCount; i++) {
        	totalMatches += disease_gmatches[i];
        }

    	double average;
    	double matchPercent_undiseased = ((double) disease_gmatches[disease_index] / 
    			((double) Integer.parseInt(disease_imagesCount[disease_index])*500)) * 100;
    	double average_undiseased = (matchPercent_undiseased * 0.6) + (disease_hmatches[disease_index] * 0.40);
    	for(int i=0; i<disease_rowCount; i++) {
			if(totalMatches > 0) {
    			matchPercent = ((double) disease_gmatches[i] / ((double) Integer.parseInt(disease_imagesCount[i])*500)) * 100;
			}
			else matchPercent = 0;
			
//			average = (matchPercent + disease_hmatches[i]) / 2;
			average = (matchPercent * 0.6) + (disease_hmatches[i] * 0.40);
			displayStr += "Feature and Histogram Match Percentage [" + disease_name[i] + "]: " + String.format("%.2f", average) + "\n";
			
			if(average > best_percentage) {
				best_percentage = average;
				disease_index = i;
				if((Math.abs(average_undiseased - average) <= 1)) {
//						&& (Math.abs(disease_hmatches[UNDISEASED_INDEX-1] - disease_hmatches[i]) <= 3)) {
					best_percentage = average_undiseased;
					disease_index = UNDISEASED_INDEX-1;
				}
			}
    	}
    	
    	if(totalMatches == 0) {
    		for(int i=0; i<disease_rowCount; i++) {
    			if(disease_hmatches[i] < 55) {
    				disease_index = UNDISEASED_INDEX - 1;
    				break;
    			}
    		}
    	}
    	
        diagnosedDisease = disease_name[disease_index];
        diagnosedDiseaseId = disease_id[disease_index];
    }
    
    /**
     * Function to convert buffered images to Mat (OpenCV) type
     * Source: http://www.codeproject.com/Tips/752511/How-to-
     *         Convert-Mat-to-BufferedImage-Vice-Versa
     * @param in
     * @return 
     */
    public Mat bufferedImageToMat(BufferedImage in)
    {
    	Mat out;
		byte[] data;
		
		out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC3);
		data = new byte[in.getWidth() * in.getHeight() * (int)out.elemSize()];
		int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
		for(int i = 0; i < dataBuff.length; i++)
		{
			data[i*3] = (byte) ((dataBuff[i] >> 16) & 0xFF);
			data[i*3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
			data[i*3 + 2] = (byte) ((dataBuff[i] >> 0) & 0xFF);
		}      
		out.put(0, 0, data);
		return out;
     } 
    
    /**
     * Image Pre-processing Phase
     * @param img 
     */
    public Mat imagePreprocessing(Mat img) {
    	Mat dst = img.clone();
    	Size size = new Size(5, 5);
    	Imgproc.GaussianBlur(img, dst, size, 3, 3); // Gaussian Blur - smoothens the image
    	
        return img;
    }
    
    /**
     * Detects the important key points of an image
     * @param img
     * @return keypoints
     */
    public MatOfKeyPoint featureDetection(Mat img) {
        FeatureDetector orbfd = FeatureDetector.create(FeatureDetector.ORB);

        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        orbfd.detect(img, keypoints);
        return keypoints;
    }
    
    /**
     * Describes important features in an image
     * @param img
     * @return descriptors
     */
    public Mat featureDescription(Mat img, MatOfKeyPoint keypoints) {
        DescriptorExtractor orbde = DescriptorExtractor.create(DescriptorExtractor.ORB);
        
        Mat descriptors = new Mat();
        orbde.compute(img, keypoints, descriptors);
        
        return descriptors;
    }
    
    /**
     * Matches the descriptors of the two images together
     * @param descriptors_1
     * @param descriptors_2
     * @return matches
     */
    public MatOfDMatch featureMatching(Mat descriptors_1, Mat descriptors_2) {
        DescriptorMatcher flanndm = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);

        MatOfDMatch matches = new MatOfDMatch();

        if(descriptors_1.type()!=CvType.CV_32F) {
            descriptors_1.convertTo(descriptors_1, CvType.CV_32F);
        }

        if(descriptors_2.type()!=CvType.CV_32F) {
            descriptors_2.convertTo(descriptors_2, CvType.CV_32F);
        }

        flanndm.match(descriptors_1, descriptors_2, matches);
        return matches;
    }
    
    /**
     * Computes for the good matches among all the matches detected
     * @param descriptors
     * @param matches
     * @return good_matches
     */
    public MatOfDMatch computeGoodMatches(Mat descriptors, MatOfDMatch matches) {
        double max_dist = 0; double min_dist = 100;

        DMatch[] dmatches = matches.toArray();
        for(int i = 0; i < descriptors.rows(); i++) { 
            double dist = dmatches[i].distance;
            if( dist < min_dist ) min_dist = dist;
            if( dist > max_dist ) max_dist = dist;
        }
        
        MatOfDMatch good_matches = new MatOfDMatch();

        for(int i = 0; i < descriptors.rows(); i++) { 
            if(dmatches[i].distance <= Math.max(2*min_dist, 0.02)) { 
                    good_matches.push_back(matches);
            }
        }

        return good_matches;
    }
    
    public int computeNumberOfGoodMatches(MatOfDMatch good_matches) {
    	DMatch[] good_dmatches = good_matches.toArray();
    	return good_dmatches.length;
    }
    
    /**
     * Computes the total number of keyPoints matched
     * @param good_matches
     * @return
     */
    public int computeNumberOfMatchedKeypoints(MatOfDMatch good_matches) {
        int arr[] = new int[500];
        for(int i=0; i<500; i++) {
            arr[i] = 0;
        }
        
        DMatch[] good_dmatches = good_matches.toArray();
        for( int i = 0; i < (int)good_dmatches.length; i++ ) {
            arr[good_dmatches[i].trainIdx] = 1;
        }

        int count = 0;
        for(int i=0; i<500; i++) {
            if(arr[i] == 1) count++;
        }
        
        return count;
    }
    
    public double histogramMatching(Mat img1, Mat img2) {
	    Mat hsv_test1, hsv_test2;

	    hsv_test1 = new Mat(img1.height(), img1.width(), CvType.CV_8UC2);
	    hsv_test2 = new Mat(img2.height(), img2.width(), CvType.CV_8UC2);
	    
	    Imgproc.cvtColor( img1, hsv_test1, Imgproc.COLOR_RGB2GRAY );
	    Imgproc.cvtColor( img2, hsv_test2, Imgproc.COLOR_RGB2GRAY );

	    Vector<Mat> hsv1_planes, hsv2_planes;
	    hsv1_planes = new Vector<Mat>();
	    hsv2_planes = new Vector<Mat>();
	    
	    Core.split(hsv_test1, hsv1_planes);
	    Core.split(hsv_test2, hsv2_planes);
	    
	    boolean accumulate = false;
	    MatOfInt channels = new MatOfInt(0);
	    final MatOfFloat ranges = new MatOfFloat(0f, 256f);
	    MatOfInt histSize = new MatOfInt(256);
	    
	    Mat hist_test1 = new Mat();
	    Mat hist_test2 = new Mat();

	    Imgproc.calcHist(hsv1_planes, channels, new Mat(), hist_test1, histSize, ranges, accumulate);
	    Core.normalize( hist_test1, hist_test1, 0, 1, Core.NORM_MINMAX, -1, new Mat() );

	    Imgproc.calcHist(hsv2_planes, channels, new Mat(), hist_test2, histSize, ranges, accumulate);
	    Core.normalize( hist_test2, hist_test2, 0, 1, Core.NORM_MINMAX, -1, new Mat() );

        return Imgproc.compareHist( hist_test1, hist_test2, 0);
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