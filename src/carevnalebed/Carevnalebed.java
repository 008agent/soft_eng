package carevnalebed;

import javax.swing.SwingUtilities;

/**
 *
 * @author s153335
 */
public class Carevnalebed implements Runnable{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SwingUtilities.invokeLater(/*new Carevnalebed()*/ new LoginForm());
    }

    public static void _exit() {
        System.exit(0);
    }
    
    @Override
    public void run() {
       init_form();
    }
    
    private void init_form() {
        MainForm fm = new MainForm();
        fm.pack();
        fm.setVisible(true);
    }
    
}
