/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.service.iface;

import java.util.List;
import pe.gob.onpe.claridadui.model.DetalleInforme;
import pe.gob.onpe.claridadui.model.Formato;

/**
 *
 * @author bvaldez
 */
public interface IFormatoService {
    public Formato getFormato(int type);  
    public List<DetalleInforme> getDataInforme(int type); 
}
