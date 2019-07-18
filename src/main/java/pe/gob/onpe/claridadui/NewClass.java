///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package pe.gob.onpe.ropc.helper;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.itextpdf.text.BaseColor;
//import com.itextpdf.text.Chunk;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Element;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.FontFactory;
//import com.itextpdf.text.Image;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.Rectangle;
//import com.itextpdf.text.pdf.Barcode128;
//import com.itextpdf.text.pdf.BaseFont;
//import com.itextpdf.text.pdf.PdfContentByte;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPTable;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Properties;
//import javax.annotation.PostConstruct;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.UrlResource;
//import pe.gob.onpe.ropc.constants.ConstanteMedida;
//import pe.gob.onpe.ropc.enums.EstadoRegistroCandEnum;
//import pe.gob.onpe.ropc.enums.EstadoRegistroOrgEnum;
//import pe.gob.onpe.ropc.service.dto.MedidaRequestDTO;
//import com.itextpdf.text.pdf.CMYKColor;
//import com.itextpdf.text.pdf.ColumnText;
//import com.itextpdf.text.pdf.ICC_Profile;
//import com.itextpdf.text.pdf.PdfAWriter;
//import com.itextpdf.text.pdf.PdfName;
//import com.itextpdf.text.pdf.PdfString;
//import java.io.File;
//import java.io.FileInputStream;
//import static java.lang.Math.toIntExact;
//import pe.gob.onpe.ropc.domain.ProcesoElectoral;
//
///**
// *
// * @author bvaldez
// */
//public class FormatoGenerator {
//
//    @javax.annotation.Resource(name = "ropcProperties", mappedName = "resource/ropc_properties")
//    private Properties ropcProperties;
//    public final List<Path> PATH_LOCATION_FILES = new ArrayList<>();
//    public ProcesoElectoral oProceso = new ProcesoElectoral();
//    public String DOCUMENT_NAME = "";
//    public String ROPC_VIEW = "";
//    public CMYKColor ROPC_BLACK = new CMYKColor(0f, 0f, 0f, 1f); 
//    public CMYKColor ROPC_WHITE = new CMYKColor(0f, 0f, 0f, 0f);
//    public CMYKColor ROPC_RED = new CMYKColor(0f, 1f, 0.8f, 0.15f); 
//    public CMYKColor ROPC_GRAY = new CMYKColor(0f, 0f, 0f, 0.30f);
//    public Float ROPC_LINE = 1.5f;    
//
//    @PostConstruct
//    public void initialize() {
//        PATH_LOCATION_FILES.add(Paths.get(ropcProperties.getProperty("path_uploads") + "simbolos"));
//        PATH_LOCATION_FILES.add(Paths.get(ropcProperties.getProperty("path_uploads") + "candidatos"));
//        PATH_LOCATION_FILES.add(Paths.get(ropcProperties.getProperty("path_uploads") + "plantillas")); 
//        PATH_LOCATION_FILES.add(Paths.get(ropcProperties.getProperty("path_uploads") + "numero_lista"));
//        PATH_LOCATION_FILES.add(Paths.get(ropcProperties.getProperty("path_uploads") + "fuentes")); 
//    } 
//    
//    public void initParam(JsonObject jProceso, String tipoVista){
//        
//        FontFactory.register(PATH_LOCATION_FILES.get(4) + "/arial.ttf", "arial");
//        FontFactory.register(PATH_LOCATION_FILES.get(4) + "/ARIALN.TTF", "arialnarrow");
//        FontFactory.register(PATH_LOCATION_FILES.get(4) + "/hum777k_0.ttf", "hum777k_0");
//        FontFactory.register(PATH_LOCATION_FILES.get(4) + "/Humnst777_BT.ttf", "Humnst777_BT");
//        FontFactory.register(PATH_LOCATION_FILES.get(4) + "/arialbd.ttf", "arialbd");
//        FontFactory.register(PATH_LOCATION_FILES.get(4) + "/hum531k_0.ttf", "hum531k_0");
//        
//               
//        ROPC_VIEW = tipoVista;
//        oProceso.setCodigo(jProceso.get("C_PROCESO_PK").getAsString()); 
//        oProceso.setNombre(jProceso.get("C_NOMBRE").getAsString());
//        oProceso.setSegundaVuelta((jProceso.get("F_SEGUNDA_VUELTA").getAsInt() != 0));
//        oProceso.setSiglas(jProceso.get("C_SIGLAS").getAsString());
//        oProceso.setTipo(jProceso.get("C_TIPO_PROCESO").getAsString());
//    }
//    
//    public void setInfoDocument(Document document){
//        document.addTitle(oProceso.getNombre());
//        document.addAuthor("Oficina de Procesos Electorales, ONPE");
//        document.addLanguage("en-US"); 
//        if(ROPC_VIEW.equalsIgnoreCase(ConstanteMedida.V_FORMATO_PRELIMINAR)){ 
//            document.addSubject("Vista Preliminar Documento: "+DOCUMENT_NAME);
//        }else if(ROPC_VIEW.equalsIgnoreCase(ConstanteMedida.V_FORMATO_PREVIO)){ 
//            document.addSubject("Vista Previa Documento: "+DOCUMENT_NAME);
//        }else if(ROPC_VIEW.equalsIgnoreCase(ConstanteMedida.V_FORMATO_DIFUSION)){
//            document.addSubject("Documento de Difusión: "+DOCUMENT_NAME);
//        }else if(ROPC_VIEW.equalsIgnoreCase(ConstanteMedida.V_FORMATO_DEFINITIVO)){
//            document.addSubject("Documento de Sufragio: "+DOCUMENT_NAME);
//        }
//        document.addKeywords(DOCUMENT_NAME);
//        document.addCreator("ROPC.V3");              
//    }
//    
//    public void setIccProfile(PdfAWriter writer) throws IOException, DocumentException {  
//        String pathFont = PATH_LOCATION_FILES.get(4) + "/icc.icm";      
//        File file = new File(pathFont);
//        ICC_Profile icc = ICC_Profile.getInstance(new FileInputStream(file));        
//        writer.setOutputIntents("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", icc);         
//    }
//    
//    public String getAmbitoByEleccion(String proceso, String tipoEleccion){
//        String response = null;
//        if (proceso.equalsIgnoreCase("03")) {
//            switch (tipoEleccion) {
//                case ConstanteMedida.E_REGIONAL:
//                    response = ConstanteMedida.C_AMBITO_REG;
//                    break;
//                case ConstanteMedida.E_REGIONAL_CONCEJAL:
//                    response = ConstanteMedida.C_AMBITO_REG;
//                    break;
//                case ConstanteMedida.E_PROVINCIAL_DISTRITAL:
//                    response = ConstanteMedida.C_AMBITO_DIS;
//                    break;
//                case ConstanteMedida.E_PROVINCIAL:
//                    response = ConstanteMedida.C_AMBITO_PRO;
//                    break;
//            }
//        }
//        return response;
//    }    
//    public void setHeader(PdfAWriter writer, String text, Float with, Float height, Float fontSize, Float x, Float y) throws IOException, DocumentException {
//        Font font = FontFactory.getFont("arialbd", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, fontSize, Font.NORMAL, ROPC_BLACK);         
//        Chunk textHeader = new Chunk(text, font);
//        Paragraph p = new Paragraph(textHeader);   
//        PdfPTable firstColumn = new PdfPTable(1);
//        firstColumn.setTotalWidth(new float[]{with});
//        firstColumn.addCell(createTextCellHeader(p, height));
//        PdfContentByte canvas = writer.getDirectContentUnder();
//        firstColumn.completeRow();
//        firstColumn.writeSelectedRows(0, -1, x, y, canvas);
//    }  
//    public void setFooter(PdfAWriter writer, String text, Float with, Float height, Float fontSize, Float x, Float y) throws IOException, DocumentException {
//        Font font = FontFactory.getFont("hum531k_0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, fontSize, Font.BOLD, ROPC_WHITE); 
//        Chunk textHeader = new Chunk(text, font);
//        Paragraph p = new Paragraph(textHeader);  
//        PdfPTable firstColumn = new PdfPTable(1);
//        firstColumn.setTotalWidth(new float[]{with});
//        firstColumn.addCell(createTextCellFooter(p, height));
//        PdfContentByte canvas = writer.getDirectContentUnder();
//        firstColumn.completeRow();
//        firstColumn.writeSelectedRows(0, -1, x, y, canvas);
//    }         
//    public String getNameDocument(String name, String tipoEleccion, String tipoVista, String version){
//        String response = null;   
//        version = String.format("%03d", Integer.parseInt(version)); 
//        if(oProceso.getTipo().equalsIgnoreCase(ConstanteMedida.C_TIPO_PROC_ERM)){
//            switch (tipoEleccion) {
//                case ConstanteMedida.E_REGIONAL: 
//                    response = getDescTypeView(tipoVista)+"R_"+name+"_V"+version+".pdf";        
//                    break;
//                case ConstanteMedida.E_REGIONAL_CONCEJAL:
//                    response = getDescTypeView(tipoVista)+"RP_"+name+"_V"+version+".pdf";  
//                    break;
//                case ConstanteMedida.E_PROVINCIAL_DISTRITAL:
//                    response = getDescTypeView(tipoVista)+"P_"+name+"_V"+version+".pdf";   
//                    break;
//                case ConstanteMedida.E_PROVINCIAL:
//                    response = getDescTypeView(tipoVista)+"P_"+name+"_V"+version+".pdf";    
//                    break;
//            }                
//        }  
//        DOCUMENT_NAME = response;
//        return response;          
//   } 
//    public String getCustomBarCode(String code, String tipoEleccion, String tipoFormato){
//        String response = null;
//        switch (tipoEleccion) {
//            case ConstanteMedida.E_REGIONAL:
//                response = getCodeByFormat(code, tipoFormato)+"G"; 
//                break;
//            case ConstanteMedida.E_REGIONAL_CONCEJAL:
//                response = getCodeByFormat(code, tipoFormato)+"R"; 
//                if(tipoFormato.equalsIgnoreCase(ConstanteMedida.C_TIPO_CARTEL))
//                response = getCodeByFormat(code, tipoFormato)+"C"; 
//                break;
//            case ConstanteMedida.E_PROVINCIAL_DISTRITAL:
//                response = getCodeByFormat(code, tipoFormato)+"D";  
//                break;
//            case ConstanteMedida.E_PROVINCIAL:
//                response = getCodeByFormat(code, tipoFormato)+"P";   
//                break;
//        }        
//        return response;          
//   }
//    private String getCodeByFormat(String code, String typeFormat){
//        switch (typeFormat) {
//            case ConstanteMedida.C_TIPO_BRAILLE:
//                code = "B"+code;        
//                break;
//            case ConstanteMedida.C_TIPO_CARTEL:
//                code = "L"+code;  
//                break;
//            case ConstanteMedida.C_TIPO_CEDULA:
//                code = "C"+code;  
//                break;
//        }
//        return code;
//    }         
//    public MedidaRequestDTO getMedida(String proceso, String tipoEleccion, Integer numOrg) {
//        MedidaRequestDTO request = new MedidaRequestDTO();
//        request.setProceso(proceso);
//        request.setEleccion(tipoEleccion);
//        request.setOrganizaciones(numOrg); 
//        return request;
//    }
//    public Image createBarCode(PdfAWriter writer, String code, Float height) {
//        Barcode128 barcode = new Barcode128();
//        barcode.setCodeType(Barcode128.CODE128);
//        barcode.setCode(code);
//        barcode.setBarHeight(height);
//        barcode.setFont(null);
//        Image image = barcode.createImageWithBarcode(writer.getDirectContent(), ROPC_BLACK, ROPC_BLACK);
//        image.setAccessibleAttribute(PdfName.ALT, new PdfString("ROPC"));
//        return image;
//    }
//    public float mmToPt(float mm) {
//        return ((72f * mm) / 25.4f);
//    }
//    public void setPageSize(Document document, Float width, Float height) {
//        Rectangle size = new Rectangle(width, height);
//        document.setMargins(0, 0, 0, 0);
//        document.setPageSize(size);
//        document.newPage();
//    }
//    public void setPageTemplate(PdfAWriter writer, String path, Float width, Float height) throws IOException, DocumentException {
//        PdfContentByte canvas = writer.getDirectContentUnder();
//        Image image = Image.getInstance(path);
//        image.setAccessibleAttribute(PdfName.ALT, new PdfString("ROPC"));
//        image.setAbsolutePosition(0, 0);
//        image.scaleToFit(width, height);
//        canvas.addImage(image);
//    }
//    public void setPageTemplateNoContent(PdfAWriter writer, Float width, Float height) throws IOException, DocumentException {
//        Resource file = getImage("nocontent", PATH_LOCATION_FILES.get(2));
//        Image image = Image.getInstance(file.getURL());       
//        image.setAccessibleAttribute(PdfName.ALT, new PdfString("ROPC"));
//        PdfContentByte canvas = writer.getDirectContentUnder();
//        image.setAbsolutePosition(0, 0);
//        image.scaleToFit(width, height);
//        canvas.addImage(image);
//    }    
//    public String getAmbito(String ubigeo) {
//        String ambito;
//        if (ubigeo.substring(2, 6).equalsIgnoreCase("0000")) {
//            ambito = ConstanteMedida.C_AMBITO_REG;
//        } else if (ubigeo.substring(4, 6).equalsIgnoreCase("00")) {
//            ambito = ConstanteMedida.C_AMBITO_PRO;
//        } else {
//            ambito = ConstanteMedida.C_AMBITO_DIS;
//        }
//        return ambito;
//    }
//    public String getTypeEleccionByAmbito(String tipoProceso, String ambito) {
//        String response = null;        
//        if(oProceso.getTipo().equalsIgnoreCase(ConstanteMedida.C_TIPO_PROC_ERM)){
//            switch (ambito) {
//                case ConstanteMedida.C_AMBITO_REG:
//                    response = ConstanteMedida.E_REGIONAL;
//                    break;
//                case ConstanteMedida.C_AMBITO_PRO:
//                    response = ConstanteMedida.E_REGIONAL_CONCEJAL;
//                    break;
//                case ConstanteMedida.C_AMBITO_DIS:
//                    response = ConstanteMedida.E_PROVINCIAL_DISTRITAL;
//                    break;
//                case ConstanteMedida.C_AMBITO_DIS_CAP:
//                    response = ConstanteMedida.E_PROVINCIAL;
//                    break;
//            }              
//        }   
//        return response; 
//    }   
//    public String getDescTypeView(String tipoVista){
//        String response = null;
//        switch (tipoVista) {
//            case ConstanteMedida.V_FORMATO_PRELIMINAR:
//                response = "FPRELIMINAR_";
//                break;             
//            case ConstanteMedida.V_FORMATO_PREVIO:
//                response = "FPREVIO_";
//                break;
//            case ConstanteMedida.V_FORMATO_DIFUSION:
//                response = "FDIF_";
//                break;
//            case ConstanteMedida.V_FORMATO_DEFINITIVO:
//                response = "FDEF_";
//                break;
//        }    
//        return response;
//    }
//    public List getValidStatusCandidatos(Integer level) {
//        List response = new ArrayList();        
//        if(level == 0){
//            response = null;
//        }        
//        if(level >= 1){
//            String[] canceladas = EstadoRegistroCandEnum.CANCELADA.getParametros();      
//            for (String cancelada : canceladas) {
//                response.add(cancelada);
//            }            
//        }
//        if(level >= 2){
//            String[] pendientes = EstadoRegistroCandEnum.POR_CONFIRMAR.getParametros();
//            for (String pendiente : pendientes) {
//                response.add(pendiente);
//            }        
//        }                
//        return response;
//    }
//    public List getValidStatusOrganizaciones(Integer level) {
//        List response = new ArrayList();  
//        if(level == 0){
//            response = null;
//        }         
//        if(level >= 1){
//            String[] canceladas = EstadoRegistroOrgEnum.CANCELADO.getParametros();
//            for (String cancelada : canceladas) {
//                response.add(cancelada);
//            }            
//        }
//        if(level >= 2){
//            String[] pendientes = EstadoRegistroOrgEnum.POR_CONFIRMAR.getParametros();        
//            for (String pendiente : pendientes) {
//                response.add(pendiente);
//            }
//        }
//        return response;
//    }
//    public Integer getNumOrg(JsonArray jOrganizaciones) {
//        Integer response = 0;
//        if(oProceso.getTipo().equalsIgnoreCase(ConstanteMedida.C_TIPO_PROC_ERM)){
//            if(oProceso.isSegundaVuelta()){
//               response =  2;
//            }else{
//                if (jOrganizaciones != null) {          
//                    response =  jOrganizaciones.size();
//                } 
//            }            
//        }  
//        return response;
//    }
//    public Resource getImage(String filename, Path filePath) {
//        try {
//            Path file = filePath.resolve(filename);
//            Resource resource = new UrlResource(file.toUri());
//            if (resource.exists() || resource.isReadable()) {
//                return resource;
//            } else {
//                file = filePath.resolve("notfound");
//                resource = new UrlResource(file.toUri());
//                return resource;
//            }
//        } catch (MalformedURLException e) {
//            throw new RuntimeException("ImagenService: Error en pathLocation!");
//        }
//    }
//
//    //CEDULA BRAILLE
//    public void setHeaderColumn1(PdfAWriter writer, String text, Float fontSize, Float with, Float height, Float x, Float y) throws IOException, DocumentException {
//        Font font = FontFactory.getFont("hum777k_0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, fontSize, Font.NORMAL, ROPC_WHITE);        
//        Chunk textHeader = new Chunk(text, font);
//        Paragraph p = new Paragraph(textHeader);
//        PdfPTable firstColumn = new PdfPTable(1);
//        firstColumn.setTotalWidth(new float[]{with});
//        firstColumn.addCell(createTextCellSubHeader(p, height));
//        PdfContentByte canvas = writer.getDirectContentUnder();
//        firstColumn.completeRow();
//        firstColumn.writeSelectedRows(0, -1, x, y, canvas);
//    }
//    public PdfPCell createTextCell1(String text, Float padding, Float height, Float fontSize) {        
//        Font font = FontFactory.getFont("arialnarrow", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, fontSize, Font.BOLD, ROPC_BLACK); 
//        Chunk textHeader = new Chunk(text, font);
//        Float line = 1f;
//        Paragraph p = new Paragraph(textHeader);
//        PdfPCell cell = new PdfPCell(p);
//        if(ROPC_VIEW.equalsIgnoreCase(ConstanteMedida.V_FORMATO_PRELIMINAR) || ROPC_VIEW.equalsIgnoreCase(ConstanteMedida.V_FORMATO_PREVIO)){
//            cell.setBorderWidth(line);        
//            cell.setBorderColor(ROPC_BLACK);
//        }else{
//            cell.setBorder(Rectangle.NO_BORDER);
//        }
//        cell.setPaddingLeft(padding);
//        cell.setPaddingRight(padding);
//        cell.setFixedHeight(height);
//        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//        return cell;
//    }
//    
//    //ORDENAR
//    public PdfPCell createTextCell(String text, Float padding, Float height) {
//        Float line = 1.5f;
//        Paragraph p = new Paragraph(text);
//        PdfPCell cell = new PdfPCell(p);
//        cell.setBorderWidth(line);
//        cell.setPaddingLeft(padding);
//        cell.setPaddingRight(padding);
//        cell.setFixedHeight(height);
//        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//        return cell;
//    }    
//    public PdfPCell createTextCellCartel(String text, Float padding, Float height, Float fontSize) {      
//        Font font = FontFactory.getFont("arialnarrow", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, fontSize, Font.BOLD, ROPC_BLACK);       
//        Chunk textHeader = new Chunk(text, font);
//        Paragraph p = new Paragraph(textHeader);     
//        PdfPCell cell = new PdfPCell(p);        
//        if(ROPC_VIEW.equalsIgnoreCase(ConstanteMedida.V_FORMATO_PREVIO)){
//            cell.setBorderWidth(ROPC_LINE); 
//        }else{
//            cell.setBorder(Rectangle.NO_BORDER);
//        }      
//        cell.setPaddingLeft(padding);
//        cell.setPaddingRight(padding);
//        cell.setFixedHeight(height);
//        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//        return cell;
//    }      
//    //LABEL
//    public void setLabelPreview(PdfAWriter writer, Float x, Float y) throws IOException, DocumentException {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date date = new Date();
//        String sPreview = "Vista Previa \nFecha: " + dateFormat.format(date);
//        if(ROPC_VIEW.equalsIgnoreCase(ConstanteMedida.V_FORMATO_PRELIMINAR)){
//            sPreview = "Vista Preliminar \nFecha: " + dateFormat.format(date);            
//        }        
//            
//        Font font = FontFactory.getFont("arial", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 9f, Font.NORMAL, ROPC_BLACK);  
//        Chunk textHeader = new Chunk(sPreview, font);
//        Paragraph text = new Paragraph(textHeader);
//        PdfPTable tPreview = new PdfPTable(1);
//        tPreview.setTotalWidth(new float[]{200f});
//        tPreview.addCell(createTextCellPreview(text));
//        PdfContentByte canvas = writer.getDirectContentUnder();
//        tPreview.completeRow();
//        tPreview.writeSelectedRows(0, -1, x, y, canvas);
//    }
//    public void setLabelDifusion(PdfAWriter writer, Float x, Float y) throws IOException, DocumentException {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date date = new Date();
//        String sPreview = "Vista Previa \nFecha: " + dateFormat.format(date);
//               
//        Font font = FontFactory.getFont("arial", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 9f, Font.NORMAL, ROPC_RED);         
//        Chunk textHeader = new Chunk(sPreview, font);
//        Paragraph text = new Paragraph(textHeader);
//        PdfPTable tPreview = new PdfPTable(1);
//        tPreview.setTotalWidth(new float[]{200f});
//        tPreview.addCell(createTextCellPreview(text));
//        PdfContentByte canvas = writer.getDirectContentUnder();
//        tPreview.completeRow();
//        tPreview.writeSelectedRows(0, -1, x, y, canvas);
//    }
//    public void setTemplateDifusion(PdfAWriter writer, Float x, Float width, Float height) throws IOException, DocumentException {        
//        if(ROPC_VIEW.equalsIgnoreCase(ConstanteMedida.V_FORMATO_PREVIO)){
//            PdfContentByte canvas = writer.getDirectContentUnder();
//            String pathPreview = PATH_LOCATION_FILES.get(2) + "/inhabilitado";
//            Image imagePreview = Image.getInstance(pathPreview);
//            imagePreview.setAbsolutePosition(x, 0);
//            double aRad = Math.atan2(height, width);
//            Float angle = (float) Math.toDegrees(aRad);
//            imagePreview.setRotationDegrees(angle);
//            imagePreview.scaleToFit(width, height);
//            canvas.addImage(imagePreview);         
//        }else{
//            String sPreview = "INHABILITADO";
//            String pathFont = PATH_LOCATION_FILES.get(4) + "/ARIALN.TTF";  
//            Float size;
//            if(width<450){
//                size = 110f;
//            }else{
//                size = 140f;
//            }            
//            Font font = FontFactory.getFont("arialnarrow", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, size, Font.BOLD, ROPC_RED);               
//            Chunk textHeader = new Chunk(sPreview, font);
//            Paragraph text = new Paragraph(textHeader);            
//            double aRad = Math.atan2(height, width);
//            double angle = Math.toDegrees(aRad);
//            Integer rotate = toIntExact(Math.round(angle)); 
//            PdfContentByte canvas = writer.getDirectContentUnder();
//            canvas.moveTo(0, 0); 
//
//            ColumnText.showTextAligned(canvas,Element.ALIGN_CENTER, text, (width+70f)/2, (height-70f)/2, rotate);                 
//        }            
//    }
//    public void setLabelInformacion(PdfAWriter writer, Float fontSize, Float x, Float y) throws IOException, DocumentException {
//        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        Date date = new Date();
//        String sPreview = "Información al: " + dateFormat.format(date);
//        Font font = FontFactory.getFont("arialnarrow", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, fontSize, Font.NORMAL, ROPC_BLACK);               
//        Chunk textHeader = new Chunk(sPreview, font);
//        Paragraph text = new Paragraph(textHeader);
//        PdfPTable tPreview = new PdfPTable(1);
//        tPreview.setTotalWidth(new float[]{500f});
//        tPreview.addCell(createTextCellPreview(text));
//        PdfContentByte canvas = writer.getDirectContentUnder();
//        tPreview.completeRow();
//        tPreview.writeSelectedRows(0, -1, x, y, canvas);
//    }    
//
//    //CELL
//    public PdfPCell createEmptyCell(Float height) {
//        PdfPCell cell = new PdfPCell();
//        cell.setBorderWidth(0); 
//        if(ROPC_VIEW.equalsIgnoreCase(ConstanteMedida.V_FORMATO_PRELIMINAR)){ 
//            Float line = 1.5f;
//            cell.setBorderWidth(line);            
//        }
//        cell.setFixedHeight(height);
//        return cell;
//    }
//    public PdfPCell createTextCellPreview(Paragraph text) {
//        Paragraph p = new Paragraph(text);
//        PdfPCell cell = new PdfPCell(p);
//        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        cell.setBorderWidth(0);
//        return cell;
//    }
//    public PdfPCell createTextCellHeader(Paragraph text, Float height) {
//        Paragraph p = new Paragraph(text);
//        PdfPCell cell = new PdfPCell(p);
//        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setFixedHeight(height);
//        cell.setBorderWidth(0);
//        return cell;
//    }
//    public PdfPCell createTextCellFooter(Paragraph text, Float height) {
//        Paragraph p = new Paragraph(text);
//        PdfPCell cell = new PdfPCell(p);
//        cell.setPadding(0);
//        if(ROPC_VIEW.equalsIgnoreCase(ConstanteMedida.V_FORMATO_PRELIMINAR) || ROPC_VIEW.equalsIgnoreCase(ConstanteMedida.V_FORMATO_PREVIO)){
//            cell.setBackgroundColor(ROPC_GRAY);
//        }
//        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);        
//        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//        cell.setFixedHeight(height);
//        cell.setBorder(Rectangle.NO_BORDER);
//        cell.setPaddingRight(mmToPt(3f));
//        return cell;
//    }        
//    public PdfPCell createTextCellTitleOrg(Paragraph text, Float height) {
//        Float line = 1.5f;
//        Paragraph p = new Paragraph(text);
//        PdfPCell cell = new PdfPCell(p);
//        cell.setVerticalAlignment(Element.ALIGN_TOP);
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setFixedHeight(height+mmToPt(line*2));
//        cell.setBorderWidth(line);
//        return cell;
//    }   
//    public PdfPCell createTextPieCartel2da(Paragraph text, Float height) {
//        Float line = 1.5f;
//        Paragraph p = new Paragraph(text);
//        PdfPCell cell = new PdfPCell(p);
//        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
//        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//        cell.setFixedHeight(height+mmToPt(line*2));
//        cell.setBorderWidth(line);
//        return cell;
//    }    
//    public PdfPCell createTextCellSubHeader(Paragraph text, Float height) {
//        Paragraph p = new Paragraph(text);
//        PdfPCell cell = new PdfPCell(p);
//        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setFixedHeight(height);
//        if(ROPC_VIEW.equalsIgnoreCase(ConstanteMedida.V_FORMATO_PRELIMINAR) || ROPC_VIEW.equalsIgnoreCase(ConstanteMedida.V_FORMATO_PREVIO)){ 
//            cell.setBackgroundColor(ROPC_BLACK);
//        }
//        cell.setBorderWidth(0);
//        return cell;
//    }    
//    public PdfPCell createSimbolCell(String code, Float width, Float height, Integer numOrg) throws DocumentException, IOException {
//        Float line = 1f;
//        Resource file = getImage(code, PATH_LOCATION_FILES.get(0));
//        Image image = Image.getInstance(file.getURL());
//        image.setAccessibleAttribute(PdfName.ALT, new PdfString("ROPC"));
//        if (numOrg == 1) {
//            height = height / 2;
//        }
//        image.scaleAbsolute(width - 3f, height - 3f);
//        PdfPCell cell = new PdfPCell(image);
//        cell.setBorderWidth(line);        
//        cell.setBorderColor(ROPC_BLACK);       
//        cell.setFixedHeight(height);
//        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        return cell;
//    }
//    public PdfPCell createPhotoCell(String code, Float width, Float height, Integer numOrg) throws DocumentException, IOException {
//        Float line = 1f;
//        Resource file = getImage(code, PATH_LOCATION_FILES.get(1));
//        Image image = Image.getInstance(file.getURL());
//        image.setAccessibleAttribute(PdfName.ALT, new PdfString("ROPC"));
//        if (numOrg == 1) {
//            height = height / 2;
//        }
//        image.scaleAbsolute(width - 3f, height - 3f);
//        image.setSmask(true);  
//        PdfPCell cell = new PdfPCell(image);  
//        cell.setBorderWidth(line);        
//        cell.setBorderColor(ROPC_BLACK);         
//        cell.setFixedHeight(height);
//        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        return cell;
//    }
//    public PdfPCell createSimbolNumberCell(String code, Float width, Float height, Integer numOrg) throws DocumentException, IOException {
//        Float line = 1f;
//        Resource file = getImage(code + ".jpg", PATH_LOCATION_FILES.get(3));
//        Image image = Image.getInstance(file.getURL());
//        image.setAccessibleAttribute(PdfName.ALT, new PdfString("ROPC"));
//        if (numOrg == 1) {
//            height = height / 2;
//        }
//        image.scaleAbsolute(width - 3f, height - 3f);
//        PdfPCell cell = new PdfPCell(image);
//        cell.setBorderWidth(line);
//        cell.setFixedHeight(height);
//        cell.setBorderColor(ROPC_BLACK);
//        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setBackgroundColor(ROPC_WHITE);
//        return cell;
//    }
//    public PdfPCell createCellLocked(Float height) {
//        Paragraph p = new Paragraph();
//        PdfPCell cell = new PdfPCell(p);
//        cell.setFixedHeight(height);
//        cell.setBorderWidth(0);        
//        if(ROPC_VIEW.equalsIgnoreCase(ConstanteMedida.V_FORMATO_PREVIO)){ 
//            cell.setBorderWidth(ROPC_LINE);            
//        }
//        cell.setBackgroundColor(ROPC_BLACK);
//        return cell;
//    }    
//    public PdfPCell createTextCellOrgVerificacion(String text, Float fontSize, Float padding, Float height) {             
//        Font font = FontFactory.getFont("arial", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, fontSize, Font.BOLD, ROPC_BLACK);       
//        Chunk textHeader = new Chunk(text, font);
//        Paragraph p = new Paragraph(textHeader);        
//        PdfPCell cell = new PdfPCell(p);
//        cell.setBorderWidth(0);
//        if(ROPC_VIEW.equalsIgnoreCase(ConstanteMedida.V_FORMATO_PREVIO)){ 
//            cell.setBorderWidth(ROPC_LINE);            
//        }        
//        cell.setPaddingLeft(padding);
//        cell.setPaddingRight(padding);
//        cell.setFixedHeight(height);
//        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//        return cell;
//    }  
//    public PdfPCell createEmptyCellBorder(Float height) {
//        Float line = 1.5f;
//        PdfPCell cell = new PdfPCell();
//        cell.setBorderWidth(line);
//        cell.setFixedHeight(height);
//        return cell;
//    }                  
//}
