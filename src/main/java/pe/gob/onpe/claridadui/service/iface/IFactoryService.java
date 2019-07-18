/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.service.iface;

import com.itextpdf.text.Document;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author bvaldez
 */
public interface IFactoryService {
    public IExcelXSSFValidatorService validateExcelXSSF(XSSFWorkbook file, int type, String pathRuc, String pathClaridad, int candidato);   
    public IExcelExportService exportExcelXSSF(int type, String pathFormato, int candidato);    
    public IPdfExportService exportPdf(String path, int candidato);
    
    public IExcelXSSFValidatorService readerXSSF(XSSFWorkbook workbook, int typeFormat, String serviceRuc, String pathServer, int codeOrganization, int codeCandidate);      
}
