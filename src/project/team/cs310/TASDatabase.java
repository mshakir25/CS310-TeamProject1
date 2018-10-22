package project.team.cs310;

// Muhammad Shakir  ..... 
// Austin Griffin
// Micheal Rogers
// Ryan Hill 
// Dalton Murphee

import java.sql.*;
import java.util.GregorianCalendar;
import java.util.logging.*;

public class TASDatabase {
    
    private Connection conn;
    private Statement stmt;
    private ResultSet result;
    
    //CONSTRUCTOR
    public TASDatabase()
    {    
        try
        {
            //Identify the server
            String server = ("jdbc:mysql://localhost/tas");
            String username = "tasuser";
            String password = "WarRoomF";
            System.out.println("Connecting to " + server + "...");

            //load the MYSQL JDBC Driver
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            //open the connection
            conn = DriverManager.getConnection(server, username,password);
            stmt = conn.createStatement();
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }
    public void close()
    {
        // Close the connection from the data base
        try
        {
            result.close();
            stmt.close();
            conn.close();
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }
    
    public Punch getPunch(int id)
    {    
        Punch p = null;
        try
        {
            
            String query = "SELECT *, UNIX_TIMESTAMP(originaltimestamp) * 1000 AS ts FROM punch WHERE id = " + Integer.toString(id);
        
            try (Statement st = conn.createStatement())
            {
                ResultSet rs = st.executeQuery(query);   
                while (rs.next())
                {
                    int Id = rs.getInt("id");
                    int terminalId = rs.getInt("terminalid");
                    String badgeId = rs.getString("badgeid");
                    int punchTypeId = rs.getInt("punchtypeid");
                    Badge b = getBadge(badgeId);
                    long time = rs.getLong("ts");
                    //Need to create a punch and to create a punch I need a badge
                    p = new Punch(b, terminalId, punchTypeId);
                    //set original time to whatever it was with setter
                    GregorianCalendar origTime = new GregorianCalendar();
                    origTime.setTimeInMillis(time);
                    p.setOriginaltime(origTime);
                }
            }
        }
        catch(SQLException ex)
        {
            Logger.getLogger(TASDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return p;   
    }
    public Badge getBadge(String id)
    {    
        Badge b = null;
        try(Statement st = conn.createStatement())
        {
            String query = "SELECT * FROM badge WHERE id = " + "\"" + id + "\"";
    //PreparedStatement pstUpdate = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    //pstUpdate.setString(1, id);
            ResultSet rs = st.executeQuery(query);

            while(rs.next())
            {
                String Id = rs.getString("id");
                String d = rs.getString("description");


                b = new Badge(Id,d);
            }
        }
        catch(SQLException ex)
        {
            Logger.getLogger(TASDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return b;
    }
    
    public Shift getShift(int id)
    {
        Shift s = null;
        try(Statement st = conn.createStatement()){
        String query = "SELECT * FROM shift WHERE id = " + Integer.toString(id);
        ResultSet rs = st.executeQuery(query);
         
        while(rs.next())
        {
            int Id = rs.getInt("id");
            int interval = rs.getInt("interval");
            int graceperiod = rs.getInt("graceperiod");
            int dock = rs.getInt("dock");
            int lunchdeduct = rs.getInt("lunchdeduct");
            Time start = rs.getTime("start");
            Time stop = rs.getTime("stop");
            Time lunchstart = rs.getTime("lunchstart");
            Time lunchstop = rs.getTime("lunchstop");
            String d = rs.getString("description");
            s = new Shift(Id,interval,graceperiod,dock,lunchdeduct,d,start,stop,lunchstart,lunchstop);
        }
    }
        catch(SQLException ex)
        {
            Logger.getLogger(TASDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
            //Querys for the corresponding shift, creates the object, returns it
        return s;
    }
    
    public Shift getShift(Badge badge)
    {
        Shift s = null;
        int shiftid = 0; 
        try(Statement st = conn.createStatement()){
        String query = "SELECT * FROM employee WHERE badgeid = " + "\"" + badge.getId() + "\"";
        //PreparedStatement pstUpdate = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        //pstUpdate.setString(1, badge.getId());
        ResultSet rs = st.executeQuery(query);
         
        while(rs.next())
        {
            shiftid = rs.getInt("shiftid");
        }
         
        query = "SELECT * FROM shift WHERE id = " + Integer.toString(shiftid);

        rs = st.executeQuery(query);
        while(rs.next())
        {
            int Id = rs.getInt("id");
            int interval = rs.getInt("interval");
            int graceperiod = rs.getInt("graceperiod");
            int dock = rs.getInt("dock");
            int lunchdeduct = rs.getInt("lunchdeduct");
            Time start = rs.getTime("start");
            Time stop = rs.getTime("stop");
            Time lunchstart = rs.getTime("lunchstart");
            Time lunchstop = rs.getTime("lunchstop");
            String d = rs.getString("description");

            s = new Shift(Id,interval,graceperiod,dock,lunchdeduct,d,start,stop,lunchstart,lunchstop);
        }
    }
        catch(SQLException ex)
        {
            Logger.getLogger(TASDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return s;
    }

    public int insertPunch(Punch p)
    {
	int terminalID = p.getTerminalid();
	int punchTypeID = p.getPunchtypeid();
	int ID = p.getId();
	GregorianCalendar g = p.getOriginaltime();
	String badgeID = p.getBadgeid();
        
	try
        {
            int punchID;
            int Results;
            ResultSet rst;		
            
            String query = " insert into users (id, terminalid, badgeid, originaltimestamp, punchtypeid)" + " values (?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = conn.prepareStatement(query,PreparedStatement.RETURN_GENERATED_KEYS);
            
            preparedStmt.setInt (1, ID);
            preparedStmt.setInt (2, terminalID);
            preparedStmt.setString   (3, badgeID);
            //preparedStmt.setGregorianCalendar(4, g);
            preparedStmt.setInt    (5, punchTypeID);

            Results = preparedStmt.executeUpdate();
            conn.close();

            //Check to see if punch was properly inserted		

            if(Results == 1)
            {
                rst = preparedStmt.getGeneratedKeys();
                if(rst.next()){
                        punchID = rst.getInt(1);
                        p.setId(punchID);
                }
            }
        }
        catch(SQLException ex)
        {
            Logger.getLogger(TASDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return p.getId();
    }
}