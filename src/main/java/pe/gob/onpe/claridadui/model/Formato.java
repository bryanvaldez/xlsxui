/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.model;

import java.util.List;

/**
 *
 * @author bvaldez
 */
public class Formato {
    private int id;
    private String descripcion;
    private String detalleHoja;
    private int cantidadHoja;
    private int tipoFormato;
    private String indicacion;    
    private List<DetalleFormato> detalle;   
    private String rutaObservaciones;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDetalleHoja() {
        return detalleHoja;
    }

    public void setDetalleHoja(String detalleHoja) {
        this.detalleHoja = detalleHoja;
    }

    public int getCantidadHoja() {
        return cantidadHoja;
    }

    public void setCantidadHoja(int cantidadHoja) {
        this.cantidadHoja = cantidadHoja;
    }

    public int getTipoFormato() {
        return tipoFormato;
    }

    public void setTipoFormato(int tipoFormato) {
        this.tipoFormato = tipoFormato;
    }

    public String getIndicacion() {
        return indicacion;
    }

    public void setIndicacion(String indicacion) {
        this.indicacion = indicacion;
    }

    public List<DetalleFormato> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<DetalleFormato> detalle) {
        this.detalle = detalle;
    }

    public String getRutaObservaciones() {
        return rutaObservaciones;
    }

    public void setRutaObservaciones(String rutaObservaciones) {
        this.rutaObservaciones = rutaObservaciones;
    }
    
}
