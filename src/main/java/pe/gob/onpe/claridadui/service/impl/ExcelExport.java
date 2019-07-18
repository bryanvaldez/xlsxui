/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pe.gob.onpe.claridadui.Constants.Validaciones;
import pe.gob.onpe.claridadui.enums.FormatoEnum;
import pe.gob.onpe.claridadui.model.Formato;
import pe.gob.onpe.claridadui.service.iface.IExcelExportService;
import pe.gob.onpe.claridadui.service.iface.IFormatoService;

/**
 *
 * @author bvaldez
 */
public class ExcelExport implements IExcelExportService{
    
    public final int type;
    public int candidato;   
    public String pathFormato; 

    public ExcelExport(int type, String pathFormato, int candidato){
        this.type = type;      
        this.candidato = candidato;
        this.pathFormato = pathFormato;
    }    
    
    @Override
    public XSSFWorkbook export() {
        
        switch (type) {
            case Validaciones.FORMAT_5:     

                exportDataFormatIncome();
                break;
            case Validaciones.FORMAT_6:
                //exportDataFormatExpenses();
                break;
        } 
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void exportDataFormatIncome(){
        
        try {
            Locale.setDefault(Locale.US); 
            String path = pathFormato + "Formato5_empty.xlsx";               
            File fileExcel = new File(path);
            FileInputStream inputStream = new FileInputStream(fileExcel);
            XSSFWorkbook wb = new XSSFWorkbook(inputStream);
            
            IFormatoService factory  = new FormatoService();            
            Formato formato = factory.getFormato(type);   
            

//            createIncomeHeader(wb,formato, candidato, true);
//            createIncomeFooter(wb,candidato, true);
//            createIncomeRowData(wb,listIngresos);
//
//            response.setContentType("application/vnd.ms-excel");
//            response.setHeader("Content-Disposition", "attachment; filename=" + "F7_CEDULA_INGRESOS_FP.xlsx");
//            wb.write(response.getOutputStream());              
//                 
        } catch (IOException e) {
        }    
    }    
    
    
}
