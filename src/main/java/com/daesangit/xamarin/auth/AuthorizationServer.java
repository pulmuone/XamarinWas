package com.daesangit.xamarin.auth;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 *
 * @author gwise
 */
public class AuthorizationServer extends HttpServlet {

	private static final long serialVersionUID = -5555042245810163497L;

	/**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(" ");
        System.out.println("---------------------------------------------");
        System.out.println("-----Debug Start Get AuthorizationServer-----");
        System.out.println("---------------------------------------------");
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            System.out.println(key +", "+ value);
        }
        
        String authorization = request.getHeader("Authorization");
     
        if (authorization != null && authorization.startsWith("Basic")) 
        {        	
        	String base64Credentials = authorization.substring("Basic".length()).trim();
            //String credentials = new String(DatatypeConverter.parseBase64Binary(base64Credentials));
            String credentials = new String(Base64.getDecoder().decode(base64Credentials), Charset.forName("UTF-8"));
            String[] values = credentials.split(":",2);
            System.out.println("ID : " + values[0]);
            System.out.println("PW : " + values[1]);
        }
        else
        {
            return;
        }
        
        //ToDo
        //Database Login Process
        
        /*
        Algorithm algorithm = Algorithm.HMAC256("bcwms");
        String token = JWT.create()
        .withIssuer("bcwms")
        .withExpiresAt(EXPIRES_DATE)
        .withClaim("SCOPE", "INBOUND|OUTBOUND|PUTAWAY|INVENTORY")
        .withClaim("SCOPE2", "INBOUND|OUTBOUND|PUTAWAY")       
        .sign(algorithm);
        */
        
        System.out.println("token : " + Authorization.getInstance().getToken());        
        try (PrintWriter out = response.getWriter()) 
        {
            out.write(Authorization.getInstance().getToken());
            out.flush();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
