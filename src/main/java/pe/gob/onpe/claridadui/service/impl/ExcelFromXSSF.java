/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pe.gob.onpe.claridadui.Constants.Mensajes;
import pe.gob.onpe.claridadui.Constants.Validaciones;
import pe.gob.onpe.claridadui.enums.FormatoEnum;
import pe.gob.onpe.claridadui.model.DetalleFormato;
import pe.gob.onpe.claridadui.model.Formato;
import pe.gob.onpe.claridadui.service.iface.IExcelXSSFValidatorService;
import pe.gob.onpe.claridadui.service.iface.IFormatoService;

/**
 *
 * @author bvaldez
 */
public class ExcelFromXSSF extends ExcelValidator implements IExcelXSSFValidatorService{
    
    public ExcelFromXSSF(XSSFWorkbook file, int type, String pathRuc, String pathClaridad, int candidato){
        super(file, type, pathRuc, pathClaridad, candidato);
    }

    @Override
    public String validate() {
        JsonObject jResponse = new JsonObject();

        JsonArray data = new JsonArray();

        IFormatoService factory  = new FormatoService();
        Formato formato = factory.getFormato(type);        
        
        validExcel_Sheet(workbook, formato);        
        
        if(validExcel){
            data = getSheetsData(formato);            
            
            if(!validData){
                saveFileObservation(workbook, formato); 
                System.out.println("[ OBSERVACIONES ]");
            }else{
                System.out.println("[ CORRECTO!! ]");
            }                        
        }
                              
        jResponse.addProperty("validData", validData);        
        jResponse.addProperty("msjValidData", msjValidData);        
        jResponse.addProperty("validExcel", validExcel);        
        jResponse.addProperty("msjValidExcel", msjValidExcel);
        jResponse.add("data", data);                         
        return new Gson().toJson(jResponse.get("data"));
    }
   
    //-----------------------------------------------------------Step 
    //1 Step
    private JsonArray getSheetsData(Formato formato){        
        JsonArray jResponse = new JsonArray();         
        JsonObject jSheetData;             
        JsonArray formatSheets = new JsonParser().parse(formato.getDetalleHoja()).getAsJsonArray();
        JsonArray jCordinates = getCoordinates(formatSheets); 
        
        for (int i = 0; i < jCordinates.size(); i++) {
            JsonObject jCordinate = jCordinates.get(i).getAsJsonObject();            
            if(jCordinate.get("isIndex").getAsBoolean()){
                jSheetData = getTableIterator(formato, jCordinate);
                jResponse.add(jSheetData);                
                JsonObject response = getSheetValidIndex(formato, jCordinates, jSheetData);                       
                jCordinates =  response.get("jCoordinates").getAsJsonArray();                                 
                System.out.println("Hoja: " +  (jCordinate.get("hoja").getAsInt()) +" | success");                
            }
        }
        
        for (int i = 0; i < jCordinates.size(); i++) {
            JsonObject jCordinate = jCordinates.get(i).getAsJsonObject();            
            if(!jCordinate.get("isIndex").getAsBoolean()){
                jSheetData = getTableIterator(formato, jCordinate);
                jResponse.add(jSheetData);
                System.out.println("Hoja: " +  (jCordinate.get("hoja").getAsInt()) + " | success"); 
            }
        }        
        
        return jResponse;
    }       
    //2 Get Sheet Cordinates
    private JsonObject getSheetValidIndex(Formato formato, JsonArray jCordinates, JsonObject jSheetIndexData){        
        JsonObject jResponse = new JsonObject();  
                            
        if(formato.getId() == FormatoEnum.FORMATO_5.getId()){
            jResponse = validFormat5(formato, jCordinates, jSheetIndexData);
        }else if(formato.getId() == FormatoEnum.FORMATO_6.getId()){
            jResponse = validFormat6(formato, jCordinates, jSheetIndexData);            
        }        
        return jResponse;        
    }    
    //3 Step
    
    private JsonArray getCoordinates(JsonArray formatSheets){
        JsonArray jResponse = new JsonArray();         
        for (int i = 0; i < formatSheets.size(); i++) {
            JsonObject formatSheet = formatSheets.get(i).getAsJsonObject();
            int position = formatSheet.get("hoja").getAsInt()-1;
            XSSFSheet sheet = workbook.getSheetAt(position);           
            jResponse.add(getCoordinate(sheet, formatSheet));
        }                
        return jResponse;
    }        
    private JsonObject getCoordinate(XSSFSheet sheet, JsonObject formatSheet){
        JsonObject jResponse = new JsonObject();
        boolean success = true;        
        Iterator<Row> rowIterator = sheet.iterator();
          
        int hoja = formatSheet.get("hoja").getAsInt();
        boolean isIndex = formatSheet.get("isIndex").getAsBoolean();
        String initTable = formatSheet.get("iniTabla").getAsString();
        String subTotalTable = formatSheet.get("subtotal").getAsString();
        String totalTable = formatSheet.get("total").getAsString();   
        String formato = formatSheet.get("descripcion").getAsString();
        
        int initRow = 0;
        int finRow = 0;
        int subtotalRow = 0;
        int totalRow = 0;

        boolean valve = true;

        if(!initTable.equalsIgnoreCase("")){
            Row row;
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                Cell celda;
                if (valve) {
                    while (cellIterator.hasNext()) {
                        celda = cellIterator.next();
                        String valueCell = getValueCell(celda).trim();

                        if(valueCell.equalsIgnoreCase(initTable)){
                            initRow = celda.getRow().getRowNum() + 1;                        
                        }else{
                            if(subTotalTable.equalsIgnoreCase("")){
                                if (valueCell.equalsIgnoreCase(totalTable)){
                                    finRow = celda.getRow().getRowNum() - 1;
                                    subtotalRow = 0;
                                    totalRow = celda.getRow().getRowNum();
                                    valve = false;
                                    break;
                                }                                
                            }else{
                                if (valueCell.equalsIgnoreCase(subTotalTable)){
                                    subtotalRow = celda.getRow().getRowNum();
                                    finRow = celda.getRow().getRowNum() - 1;
                                }else if(valueCell.equalsIgnoreCase(totalTable)){
                                    totalRow = celda.getRow().getRowNum();
                                    valve = false;
                                    break;                                    
                                }                                                              
                            }                            
                        }
                    }
                } else {
                    break;
                }
            }             
        }else{
            success = false;
        }        
                
        jResponse.addProperty("hoja", hoja);
        jResponse.addProperty("isIndex", isIndex);        
        jResponse.addProperty("initRow", initRow);
        jResponse.addProperty("finRow", finRow);
        jResponse.addProperty("subtotalRow", subtotalRow);
        jResponse.addProperty("totalRow", totalRow);
        jResponse.addProperty("status", success);
        jResponse.addProperty("formato", formato);
        return jResponse;        
    } 
    private JsonObject getTableIterator(Formato formato, JsonObject coordinate){    
        
        JsonObject jResponse = new JsonObject();        
        JsonArray jdata = new JsonArray();
        JsonArray subTotal = new JsonArray();
        JsonArray total = new JsonArray();        
        String formatName = "";
                
        double sumCol1 = 0, sumCol2= 0;
        
        if(coordinate.get("status").getAsBoolean()){ 
            int hoja = coordinate.get("hoja").getAsInt()-1;
            int rowInitTable = coordinate.get("initRow").getAsInt();
            int rowFinTable = coordinate.get("finRow").getAsInt();
            int rowSubtotal = coordinate.get("subtotalRow").getAsInt();
            int rowTotal = coordinate.get("totalRow").getAsInt();  
            boolean isIndex = coordinate.get("isIndex").getAsBoolean();
            
            XSSFSheet sheet = workbook.getSheetAt(hoja);
            formatName = sheet.getSheetName(); 
            Iterator<Row> rowIterator = sheet.iterator();  
            Row row;            
            while (rowIterator.hasNext()) {               
                row = rowIterator.next();                           
                JsonObject rowOut;                                                                 
                if (row.getRowNum() >= rowInitTable && row.getRowNum() <= rowFinTable) { //Data Table
                    rowOut = getRowIterator(formato, hoja, row, Validaciones.T_TABLE);                                        
                    if(rowOut.get("success").getAsBoolean()){        
                        jdata.add(rowOut.getAsJsonObject("data"));                        
                        sumCol1+= getRowAmount(rowOut, 1); 
                        sumCol2+= getRowAmount(rowOut, 2);                
                        
                        //CUSTOM VALIDATION -- COLOCAR AQUI VALIDACIONES ADICIONALES
//                        validCustom_Fechas(row, formato, coordinate, jdata);
//                        validCustom_Comprobante(row, formato, coordinate, jdata);
//                        validCustom_Padron(row, formato, coordinate, jdata);
//                        validCustom_Ruc(row, formato, coordinate, jdata);
//                        validCustom_Detalle(row, formato, coordinate, jdata);
//                        validCustom_AmountUit(row, formato, coordinate, jdata);
                    }                    
                }else if(row.getRowNum() == rowSubtotal && rowSubtotal>0){  //Data Subtotal
                    rowOut = getRowIterator(formato, hoja, row, Validaciones.T_SUBTOTAL);
                    if(rowOut.get("success").getAsBoolean()){
                        subTotal.add(rowOut.getAsJsonObject("data"));
                        validData_SubTotal(row, rowOut, sumCol1, sumCol2);         
                    }                      
                }else if(row.getRowNum() == rowTotal && rowTotal>0){  //Data Total
                    rowOut = getRowIterator(formato, hoja, row, Validaciones.T_TOTAL);
                    if(rowOut.get("success").getAsBoolean()){
                        total.add(rowOut.getAsJsonObject("data"));
                        validData_Total(row, rowOut, sumCol1, sumCol2);
                    }                      
                }                
            }            
        }                              
        jResponse.add("data", jdata);
        jResponse.add("subTotal", subTotal);
        jResponse.add("total", total);
        jResponse.addProperty("formato", formatName); 
        return jResponse;       
    }    
    private JsonObject getRowIterator(Formato formato, int position, Row row, int TypeTable){        
        JsonObject jResponse = new JsonObject();
        JsonObject rowResult = new JsonObject();  
        boolean succes = false;      
        
        JsonArray jAmount = new JsonArray();
        
        double amountCol1 = 0, amountCol2 = 0;        
        int columnAmount1 = 0, columnAmount2 = 0;

        Iterator<Cell> cellIterator = row.cellIterator();  
        Cell cell;
        while (cellIterator.hasNext()) {
            cell = cellIterator.next();
            JsonObject cellValue = getCellValue(position, cell, formato, TypeTable);                        
            JsonObject monto = new JsonObject(); 
            if(cellValue.get("success").getAsBoolean()){
                if(cellValue.get("isSuma").getAsBoolean()){                    
                    int order = cellValue.get("order").getAsInt();                    
                    if(order == 0 || order == 1){
                        amountCol1 = cellValue.get("valueCell").getAsDouble();
                        columnAmount1 = cell.getColumnIndex();
                        monto.addProperty("monto", amountCol1);
                        monto.addProperty("columna", columnAmount1);
                        monto.addProperty("group", 1);
                        jAmount.add(monto);                
                    }else if(order == 2){
                        amountCol2 = cellValue.get("valueCell").getAsDouble();
                        columnAmount2 = cell.getColumnIndex();
                        monto.addProperty("monto", amountCol2);
                        monto.addProperty("columna", columnAmount2);
                        monto.addProperty("group", 2);
                        jAmount.add(monto);                         
                    }                                    
                }
                rowResult.addProperty(cellValue.get("labelCell").getAsString(), cellValue.get("valueCell").getAsString()); 
                succes = true;
            }                       
        }                    
        jResponse.add("amount", jAmount);    
        jResponse.add("data", rowResult);
        jResponse.addProperty("success", succes);       
        return jResponse;    
    }    
    private JsonObject getCellValue(int position, Cell cell, Formato formato, int typeData){
        JsonObject jResponse = new JsonObject();
        boolean success  = false;
        for (DetalleFormato parameter : formato.getDetalle()) {            
            if(parameter.getProcesoDetalle()== Validaciones.FORMAT_READER && parameter.getHojaExcel() == position+1 && parameter.getTipoDato() == typeData){                 
                if(parameter.getColumnaExcel() == cell.getColumnIndex() &&  parameter.getFilaExcel() == 0 ){
                    String valueCell = getValueCell(cell);
                    boolean validCell =  validData_EmptyOrRegex(cell, parameter, valueCell);
                    if(validCell){
                        success = true;
                        jResponse.addProperty("labelCell", parameter.getNombreColumna());
                        jResponse.addProperty("valueCell", valueCell);
                        jResponse.addProperty("isSuma", parameter.isSuma());
                        jResponse.addProperty("order", parameter.getOrden());
                    }
                    break;
                }else{                
                    if(parameter.getColumnaExcel() == cell.getColumnIndex() &&  parameter.getFilaExcel() == cell.getRowIndex() ){
                        String valueCell = getValueCell(cell);
                        boolean validCell =  validData_EmptyOrRegex(cell, parameter, valueCell);
                        if(validCell){
                            success = true;
                            jResponse.addProperty("labelCell", parameter.getNombreColumna());
                            jResponse.addProperty("valueCell", valueCell);
                            jResponse.addProperty("isSuma", parameter.isSuma()); 
                            jResponse.addProperty("order", parameter.getOrden());
                        }
                        break;
                    }                                    
                }
            }                       
        }         
        jResponse.addProperty("success", success);        
        return jResponse;
    }    
    private double getRowAmount(JsonObject rowOut, int position){                   
        double response = 0;                
        JsonArray aAmounts = rowOut.getAsJsonArray("amount");                        
        for (int i = 0; i < aAmounts.size(); i++) {
            JsonObject amount = aAmounts.get(i).getAsJsonObject();            
            if(position == amount.get("group").getAsInt()){
                response = amount.get("monto").getAsDouble();
            }                         
        }                   
        return response;
    }
    
    //-----------------------------------------------------------Validations
    public void validExcel_Sheet(XSSFWorkbook workbook, Formato format) {
        int countSheetValid = 0;
        JsonArray formatSheets = new JsonParser().parse(format.getDetalleHoja()).getAsJsonArray();
        for (int i = 0; i < formatSheets.size(); i++) {
            JsonObject formatSheet = formatSheets.get(i).getAsJsonObject();
            for (int j = 0; j < workbook.getNumberOfSheets(); j++) {
                if (formatSheet.get("descripcion").getAsString().equalsIgnoreCase(workbook.getSheetName(j))) {
                    countSheetValid++;
                    break;
                }
            }
        }        
        if(countSheetValid != formatSheets.size() ||  workbook.getNumberOfSheets() != formatSheets.size()){
            validExcel = false;
            msjValidExcel = Mensajes.M_INVALID_EXCEL;            
        }
    }    
    public boolean validData_EmptyOrRegex(Cell cell, DetalleFormato parameter, String value) {        
        boolean response = true;        
        String regex = parameter.getValidacion();
        String messageRegexError = parameter.getMensajeValidacion();
        String messageEmptyError = parameter.getComentario();
        
        XSSFSheet sheet = (XSSFSheet) cell.getSheet();
        XSSFWorkbook wb = (XSSFWorkbook) sheet.getWorkbook();

        if (value.equalsIgnoreCase("")) {
            if (parameter.getObligatorio() == Validaciones.FORMAT_REQUIRED) {
                cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                cell.setCellComment(getComentario(cell, messageEmptyError));                
                response = false;  
                validData = response; 
            }else{
                response = false;
            }
        } else {
            if (regex != null && !regex.trim().isEmpty()) {
                if (!value.matches(regex)) {
                    cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                    cell.setCellComment(getComentario(cell, messageRegexError));
                    response = false;
                    validData = response;
                }
            }
        }                
        return response;
    }        
    private void validData_SubTotal(Row row, JsonObject rowOut, double sumCol1, double sumCol2){
        JsonArray aAmounts = rowOut.getAsJsonArray("amount"); 
        for (int i = 0; i < aAmounts.size(); i++) {
            JsonObject amount = aAmounts.get(i).getAsJsonObject();
            int columna = amount.get("columna").getAsInt();
            double monto = amount.get("monto").getAsDouble();  
            if(i==0){
                validData_Amount(row, columna, monto, sumCol1);

            }else if(i == 1){
                validData_Amount(row, columna, monto, sumCol2);                 
            }                            
        }      
    }
    private void validData_Total(Row row, JsonObject rowOut, double sumCol1, double sumCol2){
        JsonArray aAmounts = rowOut.getAsJsonArray("amount"); 
        for (int i = 0; i < aAmounts.size(); i++) {
            JsonObject amount = aAmounts.get(i).getAsJsonObject();
            int columna = amount.get("columna").getAsInt();
            double monto = amount.get("monto").getAsDouble();                              
            validData_Amount(row, columna, monto, sumCol1+sumCol2);
        }     
    }    
    private void validData_Amount(Row row, int columna, double amount1, double amount2){   
        double THRESHOLD = .1;        
        if( !(Math.abs(amount1-amount2)<THRESHOLD) ){
            Cell cell = row.getCell(columna);
            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
            cell.setCellComment(getComentario(cell, Mensajes.M_INVALID_AMOUNT));                
            validData = false; 
        }  
    }        

    //-----------------------------------------------------------CUSTOM VALIDATION INDEX 
    private JsonObject validFormat5(Formato formato, JsonArray jCordinates, JsonObject jSheetIndexData){        
        JsonObject jResponse = new JsonObject();
        JsonArray jResponseCoordinates = new JsonArray();        
        JsonArray sheetsActive = jSheetIndexData.get("data").getAsJsonArray();     
        boolean is5A = false, is5B = false, is5C = false;
        double TotalIndex5A = 0, TotalIndex5B = 0, TotalIndex5C = 0;
        double Total5A = 0, Total5B = 0, Total5C = 0;

        for (int i = 0; i < sheetsActive.size(); i++) {
            JsonObject sheetActive = sheetsActive.get(i).getAsJsonObject();                
            if(!is5A){
                is5A = sheetActive.get("5A") != null;
                if(is5A){TotalIndex5A = sheetActive.get("5A").getAsDouble();}
            }
            if(!is5B){
                is5B = sheetActive.get("5B") != null;
                if(is5B){TotalIndex5B = sheetActive.get("5B").getAsDouble();}
            }
            if(!is5C){
                is5C = sheetActive.get("5C") != null;
                if(is5C){TotalIndex5C = sheetActive.get("5C").getAsDouble();}
            }                 
        }               
        
        for (int i = 0; i < jCordinates.size(); i++) {
            JsonObject jCordinate = jCordinates.get(i).getAsJsonObject();
            int sheetPosition = jCordinate.get("hoja").getAsInt(); 
            if(sheetPosition == 1){
               //jFormatSheets.add(temp);
            }else if(sheetPosition == 2 && is5A){
                jResponseCoordinates.add(jCordinate);
                Total5A = getTotalBySheet(formato, jCordinate);
            }else if(sheetPosition == 3 && is5B){
                jResponseCoordinates.add(jCordinate);
                Total5B = getTotalBySheet(formato, jCordinate);
            }else if(sheetPosition == 4 && is5C){
                jResponseCoordinates.add(jCordinate);
                Total5C = getTotalBySheet(formato, jCordinate);
            }                
        }                 
        
        XSSFSheet sheet = workbook.getSheetAt(0);
        if(is5A){
            if(TotalIndex5A != Total5A){
                Row rowTotal = sheet.getRow(10);
                Cell cell = rowTotal.getCell(8); 
                cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                cell.setCellComment(getComentario(cell, Mensajes.M_INVALID_AMOUNT_SHEET));                
                validData = false;                 
            }
        }
        if(is5B){
            if(TotalIndex5B != Total5B){
                Row rowTotal = sheet.getRow(11);
                Cell cell = rowTotal.getCell(8);   
                cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                cell.setCellComment(getComentario(cell, Mensajes.M_INVALID_AMOUNT_SHEET));                
                validData = false;                 
            }        
        }
        if(is5C){
            if(TotalIndex5C != Total5C){
                Row rowTotal = sheet.getRow(12);
                Cell cell = rowTotal.getCell(8); 
                cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                cell.setCellComment(getComentario(cell, Mensajes.M_INVALID_AMOUNT_SHEET));                
                validData = false;                 
            }        
        }
        
        jResponse.add("jCoordinates", jResponseCoordinates);  
        return jResponse; 
    }    
    private JsonObject validFormat6(Formato formato, JsonArray jCordinates, JsonObject jSheetIndexData){     
        
        JsonObject jResponse = new JsonObject();
        JsonArray jResponseCoordinates = new JsonArray();        
        JsonArray sheetsActive = jSheetIndexData.get("data").getAsJsonArray();     
        boolean is6A = false, is6B = false, is6C = false;
        double TotalIndex6A = 0, TotalIndex6B = 0, TotalIndex6C = 0;
        double Total6A = 0, Total6B = 0, Total6C = 0;

        for (int i = 0; i < sheetsActive.size(); i++) {
            JsonObject sheetActive = sheetsActive.get(i).getAsJsonObject();                
            if(!is6A){
                is6A = sheetActive.get("6A") != null;
                if(is6A){TotalIndex6A = sheetActive.get("6A").getAsDouble();}
            }
            if(!is6B){
                is6B = sheetActive.get("6B") != null;
                if(is6B){TotalIndex6B = sheetActive.get("6B").getAsDouble();}
            }
            if(!is6C){
                is6C = sheetActive.get("6C") != null;
                if(is6C){TotalIndex6C = sheetActive.get("6C").getAsDouble();}
            }                 
        }               
        
        for (int i = 0; i < jCordinates.size(); i++) {
            JsonObject jCordinate = jCordinates.get(i).getAsJsonObject();
            int sheetPosition = jCordinate.get("hoja").getAsInt(); 
            if(sheetPosition == 1){
               //jFormatSheets.add(temp);
            }else if(sheetPosition == 2 && is6A){
                jResponseCoordinates.add(jCordinate);
                Total6A = getTotalBySheet(formato, jCordinate);
            }else if(sheetPosition == 3 && is6B){
                jResponseCoordinates.add(jCordinate);
                Total6B = getTotalBySheet(formato, jCordinate);
            }else if(sheetPosition == 4 && is6C){
                jResponseCoordinates.add(jCordinate);
                Total6C = getTotalBySheet(formato, jCordinate);
            }                
        }                 
        
        XSSFSheet sheet = workbook.getSheetAt(0);
        if(is6A){
            if(TotalIndex6A != Total6A){
                Row rowTotal = sheet.getRow(8);
                Cell cell = rowTotal.getCell(7); 
                cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                cell.setCellComment(getComentario(cell, Mensajes.M_INVALID_AMOUNT_SHEET));                
                validData = false;                 
            }
        }
        if(is6B){
            if(TotalIndex6B != Total6B){
                Row rowTotal = sheet.getRow(9);
                Cell cell = rowTotal.getCell(7);   
                cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                cell.setCellComment(getComentario(cell, Mensajes.M_INVALID_AMOUNT_SHEET));                
                validData = false;                 
            }        
        }
        if(is6C){
            if(TotalIndex6C != Total6C){
                Row rowTotal = sheet.getRow(10);
                Cell cell = rowTotal.getCell(7); 
                cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                cell.setCellComment(getComentario(cell, Mensajes.M_INVALID_AMOUNT_SHEET));                
                validData = false;                 
            }        
        }        
        jResponse.add("jCoordinates", jResponseCoordinates);  
        return jResponse;         

    }        
    private double getTotalBySheet(Formato formato, JsonObject jCordinate){
        double amount = 0;
        boolean success = true;
                  
        if(jCordinate.get("status").getAsBoolean()){
            int hoja = jCordinate.get("hoja").getAsInt();
            XSSFSheet sheet = workbook.getSheetAt(hoja-1);
            int row = jCordinate.get("totalRow").getAsInt();
            for (DetalleFormato parameter : formato.getDetalle()) {  
                if(parameter.getHojaExcel() == hoja){
                    if(parameter.getTipoDato() == Validaciones.T_TOTAL){                        
                        Row rowTotal = sheet.getRow(row);
                        Cell cell = rowTotal.getCell(parameter.getColumnaExcel()); 
                        String valueCell= getValueCell(cell);
                        String regex = "^\\d+(\\.\\d{1,2})?$";
                        if(valueCell.equalsIgnoreCase("")){
                            success = false;
                        }else{
                            if (!valueCell.matches(regex)) {
                                success = false;
                            }else{
                                amount = Double.parseDouble(valueCell);
                            }
                        }
                    }                        
                }
            }                                               
        }           
        return amount;
    }    
    
    //-----------------------------------------------------------CUSTOM VALIDATION  TABLE    
    private void validCustom_Fechas(Row row, Formato formato, JsonObject coordinate, JsonArray jdata) {            
        int hoja = coordinate.get("hoja").getAsInt();
        String formatName = coordinate.get("formato").getAsString();                
        Date rowDate = new Date(), initDate = new Date(), currentDate = new Date();      
        int lastPosition = jdata.size()-1;
        JsonObject jRowData = jdata.get(lastPosition).getAsJsonObject();  
        boolean isDateValue = jRowData.get("fecha") != null;                
        try {
            if(isDateValue){
                rowDate = df.parse(jRowData.get("fecha").getAsString()); 
                initDate = df.parse("01/01/2018");
                for (DetalleFormato parameter : formato.getDetalle()) { 
                    if(parameter.getNombreColumna().equalsIgnoreCase("fecha")){                            
                        if(parameter.getHojaExcel() == hoja){
                            //if(formatName.equalsIgnoreCase("Anexo-5A")){ }
                            //System.out.println("Fila : "+ row.getRowNum()  +"   columna: "+parameter.getColumnaExcel()+  " valor: "+rowDate);
                            boolean validate = rowDate.compareTo(initDate) >= 0 && rowDate.compareTo(currentDate) <= 0;
                            if(!validate){
                                Cell cell = row.getCell(parameter.getColumnaExcel()); 
                                cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                                cell.setCellComment(getComentario(cell, Mensajes.M_INVALID_LIMIT_DATE));                
                                validData = false;                              
                            }
                        }
                    }           
                }                                                                                          
            }
        } catch (Exception e) {
            System.out.println(e);
        }                
    }
    private void validCustom_Padron(Row row, Formato formato, JsonObject coordinate, JsonArray jdata) {    
        
        if(coordinate.get("formato").getAsString().equalsIgnoreCase("Anexo-5A")){
            JsonObject jRowData = jdata.get(jdata.size()-1).getAsJsonObject();
            
            boolean isDocumento = jRowData.get("documento") != null;            
            boolean isNombres = jRowData.get("nombres") != null;         
            boolean isAppat = jRowData.get("apPaterno") != null;  
            boolean isApmat = jRowData.get("apMaterno") != null;              
            
            if(isDocumento){
                String documento = jRowData.get("documento").getAsString();
                String url = pathClaridad+"servicio/aportante/"+documento;
                boolean isPadron = false;
                JsonObject jPadronResponse = new JsonObject();
                try {
                    jPadronResponse = (JsonObject) new JsonParser().parse(getUrlService(url));    
                    isPadron = jPadronResponse.get("success").getAsBoolean();
                } catch (Exception e) {
                    System.out.println("Problemas con la de Conexion Servicio.");
                }                                        
                if(isPadron){                    
                    JsonObject jPadron = (JsonObject) new JsonParser().parse(jPadronResponse.get("data").getAsString());                                        
                    String nombres = jPadron.get("nombres") != null? jPadron.get("nombres").getAsString():"";
                    String apPaterno = jPadron.get("apPat") != null? jPadron.get("apPat").getAsString():"";
                    String apMaterno = jPadron.get("apMat") != null? jPadron.get("apMat").getAsString():"";    

                    if(isNombres && isPadron){
                        if(!nombres.equalsIgnoreCase(jRowData.get("nombres").getAsString().trim())){                        
                            Cell cell = row.getCell(6); 
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, nombres));                
                            validData = false;                                                     
                        }                        
                    }
                    if(isAppat&& isPadron){
                        if(!apPaterno.equalsIgnoreCase(jRowData.get("apPaterno").getAsString().trim())){
                            Cell cell = row.getCell(4);
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, apPaterno));                
                            validData = false;                           
                        }                        
                    }
                    if(isApmat&& isPadron){
                        if(!apMaterno.equalsIgnoreCase(jRowData.get("apMaterno").getAsString().trim())){
                            Cell cell = row.getCell(5); 
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, apMaterno));                
                            validData = false;                               
                        }                               
                    }                  
                }else{
                    Cell cell = row.getCell(7);
                    cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                    cell.setCellComment(getComentario(cell, Mensajes.M_NOFOUND_DNI));                
                    validData = false;   
                }                         
            }            
        }else if(coordinate.get("formato").getAsString().equalsIgnoreCase("Anexo-5C")){
            JsonObject jRowData = jdata.get(jdata.size()-1).getAsJsonObject();
            
            boolean isDocumento = jRowData.get("documento") != null;            
            boolean isNombres = jRowData.get("nombres") != null;         
            boolean isAppat = jRowData.get("apPaterno") != null;  
            boolean isApmat = jRowData.get("apMaterno") != null;              
            
            if(isDocumento){
                String documento = jRowData.get("documento").getAsString();
                String url = pathClaridad+"servicio/aportante/"+documento;
                boolean isPadron = false;
                JsonObject jPadronResponse = new JsonObject();
                try {
                    jPadronResponse = (JsonObject) new JsonParser().parse(getUrlService(url));    
                    isPadron = jPadronResponse.get("success").getAsBoolean();
                } catch (Exception e) {
                    System.out.println("Problemas con la de Conexion Servicio.");
                }                  
                if(isPadron){
                    JsonObject jPadron = (JsonObject) new JsonParser().parse(jPadronResponse.get("data").getAsString());                                        
                    String nombres = jPadron.get("nombres") != null? jPadron.get("nombres").getAsString():"";
                    String apPaterno = jPadron.get("apPat") != null? jPadron.get("apPat").getAsString():"";
                    String apMaterno = jPadron.get("apMat") != null? jPadron.get("apMat").getAsString():"";  
                    if(isNombres && isPadron){
                        if(!nombres.equalsIgnoreCase(jRowData.get("nombres").getAsString().trim())){                        
                            Cell cell = row.getCell(7); 
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, nombres));                
                            validData = false;                                                     
                        }                        
                    }
                    if(isAppat&& isPadron){
                        if(!apPaterno.equalsIgnoreCase(jRowData.get("apPaterno").getAsString().trim())){
                            Cell cell = row.getCell(5);
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, apPaterno));                
                            validData = false;                           
                        }                        
                    }
                    if(isApmat&& isPadron){
                        if(!apMaterno.equalsIgnoreCase(jRowData.get("apMaterno").getAsString().trim())){
                            Cell cell = row.getCell(6); 
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, apMaterno));                
                            validData = false;                               
                        }                               
                    }                  
                }else{
                    Cell cell = row.getCell(8);
                    cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                    cell.setCellComment(getComentario(cell, Mensajes.M_NOFOUND_DNI));                
                    validData = false;   
                }                         
            }            
        }else if(coordinate.get("formato").getAsString().equalsIgnoreCase("Anexo-6B")){
            JsonObject jRowData = jdata.get(jdata.size()-1).getAsJsonObject();
            
            boolean isTipoDocumento= jRowData.get("tipoDocumento") != null;
            boolean isDocumento = jRowData.get("documento") != null;            
            boolean isNombres = jRowData.get("razonSocial") != null;                     
            
            if(isTipoDocumento){
                int tipoDocumento = jRowData.get("tipoDocumento").getAsInt();
                if(tipoDocumento == Validaciones.TYPEDOC_DNI){
                    if(isDocumento){
                        String documento = jRowData.get("documento").getAsString(); 
                        String url = pathClaridad+"servicio/aportante/"+documento;
                        boolean isPadron = false;
                        JsonObject jPadronResponse = new JsonObject();
                        try {
                            jPadronResponse = (JsonObject) new JsonParser().parse(getUrlService(url));    
                            isPadron = jPadronResponse.get("success").getAsBoolean();
                        } catch (Exception e) {
                            System.out.println("Problemas con la de Conexion Servicio.");
                        }  
                        if(isPadron){
                            JsonObject jPadron = (JsonObject) new JsonParser().parse(jPadronResponse.get("data").getAsString());
                            String nombresResponse = "";
                            if(jPadron.get("apPat") != null){
                                 nombresResponse += jPadron.get("apPat").getAsString();
                            }
                            if(jPadron.get("apMat") != null){
                                 nombresResponse += " "+jPadron.get("apMat").getAsString();
                            }        
                            if(jPadron.get("nombres") != null){
                                 nombresResponse += " "+jPadron.get("nombres").getAsString();
                            }                                                           
                            if(isNombres){                                
                                if(!nombresResponse.equalsIgnoreCase(jRowData.get("razonSocial").getAsString().trim())){                        
                                    Cell cell = row.getCell(4); 
                                    cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                                    cell.setCellComment(getComentario(cell, nombresResponse));                
                                    validData = false;                                                     
                                }                        
                            }                  
                        }else{
                            Cell cell = row.getCell(6);
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, Mensajes.M_NOFOUND_DNI));                
                            validData = false; 
                        }                                                                         
                    }                
                }                       
            }            
        }else if(coordinate.get("formato").getAsString().equalsIgnoreCase("Anexo-6C")){
            JsonObject jRowData = jdata.get(jdata.size()-1).getAsJsonObject();
            
            boolean isTipoDocumento= jRowData.get("tipoDocumento") != null;
            boolean isDocumento = jRowData.get("documento") != null;            
            boolean isNombres = jRowData.get("razonSocial") != null;                     
            
            if(isTipoDocumento){
                int tipoDocumento = jRowData.get("tipoDocumento").getAsInt();
                if(tipoDocumento == Validaciones.TYPEDOC_DNI){
                    if(isDocumento){
                        String documento = jRowData.get("documento").getAsString(); 
                        String url = pathClaridad+"servicio/aportante/"+documento;
                        boolean isPadron = false;
                        JsonObject jPadronResponse = new JsonObject();
                        try {
                            jPadronResponse = (JsonObject) new JsonParser().parse(getUrlService(url));    
                            isPadron = jPadronResponse.get("success").getAsBoolean();
                        } catch (Exception e) {
                            System.out.println("Problemas con la de Conexion Servicio.");
                        }  
                        if(isPadron){
                            JsonObject jPadron = (JsonObject) new JsonParser().parse(jPadronResponse.get("data").getAsString());
                            String nombresResponse = "";
                            if(jPadron.get("apPat") != null){
                                 nombresResponse += jPadron.get("apPat").getAsString();
                            }
                            if(jPadron.get("apMat") != null){
                                 nombresResponse += " "+jPadron.get("apMat").getAsString();
                            }        
                            if(jPadron.get("nombres") != null){
                                 nombresResponse += " "+jPadron.get("nombres").getAsString();
                            }   
                            if(isNombres){                                
                                if(!nombresResponse.equalsIgnoreCase(jRowData.get("razonSocial").getAsString().trim())){                        
                                    Cell cell = row.getCell(6); 
                                    cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                                    cell.setCellComment(getComentario(cell, nombresResponse));                
                                    validData = false;                                                     
                                }                        
                            }                  
                        }else{
                            Cell cell = row.getCell(8);
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, Mensajes.M_NOFOUND_DNI));                
                            validData = false; 
                        }                                                                         
                    }                
                }                       
            }            
        }         
        
    }
    private void validCustom_Ruc(Row row, Formato formato, JsonObject coordinate, JsonArray jdata) {    
        
        if(coordinate.get("formato").getAsString().equalsIgnoreCase("Anexo-6A")){
            JsonObject jRowData = jdata.get(jdata.size()-1).getAsJsonObject();
            
            boolean isRuc= jRowData.get("ruc") != null;            
            boolean isRazonSocial= jRowData.get("razonSocial") != null;         
            
            
            if(isRuc){
                boolean isRucConsulta = false;                 
                String ruc = jRowData.get("ruc").getAsString();
                String url = pathRuc+ruc;   
                try {
                    JsonObject jresponse = (JsonObject) new JsonParser().parse(getUrlService(url));                       
                    isRucConsulta = jresponse.get("respuesta").getAsString().equalsIgnoreCase("0");                                                                              
                    if(isRucConsulta){
                        JsonObject jRuc = jresponse.getAsJsonObject("data");                      
                        String rucResponse = jRuc.get("sRazonSocial").getAsString().trim();  
                        if(isRazonSocial){
                            if(!rucResponse.equalsIgnoreCase(jRowData.get("razonSocial").getAsString().trim())){                        
                                Cell cell = row.getCell(4); 
                                cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                                cell.setCellComment(getComentario(cell, rucResponse));                
                                validData = false;                                                     
                            }                        
                        }                  
                    }else{
                        Cell cell = row.getCell(7);
                        cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                        cell.setCellComment(getComentario(cell, Mensajes.M_RUC_NO_FOUND));                
                        validData = false;   
                    }                     
                } catch (Exception e) {
                    System.out.println("Problemas con la de Conexion Servicio.");
                }                        
            }            
        }else if(coordinate.get("formato").getAsString().equalsIgnoreCase("Anexo-6B")){
            JsonObject jRowData = jdata.get(jdata.size()-1).getAsJsonObject();
            
            boolean isTipoDocumento= jRowData.get("tipoDocumento") != null; 
            boolean isDocumento= jRowData.get("documento") != null;
            boolean isRazonSocial= jRowData.get("razonSocial") != null;     
            
            if(isTipoDocumento){
                int tipoDocumento = jRowData.get("tipoDocumento").getAsInt();
                if(tipoDocumento == Validaciones.TYPEDOC_RUC){
                    if(isDocumento){
                        String ruc = jRowData.get("documento").getAsString();
                        String url = pathRuc+ruc; 
                        try {                           
                            JsonObject jresponse = (JsonObject) new JsonParser().parse(getUrlService(url));                       
                            boolean isRucConsulta = jresponse.get("respuesta").getAsString().equalsIgnoreCase("0");                                                                              
                            if(isRucConsulta){
                                JsonObject jRuc = jresponse.getAsJsonObject("data");                      
                                String rucResponse = jRuc.get("sRazonSocial").getAsString().trim();  
                                if(isRazonSocial){
                                    if(!rucResponse.equalsIgnoreCase(jRowData.get("razonSocial").getAsString().trim())){                        
                                        Cell cell = row.getCell(4); 
                                        cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                                        cell.setCellComment(getComentario(cell, rucResponse));                
                                        validData = false;                                                     
                                    }                        
                                }                  
                            }else{
                                Cell cell = row.getCell(6);
                                cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                                cell.setCellComment(getComentario(cell, Mensajes.M_RUC_NO_FOUND));                
                                validData = false;   
                            }                     
                        } catch (Exception e) {
                            System.out.println("Problemas con la de Conexion Servicio.");
                        }                                                                                                 
                    }                
                }
            }
        }else if(coordinate.get("formato").getAsString().equalsIgnoreCase("Anexo-6C")){
            JsonObject jRowData = jdata.get(jdata.size()-1).getAsJsonObject();
            
            boolean isTipoDocumento= jRowData.get("tipoDocumento") != null; 
            boolean isDocumento= jRowData.get("documento") != null;
            boolean isRazonSocial= jRowData.get("razonSocial") != null;     
            
            if(isTipoDocumento){
                int tipoDocumento = jRowData.get("tipoDocumento").getAsInt();
                if(tipoDocumento == Validaciones.TYPEDOC_RUC){
                    if(isDocumento){
                        String ruc = jRowData.get("documento").getAsString();
                        String url = pathRuc+ruc;                         
                        try {                           
                            JsonObject jresponse = (JsonObject) new JsonParser().parse(getUrlService(url));                       
                            boolean isRucConsulta = jresponse.get("respuesta").getAsString().equalsIgnoreCase("0");                             
                            if(isRucConsulta){
                                JsonObject jRuc = jresponse.getAsJsonObject("data");                      
                                String rucResponse = jRuc.get("sRazonSocial").getAsString().trim();   
                                if(isRazonSocial){
                                    if(!rucResponse.equalsIgnoreCase(jRowData.get("razonSocial").getAsString().trim())){                        
                                        Cell cell = row.getCell(6); 
                                        cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                                        cell.setCellComment(getComentario(cell, rucResponse));                
                                        validData = false;                                                     
                                    }                        
                                }                  
                            }else{
                                Cell cell = row.getCell(8);
                                cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                                cell.setCellComment(getComentario(cell, Mensajes.M_RUC_NO_FOUND));                
                                validData = false;   
                            }                            
                  
                        } catch (Exception e) {
                            System.out.println("Problemas con la de Conexion Servicio.");
                        }                         
                    }                
                }
            }
        }                 
    }    
    private void validCustom_Detalle(Row row, Formato formato, JsonObject coordinate, JsonArray jdata) {            
        if(coordinate.get("formato").getAsString().equalsIgnoreCase("Anexo-5A")){
            JsonObject jRowData = jdata.get(jdata.size()-1).getAsJsonObject();
            
            boolean isEfectivo = jRowData.get("montoEfectivo") != null;            
            boolean isEspecie = jRowData.get("montoEspecie") != null;         
            boolean isDetalle = jRowData.get("detalle") != null;               
            
            if(isEfectivo && isEspecie){
                Cell cell = row.getCell(10);
                cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                cell.setCellComment(getComentario(cell, Mensajes.M_DUPLICATE_AMOUNT));     
                
                cell = row.getCell(11);
                cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                cell.setCellComment(getComentario(cell, Mensajes.M_DUPLICATE_AMOUNT));                   
                
                validData = false;                   
            }else{                
                if(!isEfectivo && !isEspecie){
                    Cell cell = row.getCell(10);
                    cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                    cell.setCellComment(getComentario(cell, Mensajes.M_REQUIRED_AMOUNT));     

                    cell = row.getCell(11);
                    cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                    cell.setCellComment(getComentario(cell, Mensajes.M_REQUIRED_AMOUNT));                   

                    validData = false;                                      
                }else{
                    if(isEspecie){
                        if(!isDetalle){
                            Cell cell = row.getCell(12);
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, Mensajes.M_REQUIRED_DESC_ESPECIE));                    
                            validData = false;                                          
                        }                
                    }                                 
                }                        
            }
        }             
    }    
    private void validCustom_Comprobante(Row row, Formato formato, JsonObject coordinate, JsonArray jdata) {            
        if(coordinate.get("formato").getAsString().equalsIgnoreCase("Anexo-5A")){
            JsonObject jRowData = jdata.get(jdata.size()-1).getAsJsonObject();            
            boolean isComprobante = jRowData.get("numComprobante") != null;                        
            if(isComprobante){
                String currentNumComprobante = jRowData.get("numComprobante").getAsString(); 
                int cantProcesable = jdata.size()-1;                             
                if(cantProcesable>0){
                    for (int i = 0; i < cantProcesable; i++) {
                        JsonObject iRowData = jdata.get(i).getAsJsonObject();
                        String iNumComprobante = iRowData.get("numComprobante").getAsString(); 
                        if(currentNumComprobante.equalsIgnoreCase(iNumComprobante)){
                            Cell cell = row.getCell(3);
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, Mensajes.M_DUPLICATE_DOC));    
                            validData = false;
                            isComprobante = false;
                            break;
                        }
                    }                                        
                }
                if(isComprobante){
                    String url = pathClaridad+"servicio/comprobante/"+candidato+"/"+currentNumComprobante;
                    boolean duplicate = false;
                    JsonObject jPadronResponse = new JsonObject();
                    try {
                        jPadronResponse = (JsonObject) new JsonParser().parse(getUrlService(url));    
                        duplicate = jPadronResponse.get("success").getAsBoolean();
                    } catch (Exception e) {
                        System.out.println("Problemas con la de Conexion Servicio Comprobante.");
                    }                    
                    if(duplicate){
                        Cell cell = row.getCell(3);
                        cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                        cell.setCellComment(getComentario(cell, Mensajes.M_DUPLICATE_BD));    
                        validData = false;                
                    }                
                }                                              
            }   
        }             
    } 
    private void validCustom_AmountUit(Row row, Formato formato, JsonObject coordinate, JsonArray jdata) {            
        if(coordinate.get("formato").getAsString().equalsIgnoreCase("Anexo-5b")){
            double limiUIT = Validaciones.UIT *250;
            JsonObject jRowData = jdata.get(jdata.size()-1).getAsJsonObject();            
            boolean isMonto = jRowData.get("monto") != null;                        
            if(isMonto){
                double amount = jRowData.get("monto").getAsDouble();
                if(amount>limiUIT){
                    Cell cell = row.getCell(6);
                    cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                    cell.setCellComment(getComentario(cell, Mensajes.M_UIT_EXCEEDED));    
                    validData = false;                            
                }
            }   
        }             
    } 
    
    
    
    
    
    //SERVICES
    public String getUrlService(String urlName) throws NoSuchAlgorithmException, KeyManagementException {       
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };  
        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory()); 
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        }; 
        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);        
        String res = null;        
        try {
            URL url = new URL(urlName);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            res = "";
            String str = "";
            while (null != (str = br.readLine())) {
                res += str;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }    
    
    
}
