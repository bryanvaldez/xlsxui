/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pe.gob.onpe.claridadui.util.ExcelUtil;

/**
 *
 * @author Bryan Luis Valdez Jara <ibryan.valdez@gmail.com>
 */
public class XLSX_Build extends ExcelUtil{
    
    public String token = java.util.Base64.getUrlEncoder().encodeToString("0NP3_CL4R1D4D".getBytes());    
    public final XSSFWorkbook workbook;
    public final int typeFormat;
    public final String serviceRuc;
    public final String pathServer;
    public final String pathObs;
    public final int codeOrganization;
    public final int codeCandidate;
    public final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    public static final String PATH_OBSERVATION_INCOME = "D:\\CLARIDAD3\\OBSERVACIONES\\INGRESOS\\prueba.xlsx";
    public static final String PATH_OBSERVATION_EXPENSES = "D:\\CLARIDAD3\\OBSERVACIONES\\GASTOS\\prueba.xlsx";    
        
    public XLSX_Build(XSSFWorkbook workbook, int typeFormat, String serviceRuc, String pathServer, int codeOrganization, int codeCandidate, String pathObs){
        this.workbook = workbook;
        this.typeFormat = typeFormat;
        this.serviceRuc = serviceRuc;
        this.pathServer  = pathServer;        
        this.codeOrganization = codeOrganization;   
        this.codeCandidate = codeCandidate;
        this.pathObs = pathObs;
    }
    
    
    
    
}
