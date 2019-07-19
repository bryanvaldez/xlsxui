/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.model;

/**
 *
 * @author Bryan Luis Valdez Jara <ibryan.valdez@gmail.com>
 */
public class XLSX_DetailCell {
    
    private boolean isValidCell;
    private String labelCell;
    private String valueCell;
    private int orderCell;    
    private boolean isSumaCell;
    
    private boolean isValidCellData;
    private boolean isEmptyCellData;
    private String messageCellData;
    
    private int index;

    public XLSX_DetailCell() {
    }

    public XLSX_DetailCell(XLSX_DetailCell object) {
        this.isValidCell = object.isValidCell;
        this.labelCell = object.getLabelCell();
        this.valueCell = object.getValueCell();
        this.orderCell = object.getOrderCell();
        this.isSumaCell = object.isSumaCell;
        this.isValidCellData = object.isValidCellData;
        this.isEmptyCellData = object.isEmptyCellData;
        this.messageCellData = object.getMessageCellData();
        this.index = object.getIndex();
    }

    public boolean isIsValidCell() {
        return isValidCell;
    }

    public void setIsValidCell(boolean isValidCell) {
        this.isValidCell = isValidCell;
    }

    public String getLabelCell() {
        return labelCell;
    }

    public void setLabelCell(String labelCell) {
        this.labelCell = labelCell;
    }

    public String getValueCell() {
        return valueCell;
    }

    public void setValueCell(String valueCell) {
        this.valueCell = valueCell;
    }

    public int getOrderCell() {
        return orderCell;
    }

    public void setOrderCell(int orderCell) {
        this.orderCell = orderCell;
    }

    public boolean isIsSumaCell() {
        return isSumaCell;
    }

    public void setIsSumaCell(boolean isSumaCell) {
        this.isSumaCell = isSumaCell;
    }

    public boolean isIsValidCellData() {
        return isValidCellData;
    }

    public void setIsValidCellData(boolean isValidCellData) {
        this.isValidCellData = isValidCellData;
    }

    public boolean isIsEmptyCellData() {
        return isEmptyCellData;
    }

    public void setIsEmptyCellData(boolean isEmptyCellData) {
        this.isEmptyCellData = isEmptyCellData;
    }

    public String getMessageCellData() {
        return messageCellData;
    }

    public void setMessageCellData(String messageCellData) {
        this.messageCellData = messageCellData;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


}
