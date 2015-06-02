package carevnalebed;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;

public class ODBConnection 
{
///Variables
    Connection _cn = null;
    Boolean _connected = false;
    String _user = "";
    String _pass = "";
    
    String _loginDb = "";
    String _passwordDb = "";
    boolean _loggedDb = false;
    int _roleDb = 0;
    
    boolean _logged = false;
    
    DefaultListModel _listModelNames = new DefaultListModel();
    DefaultListModel _listModelNamesSearch = new DefaultListModel();
    DefaultListModel _listModelWillings = new DefaultListModel();
    
    List<String> _idS = new ArrayList<>();
    List<String> _names = new ArrayList<>();
    List<String> _descriptions = new ArrayList<>();
    
///endVariables    
    public static void main(String[] args) {
        System.out.println("ODBConnection.java testspace");
        
        ODBConnection odbc = new ODBConnection("s153335", "eaq448");
        
        System.out.println(String.format("Connected %b",odbc.IsConnected().toString()));
        
        odbc.Login("admin", "admin");
        
        Globals.Verbose = true;
        
        odbc.ExecuteQuery("select * from \"S153335\".\"CLUSERS\" where \"id\"=3");
    }
    

    //залогиниться в систему.
    public boolean Login(String login,String pass) {
        _loginDb = login;
        _passwordDb = pass;
        
        String tmpPassB64 = GetPass4UserB64(_loginDb);
        
        if(tmpPassB64.equals(Base64.Encode(_passwordDb))) {
            System.out.println("user found");
            _loggedDb = true;
        }
        else {
            _loggedDb = false;
            return false;
        }
        
        if(_loggedDb) {
            _roleDb = GetRoleId4User(_loginDb);
            System.out.println(_roleDb);
            return true;
        }
        return false;
    }
    
    public void Logout() {
        _loginDb = _passwordDb = "";
        _roleDb = 0;
        _loggedDb = false;
    }
    
    public int GetRoleId() {
        return _roleDb;
    }

    
    public Boolean IsConnected() {
        return _connected;
    }
    
    public ODBConnection(String user,String passw) {
        _user = user; _pass = passw;
        
        //register driver
        try {
            DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return; 
        }
       
        String url = "jdbc:oracle:thin:@localhost:1521:orbis";
        
        try {     
        _cn =
            DriverManager.getConnection (url,_user,_pass);
            _connected = true;
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            _connected = false;
        }
    }
    
    /**
     * Закрытие соединения с бд
     */
    public void CloseConnection() {
        try {
            _cn.close();
            _connected = false;
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
        private ArrayList<Object> GetOrdersIds4UserId(int idUser) {
        if(!_connected) return null;
        
        ArrayList<Object> tmp = new ArrayList<>();
        try {
                Statement st = _cn.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM CLORDERS");
                while(rs.next()) {
                    if(rs.getInt("id_user")==idUser) {
                        tmp.add(rs.getInt("id_miracle"));
                    }
                }
                return tmp;
        }
        catch(Exception ex) {
                ex.printStackTrace();
                return null;
        }  
    }
    
    public void DeleteByROWID(String rowId) {
        if(!_connected) return;
        try {
                Statement st = _cn.createStatement();
                ResultSet rs = st.executeQuery("DELETE FROM CLORDERS WHERE ROWID = '"+ rowId +"'");
        }
        catch(Exception ex) {
                ex.printStackTrace();
                return;
        }  
    }
    
    public int GetRoleIdByAlias(String name) {
        if(!_connected) return 0;
        try {
                Statement st = _cn.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM \"S153335\".\"CLROLES\" WHERE \"alias\"='"+ name +"'");
                while(rs.next()) {
                    return rs.getInt("id");
                }
        }
        catch(Exception ex) {
                ex.printStackTrace();
                return 0;
        }  
        return 0;
    }
    
    public String GetROWID4SelectedUser(String user) {
        if(!_connected) return null;
        try {
            Statement st = _cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT ROWID FROM CLUSERS");
            while(rs.next()) {
                String tmp = new String(rs.getRowId("ROWID").getBytes());
                Statement innerSt = _cn.createStatement();
                ResultSet innerRs = innerSt.executeQuery("SELECT * FROM CLUSERS WHERE ROWID = '" + tmp + "'");
                innerRs.next();
                if(user.equals(innerRs.getString("name"))) {
                    return tmp;
                }
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return null;
    }
    
    public void DeleteUserByRowId(String RowId) {
        if(!_connected) return;
        try {
                Statement innerSt = _cn.createStatement();
                innerSt.executeQuery("DELETE FROM CLUSERS WHERE ROWID = '" + RowId + "'");
        }            
        catch(Exception ex) {
                ex.printStackTrace();
        }
    }
    
//    public String GetROWID4SelectedOrder(JTextField textFieldLogin) {
//        if(!_connected) return null;
//        int ordId = GetUserId(jTextFieldLogin.getText()) * 100 + GetMiracleIdByName((String)jListWillings.getSelectedValue());
//        try {
//                Statement st = _cn.createStatement();
//                ResultSet rs = st.executeQuery("SELECT ROWID FROM CLORDERS");
//                while(rs.next()) {               
//                    String tmpRowId = new String(rs.getRowId("ROWID").getBytes());
//                    Statement innerSt = _cn.createStatement();
//                    ResultSet innerRS = innerSt.executeQuery("SELECT * FROM CLORDERS WHERE ROWID = '"+ tmpRowId +"'");
//                        innerRS.next();
//                        if(ordId==innerRS.getInt("id")) return tmpRowId;
//                }
//                return null;
//        }
//        catch(Exception ex) {
//                ex.printStackTrace();
//                return null;
//        }  
//    }
    
    public String GetMiracleNameById(int id) {
        if(!_connected) return null;        
        try {
                Statement st = _cn.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM CLMIRACLES");
                while(rs.next()) {
                    if(rs.getInt("id")==id) {
                        return rs.getString("name");
                    }
                }
                return null;
        }
        catch(Exception ex) {
                ex.printStackTrace();
                return null;
        }  
    }
    
    public int GetMiracleIdByName(String mirName) {
        if(!_connected) return -1;        
        try {
                Statement st = _cn.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM CLMIRACLES");
                while(rs.next()) {
                    if(rs.getString("name").equals(mirName)) {
                        return rs.getInt("id");
                    }
                }
                return -1;
        }
        catch(Exception ex) {
                ex.printStackTrace();
                return -1;
        } 
    }
    
    public void SetMiracleId4UserId(int miracleId,int userId) {
        if(!_connected) return;        
        try {
                Statement st = _cn.createStatement();
                ResultSet rs = st.executeQuery("INSERT INTO \"S153335\".\"CLORDERS\" (\"id\", \"id_miracle\", \"id_user\") VALUES ('"+ (userId*100+miracleId) +"','"+ miracleId +"', '"+ userId +"')");
        }
        catch(Exception ex) {
                ex.printStackTrace();
        }
    }
    
    public ResultSet ExecuteQuery(String query) {
        if(!_connected) return null;        
        ResultSet rs = null;
        try {
                Statement st = _cn.createStatement();
                rs = st.executeQuery(query);
        }
        catch(Exception ex) {
                ex.printStackTrace();
        }
        return rs;
    }
    
    public ArrayList<String> GetUsersList() {
        ArrayList<String> al = new ArrayList<>();
        al.clear();
        if(!_connected) return al;        
        try {
                Statement st = _cn.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM CLUSERS");
                while(rs.next()) {
                    al.add(rs.getString("name"));
                }
        }
        catch(Exception ex) {
                ex.printStackTrace();
        }
        return al;
    }
    
    public int GetUserId(String userName) {
        if(!_connected) return -1;        
        try {
                Statement st = _cn.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM CLUSERS");
                while(rs.next()) {
                    if(rs.getString("name").equals(userName)) {
                        return rs.getInt("id");
                    }
                }
                return -1;
        }
        catch(Exception ex) {
                ex.printStackTrace();
                return -1;
        }
    }

    public String GetPass4UserB64(String user) {
        if(!_connected) return null;
        String result = null;
        
        try {
                Statement st = _cn.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM CLUSERS");
                while(rs.next()) {
                    if(rs.getString("name").equals(user)) {
                        return rs.getString("secret");
                    }
                }
                return null;
        }
        catch(Exception ex) {
                ex.printStackTrace();
                return null;
        }
    }
    
    public int GetRoleId4User(String user) {
        if(!_connected) return 0;
        int result = 0;
        
        try {
                Statement st = _cn.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM CLUSERS");
                while(rs.next()) {
                    if(rs.getString("name").equals(user)) {
                        return rs.getInt("idAccountRole");
                    }
                }
                return 0;
        }
        catch(Exception ex) {
                ex.printStackTrace();
                return 0;
        }
    }
        
}
