package com.daesangit.xamarin.webapp;


import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
/**
 *
 * @author gwise
 */
public class Connector 
{
    private static DataSource ds;
    
    static 
    {
        try 
        {
	          InitialContext ctx = new InitialContext();
	          ds=(DataSource)ctx.lookup("java:/comp/env/jdbc/postgres");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
	    
    public static Connection getConnection() throws SQLException
    {
   	 	 Connection con = ds.getConnection();
//		 Connection actual = ((javax.sql.PooledConnection)con).getConnection();
//		 actual.setAutoCommit(false);
   	 	 //con.setAutoCommit(false);
		 
        return con;
    }    
}
