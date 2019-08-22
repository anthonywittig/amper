/*
 * RollBackDBDialog.java
 *
 * Created on September 7, 2007, 6:02 PM
 */

package AMP2.MainDisplay;

import AMP2.Util.MiscStuff;
import java.io.*;

/**
 *
 * @author  awittig
 */
public class RollBackDBDialog extends javax.swing.JDialog {
    
    public boolean hasValue = false;
    private String returnPath = "";
    
    /** Creates new form RollBackDBDialog */
    public RollBackDBDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        filesCB.removeAllItems();
        final File directory = GUI.getCon().getBackupDirectory();
        if(!directory.exists()){
            try{
                directory.mkdir();
            }
            catch(Exception e){
                MiscStuff.writeToLog("RollBackDBDialog(): " + e.toString());
            }
        }
        File[] files = directory.listFiles();
        if(files != null){
            for(int i = files.length - 1; i > -1; i--) {
                //remove the derby log file if it's there
                if(!files[i].getName().equals("derby.log")){
                    filesCB.addItem(files[i]);
                }
            }
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        filesCB = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Roll Back DB");
        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24));
        jLabel1.setForeground(java.awt.Color.red);
        jLabel1.setText("This will roll back the entire Database!");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18));
        jLabel2.setForeground(java.awt.Color.red);
        jLabel2.setText("All information for all companies will be reset!");

        jLabel3.setText("Choose the file to restore from (date and time is at end of file name):");

        filesCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        filesCB.setPreferredSize(new java.awt.Dimension(332, 22));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/IM000866.jpg")));
        jLabel4.setName("");

        jLabel5.setText("Jacob says:");

        jButton1.setText("Go!");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goAP(evt);
            }
        });

        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelAP(evt);
            }
        });

        jLabel6.setText("Space to make sure you hit the right one...");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(22, 22, 22)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jButton1)
                        .add(103, 103, 103)
                        .add(jLabel6)
                        .add(108, 108, 108)
                        .add(jButton2))
                    .add(filesCB, 0, 776, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 418, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layout.createSequentialGroup()
                                .add(24, 24, 24)
                                .add(jLabel2))
                            .add(jLabel5)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jLabel4)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(layout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(jLabel5)
                        .add(49, 49, 49)
                        .add(jLabel1)
                        .add(63, 63, 63)
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jLabel3))
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(filesCB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(29, 29, 29)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(jButton2)
                    .add(jLabel6))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void cancelAP(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelAP
        
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_cancelAP
    
    private void goAP(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goAP
        hasValue = true;
        setReturnPath(filesCB.getSelectedItem().toString());
        this.setVisible(false);
    }//GEN-LAST:event_goAP
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RollBackDBDialog(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    
    public String getReturnPath() {
        return returnPath;
    }
    
    public void setReturnPath(String returnPath) {
        this.returnPath = returnPath;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox filesCB;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    // End of variables declaration//GEN-END:variables
    
}