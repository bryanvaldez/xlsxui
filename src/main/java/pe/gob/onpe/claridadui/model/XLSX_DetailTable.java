/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.model;

import java.util.List;

/**
 *
 * @author Bryan Luis Valdez Jara <ibryan.valdez@gmail.com>
 */
public class XLSX_DetailTable {
    
    private String nameFormat;
    private List<XLSX_DetailRow> valueBody;
    private List<XLSX_DetailRow> valueSubtotal;
    private List<XLSX_DetailRow> valueTotal;

    public XLSX_DetailTable() {
    }
       
    public String getNameFormat() {
        return nameFormat;
    }

    public void setNameFormat(String nameFormat) {
        this.nameFormat = nameFormat;
    }

    public List<XLSX_DetailRow> getValueBody() {
        return valueBody;
    }

    public void setValueBody(List<XLSX_DetailRow> valueBody) {
        this.valueBody = valueBody;
    }

    public List<XLSX_DetailRow> getValueSubtotal() {
        return valueSubtotal;
    }

    public void setValueSubtotal(List<XLSX_DetailRow> valueSubtotal) {
        this.valueSubtotal = valueSubtotal;
    }

    public List<XLSX_DetailRow> getValueTotal() {
        return valueTotal;
    }

    public void setValueTotal(List<XLSX_DetailRow> valueTotal) {
        this.valueTotal = valueTotal;
    }
    

    
}