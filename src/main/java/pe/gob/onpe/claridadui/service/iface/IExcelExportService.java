/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.service.iface;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author bvaldez
 */
public interface IExcelExportService {
    public XSSFWorkbook export();
}
