package AMP2.MainDisplay;

import AMP2.Payroll.Employee;
import AMP2.Payroll.OrWithEng;
import AMP2.Payroll.PayDates;
import AMP2.Payroll.PayPeriod;
import com.wittigweb.swing.NumericTextField;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class PayPeriodPanel extends JPanel{
    
    private JTextField rate, 
            hours,
            orWith,
            claim,
            adjustmentNote;
    private NumericTextField adjustmentValue;
    private PayPeriod lastDeletedPayP;
    private PayDates newPayDate;
    private final JFrame dateDialogOwner;
    
    /**
     * A method that sets up a payPeriod panel.
     *
     * @param isAdd, true if it will be set up with no hour data ect.
     *               false if we will be finding the data to populate...
     * @param bPanel, our last panel to be added, probably buttons.
     * @param emp, our employee.
     *
     * @return pan, our panel.
     */    
    PayPeriodPanel(
            boolean isAdd, 
            boolean newDate, 
            JPanel bPanel,
            Employee emp,
            PayDates newPayDate,
            JFrame dateDialogOwner) {
    
        this.newPayDate = newPayDate;
        this.dateDialogOwner = dateDialogOwner;
        setup(isAdd, newDate, bPanel, emp);
    }
    
    private void setup( 
            boolean isAdd, 
            boolean newDate, 
            JPanel bPanel,
            Employee emp){

        if (!isAdd) {
            newDate = false;
        }
        
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JPanel pan1 = new JPanel();
        
        final int numberOfColumns;
        if(OrWithEng.orWithEnabled()){
            numberOfColumns = 7;
        }else {
            numberOfColumns = 6;
        }
        pan1.setLayout(new GridLayout(1, numberOfColumns));
        int c = SwingConstants.CENTER;
        int l = SwingConstants.LEFT;
        JLabel lab;

        lab = new JLabel("Date", c);
        pan1.add(lab);
        lab = new JLabel("Rate", c);
        pan1.add(lab);
        lab = new JLabel("Hours", c);
        pan1.add(lab);
        
        if(OrWithEng.orWithEnabled()){
            lab = new JLabel("OR W/H", c);
            pan1.add(lab);
        }
        lab = new JLabel("Claim", c);
        pan1.add(lab);
        
        pan1.add(new JLabel("Adj.", c));
        pan1.add(new JLabel("Adj. Notes", c));
        
        this.add(pan1);

        pan1 = new JPanel();
        pan1.setLayout(new GridLayout(1, numberOfColumns));

        if (isAdd) {
            if (emp.getLastPayPeriod() != null && newDate != true) {
                lab = new JLabel(emp.getLastPayPeriod().getDate().getNextPD().
                        toString(), l);
            } else {
                DateDialog dateD = new DateDialog(dateDialogOwner);
                newPayDate = dateD.getNewPayDate();
                lab = new JLabel(newPayDate.toString(), l);
            }
            pan1.add(lab);

            rate = new JTextField("" + emp.getCurrentRate());

            pan1.add(rate);

            hours = new JTextField();
            pan1.add(hours);
            
            orWith = new JTextField("Auto");
            if(OrWithEng.orWithEnabled()){
                pan1.add(orWith);
            }
            
            claim = new JTextField("Auto");
            pan1.add(claim);

            adjustmentValue = new NumericTextField("0", 5);
            pan1.add(adjustmentValue);
            
            adjustmentNote = new JTextField("");
            pan1.add(adjustmentNote);
            
            this.add(pan1);
        } else {
            final List<PayPeriod> payPs = emp.getPayPeriods();
            PayPeriod mPayP = null;

            for(PayPeriod payP : payPs){
                if (payP.getDate().toString().equals(newPayDate.toString())) {
                    mPayP = payP;
                }
            }

            if (mPayP != null) {

                lab = new JLabel(mPayP.getDate().toString(), l);
                pan1.add(lab);
                rate = new JTextField("" + mPayP.getRate());
                pan1.add(rate);
                hours = new JTextField("" + mPayP.getHours());
                pan1.add(hours);
                
                orWith = new JTextField("Auto");
                if(OrWithEng.orWithEnabled()){
                    pan1.add(orWith);
                }
              
                claim = new JTextField("" + mPayP.getClaim());
                pan1.add(claim);

                adjustmentValue = new NumericTextField();
                pan1.add(adjustmentValue);
                adjustmentNote = new JTextField(); 
                pan1.add(adjustmentNote);
                if(!mPayP.getAdjustments().isEmpty()){
                    adjustmentValue.setText(mPayP.getAdjustments().get(0).getAmount().toString());
                    adjustmentNote.setText(mPayP.getAdjustments().get(0).getNote());
                }
                
                this.add(pan1);
                lastDeletedPayP = mPayP;
                payPs.remove(mPayP);
            } else {
                lab = new JLabel("Match not found");
                pan1.add(lab);
                this.add(pan1);
            }
        }
        this.add(bPanel);
    }

    

    public PayPeriod getLastDeletedPayP() {
        return lastDeletedPayP;
    }

    void nowShowing() {
        hours.requestFocus();
    }

    String getOrWith() {
        return orWith.getText();
    }

    String getClaim() {
        return claim.getText();
    }

    String getAdjustmentValue() {
        return adjustmentValue.getText().trim();
    }

    String getAdjustmentNote() {
        return adjustmentNote.getText();
    }

    String getHours() {
        return hours.getText();
    }

    String getRate() {
        return rate.getText();
    }

    void setHours(String hoursT) {
        hours.setText(hoursT);
    }

    void setRate(String rateT) {
        rate.setText(rateT);
    }

    PayDates getNewPayDate() {
        return newPayDate;
    }
}
