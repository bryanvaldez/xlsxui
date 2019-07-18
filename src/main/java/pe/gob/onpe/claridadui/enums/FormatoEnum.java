/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.enums;

/**
 *
 * @author bvaldez
 */
public enum FormatoEnum {
    FORMATO_5(5, "Cédula Aportes"), 
    FORMATO_6(6, "Cédula Gastos");
    
    private int id;
    private String nombre;

    private FormatoEnum() {
    }

    private FormatoEnum(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }        

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
}
