package AMP2.MainDisplay;

import AMP2.MainDisplay.util.ReturningActionListener;
import AMP2.Payroll.PayDates;
import AMP2.Payroll.PayPeriod;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class DateDialog {
    
    private final AtomicReference<JDialog> newDate = new AtomicReference<JDialog>(null);
    private JComboBox monthList, sectionList, yearList;
    private PayDates newPayDate;
    private final JFrame owner;

    public DateDialog(JFrame owner) {
        this(owner, new PayDates());
    }
    
    public DateDialog(JFrame owner, List<PayPeriod> payPeriods) {
        this.owner = owner;
        if(payPeriods != null && !payPeriods.isEmpty()){//CollectionUtils.isNotEmpty
            PayPeriod lastPayPeriod = payPeriods.get(payPeriods.size() - 1);
            setup(lastPayPeriod.getDate());
        }else{
            setup(new PayDates());
        }
    }
    
    public DateDialog(JFrame owner, PayDates payDate) {
        this.owner = owner;
        if(payDate == null){
            payDate = new PayDates();
        }
        setup(payDate);
    }
    
    private void setup(PayDates payDates) {

        JPanel pan1 = new JPanel();
        pan1.setLayout(new BoxLayout(pan1, BoxLayout.PAGE_AXIS));
        int c = SwingConstants.CENTER;

        JPanel pan2 = new JPanel();
        pan2.setLayout(new GridLayout(1, 3));
        JLabel lab = new JLabel("Month", c);
        pan2.add(lab);
        lab = new JLabel("   Section   ", c);
        pan2.add(lab);
        lab = new JLabel("Year", c);
        pan2.add(lab);
        pan1.add(pan2);

        pan2 = new JPanel();
        pan2.setLayout(new GridLayout(1, 3));
        String[] months = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12"
        };
        monthList = new JComboBox(months);
        monthList.setSelectedIndex(payDates.getMonthI() - 1);
        pan2.add(monthList);

        String[] sections = {"1-15", "16th on"};
        sectionList = new JComboBox(sections);
        sectionList.setSelectedIndex(payDates.getSectionI() - 1);
        pan2.add(sectionList);

        final List<String> yearsL = new ArrayList<String>();
        final int finalYear = Calendar.getInstance().get(Calendar.YEAR) + 2;
        for (int year = 2005; year <= finalYear; ++year) {
            yearsL.add("" + year);
        }


        String[] years = yearsL.toArray(new String[0]);
        yearList = new JComboBox(years);
        yearList.setSelectedItem("" + payDates.getYear());
        pan2.add(yearList);
        pan1.add(pan2);

        pan2 = new JPanel();
        JButton go = new JButton("Go");
        final ReturningActionListener al = new ReturningActionListener() {

            @Override
            public Object actionPerformedAndReturn(ActionEvent e) {
                String str = (String) sectionList.getSelectedItem();
                int sec = 0;

                if (str.equals("1-15")) {
                    sec = 1;
                }
                if (str.equals("16th on")) {
                    sec = 2;
                }
                newPayDate = new PayDates(new Integer((String) monthList.getSelectedItem()), sec, new Integer((String) yearList.getSelectedItem()));
                newDate.get().setVisible(false);
                newDate.set(null);
                
                return null;
            }
        };
        GUI.IntegrationTestHelper.putActionListener(GUI.IntegrationTestHelper.AlKeys.goPayPeriodDateSetForFirstTime, al);

        go.addActionListener(al);
        pan2.add(go);

        pan1.add(pan2);
        newDate.set(new JDialog(owner, true));
        newDate.get().add(pan1);
        newDate.get().pack();
        newDate.get().setVisible(true);
    }

    PayDates getNewPayDate() {
        return newPayDate;
    }

}
