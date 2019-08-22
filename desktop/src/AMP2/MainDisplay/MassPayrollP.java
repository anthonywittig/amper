package AMP2.MainDisplay;

import AMP2.BankStuff.Currency;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.Payroll.Adjustment;
import AMP2.Payroll.Employee;
import AMP2.Payroll.PayDates;
import AMP2.Payroll.PayPeriod;
import com.wittigweb.swing.NumericTextField;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class MassPayrollP extends JScrollPane{

    private final JFrame frame;
    private final PayDates payDate;
    private final List<Pair<Employee, Pair<NumericTextField, Currency>>> employeeToHours = new ArrayList<Pair<Employee, Pair<NumericTextField, Currency>>>();
    private final int companyId;
    
    public static MassPayrollP addToFrame(
            JFrame frame,
            ActionListener action,
            List<Employee> employees,
            int companyId){
        
        PayDates payDates = getPayDate(frame, employees);
        if(payDates == null){
            //they canceled the paydate selection, exit:
            action.actionPerformed(null);
            return null;
        }
        
        employees = getEmployeesMissingDate(employees, payDates);
        
        MassPayrollP payrollP = new MassPayrollP(action, employees, frame, payDates, companyId);
        
        Container containerP = frame.getContentPane();
        containerP.removeAll();
        containerP.add(payrollP);
        frame.pack();
        
        return payrollP;
    }

    private MassPayrollP(
            final ActionListener action,
            List<Employee> employees,
            JFrame frame,
            PayDates payDate,
            int companyId){
        this.frame = frame;
        this.payDate = payDate;
        this.companyId = companyId;

        JPanel bigColumn = new JPanel();
        bigColumn.setLayout(new BoxLayout(bigColumn, BoxLayout.PAGE_AXIS)); 
        
        setViewportView(bigColumn);
        
        bigColumn.add(new JLabel(" "));
        JButton cancel = new JButton("I change my mind");
        cancel.addActionListener(action);
        bigColumn.add(cancel);
        
        bigColumn.add(new JLabel(" "));
        
        bigColumn.add(new JLabel("Payroll for: " + payDate.toString() + " (only showing employees who don't have this date)"));
        
        bigColumn.add(new JLabel(" "));
        
        JPanel header = new JPanel();
        bigColumn.add(header);
        
        int numberOfColumns = 4;
        header.setLayout(new GridLayout(1, numberOfColumns));
        header.add(new JLabel(" "));
        header.add(new JLabel("Name"));
        header.add(new JLabel("Hours"));
        header.add(new JLabel(" "));
        
        //just incase we ever change the workflow, let's clear this:
        employeeToHours.clear();
        for(Employee employee : employees){
            if(!employee.getIsCurrentEmp()){
                continue;
            }
            
            employee.getLastPayPeriod();
            JPanel employeeRow = new JPanel();
            bigColumn.add(employeeRow);
            
            NumericTextField employeeHours = new NumericTextField();
            Pair<NumericTextField, Currency> hours = new MutablePair<NumericTextField, Currency>(employeeHours, null);
            employeeToHours.add(new ImmutablePair<Employee, Pair<NumericTextField, Currency>>(employee, hours));
            
            employeeRow.setLayout(new GridLayout(1, numberOfColumns));
            employeeRow.add(new JLabel(" "));
            employeeRow.add(new JLabel(employee.getName()));
            employeeRow.add(employeeHours);
            employeeRow.add(new JLabel(" "));
        }
        
        bigColumn.add(new JLabel(" "));
        bigColumn.add(new JLabel(" "));
        bigColumn.add(new JLabel(" "));
        
        JButton go = new JButton("Let's do this");
        go.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                
                //validate all numeric
                for(Pair<Employee, Pair<NumericTextField, Currency>> employeeToHour : employeeToHours){
                    Pair<NumericTextField, Currency> fieldToNumber = employeeToHour.getRight();
                    NumericTextField field = fieldToNumber.getLeft();
                    //basically this is just checking that all fields are numeric
                    Currency hours = new Currency(field.getText());
                    fieldToNumber.setValue(hours);
                }
                
                //add pay periods
                for(Pair<Employee, Pair<NumericTextField, Currency>> employeeToHour : employeeToHours){
                    Employee employee = employeeToHour.getLeft();
                    Pair<NumericTextField, Currency> fieldToNumber = employeeToHour.getRight();
                    Currency hours = fieldToNumber.getRight();
                    
                    try{
                        final PayPeriod newP = PayPeriod.getNewInstance(
                                employee.getClaim(), 
                                MassPayrollP.this.payDate, 
                                employee.getCurrentRate(), 
                                hours, 
                                MassPayrollP.this.companyId, 
                                new ArrayList<Adjustment>());

                        employee.addPayPeriod(newP);
                    } catch (DataException ex) {
                        GUI.showFatalMessageDialog(ex);
                    }
                }
                
                action.actionPerformed(e);
            }
        });
        bigColumn.add(go);
        
        bigColumn.add(new JLabel(" "));
        bigColumn.add(new JLabel(" "));
        bigColumn.add(new JLabel(" "));
        
        
//        JPanel pan = new JPanel();
//        pan.setLayout(new GridLayout(1, 2));
//        pan.setBackground(new Color(63000));
//        checksP.add(pan);
//        
//        JLabel lab = new JLabel("Total:");
//        pan.add(lab);
//        
//        JLabel totalL = new JLabel();
//        pan.add(totalL);
//        
//        pan = new JPanel();
//        pan.setLayout(new GridLayout(1, 5));
//        
//        lab = new JLabel("Check #", c);
//        pan.add(lab);
//        lab = new JLabel("Date", c);
//        pan.add(lab);
//        lab = new JLabel("To", c);
//        pan.add(lab);
//        lab = new JLabel("Amount", c);
//        pan.add(lab);
//        lab = new JLabel("G.L.", c);
//        pan.add(lab);
//        lab = new JLabel("Posted", c);
//        pan.add(lab);
//        lab = new JLabel("  Likely Post  ", c);
//        pan.add(lab);
//        lab = new JLabel("For", c);
//        pan.add(lab);
//        checksP.add(pan);
//        
//        int color = 0;
//        Currency checkTotal = Currency.Zero;
//        
//        for(Check check : checks){
//            checkTotal = checkTotal.add(check.getAmount());
//            
//            Color colorValue;
//            if(color == 2) {
//                colorValue = Color.YELLOW;
//                color = 0;
//            }
//            else {
//                color++;  
//                colorValue = Color.WHITE;
//            }
//            
//            CheckB panB = new CheckB(bHTP, check, colorValue);
//            checkBs.add(panB);
//            checksP.add(panB);
//        }
//        totalL.setText(checkTotal.twoDecFormat());
//        
//        JButton done = new JButton("Done");
//        done.addActionListener(closeAction);
//        
//        checksP.add(done);
    }

    private static PayDates getPayDate(JFrame frame, List<Employee> employees) {
        PayDates toUse = null;
        for(Employee employee : employees){
            PayPeriod payP = employee.getLastPayPeriod();
            if(payP != null){
                PayDates payD = payP.getDate().getNextPD();
                if(toUse == null || payD.after(toUse)){
                    toUse = payD;
                }
            }
        }
        
        DateDialog dateD = new DateDialog(frame, toUse);
        return dateD.getNewPayDate();
    }
    
    private static List<Employee> getEmployeesMissingDate(List<Employee> employees, PayDates payDates) {
        List<Employee> filteredEmployees = new ArrayList<Employee>();
        
        for(Employee employee : employees){
            PayPeriod payPeriod = employee.getLastPayPeriod();
            if(payPeriod == null || payDates.after(payPeriod.getDate())){
                filteredEmployees.add(employee);
            }
        }
        
        return filteredEmployees;
    }
}
