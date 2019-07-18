/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.test;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pe.gob.onpe.claridadui.enums.FormatoEnum;
import pe.gob.onpe.claridadui.service.iface.IExcelExportService;
import pe.gob.onpe.claridadui.service.iface.IFactoryService;
import pe.gob.onpe.claridadui.service.impl.FactoryService;

/**
 *
 * @author bvaldez
 */
public class testExcelExport {
    
    public static final String PATH_FORMATO = "D:\\CLARIDAD3\\FORMATOS\\";
    
    public static void main(String[] args) {
        try {            
            int tipoFormato = FormatoEnum.FORMATO_5.getId();
            int candidato = 12375;
            
            IFactoryService factory = new FactoryService();
            IExcelExportService excel = factory.exportExcelXSSF(tipoFormato, PATH_FORMATO, candidato);
            
            XSSFWorkbook excelGenerate = excel.export();
            
        } catch (Exception e) {
            System.out.println("ADVERTENCIA:"+e.getMessage());
        }
    }
    
}
