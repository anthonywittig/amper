package AMP2.MainDisplay;

import AMP2.BankStuff.BHTPane;
import AMP2.BankStuff.BankHealth;
import AMP2.BankStuff.Check;
import AMP2.BankStuff.CheckBook;
import AMP2.BankStuff.Currency;
import AMP2.BankStuff.GlCode;
import AMP2.BankStuff.GlCode.Code;
import AMP2.BankStuff.Transaction;
import AMP2.BankStuff.check.Checks;
import AMP2.BankStuff.check.ChecksImpl;
import AMP2.DatabaseStuff.CompanyDBWrap;
import AMP2.DatabaseStuff.DatabaseHelper;
import AMP2.Payroll.Employee;
import AMP2.Payroll.HumanResources;
import AMP2.Payroll.PTaxes;
import AMP2.Payroll.PayDates;
import AMP2.Payroll.PayPeriod;
import AMP2.Payroll.TaxHandler;
import AMP2.Util.FileHandler;
import AMP2.Util.PrintUtilities;
import AMP2.Util.TheFile4;
import AMP2.DatabaseStuff.db.ConnectionWrapper;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.db.SmartConnection;
import AMP2.Days.ControllerG;
import AMP2.Days.DayG;
import AMP2.Days.DayGPanel;
import AMP2.Days.SearchG;
import AMP2.Days.WeekG;
import AMP2.MainDisplay.util.ReturningActionListener;
import AMP2.Payroll.Adjustment;
import AMP2.Payroll.OrWithEng;
import AMP2.Payroll.PayrollStats;
import AMP2.Util.MiscStuff;
import com.wittigweb.swing.NumericTextField;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import static junit.framework.Assert.*;
import javax.swing.WindowConstants;
import org.apache.commons.lang3.StringUtils;


public class GUI {

    private static GUI currentInstance;
    private static String Version = "2014.03.22";

    
    //with this version we added javaDB
    //"08.10.15";    
    //with this version we fixed a couple of bugs, like the dbbackup and log folders
    //"08.09.10";
    //with this version we kill the load file (no more Serializable)
    protected int companyID = -1;
    protected static ConnectionWrapper con = new SmartConnection();
    TheFile4 tFile; // when updating must change private TheFile3 getTF3()
    FileHandler fH;
    TaxHandler tH;
    Employee currentEmp;
    /*PayDates nPayDate;*/
    PayPeriod lastDeletedPayP;
    HumanResources hR;
    ControllerG cG;
    BankHealth bH;
    DayGPanel dayGPanel;
    EnvelopeF envelopeF;
    boolean firstSetUpFrame = true;
    JFrame frame, addPayP, addEmp, editPayP, deletePayP, editEmp, deleteEmp,
            addDayF;
    JMenuBar menuB;
    Container contentP;
    JTabbedPane tabbedP, cEmployeeT, pEmployeeT, monthT;
    JPanel cEmployeeP, pEmployeeP, taxP, totalPayrollP, grossDayP, bankP,
            mainSortGP = new JPanel(), sortGP = new JPanel();
    JTextField /*rate, hours, orWith, claim,*/
            nEmpName, nEmpSS, nEmpAddress, nEmpClaim, nEmpRate,
            sortLessT, sortGreaterT/*, adjustmentValue, adjustmentNote*/;
    //final AtomicReference<JDialog> nDate = new AtomicReference<JDialog>(null);
    JComboBox /*monthList, sectionList, yearList,*/ yesNo;
    boolean displayEmpFirstTime = true;
    boolean globalUseDateD = false;
    boolean saveOnExit = true;
    Dimension frameSize = new Dimension(810, 575);
    String lastSave = null;
    private ReturningActionListener createCompany;
    private InsertCompanyDialog lastInsertCompanyDialog;
    private PayPeriodPanel payPeriodPanel;
    
    /**
     * Our Main Method...
     */
    public static void main(String[] args) {
        try {
            new GUI();
        } catch (Exception ex) {
            showFatalMessageDialog(ex);
        }
    }
    
    public static void showFatalMessageDialog(Exception e) {
        MessageDialog.FatalMessageDialog(e);
    }

    /**
     * Constructor for objects of class GUI
     */
    private GUI() throws DataException {
        long s = Calendar.getInstance().getTimeInMillis();

        //make sure tables exists/are updated.
        DatabaseHelper.CreateUpdateTables();

        cG = new ControllerG();
        hR = new HumanResources();
        bH = new BankHealth();

        fH = new FileHandler();

        setUpFrame();
        long s2 = Calendar.getInstance().getTimeInMillis();



        tryDatabaseLoad();
        //tryLastSave();
        
        //if we don't have a company, ask for one:
        while(getCompanyID() < 1){
            createCompany.actionPerformed(null);
        }

        long e = Calendar.getInstance().getTimeInMillis();
        MiscStuff.writeToLog("" + (s2 - s));
        MiscStuff.writeToLog("" + (e - s2));
        MiscStuff.writeToLog("" + (e - s)); 


        currentInstance = this;

    }

    /**
     * A method that adds an employee.
     */
    private void addEmp() {
        addEmp = new JFrame();
        Container cp = addEmp.getContentPane();

        JPanel pan1 = new JPanel();
        JButton createEmp = new JButton("Create Employee");
        final ReturningActionListener createEmpAL =
                new ReturningActionListener() {

                    public Object actionPerformedAndReturn(ActionEvent e) {
                        Employee newEmp = new Employee(nEmpName.getText(),
                                nEmpSS.getText(), nEmpAddress.getText(),
                                new Integer(nEmpClaim.getText()),
                                new Currency(nEmpRate.getText()));

                        if (((String) yesNo.getSelectedItem()).equals("No")) {
                            newEmp.setIsCurrentEmp(false);
                        }
                        hR.addEmployee(newEmp);
                        addEmp.setVisible(false);
                        displayEverything(0);
                        
                        return null;
                    }
                };
        IntegrationTestHelper.putActionListener(IntegrationTestHelper.AlKeys.createEmployee, createEmpAL);
        createEmp.addActionListener(createEmpAL);
        pan1.add(createEmp);

        cp.add(employeeIP(null, pan1));
        addEmp.pack();
        addEmp.setVisible(true);
    }

    /**
     * A method that adds a pay period to a given employee off of our add
     * payperiod frame.
     *
     * @param employee, our employee to be added to.
     * @param useDate, true if we should use the Date Dialog
     *                 false if we should use the .getNextPD();
     */
    private PayPeriod addPayPeriod(Employee employee, boolean useDate, PayPeriodPanel payPeriodPanel) {
        Employee emp = employee;
        boolean norm = false;
        boolean useDateD = useDate;

        if (emp.getLastPayPeriod() == null) {
            useDateD = true;
        }
        
        if (payPeriodPanel.getOrWith().equals("Auto")
                && payPeriodPanel.getClaim().equals("Auto")) {
            
            norm = true;
        }
        
        try {
            //get adjustment
            final List<Adjustment> adjustments = new ArrayList<Adjustment>();
            final Currency adjustmentC;
            final String adjustmentValueS = payPeriodPanel.getAdjustmentValue();
            try{
                adjustmentC = new Currency(StringUtils.isEmpty(adjustmentValueS) ? "0" : adjustmentValueS);
            } catch(Exception e){
                throw new InvalidUserInput("can't use \"" + adjustmentValueS + "\" for adjustment value", e);
            }
            final String adjustmentNoteS = payPeriodPanel.getAdjustmentNote();
            if(adjustmentC.ne(0) && StringUtils.isNotEmpty(adjustmentNoteS)){
                final Adjustment adjustment = new Adjustment(adjustmentC, adjustmentNoteS);
                adjustments.add(adjustment);
            }
            
            if (norm == true) {
                PayDates newPD;

                if (!useDateD) {
                    newPD = emp.getLastPayPeriod().getDate().getNextPD();
                } else {
                    newPD = payPeriodPanel.getNewPayDate();
                }

                final Currency hoursC;
                try {
                    hoursC = new Currency(payPeriodPanel.getHours());
                } catch (Exception e) {
                    throw new InvalidUserInput("can't use \"" + payPeriodPanel.getHours() + "\" for hours", e);
                }

                final Currency rateC;
                try {
                    rateC = new Currency(payPeriodPanel.getRate());
                } catch (Exception e) {
                    throw new InvalidUserInput("can't use \"" + payPeriodPanel.getRate() + "\" for rate", e);
                }
                
                final PayPeriod newP = PayPeriod.getNewInstance(
                        emp.getClaim(), newPD, rateC, hoursC, getCompanyID(), adjustments);

                emp.addPayPeriod(newP);
                return newP;
            } else {
                int claimInt;

                if (payPeriodPanel.getClaim().equals("Auto")) {
                    claimInt = emp.getClaim();
                } else {
                    claimInt = new Integer(payPeriodPanel.getClaim());
                }
                PayDates newPD;

                if (!useDateD) {
                    newPD = emp.getLastPayPeriod().getDate().getNextPD();
                } else {
                    newPD = payPeriodPanel.getNewPayDate();
                }

                PayPeriod newP = PayPeriod.getNewInstance(claimInt,
                        newPD, new Currency(payPeriodPanel.getRate()),
                        new Currency(payPeriodPanel.getHours()), getCompanyID(), 
                        adjustments);

                if (!payPeriodPanel.getOrWith().equals("Auto")) {
                    newP.setOrWith(new Currency(payPeriodPanel.getOrWith()));
                }
                emp.addPayPeriod(newP);
                return newP;
            }
        } catch (DataException e) {
            GUI.showFatalMessageDialog(e);
        } catch (InvalidUserInput e) {
            MessageDialog.WarnMessageDialog(e);
        }
        return null;
    }

    /**
     * A method that does our closing...
     */
    private void closeFrame() {
        int choice = JOptionPane.showConfirmDialog(frame,
                "Save on Exit?", "Baby We Were Born to Run.",
                JOptionPane.YES_NO_CANCEL_OPTION);

        if (choice == JOptionPane.OK_OPTION) {
            
            contentP.removeAll();
            
            final JPanel closingPanel = new JPanel();
            closingPanel.setLayout(new BoxLayout(closingPanel, BoxLayout.PAGE_AXIS));
            contentP.add(closingPanel);
            
            closingPanel.add(new JLabel("Please wait a minute while we tidy things up..."));
     
            final Dimension imageSize = frameSize.getSize();
            final ImageIcon jacob = new ImageIcon(((new ImageIcon(getClass().getResource("/AMP2/resources/images/jacobAndFish.jpg"))).getImage()).getScaledInstance(imageSize.width, imageSize.height, java.awt.Image.SCALE_SMOOTH));
//          
            closingPanel.add(new JLabel(jacob));
              frame.setSize(frameSize);
            frame.setVisible(true);
            javax.swing.SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    saveDatabase();
                    System.exit(0);
                }
            });
            
        }else if (choice == JOptionPane.CANCEL_OPTION) {
            //MiscStuff.writeToLog("cancel");
            return; //stop...
        }else{
            System.exit(0);
        }

        

    }

    /**
     * A method that displays our bankHealth on our bankP...
     */
    private void displayBank() {

        if (bH == null) {
            try {
                FileHandler fH = new FileHandler();
                bH = (BankHealth) fH.readObAn("AMP\\BankHealth");
                //MiscStuff.writeToLog("try");
            } catch (IOException ex) {
            } catch (ClassNotFoundException cnf) {
            }
        }
        if (bH == null) {
            bH = new BankHealth();
        }
        bankP.removeAll();
        bankP.add(BHTPane.getInstance(bH));

    }

    /**
     * A method that displays our days on our grossDayP
     */
    private void displayDays() {
        grossDayP.removeAll();

        monthT = new JTabbedPane();
        int c = SwingConstants.CENTER;
        int r = SwingConstants.RIGHT;
        int l = SwingConstants.LEFT;
        int lastUsedC = 0;

        //cG.addDayG(new DayG(Calendar.getInstance(), 801));
        //if(cG.getDayGs != null)
        List<DayG> days = cG.getDayGs();
        List<WeekG> weeks = cG.getWeekGs();

        for (int i = 0; i < 12; i++) {
            PayDates date = new PayDates(i + 1, 1, 0);
            JPanel pan = new JPanel();
            pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));

            JPanel pan1 = new JPanel();
            JButton edit = new JButton("Edit");
            edit.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    editDayGs();
                }
            });
            pan1.add(edit);
            pan.add(pan1);

            JPanel mainPan = new JPanel();
            mainPan.setLayout(new GridLayout(1, 2));
            JPanel mainPan1 = new JPanel();
            mainPan1.setLayout(new BoxLayout(mainPan1, BoxLayout.PAGE_AXIS));
            JPanel mainPan2 = new JPanel();
            mainPan2.setLayout(new BoxLayout(mainPan2, BoxLayout.PAGE_AXIS));
            mainPan.add(mainPan1);
            mainPan.add(mainPan2);

            JLabel lab;
            for (int nP = 0; nP < 2; nP++) {
                pan1 = new JPanel();
                pan1.setLayout(new GridLayout(1, 4));

                lab = new JLabel("Date");
                pan1.add(lab);
                lab = new JLabel("Gross");
                pan1.add(lab);
                lab = new JLabel("7 day Adv.");
                pan1.add(lab);
                lab = new JLabel("Notes");
                pan1.add(lab);

                if (nP == 0) {
                    mainPan1.add(pan1);
                } else {
                    mainPan2.add(pan1);
                }
            }

            Currency grossTotals = Currency.Zero;
            Currency advAdv = Currency.Zero;
            Currency totalDays = Currency.Zero;

            Currency fHGrossTotals = Currency.Zero, fHAdvAdv = Currency.Zero, fHTotalDays = Currency.Zero;
            Currency lHGrossTotals = Currency.Zero, lHAdvAdv = Currency.Zero, lHTotalDays = Currency.Zero;

            for (int j = 0; j < days.size(); j++) {

                pan1 = new JPanel();
                pan1.setLayout(new GridLayout(1, 4));
                DayG day = (DayG) days.get(j);

                //boolean col = false;
                if (day.getDayOfWeekS().equals("Sat")) {
                    //col = true;
                    pan1.setBackground(new Color(-63000));
                }

                WeekG week = (WeekG) weeks.get(j);
                if (day.getMonth() == i) {
                    lab = new JLabel(day.toStringCal());
                    pan1.add(lab);
                    lab = new JLabel(day.getGross().toString());
                    pan1.add(lab);
                    lab = new JLabel(week.getFigure(0).toString());
                    pan1.add(lab);
                    lab = new JLabel(day.getNote());
                    pan1.add(lab);

                    grossTotals = grossTotals.add(day.getGross());
                    advAdv = advAdv.add(day.getGross());
                    totalDays = totalDays.add(1);


                    if (day.getDay() < 16) {
                        mainPan1.add(pan1);
                        fHGrossTotals = fHGrossTotals.add(day.getGross());
                        fHAdvAdv = fHAdvAdv.add(day.getGross());
                        fHTotalDays = fHTotalDays.add(1);
                    } else {
                        mainPan2.add(pan1);
                        lHGrossTotals = lHGrossTotals.add(day.getGross());
                        lHAdvAdv = lHAdvAdv.add(day.getGross());
                        lHTotalDays = lHTotalDays.add(1);
                    }
                    lastUsedC = i;
                }
            }
            pan1 = new JPanel();
            pan1.setLayout(new GridLayout(1, 4));
            pan1.setBackground(Color.green);

            lab = new JLabel("First Half");
            pan1.add(lab);
            lab = new JLabel(fHGrossTotals.toString());
            pan1.add(lab);
            final String fHTotalsAdvString;
            if(fHTotalDays == Currency.Zero){
                fHTotalsAdvString = "N/A";
            }else{
                fHTotalsAdvString = fHAdvAdv.divide(fHTotalDays).format3();
            }
            lab = new JLabel(fHTotalsAdvString);
            pan1.add(lab);
            lab = new JLabel("First Half");
            pan1.add(lab);

            mainPan1.add(pan1);

            pan1 = new JPanel();
            pan1.setLayout(new GridLayout(1, 4));
            pan1.setBackground(Color.green);

            lab = new JLabel("Second Half");
            pan1.add(lab);
            lab = new JLabel(lHGrossTotals.toString());
            pan1.add(lab);
            final String lHAdvAdvString;
            if(lHTotalDays == Currency.Zero){
                lHAdvAdvString = "N/A";
            }else{
                lHAdvAdvString = lHAdvAdv.divide(lHTotalDays).format3();
            }
            lab = new JLabel(lHAdvAdvString);
            pan1.add(lab);
            lab = new JLabel("Second Half");
            pan1.add(lab);

            mainPan2.add(pan1);

            pan1 = new JPanel();
            pan1.setLayout(new GridLayout(1, 4));
            pan1.setBackground(Color.YELLOW);

            lab = new JLabel("Month Total");
            pan1.add(lab);
            lab = new JLabel(grossTotals.toString());
            pan1.add(lab);
            final String advAdvString;
            if(totalDays == Currency.Zero){
                advAdvString = "N/A";
            }else{
                advAdvString = advAdv.divide(totalDays).format3();
            }
            lab = new JLabel(advAdvString);
            pan1.add(lab);
            lab = new JLabel("Month Total");
            pan1.add(lab);

            mainPan2.add(pan1);

            monthT.add(pan, date.getMonthS());
            monthT.setSelectedIndex(lastUsedC);

            pan.add(mainPan);
        }
        mainSortGP.removeAll();
        mainSortGP.setLayout(new BoxLayout(mainSortGP, BoxLayout.PAGE_AXIS));
        monthT.add(mainSortGP, "Sort");

        JPanel pan = new JPanel();
        pan.setLayout(new GridLayout(2, 0));
        mainSortGP.add(pan);

        JLabel lab = new JLabel("Equal to or Less");
        pan.add(lab);
        lab = new JLabel("Equal to or Greater");
        pan.add(lab);
        pan.add(new JLabel(" "));
        pan.add(sortLessT = new JTextField("99999"));
        pan.add(sortGreaterT = new JTextField("0"));

        JButton jB = new JButton("Sort");
        jB.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                List<DayG> daysS = SearchG.sortD(cG.getDayGs(), 0,
                        new Currency(sortLessT.getText()));
                daysS = SearchG.sortD(daysS, 1,
                        new Currency(sortGreaterT.getText()));
                sortGP.removeAll();
                sortGP.setLayout(new GridLayout(0, 2));

                JPanel pan1 = new JPanel();
                pan1.setLayout(new GridLayout(0, 3));
                sortGP.add(pan1);

                JLabel lab = new JLabel("Date");
                pan1.add(lab);
                lab = new JLabel("Gross");
                pan1.add(lab);
                //  lab = new JLabel("7 day Adv.");
                //pan1.add(lab);
                lab = new JLabel("Notes");
                pan1.add(lab);

                JPanel pan2 = new JPanel();
                pan2.setLayout(new GridLayout(0, 3));
                sortGP.add(pan2);

                lab = new JLabel("Date");
                pan2.add(lab);
                lab = new JLabel("Gross");
                pan2.add(lab);
                lab = new JLabel("Notes");
                pan2.add(lab);

                int count = 1;
                for (int i = 0; i < daysS.size(); i++) {
                    DayG day = (DayG) daysS.get(i);

                    if (count < 15) {
                        lab = new JLabel(day.toStringCal());
                        pan1.add(lab);
                        lab = new JLabel(day.getGross().toString());
                        pan1.add(lab);
                        //lab = new JLabel(numberFormatter.format(week.getFigure(0)));
                        //pan1.add(lab);
                        lab = new JLabel(day.getNote());
                        pan1.add(lab);
                    }
                    if (count >= 15 && count < 32) {
                        lab = new JLabel(day.toStringCal());
                        pan2.add(lab);
                        lab = new JLabel(day.getGross().toString());
                        pan2.add(lab);
                        //   lab = new JLabel(numberFormatter.format(week.getFigure(0)));
                        // pan2.add(lab);
                        lab = new JLabel(day.getNote());
                        pan2.add(lab);
                    }
                    count++;
                }
                mainSortGP.add(sortGP);
                //mainSortGP.repaint();
                frame.repaint();
            }
        });
        pan.add(jB);

        //mainPan.add(sortGP);


        grossDayP.add(monthT);
    }

    /**
     * A method that displays our employees on the employeeP
     */
    private void displayEmployees() {

        final List<Employee> employees = hR.getEmployees();
        cEmployeeT = new JTabbedPane();
        pEmployeeT = new JTabbedPane();
        employeePReset();

        for (int i = 0; i < employees.size(); i++) {
            Employee currentEmp = (Employee) employees.get(i);

            if (currentEmp.getIsCurrentEmp()) {
                cEmployeeT.add(currentEmp.getName(),
                        payrollLayout(currentEmp));
            } else {
                pEmployeeT.add(currentEmp.getName(),
                        payrollLayout(currentEmp));
            }
        }

        cEmployeeP.add(cEmployeeT);
        pEmployeeP.add(pEmployeeT);
        
        displayTotals(employees);
        try {
            displayTaxes();
        } catch (DataException e) {
            GUI.showFatalMessageDialog(e);
        }
    }

    /**
     *This method displays everything. It should be called as displayEmployees
     *once was.
     *
     *@param whatFirst, 0 for employess & taxes, 1 for days, 2 for bank (we
     *                  don't use this yet, but it's reserved).
     */
    private void displayEverything(int whatFirst) {
        long s = Calendar.getInstance().getTimeInMillis();
        String name = "";

        if (!displayEmpFirstTime) {
            JPanel selectedP = (JPanel) tabbedP.getSelectedComponent();

            if (selectedP.getComponent(0) instanceof JTabbedPane) {
                JTabbedPane selectedT = (JTabbedPane) selectedP.getComponent(0);
                int i = selectedT.getSelectedIndex();
                if (i > 0) {
                    name = selectedT.getTitleAt(i);
                } else {
                    displayEmpFirstTime = false;
                }
            } else {
                displayEmpFirstTime = false;
            }
        }
        
        //switch and if statments work together, don't change one with out
        //looking at the other...
        switch (whatFirst) {
            case 0:
                displayEmployees();
                break;
            case 1:
                try{
                    displayDays();
                }catch(Exception e){
                    GUI.showFatalMessageDialog(e);
                    //stop working:
                    return;
                }
                break;
            case 2:
                displayBank();
                break;
        }

        if (whatFirst != 0) {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    displayEmployees();
                }
            });
        }
        if (whatFirst != 1) {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    try{
                        displayDays();
                    }catch(Exception e){
                        GUI.showFatalMessageDialog(e);
                    }
                }
            });
        }
        if (whatFirst != 2) {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    displayBank();
                }
            });
        }

        frame.setSize(frameSize);
        frame.setVisible(true);

        if (!displayEmpFirstTime) {
            int cTabC = cEmployeeT.getTabCount();
            int pTabC = pEmployeeT.getTabCount();
            int mTabC = monthT.getTabCount();

            for (int i = 0; i < cTabC; i++) {
                if (cEmployeeT.getTitleAt(i).equals(name)) {
                    cEmployeeT.setSelectedIndex(i);
                }
            }

            for (int i = 0; i < pTabC; i++) {
                if (pEmployeeT.getTitleAt(i).equals(name)) {
                    pEmployeeT.setSelectedIndex(i);
                }
            }

            for (int i = 0; i < mTabC; i++) {
                if (monthT.getTitleAt(i).equals(name)) {
                    monthT.setSelectedIndex(i);
                }
            }
        }
        displayEmpFirstTime = false;

        final CompanyDBWrap co;
        try {
            co = CompanyDBWrap.getNewInstance();
        } catch (DataException de) {
            GUI.showFatalMessageDialog(de);
            //we should quit now:
            return;
        }

        frame.setTitle("Company:   " + co.getName() + " ---- " + co.getLocation()
                + " ---- " + co.getYear());

        long e = Calendar.getInstance().getTimeInMillis();
        MiscStuff.writeToLog("" + (e - s) + " displayEverything");

    }

    /**
     * A method that displays our taxes.
     */
    private void displayTaxes() throws DataException {
        tH = TaxHandler.getNewInstance(hR.getEmployees(), cG);
        boolean col = false;

        if (taxP.getComponentCount() != 0) {
            taxP.removeAll();
        }
        JPanel pan = new JPanel();
        //pan.setBackground(Color.WHITE);
        pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
        JPanel pan1 = new JPanel();
        
        final int numberOfColumns;
        if(OrWithEng.orWithEnabled()){
            numberOfColumns = 11;
        }else{
            numberOfColumns = 10;
        }
        pan1.setLayout(new GridLayout(1, numberOfColumns));
        int c = SwingConstants.LEFT;
        JLabel lab;

        lab = new JLabel("Date", c);
        pan1.add(lab);
        
        lab = new JLabel("Sales Tax", c);
        pan1.add(lab);
        
        lab = new JLabel("SS Bus", c);
        pan1.add(lab);
        
        lab = new JLabel("Med Bus", c);
        pan1.add(lab);
        
        lab = new JLabel("State Unem", c);
        pan1.add(lab);
        if(OrWithEng.orWithEnabled()){
            lab = new JLabel("State W/H", c);
            pan1.add(lab);
        }
        lab = new JLabel("Workers Ben", c);
        pan1.add(lab);
//        lab = new JLabel("Combined P-Tax", c);
//        pan1.add(lab);
        lab = new JLabel("941", c);
        pan1.add(lab);
        lab = new JLabel("SS Emp", c);
        pan1.add(lab);
        lab = new JLabel("Med Emp", c);
        pan1.add(lab);
        lab = new JLabel("Fica Emp", c);
        pan1.add(lab);

        pan.add(pan1);

        for (int i = 0; i < 12; i++) {
            PTaxes pT = tH.getMonth(i);

            
            
            pan1 = new JPanel();
            // pan1.setBackground(Color.WHITE);
            pan1.setLayout(new GridLayout(1, numberOfColumns));
            if (col) {
                pan1.setBackground(Color.LIGHT_GRAY);
            }
            if (pT != null) {
                lab = new JLabel("Month " + (i + 1));
                pan1.add(lab);
                    
                lab = new JLabel(pT.getSalesTax().format3());
                pan1.add(lab);
                
                lab = new JLabel(pT.getSsFromBusiness().format3());
                pan1.add(lab);
                
                lab = new JLabel(pT.getMedFromBusiness().format3());
                pan1.add(lab);
                
                lab = new JLabel(pT.getUnemployment().format3());
                pan1.add(lab);
                if(OrWithEng.orWithEnabled()){
                    lab = new JLabel(pT.getOrWith().format3());
                    pan1.add(lab);
                }
                lab = new JLabel(pT.getBenefit().format3());
                pan1.add(lab);
//                lab = new JLabel(
//                        "?");
//                        //pT.getUnemployment().add(pT.getOrWith()).add(pT.getBenefit()).toString());
//                pan1.add(lab);
                lab = new JLabel(pT.get941().format3());
                pan1.add(lab);
                lab = new JLabel(pT.getSs().format3());
                pan1.add(lab);
                lab = new JLabel(pT.getMed().format3());
                pan1.add(lab);
                lab = new JLabel(pT.getFica().format3());
                pan1.add(lab);

                pan.add(pan1);

                if (col == false) {
                    col = true;
                } else {
                    col = false;
                }
            }
            if ((i + 1) % 3 == 0) {
                pan1 = new JPanel();
                pan1.setLayout(new GridLayout(1, numberOfColumns));
                pan1.setBackground(Color.YELLOW);
                
                PTaxes taxes = tH.getMonth(i - 2).add(tH.getMonth(i - 1)).add(tH.getMonth(i));

                lab = new JLabel("Quarter " + ((i + 1) / 3));
                pan1.add(lab);
                lab = new JLabel(
                        taxes.getSalesTax().format3());
                pan1.add(lab);
                
                lab = new JLabel(
                        taxes.getSsFromBusiness().format3());
                pan1.add(lab);
                
                lab = new JLabel(
                        taxes.getMedFromBusiness().format3());
                pan1.add(lab);
                
                lab = new JLabel(
                        taxes.getUnemployment().format3());
                pan1.add(lab);
                if(OrWithEng.orWithEnabled()){
                    lab = new JLabel(
                            taxes.getOrWith().format3());
                    pan1.add(lab);
                }
                lab = new JLabel(
                        taxes.getBenefit().format3());
                pan1.add(lab);
                
                lab = new JLabel(taxes.get941().format3());
                pan1.add(lab);
                
                lab = new JLabel(taxes.getSs().format3());
                pan1.add(lab);
                lab = new JLabel(taxes.getMed().format3());
                pan1.add(lab);
                lab = new JLabel(taxes.getFica().format3());
                pan1.add(lab);

                pan.add(pan1);
            }
        }

        taxP.add(pan);
    }

    /**
     * A method that sets up our totals panel...
     *
     * @param employees, our employees...
     */
    private void displayTotals(final List<Employee> employees) {

        JPanel pan = new JPanel();
        pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
        JPanel pan1 = new JPanel();
        final int numberOfColumns;
        if(OrWithEng.orWithEnabled()){
            numberOfColumns = 9;
        }else{
            numberOfColumns = 8;
        }
        pan1.setLayout(new GridLayout(1, numberOfColumns));
        int CENTER = SwingConstants.CENTER;
        int RIGHT = SwingConstants.RIGHT;

        JLabel lab = new JLabel("Date", CENTER);
        pan1.add(lab);
        lab = new JLabel("Hours", SwingConstants.CENTER);
        pan1.add(lab);
        lab = new JLabel("Gross Pay", SwingConstants.CENTER);
        pan1.add(lab);
        lab = new JLabel("SS", SwingConstants.CENTER);
        pan1.add(lab);
        lab = new JLabel("   Medicare   ", SwingConstants.CENTER);
        pan1.add(lab);
        lab = new JLabel("FICA", SwingConstants.CENTER);
        pan1.add(lab);
        if(OrWithEng.orWithEnabled()){
            lab = new JLabel("OR W/H", SwingConstants.CENTER);
            pan1.add(lab);
        }
        
        pan1.add(new JLabel("Adjustment", SwingConstants.CENTER));
        
        lab = new JLabel("Net Pay", SwingConstants.CENTER);
        pan1.add(lab);
        pan.add(pan1);

        Currency hoursT = Currency.Zero;
        Currency hoursQ = Currency.Zero;
        Currency grossPayT = Currency.Zero;
        Currency grossPayQ = Currency.Zero;
        Currency sST = Currency.Zero;
        Currency sSQ = Currency.Zero;
        Currency medT = Currency.Zero;
        Currency medQ = Currency.Zero;
        Currency ficaT = Currency.Zero;
        Currency ficaQ = Currency.Zero;
        Currency orWithT = Currency.Zero;
        Currency orWithQ = Currency.Zero;
        Currency netPayT = Currency.Zero;
        Currency netPayQ = Currency.Zero;
        Currency adjustmentsQ = Currency.Zero;
        Currency adjustmentsT = Currency.Zero;

        boolean col = false;
        //boolean fourthQ = false;

        final List<PayrollStats> sort = TaxHandler.sortAndSumPayrollStatsByMonth(employees);
        
        for (final PayrollStats payrollStats : sort){
            final PayDates pDate = payrollStats.getDate();
            final int year = pDate.getYear();
            final String dateS = pDate.toString();

            pan1 = new JPanel();
            pan1.setLayout(new GridLayout(1, numberOfColumns));

            if (col) {
                pan1.setBackground(Color.LIGHT_GRAY);
            }

            lab = new JLabel(dateS, RIGHT);
            pan1.add(lab);

            Currency num = payrollStats.getHours();//(Currency) dateSet.get(1);
            lab = new JLabel(num.toString(), RIGHT);
            hoursT = hoursT.add(num);
            hoursQ = hoursQ.add(num);
            pan1.add(lab);

            num = payrollStats.getGrossPay();//(Currency) dateSet.get(2);//Math2.round((Double) dateSet.get(2));
            lab = new JLabel(num.toString(), RIGHT);
            grossPayT = grossPayT.add(num);// += num;
            grossPayQ = grossPayQ.add(num);// += num;
            pan1.add(lab);

            num = payrollStats.getSs();//(Currency) dateSet.get(3);
            lab = new JLabel(num.toString(), RIGHT);
            sST = sST.add(num);
            sSQ = sSQ.add(num);
            pan1.add(lab);

            num = payrollStats.getMed();//(Currency) dateSet.get(4);
            lab = new JLabel(num.toString(), RIGHT);
            medT = medT.add(num);
            medQ = medQ.add(num);
            pan1.add(lab);

            num = payrollStats.getFica();//(Currency) dateSet.get(FICA_IDX);
            lab = new JLabel(num.toString(), RIGHT);
            ficaT = ficaT.add(num);//+= num;
            ficaQ = ficaQ.add(num);//+= num;
            pan1.add(lab);

            num = payrollStats.getOrWith();//(Currency) dateSet.get(OR_WITH_IDX);//Math2.round((Double) dateSet.get(6));
            orWithT = orWithT.add(num);//+= num;
            orWithQ = orWithQ.add(num);// += num;
                
            if(OrWithEng.orWithEnabled()){
                lab = new JLabel(num.toString(), RIGHT);
                pan1.add(lab);
            }else{
                //could throw an exception, but something may happen when we toggle between 
                //on and off
                if(Currency.Zero.ne(num)){
                    MiscStuff.writeToLog("the or withholding isn't 0 but it should be as it's disabled!");
                }
            }
            
            num = payrollStats.getAdjustment();
            adjustmentsT = adjustmentsT.add(num);
            adjustmentsQ = adjustmentsQ.add(num);
            pan1.add(new JLabel(num.toString(), RIGHT));

            num = payrollStats.getNetPay();//(Currency) dateSet.get(7);//Math2.round((Double) dateSet.get(7));
            lab = new JLabel(num.toString(), RIGHT);
            netPayT = netPayT.add(num);//+= num;
            netPayQ = netPayQ.add(num);// += num;
            pan1.add(lab);

            pan.add(pan1);

            //show quarters
            if (dateS.equals(new PayDates(3, 2, year).toString())
                    || dateS.equals(new PayDates(6, 2, year).toString())
                    || dateS.equals(new PayDates(9, 2, year).toString())
                    || dateS.equals(new PayDates(12, 2, year).toString())) {
                //last quarter at end.
                String quart = "";

                if (dateS.equals(new PayDates(3, 2, year).toString())) {
                    quart = "1st";
                }
                if (dateS.equals(new PayDates(6, 2, year).toString())) {
                    quart = "2nd";
                }
                if (dateS.equals(new PayDates(9, 2, year).toString())) {
                    quart = "3rd";
                }
                if (dateS.equals(new PayDates(12, 2, year).toString())) {
                    quart = "4th";
                }
                pan1 = new JPanel();
                pan1.setBackground(Color.yellow);
                pan1.setLayout(new GridLayout(1, numberOfColumns));

                lab = new JLabel(quart + " Quarter", RIGHT);
                pan1.add(lab);
                lab = new JLabel(hoursQ.toString(), RIGHT);
                pan1.add(lab);
                lab = new JLabel(grossPayQ.toString(), RIGHT);
                pan1.add(lab);
                lab = new JLabel(sSQ.toString(), RIGHT);
                pan1.add(lab);
                lab = new JLabel(medQ.toString(), RIGHT);
                pan1.add(lab);
                lab = new JLabel(ficaQ.toString(), RIGHT);
                pan1.add(lab);
                if(OrWithEng.orWithEnabled()){
                    lab = new JLabel(orWithQ.toString(), RIGHT);
                    pan1.add(lab);
                }
                
                pan1.add(new JLabel(adjustmentsQ.toString(), RIGHT));
                
                lab = new JLabel(netPayQ.toString(), RIGHT);
                pan1.add(lab);

                pan.add(pan1);

                hoursQ = Currency.Zero;
                grossPayQ = Currency.Zero;
                sSQ = Currency.Zero;
                medQ = Currency.Zero;
                ficaQ = Currency.Zero;
                orWithQ = Currency.Zero;
                netPayQ = Currency.Zero;
            }

            if (col == false) {
                col = true;
            } else {
                col = false;
            }
        }

        //totals
        pan1 = new JPanel();
        pan1.setBackground(Color.GREEN);
        pan1.setLayout(new GridLayout(1, numberOfColumns));

        lab = new JLabel("To Date", RIGHT);
        pan1.add(lab);
        lab = new JLabel(hoursT.toString(), RIGHT);
        pan1.add(lab);
        lab = new JLabel(grossPayT.toString(), RIGHT);
        pan1.add(lab);
        lab = new JLabel(sST.toString(), RIGHT);
        pan1.add(lab);
        lab = new JLabel(medT.toString(), RIGHT);
        pan1.add(lab);
        lab = new JLabel(ficaT.toString(), RIGHT);
        pan1.add(lab);
        if(OrWithEng.orWithEnabled()){
            lab = new JLabel(orWithT.toString(), RIGHT);
            pan1.add(lab);
        }
        
        pan1.add(new JLabel(adjustmentsT.toString(), RIGHT));
        
        lab = new JLabel(netPayT.toString(), RIGHT);
        pan1.add(lab);

        pan.add(pan1);

        totalPayrollP.removeAll();
        totalPayrollP.add(pan);

    }

    /**
     * A method that edits our day data displayed by our cG.
     */
    private void editDayGs() {
        JPanel selectedP = (JPanel) tabbedP.getSelectedComponent();
        JTabbedPane selectedT = (JTabbedPane) selectedP.getComponent(0);
        int i = selectedT.getSelectedIndex();
        String month = selectedT.getTitleAt(i);

        Object[] possibleValues = {"Add Day", "Replace Day",
            "Delete Day"
        };
        Object selectedValue = JOptionPane.showInputDialog(null,
                "Choose one", "Input",
                JOptionPane.INFORMATION_MESSAGE, null,
                possibleValues, possibleValues[0]);

        if (selectedValue == null) {
            return;
        }
        if (selectedValue.equals("Add Day")) {
            ePDAddDay(month);
        }
        if (selectedValue.equals("Replace Day")) {
            ePDEditDay(month);
        }
        if (selectedValue.equals("Delete Day")) {
            ePDDeleteDay(month);
        }
    }

    /**
     * A method that edits info on our current paysheet...
     */
    private void editPaysheet() {
        JPanel selectedP = (JPanel) tabbedP.getSelectedComponent();
        JTabbedPane selectedT = (JTabbedPane) selectedP.getComponent(0);
        int i = selectedT.getSelectedIndex();
        String name = selectedT.getTitleAt(i);
        Employee emp = hR.getEmployee(name);

        Object[] possibleValues = {"Add Pay Period", "Edit Pay Period",
            "Delete Pay Period", "Add Pay Check", "Edit Employee Info",
            "Delete Employee"
        };
        Object selectedValue = JOptionPane.showInputDialog(null,
                "Choose one", "Input",
                JOptionPane.INFORMATION_MESSAGE, null,
                possibleValues, possibleValues[0]);

        if (selectedValue == null) {
            return;
        }
        if (selectedValue.equals("Add Pay Period")) {
            ePAddPayPeriod(emp, false);
        }
        if (selectedValue.equals("Edit Pay Period")) {
            ePEditPayPeriod(emp);
        }
        if (selectedValue.equals("Delete Pay Period")) {
            ePDeletePayPeriod(emp);
        }
        if (selectedValue.equals("Add Pay Check")) {
            ePAddPayCheck(emp);
        }
        if (selectedValue.equals("Edit Employee Info")) {
            ePEditEmployeeInfo(emp);
        }
        if (selectedValue.equals("Delete Employee")) {
            ePDeleteEmployee(emp);
        }
    }

    /**
     * A method that creates an info panel and returns it for employees.
     *
     * @param emp, if this is null we will not populate the specific info of a
     *             given employee.
     * @param bPanel, our button panel.
     *
     * @return employeeIP, our panel.
     */
    private JPanel employeeIP(Employee emp, JPanel bPanel) {
        boolean isEdit = false;

        if (emp != null) {
            isEdit = true;
            currentEmp = emp;
        }

        int c = SwingConstants.CENTER;
        JPanel pan = new JPanel();
        pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
        JPanel pan1 = new JPanel();
        pan1.setLayout(new GridLayout(1, 6));

        JLabel lab = new JLabel("Name", c);
        pan1.add(lab);
        lab = new JLabel("SS", c);
        pan1.add(lab);
        lab = new JLabel("       Address       ", c);
        pan1.add(lab);
        lab = new JLabel("Claim", c);
        pan1.add(lab);
        lab = new JLabel("Pay Rate", c);
        pan1.add(lab);
        lab = new JLabel("Current Employee", c);
        pan1.add(lab);
        pan.add(pan1);

        if (!isEdit) {
            pan1 = new JPanel();
            pan1.setLayout(new GridLayout(1, 6));
            nEmpName = new JTextField();
            pan1.add(nEmpName);
            nEmpSS = new JTextField();
            pan1.add(nEmpSS);
            nEmpAddress = new JTextField();
            pan1.add(nEmpAddress);
            nEmpClaim = new JTextField();
            pan1.add(nEmpClaim);
            nEmpRate = new JTextField();
            pan1.add(nEmpRate);
            String[] yN = {"Yes", "No"};
            yesNo = new JComboBox(yN);
            pan1.add(yesNo);
            pan.add(pan1);
        } else {
            pan1 = new JPanel();
            pan1.setLayout(new GridLayout(1, 6));
            nEmpName = new JTextField(emp.getName(), 10);
            pan1.add(nEmpName);
            nEmpSS = new JTextField(emp.getSocS(), 10);
            pan1.add(nEmpSS);
            nEmpAddress = new JTextField(emp.getAddress(), 10);
            pan1.add(nEmpAddress);
            nEmpClaim = new JTextField("" + emp.getClaim(), 10);
            pan1.add(nEmpClaim);
            nEmpRate = new JTextField("" + emp.getCurrentRate(), 10);
            pan1.add(nEmpRate);
            String[] yN = {"Yes", "No"};
            yesNo = new JComboBox(yN);

            if (!emp.getIsCurrentEmp()) {
                yesNo.setSelectedIndex(1); // No
            }
            pan1.add(yesNo);
            pan.add(pan1);
        }
        pan.add(bPanel);
        JPanel wPan = new JPanel();
        wPan.add(new JLabel("If married, the claim should "
                + "be added to ten. 1 + 10 = 11 for a married person "
                + "claiming 1. Also, -1 is used if EXEMPT"));
        pan.add(wPan);


        return pan;
    }

    /**
     * A method that resets our employeeP
     */
    private void employeePReset() {

        if (cEmployeeP != null) {
            cEmployeeP.removeAll();
        }
        if (pEmployeeP != null) {
            pEmployeeP.removeAll();
        }
    }

    /**
     * A method that makes a pay check based on the last pay period of an
     * employee passed in.
     *
     * @param emp, the employee.
     */
    private void ePAddPayCheck(Employee emp) {
        int numI = bH.getCurrentCheckBook().nextNumber();
        Calendar now = Calendar.getInstance();
        String name = emp.getName();

        final List<PayPeriod> pPs = emp.getPayPeriods();
        //Calendar date = null;
        Currency amount = new Currency("0");
        for (int i = 0; i < pPs.size(); i++) {
            PayPeriod pP = (PayPeriod) pPs.get(i);
            amount = pP.getNetPay(); //assumes the last pay period given will
            // be the last pay period....
            //if(date != null) {
            //  Calendar date2 = pP.getDate();
            //if(date.before(date2)) {
            //  date = pP.getDate();
            //amount = pP.getNetPay();
//                }
            //          }
            //        else {
            //          date = pP.getDate();
            //        amount = pP.getNetPay();
            //  }
        }

        GlCode gL = new GlCode(new Code(5)); //sorry to hardcode...................
        Check nCheck = new Check(numI, now, name, amount, gL);

        BHTPane bHTP = (BHTPane) bankP.getComponent(0);



        bHTP.check(2, nCheck);
    }

    /**
     * A method that sets up and adds a pay period to an employee.
     *
     * @param emp, the employee to add to.
     */
    private void ePAddPayPeriod(Employee emp, boolean newDate) {
        currentEmp = emp;
        addPayP = new JFrame("Add Pay Period");
        Container cP = addPayP.getContentPane();

        JPanel pan1 = new JPanel();
        
        payPeriodPanel = new PayPeriodPanel(true, newDate, pan1, emp, null, addPayP);

        JButton add = new JButton("Add Pay Period");
        final ReturningActionListener al = new ReturningActionListener() {

            @Override
            public Object actionPerformedAndReturn(ActionEvent e) {
                try{
                    final PayPeriod payP = addPayPeriod(currentEmp, globalUseDateD, payPeriodPanel);

                    globalUseDateD = false;
                    displayEverything(0);
                    addPayP.setVisible(false);
                    addPayP = null;

                    return payP;
                }catch(Exception ex){
                    GUI.showFatalMessageDialog(ex);
                }
                
                //should never get here:
                return null;
            }
        };
        IntegrationTestHelper.putActionListener(IntegrationTestHelper.AlKeys.addPayPeriodAL, al);

        add.addActionListener(al);
        pan1.add(add);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                addPayP.setVisible(false);
                addPayP = null;
            }
        });
        pan1.add(cancel);

        JLabel lab = new JLabel("         ");
        pan1.add(lab);

        JButton changeD = new JButton("Change Date");
        changeD.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                addPayP.setVisible(false);
                globalUseDateD = true;
                addPayP = null;
                ePAddPayPeriod(currentEmp, true);
            }
        });
        pan1.add(changeD);
        
        cP.add(payPeriodPanel);
        
        addPayP.pack();
        addPayP.setVisible(true);
        payPeriodPanel.nowShowing();
    }

    /**
     * A method that adds a dayG.
     *
     * @param month, the selected month.
     */
    private void ePDAddDay(String month) {
        addDayF = new JFrame("Add Day");
        Container cP = addDayF.getContentPane();

        JPanel pan1 = new JPanel();

        ActionListener aC = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(new Integer(dayGPanel.getContent(2)),
                        new Integer(dayGPanel.getContent(1)),
                        new Integer(dayGPanel.getContent(0)));
                DayG newDayG = new DayG(newDate,
                        new Currency(dayGPanel.getContent(3)));

                if (!dayGPanel.getContent(4).equals("")) {
                    newDayG.setNote(dayGPanel.getContent(4));
                }

                cG.addDayG(newDayG);

                displayEverything(1);
                addDayF.setVisible(false);
                addDayF = null;
            }
        };

        JButton add = new JButton("Add Day");
        add.addActionListener(aC);//new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
        //                  Calendar newDate = Calendar.getInstance();
        //                newDate.set(new Integer(dayGPanel.getContent(2)),
        //                  new Integer(dayGPanel.getContent(1)),
        //                new Integer(dayGPanel.getContent(0)));
        //          DayG newDayG = new DayG(newDate,
        //            new Double(dayGPanel.getContent(3)));

        //      if(!dayGPanel.getContent(4).equals("")) {
        //        newDayG.setNote(dayGPanel.getContent(4));
        //  }

//                    cG.addDayG(newDayG);

        //                  displayEmployees();
        //                addDayF.setVisible(false);
        //              addDayF = null;}
        //        });
        pan1.add(add);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                addDayF.setVisible(false);
                addDayF = null;
            }
        });
        pan1.add(cancel);

        dayGPanel = new DayGPanel(pan1, month, cG.getDayGs());
        dayGPanel.addComponentActionListener(3, aC);
        cP.add(dayGPanel);
        addDayF.pack();
        addDayF.setVisible(true);
        dayGPanel.setFocus(3);
    }

    /**
     * A method that deletes a dayG.
     *
     * @param month, the selected month.
     */
    private void ePDDeleteDay(String month) {
        addDayF = new JFrame("Delete Day");
        Container cP = addDayF.getContentPane();

        JPanel pan1 = new JPanel();

        JLabel lab = new JLabel("Just enter the date you want to kill.    ");
        pan1.add(lab);

        JButton delete = new JButton("Delete Day");
        delete.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(new Integer(dayGPanel.getContent(2)),
                        new Integer(dayGPanel.getContent(1)),
                        new Integer(dayGPanel.getContent(0)));
                DayG newDayG = new DayG(newDate, new Currency(0));

                final List<DayG> dayGs = cG.getDayGs();
                for (int i = 0; i < dayGs.size(); i++) {
                    DayG dG = (DayG) dayGs.get(i);

                    if (dG.getDay() == newDayG.getDay()
                            && dG.getMonth() == newDayG.getMonth()
                            && dG.getYear() == newDayG.getYear()) {

                        dayGs.remove(dG);
                    }
                }

                displayEverything(1);
                addDayF.setVisible(false);
                addDayF = null;
            }
        });
        pan1.add(delete);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                addDayF.setVisible(false);
                addDayF = null;
            }
        });
        pan1.add(cancel);

        dayGPanel = new DayGPanel(pan1, month, cG.getDayGs());
        dayGPanel.setContent(3, "Not needed");
        dayGPanel.setContent(0, "");
        cP.add(dayGPanel);
        addDayF.pack();
        addDayF.setVisible(true);
        dayGPanel.setFocus(0);
    }

    /**
     * A method that replaces a dayG.
     *
     * @param month, the selected month.
     */
    private void ePDEditDay(String month) {
        addDayF = new JFrame("Replace Day");
        Container cP = addDayF.getContentPane();

        JPanel pan1 = new JPanel();

        JButton replace = new JButton("Replace");
        replace.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(new Integer(dayGPanel.getContent(2)),
                        new Integer(dayGPanel.getContent(1)),
                        new Integer(dayGPanel.getContent(0)));
                DayG newDayG = new DayG(newDate,
                        new Currency(dayGPanel.getContent(3)));

                if (!dayGPanel.getContent(4).equals("")) {
                    newDayG.setNote(dayGPanel.getContent(4));
                }

                cG.addDayG(newDayG);

                displayEverything(1);
                addDayF.setVisible(false);
                addDayF = null;
            }
        });
        pan1.add(replace);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                addDayF.setVisible(false);
                addDayF = null;
            }
        });
        pan1.add(cancel);

        dayGPanel = new DayGPanel(pan1, month, cG.getDayGs());
        dayGPanel.setContent(0, "");
        cP.add(dayGPanel);
        addDayF.pack();
        addDayF.setVisible(true);
        dayGPanel.setFocus(0);
    }

    /**
     * A method that deletes a given employee.
     *
     * @param emp, the employee to be deleted.
     */
    private void ePDeleteEmployee(Employee emp) {
        deleteEmp = new JFrame();
        Container cp = deleteEmp.getContentPane();

        JPanel pan1 = new JPanel();
        JButton delete = new JButton("DELETE this employee");
        delete.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                hR.removeEmp(currentEmp);

                deleteEmp.setVisible(false);
                deleteEmp = null;
                displayEverything(0);
            }
        });
        pan1.add(delete);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                deleteEmp.setVisible(false);
                deleteEmp = null;
                displayEverything(0);
            }
        });
        pan1.add(cancel);

        cp.add(employeeIP(emp, pan1));
        deleteEmp.pack();
        deleteEmp.setVisible(true);
    }

    /**
     * A method that deletes a pay period.
     *
     * @param emp, the employee to be deleted from.
     */
    private void ePDeletePayPeriod(Employee emp) {
        currentEmp = emp;
        deletePayP = new JFrame("Delete Pay Period");
        JPanel pan1 = new JPanel();

        JButton del = new JButton("Delete");
        del.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                displayEverything(0);
                deletePayP.setVisible(false);
                deletePayP = null;
            }
        });
        pan1.add(del);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                currentEmp.addPayPeriod(lastDeletedPayP);
                displayEverything(0);
                deletePayP.setVisible(false);
                deletePayP = null;
            }
        });
        pan1.add(cancel);

        Container cP = deletePayP.getContentPane();
        
        DateDialog dateD = new DateDialog(addPayP);
        cP.add(new PayPeriodPanel(false, false, pan1, emp, dateD.getNewPayDate(), addPayP));
        deletePayP.pack();
        deletePayP.setVisible(true);
    }

    /**
     * A method that edits our employee info.
     *
     * @param emp, our employee to edit.
     */
    private void ePEditEmployeeInfo(Employee emp) {
        editEmp = new JFrame();
        Container cp = editEmp.getContentPane();

        JPanel pan1 = new JPanel();
        JButton ok = new JButton("OK the Edit");
        ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                currentEmp.setName(nEmpName.getText());
                currentEmp.setSocS(nEmpSS.getText());
                currentEmp.setAddress(nEmpAddress.getText());
                currentEmp.setClaim(new Integer(nEmpClaim.getText()));
                currentEmp.setCurrentRate(new Currency(nEmpRate.getText()));

                if (((String) yesNo.getSelectedItem()).equals("No")) {
                    currentEmp.setIsCurrentEmp(false);
                } else {
                    currentEmp.setIsCurrentEmp(true);
                }
                editEmp.setVisible(false);
                editEmp = null;
                displayEverything(0);
            }
        });
        pan1.add(ok);

        cp.add(employeeIP(emp, pan1));
        editEmp.pack();
        editEmp.setVisible(true);
    }

    /**
     * A method that edits a PayPeriod.
     *
     * @param emp, the employee to be edited.
     */
    private void ePEditPayPeriod(Employee emp) {
        currentEmp = emp;
        editPayP = new JFrame("Edit Pay Period");
        JPanel pan1 = new JPanel();
        
        DateDialog dateD = new DateDialog(addPayP, emp.getPayPeriods());
        payPeriodPanel = new PayPeriodPanel(false, false, pan1, emp, dateD.getNewPayDate(), addPayP);

        JButton ok = new JButton("ok");
        ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                addPayPeriod(currentEmp, true, payPeriodPanel);
                displayEverything(0);
                editPayP.setVisible(false);
                editPayP = null;
            }
        });
        pan1.add(ok);

        final JLabel warning = new JLabel("Warning, this is a lame program, one"
                + " may only change the fica and Or W/H, or the claim.");
        pan1.add(warning);

        Container cP = editPayP.getContentPane();
        cP.add(payPeriodPanel);
        editPayP.pack();
        editPayP.setVisible(true);
    }

    /**
     * A method that makes our TheFile and returns it.
     *
     * @return tF, our tF file.
     */
    private TheFile4 getTF4() {
        tFile = new TheFile4();

        tFile.add(0, hR);
        tFile.add(1, cG);
        tFile.add(2, bH);

        return tFile;
    }

    /**
     * A method that gets our HumanResources.
     *
     * @return hR, our HumanResources.
     */
    public HumanResources getHR() {
        return hR;
    }

    private static boolean loadCompany(int coID, ControllerG cG, HumanResources hR, BankHealth bH) {
        try {
            return DatabaseHelper.LoadCompany(coID, cG, hR, bH);
        } catch (DataException de) {
            GUI.showFatalMessageDialog(de);
            //should never reach this:
            return false;
        }
    }

    /**
     *This method loads a file based on the path
     *
     *@param path, the path of the file to open.
     */
    public void loadFile()
            throws /*IOException, ClassNotFoundException,*/ DataException {

        MiscStuff.writeToLog("loadFile called");

//        if (lastSave) {
//            if (fH.getLastSavedFile() instanceof TheFile4) {
//                tFile = (TheFile4) fH.getLastSavedFile();
//                hR = (HumanResources) tFile.get(0);
//                cG = (ControllerG) tFile.get(1);
//                bH = (BankHealth) tFile.get(2);
//
//                backUpFile(fH.getLastSavedFileName());
//            }
//        } else {
//            
//            MiscStuff.writeToLog("loadFile !lastSave");
//            
//            Object ob = fH.readOb(path);
//            
//            MiscStuff.writeToLog("loadFile !lastSave after ob");
//            
//            if (ob instanceof TheFile4) {
//                
//                MiscStuff.writeToLog("loadFile !lastSave before tfile");
//                
//                tFile = (TheFile4) ob;
//
//                MiscStuff.writeToLog("loadFile !lastSave before hr");
//                
//                hR = (HumanResources) tFile.get(0);
//                
//                MiscStuff.writeToLog("loadFile !lastSave before cg");
//                
//                cG = (ControllerG) tFile.get(1);
//                
//                MiscStuff.writeToLog("loadFile !lastSave before bh");
//                
//                bH = (BankHealth) tFile.get(2);
//
//                MiscStuff.writeToLog("loadFile !lastSave before backup");
//                backUpFile(path);
//                MiscStuff.writeToLog("loadFile !lastSave after backup");
//            } else{
//                MiscStuff.writeToLog("loadFile !lastSave not instanceof TheFile4, path: " + path);
//            }
//        }
        //MiscStuff.writeToLog(bH.getGLCodes().values().toString());

        //database stuff

        InsertCompanyDialog cDialog = InsertCompanyDialog.getNewInsertCompanyDialog(frame, true);
        cDialog.setVisible(true);

        DatabaseHelper.InsertCompany(hR, cDialog.companyName, cDialog.location,
                cDialog.year, cG, bH);

        // end database stuff

    }

    /**
     * A method that sets up and returns a panel for an employee payroll...
     *
     * @param emp, our employee.
     *
     * @return pan, our panel...
     */
    private JPanel payrollLayout(Employee emp) {
        JPanel pan = new JPanel();
        pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
        int c = SwingConstants.CENTER;

        JPanel pan1 = new JPanel();
        pan1.setLayout(new GridLayout(1, 5));
        JLabel lab = new JLabel("");//over button
        pan1.add(lab);
        lab = new JLabel("Name", c);
        pan1.add(lab);
        lab = new JLabel("S.S. #", c);
        pan1.add(lab);
        lab = new JLabel("Address", c);
        pan1.add(lab);
        lab = new JLabel("Claim", c);
        pan1.add(lab);
        pan.add(pan1);

        pan1 = new JPanel();
        pan1.setLayout(new GridLayout(1, 5));
        JButton edit = new JButton("Edit");
        edit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                editPaysheet();
            }
        });

        pan1.add(edit);
        lab = new JLabel(emp.getName(), c);
        pan1.add(lab);
        lab = new JLabel(emp.getSocS(), c);
        pan1.add(lab);
        String address = emp.getAddress();
        address = address + "   ";

        if (address.length() > 15) {
            int blank = address.indexOf(' ');

            for (; blank < 15;) {
                blank = address.indexOf(' ', blank + 1);
            }
            String str1 = address.substring(0, blank);
            String str2 = address.substring(blank, address.length());

            JPanel panelLab = new JPanel();
            panelLab.setLayout(new BoxLayout(panelLab, BoxLayout.PAGE_AXIS));

            lab = new JLabel(str1, c);
            panelLab.add(lab);
            //if(!str2.equals(" ")) {
            lab = new JLabel(str2, c);
            panelLab.add(lab);
            //}

            pan1.add(panelLab);
        } else {
            lab = new JLabel(emp.getAddress(), c);
            pan1.add(lab);
        }
        int claims = emp.getClaim();

        if (claims == -1) {
            lab = new JLabel("Exempt", c);
        } else {
            lab = new JLabel("" + claims, c);
        }
        pan1.add(lab);
        pan.add(pan1);

        pan.add(payrollLayoutPayPeriod(emp));

        return pan;
    }

    /**
     * A method that sets up a frame with our employees pay period info.
     *
     * @param emp, our employee.
     *
     * @return pan, our panel...
     */
    private JPanel payrollLayoutPayPeriod(Employee emp) {
        final List<PayPeriod> payPeriods = emp.getPayPeriods();
        final JPanel pan = new JPanel();
        pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));

        JPanel pan1 = new JPanel();
        final int numberOfColumns;
        if(OrWithEng.orWithEnabled()){
            numberOfColumns = 10;
        }else{
            numberOfColumns = 9;
        }
        pan1.setLayout(new GridLayout(1, numberOfColumns));
        int c = SwingConstants.CENTER;
        int r = SwingConstants.RIGHT;

        JLabel lab = new JLabel("Date", c);
        pan1.add(lab);
        lab = new JLabel("Rate", SwingConstants.CENTER);
        pan1.add(lab);
        lab = new JLabel("Hours", SwingConstants.CENTER);
        pan1.add(lab);
        lab = new JLabel("Gross Pay", SwingConstants.CENTER);
        pan1.add(lab);
        lab = new JLabel("SS", SwingConstants.CENTER);
        pan1.add(lab);
        lab = new JLabel(" Medicare ", SwingConstants.CENTER);
        pan1.add(lab);
        lab = new JLabel("FICA", SwingConstants.CENTER);
        pan1.add(lab);
        if(OrWithEng.orWithEnabled()){
            lab = new JLabel("OR W/H", SwingConstants.CENTER);
            pan1.add(lab);
        }
        
        pan1.add(new JLabel("Adj.", SwingConstants.CENTER));
        
        lab = new JLabel("Net Pay", SwingConstants.CENTER);
        pan1.add(lab);
        pan.add(pan1);

        boolean col = false;
        Font font = new Font("2", Font.BOLD, 11);
        String spacer = "   ";

        for (int i = 0; i < payPeriods.size(); i++) {
            pan1 = new JPanel();
            pan1.setLayout(new GridLayout(1, numberOfColumns));
            PayPeriod pP = (PayPeriod) payPeriods.get(i);

            if (col) {
                pan1.setBackground(Color.LIGHT_GRAY);
            }
            
            String payDate = pP.getDate().toString();
            //payDate = payDate.replace(" 20", "<br/>20");
            //lab = new JLabel("<html><body>"+payDate+"</body></html>", c);
            lab = new JLabel(payDate + spacer, r);
            lab.setFont(font);
            pan1.add(lab);
            lab = new JLabel(pP.getRate().toString() + spacer, r);
            pan1.add(lab);
            lab = new JLabel(pP.getHours().toString() + spacer, r);
            pan1.add(lab);
            lab = new JLabel(pP.getGrossPay().toString() + spacer, r);
            pan1.add(lab);
            lab = new JLabel(pP.getSS().toString() + spacer, r);
            pan1.add(lab);
            lab = new JLabel(pP.getMed().toString() + spacer, r);
            pan1.add(lab);
            lab = new JLabel(pP.getFica().toString() + spacer, r);
            pan1.add(lab);
            if(OrWithEng.orWithEnabled()){
                lab = new JLabel(pP.getOrWith().toString() + spacer, r);
                pan1.add(lab);
            }
            
            pan1.add(new JLabel(pP.getAdjustmentsTotal().toString() + spacer, r));
            
            lab = new JLabel(pP.getNetPay().toString() + spacer, r);
            pan1.add(lab);

            pan.add(pan1);

            if (col == false) {
                col = true;
            } else {
                col = false;
            }

        }

        pan1 = new JPanel();
        pan1.setBackground(Color.yellow);
        pan1.setLayout(new GridLayout(1, numberOfColumns));

        lab = new JLabel("To Date", r);
        pan1.add(lab);
        lab = new JLabel("" + emp.getCurrentRate() + spacer, r);
        pan1.add(lab);
        lab = new JLabel(emp.getHoursT().toString() + spacer, r);
        pan1.add(lab);
        lab = new JLabel(emp.getGrossPayT().toString() + spacer, r);
        pan1.add(lab);
        lab = new JLabel(emp.getSST().toString() + spacer, r);
        pan1.add(lab);
        lab = new JLabel(emp.getMedT().toString() + spacer, r);
        pan1.add(lab);
        lab = new JLabel(emp.getFicaT().toString() + spacer, r);
        pan1.add(lab);
        if(OrWithEng.orWithEnabled()){
            lab = new JLabel(emp.getOrWithT().toString() + spacer, r);
            pan1.add(lab);
        }
        lab = new JLabel(emp.getAdjustmentsTotalT().toString() + spacer, r);
        pan1.add(lab);
        lab = new JLabel(emp.getNetPayT().toString() + spacer, r);
        pan1.add(lab);

        pan.add(pan1);

        return pan;
    }

    /**
     * A method that does our save as function.
     *
     * @return true if no exception was thrown during the operation,
     *         false otherwise.
     */
    public boolean saveAs() {

        JFileChooser fC = new JFileChooser();
        int returnVal = fC.showSaveDialog(frame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = fC.getSelectedFile().getAbsolutePath() + ".amf";
            try {
                saveFile(path);
                frame.setTitle(path);
            } catch (IOException ex) {
                MiscStuff.writeToLog("ioex");
                return false;
            }
        }
        return true;
    }

    /**
     * A method that does our save database function.
     *
     * @return true if all goes well,
     *         false otherwise.
     */
    public boolean saveDatabase() {


        //this will delete all the information about our company and reinsert it.
        //(specifically, the delete happens inside insertCompany
        
        
        final CompanyDBWrap co;
        try {
            co = CompanyDBWrap.getNewInstance();
        } catch (DataException de) {
            GUI.showFatalMessageDialog(de);
            //nothing to do:
            return false;
        }

        final boolean result;
        try {
            result = DatabaseHelper.InsertCompany(hR, co.getName(), co.getLocation(),
                    co.getYear(), cG, bH);

            setCompanyID(CompanyDBWrap.SelectCompanyID(co.getName(), co.getLocation(), co.getYear()));
        } catch (DataException de) {
            GUI.showFatalMessageDialog(de);
            //the fatal message dialog will kill the jvm
            //so this return should never execute
            return false;
        } catch (Exception e) {
            GUI.showFatalMessageDialog(e);
            //the fatal message dialog will kill the jvm
            //so this return should never execute
            return false;
        }

        return result;
    }

    /**
     *This mehtod saves our file given the file name.
     *
     *@param path, the path to save our file to.
     */
    private void saveFile(String path) throws IOException {
        fH.writeOb(path, getTF4());
    }
    
    /**
     * A method that sets up our frame.
     */
    private void setUpFrame() throws DataException {

        if(firstSetUpFrame){
            firstSetUpFrame = false;
            frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

            frame.addWindowListener(new WindowListener() {

                public void windowActivated(WindowEvent e) {
                }

                public void windowClosed(WindowEvent e) {
                }

                public void windowClosing(WindowEvent e) {
                    closeFrame();
                }

                public void windowDeactivated(WindowEvent e) {
                }

                public void windowDeiconified(WindowEvent e) {
                }

                public void windowIconified(WindowEvent e) {
                }

                public void windowOpened(WindowEvent e) {
                }
            });

            setUpMenu();
            contentP = frame.getContentPane();
            tabbedP = new JTabbedPane();
            //we add the tabbedP to contentP further down

            cEmployeeP = new JPanel();
            cEmployeeP.setLayout(new BoxLayout(cEmployeeP, BoxLayout.PAGE_AXIS));
            tabbedP.add(cEmployeeP, "Current Emp");

            pEmployeeP = new JPanel();
            pEmployeeP.setLayout(new BoxLayout(pEmployeeP, BoxLayout.PAGE_AXIS));
            tabbedP.add(pEmployeeP, "Past Emp");

            totalPayrollP = new JPanel();
            totalPayrollP.setLayout(new BoxLayout(totalPayrollP,
                    BoxLayout.PAGE_AXIS));
            tabbedP.add(totalPayrollP, "Total Payroll");

            taxP = new JPanel();
            taxP.setLayout(new BoxLayout(taxP, BoxLayout.PAGE_AXIS));
            tabbedP.add(taxP, "Taxes");

            grossDayP = new JPanel();
            grossDayP.setLayout(new BoxLayout(grossDayP, BoxLayout.PAGE_AXIS));
            tabbedP.add(grossDayP, "Days");

            bankP = new JPanel();
            bankP.setLayout(new BoxLayout(bankP, BoxLayout.PAGE_AXIS));
            tabbedP.add(bankP, "Bank");
        }
        
        //start w/ remove incase there's something left over from another workflow
        contentP.removeAll();
        contentP.add(tabbedP);
        frame.setSize(frameSize);
        frame.setVisible(true);
       
    }

    /**
     * A method that sets up our menu bar.
     */
    private void setUpMenu() throws DataException {
        menuB = new JMenuBar();
        JMenu file = new JMenu("File");

        JMenuItem switchCo = new JMenuItem("Switch w/o Save");
        switchCo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                final InsertCompanyDialog cDialog;
                try {
                    cDialog = InsertCompanyDialog.getNewInsertCompanyDialog(
                            frame, true, false, true);
                } catch (DataException de) {
                    GUI.showFatalMessageDialog(de);
                    return;
                }
                cDialog.setVisible(true);

                if (cDialog.hasValue) {

                    cG = new ControllerG();
                    hR = new HumanResources();
                    bH = new BankHealth();

                    final int coID;
                    try {
                        coID = CompanyDBWrap.SelectCompanyID(cDialog.companyName, cDialog.location, cDialog.year);
                    } catch (DataException ex) {
                        GUI.showFatalMessageDialog(ex);
                        return;
                    }

                    if (loadCompany(coID, cG, hR, bH)) {
                        setCompanyID(coID);
                    } else {
                        setCompanyID(-1);
                    }

                    displayEverything(0);
                }
            }
        });
        file.add(switchCo);
        if (CompanyDBWrap.getNumberOfCompanies() < 1) {
            switchCo.setEnabled(false);
        }

        JMenuItem rollBDB = new JMenuItem("Roll Back Database");
        rollBDB.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                RollBackDBDialog rDialog = new RollBackDBDialog(frame, true);
                rDialog.setVisible(true);

                if (rDialog.hasValue) {
                    //save the db before rollback
                    final InsertCompanyDialog cDialog;
                    try {
                        saveDatabase();
                        getCon().LoadDatabaseFromFile(rDialog.getReturnPath());
                        cDialog = InsertCompanyDialog.getNewInsertCompanyDialog(
                                frame, true, false, true);
                    } catch (DataException ex) {
                        GUI.showFatalMessageDialog(ex);
                        return;
                    }

//                    cG = new ControllerG();
//                    hR = new HumanResources();
//                    bH = new BankHealth();
//
//                    displayEverything(0);

//                    DatabaseHelper.LoadCompany(CompanyDBWrap.SelectCompanyID(
//                            cDialog.companyName, cDialog.location, cDialog.year),
//                            cG, hR, bH);


                    cDialog.setVisible(true);

                    if (cDialog.hasValue) {
                        cG = new ControllerG();
                        hR = new HumanResources();
                        bH = new BankHealth();

                        final int coID;
                        try {
                            coID = CompanyDBWrap.SelectCompanyID(cDialog.companyName, cDialog.location, cDialog.year);
                        } catch (DataException de) {
                            GUI.showFatalMessageDialog(de);
                            return;
                        }

                        if (loadCompany(coID, cG, hR, bH)) {
                            setCompanyID(coID);
                        } else {
                            setCompanyID(-1);
                        }

                        displayEverything(0);
                    }
                }
            }
        });
        file.add(rollBDB);


        //this uses the amp that is in the lib
        JMenuItem load = new JMenuItem("Load File");

        //AMP.MainDisplay.GUI oldGUI = new AMP.MainDisplay.GUI();
        load.addActionListener(new ActionListenerForLoadFile2());
        file.add(load);

        JMenuItem delete = new JMenuItem("Delete Company");
        delete.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                YesNoDialog yesNo = new YesNoDialog(frame, true, "Do you want to delete the current company you have open? (If you don't have one open, hit 'no'!, otherwise someone may die...)");

                if (yesNo.isReturnValue()) {
                    try {
                        final int lastUsedCompanyId = DatabaseHelper.GetLastUsedCompanyID();
                        DatabaseHelper.DeleteCompany(lastUsedCompanyId, bH);
                    } catch (DataException de) {
                        GUI.showFatalMessageDialog(de);
                        return;
                    }


                }


                //cG = new ControllerG();
                //hR = new HumanResources();
                //bH = new BankHealth();



                displayEverything(0);


            }
        });
        file.add(delete);

        JMenu newYear = new JMenu("New Year/Company");
        file.add(newYear);

        JMenuItem loadCE = new JMenuItem("Current Employees");
        createCompany =
                new ReturningActionListener() {

                    public Object actionPerformedAndReturn(ActionEvent e) {

                        
                        try {
                            lastInsertCompanyDialog = InsertCompanyDialog.getNewInsertCompanyDialog(
                                    frame, true, false, false);
                        } catch (DataException de) {
                            GUI.showFatalMessageDialog(de);
                            return null;
                        }
                        //InsertCompanyDialog cDialog = InsertCompanyDialog.getNewInsertCompanyDialog(frame, true, false, false);
                        lastInsertCompanyDialog.setVisible(true);

                        final List<Employee> empls = hR.getEmployees();
                        ArrayList toRemoveEmp = new ArrayList();

                        for (int i = 0; i < empls.size(); i++) {
                            Employee emp = (Employee) empls.get(i);
                            if (emp.getIsCurrentEmp() == false) {
                                toRemoveEmp.add(emp);
                            } else {
                                List<PayPeriod> pPs = emp.getPayPeriods();
                                pPs.clear();
                            }
                        }
                        empls.removeAll(toRemoveEmp);


                        cG = new ControllerG();
                        bH = new BankHealth();

                        if (lastInsertCompanyDialog.hasValue) {
                            try {
                                DatabaseHelper.InsertCompany(hR, 
                                        lastInsertCompanyDialog.companyName, 
                                        lastInsertCompanyDialog.location,
                                        lastInsertCompanyDialog.year, cG, bH);
                                setCompanyID(DatabaseHelper.GetLastUsedCompanyID());
                            } catch (DataException de) {
                                GUI.showFatalMessageDialog(de);
                            }
                        }

                        displayEverything(0);
                        return null;
                    }
                };
        IntegrationTestHelper.putActionListener(IntegrationTestHelper.AlKeys.newYearCurrentEmployee, createCompany);
        loadCE.addActionListener(createCompany);

        newYear.add(loadCE);

        JMenuItem save = new JMenuItem("Save");
        final ReturningActionListener saveAL =
                new ReturningActionListener() {

                    public Object actionPerformedAndReturn(ActionEvent e) {
//                try {
//                    if(fH.getLRFN() != null) {
//                        //saveFile(fH.getLRFN());
//                        saveFile(frame.getTitle());
//                        //fH.writeToLastFileName(getTF4());
//                        displayEverything(0);
//                    }
//                    else {
//                        saveAs();
//                    }
//                }
//                catch(Exception ex) {
//
//                }
                        saveDatabase();
                        return null;
                    }
                };
        IntegrationTestHelper.putActionListener(IntegrationTestHelper.AlKeys.saveAL, saveAL);
        save.addActionListener(saveAL);
        file.add(save);

//        JMenuItem saveAs = new JMenuItem("Save As...");
//        saveAs.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                saveAs();
//            }
//        });
//        file.add(saveAs);

        JMenu printM = new JMenu("Print Menu");
        file.add(printM);

        JMenuItem print = new JMenuItem("This Screen");
        print.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Container con = frame.getContentPane();
                con = (Container) con.getComponent(0);

                boolean done = false;
                while (!done) {
                    if (con instanceof JTabbedPane) {
                        JTabbedPane tP = (JTabbedPane) con;
                        con = (Container) tP.getSelectedComponent();

                        if (con.getComponentCount() == 1) {
                            con = (Container) con.getComponent(0);
                        }
                        //done = true;
                    } else {
                        //MiscStuff.writeToLog("" + con.getComponentCount());
                        done = true;
                    }
                }

                PrintUtilities.printComponent(con);
                displayEverything(0);
            }
        });
        printM.add(print);

        JMenuItem printCE = new JMenuItem("Current Employees");
        printCE.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int count = cEmployeeT.getTabCount();
                for (int i = 0; i < count; i++) {
                    JPanel empPP = (JPanel) cEmployeeT.getComponentAt(i);
                    PrintUtilities.printComponent(empPP, 1);
                }
                displayEverything(0);
            }
        });
        printM.add(printCE);

        JMenuItem printAE = new JMenuItem("All Employees");
        printAE.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int count = cEmployeeT.getTabCount();
                for (int i = 0; i < count; i++) {
                    JPanel empPP = (JPanel) cEmployeeT.getComponentAt(i);
                    PrintUtilities.printComponent(empPP, 1);
                }
                count = pEmployeeT.getTabCount();
                for (int i = 0; i < count; i++) {
                    JPanel empPP = (JPanel) pEmployeeT.getComponentAt(i);
                    PrintUtilities.printComponent(empPP, 1);
                }
                displayEverything(0);
                
            }
        });
        printM.add(printAE);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                closeFrame();
            }
        });
        file.add(exit);

        menuB.add(file);

        JMenu employee = new JMenu("Employee");

        JMenuItem addEmp = new JMenuItem("Add Employee");
        final ReturningActionListener addEmpAL = new ReturningActionListener() {

            public Object actionPerformedAndReturn(ActionEvent e) {
                addEmp();
                return null;
            }
        };
        IntegrationTestHelper.putActionListener(IntegrationTestHelper.AlKeys.addEmployee, addEmpAL);
        addEmp.addActionListener(addEmpAL);
        employee.add(addEmp);
        
        addMassPayrollOption(employee);
        
        menuB.add(employee);

        JMenu outGoing = new JMenu("Out-Going");

        JMenuItem envelopes = new JMenuItem("Print Envelope");
        envelopes.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                envelopeF = new EnvelopeF(0);
            }
        });
        outGoing.add(envelopes);
        menuB.add(outGoing);

        // <editor-fold defaultstate="collapsed" desc="Tools">
        JMenu tools = new JMenu("Tools");

        JMenuItem taxes = new JMenuItem("Tax Manager");
        
        final ReturningActionListener openTaxManagerAl = new ReturningActionListener() {
            @Override
            public Object actionPerformedAndReturn(ActionEvent e) {
                try {
                    TaxManager tMan = TaxManager.getNewInstance(getCompanyID());
                    tMan.setVisible(true);
                    tMan.addWindowListener(new WindowListener(){

                        @Override
                        public void windowOpened(WindowEvent e) {}
                        @Override
                        public void windowClosing(WindowEvent e) {}
                        @Override
                        public void windowClosed(WindowEvent e) {
                            //assume we need to recalculate payroll info:
                            hR.recalculatePayroll();
                            displayEverything(0);
                        }
                        @Override
                        public void windowIconified(WindowEvent e) {}
                        @Override
                        public void windowDeiconified(WindowEvent e) {}
                        @Override
                        public void windowActivated(WindowEvent e) {}
                        @Override
                        public void windowDeactivated(WindowEvent e) {}
                        
                    });
                } catch (DataException ex) {
                    GUI.showFatalMessageDialog(ex);
                }
                return null;
            }
        };
        IntegrationTestHelper.putActionListener(IntegrationTestHelper.AlKeys.openTaxManager, openTaxManagerAl);
        taxes.addActionListener(openTaxManagerAl);
        tools.add(taxes);

        menuB.add(tools);
        // </editor-fold>

        JMenu about = new JMenu("About");

        JMenuItem version = new JMenuItem("Version");
        version.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "    Version: " + GUI.Version, "Version",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        about.add(version);
        menuB.add(about);

        frame.setJMenuBar(menuB);
    }

    /**
     *this method tries to load data based on the database
     */
    private void tryDatabaseLoad() throws DataException {
        int lastUsedID = DatabaseHelper.GetLastUsedCompanyID();

        MiscStuff.writeToLog("in GUI.tryDatabaseLoad(), lastUsedID = " + lastUsedID);

        //reset if not already -1
        setCompanyID(-1);

        if (lastUsedID != -1) {
            if (DatabaseHelper.LoadCompany(lastUsedID, cG, hR, bH)) {
                setCompanyID(lastUsedID);
            } else {
                MiscStuff.writeToLog("in GUI.tryDatabaseLoad(), else hit");
            }
        }
        MiscStuff.writeToLog("in GUI, num of emp: " + hR.getEmployees().size());
        displayEverything(0);

    }

    /**
     * A method that tries to load the last saved file...
     */
    private void tryLastSave() {
        long s = Calendar.getInstance().getTimeInMillis();
        try {
            loadFile();//"", true);

            frame.setTitle(fH.getLRFN());
            long e = Calendar.getInstance().getTimeInMillis();
            MiscStuff.writeToLog("" + (e - s) + " tryLastSaveBeforeDisplayEverything");
            displayEverything(0);

        } catch (DataException de) {
            GUI.showFatalMessageDialog(de);
        }
        //MiscStuff.writeToLog("CastException"); }
    }

    /**
     * A method for testing...
     */
    public void zTest() {
        cG = new ControllerG();
        DayG day = new DayG(Calendar.getInstance(), new Currency(801));
        day.setNote("sample");
        cG.addDayG(day);
    }
//    
//    
//    
//    
//    
//    
//    gets and sets:
//    
//    

    public static ConnectionWrapper getCon() {
        return con;
    }

    public static void setCon(ConnectionWrapper aCon) {
        con = aCon;
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

    private void addMassPayrollOption(JMenu menu) {
        JMenuItem massPayroll = new JMenuItem("Mass Payroll");
        final ReturningActionListener massPayrollAl = new ReturningActionListener() {
            @Override
            public Object actionPerformedAndReturn(ActionEvent e) {
                MassPayrollP mp = MassPayrollP.addToFrame(
                        frame,
                        new ActionListener(){
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                //what should be run when the ok or cancel buttons are hit
                                //to restore the frame to normal
                                try {
                                    setUpFrame();
                                    displayEverything(0);
                                } catch (Exception ex) {
                                    showFatalMessageDialog(ex);
                                }
                            }
                        },
                        hR.getEmployees(),
                        getCompanyID());
                
                return mp;
            }
        };
        //IntegrationTestHelper.putActionListener(IntegrationTestHelper.AlKeys.addEmployee, addEmpAL);
        massPayroll.addActionListener(massPayrollAl);
        menu.add(massPayroll);
    }

    //I know I hate it when people do this...
    public class ActionListenerForLoadFile2 implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            AMP.MainDisplay.GUI oldGUI = new AMP.MainDisplay.GUI();
            oldGUI.hideGUI();
            oldGUI.new ActionListenerForLoadFile().actionPerformed(e);

            oldGUI.closeFrameQuick();

        }
    }

    public static class IntegrationTestHelper {

        private final static long timeToWait = 1000;
        private static final HashMap<AlKeys, ReturningActionListener> actionListeners =
                new HashMap<AlKeys, ReturningActionListener>();

        

        
//        private static final HashMap<String, Component> components = 
//                new HashMap<String, Component>();
//        public static String employeePanel = "epp";
        public static enum AlKeys {
            newYearCurrentEmployee, addEmployee, createEmployee
            , saveAL, addPayPeriodAL, goPayPeriodDateSetForFirstTime, openTaxManager;
        }

        public static ReturningActionListener getActionListener(AlKeys key) {
            return actionListeners.get(key);
        }

        public static void putActionListener(AlKeys key, ReturningActionListener aL) {
            actionListeners.put(key, aL);
        }

        static void createEmployeeActionPerformed(String name, String ss, String address, int claim, Currency rate) {

            currentInstance.nEmpName.setText(name);
            currentInstance.nEmpSS.setText(ss);
            currentInstance.nEmpAddress.setText(address);
            currentInstance.nEmpClaim.setText("" + claim);
            currentInstance.nEmpRate.setText("" + rate);

            getActionListener(AlKeys.createEmployee).actionPerformed(null);
        }

        static void saveActionPerformed() {
            getActionListener(GUI.IntegrationTestHelper.AlKeys.saveAL).actionPerformed(null);
        }

        static PayPeriod addPayPeriod(Employee emp, String hoursT) throws InterruptedException {
            return addPayPeriod(emp, hoursT, null);
        }
        
        static PayPeriod addPayPeriod(final Employee emp, String hoursT, String rateT) throws InterruptedException {

            new Thread(
                    new Runnable() {

                        public void run() {
                            try {
                                Thread.sleep(timeToWait);
                            } catch (InterruptedException ex) {
                            }
                            //we only need to do this for the first pay period for this employee:
                            if(emp.getPayPeriods().isEmpty()){
                                getActionListener(AlKeys.goPayPeriodDateSetForFirstTime).actionPerformed(null);
                            }
                        }
                    }).start();

            
            currentInstance.ePAddPayPeriod(emp, false);

            currentInstance.payPeriodPanel.setHours(hoursT);
            
            if(rateT != null){
                currentInstance.payPeriodPanel.setRate(rateT);
            }
            
            

            return (PayPeriod) getActionListener(AlKeys.addPayPeriodAL).actionPerformedAndReturn(null);
//            Thread.sleep(timeToWait);

        }

        static Checks getChecks() {
            final Checks checks = new ChecksImpl();
            for (final CheckBook cb : currentInstance.bH.getCheckBooks()) {
                checks.addAll(cb.getChecks());
            }
            return checks;
        }

        static Employee getEmployee(String empName) {
            return currentInstance.hR.getEmployee(empName);
        }

        static List<Transaction> getTransactions() {
            final List<Transaction> transactions = new ArrayList<Transaction>();
            for (final CheckBook cb : currentInstance.bH.getCheckBooks()) {
                transactions.addAll(cb.getTransactions());
            }
            return transactions;
        }
        
        public static void createGuiAndInsertCompany(
                final String companyName, 
                final String companyLocation, 
                final String companyYear) throws DataException {
            
            new Thread(new Runnable(){
                @Override
                public void run(){
                    
                    try {
                            Thread.sleep(timeToWait * 20);
                    } catch (InterruptedException ex) {
                    }
                    //now fill in info:
                    InsertCompanyDialog.IntegrationTestHelper.okBActionPerformed(
                            companyName, 
                            companyYear, 
                            companyLocation);
                    
                }
            }).start();
            
            new GUI();
        }
        
        static void createGuiAndExpectExistingCompany() throws DataException {
            new GUI();
            //assert not showing the 'pick your company' box:
            assertNotNull(currentInstance);
            assertNull(currentInstance.lastInsertCompanyDialog);
            //assertFalse(currentInstance.lastInsertCompanyDialog.isVisible());
        }
    }
}
