/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.util;

import java.sql.Statement;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Bryan Valdez <Bryan at ibryan.valdez@gmail.com>
 */
public class Conexion {
  
  	Connection conn;
	
	  public Conexion() throws Exception {

		  try{

			  DataSourceConfig dsn = new DataSourceConfig();
			  //conn = dsn.datasource().getConnection();
			  
		  }catch(Exception e){
			  throw new Exception(e.getMessage());
		  }
	     try{
	    	 //conn = ds.getConnection();
	     }catch (Exception sqle) {
			throw new Exception(sqle.getMessage());
		}

	  }
	  
	  public Connection getConexion(){
		  return this.conn;
		  
	  }
	  
	  public void closeConnection() throws Exception{
		  closeResources(conn, null, null,null,null);	  
	  }
	  public void closeResultSet(ResultSet rs) throws Exception{
		  closeResources(null, rs, null,null,null);	  
	  }
	  public void closeStatement(Statement stmt) throws Exception{
		  closeResources(null, null, stmt,null,null);	  
	  }
	  public void closeCallableStatement(CallableStatement cstmt) throws Exception{
		  closeResources(null, null, null,cstmt,null);	  
	  }
	  public void closeResources(Connection conn, ResultSet rs,Statement stmt,CallableStatement cstmt , PreparedStatement pstmt) throws Exception{	 
		   if (conn != null) {
		        try {
		            conn.close();
		        } catch (SQLException ex) {
		            //System.out.println(ex.getMessage());
		            throw new Exception(ex.getMessage());
		        }
		    }
		    if (rs != null) {
		        try {
		            rs.close();
		        } catch (SQLException ex) {
		        	//System.out.println(ex.getMessage());
		            throw new Exception(ex.getMessage());
		        }
		    }

		    if (stmt != null) {
		        try {
		            stmt.close();
		        } catch (SQLException ex) {
		        	//System.out.println(ex.getMessage());
		            throw new Exception(ex.getMessage());
		        }
		    }
		    if (cstmt != null) {
		        try {
		            cstmt.close();
		        } catch (SQLException ex) {
		        	//System.out.println(ex.getMessage());
		            throw new Exception(ex.getMessage());
		        }
		    }
		    if (pstmt != null) {
		        try {
		            pstmt.close();
		        } catch (SQLException ex) {
		        	//System.out.println(ex.getMessage());
		            throw new Exception(ex.getMessage());
		        }
		    }
	  }
}
