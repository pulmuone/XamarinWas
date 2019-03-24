package com.daesangit.xamarin.webapp;

import com.daesangit.xamarin.auth.Authorization;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.util.Enumeration;
import javax.json.Json;
import javax.json.stream.JsonParser;

public class GetServlet extends HttpServlet 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        request.setCharacterEncoding("UTF-8");
        //response.setContentType("text/html;charset=UTF-8");
        //response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        
        String result = "";
        
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            System.out.println(key +", "+ value);
        }
        
        //Header 가져오기
        System.out.println("-----JWT-----");
        System.out.println(request.getHeader("jwt"));
        
        String token = request.getHeader("jwt");

        if (Authorization.getInstance().VerifyToken(token))
        {
            System.out.println("-----Success token-----");
        }
        else
        {
             System.out.println("-----Fail token-----");
             
            try (PrintWriter writer = response.getWriter()) {
                writer.write(result);
                writer.flush();
            }
            
            return;
        }
        
        //How to #1
        //Client에서 "application/x-www-form-urlencoded"
        //String requestParam = request.getParameter("requestParam");
        //How to #2
        //Client에서 application/xml, application/json
        String requestParam = getBody(request);

        System.out.println(" ");
        System.out.println("----------------------------------------");
        System.out.println("-----Debug Start Get processRequest-----");
        System.out.println("----------------------------------------");

        System.out.println("requestParam : " + requestParam);

        final JsonParser jsonParser = Json.createParser(new StringReader(requestParam));

        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        String key = null;
        String value = null;
        while (jsonParser.hasNext()) 
        {
            final JsonParser.Event event = jsonParser.next();
            
            switch (event) {
                case KEY_NAME:
                    key = jsonParser.getString();
                    break;
                case VALUE_STRING:
                    value = jsonParser.getString();
                    if (key != null) 
                    {
                        linkedHashMap.put(key, value);
                    }
                    break;
                case VALUE_NUMBER:
                    value = jsonParser.getString();
                    if (key != null) 
                    {
                        linkedHashMap.put(key, value);
                    }
                    break;
                default:
                    break;
            }
        }
        jsonParser.close();

        try 
        {
            //GetTable gettable = new GetTable();
            //gettable.MakeDOM(requestParam);

            //LinkedHashMap[] hashCheck = gettable.GetParamData("DATA"); // 전송된 순서대로 처리 하려면 LinkedHashMap 으로 처리한다.
            result = GetCommand(linkedHashMap);
            
            System.out.println("Result : " + result);
            
            try (PrintWriter writer = response.getWriter()) {
                writer.write(result);
                writer.flush();
            }
        }
        catch (Exception e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String GetCommand(LinkedHashMap linkedHashMap) throws Exception 
    {
        String result = "";

        int rowCnt = 2; //프로시저 첫번째 파라미터는 리턴으로 사용하기 때문에 2번째부터 사용해야 한다.
        try(
                Connection conn = Connector.getConnection();
                CallableStatement cstmt = conn.prepareCall((String) linkedHashMap.get("USP"))
            )
        {
            //conn.setAutoCommit(false);
            cstmt.registerOutParameter(1, Types.OTHER); //Returns JSON도 Types.OTHER 사용한다.
     
            Set<String> keys = linkedHashMap.keySet();
            for (String key : keys) 
            {
                if (!key.equals("USP")) 
                {
                    System.out.println("Row : " + rowCnt + " Value of " + key + " is: " + linkedHashMap.get(key));
                            //+ ", Type Of : " + linkedHashMap.get(key).getClass().getSimpleName());

                    cstmt.setObject(rowCnt, linkedHashMap.get(key));
                    rowCnt = rowCnt + 1;
                }
            }

            cstmt.execute();
            result = cstmt.getObject(1).toString();
        } 
        catch (SQLException e) 
        {
            System.out.println(e.getMessage());
        } 
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
        }

        return result;
    }

    public static String getBody(HttpServletRequest request) throws IOException {
        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try 
        {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) 
            {
                //charset UTF-8로 하지 않으면 한글깨짐.
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                char[] charBuffer = new char[1024]; // 1 KB char buffer, 사이즈 조정은 업무에 맞게
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) 
                {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
        } 
        catch (IOException ex) 
        {
            throw ex;
        } 
        finally 
        {
            if (bufferedReader != null) 
            {
                try 
                {
                    bufferedReader.close();
                } catch (IOException ex) 
                {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
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
