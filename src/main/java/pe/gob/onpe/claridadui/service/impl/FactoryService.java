/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.service.impl;

import com.itextpdf.text.Document;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pe.gob.onpe.claridadui.service.iface.IExcelExportService;
import pe.gob.onpe.claridadui.service.iface.IFactoryService;
import pe.gob.onpe.claridadui.service.iface.IExcelXSSFValidatorService;
import pe.gob.onpe.claridadui.service.iface.IPdfExportService;

/**
 *
 * @author bvaldez
 */
public class FactoryService implements IFactoryService{

    @Override
    public IExcelXSSFValidatorService validateExcelXSSF(XSSFWorkbook file, int type, String pathRuc, String pathClaridad, int candidato) {
        return new ExcelFromXSSF(file, type, pathRuc, pathClaridad, candidato);
    }

    @Override
    public IExcelExportService exportExcelXSSF(int type, String pathFormato, int candidato) {
        return new ExcelExport(type, pathFormato, candidato);
    }

    @Override
    public IPdfExportService exportPdf(String path, int candidato) {
        return new PdfExport(path, candidato);
    }

    @Override
    public IExcelXSSFValidatorService readerXSSF(XSSFWorkbook workbook, int typeFormat, String serviceRuc, String pathServer, int codeOrganization, int codeCandidate) {
        return new XLSX_Read(workbook, typeFormat, serviceRuc, pathServer, codeOrganization, codeCandidate);
    }

}
