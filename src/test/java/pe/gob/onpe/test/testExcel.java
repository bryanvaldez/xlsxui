/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.test;

import java.io.File;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pe.gob.onpe.claridadui.enums.FormatoEnum;
import pe.gob.onpe.claridadui.service.iface.IExcelXSSFValidatorService;
import pe.gob.onpe.claridadui.service.iface.IFactoryService;
import pe.gob.onpe.claridadui.service.impl.FactoryService;
import pe.gob.onpe.claridadui.util.Conexion;

/**
 *
 * @author bvaldez
 */
public class testExcel {

    public static final String PATH_FORMATO_5 = "D:\\CLARIDAD3\\PRUEBA\\Formato_5u.xlsx";
    public static final String PATH_FORMATO_6 = "D:\\CLARIDAD3\\PRUEBA\\Formato_6u.xlsx";
    public static final String PATH_RUC = "https://192.168.48.27:8181/SunatConsultaWS-2/consultaRuc?sRuc=";
    public static final String PATH_CLARIDAD = "http://localhost:8080/claridadCandidato/";    
    
        
    public static void main(String[] args) {
        try {
            String path = testExcel.class.getResource("/formatos/Formato_5.xlsx").toURI().getPath();               
//            try {
//                PreparedStatement pstmt = null;
//                ResultSet rs = null;
//                Conexion con = new Conexion();
//                String SQL = "SELECT * FROM TAB_PERIODO";
//                con.getConexion().setAutoCommit(false);
//                pstmt = con.getConexion().prepareStatement(SQL);
//                rs = pstmt.executeQuery();
//                con.getConexion().commit();
//                
//                while (rs.next()) {
//                    System.out.println(rs.getString("C_DOCUMENTO_RESOLUCION"));                    
//                }                                
//            } catch (Exception e) {
//            }
            
            int tipoFormato = FormatoEnum.FORMATO_5.getId();
            int candidato = 12375;
            int organnizacion = 1;
            
            XSSFWorkbook file = new XSSFWorkbook(new FileInputStream(PATH_FORMATO_5));
            IFactoryService factory = new FactoryService();            
            IExcelXSSFValidatorService excelValidator = factory.readerXSSF(file, tipoFormato, PATH_RUC, PATH_CLARIDAD, organnizacion, candidato);
            //IExcelXSSFValidatorService excelValidator = factory.validateExcelXSSF(file, tipoFormato, PATH_RUC, PATH_CLARIDAD, candidato);
            
            String responseJson = excelValidator.validate();
            System.out.println(responseJson);
            
        } catch (Exception e) {
            System.out.println("ADVERTENCIA:"+e.getMessage());
        }
    }
       
}
