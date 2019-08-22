/*
 * InsertCompanyDialog.java
 *
 * Created on December 4, 2006, 11:25 PM
 */

package AMP2.MainDisplay;

import AMP2.DatabaseStuff.command.Select;
import AMP2.DatabaseStuff.command.SelectBuilder;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.results.Result;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Table;
import java.util.*;

/**
 *
 * @author  TheFamily
 */
public class InsertCompanyDialog extends javax.swing.JDialog {
    
    public static InsertCompanyDialog _integrationTesting_lastInstance;
    
    public String companyName, location;
    public int year;
    public boolean hasValue = false;
    private boolean exitOnCancel = true;
    private boolean enforceIntegrity = false;
    private ArrayList names, years, locations;
    private ArrayList<Integer> namesI = new ArrayList<Integer>();
    private ArrayList<Integer> yearsI  = new ArrayList<Integer>();
    private ArrayList<Integer> locationsI = new ArrayList<Integer>();
    
    
    /** Creates new form InsertCompanyDialog */
    private InsertCompanyDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
        //not best practice...
        _integrationTesting_lastInstance = this;
    }
    
    public static InsertCompanyDialog getNewInsertCompanyDialog(java.awt.Frame parent, boolean modal) throws DataException {
        
        InsertCompanyDialog cd = new InsertCompanyDialog(parent, modal);
        cd.setUp();
        
        return cd;
    }
    
    /** Creates new form InsertCompanyDialog */
    public  static InsertCompanyDialog getNewInsertCompanyDialog(java.awt.Frame parent, boolean modal,
            boolean exitOnCancel, boolean enforceIntegrity) throws DataException {
        
        final InsertCompanyDialog cd = new InsertCompanyDialog(parent, modal);
        cd.setUp();
        
        cd.enforceIntegrity = enforceIntegrity;
        if(enforceIntegrity){
            
            //just to fire the change event
            //if(nameCB.getItemCount() > 1){
//                Object ob = nameCB.getItemAt(0);
//                nameCB.removeItemAt(0);
//                nameCB.addItem(ob);
            Object ob = ""; 
                cd.nameCB.addItem(ob);
                cd.nameCB.removeItem(ob);
            //}
            
            cd.nameCB.setEditable(false);
            cd.yearCB.setEditable(false);
            cd.locationCB.setEditable(false);
        }
        
        cd.exitOnCancel = exitOnCancel;
        
        return cd;
        
    }
    
    /**
     * This method loads up our indexes for our enfoeceIntegrity
     */
    private void loadLocations() {
        
        
//        yearsI = new ArrayList<Integer>();
//        String yearVal = yearCB.getSelectedItem().toString();
//        for(int i = 0; i < years.size(); i++) {
//            if(years.get(i).toString().equals(yearVal)) {
//                yearsI.add(i);
//            }
//        }
        
        
        
        
        
        
    }
    
    /**
     *  This loads our namesI array
     */
    private void loadNamesI(){
        namesI = new ArrayList<Integer>();
            //load name:
            String nameVal = nameCB.getSelectedItem().toString();
            for(int i = 0; i < names.size(); i++) {
                if(names.get(i).toString().equals(nameVal)) {
                    namesI.add(i);
                }
            }
    }
    
    /**
     * This method sets up our dialog
     */
    private void setUp() throws DataException {
        initComponents();
        
        
        
            final Results rs;
            {
                final SelectBuilder sb = new SelectBuilder();
                sb.addValue(Column.name, Column.location, Column.year)
                        .table(Table.companies);
                //"select name, location, year from companies"
                final Select select = sb.build();
                try{
                    rs = GUI.getCon().select(select);
                }catch(Exception e){
                    throw new DataException(select.toString(), e);
                }
            }
            names = new ArrayList();
            years = new ArrayList();
            locations = new ArrayList();
            
            for(final Result entry : rs) {
                
                final String name = (String) entry.get(Column.name);
                if(!names.contains(name)){
                    names.add(name);
                    nameCB.addItem(name);
                }else{//add it to our arraylist anyway for enforcing integrety
                    //what does the comment mean???
                    names.add(name);
                }
                
                final String location = (String) entry.get(Column.location);
                if(!locations.contains(location)) {
                    locations.add(location);
                    locationCB.addItem(location);
                }else{//add it to our arraylist anyway for enforcing integrety
                    //what does the comment mean???
                    locations.add(location);
                }
                
                final Integer year = (Integer) entry.get(Column.year);
                if(!years.contains(year)) {
                    years.add(year);
                    yearCB.addItem(year);
                }else{//add it to our arraylist anyway for enforcing integrety
                    //what does the comment mean???
                    years.add(year);
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
        okB = new javax.swing.JButton();
        cancelB = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        nameCB = new javax.swing.JComboBox();
        yearCB = new javax.swing.JComboBox();
        locationCB = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        okB.setText("OK");
        okB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBActionPerformed(evt);
            }
        });

        cancelB.setText("Cancel");
        cancelB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBActionPerformed(evt);
            }
        });

        jLabel1.setText("Company Name:");

        jLabel2.setText("Year:");

        nameCB.setEditable(true);
        nameCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                companyNameAP(evt);
            }
        });

        yearCB.setEditable(true);
        yearCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yearAP(evt);
            }
        });

        locationCB.setEditable(true);
        locationCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locationAP(evt);
            }
        });

        jLabel3.setText("Location:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jLabel2)
                    .add(jLabel3)
                    .add(okB))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cancelB)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(yearCB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(nameCB, 0, 103, Short.MAX_VALUE)
                        .add(locationCB, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(nameCB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(locationCB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .add(5, 5, 5)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(yearCB, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelB)
                    .add(okB))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void yearAP(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yearAP
        
        
    }//GEN-LAST:event_yearAP
    
    private void locationAP(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_locationAP
        if(enforceIntegrity) {
            //loadLocations();
            locationsI = new ArrayList<Integer>();
            
            
            
            //load location:
            String locationVal = "";
            try {
                locationVal = locationCB.getSelectedItem().toString();
            } catch(Exception e) {
                locationVal = locationCB.getItemAt(0).toString();
            }
            for(int i = 0; i < locations.size(); i++) {
                if(locations.get(i).toString().equals(locationVal)) {
                    locationsI.add(i);
                }
            }
            
            loadNamesI();
            
            yearCB.removeAllItems();
            for(int i : locationsI) {
                for(int j : namesI){
                    if(i == j){
                        yearCB.addItem(years.get(i).toString());
                    }
                }
            }
            yearCB.setSelectedIndex(0);
        }
        
    }//GEN-LAST:event_locationAP
    
    private void companyNameAP(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_companyNameAP
        
        if(enforceIntegrity) {
            //loadLocations();
            
            loadNamesI();
            
            
            
            enforceIntegrity = false;
            
            locationCB.removeAllItems();
            for(int i : namesI) {
                boolean unique = true;
                for(int j = 0; j < locationCB.getItemCount(); j++){
                    if(locationCB.getItemAt(j).toString().equals(locations.get(i).toString())){
                        unique = false;
                    }
                }
                if(unique){
                    locationCB.addItem(locations.get(i).toString());
                }
            }
            
            enforceIntegrity = true;
            
            locationCB.setSelectedIndex(0);
        }
        
        
    }//GEN-LAST:event_companyNameAP
    
    private void cancelBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBActionPerformed
// TODO add your handling code here:
        //for now...
        
        hasValue = false;
        
        if(exitOnCancel) {
            System.exit(0);
        } else {
            this.dispose();
        }
    }//GEN-LAST:event_cancelBActionPerformed
    
    private void okBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBActionPerformed
// TODO add your handling code here:
        
        hasValue = true;
        
        companyName = nameCB.getSelectedItem().toString();
        year = new Integer(yearCB.getSelectedItem().toString());
        location = locationCB.getSelectedItem().toString();
        
        this.setVisible(false);
    }//GEN-LAST:event_okBActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InsertCompanyDialog(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox locationCB;
    private javax.swing.JComboBox nameCB;
    private javax.swing.JButton okB;
    private javax.swing.JComboBox yearCB;
    // End of variables declaration//GEN-END:variables
 
    
    public static class IntegrationTestHelper{
        
        
        public static void okBActionPerformed(String companyName, String year, String location){
            _integrationTesting_lastInstance.nameCB.setSelectedItem(companyName);
            _integrationTesting_lastInstance.yearCB.setSelectedItem(year);
            _integrationTesting_lastInstance.locationCB.setSelectedItem(location);
            
            _integrationTesting_lastInstance.okBActionPerformed(null);
        } 
    }
}
