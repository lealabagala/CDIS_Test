/**
 * Coconut Disease Image Detection
 * Using ORB Feature Extraction and
 * FLANN based Matcher (c) 2016
 */
package JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Class to perform database functions
 * @author LeaMarie
 */
public class Database 
{    
    String url = "jdbc:mysql://localhost/";     
    Connection conn;                            
    Statement stmt;                             
    
    /**
     * Loads the JDBC Driver
     * @throws Exception 
     */
    public void loadDriver()
    {
    	try {
    		Class.forName("com.mysql.jdbc.Driver");
    	} catch(Exception ex) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error Loading Data");
			alert.setHeaderText(null);
			alert.setContentText("Check if the XAMPP MySQL Server is running.");
			alert.showAndWait();
			
			ex.printStackTrace();
		}
    }
    
    /**
     * Closes the connection with the database
     * @throws SQLException 
     */
    public void closeConn() throws SQLException {
        if(conn != null) this.conn.close();
    }
    
    /**
     * Inserts a row in a specific table in the database
     * @param dbname
     * @param tbname
     * @param values
     * @throws Exception 
     */
    public void insertValues(String dbname, String tbname, String values)
    {       
    	try {
	        conn = DriverManager.getConnection(url + dbname, "root", "");
	        stmt = conn.createStatement();
	        stmt.execute("USE "+ dbname);
	        stmt.execute("INSERT INTO " + tbname + " " + values);

	    	closeConn();
	    } catch(Exception ex) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error Loading Data");
			alert.setHeaderText(null);
			alert.setContentText("Check if the XAMPP MySQL Server is running.");
			alert.showAndWait();
			
			ex.printStackTrace();
		}
    }
    
    /**
     * Deletes rows of a table in the database
     * @param dbname
     * @param tbname
     * @param cond
     * @throws Exception 
     */
    public void deleteValues(String dbname, String tbname, String cond)
    {       
    	try {
	        conn = DriverManager.getConnection(url + dbname, "root", "");
	        stmt = conn.createStatement();
	        stmt.execute("USE "+ dbname);
	        stmt.execute("DELETE FROM " + tbname + " WHERE " + cond);
	        closeConn();
	    } catch(Exception ex) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error Loading Data");
			alert.setHeaderText(null);
			alert.setContentText("Check if the XAMPP MySQL Server is running.");
			alert.showAndWait();
			
			ex.printStackTrace();
		}
    }
    
    /**
     * Updates the values of a row in a database
     * @param dbname
     * @param tbname
     * @param values
     * @param cond
     * @throws Exception 
     */
    public void updateValues(String dbname, String tbname, String values, String cond)
    {       
    	try {
	        conn = DriverManager.getConnection(url + dbname, "root", "");
	        stmt = conn.createStatement();
	        stmt.execute("USE " + dbname);
	        stmt.execute("UPDATE " + tbname + " SET " + values + " WHERE " + cond);
	        closeConn();
	    } catch(Exception ex) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error Loading Data");
			alert.setHeaderText(null);
			alert.setContentText("Check if the XAMPP MySQL Server is running.");
			alert.showAndWait();
			
			ex.printStackTrace();
		}
    }
    
    /**
     * Queries values from a table in the database with a condition
     * @param dbname
     * @param values
     * @param tbname
     * @param cond
     * @return
     * @throws Exception 
     */
    public String selectValues(String dbname, String values, String tbname, String cond)
    {      
    	String str = "";
        
    	try {
	        conn = DriverManager.getConnection(url + dbname,"root","");
	        stmt = conn.createStatement();
	        ResultSet set = stmt.executeQuery("SELECT " + values + " FROM " + tbname + " WHERE " + cond);
	        while(set.next())
	        {
	            str = set.getString(values);
	        }
	        closeConn();
	    } catch(Exception ex) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error Loading Data");
			alert.setHeaderText(null);
			alert.setContentText("Check if the XAMPP MySQL Server is running.");
			alert.showAndWait();
			
			ex.printStackTrace();
		}
        return str;
    }
    
    /**
     * Queries values from a table in the database with a condition
     * @param dbname
     * @param values
     * @param tbname
     * @param cond
     * @return
     * @throws Exception 
     */
    public String selectOneValue(String dbname, String values, String tbname, String cond)
    {      
    	String str = "";
        try {
	        conn = DriverManager.getConnection(url + dbname,"root","");
	        stmt = conn.createStatement();
	        ResultSet set = stmt.executeQuery("SELECT " + values + " FROM " + tbname + " WHERE " + cond);
	        while(set.next())
	        {
	            str = set.getString(values);
	            break;
	        }
	        closeConn();
	    } catch(Exception ex) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error Loading Data");
			alert.setHeaderText(null);
			alert.setContentText("Check if the XAMPP MySQL Server is running.");
			alert.showAndWait();
			
			ex.printStackTrace();
		}
        return str;
    }
    
    /**
     * Queries values from a table in the database without a condition
     * @param dbname
     * @param values
     * @param tbname
     * @return
     * @throws Exception 
     */
    public String selectValuesNoCond(String dbname, String values, String tbname)
    {      
    	String str = "";
        
    	try {
	        conn = DriverManager.getConnection(url + dbname,"root","");
	        stmt = conn.createStatement();
	        ResultSet set = stmt.executeQuery("SELECT " + values + " FROM " + tbname);
	        while(set.next())
	        {
	            str = set.getString(values);
	        }
	        closeConn();
    	} catch(Exception ex) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error Loading Data");
			alert.setHeaderText(null);
			alert.setContentText("Check if the XAMPP MySQL Server is running.");
			alert.showAndWait();
			
			ex.printStackTrace();
		}

        return str;
    }
}