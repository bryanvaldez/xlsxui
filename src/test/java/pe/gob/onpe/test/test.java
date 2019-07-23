/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.test;

/**
 *
 * @author Bryan Luis Valdez Jara <ibryan.valdez@gmail.com>
 */
public class test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        String token = "claridad";
        String encode = java.util.Base64.getUrlEncoder().encodeToString(token.getBytes());
        System.err.println(encode);
        
    }
    
    
//    private String requestDecode(HttpServletRequest request) {
//        String token = request.getHeader("Request");
//        String response = new String(java.util.Base64.getUrlDecoder().decode(token));
//        return response;
//    }
//
//    private void responseEncode(HttpServletResponse response, JsonObject jResponse) {
//        String header = java.util.Base64.getUrlEncoder().encodeToString(new Gson().toJson(jResponse).getBytes());
//        response.addHeader("cDat", header);
//    }    
    
}
