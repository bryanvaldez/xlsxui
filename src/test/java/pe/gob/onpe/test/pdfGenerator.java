/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.test;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import pe.gob.onpe.claridadui.service.iface.IFactoryService;
import pe.gob.onpe.claridadui.service.iface.IPdfExportService;
import pe.gob.onpe.claridadui.service.impl.FactoryService;

/**
 *
 * @author Bryan Luis Valdez Jara <ibryan.valdez@gmail.com>
 */
public class pdfGenerator {

    public static final String PATH = "D:\\CLARIDAD3\\PDF\\test.pdf";

    public static void main(String[] args) {
        try {
            IFactoryService factory = new FactoryService();
            IPdfExportService pdfExport = factory.exportPdf(PATH, 0);
            Document document = pdfExport.export();
            document.close();
            
        } catch (Exception e) {
            
        }

    }

    
    

    
    
}
