/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.model;

import com.google.gson.JsonArray;
import java.util.List;

/**
 *
 * @author Bryan Luis Valdez Jara <ibryan.valdez@gmail.com>
 */
public class XLSX_DetailRow {
    
    private List<XLSX_DetailCell> valueRow;
    private List<XLSX_DetailAmount> amountRow;
    private boolean isValidRow;
    private boolean isValidRowData;

    public XLSX_DetailRow() {
    }       

    public XLSX_DetailRow(XLSX_DetailRow object) {
        this.valueRow = object.getValueRow();
        this.amountRow = object.getAmountRow();
        this.isValidRow = object.isValidRow;
        this.isValidRowData = object.isValidRowData;
    }        
    
    public List<XLSX_DetailCell> getValueRow() {
        return valueRow;
    }

    public void setValueRow(List<XLSX_DetailCell> valueRow) {
        this.valueRow = valueRow;
    }

    public List<XLSX_DetailAmount> getAmountRow() {
        return amountRow;
    }

    public void setAmountRow(List<XLSX_DetailAmount> amountRow) {
        this.amountRow = amountRow;
    }

    public boolean isIsValidRow() {
        return isValidRow;
    }

    public void setIsValidRow(boolean isValidRow) {
        this.isValidRow = isValidRow;
    }

    public boolean isIsValidRowData() {
        return isValidRowData;
    }

    public void setIsValidRowData(boolean isValidRowData) {
        this.isValidRowData = isValidRowData;
    }


}
