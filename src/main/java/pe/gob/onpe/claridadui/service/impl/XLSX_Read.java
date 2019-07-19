/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pe.gob.onpe.claridadui.Constants.Mensajes;
import pe.gob.onpe.claridadui.Constants.Validaciones;
import pe.gob.onpe.claridadui.enums.FormatoEnum;
import pe.gob.onpe.claridadui.model.DetalleFormato;
import pe.gob.onpe.claridadui.model.Formato;
import pe.gob.onpe.claridadui.model.XLSX_DetailAmount;
import pe.gob.onpe.claridadui.model.XLSX_DetailCell;
import pe.gob.onpe.claridadui.model.XLSX_DetailRow;
import pe.gob.onpe.claridadui.model.XLSX_DetailTable;
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
    
    XLSX_DetailCell detail_cell = new XLSX_DetailCell();
    XLSX_DetailRow detail_row = new XLSX_DetailRow();   
    XLSX_DetailTable detail_table = new XLSX_DetailTable();    
    
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

    ///--------------------------------Iterators
    private JsonArray getSheetsData(Formato formato){        
        JsonArray jResponse = new JsonArray();         
        JsonObject jSheetData;             
        JsonArray formatSheets = new JsonParser().parse(formato.getDetalleHoja()).getAsJsonArray();
        JsonArray jCordinates = getCoordinates(formatSheets); 
        
        for (int i = 0; i < jCordinates.size(); i++) {
            JsonObject jCordinate = jCordinates.get(i).getAsJsonObject();            
            if(jCordinate.get("isIndex").getAsBoolean()){
                getTableIterator(formato, jCordinate);              
                JsonObject response =getSheetValidIndex(formato, jCordinates);                       
                jCordinates =  response.get("jCoordinates").getAsJsonArray();                                 
                for (XLSX_DetailRow row : detail_table.getValueBody()) {
                    for (XLSX_DetailCell xLSX_DetailCell : row.getValueRow()) {
                        System.out.println(xLSX_DetailCell.isIsValidCellData()+" | "+xLSX_DetailCell.getLabelCell()+": "+xLSX_DetailCell.getValueCell()+"------"+xLSX_DetailCell.getMessageCellData());
                    }                    
                }
                for (XLSX_DetailRow row : detail_table.getValueTotal()) {
                    for (XLSX_DetailCell xLSX_DetailCell : row.getValueRow()) {
                        System.out.println(xLSX_DetailCell.isIsValidCellData()+" | "+xLSX_DetailCell.getLabelCell()+": "+xLSX_DetailCell.getValueCell()+"------"+xLSX_DetailCell.getMessageCellData());
                    }                    
                }                
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
    private void getTableIterator(Formato formato, JsonObject coordinate){           
        detail_table = new XLSX_DetailTable(); 
        List<XLSX_DetailRow> valueBody = new ArrayList<>();
        List<XLSX_DetailRow> valueSubtotal = new ArrayList<>();
        List<XLSX_DetailRow> valueTotal = new ArrayList<>();                
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
                if (row.getRowNum() >= rowInitTable && row.getRowNum() <= rowFinTable) { //Data Table
                    getRowIterator(formato, hoja, row, Validaciones.T_TABLE);                    
                    if(detail_row.isIsValidRow()){        
                        sumCol1+= calc_RowAmount(1); 
                        sumCol2+= calc_RowAmount(2);                           
                        //if(cellData.get("isValidRowData").getAsBoolean()){                       
                        //CUSTOM VALIDATION -- COLOCAR AQUI VALIDACIONES ADICIONALES
//                            validCustom_Fechas(row, formato, coordinate, jdata);
//                            validCustom_Comprobante(row, formato, coordinate, jdata);
//                            validCustom_Padron(row, formato, coordinate, jdata);
//                            validCustom_Ruc(row, formato, coordinate, jdata);
//                            validCustom_Detalle(row, formato, coordinate, jdata);
//                            validCustom_AmountUit(row, formato, coordinate, jdata);                            
                        //}  
                        valueBody.add(new XLSX_DetailRow(detail_row));                        
                    }                    
                }else if(row.getRowNum() == rowSubtotal && rowSubtotal>0){  //Data Subtotal
                    getRowIterator(formato, hoja, row, Validaciones.T_SUBTOTAL);
                    if(detail_row.isIsValidRow()){
                        calc_SubTotal(row, sumCol1, sumCol2);         
                        valueSubtotal.add(new XLSX_DetailRow(detail_row));                        
                    }                      
                }else if(row.getRowNum() == rowTotal && rowTotal>0){  //Data Total
                    getRowIterator(formato, hoja, row, Validaciones.T_TOTAL);
                    if(detail_row.isIsValidRow()){
                        calc_Total(row, sumCol1, sumCol2);
                        valueTotal.add(new XLSX_DetailRow(detail_row));                        
                    }                      
                }                
            }            
        }               
        detail_table.setValueBody(valueBody);
        detail_table.setValueSubtotal(valueSubtotal);
        detail_table.setValueTotal(valueTotal);
        detail_table.setNameFormat(formatName);    
    }   
    private void getRowIterator(Formato formato, int position, Row row, int TypeTable){              
        List<XLSX_DetailCell> currentRow = new ArrayList<>();
        List<XLSX_DetailAmount> detail_amount = new ArrayList<>();                
        boolean isValidRow = false;
        boolean isValidRowData = true;       
        double amountCol1 = 0, amountCol2 = 0;        
        int columnAmount1 = 0, columnAmount2 = 0;
        Iterator<Cell> cellIterator = row.cellIterator();  
        Cell cell;
        while (cellIterator.hasNext()) {
            cell = cellIterator.next();
            detail_cell = new XLSX_DetailCell();
            getCellIterator(position, cell, formato, TypeTable);                                                 
            XLSX_DetailAmount monto = new XLSX_DetailAmount();            
            if(detail_cell.isIsValidCell()){
                isValidRow = true;
                currentRow.add(new XLSX_DetailCell(detail_cell));                                
                if(detail_cell.isIsValidCellData()){
                    if(!detail_cell.isIsEmptyCellData() && detail_cell.isIsSumaCell()){
                        int order = detail_cell.getOrderCell();                    
                        if(order == 0 || order == 1){
                            amountCol1 = Double.parseDouble(detail_cell.getValueCell());
                            columnAmount1 = cell.getColumnIndex();                            
                            detail_amount.add(new XLSX_DetailAmount(amountCol1, columnAmount1, 1));           
                        }else if(order == 2){
                            amountCol2 = Double.parseDouble(detail_cell.getValueCell());
                            columnAmount2 = cell.getColumnIndex();                            
                            detail_amount.add(new XLSX_DetailAmount(amountCol2, columnAmount2, 2));                                                   
                        }                      
                    }                
                }else{
                    isValidRowData = false;
                }                                              
            }                     
        }                            
        detail_row.setAmountRow(detail_amount);
        detail_row.setValueRow(currentRow);
        detail_row.setIsValidRow(isValidRow);
        detail_row.setIsValidRowData(isValidRowData);  
    }        
    private void getCellIterator(int position, Cell cell, Formato formato, int typeData){
        boolean isValidCell  = false;
        for (DetalleFormato parameter : formato.getDetalle()) {            
            if(parameter.getProcesoDetalle()== Validaciones.FORMAT_READER && parameter.getHojaExcel() == position+1 && parameter.getTipoDato() == typeData){                                 
                if(parameter.getColumnaExcel() == cell.getColumnIndex() && (parameter.getFilaExcel() == 0 || parameter.getFilaExcel() == cell.getRowIndex()) ){
                    isValidCell = true;
                    String valueCell = getValueCell(cell);  
                    validCellData(cell, parameter, valueCell);
                    detail_cell.setLabelCell(parameter.getNombreColumna());
                    detail_cell.setOrderCell(parameter.getOrden());
                    detail_cell.setIsSumaCell(parameter.isSuma());
                    detail_cell.setValueCell(valueCell);
                    break;                                
                }
            }                       
        }         
        detail_cell.setIsValidCell(isValidCell);       
    }        
    private void validCellData(Cell cell, DetalleFormato parameter, String value) {  
        String messageCellData = "";
        boolean isValidCellData = true;
        boolean isEmptyCellData = false;        
        String regex = parameter.getValidacion();
        String messageRegexError = parameter.getMensajeValidacion();
        String messageEmptyError = parameter.getComentario();        
        if (value.equalsIgnoreCase("")) {
            if (parameter.getObligatorio() == Validaciones.FORMAT_REQUIRED) {
                cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                cell.setCellComment(getComentario(cell, messageEmptyError));
                messageCellData = messageEmptyError;
                isValidCellData = false;  
            }else{
                isEmptyCellData = true;
            }
        } else {
            if (regex != null && !regex.trim().isEmpty()) {
                if (!value.matches(regex)) {
                    cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                    cell.setCellComment(getComentario(cell, messageRegexError));
                    messageCellData = messageRegexError;
                    isValidCellData = false;
                }
            }
        }           
        detail_cell.setIsValidCellData(isValidCellData);
        detail_cell.setIsEmptyCellData(isEmptyCellData);
        detail_cell.setMessageCellData(messageCellData);
    }               
    ///----------------------------Cálculos  
    private double calc_RowAmount(int position){                   
        double response = 0;                        
        for (int i = 0; i < detail_row.getAmountRow().size(); i++) {
            XLSX_DetailAmount amount = detail_row.getAmountRow().get(i);         
            if(position == amount.getGroup()){
                response = amount.getAmount();
            }                         
        }                   
        return response;
    }          
    private void calc_SubTotal(Row row, double sumCol1, double sumCol2){
        for (int i = 0; i < detail_row.getAmountRow().size(); i++) {
            XLSX_DetailAmount amount = detail_row.getAmountRow().get(i);
            int columna = amount.getColumn();
            double monto = amount.getAmount();  
            if(i==0){
                calc_ValidAmount(row, columna, monto, sumCol1, detail_row.getValueRow().get(i), Mensajes.M_INVALID_AMOUNT);

            }else if(i == 1){
                calc_ValidAmount(row, columna, monto, sumCol2, detail_row.getValueRow().get(i), Mensajes.M_INVALID_AMOUNT);                 
            }                            
        }      
    }
    private void calc_Total(Row row, double sumCol1, double sumCol2){
        for (int i = 0; i < detail_row.getAmountRow().size(); i++) {
            XLSX_DetailAmount amount = detail_row.getAmountRow().get(i);
            int columna = amount.getColumn();
            double monto = amount.getAmount();  
            calc_ValidAmount(row, columna, monto, sumCol1+sumCol2, detail_row.getValueRow().get(i), Mensajes.M_INVALID_AMOUNT);
        }     
    }  
    private void calc_ValidAmount(Row row, int columna, double amount1, double amount2, XLSX_DetailCell cellData, String observation){   
        double THRESHOLD = .1;   
        if( !(Math.abs(amount1-amount2)<THRESHOLD) ){
            Cell cell = row.getCell(columna);
            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
            cell.setCellComment(getComentario(cell, observation));                
            cellData.setIsValidCellData(false);
            cellData.setMessageCellData(observation);
        }
    }          
    //-----------------------------Cordenadas
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
 
    //-----------------------------Validaciones
    private JsonObject getSheetValidIndex(Formato formato, JsonArray jCordinates){        
        JsonObject jResponse = new JsonObject();                              
        if(formato.getId() == FormatoEnum.FORMATO_5.getId()){
            jResponse = validFormat5(formato, jCordinates);
        }else if(formato.getId() == FormatoEnum.FORMATO_6.getId()){
            //jResponse = validFormat6(formato, jCordinates);            
        }        
        return jResponse;        
    }   
    private JsonObject validFormat5(Formato formato, JsonArray jCordinates){        
        JsonObject jResponse = new JsonObject();
        JsonArray jResponseCoordinates = new JsonArray();             
       
        double Total5A = 0, Total5B = 0, Total5C = 0;
                
        XLSX_DetailCell sheet_5A = new XLSX_DetailCell();
        XLSX_DetailCell sheet_5B = new XLSX_DetailCell();
        XLSX_DetailCell sheet_5C = new XLSX_DetailCell();
        
        
        for (int i = 0; i < detail_table.getValueBody().size(); i++) {
            XLSX_DetailRow indexRow = detail_table.getValueBody().get(i);            
            for (XLSX_DetailCell detailCell : indexRow.getValueRow()) {                
                if(detailCell.getLabelCell().equalsIgnoreCase("5A")){
                    sheet_5A = detailCell;
                }else if(detailCell.getLabelCell().equalsIgnoreCase("5B")){
                    sheet_5B = detailCell;
                }else if(detailCell.getLabelCell().equalsIgnoreCase("5C")){
                    sheet_5C = detailCell;
                }                             
            }                         
        }                       
        for (int i = 0; i < jCordinates.size(); i++) {
            JsonObject jCordinate = jCordinates.get(i).getAsJsonObject();
            int sheetPosition = jCordinate.get("hoja").getAsInt(); 
            if(sheetPosition == 1){
            }else if(sheetPosition == 2 && sheet_5A.isIsValidCellData() && !sheet_5A.isIsEmptyCellData()){
                jResponseCoordinates.add(jCordinate);
                Total5A = getTotalBySheet(formato, jCordinate);
            }else if(sheetPosition == 3 && sheet_5B.isIsValidCellData() && !sheet_5B.isIsEmptyCellData()){
                jResponseCoordinates.add(jCordinate);
                Total5B = getTotalBySheet(formato, jCordinate);
            }else if(sheetPosition == 4 && sheet_5C.isIsValidCellData() && !sheet_5C.isIsEmptyCellData()){
                jResponseCoordinates.add(jCordinate);
                Total5C = getTotalBySheet(formato, jCordinate);
            }                
        }           
                
        XSSFSheet sheet = workbook.getSheetAt(0);         
        if(sheet_5A.isIsValidCellData() && !sheet_5A.isIsEmptyCellData()){
            calc_ValidAmount(sheet.getRow(10), 8, Double.parseDouble(sheet_5A.getValueCell()), Total5A, sheet_5A, Mensajes.M_INVALID_AMOUNT_SHEET);
        }
        if(sheet_5B.isIsValidCellData() && !sheet_5B.isIsEmptyCellData()){
            calc_ValidAmount(sheet.getRow(11), 8, Double.parseDouble(sheet_5B.getValueCell()), Total5B, sheet_5B, Mensajes.M_INVALID_AMOUNT_SHEET);    
        }
        if(sheet_5C.isIsValidCellData() && !sheet_5C.isIsEmptyCellData()){
            calc_ValidAmount(sheet.getRow(12), 8, Double.parseDouble(sheet_5C.getValueCell()), Total5C, sheet_5C, Mensajes.M_INVALID_AMOUNT_SHEET);        
        }        
        jResponse.add("jCoordinates", jResponseCoordinates);  
        return jResponse; 
    }             
    private double getTotalBySheet(Formato formato, JsonObject jCordinate){  ///!!!falta validar el monto extraido
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
 
}
