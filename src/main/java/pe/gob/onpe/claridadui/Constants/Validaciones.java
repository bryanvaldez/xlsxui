/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.Constants;

/**
 *
 * @author bvaldez
 */
public class Validaciones {
    
    public static final int FORMAT_READER = 0; 
    public static final int FORMAT_WRITE = 1;     
    
    public static final int FORMAT_5 = 5; 
    public static final int FORMAT_6 = 6;     
    
    public static final int T_TABLE = 0; 
    public static final int T_SUBTOTAL = 1;   
    public static final int T_TOTAL = 2;       
        
    public static final int FORMAT_REQUIRED = 1;  
    public static final double UIT = 4150;      
    
    public static final int SET_CLEAN = 0;    
    public static final int SET_OBSERVATION = 1;  
    
    public static final int TYPEDOC_DNI = 1;  
    public static final int TYPEDOC_RUC = 2;  
    public static final int TYPEDOC_ETC = 3;          
    
    
    public static final String MONTO = "^\\d+(\\.\\d{1,2})?$";     
    public static final String FECHA = "^(\\d{2}\\/([0][1-9]|[1][0-2])\\/(199[6-9]|2[0-9]{3}))$"; 
    public static final String TABLA1 = "^(1|2|3|4|5|1.0|2.0|3.0|4.0|5.0)$"; 
    public static final String TABLA2 = "^(1|2|3|4|5|6|1.0|2.0|3.0|4.0|5.0|6.0)$";
    public static final String TABLA3 = "^(1|2|3|4|5|1.0|2.0|3.0|4.0|5.0)$";     
    public static final String SUSTENTO = "^(.{1,150})$"; 
    public static final String NOMBRES = "^(.{1,150})$"; 
    public static final String DOCUMENTO = "^(.{1,150})$"; 
    public static final String PROCEDENCIA = "^(.{1,350})$"; 
    public static final String LUGAR = "^(.{1,350})$"; 
    public static final String DIRECCION = "^(.{1,350})$"; 
    public static final String DETALLE = "^(.{1,500})$";
    public static final String COMPROB = "^(.{1,50})$";
    
    public static final String MEDIO = "^(1|2|3|4|1.0|2.0|3.0|4.0)$";
    public static final String RAZON = "^(.{1,400})$";
    public static final String RUC = "^([0-9]{1,12})$";
    public static final String TIPCOMPROB = "^(1|2|3|4|1.0|2.0|3.0|4.0)$"; 
    public static final String NUMCOMPROB = "^(.{1,50})"; 
    public static final String TIPOPAGO = "^(1|2|1.0|2.0)$"; 
    public static final String ESPECIF = "^(.{1,400})$";
    
    public static final String TIPOGASTO = "^(1|2|3|4|5|1.0|2.0|3.0|4.0|5.0)$";
    public static final String TIPODOC = "^(1|2|3|1.0|2.0|3.0)$";     
    
}
