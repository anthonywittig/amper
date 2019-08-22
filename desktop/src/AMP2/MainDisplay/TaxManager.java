/*
 * TaxManager.java
 *
 * Created on September 8, 2008, 11:07 PM
 */
package AMP2.MainDisplay;

import AMP2.BankStuff.Currency;
import AMP2.DatabaseStuff.DatabaseHelper;
import AMP2.DatabaseStuff.Tax;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.MainDisplay.util.ReturningActionListener;
import AMP2.Payroll.FicaEng;
import AMP2.Payroll.OrWithEng;
import AMP2.tax.FicaImporter;
import com.wittigweb.swing.NumericTextField;
import java.awt.Component;
import java.awt.GridLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import static junit.framework.Assert.*;

/**
 *
 * @author  Andy
 */
public class TaxManager extends javax.swing.JFrame {
    
    private static AtomicReference<TaxManager> lastInstance = new AtomicReference<TaxManager>();

    protected int companyID = -1;
    //this is for a more functional style use of an int:
    protected int j = 0;

    private TaxManager(){
    }
    
    public static TaxManager getNewInstance(int companyID) throws DataException {
        
        final TaxManager tax = new TaxManager();
        
        tax.setCompanyID(companyID);
        tax.initComponents();
        tax.setUp();
        
        
        //for integration testing
        lastInstance.set(tax);
        
        return tax;

    }

    public NumericTextField[][] addPanel(JTabbedPane tp, JComboBox cb, int taxName) {

        final String toAdd = cb.getSelectedItem().toString();
        final JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(0, 2));

        jp.add(new JLabel("Under"));
        jp.add(new JLabel("Pays"));

        final NumericTextField[][] underAndTax = new NumericTextField[50][2];
        for (int i = 0; i < underAndTax.length; ++i) {
            final NumericTextField tf1 = new NumericTextField();
            tf1.setName(toAdd + "," + taxName + ",underAmount," + i);

            jp.add(tf1);
            underAndTax[i][0] = tf1;

            final NumericTextField tf2 = new NumericTextField();
            tf2.setName(toAdd + "," + taxName + ",tax," + i);

            jp.add(tf2);
            underAndTax[i][1] = tf2;
        }

        
        //adding jpanel
        boolean added = false;
        for (int i = 0; i < tp.getTabCount() && !added; ++i) {
            String title = tp.getTitleAt(i);
            if (Integer.parseInt(title) == Integer.parseInt(toAdd)) {
                //already have it, 
                added = true;
            } else if (Integer.parseInt(title) > Integer.parseInt(toAdd)) {
                tp.insertTab(toAdd, null, jp, "", i);
                added = true;
            }
        }

        if (!added) {
            tp.addTab(toAdd, jp);
        }
        
        return underAndTax;
    }
    
    public void doSaveHelper(JPanel jp, int taxType, int claim) throws DataException, SQLException{
        //get all records for this claim
        j = 0;
//        try {
            ResultSet rs = Tax.getAllTaxesByCompanyIDAndTaxTypeAndClaim(getCompanyID(), taxType, claim);
            while (rs.next()) {

                Tax tax = null;
                while (tax == null && j < jp.getComponentCount()) {

                    tax = getNextTax(jp);

                    if (tax.getUnderAmount().ne(-1) &&
                            tax.getTax().ne(-1)) {

                        //we got some good data, now update the next guy:
                        tax.setId(rs.getInt("id"));
                        tax.update();
                    } else {
                        //let's try again:
                        tax = null;
                    }
                }

                if (tax == null) {
                    //we didn't get to fix this record, delete
                    DatabaseHelper.delete(rs.getInt("id"), "taxes");
                }
            }

            //now we have gone through all of the preexisting records, add any new guys that remain:
            while (j < jp.getComponentCount()) {
                Tax tax = getNextTax(jp);

                if (tax.getUnderAmount().ne(-1) &&
                        tax.getTax().ne(-1)) {

                    //we got some good data, insert:
                    tax.update();
                }
            }
    }

    public String getNameHelper(Component c) {
        if (c.getName() != null) {
            return c.getName();
        }

        return "";
    }

    public Tax getNextTax(JPanel jp) {
        Tax tax = new Tax();

        for (int i = 0; i < 2 && j < jp.getComponentCount(); ++i, ++j) {
            Component c = jp.getComponent(j);

            if (c instanceof NumericTextField) {
                NumericTextField ntf = (NumericTextField) c;

                String[] vals = ntf.getName().split(",");

                if (vals[2].equals("underAmount")) {
                    //this is the first text box (of the set)
                    tax = new Tax();

                    tax.setClaim(Integer.parseInt(vals[0]));
                    tax.setCompanyID(companyID);
                    tax.setTaxTypeID(Integer.parseInt(vals[1]));

                    try {
                        tax.setUnderAmount(new Currency(ntf.getText()));
                    } catch (Exception e) {
                        //ignore it
                    }
                } else if (vals[2].equals("tax")) {
                    try {
                        tax.setTax(new Currency(ntf.getText()));
                    } catch (Exception e) {
                        //ignore it
                    }
                }
            }
        }

        return tax;
    }

    private boolean isNumeric(JTextField jtf) {
        boolean ret = false;

        try {
            Double.parseDouble(jtf.getText());
            ret = true;
        } catch (Exception e) {
        }

        return ret;
    }

    protected void numericInput(java.awt.event.KeyEvent evt) {
        JTextField jtf = (JTextField) evt.getSource();

        if (!isNumeric(jtf)) {
            jtf.setText("");
        }
    }

    public void setUp() throws DataException {
        
        
        

        //need to find out what claims we have for fica on this company, order them
        try {
            PreparedStatement stmt = GUI.getCon().GetConnection().prepareStatement("select * from taxes where companyID = ? order by taxTypeID, claim, double(underAmount) ");
            stmt.setInt(1, companyID);
            ResultSet rs = stmt.executeQuery();

            int lastTaxTypeID = -1;
            int lastClaim = -1;
            JPanel currentP = new JPanel();
            JPanel lastP = null;
            JTabbedPane currentTP = new JTabbedPane();
            int counter = 0;
            
            while (rs.next()) {
                int taxTypeID = rs.getInt("taxTypeID");
                int claim = rs.getInt("claim");

                if (taxTypeID == FicaEng.TaxType) {
                    //FICA
                    currentTP = FICATabP;
                } else if (taxTypeID == OrWithEng.TaxType) {
                    //withholding
                    currentTP = WithTabP;
                } else{
                    continue;
                }

                if (taxTypeID != lastTaxTypeID) {
                    lastClaim = -1;
                }

                if (claim != lastClaim) {

                    if (lastP != null) {

                        for (int i = 0; i < 25; ++i) {

                            NumericTextField tf = new NumericTextField();
                            tf.setName(lastClaim + "," + lastTaxTypeID + ",underAmount," + (counter + i));
                            lastP.add(tf);

                            tf = new NumericTextField();
                            tf.setName(lastClaim + "," + lastTaxTypeID + ",tax," + (counter + i));
                            lastP.add(tf);
                        }

                    }

                    counter = 0;
                    lastClaim = claim;
                    lastTaxTypeID = taxTypeID;
                    //we are on to the next claim
                    currentP = new JPanel();
                    currentP.setLayout(new GridLayout(0, 2));

                    JLabel jl = new JLabel("Under");
                    currentP.add(jl);

                    jl = new JLabel("Pays");
                    currentP.add(jl);

                    currentTP.addTab("" + claim, currentP);
                }



                ++counter;
                NumericTextField tf = new NumericTextField();
                tf.setName(claim + "," + taxTypeID + ",underAmount," + counter);
                tf.setText("" + rs.getInt("underAmount"));
                currentP.add(tf);

                tf = new NumericTextField();
                tf.setName(claim + "," + taxTypeID + ",tax," + counter);
                
                //this next line is simply to try and catch type cast errors
                final Currency tax = new Currency(rs.getString("tax"));
                tf.setText(tax.toString());
                currentP.add(tf);

                lastP = currentP;
            //lastTP = currentTP;
            }


            //this should get the very last guy:
            if (//lastClaim != -1 && 
                    lastP != null) {

                for (int i = 0; i < 25; ++i) {

                    NumericTextField tf = new NumericTextField();
                    tf.setName(lastClaim + "," + lastTaxTypeID + ",underAmount," + (counter + i));
                    //tf.setText("" + rs.getInt("underAmount"));
                    lastP.add(tf);

                    tf = new NumericTextField();
                    tf.setName(lastClaim + "," + lastTaxTypeID + ",tax," + (counter + i));
                    //tf.setText("" + rs.getInt("tax"));
                    lastP.add(tf);
                }
            }

        } catch (SQLException e) {
            throw new DataException("companyID: " + companyID, e);
        }
        
        
        
        
        //now let's set up our others:
        NumericTextField.addFilter(ssText);
        ssText.setText("" + Tax.getSs(companyID));
        
        NumericTextField.addFilter(ssFromBusinessText);
        ssFromBusinessText.setText("" + Tax.getSsFromBusiness(companyID));
        
        NumericTextField.addFilter(medText);
        medText.setText("" + Tax.getMed(companyID));
        
        NumericTextField.addFilter(medFromBusinessText);
        medFromBusinessText.setText("" + Tax.getMedFromBusiness(companyID));
        
        NumericTextField.addFilter(t941Text);
        t941Text.setText("" + Tax.get941(companyID));
        
        NumericTextField.addFilter(benefitText);
        benefitText.setText("" + Tax.getBenefit(companyID));
        
        NumericTextField.addFilter(unemploymentText);
        unemploymentText.setText("" + Tax.getUnemployment(companyID));
        
        NumericTextField.addFilter(salesTaxTb);
        salesTaxTb.setText("" + Tax.getSalesTax(companyID));
        
        

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        claimCB = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        FICATabP = new javax.swing.JTabbedPane();
        deleteAllFica = new javax.swing.JButton();
        importSingleData = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        withClaimCB = new javax.swing.JComboBox();
        withAddClaimB = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        WithTabP = new javax.swing.JTabbedPane();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        ssText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        medText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        t941Text = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        benefitText = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        unemploymentText = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        salesTaxTb = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ssFromBusinessText = new javax.swing.JTextField();
        medFromBusinessText = new javax.swing.JTextField();
        saveB = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        loadDefaultsB = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tax Manager");

        jTabbedPane1.setName("FICA"); // NOI18N

        jButton1.setText("Add Claim");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        claimCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19" }));
        claimCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                claimCBActionPerformed(evt);
            }
        });

        FICATabP.setVerifyInputWhenFocusTarget(false);
        jScrollPane1.setViewportView(FICATabP);

        deleteAllFica.setText("DELETE ALL FICA DATA");
        deleteAllFica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAllFicaActionPerformed(evt);
            }
        });

        importSingleData.setText("Import Single Data");
        importSingleData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importSingleDataActionPerformed(evt);
            }
        });

        jButton5.setText("Import Married Data");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(claimCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(importSingleData)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deleteAllFica)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(claimCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jButton5)
                    .addComponent(importSingleData)
                    .addComponent(deleteAllFica))
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("FICA", jPanel1);

        withClaimCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19" }));
        withClaimCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                withClaimCBActionPerformed(evt);
            }
        });

        withAddClaimB.setText("Add Claim");
        withAddClaimB.setEnabled(false);
        withAddClaimB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                withAddClaimBActionPerformed(evt);
            }
        });

        WithTabP.setVerifyInputWhenFocusTarget(false);
        jScrollPane2.setViewportView(WithTabP);

        jCheckBox1.setText("Enable Withholding");
        jCheckBox1.setEnabled(false);
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(withClaimCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(withAddClaimB)
                .addGap(18, 18, 18)
                .addComponent(jCheckBox1)
                .addContainerGap(418, Short.MAX_VALUE))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(withClaimCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(withAddClaimB)
                    .addComponent(jCheckBox1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Withholding", jPanel2);

        jLabel1.setText("Social Security from employee");

        jLabel2.setText("Medicare from employee");

        jLabel3.setText("941");

        jLabel4.setText("Benefit");

        jLabel5.setText("Unemployment");

        unemploymentText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unemploymentTextActionPerformed(evt);
            }
        });

        jLabel6.setText("Sales Tax");

        jLabel7.setText("Social Security from business");

        jLabel8.setText("Medicare from business");

        ssFromBusinessText.setText("jTextField1");

        medFromBusinessText.setText("jTextField2");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addGap(31, 31, 31)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ssText, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                    .addComponent(medText)
                    .addComponent(ssFromBusinessText)
                    .addComponent(medFromBusinessText))
                .addGap(113, 113, 113)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(29, 29, 29)
                        .addComponent(unemploymentText, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(66, 66, 66)
                        .addComponent(benefitText))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6))
                        .addGap(54, 54, 54)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(t941Text, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                            .addComponent(salesTaxTb))))
                .addContainerGap(154, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(ssText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(salesTaxTb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(t941Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(ssFromBusinessText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(benefitText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(medText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(unemploymentText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(medFromBusinessText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(173, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Other Stuff", jPanel3);

        saveB.setText("Save");
        saveB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBActionPerformed(evt);
            }
        });

        jButton3.setText("Cancel");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        loadDefaultsB.setText("Restore Oregon 2005 taxes (you don't want to be doing that)");
        loadDefaultsB.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loadDefaultsBMouseEntered(evt);
            }
        });
        loadDefaultsB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadDefaultsBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(saveB)
                .addGap(48, 48, 48)
                .addComponent(jButton3)
                .addGap(169, 169, 169)
                .addComponent(loadDefaultsB)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadDefaultsB)
                    .addComponent(saveB)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    addFicaClaim();


//FICATabP.add(new JTabbedPane("Hello"));
}//GEN-LAST:event_jButton1ActionPerformed

private void claimCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_claimCBActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_claimCBActionPerformed

private void saveBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBActionPerformed
    
    //need to be careful of the doSaveHelper line
    JTabbedPane[] jtps = {FICATabP, WithTabP};
    
    for(int h = 0; h < jtps.length; ++h){
    
        for (int i = 0; i < jtps[h].getComponentCount(); ++i) {

            JPanel jp = (JPanel) jtps[h].getComponent(i);
            
            try{
                doSaveHelper(jp, (h+1), Integer.parseInt(jtps[h].getTitleAt(i)));
            } catch (SQLException ex) {
                MessageDialog.FatalMessageDialog(ex);
            }catch(DataException ex){
                MessageDialog.FatalMessageDialog(ex);
            }
        }
    }

    
    try{
        //now let's save the others:
        Currency x;
        
        x = new Currency(ssText.getText());
        Tax.updateSs(companyID, x);

        x = new Currency(ssFromBusinessText.getText());
        Tax.updateSsFromBusiness(companyID, x);
        
        x = new Currency(medText.getText());
        Tax.updateMed(companyID, x);
        
        x = new Currency(medFromBusinessText.getText());
        Tax.updateMedFromBusiness(companyID, x);

        x = new Currency(t941Text.getText());
        Tax.update941(companyID, x);
            
        x = new Currency(benefitText.getText());
        Tax.updateBenefit(companyID, x);

        x = new Currency(unemploymentText.getText());   
        Tax.updateUnemployment(companyID, x);
        
        x = new Currency(salesTaxTb.getText());   
        Tax.updateSalesTax(companyID, x);
    
    }catch(Exception de){
        GUI.showFatalMessageDialog(de);
    }

    this.dispose();

}//GEN-LAST:event_saveBActionPerformed

private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    this.dispose();
}//GEN-LAST:event_jButton3ActionPerformed

private void loadDefaultsBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadDefaultsBActionPerformed


    //delete and load:
    try{
        DatabaseHelper.insertDefaultTaxesToDatabase(companyID);
    }catch(DataException de){
        GUI.showFatalMessageDialog(de);
    }

    this.dispose();
}//GEN-LAST:event_loadDefaultsBActionPerformed

private void withClaimCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_withClaimCBActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_withClaimCBActionPerformed

private void withAddClaimBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_withAddClaimBActionPerformed
    addWithClaim();
}//GEN-LAST:event_withAddClaimBActionPerformed

private void loadDefaultsBMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loadDefaultsBMouseEntered
// TODO add your handling code here:
//    new AePlayWave("otherStuff.zip" + System.getProperty("file.separator") +
//            
//            "audio" + System.getProperty("file.separator") +"notgood.wav").start();
    //getClass().getResourceAsStream(
    //new AePlayWave("resources" + System.getProperty("file.separator") +"audio" + System.getProperty("file.separator") +"notgood.wav").run();
    //getClass().
//    File f = new File(System.getProperty("user.dir"));
//    
//    getClass().getResource(name)
}//GEN-LAST:event_loadDefaultsBMouseEntered

private void unemploymentTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unemploymentTextActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_unemploymentTextActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void deleteAllFicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAllFicaActionPerformed
        
        try{
            Tax.deleteAllFica(companyID);
        }catch(Exception de){
            GUI.showFatalMessageDialog(de);
        }
        
        this.dispose();
    }//GEN-LAST:event_deleteAllFicaActionPerformed

    private void importSingleDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importSingleDataActionPerformed
        try{
            new FicaImporter(this, true);
        }catch(Exception de){
            GUI.showFatalMessageDialog(de);
        }
        
        this.dispose();
    }//GEN-LAST:event_importSingleDataActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        try{
            new FicaImporter(this, false);
        }catch(Exception de){
            GUI.showFatalMessageDialog(de);
        }
        
        this.dispose();
    }//GEN-LAST:event_jButton5ActionPerformed

    private NumericTextField[][] addFicaClaim() {
            return addPanel(FICATabP, claimCB, FicaEng.TaxType);
    }
    
    private NumericTextField[][] addWithClaim() {
            return addPanel(WithTabP, withClaimCB, OrWithEng.TaxType);
    }

     /**
     * Get the value of companyID
     *
     * @return the value of companyID
     */
    public int getCompanyID() {
        return companyID;
    }

    /**
     * Set the value of companyID
     *
     * @param companyID new value of companyID
     */
    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane FICATabP;
    private javax.swing.JTabbedPane WithTabP;
    private javax.swing.JTextField benefitText;
    private javax.swing.JComboBox claimCB;
    private javax.swing.JButton deleteAllFica;
    private javax.swing.JButton importSingleData;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton loadDefaultsB;
    private javax.swing.JTextField medFromBusinessText;
    private javax.swing.JTextField medText;
    private javax.swing.JTextField salesTaxTb;
    private javax.swing.JButton saveB;
    private javax.swing.JTextField ssFromBusinessText;
    private javax.swing.JTextField ssText;
    private javax.swing.JTextField t941Text;
    private javax.swing.JTextField unemploymentText;
    private javax.swing.JButton withAddClaimB;
    private javax.swing.JComboBox withClaimCB;
    // End of variables declaration//GEN-END:variables

    

    
    
    public static class IntegrationTestHelper {
        
        private final static long timeToWait = 1000;
        private static final HashMap<String, ReturningActionListener> actionListeners =
                new HashMap<String, ReturningActionListener>();
        
        
        public static void addFicaClaim(final String claim, final String[][] values){
            
            //find right claim index
            final JComboBox _claimCB = lastInstance.get().claimCB;
            final int claimCbItemCount = _claimCB.getItemCount();
            for(int claimIdx = 0; claimIdx < claimCbItemCount; ++claimIdx){
                final String claimStringValue = _claimCB.getItemAt(claimIdx).toString();
                if(claimStringValue.equals(claim)){
                    _claimCB.setSelectedIndex(claimIdx);
                    break;
                }
            }
            
            //now add claim
            final NumericTextField[][] taxAndUnders = lastInstance.get().addFicaClaim();
            
            //now fill in values
            assertTrue("values has length: " + values.length + ", which is greater than " + taxAndUnders.length, 
                    values.length <= taxAndUnders.length);
            for(int rowIdx = 0; rowIdx < values.length; ++rowIdx){
                assertTrue("values[" + rowIdx + "] has length " + values[rowIdx].length + ", which is less than " + taxAndUnders[rowIdx].length,
                        values[rowIdx].length <= taxAndUnders[rowIdx].length);
                
                for(int colIdx = 0; colIdx < values[rowIdx].length; ++colIdx){
                    taxAndUnders[rowIdx][colIdx].setText(values[rowIdx][colIdx]);
                }
            }
            
            //now save
            lastInstance.get().saveBActionPerformed(null);
            
        }

        public static void addWithClaim(final String claim, final String[][] values){
            
            //find right claim index
            final JComboBox _claimCB = lastInstance.get().withClaimCB;
            final int claimCbItemCount = _claimCB.getItemCount();
            for(int claimIdx = 0; claimIdx < claimCbItemCount; ++claimIdx){
                final String claimStringValue = _claimCB.getItemAt(claimIdx).toString();
                if(claimStringValue.equals(claim)){
                    _claimCB.setSelectedIndex(claimIdx);
                    break;
                }
            }
            
            //now add claim
            final NumericTextField[][] taxAndUnders = lastInstance.get().addWithClaim();
            
            //now fill in values
            assertTrue("values has length: " + values.length + ", which is greater than " + taxAndUnders.length, 
                    values.length <= taxAndUnders.length);
            
            for(int rowIdx = 0; rowIdx < values.length; ++rowIdx){
                assertTrue("values[" + rowIdx + "] has length " + values[rowIdx].length + ", which is less than " + taxAndUnders[rowIdx].length,
                        values[rowIdx].length <= taxAndUnders[rowIdx].length);
                
                for(int colIdx = 0; colIdx < values[rowIdx].length; ++colIdx){
                    taxAndUnders[rowIdx][colIdx].setText(values[rowIdx][colIdx]);
                }
            }
            
            //now save
            lastInstance.get().saveBActionPerformed(null);
            
            
        }
        
        static void setSs(String rate) {
            
            lastInstance.get().ssText.setText(rate);
            
            lastInstance.get().saveBActionPerformed(null);
            
        }
        
        static void assertSs(String rate){
            final Currency goingRate = new Currency(lastInstance.get().ssText.getText());
            assertEquals(new Currency(rate), goingRate);
        }
        
        static void setMed(String rate){
            lastInstance.get().medText.setText(rate);
            
            lastInstance.get().saveBActionPerformed(null);
            
        }
        
        static void assertMed(String rate){
            final Currency goingRate = new Currency(lastInstance.get().medText.getText());
            assertEquals(new Currency(rate), goingRate);    
        }
        
//
//        x = new Currency(t941Text.getText());
//        Tax.update941(companyID, x);
//            
//        x = new Currency(benefitText.getText());
//        Tax.updateBenefit(companyID, x);
//
//        x = new Currency(unemploymentText.getText());   
//        Tax.updateUnemployment(companyID, x);
        
    }
}
