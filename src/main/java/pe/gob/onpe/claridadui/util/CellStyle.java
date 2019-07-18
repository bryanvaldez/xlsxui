/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.util;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pe.gob.onpe.claridadui.Constants.Validaciones;

/**
 *
 * @author bvaldez
 */
public class CellStyle {
    
    public XSSFColor COLOR_CELL_ENUM = new XSSFColor(new java.awt.Color(215, 210, 183));    
    public XSSFColor YELLOW_CELL = new XSSFColor(new java.awt.Color(255, 255, 0)); 
        
    public XSSFCellStyle styleSimpleCellObservation(XSSFWorkbook wb, XSSFCellStyle cellStyle) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setDataFormat(cellStyle.getDataFormat());    
        style.setBorderBottom(cellStyle.getBorderBottom()); 
        style.setBorderTop(cellStyle.getBorderTop());
        style.setBorderRight(cellStyle.getBorderRight());
        style.setBorderLeft(cellStyle.getBorderLeft());
        style.setFillForegroundColor(YELLOW_CELL);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }    
//    public XSSFCellStyle styleCellObservation(XSSFWorkbook wb) {
//        XSSFCellStyle style = wb.createCellStyle();
//        style = getBorderThin(style);
//        style.setFillForegroundColor(YELLOW_CELL);
//        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        return style;
//    }       
    
    public XSSFCellStyle styleCell_Date(XSSFWorkbook wb, int type) {
        CreationHelper createHelper = wb.getCreationHelper();  
        XSSFCellStyle style = wb.createCellStyle();
        style = getBorderThin(style);
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
        if(type == Validaciones.SET_OBSERVATION){
            style.setFillForegroundColor(YELLOW_CELL);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);        
        }
        style.setAlignment(style.ALIGN_RIGHT);
        return style;
    }      
    
    
    
    public XSSFCellStyle styleCellAmountTotal(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        style.setFont(getfontTotal(wb));
        style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(YELLOW_CELL);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }    
    
    private XSSFFont getfontTotal(XSSFWorkbook wb) {
        XSSFFont font = wb.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        return font;
    }    
    
    
    private XSSFCellStyle getBorderThin(XSSFCellStyle style) {
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        return style;
    }    
    private XSSFCellStyle getBorderMedium(XSSFCellStyle style) {
        style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
        style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
        style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
        style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        return style;
    }    
    
}
