/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.claridadui.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import pe.gob.onpe.claridadui.Constants.Comentarios;
import pe.gob.onpe.claridadui.Constants.Mensajes;
import pe.gob.onpe.claridadui.Constants.Validaciones;
import pe.gob.onpe.claridadui.enums.FormatoEnum;
import pe.gob.onpe.claridadui.model.DetalleFormato;
import pe.gob.onpe.claridadui.model.DetalleInforme;
import pe.gob.onpe.claridadui.model.Formato;
import pe.gob.onpe.claridadui.service.iface.IFormatoService;

/**
 *
 * @author bvaldez
 */
public class FormatoService implements IFormatoService {

    @Override
    public Formato getFormato(int type) {
        Formato formato = new Formato();
        if (validFormat(type)) {
            formato.setId(type);
            formato.setDescripcion(getDescription(type));
            formato.setDetalleHoja(getSheetDetail(type));
            formato.setCantidadHoja(getNumberSheet(type));
            formato.setTipoFormato(type);
            formato.setIndicacion(getIndication(type));
            formato.setRutaObservaciones(pathObs(type));
            formato.setDetalle(getDetalle(type));
        }
        return formato;
    }

    private boolean validFormat(int type) {
        if (type == FormatoEnum.FORMATO_5.getId()) {
            return true;
        } else if (type == FormatoEnum.FORMATO_6.getId()) {
            return true;
        }
        return false;
    }

    private String getDescription(int type) {
        String response = "";
        if (type == FormatoEnum.FORMATO_5.getId()) {
            response = "Cédula de Aportaciones del Financimiento Privado";

        } else if (type == FormatoEnum.FORMATO_6.getId()) {
            response = "Cédula de Gastos del Financimiento Privado";
        }
        return new Gson().toJson(response);
    }

    private String getSheetDetail(int type) {
        JsonArray jResponse = new JsonArray();
        if (type == FormatoEnum.FORMATO_5.getId()) {
            JsonObject hoja1 = new JsonObject();
            JsonObject hoja2 = new JsonObject();
            JsonObject hoja3 = new JsonObject();
            JsonObject hoja4 = new JsonObject();

            hoja1.addProperty("hoja", 1);
            hoja1.addProperty("isIndex", true);
            hoja1.addProperty("descripcion", "Formato-5");            
            hoja1.addProperty("iniTabla", "TOTAL S/");
            hoja1.addProperty("subtotal", "");
            hoja1.addProperty("total", "TOTAL INGRESOS DE CAMPAÑA");
            
            hoja2.addProperty("hoja", 2);
            hoja2.addProperty("isIndex", false);
            hoja2.addProperty("descripcion", "Anexo-5A");
            hoja2.addProperty("iniTabla", "Apellido Paterno");
            hoja2.addProperty("subtotal", "SUBTOTALES");
            hoja2.addProperty("total", "TOTAL INGRESOS"); 
            
            hoja3.addProperty("hoja", 3);
            hoja3.addProperty("isIndex", false);
            hoja3.addProperty("descripcion", "Anexo-5B");
            hoja3.addProperty("iniTabla", "Fecha de la actividad");
            hoja3.addProperty("subtotal", "");
            hoja3.addProperty("total", "TOTAL INGRESOS");
            
            hoja4.addProperty("hoja", 4);
            hoja4.addProperty("isIndex", false);
            hoja4.addProperty("descripcion", "Anexo-5C");
            hoja4.addProperty("iniTabla", "Fecha del ingreso");
            hoja4.addProperty("subtotal", "");
            hoja4.addProperty("total", "Total");   
            
            jResponse.add(hoja1);
            jResponse.add(hoja2);
            jResponse.add(hoja3);
            jResponse.add(hoja4);

        } else if (type == FormatoEnum.FORMATO_6.getId()) {
            JsonObject hoja1 = new JsonObject();
            JsonObject hoja2 = new JsonObject();
            JsonObject hoja3 = new JsonObject();
            JsonObject hoja4 = new JsonObject();

            hoja1.addProperty("hoja", 1);
            hoja1.addProperty("isIndex", true);
            hoja1.addProperty("descripcion", "Formato-6");
            hoja1.addProperty("iniTabla", "TOTAL S/");
            hoja1.addProperty("subtotal", "");
            hoja1.addProperty("total", "TOTAL GASTOS DE CAMPAÑA");            
            
            hoja2.addProperty("hoja", 2);
            hoja2.addProperty("isIndex", false);
            hoja2.addProperty("descripcion", "Anexo-6A");
            hoja2.addProperty("iniTabla", "Monto  S/.");
            hoja2.addProperty("subtotal", "");
            hoja2.addProperty("total", "TOTAL");            
            
            hoja3.addProperty("hoja", 3);
            hoja3.addProperty("isIndex", false);
            hoja3.addProperty("descripcion", "Anexo-6B");
            hoja3.addProperty("iniTabla", "Monto S/");
            hoja3.addProperty("subtotal", "");
            hoja3.addProperty("total", "TOTAL");            
            
            hoja4.addProperty("hoja", 4);
            hoja4.addProperty("isIndex", false);
            hoja4.addProperty("descripcion", "Anexo-6C");
            hoja4.addProperty("iniTabla", "Monto S/");
            hoja4.addProperty("subtotal", "");
            hoja4.addProperty("total", "TOTAL");            

            jResponse.add(hoja1);
            jResponse.add(hoja2);
            jResponse.add(hoja3);
            jResponse.add(hoja4);
        }
        return new Gson().toJson(jResponse);
    }

    private String getIndication(int type) {
        JsonObject jResponse = new JsonObject();
        if (type == FormatoEnum.FORMATO_5.getId()) {
            jResponse.addProperty("plantilla", "Plantilla para la Cédula de Aportaciones del Financimiento Privado");
            jResponse.addProperty("carga", "Descargue la plantilla.");
            jResponse.addProperty("archivo", "F5_CEDULA_INGRESOS_FPD");
        } else if (type == FormatoEnum.FORMATO_6.getId()) {
            jResponse.addProperty("plantilla", "Plantilla para la Cédula de Gastos del Financimiento Privado");
            jResponse.addProperty("carga", "Descargue la plantilla.");
            jResponse.addProperty("archivo", "F6_CEDULA_GASTOS_FPD");
        }
        return new Gson().toJson(jResponse);

    }
    
    private int getNumberSheet(int type){
        if (type == FormatoEnum.FORMATO_5.getId()) {
            return 4;
        } else if (type == FormatoEnum.FORMATO_6.getId()) {
            return 4;
        }
        return 0;    
    }
    
    private String pathObs(int type){
        if (type == FormatoEnum.FORMATO_5.getId()) {
            return "D:\\CLARIDAD3\\OBSERVACIONES\\INGRESOS";
        } else if (type == FormatoEnum.FORMATO_6.getId()) {
            return "D:\\CLARIDAD3\\OBSERVACIONES\\GASTOS";
        }
        return "";     
    }
    
    private List<DetalleFormato> getDetalle(int type){
        List<DetalleFormato> response = new ArrayList<>();        
        if (type == FormatoEnum.FORMATO_5.getId()) {
            response = getIncomeDetail();
        } else if (type == FormatoEnum.FORMATO_6.getId()) {
            response = getExpensesDetail();
        }
        return response;        
    }

    private List<DetalleFormato> getIncomeDetail(){
        List<DetalleFormato> p = new ArrayList<>();
        //PARAMETROS DE LECTURA - FORMATO 5
        p.add(new DetalleFormato(1, 8, 10, "5A", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 0, 0, 0, Validaciones.T_TABLE, true));
        p.add(new DetalleFormato(1, 8, 11, "5B", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 0, 0, 0, Validaciones.T_TABLE, true));
        p.add(new DetalleFormato(1, 8, 12, "5C", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 0, 0, 0, Validaciones.T_TABLE, true));
        p.add(new DetalleFormato(1, 8, 13, "total", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 1, 0, 0, Validaciones.T_TOTAL, true));
          
        //PARAMETROS DE LECTURA ANEXO 5A 
        p.add(new DetalleFormato(2, 2, 0, "fecha", Validaciones.FECHA, Mensajes.I_FECHA, Comentarios.I_FECHA, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(2, 3, 0, "numComprobante", Validaciones.COMPROB, Mensajes.I_COMPROBANTE, Comentarios.I_COMPROBANTE, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(2, 4, 0, "apPaterno", Validaciones.NOMBRES, Mensajes.I_NOMBRES, Comentarios.I_NOMBRES, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(2, 5, 0, "apMaterno", Validaciones.NOMBRES, Mensajes.I_NOMBRES, Comentarios.I_NOMBRES, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(2, 6, 0, "nombres", Validaciones.NOMBRES, Mensajes.I_NOMBRES, Comentarios.I_NOMBRES, 0, 1, 5, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(2, 7, 0, "documento", Validaciones.DOCUMENTO, Mensajes.I_DOCUMENTO, Comentarios.I_DOCUMENTO, 0, 1, 0, 0, Validaciones.T_TABLE, false));                
        p.add(new DetalleFormato(2, 8, 0, "direccion", Validaciones.DIRECCION, Mensajes.I_DIRECCION, Comentarios.I_DIRECCION, 0, 0, 0, 0, Validaciones.T_TABLE, false));        
        p.add(new DetalleFormato(2, 9, 0, "codAporte", Validaciones.TABLA1, Mensajes.I_TABLA1, Comentarios.I_TABLA1, 0, 0, 0, 0, Validaciones.T_TABLE, false));         
        p.add(new DetalleFormato(2, 10, 0, "montoEfectivo", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 0, 1, 0, Validaciones.T_TABLE, true));
        p.add(new DetalleFormato(2, 11, 0, "montoEspecie", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 0, 2, 0, Validaciones.T_TABLE, true));        
        p.add(new DetalleFormato(2, 12, 0, "detalle", Validaciones.DETALLE, Mensajes.I_DETALLE, Comentarios.I_DETALLE, 0, 0, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(2, 10, 0, "subTotalEfectivo", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 1, 1, 0, Validaciones.T_SUBTOTAL, true)); 
        p.add(new DetalleFormato(2, 11, 0, "subTotalEspecie", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 1, 2, 0, Validaciones.T_SUBTOTAL, true));         
        p.add(new DetalleFormato(2, 10, 0, "total", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 1, 0, 0, Validaciones.T_TOTAL, true)); 
        
        //PARAMETROS DE LECTURA ANEXO 5B        
        p.add(new DetalleFormato(3, 2, 0, "fecha", Validaciones.FECHA, Mensajes.I_FECHA, Comentarios.I_FECHA, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(3, 3, 0, "codActividad", Validaciones.TABLA2, Mensajes.I_TABLA2, Comentarios.I_TABLA2, 0, 1, 0, 0, Validaciones.T_TABLE, false)); 
        p.add(new DetalleFormato(3, 4, 0, "lugarActividad", Validaciones.LUGAR, Mensajes.I_LUGAR, Comentarios.I_LUGAR, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(3, 6, 0, "monto", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 1, 0, 0, Validaciones.T_TABLE, true));
        p.add(new DetalleFormato(3, 7, 0, "detalle", Validaciones.DETALLE, Mensajes.I_DETALLE, Comentarios.I_DETALLE, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(3, 6, 0, "total", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 1, 0, 0, Validaciones.T_TOTAL, true));          
        
        //PARAMETROS DE LECTURA ANEXO 5C        
        p.add(new DetalleFormato(4, 2, 0, "fecha", Validaciones.FECHA, Mensajes.I_FECHA, Comentarios.I_FECHA, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(4, 3, 0, "codAporte", Validaciones.TABLA3, Mensajes.I_TABLA3, Comentarios.I_TABLA3, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(4, 4, 0, "sustento", Validaciones.SUSTENTO, Mensajes.I_SUSTENTO, Comentarios.I_SUSTENTO, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(4, 5, 0, "apPaterno", Validaciones.NOMBRES, Mensajes.I_NOMBRES, Comentarios.I_NOMBRES, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(4, 6, 0, "apMaterno", Validaciones.NOMBRES, Mensajes.I_NOMBRES, Comentarios.I_NOMBRES, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(4, 7, 0, "nombres", Validaciones.NOMBRES, Mensajes.I_NOMBRES, Comentarios.I_NOMBRES, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(4, 8, 0, "documento", Validaciones.DOCUMENTO, Mensajes.I_DOCUMENTO, Comentarios.I_DOCUMENTO, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(4, 9, 0, "procedencia", Validaciones.PROCEDENCIA, Mensajes.I_PROCEDENCIA, Comentarios.I_PROCEDENCIA, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(4, 10, 0, "aporte", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 1, 0, 0, Validaciones.T_TABLE, true));        
        p.add(new DetalleFormato(4, 10, 0, "total", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 1, 0, 0, Validaciones.T_TOTAL, true));          
        
        return p;         
    }
    
    private List<DetalleFormato> getExpensesDetail(){
        List<DetalleFormato> p = new ArrayList<>();        
        //PARAMETROS DE LECTURA - FORMATO 6
        p.add(new DetalleFormato(1, 7, 8, "6A", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 0, 0, 0, Validaciones.T_TABLE, true));
        p.add(new DetalleFormato(1, 7, 9, "6B", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 0, 0, 0, Validaciones.T_TABLE, true));
        p.add(new DetalleFormato(1, 7, 10, "6C", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 0, 0, 0, Validaciones.T_TABLE, true));
        p.add(new DetalleFormato(1, 7, 11, "total", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 1, 0, 0, Validaciones.T_TOTAL, true));   
        
        //PARAMETROS DE LECTURA ANEXO 6A
        p.add(new DetalleFormato(2, 2, 0, "fecha", Validaciones.FECHA, Mensajes.I_FECHA, Comentarios.I_FECHA, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(2, 3, 0, "tipoMedio", Validaciones.MEDIO, Mensajes.I_MEDIO, Comentarios.I_MEDIO, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(2, 4, 0, "razonSocial", Validaciones.RAZON, Mensajes.I_RAZON, Comentarios.I_RAZON, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(2, 5, 0, "ruc", Validaciones.RUC, Mensajes.I_RUC, Comentarios.I_RUC, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(2, 6, 0, "tipoComprobante", Validaciones.TIPCOMPROB, Mensajes.I_TIPCOMPROB, Comentarios.I_TIPCOMPROB, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(2, 7, 0, "numComprobante", Validaciones.NUMCOMPROB, Mensajes.I_NUMCOMPROB, Comentarios.I_NUMCOMPROB, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(2, 8, 0, "tipoPago", Validaciones.TIPOPAGO, Mensajes.I_TIPOPAGO, Comentarios.I_TIPOPAGO, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(2, 9, 0, "monto", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 1, 0, 0, Validaciones.T_TABLE, true));
        p.add(new DetalleFormato(2, 10, 0, "descripcion", Validaciones.ESPECIF, Mensajes.I_ESPECIFIC, Comentarios.I_ESPECIFIC, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(2, 9, 0, "total", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 1, 0, 0, Validaciones.T_TOTAL, true));
        
        //PARAMETROS DE LECTURA ANEXO 6B
        p.add(new DetalleFormato(3, 2, 0, "fecha", Validaciones.FECHA, Mensajes.I_FECHA, Comentarios.I_FECHA, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(3, 3, 0, "tipoGasto", Validaciones.TIPOGASTO, Mensajes.I_TIPOGASTO, Comentarios.I_TIPOGASTO, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(3, 4, 0, "razonSocial", Validaciones.RAZON, Mensajes.I_RAZON, Comentarios.I_RAZON, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(3, 5, 0, "tipoDocumento", Validaciones.TIPODOC, Mensajes.I_TIPODOC, Comentarios.I_TIPODOC, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(3, 6, 0, "documento", Validaciones.DOCUMENTO, Mensajes.I_DOCUMENTO, Comentarios.I_DOCUMENTO, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(3, 7, 0, "tipoComprobante", Validaciones.TIPCOMPROB, Mensajes.I_TIPCOMPROB, Comentarios.I_TIPCOMPROB, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(3, 8, 0, "numComprobante", Validaciones.NUMCOMPROB, Mensajes.I_NUMCOMPROB, Comentarios.I_NUMCOMPROB, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(3, 9, 0, "tipoPago", Validaciones.TIPOPAGO, Mensajes.I_TIPOPAGO, Comentarios.I_TIPOPAGO, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(3, 10, 0, "monto", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 1, 0, 0, Validaciones.T_TABLE, true));        
        p.add(new DetalleFormato(3, 11, 0, "descripcion", Validaciones.ESPECIF, Mensajes.I_ESPECIFIC, Comentarios.I_ESPECIFIC, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(3, 10, 0, "total", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 1, 0, 0, Validaciones.T_TOTAL, true));
                
        //PARAMETROS DE LECTURA ANEXO 6C
        p.add(new DetalleFormato(4, 2, 0, "fecha", Validaciones.FECHA, Mensajes.I_FECHA, Comentarios.I_FECHA, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(4, 3, 0, "concepto", Validaciones.ESPECIF, Mensajes.I_ESPECIFIC, Comentarios.I_ESPECIFIC, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(4, 4, 0, "tipoPago", Validaciones.TIPOPAGO, Mensajes.I_TIPOPAGO, Comentarios.I_TIPOPAGO, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(4, 5, 0, "monto", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 1, 0, 0, Validaciones.T_TABLE, true));         
        p.add(new DetalleFormato(4, 6, 0, "razonSocial", Validaciones.RAZON, Mensajes.I_RAZON, Comentarios.I_RAZON, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(4, 7, 0, "tipoDocumento", Validaciones.TIPODOC, Mensajes.I_TIPODOC, Comentarios.I_TIPODOC, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(4, 8, 0, "documento", Validaciones.DOCUMENTO, Mensajes.I_DOCUMENTO, Comentarios.I_DOCUMENTO, 0, 1, 0, 0, Validaciones.T_TABLE, false));   
        p.add(new DetalleFormato(4, 9, 0, "tipoComprobante", Validaciones.TIPCOMPROB, Mensajes.I_TIPCOMPROB, Comentarios.I_TIPCOMPROB, 0, 1, 0, 0, Validaciones.T_TABLE, false));
        p.add(new DetalleFormato(4, 10, 0, "numComprobante", Validaciones.NUMCOMPROB, Mensajes.I_NUMCOMPROB, Comentarios.I_NUMCOMPROB, 0, 1, 0, 0, Validaciones.T_TABLE, false));        
        p.add(new DetalleFormato(4, 5, 0, "total", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 1, 0, 0, Validaciones.T_TOTAL, true));
        
        return p;
    }
    
    
    private List<DetalleFormato> getIncomeExportDetail(){
        List<DetalleFormato> p = new ArrayList<>();
        
        p.add(new DetalleFormato(1, 1, 2, "simbolo", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 0, 0, 0, Validaciones.T_TABLE, true));
        p.add(new DetalleFormato(1, 7, 9, "6B", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 0, 0, 0, Validaciones.T_TABLE, true));
        p.add(new DetalleFormato(1, 7, 10, "6C", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 0, 0, 0, Validaciones.T_TABLE, true));
        p.add(new DetalleFormato(1, 7, 11, "total", Validaciones.MONTO, Mensajes.I_MONTO, Comentarios.I_MONTO, 0, 1, 0, 0, Validaciones.T_TOTAL, true));           
        return p;
    }
    
    @Override
    public List<DetalleInforme> getDataInforme(int type) {
        
        DateFormat dateFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        Date date = new Date();
        
        String cInforme = "0xx-2019-PAS-JANRFP-SGTN-GSFP/ONPE";
        String cResolucion = "0xx-2019-PAS-JANRFP-SGTN-GSFP/ONPE";
        String nCandidato = "JORGE DIAZ CASTILLO";
        String fResolucion = dateFormat.format(date);

        
        
        
        List<DetalleInforme> p = new ArrayList<>();        
        switch (type) {
            case 13:
                p.add(new DetalleInforme(1,"Arial",16,true, "1 Reglamento de Financiamiento y Supervisión de Fondos Partidarios"));
                p.add(new DetalleInforme(2,"Arial",16,true, "\"Artículo 119°.- Inicio del procedimiento"));
                p.add(new DetalleInforme(3,"Arial",16,true, "Las acciones u omisiones referidas a eventuales infracciones sancionables de una organización política, candidato a cargo de elección popular, del promotor o de la autoridad sometida a la consulta popular de revocatoria, son evaluadas por la Gerencia para determinar si concurren las circunstancias que justifiquen el inicio del procedimiento administrativo sancionador."));
                p.add(new DetalleInforme(4,"Arial",16,true, "Con la notificación de la resolución de la Gerencia, se da inicio al procedimiento administrativo sancionador.\""));
                break;            
            case 12:
                p.add(new DetalleInforme(1,"Arial",16,true, "Lima, "+fResolucion));
                break;              
            case 11:
                p.add(new DetalleInforme(1,"Arial",16,true, "INFORME "+cInforme));
                p.add(new DetalleInforme(2,"Arial",12,true, "INFORME SOBRE LAS ACTUACIONES PREVIAS AL INICIO DEL PROCEDIMIENTO ADMINISTRATIVO SANCIONADOR CONTRA EL CANDIDATO \""+nCandidato+"\" POR NO PRESENTAR LA INFORMACIÓN SOBRE LAS APORTACIONES E INGRESOS RECIBIDOS Y SOBRE LOS GASTOS EFECUADOS DURANTE LA CAMPAÑA ELECTORAL EN LAS ELECCIONES REGIONALES Y MUNICIPALES 2018 EN EL PLAZO ESTABLECIDO POR LEY"));
                p.add(new DetalleInforme(3,"Calibri",10,true, "Jefatura del Área de Normativa y Regulación de Finanzas Partidarias\n Gerencia de Supervisión de Fondos Partidarios\n Enero 2019 "));
                break;                    
            case 0:
                p.add(new DetalleInforme(0,null,13,true, "ANTECEDENTES"));
                p.add(new DetalleInforme(0,null,13,true, "BASE LEGAL"));
                p.add(new DetalleInforme(0,null,13,true, "ANÁLISIS"));
                p.add(new DetalleInforme(0,null,13,true, "CONCLUSIONES"));
                p.add(new DetalleInforme(0,null,13,true, "RECOMENDACIÓN"));
                break;
            case 1:
                p.add(new DetalleInforme(1,null,13,false,"Mediante Resoluciones Nos. 3591 y 3594-2018-JNE, publicadas en el diario oficial El Peruano el 28 de diciembre de 2018, el Pleno del Jurado Nacional de Elecciones (en adelante JNE), en uso de sus atribuciones, declaró concluidos los procesos de Elecciones Municipales 2018 y Elecciones Regionales 2018, convocados mediante  Decreto Supremo Nº 004-2018-PCM (Anexo A y B)."));                                
                p.add(new DetalleInforme(2,null,13,false,"Mediante Resolución Jefatural Nº 0000XX-201X-JN/ONPE del XX de XXX de 201X, publicada en el diario oficial El Peruano el XX de enero de 2019, la Oficina Nacional de Procesos Electorales (en adelante ONPE) fijó el 21 de enero de 2019, como último día para que las organizaciones políticas y los responsables de campaña presenten la información sobre las aportaciones e ingresos recibidos y sobre los gastos efectuados durante la campaña electoral en las Elecciones Regionales y Municipales 2018 (Anexo C)."));                                
                p.add(new DetalleInforme(3,null,13,false,"Mediante Oficio Nº 0000xx- 2019- /ONPE del XX de enero de 2019, la ONPE informó que el último día para que las organizaciones políticas y los responsables de campaña presenten la información sobre las aportaciones e ingresos recibidos y sobre los gastos efectuados durante la campaña electoral en las Elecciones Regionales y Municipales 2018; vencía el 21 de enero de 2019 (Anexo D)."));                                                
                p.add(new DetalleInforme(4,null,13,false,"Mediante Nota de Prensa publicada el XX de enero de 2019 en la página web de la ONPE, se precisó que el plazo para que las organizaciones políticas y los responsables de campaña presenten la información sobre las aportaciones e ingresos recibidos y sobre los gastos efectuados durante la campaña electoral en las Elecciones Regionales y Municipales 2018; vencía el 21 de enero de 2019 (Anexo E)."));                                
                p.add(new DetalleInforme(5,null,13,false,"Mediante Informe Nº 0000XX-2019-JAVC-SGVC-GSFP/ONPE del XX de enero de 2019, el Jefe (e) de Área de Verificación y Control de la Gerencia de Supervisión de Fondos Partidarios (en adelante GSFP), remitió la relación de los candidatos que no cumplieron con presentar su información sobre las aportaciones e ingresos recibidos y sobre los gastos efectuados durante la campaña electoral en las Elecciones Regionales y Municipales 2018 hasta el 21 de enero de 2019 (Anexo F)."));                
                p.add(new DetalleInforme(6,null,13,false,"Mediante consulta realizada en el portal institucional del JNE   https://plataformaelectoral.jne.gob.pe/ListaDeCandidatos/; se advierte que el ciudadano “XXXXX” figura como candidato en la solicitud presentada por su organización política ante el JNE para su participación en las Elecciones Regionales y Municipales 2018 (Anexo G)."));
                break;
            case 2:
                p.add(new DetalleInforme(1,null,13,false, "Ley N° 28094, Ley de Organizaciones Políticas y sus modificatorias (en adelante LOP)."));
                p.add(new DetalleInforme(2,null,13,false, "Texto Único Ordenado de la Ley N° 27444, Ley del Procedimiento Administrativo General, aprobado mediante Decreto Supremo N° 006-2017-JUS (en adelante TUO de LPAG)."));
                p.add(new DetalleInforme(3,null,13,false, "Resolución Jefatural N° 025-2018-J/ONPE que aprueba el Reglamento de Financiamiento y Supervisión de Fondos Partidarios y sus modificatorias (en adelante RFSFP)."));
                break;
            case 3:
                p.add(new DetalleInforme(1,null,13,false, "El artículo 30-A de la LOP establece que los ingresos y gastos efectuados por el candidato deben de ser informados a la GSFP de la ONPE a través de los medios que esta disponga y en los plazos señalados en el reglamento correspondiente, con copia a la organización política."));
                p.add(new DetalleInforme(2,null,13,false, "Por su parte, el numeral 34.6 del artículo 34° de la LOP, establece que las organizaciones políticas y los responsables de campaña, de ser el caso, presentan informes a la GSFP de la ONPE, sobre las aportaciones e ingresos recibidos y sobre los gastos que efectúan durante la campaña electoral, en un plazo no mayor de quince (15) días hábiles contados a partir del día siguiente de la publicación en el diario oficial El Peruano de la Resolución que declara la conclusión del proceso electoral que corresponda."));
                p.add(new DetalleInforme(3,null,13,false, "En ese contexto, a través de las Resoluciones Nos. 3591 y 3594-2018-JNE, publicadas en el diario oficial El Peruano el 28 de diciembre de 2018, el Pleno del JNE, en uso de sus atribuciones, declaró concluidos los procesos de Elecciones Municipales 2018 y Elecciones Regionales 2018, convocados mediante el Decreto Supremo Nº 004-2018-PCM."));
                p.add(new DetalleInforme(4,null,13,false, "Conforme se desprende en los documentos señalados en los antecedentes del presente informe, se advierte que la ONPE, informó oportunamente a las organizaciones políticas, los candidatos y los responsables de campaña, que la fecha de presentación de la información sobre las aportaciones e ingresos recibidos y sobre los gastos efectuados durante la campaña electoral en las Elecciones Regionales y Municipales 2018; vencía el 21 de enero de 2019."));
                p.add(new DetalleInforme(5,null,13,false, "Cabe agregar, que el Jefe (e) de Área de Verificación y Control de la GSFP, mediante Informe Nº 0000xx-2019-JAVC-SGVC-GSFP/ONPE; remitió la relación de los candidatos que no cumplieron con presentar su información sobre las aportaciones e ingresos recibidos y sobre los gastos efectuados durante la campaña electoral en las Elecciones Regionales y Municipales 2018 hasta el 21 de enero de 2019."));
                p.add(new DetalleInforme(6,null,13,false, "Sobre el particular, de la consulta realizada en el portal institucional del JNE   https://plataformaelectoral.jne.gob.pe/ListaDeCandidatos/; se advierte que el ciudadano “XXXXX” figura como candidato en la solicitud presentada por su organización política ante el JNE para su participación en las Elecciones Regionales y Municipales 2018."));
                p.add(new DetalleInforme(7,null,13,false, "Ahora bien, el último párrafo del artículo 30-A de la LOP, establece que el incumplimiento de la información sobre los ingresos y gastos efectuados por el candidato en el plazo señalado es de responsabilidad exclusiva del candidato y de su responsable de campaña; asimismo, el último párrafo del artículo 97° del RFSFP, prescribe que el candidato es responsable por las acciones y/u omisiones que realice su responsable de campaña y será sancionado conforme a lo establecido en el artículo 111° del RFSFP."));
                p.add(new DetalleInforme(8,null,13,false, "En esa línea, el artículo 36-B de la LOP, contempla que los candidatos que no informen a la GSFP de la ONPE, de los gastos e ingresos efectuados durante su campaña son sancionados con una multa. Asimismo, el numeral 1) del artículo 111° del RFSFP, establece que son infracciones administrativas cuando el candidato no informe de los gastos e ingresos efectuados durante su campaña en el plazo de quince (15) días hábiles, contados a partir del día siguiente de la publicación en el diario oficial El Peruano de la resolución que declara la conclusión del proceso electoral que corresponda."));
                p.add(new DetalleInforme(9,null,13,false, "Por lo que, de acreditarse tal incumplimiento, el artículo 36-B de la LOP concordante con el primer párrafo del artículo 112° del RFSFP prescriben como sanción para el candidato una multa no menor de diez (10) ni mayor de treinta (30) Unidades Impositivas Tributarias (UIT)."));
                p.add(new DetalleInforme(10,null,13,false, "Es de precisar, que la obligación de presentar su información sobre las aportaciones e ingresos recibidos y sobre los gastos efectuados durante la campaña electoral en las Elecciones Regionales y Municipales 2018, no solo implica el deber de presentación de la misma, sino involucra además, que ésta se presente dentro del plazo establecido y que permita efectuar una correcta verificación y control por parte de la ONPE; es decir, se encuentre debidamente sustentada y registrada."));
                p.add(new DetalleInforme(11,null,13,false, "El espíritu de la norma, busca a través de esta obligación, la transparencia de los fondos o recursos obtenidos por los candidatos; así como, la utilización de los mismos; con el objeto de prevenir la infiltración de aportes de fuentes prohibidas y el adecuado uso de su financiamiento conforme a los topes considerados en la LOP y el RFSFP. Dicha obligación no está ligada a si el candidato obtuvo ingresos o no, sino a transparentar su actividad económico-financiera y cumplir con su responsabilidad conforme a Ley."));
                p.add(new DetalleInforme(12,null,13,false, "En ese sentido, la no presentación de la información sobre las aportaciones e ingresos recibidos y sobre los gastos efectuados durante la campaña electoral en las Elecciones Regionales y Municipales 2018, hasta el 21 de enero de 2019, por parte del candidato “XXXXXXX” devendría en una infracción según lo dispuesto por el artículo 36-B de la LOP concordante con el numeral 1 del artículo 111° del RFSFP, por lo que de conformidad con el artículo 36-B de la LOP concordante con el primer párrafo del artículo 112° del RFSFP sería sancionable con una multa no menor de diez (10) ni mayor de treinta (30) Unidades Impositivas Tributarias (UIT) previo el procedimiento administrativo sancionador respectivo."));
                break;
            case 4:
                p.add(new DetalleInforme(1,null,13,false, "El candidato “XXXXXXX” no cumplió con presentar la información sobre las aportaciones e ingresos recibidos y sobre los gastos efectuados durante la campaña electoral en las Elecciones Regionales y Municipales 2018, hasta el 21 de enero de 2019; por lo que, habría colisionado con el numeral 34.6 del artículo 34° de la LOP y el artículo 97° del RFSFP."));
                p.add(new DetalleInforme(2,null,13,false, "En ese sentido, después de evaluada la documentación que sustenta la omisión descrita en el párrafo anterior, la GSFP, conforme a lo dispuesto en el artículo 119° del RFSFP, determina que si concurren circunstancias que justifican el inicio del procedimiento administrativo sancionador y, de ser el caso, podría concluir con la imposición de una multa no menor de diez (10) ni mayor de treinta (30) Unidades Impositivas Tributarias."));
                break;
            case 5:
                p.add(new DetalleInforme(1,null,13,false, "Por lo expuesto, resulta necesario que se emita la Resolución Gerencial respectiva , a través de la cual se disponga el inicio del procedimiento administrativo sancionador contra el candidato “XXXXX”  por no presentar la información sobre las aportaciones e ingresos recibidos y sobre los gastos efectuados durante la campaña electoral en las Elecciones Regionales y Municipales 2018 hasta el 21 de enero de 2019."));
                break;                
            default:  
                break;
        }

        return p;
    }
    

}
