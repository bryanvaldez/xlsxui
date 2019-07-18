/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.service.impl;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.RomanList;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import pe.gob.onpe.claridadui.model.DetalleInforme;
import pe.gob.onpe.claridadui.service.iface.IFormatoService;
import pe.gob.onpe.claridadui.service.iface.IPdfExportService;

/**
 *
 * @author Bryan Luis Valdez Jara <ibryan.valdez@gmail.com>
 */
public class PdfExport implements IPdfExportService{
    
    public final Document document = new Document();
    public final String path;
    public final int candidato;
    
    public final Float widthPage = PageSize.LETTER.getWidth();// 612
    public final Float heightPage = PageSize.LETTER.getHeight();// 792 
    
    public final Float mLeft = 85f;   
    public final Float mRight = 85f;   
    public final Float mTop = 92.13f;    
    public final Float mBot = 92.13f;

    public final Float spacing = 10f;
    public final Float indent = 30f;
    
    public int countPage = 0;    
    
    BaseColor blue = WebColors.getRGBColor("#2e75b6");
    BaseColor gray = WebColors.getRGBColor("#d9d9d9");
    
    
    public PdfExport(String path, int candidato){
        this.path = path;
        this.candidato = candidato;
    }    

    @Override
    public Document export() {
        try {         
            FontFactory.register(PdfExport.class.getResource("/fuentes/arial.ttf").toURI().getPath(), "arial");            
            setPage();                             
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));            
            createHeader(writer);
            createFooter(writer);                        
            document.open(); 
            createIntro(writer);            
            createBody(writer);

        } catch (Exception e) {            
        }        
        return document;
    }        
    
                    
    
    private void createIntro(PdfWriter writer){
        try {
            PdfContentByte canvas = writer.getDirectContentUnder();            
            IFormatoService factory  = new FormatoService();  
            java.util.List<DetalleInforme> data = factory.getDataInforme(11);    
                
            Float width = widthPage-(mLeft+ mRight);
            
            Font f1 = new Font(FontFamily.UNDEFINED, 16, Font.BOLD);          
            Paragraph p1 = new Paragraph(data.get(0).getContenido(), f1);
                    
            Font f2 = new Font(FontFamily.UNDEFINED, 12, Font.BOLD);
            Paragraph p2 = new Paragraph(data.get(1).getContenido(), f2);            
            
            Font f3 = new Font(FontFamily.UNDEFINED, 10, Font.ITALIC);
            Paragraph p3 = new Paragraph(data.get(2).getContenido(), f3);                
            
            PdfPCell cell1 = new PdfPCell(p1);        
            cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);      
            cell1.setBorderWidth(Rectangle.NO_BORDER);              
            
            PdfPCell cell2 = new PdfPCell(p2);
            cell2.setVerticalAlignment(Element.ALIGN_TOP);
            cell2.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
            cell2.setFixedHeight(100F);       
            cell2.setBorderWidth(Rectangle.NO_BORDER);     
            
            PdfPCell cell3 = new PdfPCell(p3);
            cell3.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setFixedHeight(60F);       
            cell3.setBorderWidth(Rectangle.NO_BORDER);      
            
            PdfPCell cell4 = new PdfPCell();
            cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell4.setFixedHeight(heightPage-(120f));       
            cell4.setBorderWidth(Rectangle.NO_BORDER);   
            cell4.setBackgroundColor(blue);
            
            PdfPCell cell5 = new PdfPCell();
            cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell5.setFixedHeight(15f);       
            cell5.setBorderWidth(Rectangle.NO_BORDER);   

            PdfPCell cell6 = new PdfPCell();
            cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell6.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell6.setFixedHeight(20f);       
            cell6.setBorderWidth(Rectangle.NO_BORDER);   
            cell6.setBackgroundColor(blue);            
                        
            PdfPTable bloque1 = new PdfPTable(1);
            bloque1.setTotalWidth(new float[]{width});
            bloque1.addCell(cell1);   
            bloque1.completeRow();
            bloque1.writeSelectedRows(0, -1, mLeft, heightPage-110f, canvas);  
                        
            PdfPTable bloque2 = new PdfPTable(1);
            bloque2.setTotalWidth(new float[]{width});                     
            bloque2.addCell(cell2);  
            bloque2.addCell(cell3);
            bloque2.completeRow();
            bloque2.writeSelectedRows(0, -1, mLeft, mBot+150F, canvas);     
            
            PdfPTable bloque3 = new PdfPTable(1);
            bloque3.setTotalWidth(new float[]{20f});                     
            bloque3.addCell(cell4);  
            bloque3.addCell(cell5);
            bloque3.addCell(cell6);
            bloque3.completeRow();
            bloque3.writeSelectedRows(0, -1, mLeft-50f, heightPage-30f, canvas);  

            document.newPage();            
            
        } catch (Exception e) {
        }           
    }   
    
    private void createBody(PdfWriter writer){
        try {
            IFormatoService factory  = new FormatoService();
            java.util.List<DetalleInforme> data = factory.getDataInforme(11);
            
            
            Float y = heightPage-100f;
            Float height = 25f;    
            
            Font f1 = new Font(FontFamily.UNDEFINED, 14, Font.BOLD);
            Paragraph p1 = new Paragraph(data.get(0).getContenido(), f1);

            Font f2 = new Font(FontFamily.UNDEFINED, 12, Font.BOLD);
            Paragraph p2 = new Paragraph(data.get(1).getContenido(), f2);                                                 
            
            PdfPCell cell1 = new PdfPCell(p1);        
            cell1.setVerticalAlignment(Element.ALIGN_TOP);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setFixedHeight(height);       
            cell1.setBorderWidth(Rectangle.NO_BORDER);                
            
            PdfPCell cell2 = new PdfPCell(p2);        
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setFixedHeight(110f);       
            cell2.setBackgroundColor(gray);
            cell2.setBorderWidth(1);          
                        
            PdfPTable t1 = new PdfPTable(1);
            t1.setWidthPercentage(100);
            t1.addCell(cell1);  
            t1.addCell(cell2);
            t1.completeRow();
            document.add(t1);      
            
            
            int count = 0;            
            java.util.List<DetalleInforme> subtitles = factory.getDataInforme(count);             
            
            RomanList list = new RomanList();  
            list.setAutoindent(false);
            list.setSymbolIndent(indent);            
            for (DetalleInforme subtitle : subtitles) {
                count++;
                java.util.List<DetalleInforme> items = factory.getDataInforme(count);
                List subList = new List(List.ORDERED);
                subList.setAutoindent(false);
                subList.setIndentationLeft(-indent);
                subList.setSymbolIndent(indent); 
                subList.setPreSymbol(count + ".");               
                for (DetalleInforme item : items) {
                    Font f4 = new Font(FontFamily.UNDEFINED, 11, Font.NORMAL);
                    Paragraph p4 = new Paragraph(item.getContenido(), f4);
                    p4.setSpacingBefore(spacing);
                    p4.setAlignment(Element.ALIGN_JUSTIFIED);
                    ListItem listItem = new ListItem(p4);                   
                    subList.add(listItem);
                }    
                Font f3 = new Font(FontFamily.UNDEFINED, 11, Font.BOLD);
                ListItem item = new ListItem(new Paragraph(subtitle.getContenido(), f3));            
                item.setSpacingBefore(spacing);                  
                list.add(item);
                list.add(subList);

            }                      
            document.add(list);             
            
            java.util.List<DetalleInforme> sfin = factory.getDataInforme(12); 
            Font f3 = new Font(FontFamily.UNDEFINED, 11, Font.NORMAL);
            Paragraph fin = new Paragraph(sfin.get(0).getContenido() , f3); 
            fin.setSpacingBefore(spacing);
            fin.setAlignment(Element.ALIGN_RIGHT);            
            document.add(fin); 
            
        } catch (Exception e) {
        }       
    }      
    


    public PdfPCell createLogoTitle(){
        PdfPCell cell = new PdfPCell();
        try {
            String path = PdfExport.class.getResource("/imagenes/logoBryan.png").toURI().getPath();
            Image image = Image.getInstance(path);
            cell = new PdfPCell(image);
            cell.setFixedHeight(53f);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT); 
            cell.setBorder(Rectangle.NO_BORDER);
        } catch (Exception e) {
        }   
        return cell; 
    }         
    private void createHeader(PdfWriter writer){
        HeaderTable event = new HeaderTable();
        writer.setPageEvent(event);                     
    }            
    private void createFooter(PdfWriter writer){
        FooterTable event = new FooterTable();
        writer.setPageEvent(event);         
    }                      
    public class HeaderTable extends PdfPageEventHelper {

        protected float tableHeight;
        PdfPTable header = new PdfPTable(1);
        
        public HeaderTable() {            
            header.setTotalWidth(widthPage-(mLeft+ mRight));            
            header.addCell(createLogoTitle());                        
        }

        public void onEndPage(PdfWriter writer, Document document) {      
            header.writeSelectedRows(0, -1, mLeft, heightPage-30f, writer.getDirectContent());                
        }
    }        
    public class FooterTable extends PdfPageEventHelper {
        protected PdfPTable table;
        protected float tableHeight;
        
        public FooterTable() {
        }
        public float getTableHeight() {
            return tableHeight;
        }
        public void onEndPage(PdfWriter writer, Document document) {
            if(document.getPageNumber() == 1){
                          
            }else{
                Font f1 = new Font();
                f1.setSize(9);                
                Paragraph p = new Paragraph((document.getPageNumber()-1)+"", f1);
                PdfPTable table = new PdfPTable(1);
                table.setTotalWidth(widthPage-(mLeft+ mRight));
                
                PdfPCell cell = new PdfPCell(p);        
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setFixedHeight(mBot);       
                cell.setBorderWidth(0);                
                
                table.addCell(cell);           
                table.writeSelectedRows(0, -1, mLeft, mBot, writer.getDirectContent());
                
                
                if(document.getPageNumber() == 6){
                    IFormatoService factory  = new FormatoService();
                    java.util.List<DetalleInforme> reference = factory.getDataInforme(13);
                    PdfPTable column = new PdfPTable(3);
                    column.setTotalWidth(widthPage-(mLeft+ mRight));

                    for (int i = 1; i <= 3; i++) {                
                        PdfPCell item = new PdfPCell();
                        item.setFixedHeight(7f);       
                        item.setBorderWidth(Rectangle.NO_BORDER);
                        if(i==1){item.setBorderWidthTop(1);}           
                        column.addCell(item);                
                    }

                    for (DetalleInforme item : reference) { 
                        Font fi = new Font(FontFamily.UNDEFINED, 7, Font.BOLD);
                        if(item.getType()==2){
                            fi.setStyle(Font.BOLDITALIC);
                        }                        
                        if(item.getType()>2){
                            fi.setStyle(Font.NORMAL);
                        }
                        Paragraph pi = new Paragraph(item.getContenido(), fi);                
                        PdfPCell ritem = new PdfPCell(pi);
                        ritem.setColspan(3);      
                        if(item.getType()!=4){
                            ritem.setPaddingTop(0f);                            
                        }                        
                        ritem.setBorderWidth(Rectangle.NO_BORDER);          
                        column.addCell(ritem);                   
                    }                   
                    column.writeSelectedRows(0, -1, mLeft, mBot+50f, writer.getDirectContent());                    
                }
                
                
            }
        }
    }        
    private void setPage() { 
        Rectangle size = new Rectangle(widthPage, heightPage); 
        document.setPageSize(size);                 
        document.setMargins(mLeft, mRight, mTop, mBot);
    }      
}
