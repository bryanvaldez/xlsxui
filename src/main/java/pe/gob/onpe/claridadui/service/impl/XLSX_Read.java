/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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
    ///--------------------------------Iterators
    private JsonArray getSheetsData(Formato formato){        
        JsonArray jResponse = new JsonArray();                      
        JsonArray formatSheets = new JsonParser().parse(formato.getDetalleHoja()).getAsJsonArray();
        JsonArray jCordinates = getCoordinates(formatSheets);         
        for (int i = 0; i < jCordinates.size(); i++) {
            JsonObject jCordinate = jCordinates.get(i).getAsJsonObject();            
            if(jCordinate.get("isIndex").getAsBoolean()){
                getTableIterator(formato, jCordinate);              
                JsonObject response = getSheetValidIndex(formato, jCordinates);                       
                jCordinates =  response.get("jCoordinates").getAsJsonArray();  
                jResponse.add(build_Response());                                
            }
        }        
        for (int i = 0; i < jCordinates.size(); i++) {
            JsonObject jCordinate = jCordinates.get(i).getAsJsonObject();            
            if(!jCordinate.get("isIndex").getAsBoolean()){
                getTableIterator(formato, jCordinate);
                jResponse.add(build_Response());
            }
        }                
        return jResponse;
    }      
    private void getTableIterator(Formato formato, JsonObject coordinate){           
        detail_table = new XLSX_DetailTable(); 
        List<XLSX_DetailRow> valueBody = new ArrayList<>();
        List<XLSX_DetailRow> valueSubtotal = new ArrayList<>();
        List<XLSX_DetailRow> valueTotal = new ArrayList<>();                           
        double sumCol1 = 0, sumCol2= 0;      
        if(coordinate.get("status").getAsBoolean()){ 
            int hoja = coordinate.get("hoja").getAsInt()-1;
            detail_table.setIndex(hoja);            
            int rowInitTable = coordinate.get("initRow").getAsInt();
            int rowFinTable = coordinate.get("finRow").getAsInt();
            int rowSubtotal = coordinate.get("subtotalRow").getAsInt();
            int rowTotal = coordinate.get("totalRow").getAsInt();  
            boolean isIndex = coordinate.get("isIndex").getAsBoolean();
            
            XSSFSheet sheet = workbook.getSheetAt(hoja);   
            detail_table.setNameFormat(sheet.getSheetName());
            Iterator<Row> rowIterator = sheet.iterator();  
            Row row;            
            while (rowIterator.hasNext()) {               
                row = rowIterator.next();                                                                                           
                if (row.getRowNum() >= rowInitTable && row.getRowNum() <= rowFinTable) { //Data Body
                    getRowIterator(formato, hoja, row, Validaciones.T_TABLE);                    
                    calc_RowErrors(detail_row);
                    if(detail_row.isIsValidRow()){        
                        sumCol1+= calc_RowAmount(1); 
                        sumCol2+= calc_RowAmount(2);  
                        if(detail_row.isIsValidRowData()){
                            valid_CustomFechas(row);
                            valid_CustomComprobante(row);
                            valid_CustomPadron(row);
                        
                        }
                        
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
        detail_row.setIndex(row.getRowNum());
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
        detail_cell.setIndex(cell.getColumnIndex());        
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
    private void calc_RowErrors(XLSX_DetailRow detailRow){
        if(detailRow.isIsValidRowData()){
            detail_table.setCantValidBody(detail_table.getCantValidBody()+1);
        }else{
            detail_table.setCantInvalidBody(detail_table.getCantInvalidBody()+1);
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
    //-----------------------------Build
    private JsonObject build_Response(){
        JsonObject jSheetData = new JsonObject();      
        jSheetData.addProperty("nombreFormato", detail_table.getNameFormat());
        jSheetData.addProperty("registrosIncorrectos", detail_table.getCantValidBody());
        jSheetData.addProperty("registrosCorrectos", detail_table.getCantInvalidBody());          
        for (XLSX_DetailRow row : detail_table.getValueTotal()) {            
            for (XLSX_DetailCell cellDetail : row.getValueRow()) {
                JsonObject total = new JsonObject();
                total.addProperty("estado", cellDetail.isIsValidCellData());
                total.addProperty("valor", cellDetail.getValueCell());
                total.addProperty("observacion", cellDetail.getMessageCellData());
                jSheetData.add("montoTotal", total);
                break;
            }                    
        }            
        JsonArray subtotal = new JsonArray();        
        for (XLSX_DetailRow row : detail_table.getValueSubtotal()) {            
            for (XLSX_DetailCell cellDetail : row.getValueRow()) {
                JsonObject tempSubtotal = new JsonObject();
                tempSubtotal.addProperty("estado", cellDetail.isIsValidCellData());
                tempSubtotal.addProperty("valor", cellDetail.getValueCell());
                tempSubtotal.addProperty("observacion", cellDetail.getMessageCellData());
                subtotal.add(tempSubtotal);
            }
            jSheetData.add("subtotal", subtotal);
        }  
        JsonArray dataBody = new JsonArray();
        JsonArray dataBodyObs = new JsonArray();
        for (XLSX_DetailRow row : detail_table.getValueBody()) {
            JsonObject rowBody = new JsonObject();
            JsonObject rowBodyObs = new JsonObject();
            rowBody.addProperty("estado", row.isIsValidRowData());            
            for (XLSX_DetailCell cellDetail : row.getValueRow()) {
                rowBody.addProperty(cellDetail.getLabelCell(), cellDetail.getValueCell());
                rowBodyObs.addProperty(cellDetail.getLabelCell(), cellDetail.getMessageCellData());
            }
            dataBody.add(rowBody);
            dataBodyObs.add(rowBodyObs);
        }                
        jSheetData.add("subtotal", subtotal);
        jSheetData.add("data", dataBody);
        jSheetData.add("dataObs", dataBodyObs);
        return jSheetData;
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
    
    
    //Validaciones Personalizadas
    private void valid_CustomFechas(Row row) {     
        Date rowDate,initDate;
        Date currentDate = new Date();        
        try {
            for (XLSX_DetailCell detailCell : detail_row.getValueRow()) {
                if(detailCell.getLabelCell().equalsIgnoreCase("fecha") && !detailCell.isIsEmptyCellData()){                    
                    rowDate = df.parse(detailCell.getValueCell());
                    initDate = df.parse("01/01/2018");
                    boolean validate = rowDate.compareTo(initDate) >= 0 && rowDate.compareTo(currentDate) <= 0;
                    if(!validate){
                        xlsx_setComment(row, detailCell.getIndex(), Mensajes.M_INVALID_LIMIT_DATE);
                        detailCell.setIsValidCellData(false);
                        detailCell.setMessageCellData(Mensajes.M_INVALID_LIMIT_DATE);
                        detail_row.setIsValidRowData(false);                        
                    }   
                    break;
                }
            }            
        } catch (Exception e) {
        }
    }
    private void valid_CustomComprobante(Row row) {
        if(detail_table.getNameFormat().equalsIgnoreCase("Anexo-5A")){
            String currentNumComprobante = "";
            int currentIndexRow = detail_row.getIndex();        
            for (XLSX_DetailCell detailCell : detail_row.getValueRow()) {
                if(detailCell.getLabelCell().equalsIgnoreCase("comprobante") && !detailCell.isIsEmptyCellData()){  
                    currentNumComprobante = detailCell.getValueCell();
                }
            }                 
            for (XLSX_DetailRow detailRow : detail_table.getValueBody()) {
                if(detailRow.getIndex() != currentIndexRow){
                    for (XLSX_DetailCell detailCell : detailRow.getValueRow()) {
                        if(detailCell.getLabelCell().equalsIgnoreCase("comprobante") && !detailCell.isIsEmptyCellData()){
                            if(currentNumComprobante.equalsIgnoreCase(detailCell.getValueCell())){
                                xlsx_setComment(row, detailCell.getIndex(), Mensajes.M_DUPLICATE_DOC);
                                detailCell.setIsValidCellData(false);
                                detailCell.setMessageCellData(Mensajes.M_INVALID_LIMIT_DATE);
                                detail_row.setIsValidRowData(false);                               
                            }
                        }
                    }                
                }
            }        
        }
    }    
    private void valid_CustomPadron(Row row, Formato formato, JsonObject coordinate, JsonArray jdata) {        
        if(detail_table.getNameFormat().equalsIgnoreCase("Anexo-5A")){
            
        
        }
        
        
        int response = 0;
        
        if (coordinate.get(FORMATO).getAsString().equalsIgnoreCase("Anexo-5A")) {
            JsonObject jRowData = jdata.get(jdata.size() - 1).getAsJsonObject();

            boolean isDocumento = jRowData.get(DOCUMENTO) != null;
            boolean isNombres = jRowData.get("nombres") != null;
            boolean isAppat = jRowData.get("apPaterno") != null;
            boolean isApmat = jRowData.get("apMaterno") != null;

            if (isDocumento) {
                String documento = jRowData.get(DOCUMENTO).getAsString();
                PadronDTO padron = padronTmpService.getPadron(documento);
                if (padron != null) {
                    String nombres = padron.getNombres();
                    String apPaterno = padron.getApPat();
                    String apMaterno = padron.getApMat();
                    if (isNombres && nombres != null) {
                        if (!nombres.equalsIgnoreCase(jRowData.get("nombres").getAsString().trim())) {
                            Cell cell = row.getCell(6);
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, nombres));
                            validData = false;
                            response++;
                        }
                    }
                    if (isAppat && apPaterno != null) {
                        if (!apPaterno.equalsIgnoreCase(jRowData.get("apPaterno").getAsString().trim())) {
                            Cell cell = row.getCell(4);
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, apPaterno));
                            validData = false;
                            response++;
                        }
                    }
                    if (isApmat && apMaterno != null) {
                        if (!apMaterno.equalsIgnoreCase(jRowData.get("apMaterno").getAsString().trim())) {
                            Cell cell = row.getCell(5);
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, apMaterno));
                            validData = false;
                            response++;
                        }
                    }
                } else {
                    Cell cell = row.getCell(7);
                    cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                    cell.setCellComment(getComentario(cell, Mensajes.M_NOFOUND_DNI));
                    validData = false;
                    response++;
                }
            }
        } else if (coordinate.get(FORMATO).getAsString().equalsIgnoreCase("Anexo-5C")) {
            JsonObject jRowData = jdata.get(jdata.size() - 1).getAsJsonObject();

            boolean isDocumento = jRowData.get(DOCUMENTO) != null;
            boolean isNombres = jRowData.get("nombres") != null;
            boolean isAppat = jRowData.get("apPaterno") != null;
            boolean isApmat = jRowData.get("apMaterno") != null;

            if (isDocumento) {
                String documento = jRowData.get(DOCUMENTO).getAsString();
                PadronDTO padron = padronTmpService.getPadron(documento);
                if (padron != null) {
                    String nombres = padron.getNombres();
                    String apPaterno = padron.getApPat();
                    String apMaterno = padron.getApMat();
                    if (isNombres && nombres != null) {
                        if (!nombres.equalsIgnoreCase(jRowData.get("nombres").getAsString().trim())) {
                            Cell cell = row.getCell(7);
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, nombres));
                            validData = false;
                            response++;
                        }
                    }
                    if (isAppat && apPaterno != null) {
                        if (!apPaterno.equalsIgnoreCase(jRowData.get("apPaterno").getAsString().trim())) {
                            Cell cell = row.getCell(5);
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, apPaterno));
                            validData = false;
                            response++;
                        }
                    }
                    if (isApmat && apMaterno != null) {
                        if (!apMaterno.equalsIgnoreCase(jRowData.get("apMaterno").getAsString().trim())) {
                            Cell cell = row.getCell(6);
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, apMaterno));
                            validData = false;
                            response++;
                        }
                    }
                } else {
                    Cell cell = row.getCell(8);
                    cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                    cell.setCellComment(getComentario(cell, Mensajes.M_NOFOUND_DNI));
                    validData = false;
                    response++;

                }
            }
        } else if (coordinate.get(FORMATO).getAsString().equalsIgnoreCase("Anexo-6B")) {
            JsonObject jRowData = jdata.get(jdata.size() - 1).getAsJsonObject();

            boolean isTipoDocumento = jRowData.get("tipoDocumento") != null;
            boolean isDocumento = jRowData.get(DOCUMENTO) != null;
            boolean isNombres = jRowData.get("razonSocial") != null;

            if (isTipoDocumento) {
                int tipoDocumento = jRowData.get("tipoDocumento").getAsInt();
                if (tipoDocumento == Validaciones.TYPEDOC_DNI) {
                    if (isDocumento) {
                        String documento = jRowData.get(DOCUMENTO).getAsString();
                        PadronDTO padron = padronTmpService.getPadron(documento);
                        if (padron != null) {
                            String nombresResponse = "";
                            if (padron.getApPat() != null) {
                                nombresResponse += padron.getApPat();
                            }
                            if (padron.getApMat() != null) {
                                nombresResponse += " " + padron.getApMat();
                            }
                            if (padron.getNombres() != null) {
                                nombresResponse += " " + padron.getNombres();
                            }
                            if (isNombres) {
                                if (!nombresResponse.equalsIgnoreCase(jRowData.get("razonSocial").getAsString().trim())) {
                                    Cell cell = row.getCell(4);
                                    cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                                    cell.setCellComment(getComentario(cell, nombresResponse));
                                    validData = false;
                                    response++;
                                }
                            }
                        } else {
                            Cell cell = row.getCell(6);
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, Mensajes.M_NOFOUND_DNI));
                            validData = false;
                            response++;
                        }
                    }
                }
            }
        } else if (coordinate.get(FORMATO).getAsString().equalsIgnoreCase("Anexo-6C")) {
            JsonObject jRowData = jdata.get(jdata.size() - 1).getAsJsonObject();

            boolean isTipoDocumento = jRowData.get("tipoDocumento") != null;
            boolean isDocumento = jRowData.get(DOCUMENTO) != null;
            boolean isNombres = jRowData.get("razonSocial") != null;

            if (isTipoDocumento) {
                int tipoDocumento = jRowData.get("tipoDocumento").getAsInt();
                if (tipoDocumento == Validaciones.TYPEDOC_DNI) {
                    if (isDocumento) {
                        String documento = jRowData.get(DOCUMENTO).getAsString();
                        PadronDTO padron = padronTmpService.getPadron(documento);
                        if (padron != null) {
                            String nombresResponse = "";
                            if (padron.getApPat() != null) {
                                nombresResponse += padron.getApPat();
                            }
                            if (padron.getApMat() != null) {
                                nombresResponse += " " + padron.getApMat();
                            }
                            if (padron.getNombres() != null) {
                                nombresResponse += " " + padron.getNombres();
                            }
                            if (isNombres) {
                                if (!nombresResponse.equalsIgnoreCase(jRowData.get("razonSocial").getAsString().trim())) {
                                    Cell cell = row.getCell(6);
                                    cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                                    cell.setCellComment(getComentario(cell, nombresResponse));
                                    validData = false;
                                    response++;
                                }
                            }
                        } else {
                            Cell cell = row.getCell(8);
                            cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
                            cell.setCellComment(getComentario(cell, Mensajes.M_NOFOUND_DNI));
                            validData = false;
                            response++;
                        }
                    }
                }
            }
        }
        return response;
    }


    //------------------------Utilitarios
    private void xlsx_setComment(Row row, int index, String message){
        Cell cell = row.getCell(index);
        cell.setCellStyle(styleSimpleCellObservation(workbook, (XSSFCellStyle) cell.getCellStyle()));
        cell.setCellComment(getComentario(cell, message));
    }
 
}
