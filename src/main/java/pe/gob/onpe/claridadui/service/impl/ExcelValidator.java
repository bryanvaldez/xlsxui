/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pe.gob.onpe.claridadui.Constants.Validaciones;
import pe.gob.onpe.claridadui.enums.FormatoEnum;
import pe.gob.onpe.claridadui.model.DetalleFormato;
import pe.gob.onpe.claridadui.model.Formato;
import pe.gob.onpe.claridadui.util.ExcelUtil;

/**
 *
 * @author bvaldez
 */
public class ExcelValidator extends ExcelUtil{
    
    public final XSSFWorkbook workbook;
    public final int type;
    
    public boolean validExcel = true;
    public boolean validData = true;
    
    public String msjValidExcel = "";
    public String msjValidData = "";    
        
    public static final String PATH_OBSERVATION_INCOME = "D:\\CLARIDAD3\\OBSERVACIONES\\INGRESOS\\prueba.xlsx";
    public static final String PATH_OBSERVATION_EXPENSES = "D:\\CLARIDAD3\\OBSERVACIONES\\GASTOS\\prueba.xlsx";
    
    
    public String pathRuc;    
    public String pathClaridad; 
    public int candidato; 
    
    public DateFormat df = new SimpleDateFormat("dd/MM/yyyy");    
    
    public ExcelValidator(XSSFWorkbook workbook, int type, String pathRuc, String pathClaridad, int candidato){
        this.workbook = workbook;
        this.type = type;
        this.pathRuc = pathRuc;
        this.pathClaridad  = pathClaridad;        
        this.candidato = candidato;
    }
    
    public boolean saveFileObservation(XSSFWorkbook workbook, Formato formato) {
        try {
            String path = "";
            
            if(formato.getId() == FormatoEnum.FORMATO_5.getId()){
                path = PATH_OBSERVATION_INCOME;
            }
            if(formato.getId() == FormatoEnum.FORMATO_6.getId()){
                path = PATH_OBSERVATION_EXPENSES;
            }            
            
            File fileExcelResultado = new File(path);
            OutputStream outputStream = new FileOutputStream(fileExcelResultado);
            workbook.write(outputStream);
            outputStream.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }    
    
}
