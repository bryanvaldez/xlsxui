/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pe.gob.onpe.claridadui.Constants.Mensajes;
import pe.gob.onpe.claridadui.Constants.Validaciones;
import pe.gob.onpe.claridadui.model.DetalleFormato;
import pe.gob.onpe.claridadui.model.Formato;
import pe.gob.onpe.claridadui.service.iface.IExcelXSSFValidatorService;
import pe.gob.onpe.claridadui.service.iface.IFormatoService;

/**
 *
 * @author Bryan Luis Valdez Jara <ibryan.valdez@gmail.com>
 */
public class XLSX_Read extends XLSX_Build implements IExcelXSSFValidatorService{
    
    public XLSX_Read(XSSFWorkbook workbook, int typeFormat, String serviceRuc, String pathServer, int codeOrganization, int codeCandidate) {
        super(workbook, typeFormat, serviceRuc, pathServer, codeOrganization, codeCandidate);
    }
    
    JsonObject jResponseRead = new JsonObject();
    JsonArray data = new JsonArray();    
    
    @Override
    public String validate(){
        IFormatoService factory  = new FormatoService();
        Formato formato = factory.getFormato(typeFormat);         
        boolean validExcel = validExcel_Sheet(workbook, formato);
        
        if(validExcel){
            data = getSheetsData(formato);
        }
        
        
        
        return "ok";
    }

    //Validations
    public boolean validExcel_Sheet(XSSFWorkbook workbook, Formato format){
        boolean response = true;        
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
            jResponseRead.addProperty("validExcel", Boolean.FALSE);
            jResponseRead.addProperty("msjValidExcel", Mensajes.M_INVALID_EXCEL);
            response = false;
        }
        return response;
    }          
    //Procedure
    private JsonArray getSheetsData(Formato formato){        
        JsonArray jResponse = new JsonArray();         
        JsonObject jSheetData;             
        JsonArray formatSheets = new JsonParser().parse(formato.getDetalleHoja()).getAsJsonArray();
        JsonArray jCordinates = getCoordinates(formatSheets); 
        
        for (int i = 0; i < jCordinates.size(); i++) {
            JsonObject jCordinate = jCordinates.get(i).getAsJsonObject();            
            if(jCordinate.get("isIndex").getAsBoolean()){
                jSheetData = getTableIterator(formato, jCordinate);
                //jResponse.add(jSheetData);                
                //JsonObject response = getSheetValidIndex(formato, jCordinates, jSheetData);                       
                //jCordinates =  response.get("jCoordinates").getAsJsonArray();                                 
                System.out.println("Hoja: " +  (jCordinate.get("hoja").getAsInt()) +" | "+jSheetData);                
            }
        }
        
        for (int i = 0; i < jCordinates.size(); i++) {
            JsonObject jCordinate = jCordinates.get(i).getAsJsonObject();            
            if(!jCordinate.get("isIndex").getAsBoolean()){
                //jSheetData = getTableIterator(formato, jCordinate);
                //jResponse.add(jSheetData);
                System.out.println("Hoja: " +  (jCordinate.get("hoja").getAsInt()) + " | success"); 
            }
        }        
        
        return jResponse;
    }            
    //Reader
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
    
    
    
    
    
    
    
    
    ///--------------------------------sometimes
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
                        JsonObject cellData = rowOut.getAsJsonObject("data");
                        jdata.add(cellData);        
                        if(cellData.get("validCellData").getAsBoolean()){
                            sumCol1+= getRowAmount(rowOut, 1); 
                            sumCol2+= getRowAmount(rowOut, 2);                         
                        //CUSTOM VALIDATION -- COLOCAR AQUI VALIDACIONES ADICIONALES
//                            validCustom_Fechas(row, formato, coordinate, jdata);
//                            validCustom_Comprobante(row, formato, coordinate, jdata);
//                            validCustom_Padron(row, formato, coordinate, jdata);
//                            validCustom_Ruc(row, formato, coordinate, jdata);
//                            validCustom_Detalle(row, formato, coordinate, jdata);
//                            validCustom_AmountUit(row, formato, coordinate, jdata);                            
                        }                                       
                    }                    
                }else if(row.getRowNum() == rowSubtotal && rowSubtotal>0){  //Data Subtotal
                    rowOut = getRowIterator(formato, hoja, row, Validaciones.T_SUBTOTAL);
                    if(rowOut.get("success").getAsBoolean()){
                        subTotal.add(rowOut.getAsJsonObject("data"));
                        //validData_SubTotal(row, rowOut, sumCol1, sumCol2);         
                    }                      
                }else if(row.getRowNum() == rowTotal && rowTotal>0){  //Data Total
                    rowOut = getRowIterator(formato, hoja, row, Validaciones.T_TOTAL);
                    if(rowOut.get("success").getAsBoolean()){
                        total.add(rowOut.getAsJsonObject("data"));
                        //validData_Total(row, rowOut, sumCol1, sumCol2);
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
        boolean success = false;      
        
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
                success = true;
                rowResult.addProperty(cellValue.get("labelCell").getAsString(), cellValue.get("valueCell").getAsString()); 
                rowResult.addProperty("messageCellObs", cellValue.get("messageCellObs").getAsString()); 
                rowResult.addProperty("validCellData", cellValue.get("validCellData").getAsBoolean());
                if(cellValue.get("validCellData").getAsBoolean()){
                    if(cellValue.get("isSuma").getAsBoolean() ){    //Problemas con los campos permitidos en blanco agregar validacion                
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
                
                }
            }                     
        }                    
        jResponse.add("amount", jAmount);    
        jResponse.add("data", rowResult);
        jResponse.addProperty("success", success);       
        return jResponse;    
    }    
    private JsonObject getCellValue(int position, Cell cell, Formato formato, int typeData){
        JsonObject jResponse = new JsonObject();
        boolean success  = false;
        for (DetalleFormato parameter : formato.getDetalle()) {            
            if(parameter.getProcesoDetalle()== Validaciones.FORMAT_READER && parameter.getHojaExcel() == position+1 && parameter.getTipoDato() == typeData){                                 
                if(parameter.getColumnaExcel() == cell.getColumnIndex() && (parameter.getFilaExcel() == 0 || parameter.getFilaExcel() == cell.getRowIndex()) ){
                    success = true;
                    String valueCell = getValueCell(cell);  
                    JsonObject validDataCell = validCellData(cell, parameter, valueCell);                    
                    jResponse.addProperty("validCellData", validDataCell.get("success").getAsBoolean());
                    jResponse.addProperty("messageCellObs", validDataCell.get("messageCellObs").getAsString());
                    jResponse.addProperty("labelCell", parameter.getNombreColumna());
                    jResponse.addProperty("valueCell", valueCell);
                    jResponse.addProperty("isSuma", parameter.isSuma());
                    jResponse.addProperty("order", parameter.getOrden());
                    break;                                
                }
            }                       
        }         
        jResponse.addProperty("success", success);        
        return jResponse;
    }   
    public JsonObject validCellData(Cell cell, DetalleFormato parameter, String value) {  
        JsonObject jResponse = new JsonObject();
        String messageCellObs = "";
        boolean success = true;         
        
        String regex = parameter.getValidacion();
        String messageRegexError = parameter.getMensajeValidacion();
        String messageEmptyError = parameter.getComentario();        

        if (value.equalsIgnoreCase("")) {
            if (parameter.getObligatorio() == Validaciones.FORMAT_REQUIRED) {
                cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                cell.setCellComment(getComentario(cell, messageEmptyError));
                messageCellObs = messageEmptyError;
                success = false;  
                //validData = response; 
            }
        } else {
            if (regex != null && !regex.trim().isEmpty()) {
                if (!value.matches(regex)) {
                    cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                    cell.setCellComment(getComentario(cell, messageRegexError));
                    messageCellObs = messageRegexError;
                    success = false;
                    //validData = response;
                }
            }
        }           
        jResponse.addProperty("success", success);
        jResponse.addProperty("messageCellObs", messageCellObs); 
        return jResponse;
    }    
    
}
