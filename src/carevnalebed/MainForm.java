package carevnalebed;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 *
 * @author s153335
 */
public class MainForm extends javax.swing.JFrame {

    static Connection _cn = null;
    static Boolean _connected = false;
    static String _user = "s153335";
    static String _pass = "eaq448";
    
    static boolean _logged = false;
    
    DefaultListModel _listModelNames = new DefaultListModel();
    DefaultListModel _listModelNamesSearch = new DefaultListModel();
    DefaultListModel _listModelWillings = new DefaultListModel();
    
    static List<String> _idS;
    static List<String> _names;
    static List<String> _descriptions;
    
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
    
    private void DeleteByROWID(String rowId) {
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
    
    private String GetROWID4SelectedOrder() {
        if(!_connected) return null;
        int ordId = GetUserId(jTextFieldLogin.getText()) * 100 + GetMiracleIdByName((String)jListWillings.getSelectedValue());
        try {
                Statement st = _cn.createStatement();
                ResultSet rs = st.executeQuery("SELECT ROWID FROM CLORDERS");
                while(rs.next()) {               
                    String tmpRowId = new String(rs.getRowId("ROWID").getBytes());
                    Statement innerSt = _cn.createStatement();
                    ResultSet innerRS = innerSt.executeQuery("SELECT * FROM CLORDERS WHERE ROWID = '"+ tmpRowId +"'");
                        innerRS.next();
                        if(ordId==innerRS.getInt("id")) return tmpRowId;
                }
                return null;
        }
        catch(Exception ex) {
                ex.printStackTrace();
                return null;
        }  
    }
    
    private String GetMiracleNameById(int id) {
        if(!_connected) return null;        
        try {
                Statement st = _cn.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM CLMAIN");
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
    
    private int GetMiracleIdByName(String mirName) {
        if(!_connected) return -1;        
        try {
                Statement st = _cn.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM CLMAIN");
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
    
    private void SetMiracleId4UserId(int miracleId,int userId) {
        if(!_connected) return;        
        try {
                Statement st = _cn.createStatement();
                ResultSet rs = st.executeQuery("INSERT INTO \"S153335\".\"CLORDERS\" (\"id\", \"id_miracle\", \"id_user\") VALUES ('"+ (userId*100+miracleId) +"','"+ miracleId +"', '"+ userId +"')");
        }
        catch(Exception ex) {
                ex.printStackTrace();
        }
    }
    
    private int GetUserId(String userName) {
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

    private String GetPass4UserB64(String user) {
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

    private void ReadData() {
            if(!_connected) return;
            
            try {
                Statement st = _cn.createStatement();

                ResultSet rs = st.executeQuery("SELECT * FROM CLMAIN");
                
                _idS = new ArrayList<>();
                _names = new ArrayList<>();
                _descriptions = new ArrayList<>();
                
                while(rs.next()) {
                    _idS.add(rs.getString(1));
                    _names.add(rs.getString(2));
                    _descriptions.add(rs.getString(3));
                }
                
                //_listModelNames = n
                
                _listModelNames.clear();
                
                for( String s:_names ) {
                   _listModelNames.addElement(s); 
                  // System.out.println(s);
                }
                
                jListNames.setModel(_listModelNames);
                
            }
            catch(Exception ex) {
                ex.printStackTrace();
                return;
            }
    }
    /**
     * Инициализация соединения с бд
     */
    private void InitConnection(String user,String pass) {
        
        String url = "jdbc:oracle:thin:@localhost:1521:orbis";
        
        
        try {     
        _cn =
            DriverManager.getConnection (url,user,pass);
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
    private void CloseConnection() {
        try {
            _cn.close();
            _connected = false;
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public MainForm() {
        initComponents();

        //register driver
        try {
            DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return; 
        }
        
        InitConnection(_user, _pass);
        
        ReadData();
        
        if(!_connected) {
                carevnalebed.Carevnalebed._exit();
        }
        
        jListWillings.setModel(_listModelWillings);
        //ReadDataToTable(jTable1);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jListNames = new javax.swing.JList();
        jLabelDescription = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jPasswordFieldPass = new javax.swing.JPasswordField();
        jTextFieldLogin = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListWillings = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jTextFieldSearch = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Царевна Лебедь");
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jListNames.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListNames.setDoubleBuffered(true);
        jListNames.setVisibleRowCount(16);
        jListNames.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListNamesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jListNames);

        jLabelDescription.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelDescription.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel1.setText("Каталог чудес : ");

        jLabel2.setText("Описание : ");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabel3.setText("Пользователь : ");

        jPasswordFieldPass.setText("Приехала");

        jTextFieldLogin.setText("Тетя Ася");

        jLabel4.setText("Пароль :");

        jButton1.setText("Войти/Выйти");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Хочу!");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(jListWillings);

        jLabel5.setText("Список желаний : ");

        jButton3.setText("Не хочу!");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jTextFieldSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSearchActionPerformed(evt);
            }
        });
        jTextFieldSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldSearchKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldSearchKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldSearch)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldLogin)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPasswordFieldPass, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane2)
                    .addComponent(jLabel5)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(296, 296, 296))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(jTextFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(jLabelDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel4))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jTextFieldLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jPasswordFieldPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(24, 24, 24)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 15, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        CloseConnection();
    }//GEN-LAST:event_formWindowClosing

    private void jListNamesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListNamesMouseClicked
        //System.out.println(jListNames.getSelectedIndex());
        jLabelDescription.setText(_descriptions.get(jListNames.getSelectedIndex()));
    }//GEN-LAST:event_jListNamesMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
//        jTextFieldLogin.setEnabled(false);
//        jPasswordFieldPass.setEnabled(false);
        if(_logged) {
            _logged = false;
            jTextFieldLogin.setEnabled(true);
            jPasswordFieldPass.setEnabled(true);
        }
        else {
            String userName = jTextFieldLogin.getText();
            String userPass = new String(jPasswordFieldPass.getPassword());
            String resTmp = GetPass4UserB64(userName);
            if(resTmp!=null) {
                if(resTmp.equals(Base64.encode(userPass))) {
                    _logged = true;
                    jTextFieldLogin.setEnabled(false);
                    jPasswordFieldPass.setEnabled(false);
                }
                else {
                    _logged = false;
                    jTextFieldLogin.setEnabled(true);
                    jPasswordFieldPass.setEnabled(true);
                }
            }

        }
        //очистить список
        _listModelWillings.clear();
        if(_logged) {
            ArrayList<Object> willings = GetOrdersIds4UserId(GetUserId(jTextFieldLogin.getText()));
            if(willings==null) return;
            
            for(Object tmp:willings) {
                _listModelWillings.addElement( GetMiracleNameById((int)tmp) );
            }
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(jListNames.getSelectedValue()!=null) {
                if(_listModelWillings.contains(jListNames.getSelectedValue())) {
                    return;
                }
                else {
                //добавить в базу для текущего пользователя
                
                int uid = GetUserId(jTextFieldLogin.getText());
                if(uid==-1) return;
                SetMiracleId4UserId( GetMiracleIdByName((String)jListNames.getSelectedValue()), GetUserId(jTextFieldLogin.getText()));
                _listModelWillings.addElement(jListNames.getSelectedValue());
                }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if(jListWillings.getSelectedIndex()!=-1) {
            if(_logged) {
                DeleteByROWID(GetROWID4SelectedOrder()); 
                _listModelWillings.removeElement(jListWillings.getSelectedValue());
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextFieldSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSearchActionPerformed
        // TODO add your handling code here:  
    }//GEN-LAST:event_jTextFieldSearchActionPerformed

    private void jTextFieldSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldSearchKeyPressed

        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSearchKeyPressed

    private void jTextFieldSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldSearchKeyReleased
        try {
            if(jTextFieldSearch.getText().length()!=0) {
                //_listModelNamesSearch = _listModelNames; 
                _listModelNamesSearch.clear();
                for(int i=0;i<_listModelNames.getSize();i++) {
                    if(((String)_listModelNames.get(i)).contains(jTextFieldSearch.getText())) {
                        _listModelNamesSearch.addElement(_listModelNames.get(i));
                    }
                }
                jListNames.setModel(_listModelNamesSearch);
            }
            else {
                jListNames.setModel(_listModelNames);
            }
        }
        catch(Exception ex) {
            jListNames.setModel(_listModelNames);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSearchKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelDescription;
    private javax.swing.JList jListNames;
    private javax.swing.JList jListWillings;
    private javax.swing.JPasswordField jPasswordFieldPass;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextFieldLogin;
    private javax.swing.JTextField jTextFieldSearch;
    // End of variables declaration//GEN-END:variables
}
