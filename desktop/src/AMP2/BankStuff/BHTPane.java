package AMP2.BankStuff;

/**
 * Write a description of class BHPanel here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import AMP2.BankStuff.display.CheckB;
import AMP2.BankStuff.GlCode.Code;
import AMP2.BankStuff.check.Checks;
import AMP2.BankStuff.display.ChecksP;
import AMP2.MainDisplay.InvalidUserInput;
import AMP2.MainDisplay.MessageDialog;
import AMP2.MainDisplay.util.ReturningActionListener;
import AMP2.Util.JComboBoxItem;
import AMP2.Util.MiscStuff;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.junit.Assert;


public class BHTPane extends JTabbedPane
{
    private static final AtomicReference<BHTPane> instance = new AtomicReference<BHTPane>();
    
    private BankHealth bH;
    private Check currentCheck;
    private Transaction currentTrans;
    private Bill currentBill;

    private final JTextField checkNumT = new JTextField();
    private final JTextField dateMT = new JTextField();
    private final JTextField dateDT = new JTextField();
    private final JTextField dateYT = new JTextField();
    private final JTextField payToT = new JTextField();
    private final JTextField amountT = new JTextField();
    private final JTextField clearDateDT = new JTextField();
    private final JTextField clearDateMT = new JTextField();
    private final JTextField clearDateYT = new JTextField();
    private final JTextField forT = new JTextField();
    private final JTextField recurrenceCodeT = new JTextField();
    private final JTextField daysForwardT = new JTextField();
    private final JTextField dailyAdvT = new JTextField();
    
    private final JTextField newGLCodeT = new JTextField();
    private JLabel lastMonthPaidL;
    private JComboBox cashedCB, gLCodeCodeCB;
    private JFrame checkF, checksF, transF, transactionsF, 
        billF, billsF, 
        snapShotF, sortF, gLF;
    private JLabel totalL;
    
    private int lastAPDisplay = 0;
    private GlCode lastGL = new GlCode(new Code(0));
    private Checks lastChecks;
    private List<Bill> lastBills = new ArrayList<Bill>();
    private List<Transaction> lastTransactions = new ArrayList<Transaction>();
    private Calendar lastCDate, lastCDateE;
    private String lastText = "";
    private boolean standAlone = false;

    private final ReturningActionListener allChecksActionListener = new ReturningActionListener() {
        @Override
        public Object actionPerformedAndReturn(ActionEvent e) {
//            if(e != null && e.getSource() instanceof BHTPane){
//                BHTPane bHTPane2 = (BHTPane) e.getSource();
//                System.out.println("bHTPane2 has " + bHTPane2.bH.getCurrentCheckBook().getChecks().size() + " checks");
//            }
            return displayAllChecks();
        }
    };
    
    
    /**
     * Our Main Method for a stand alone BankHeather...
     */
    public static void main(String[] args) {
        JFrame fra = new JFrame();
        fra.setSize(new Dimension(810, 575));
        fra.add(new BHTPane(fra));
        fra.setVisible(true);
        //standAlone = true;
    }
    
    public static synchronized BHTPane getInstance(BankHealth bh){
        if(instance.get() == null){
            instance.set(new BHTPane(bh));
        }else{
            instance.get().setUp(bh);
        }
        
        return instance.get();
    }
    
    /**
     * Constructor for objects of class BHPanel used with our main method.
     */
    private BHTPane(JFrame fra) {
        this();
        
        standAlone = true;
        
        FileHandler fH = new FileHandler();
        
        try{
            bH = (BankHealth) fH.readObAn("BankHealth\\Banker");   
        }
        catch(Exception e) {
            MiscStuff.writeToLog(e.toString());   
        }
        
        fra.addWindowListener(new WindowListener() {
            public void windowActivated(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {}
            public void windowClosing(WindowEvent e) {
                closeFrame();}
            public void windowDeactivated(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowOpened(WindowEvent e) {}
        });
        
        setUp();
    }
    
    /**
     * Constructor for objects of class BHPanel
     */
    private BHTPane() {
        this(new BankHealth());
    }
    
    /**
     * Constructor for objects of class BHPanel
     */
    private BHTPane(BankHealth bH) {
        super();
        
        setUp(bH);
    }
    
    /**
     * A method that adds our bill...
     */
    public void addBill() {
        
        //GlCode glCode = bH.getGlCodeByCode(((JComboBoxItem) gLCodeCodeCB.getSelectedItem()).getId());
        
        Bill bill = new Bill(
        new Currency(amountT.getText()),
        new Integer(dateDT.getText()), 
        //new Integer(gLCodeT.getText()), 
        //bH.getGlCode(new Code(((JComboBoxItem) gLCodeCodeCB.getSelectedItem()).getId())),
        ((JComboBoxItem) gLCodeCodeCB.getSelectedItem()).getGlCode(),
        new Integer(recurrenceCodeT.getText()),
        forT.getText());
        
        boolean paid = false;
        if(cashedCB.getSelectedItem().equals("Yes")) {
            bill.setIsPaid(true);
        }
        bH.addBill(bill);
    }
    
    /**
     * A method that adds our check.
     * 
     * @return check, the new check we created.
     */
    public Check addCheck() throws InvalidUserInput {
        boolean goneThrough = false;
        if(cashedCB.getSelectedItem().equals("Yes")) {
            goneThrough = true;   
        }
        Calendar cDate = Calendar.getInstance();
        cDate.set( 
        new Integer(dateYT.getText()), 
        (new Integer(dateMT.getText()) - 1), // OK
        new Integer(dateDT.getText()));
        
        Calendar cDateE = Calendar.getInstance();
        cDateE.set(new Integer(clearDateYT.getText()),
        new Integer(clearDateMT.getText()) - 1, 
        new Integer(clearDateDT.getText()));
        
        //MiscStuff.writeToLog(cDate);
        
        //make sure we have all the fields
        final int checkNum;
        try{
            checkNum = new Integer(checkNumT.getText());
        }catch(Exception e){
            throw new InvalidUserInput("The check number isn't a number: " + checkNumT.getText(), e)
                    .setUserReadableMessage("Please enter a number as the check number. You entered: " + checkNumT.getText());
        }
        
        final Currency amount;
        try{
            amount = new Currency(amountT.getText());
        }catch(Exception e){
            //TODO: this should just give a warning and tell the user to try again (if it doesn't)
            throw new InvalidUserInput("The amount wasn't a number:\":" + amountT.getText() + "\"", e)
                    .setUserReadableMessage("Please enter just a number (you entered " + amountT.getText() + ")");
        }
        
        final GlCode glCode;
        try{
            glCode = //((JComboBoxItem) gLCodeCodeCB.getSelectedItem()).getId();
                    ((JComboBoxItem) gLCodeCodeCB.getSelectedItem()).getGlCode();
        }catch(Exception e){
            throw new InvalidUserInput("Couldn't get the gl code", e);
        }
        
        final Check check = new Check(
                checkNum, 
                cDate, payToT.getText(), 
                amount, 
                //bH.getGlCode(new Code(glCode)),
                glCode,
                goneThrough, 
                cDateE, forT.getText());  
        
        bH.getCurrentCheckBook().addCheck(check);
        return check;
    }
    
    /**
     * A method that takes care of our bill stuff...
     * 
     * @param operation, 0 for add, 1 for a replace(must send a bill), 2 for...
     * @param bill, used with some operations.
     */
    public void bill(int operation, Bill bill) {
        killMinorFs();
        
        int c = SwingConstants.CENTER;
        
        billF = new JFrame();
        JPanel mainP = new JPanel();
        JPanel pan = new JPanel();
        billF.getContentPane().add(mainP);
        
        mainP.setLayout(new BoxLayout(mainP, BoxLayout.PAGE_AXIS));  
        pan.setLayout(new GridLayout(1, 7));
        
        JLabel lab = new JLabel("Due Date", c);
        pan.add(lab);
        lab = new JLabel("Amount", c);
        pan.add(lab);
        lab = new JLabel("Paid", c);
        pan.add(lab);
        lab = new JLabel("Description", c);
        pan.add(lab);
        lab = new JLabel("Last Month Paid", c);
        pan.add(lab);
        lab = new JLabel("G.L. Code", c);
        pan.add(lab);
        lab = new JLabel("Recurrence Code", c);
        pan.add(lab);
        
        mainP.add(pan);
        
        cashedCB = new JComboBox(new String[]{"No", "Yes"});
        
        switch(operation) {
            case 0:
                pan = new JPanel();
                pan.setLayout(new GridLayout(1, 7));
                
                dateDT.setText("" + Calendar.getInstance().
                    get(Calendar.DAY_OF_MONTH));
                pan.add(dateDT);
                amountT.setText("");
                pan.add(amountT);
                
                pan.add(cashedCB);
                
                forT.setText(" "); //Description
                pan.add(forT);
                lab = new JLabel("Never");
                pan.add(lab);
                
                setAndLoadGLCodeCB();
                pan.add(gLCodeCodeCB);
//                gLCodeT = new JTextField("0");
//                pan.add(gLCodeT);
                
                recurrenceCodeT.setText("1");
                pan.add(recurrenceCodeT);
                mainP.add(pan);
               
                pan = new JPanel();
                
                JButton addB = new JButton("Add Bill");
                addB.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        
                        addBill();
                       
                        billF.setVisible(false);
                        billF = null;
                        
                        setUp();
                        //redisplayAP();
                        //MiscStuff.writeToLog(((ArrayList) 
                          //  bH.getCurrentCheckBook().getChecks()).size());
                    }
                });              
                pan.add(addB);
                
                JButton cancel = new JButton("Cancel");
                cancel.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        billF.setVisible(false);
                        billF = null;
                    }
                });
                pan.add(cancel);
                
//                JButton help = new JButton("Help");
  //              help.addActionListener(new ActionListener() {
    //                public void actionPerformed(ActionEvent e) {
      //                   JOptionPane.showInternalMessageDialog(billF, 
        //                    "informattion",
          //                  "information", JOptionPane.INFORMATION_MESSAGE);  //?????????????????????????????????????????????????  
            //        }
              //  });
                //pan.add(help);
                
                mainP.add(pan);
                
                billF.pack();
                billF.setVisible(true); 
                break;   
            
            case 1:
                
                currentBill = bill;
                pan = new JPanel();
                pan.setLayout(new GridLayout(1, 7));
                
                dateDT.setText("" + bill.getDueDate());
                pan.add(dateDT);
                amountT.setText("" + bill.getAmount());
                pan.add(amountT);
                
                if(bill.getIsPaid() == true) {
                    cashedCB.setSelectedIndex(1);   
                }
                pan.add(cashedCB);
                forT.setText(bill.getDescription()); //Description
                pan.add(forT);
                lastMonthPaidL = new JLabel("" + bill.getLastMonthPaid());
                pan.add(lastMonthPaidL);
                
                setSelectedGLCodeCB(bill.getGLCode());
                pan.add(gLCodeCodeCB);
//                gLCodeT = new JTextField("" + bill.getGLCode());
//                pan.add(gLCodeT);
                
                recurrenceCodeT.setText("" + bill.getRecurrenceCode());
                pan.add(recurrenceCodeT);
                
                mainP.add(pan);
                
                pan = new JPanel();
                JButton replace = new JButton("Replace");
                replace.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        bH.removeBill(currentBill);
                        
                        addBill();
                        
                        billF.setVisible(false);
                        billF = null;
                        
                        billsF.setVisible(false);
                        billsF = null;
                        
                        setUp();
                        redisplayAP();
                        
                    }
                });
                pan.add(replace);
                
                JButton delete = new JButton("Delete");
                delete.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        bH.removeBill(currentBill);
                        
                        billF.setVisible(false);
                        billF = null;
                        
                        billsF.setVisible(false);
                        billsF = null;
                        
                        setUp();
                        redisplayAP();
                        
                    }
                });
                pan.add(delete);
                
                cancel = new JButton("Cancel");
                cancel.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        
                        billF.setVisible(false);
                        billF = null;
                        redisplayAP();
                    }
                });
                pan.add(cancel);
                
                mainP.add(pan);
                
                billF.pack();
                billF.setVisible(true);
                break;
        }
    }
    
    /**
     * A method that returns our bill label...
     * 
     * @return pan, our panel with our bill labels.
     */
    private JPanel billLabel() {
        int c = SwingConstants.CENTER;
        
        JPanel pan = new JPanel(); 
        pan.setLayout(new GridLayout(1, 6));
        
        JLabel lab = new JLabel("Due Date", c);
        pan.add(lab);
        lab = new JLabel("Amount", c);
        pan.add(lab);
        lab = new JLabel("Description", c);
        pan.add(lab);
        lab = new JLabel("Last Month Paid", c);
        pan.add(lab);
        lab = new JLabel("G.L. Code", c);
        pan.add(lab);
        lab = new JLabel("Recurrence Code", c);
        pan.add(lab); 
        
        return pan;
    }
    
    /**
     * A method that does our check functions.
     * 
     * @param opertaion, 0 for add, 1 for a replace (must send a 
     *      check not a null with), 2 for adding a new check from a check that
     *      is passed in, 3...
     *      
     * @param check, used with some of the operations...
     */
    public void check(int operation, Check check) { //jbuttons...
        killMinorFs();
        
        int c = SwingConstants.CENTER;
        
        checkF = new JFrame();
        JPanel mainP = new JPanel();
        JPanel pan = new JPanel();
        checkF.getContentPane().add(mainP);
        
        mainP.setLayout(new BoxLayout(mainP, BoxLayout.PAGE_AXIS));  
        pan.setLayout(new GridLayout(1, 8));
        
        JLabel lab = new JLabel("Date", c);
        pan.add(lab);
        lab = new JLabel("Check #", c);
        pan.add(lab);
        lab = new JLabel("Pay To", c);
        pan.add(lab);
        lab = new JLabel("Amount", c);
        pan.add(lab);
        lab = new JLabel("G.L. Code", c);
        pan.add(lab);
        lab = new JLabel("Cashed", c);
        pan.add(lab);
        lab = new JLabel("For", c);
        pan.add(lab);
        lab = new JLabel("Clear Date", c);
        pan.add(lab);
        
        mainP.add(pan);
        
        switch(operation) {
            case 0:
                pan = new JPanel();
                pan.setLayout(new GridLayout(1, 8));
                
                JPanel dateTP = new JPanel();
                Calendar cal = Calendar.getInstance();
                dateMT.setText("" + (cal.get(Calendar.MONTH) + 1)); 
                dateTP.add(dateMT);
                dateDT.setText("" + cal.get(Calendar.DAY_OF_MONTH));
                dateTP.add(dateDT);
                dateYT.setText("" + cal.get(Calendar.YEAR));
                dateTP.add(dateYT);
                pan.add(dateTP);
                
                String numS = "";
                int numI = bH.getCurrentCheckBook().nextNumber();
                if(numI != 0) {
                    numS = "" + numI;   
                }
                checkNumT.setText(numS);
                pan.add(checkNumT);
                
                payToT.setText("");
                pan.add(payToT);
                amountT.setText("");
                pan.add(amountT);
                
                setAndLoadGLCodeCB();
                pan.add(gLCodeCodeCB);
               
                //gLCodeT = new JTextField("0");
                //pan.add(gLCodeT);
                
                cashedCB = new JComboBox(new String[]{"No", "Yes"});
                pan.add(cashedCB);
                
                forT.setText(" ");
                pan.add(forT);
                
                JPanel clearDateTP = new JPanel();
                Calendar cal2 = Calendar.getInstance();
                cal2.roll(Calendar.DAY_OF_YEAR, 3);
                clearDateMT.setText("" + (cal2.get(Calendar.MONTH) 
                    + 1));
                clearDateTP.add(clearDateMT);
                clearDateDT.setText("" + cal2.get(
                Calendar.DAY_OF_MONTH));
                clearDateTP.add(clearDateDT);
                clearDateYT.setText("" + cal2.get(Calendar.YEAR));
                clearDateTP.add(clearDateYT);
                pan.add(clearDateTP);
                
                mainP.add(pan);
                
                pan = new JPanel();
                
                JButton addB = new JButton("Add Check");
                final ReturningActionListener addCheckAl = new ReturningActionListener() {
                    public Check actionPerformedAndReturn(ActionEvent e) {
                        Check toRet = null;
                        try{
                            toRet = addCheck();
                            checkClosing();
                            setUp();
                        }catch(InvalidUserInput iui){
                            MessageDialog.WarnMessageDialog(new InvalidUserInput("Not adding check", iui).setUserReadableMessage("Not adding the check"));
                        }
                        
                        return toRet;
                        
                        //MiscStuff.writeToLog(((ArrayList) 
                          //  bH.getCurrentCheckBook().getChecks()).size());
                    }
                };
                
                IntegrationTestHelper.putActionListener(IntegrationTestHelper.AL.addCheck, addCheckAl);
                
                addB.addActionListener(addCheckAl);              
                pan.add(addB);
                
                JButton printAdd = new JButton("Print & Add");
                printAdd.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        
                        final Check check;
                        
                        try{
                            check = addCheck();
                        }catch(InvalidUserInput iui){
                            MessageDialog.WarnMessageDialog(new Exception("Not adding check", iui));
                            return;
                        }
                        
                        printCheck(check);
                        
                        checkClosing();
                        
                        setUp();
                    }
                });
                pan.add(printAdd);
                
                JButton cancel = new JButton("Cancel");
                cancel.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        checkClosing();
                    }
                });
                pan.add(cancel);
                
                mainP.add(pan);
                
                checkF.pack();
                checkF.setVisible(true); 
                checkNumT.requestFocus();
                break;   
            
            case 1:
            
                pan = new JPanel();
                JButton replace = new JButton("Replace");
                replace.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        
                        //before we were throwing the InvalidUserInput exception
                        //we were removing the check before adding the new one,
                        //To make sure we don't break anything we'll save the
                        //current check:
                        final Check checkToRemove = currentCheck;
                        
                        try{
                            addCheck();
                        }catch(InvalidUserInput iui){
                            MessageDialog.WarnMessageDialog(new Exception("Not replacing check", iui));
                            return;
                        }
                        
                        
                        
                        CheckBook cBook = bH.getCurrentCheckBook();
                        cBook.removeCheck(checkToRemove);
                        
                        
                        checkClosing();
              
                        setUp();
                        redisplayAP();
                        
                    }
                });
                pan.add(replace);
                
                printAdd = new JButton("Print & Replace");
                printAdd.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        CheckBook cBook = bH.getCurrentCheckBook();
                        cBook.removeCheck(currentCheck);
                        
                        final Check check;
                        try{
                            check = addCheck();
                        }catch(InvalidUserInput iui){
                            MessageDialog.WarnMessageDialog(new Exception("Not replacing check", iui));
                            return;
                        }
                        
                        printCheck(check);
                        
                        checkClosing();
                        
                        setUp();
                        redisplayAP();
                    }
                });
                pan.add(printAdd);
                
                JButton delete = new JButton("Delete");
                delete.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        CheckBook cBook = bH.getCurrentCheckBook();
                        
                        cBook.removeCheck(currentCheck);
                        
                        checkClosing();
                        
                        setUp();
                        redisplayAP();
                        
                    }
                });
                pan.add(delete);
                
                cancel = new JButton("Cancel");
                cancel.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        
                        checkClosing();
                        
                        redisplayAP();
                    }
                });
                pan.add(cancel);
                
                mainP.add(checkCheck(check));
                mainP.add(pan);
                
                checkF.pack();
                checkF.setVisible(true);
                break;
                
            case 2:
            
                pan = new JPanel();
                replace = new JButton("Add Check");
                replace.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        CheckBook cBook = bH.getCurrentCheckBook();
                        
                        cBook.removeCheck(currentCheck);
                        
                        try{
                            addCheck();
                        }catch(InvalidUserInput iui){
                            MessageDialog.WarnMessageDialog(new Exception("Not replacing check", iui));
                            return;
                        }
                        
                        checkClosing();
              
                        setUp();
                    }
                });
                pan.add(replace);
                
                printAdd = new JButton("Print & Add");
                printAdd.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        
                        //before we were throwing the InvalidUserInput exception
                        //we were removing the check before adding the new one,
                        //To make sure we don't break anything we'll save the
                        //current check:
                        final Check checkToRemove = currentCheck;
                        
                        
                        final Check check;
                        try{
                            check = addCheck();
                        }catch(InvalidUserInput iui){
                            MessageDialog.WarnMessageDialog(new Exception("Not replacing check", iui));
                            return;
                        }
                        
                        
                        CheckBook cBook = bH.getCurrentCheckBook();
                        cBook.removeCheck(checkToRemove);
                        
                        printCheck(check);
                        
                        checkClosing();
                        
                        setUp();
                    }
                });
                pan.add(printAdd);
                
                delete = new JButton("Delete");
                delete.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        CheckBook cBook = bH.getCurrentCheckBook();
                        
                        cBook.removeCheck(currentCheck);
                        
                        checkClosing();
                        
                        setUp();
                    }
                });
                pan.add(delete);
                
                cancel = new JButton("Cancel");
                cancel.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        
                        checkClosing();
                    }
                });
                pan.add(cancel);
                
                mainP.add(checkCheck(check));
                mainP.add(pan);
                
                checkF.pack();
                checkF.setVisible(true);
                break;
        }
    }
    
    /**
     * A method  that handles our code in which we are given a check to fill in
     * the date in our check method. It does the displaying end of it as well.
     * 
     * @param check, our check to work with.
     * @param buts, our jbuttons to add.
     * 
     * @return our panel.
     */
    private JPanel checkCheck(Check check) {
            currentCheck = check;
            JPanel pan = new JPanel();
            pan.setLayout(new GridLayout(1, 8));             
            
            Calendar cal = check.getDate();
            JPanel dateTP = new JPanel();
            dateMT.setText("" + (cal.get(Calendar.MONTH) + 1)); 
            dateTP.add(dateMT);
            dateDT.setText("" + cal.get(Calendar.DAY_OF_MONTH));
            dateTP.add(dateDT);
            dateYT.setText("" + cal.get(Calendar.YEAR));
            dateTP.add(dateYT);
            pan.add(dateTP);
            
            checkNumT.setText("" + check.getCheckNum());
            pan.add(checkNumT);  
            
            payToT.setText(check.getPayTo());
            pan.add(payToT);
            amountT.setText("" + check.getAmount());
            pan.add(amountT);
            
            setSelectedGLCodeCB(check.getGlCode());
            pan.add(gLCodeCodeCB);
            
            cashedCB = new JComboBox(new String[]{"No", "Yes"});
            if(check.getGoneThrough() == true) {
                cashedCB.setSelectedIndex(1);   
            }
            pan.add(cashedCB);
            forT.setText(check.getForS());
            pan.add(forT);
            
            JPanel clearDateTP = new JPanel();
            Calendar cal2 = check.getExpectedClearDate();
            clearDateMT.setText("" + (cal2.get(Calendar.MONTH) 
                + 1));
            clearDateTP.add(clearDateMT);
            clearDateDT.setText("" + cal2.get(
            Calendar.DAY_OF_MONTH));
            clearDateTP.add(clearDateDT);
            clearDateYT.setText("" + cal2.get(Calendar.YEAR));
            clearDateTP.add(clearDateYT);
            pan.add(clearDateTP);
            
            //pan.add(buts);
            
            return pan;     
    }
    
    /**
     * A method that handles some of our closing operations on with our check
     * method. (AKA a helper method);
     */
    private void checkClosing() {
        
        checkF.setVisible(false);
        checkF = null;
        
        if(checksF != null) {
            checksF.setVisible(false);
            checksF = null;  
        }
    }
    
    /**
     * A method used to close our frame if we are stand alone.
     */   
    private void closeFrame() {
        try{
            FileHandler fH = new FileHandler();
            fH.writeObAn("BankHealth\\Banker",
                bH);   
                //MiscStuff.writeToLog("close");
        }
        catch(IOException exc) {
        }
        System.exit(0);
    }
    
    /**
     * A method that displays all our checks...
     */
    private JFrame displayAllChecks() {      
        lastAPDisplay = 1;
   
        return displayChecks(bH.getCurrentCheckBook().getChecks());       
    }
    
    /**
     * A method that does our actionPerformed all transactions...
     */
    public void displayAllTrans() {
        lastAPDisplay = 3;
        displayTrans(bH.getCurrentCheckBook().getTransactions());
    }
    
    /**
     * A method that displays our bills.
     * 
     * @param bills, the bills to display.
     */
    private void displayBills(List<Bill> bills) {
        killMinorFs();
        
        //lastBills = bills;
        //lastAPDisplay = 4;
        int c = SwingConstants.CENTER;
        
        billsF = new JFrame();
        Container cP = billsF.getContentPane();
        
        JPanel mPan = new JPanel();
        mPan.setLayout(new BoxLayout(mPan, BoxLayout.PAGE_AXIS)); 
        //mScr.add(mPan);
        
        JScrollPane mScr = new JScrollPane(mPan);
        cP.add(mScr);
        
        JPanel pan = new JPanel();
        pan.setLayout(new GridLayout(1, 2));
        pan.setBackground(new Color(63000));
        mPan.add(pan);
        
        JLabel lab = new JLabel("Total:");
        pan.add(lab);
        
        totalL = new JLabel();
        pan.add(totalL);
        
        mPan.add(billLabel());
        
        int color = 0;
        Currency billTotal = Currency.Zero;
        
        for(int i = 0; i < bills.size(); i++) {
            pan = new JPanel();
            
            Bill bill = (Bill) bills.get(i);
            BillB panB = new BillB(this, bill);
            
            billTotal = billTotal.add(bill.getAmount());
            
            panB.setLayout(new GridLayout(1, 6));
            panB.setPreferredSize(new Dimension(650, 25));
            
            if(color == 2) {
                panB.setBackground(Color.YELLOW);
                color = 0;
            }
            else {
                color++;  
                panB.setBackground(Color.WHITE);
            }
            
            lab = new JLabel("" + bill.getDueDate(), c);
            panB.add(lab);
            lab = new JLabel("" + bill.getAmount(), c);
            panB.add(lab);
            lab = new JLabel(bill.getDescription(), c);
            panB.add(lab);
            lab = new JLabel("" + bill.getLastMonthPaid(), c);
            panB.add(lab);
            
            lab = new JLabel(bill.getGLCode().getDescription(), c);
            //lab = new JLabel(bill.getGLCode().getDescription(), c);
            panB.add(lab);
                
            lab = new JLabel("" + bill.getRecurrenceCode(), c);
            panB.add(lab);
            
            pan.add(panB);
            mPan.add(pan);
        }
        totalL.setText(billTotal.twoDecFormat());
        
        JButton done = new JButton("Done");
        done.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                billsF.setVisible(false);
                billsF = null;
            }
        });
        mPan.add(done);
        
        //cP.add(mPan);
        billsF.pack();
        billsF.setVisible(true);
    }
    
    /**
     * A method that does our ActionPerformed for our all checks...
     * 
     * @param checks, the checks we want to show...
     */
    private JFrame displayChecks(Checks checks) {
        killMinorFs();
  
        checksF = new JFrame();
        Container cP = checksF.getContentPane();
        
        ActionListener doneAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checksF.setVisible(false);
                checksF = null;
            }
        };
        
        ChecksP checksP = new ChecksP(this, checks, doneAction);
        cP.add(checksP);
        
        checksF.pack();
        checksF.setVisible(true);
        return checksF;
    }
    
    /**
     * A method that displays all the transactions given it.
     * 
     * @param transactions, the transactions to be shown.
     */
    public void displayTrans(List<Transaction> transactions) {
        killMinorFs();
        
        int c = SwingConstants.CENTER;
        
        transactionsF = new JFrame();
        Container cP = transactionsF.getContentPane();
        
        JPanel mPan = new JPanel();
        mPan.setLayout(new BoxLayout(mPan, BoxLayout.PAGE_AXIS)); 
        
        JScrollPane mScr = new JScrollPane(mPan);
        cP.add(mScr);
        
        JPanel pan = new JPanel();
        pan.setLayout(new GridLayout(1, 2));
        pan.setBackground(new Color(63000));
        mPan.add(pan);
        
        JLabel lab = new JLabel("Total:");
        pan.add(lab);
        
        totalL = new JLabel();
        pan.add(totalL);
        
        pan = new JPanel();
        pan.setLayout(new GridLayout(1, 5));
        
        lab = new JLabel("Date", c);
        pan.add(lab);
        lab = new JLabel("For", c);
        pan.add(lab);
        lab = new JLabel("Amount", c);
        pan.add(lab);
        lab = new JLabel("G.L. Code", c);
        pan.add(lab);
        lab = new JLabel("Cleared", c);
        pan.add(lab);
        
        mPan.add(pan);
        
        
        int color = 0;
        Currency totalTrans = Currency.Zero;
        
        for(int in = 0; in < transactions.size(); in++) {
            Transaction trans = (Transaction) transactions.get(in);
            totalTrans = totalTrans.add(trans.getAmount());
            
            String goneTh = "Yes";
            if(trans.getGoneThrough() == false) {
                goneTh = "No";   
            }
            
            pan = new JPanel();
            TransB panB = new TransB(this, trans);
            panB.setLayout(new GridLayout(1, 5));
            panB.setPreferredSize(new Dimension(650, 25));
            
            if(color == 2) {
                panB.setBackground(Color.YELLOW);
                color = 0;
            }
            else {
                color++;  
                panB.setBackground(Color.WHITE);
            }
            
            lab = new JLabel(trans.getDateS(), c);
            panB.add(lab);
            lab = new JLabel(trans.getDescription(), c);
            panB.add(lab);
            lab = new JLabel("" + trans.getAmount(), c);
            panB.add(lab);
            lab = new JLabel(trans.getGLCode().getDescription(), c);
            panB.add(lab);
            lab = new JLabel(goneTh, c);
            panB.add(lab);
            
            pan.add(panB);
            mPan.add(pan);
        }
        totalL.setText(totalTrans.twoDecFormat());
        
        JButton done = new JButton("Done");
        done.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                transactionsF.setVisible(false);
                transactionsF = null;
            }
        });
        mPan.add(done);
        
        transactionsF.pack();
        transactionsF.setVisible(true);     
    }
    
    /**
     * A method that kills our little frames.
     * 
     * @param redisplayAP, if we are to call redisplayAP().
     */
    public void killMinorFs() {
        ArrayList frames = new ArrayList();
        frames.add(checkF);
        frames.add(checksF);
        frames.add(transF);
        frames.add(transactionsF);
        frames.add(billF);
        frames.add(billsF);
        frames.add(snapShotF);
        frames.add(sortF);
        
        for(int i = 0; i < frames.size(); i++) {
            JFrame f = (JFrame) frames.get(i);
            
            if(f != null) {
                f.setVisible(false);
                f = null;
            }
        }
    }
    
    /**
     * A method that prints a given check.
     * 
     * @param check, the check to be printed.
     */
    public void printCheck(Check check) {
        JFrame pCheck = new JFrame();
        Container pCCP = pCheck.getContentPane();
        // 5 text boxes. 
        JPanel mPan = new JPanel();
        mPan.setLayout(new GridLayout(0, 1));
        mPan.setPreferredSize(new Dimension(600, 350));
        pCCP.add(mPan);
        // more panels up here to lower date.
        
        for(int i = 0; i < 2; i++) {
            
            if(i != 0) {
                for(int j = 0; j < 6; j++) {
                    mPan.add(new JLabel(" "));   
                }
            }
            
            //Date
            JPanel pan = new JPanel();
            pan.setLayout(new FlowLayout(FlowLayout.RIGHT));
            mPan.add(pan);
            
            JLabel lab = new JLabel(check.getDateS());
            pan.add(lab);
            
            //PayTo and Amount
            pan = new JPanel();
            pan.setLayout(new GridLayout(1, 0));
            mPan.add(pan);
            
            JPanel pan2 = new JPanel();
            pan2.setLayout(new FlowLayout(FlowLayout.LEFT));
            pan.add(pan2);
            
            lab = new JLabel(check.getPayTo());
            pan2.add(lab);
            
            pan2 = new JPanel();
            pan2.setLayout(new FlowLayout(FlowLayout.RIGHT));
            pan.add(pan2);
            
            lab = new JLabel("*" + check.getAmount().twoDecFormat() + "*");
            pan2.add(lab);
            
            //Dollar String.
            pan = new JPanel();
            pan.setLayout(new FlowLayout(FlowLayout.LEFT));
            mPan.add(pan);
            
            if(i == 0) {
                lab = new JLabel("***" + check.getDollarsS() + "***");
                pan.add(lab);
            }
            else {
                pan.add(new JLabel(""));   
            }
            
            //For
            pan = new JPanel();
            mPan.add(pan);
            lab = new JLabel("");
            pan.add(lab);
            
            pan = new JPanel();
            pan.setLayout(new FlowLayout(FlowLayout.LEFT));
            mPan.add(pan);
            
            lab = new JLabel("For: " + check.getForS());
            pan.add(lab);    
        }
        
        pCheck.pack();
        pCheck.setVisible(true);
        
        //printing...
        PrintUtilities.printComponent(pCheck, 3);
        
        pCheck.setVisible(false); 
        pCheck = null;
        
    }
    
    /**
     * A method that does our redisplaying...
     */
    public void redisplayAP() {
        
        switch(lastAPDisplay) {
            case 0: displayChecks(sortUnpostChecks(lastChecks)); break;
            case 1: displayAllChecks(); break;
            case 2: displayTrans(sortUnpostTrans(lastTransactions)); break; 
            case 3: displayAllTrans(); break;
            case 4: displayBills(lastBills); break; // good idea?
            case 5: displayChecks(sortGLChecks(lastChecks, lastGL)); break;
            case 6: displayChecks(sortDateChecks(lastChecks, lastCDate, 
                lastCDateE)); break; 
            case 7: displayTrans(sortDateTrans(lastTransactions, lastCDate, 
                lastCDateE)); break; 
            case 8: displayBills(sortGLBills(lastBills, lastGL)); break;
            case 9: displayChecks(sortDateGLChecks(lastChecks,
                lastGL, lastCDate, lastCDateE)); break;
            case 10: displayTrans(sortDateGLTrans(lastTransactions,
                lastGL, lastCDate, lastCDateE)); break;
            case 11: displayChecks(sortTextChecks(lastChecks, lastText)); break;
            case 12: displayTrans(sortTextTrans(lastTransactions, lastText)); 
                break;
            case 13: displayBills(sortTextBills(lastBills, lastText)); break;
                
            default:MiscStuff.writeToLog("redisplayAP: " + lastAPDisplay); break;
        }
    }
    
    /**
     *This method sets and loads gLCodeCodeCB
     */
    private void setAndLoadGLCodeCB(){
        gLCodeCodeCB = new JComboBox();
        
        //this is probably not a good way to make sure that our users have at least
        //one glcode:
        if(bH.getGLCodes().isEmpty()){
            bH.addGLCode(new GlCode(new Code(999), "Unknown"));
        }

        //Map<Integer, String> gLs = bH.getGLCodes();
        Collection<GlCode> gLs = bH.getGLCodes().values();
        
        //Iterator it = gLs.keySet().iterator();
        List<JComboBoxItem> list = new ArrayList<JComboBoxItem>();

        //while(it.hasNext()) {
        for(GlCode glCode : gLs){
            //int codeInt = glCode.getCode().getCode();//Integer.parseInt(it.next().toString());
            //String description = glCode.getDescription();//(String) gLs.get(code);
            JComboBoxItem cBItem = new JComboBoxItem(glCode);//codeInt, description); 
            
            boolean added = false;
            for(int i = 0; i < list.size() && !added; i++) {
                if(/*codeInt*/ glCode.getCode().getCode() < ((JComboBoxItem) list.get(i)).getGlCode().getCode().getCode()/*.getId()*/ ) {
                    list.add(i, cBItem);
                    added = true;
                }
            } 
            
            if(!added) {
                list.add(cBItem);
            }   
        }
        
        for(int i = 0; i < list.size(); i++) {
            gLCodeCodeCB.addItem((JComboBoxItem) list.get(i));
        }
    }
    
    /**
     *This method sets the selected index of the gLCodeCodeCB based on the passed in glCode
     *
     *@param code, the gLCode that should be selected
     */
    private void setSelectedGLCodeCB(int gLCodeCodeCode) {
        for(GlCode glCode : bH.getGLCodes().values()){
            if(glCode.getCode().getCode() == gLCodeCodeCode){
                setSelectedGLCodeCB(glCode);
                return;
            }
        }
        
        throw new RuntimeException("we don't have the required glCodeCodeCode " + gLCodeCodeCode + " in " + bH.getGLCodes().values());
    }
    
    /**
     *This method sets the selected index of the gLCodeCodeCB based on the passed in glCode
     *
     *@param code, the gLCode that should be selected
     */
    private void setSelectedGLCodeCB(GlCode gLCode) {
        setAndLoadGLCodeCB();
        
        DefaultComboBoxModel model = (DefaultComboBoxModel)gLCodeCodeCB.getModel();
        for (int i = 0; i < model.getSize();i++)
        {
            JComboBoxItem item = (JComboBoxItem) model.getElementAt(i);
            if(gLCode.equals(item.getGlCode())/*gLCode.getCode().getCode() ==  item.getId()*/) {
                gLCodeCodeCB.setSelectedIndex(i);
            }
        }
    }
    
    /**
     * A method that does some setting up...
     */
    private void setUp(BankHealth bh){
        this.bH = bh;
        setUp();
    }
    
    private void setUp(){
        try{
            setUp_throwsException();
        }catch(Exception e){
            MessageDialog.FatalMessageDialog(e);
        }
    }
    
    private void setUp_throwsException() throws Exception{

        int tPlace = 0;
        if(this.getTabCount() > 1) {
            tPlace = this.getSelectedIndex();  
        }
        
        this.removeAll();
        killMinorFs();
        
        int c = SwingConstants.CENTER;
        int r = SwingConstants.RIGHT;
        
        JPanel mPan = new JPanel();
        mPan.setLayout(new BoxLayout(mPan, BoxLayout.PAGE_AXIS)); 
        
        JPanel topP = new JPanel();
        topP.setLayout(new GridLayout(1, 0));
        mPan.add(topP);
        
        //something has been null in the past:
        if(bH == null){
            throw new Exception("bH is null?");
        }else if(bH.getCurrentCheckBook() == null){
            throw new Exception("Current check book is null? " + bH);
        }else if(bH.getCurrentCheckBook().getDouble(0) == null){
            throw new Exception("balance is null? " + bH.getCurrentCheckBook());
        }
        
        JLabel lab = new JLabel("Current Balance:");
        topP.add(lab);
        
        lab = new JLabel(Math2.formatR(bH.getCurrentCheckBook().getDouble(0)));
        topP.add(lab);
        
        lab = new JLabel("Adjusted Balance:");
        topP.add(lab);
        
        lab = new JLabel(Math2.formatR(bH.getCurrentCheckBook().getDouble(3)));
        topP.add(lab);
        
        JPanel pan = new JPanel();
        pan.setLayout(new GridLayout(1, 0));
        
        JPanel pan2 = new JPanel();
        pan2.setLayout(new GridLayout(0, 1)); 
        pan.add(pan2);
        
        JButton jB = new JButton("Add Check");
        final ReturningActionListener addCheckPrep = new ReturningActionListener() {
            public Object actionPerformedAndReturn(ActionEvent e) {
                check(0, null);
                
                return null;
            }
        };
        
        IntegrationTestHelper.putActionListener(IntegrationTestHelper.AL.addCheckPrep, addCheckPrep);
        
        jB.addActionListener(addCheckPrep);
        pan2.add(jB);
        
        jB = new JButton("Unpost check");
        jB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayChecks(sortUnpostChecks(bH.getCurrentCheckBook().
                    getChecks()));    
            }
        }); 
        pan2.add(jB);
        
        jB = new JButton("All Checks");

        jB.addActionListener(allChecksActionListener);
        pan2.add(jB);
        
        pan2 = new JPanel();
        pan2.setLayout(new GridLayout(0, 1)); 
        pan.add(pan2);
        
        
        final ReturningActionListener addTransactionPrep = new ReturningActionListener() {
            public Object actionPerformedAndReturn(ActionEvent e) {
                transaction(0, null);
                
                return null;
            }
        };
        
        IntegrationTestHelper.putActionListener(IntegrationTestHelper.AL.addTransactionPrep, addTransactionPrep);
        
        jB = new JButton("Add Transaction");
        jB.addActionListener(addTransactionPrep);
        pan2.add(jB);
        
        jB = new JButton("Adj. Bal. Trans.");
        jB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                transaction(2, null);   
            }
        });
        pan2.add(jB);
        
        jB = new JButton("Unpost Transactions");
        jB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayTrans(sortUnpostTrans(bH.getCurrentCheckBook().
                    getTransactions()));   
            }
        });
        pan2.add(jB);
        
        jB = new JButton("All Transactions");
        jB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayAllTrans();      
            }
        });
        pan2.add(jB);
        
        pan2 = new JPanel();
        pan2.setLayout(new GridLayout(0, 1)); 
        pan.add(pan2);
        
        jB = new JButton("Add Bill");
        jB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bill(0, null);  
            }
        });
        pan2.add(jB);
        
        jB = new JButton("All Bills");
        jB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lastBills = bH.getBills();
                lastAPDisplay = 4;
                displayBills(lastBills);   
            }
        });
        pan2.add(jB);
        
        pan2 = new JPanel();
        pan2.setLayout(new GridLayout(0, 1));
        pan.add(pan2);
        
        jB = new JButton("Sort");
        jB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int c = SwingConstants.CENTER;
                
                sortF = new JFrame(); 
                Container cP = sortF.getContentPane();
                
                JPanel mPan = new JPanel();
                mPan.setLayout(new BoxLayout(mPan, BoxLayout.PAGE_AXIS));
                cP.add(mPan);
                
                JPanel sPan = new JPanel();
                sPan.setLayout(new GridLayout(0, 4));
                mPan.add(sPan);
                
                JLabel lab = new JLabel("Sort G.L.");
                sPan.add(lab);
                lab = new JLabel("");
                sPan.add(lab);
                lab = new JLabel("Written Between");
                sPan.add(lab);
                lab = new JLabel("");
                sPan.add(lab);
                
                setAndLoadGLCodeCB();
                sPan.add(gLCodeCodeCB);
//                gLCodeT = new JTextField();
//                sPan.add(gLCodeT);
                
                JPanel dateTP = new JPanel();
                //Calendar cal = Calendar.getInstance();
                dateMT.setText("01"); 
                dateTP.add(dateMT);
                dateDT.setText("01");
                dateTP.add(dateDT);
                dateYT.setText("2005");
                dateTP.add(dateYT);
                sPan.add(dateTP);
                
                lab = new JLabel("And", c);
                sPan.add(lab);
                
                JPanel clearDateTP = new JPanel();
                Calendar cal2 = Calendar.getInstance();
                clearDateMT.setText("0" + (cal2.get(Calendar.MONTH) 
                    + 1));
                clearDateTP.add(clearDateMT);
                clearDateDT.setText("0" + cal2.get(
                Calendar.DAY_OF_MONTH));
                clearDateTP.add(clearDateDT);
                clearDateYT.setText("" + cal2.get(Calendar.YEAR));
                clearDateTP.add(clearDateYT);
                sPan.add(clearDateTP);
                
                JButton jB = new JButton("G.L. Checks");
                jB.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Calendar cDate = Calendar.getInstance();
                        cDate.set( 
                        new Integer(dateYT.getText()), 
                        (new Integer(dateMT.getText()) - 1), // OK
                        new Integer(dateDT.getText()));
                        
                        Calendar cDateE = Calendar.getInstance();
                        cDateE.set(new Integer(clearDateYT.getText()),
                        new Integer(clearDateMT.getText()) - 1, 
                        new Integer(clearDateDT.getText()));
                        
                        Checks checks = bH.getCurrentCheckBook().getChecks();
                        
                        checks = sortGLChecks(checks,
                                ((JComboBoxItem) gLCodeCodeCB.getSelectedItem()).getGlCode());
                        checks = sortDateChecks(checks, cDate, cDateE);
                        displayChecks(checks);
                        lastAPDisplay = 9;
                        lastChecks = bH.getCurrentCheckBook().getChecks();
                        
                        sortF.setVisible(false);
                        sortF = null;
                    // killAllMinorFrames ??????
                    }
                });
                sPan.add(jB);
                
                jB = new JButton("G.L. Transactions");
                jB.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Calendar cDate = Calendar.getInstance();
                        cDate.set( 
                        new Integer(dateYT.getText()), 
                        (new Integer(dateMT.getText()) - 1), // OK
                        new Integer(dateDT.getText()));
                        
                        Calendar cDateE = Calendar.getInstance();
                        cDateE.set(new Integer(clearDateYT.getText()),
                        new Integer(clearDateMT.getText()) - 1, 
                        new Integer(clearDateDT.getText()));
                        
                        List<Transaction> trans = bH.getCurrentCheckBook().
                            getTransactions();
                        
                        trans = sortGLTrans(trans, 
                            //new Integer(gLCodeT.getText())); 
                                //new Code(((JComboBoxItem) gLCodeCodeCB.getSelectedItem()).getId()));
                                ((JComboBoxItem) gLCodeCodeCB.getSelectedItem()).getGlCode());
                        trans = sortDateTrans(trans, cDate, cDateE); 
                        displayTrans(trans);
                        lastAPDisplay = 10;
                        lastTransactions = bH.getCurrentCheckBook()
                            .getTransactions();
                        sortF.setVisible(false);
                        sortF = null;
                    // killAllMinorFrames ??????
                    }
                });
                sPan.add(jB);
                
                jB = new JButton("G.L. Bills");
                jB.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        
                        List<Bill> bills = bH.getBills();
                        
                        bills = sortGLBills(bills, 
                            //new Integer(gLCodeT.getText())); 
                                ((JComboBoxItem) gLCodeCodeCB.getSelectedItem()).getGlCode());
                        displayBills(bills);
                        //lastBills = bH.getBills();
                        sortF.setVisible(false);
                        sortF = null;
                    // killAllMinorFrames ??????
                    }
                });
                sPan.add(jB);
                
                mPan.add(Box.createVerticalStrut(50));
                
                sPan = new JPanel();
                sPan.setLayout(new BoxLayout(sPan, BoxLayout.PAGE_AXIS));
                //sPan.setBackgroundColor(Color.YELLOW);
                mPan.add(sPan);
                
                lab = new JLabel("Sort By Text");
                sPan.add(lab);
                
                forT.setText("");
                forT.setColumns(30);
                sPan.add(forT);
                
                JPanel pan = new JPanel();
                pan.setLayout(new GridLayout(0, 3));
                sPan.add(pan);
                
                jB = new JButton("Text Checks");
                jB.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Checks sort = sortTextChecks(bH.
                            getCurrentCheckBook().getChecks(), forT.getText());
                        
                        displayChecks(sort);
                    }
                });
                pan.add(jB);
                
                jB = new JButton("Text Transactions");
                jB.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ArrayList sort = sortTextTrans(bH.getCurrentCheckBook().
                            getTransactions(), forT.getText());
                        
                        displayTrans(sort);
                    }
                });
                pan.add(jB);
                
                jB = new JButton("Text Bills");
                jB.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ArrayList sort = sortTextBills(bH.getBills(), 
                            forT.getText());
                        
                        displayBills(sort);
                    }
                });
                pan.add(jB);
                
                sortF.pack();
                sortF.setVisible(true);
                
            }
        });
        pan2.add(jB);
        
        mPan.add(pan);
        this.add(mPan, "Check Book");
        
        mPan = new JPanel();
        mPan.setLayout(new BoxLayout(mPan, BoxLayout.PAGE_AXIS)); 
        
        pan = new JPanel();
        
        jB = new JButton("Add SnapShot");
        jB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                snapShot(0, null);
            }
        });
        pan.add(jB);
        mPan.add(pan);
        
        pan = new JPanel();
        pan.setLayout(new GridLayout(1, 9));
      
        lab = new JLabel("Date");
        pan.add(lab);
        lab = new JLabel("Balance");
        pan.add(lab);
        lab = new JLabel("Unpost Ck");
        pan.add(lab);
        lab = new JLabel("Unpost Dep");
        pan.add(lab);
        lab = new JLabel("Adj. Bal.");
        pan.add(lab);
        lab = new JLabel("Used");
        pan.add(lab);
        lab = new JLabel("Future Bills");
        pan.add(lab);
        lab = new JLabel("Adj. Bal.");
        pan.add(lab);
        lab = new JLabel("Even Week");
        pan.add(lab);
        //lab = new JLabel("Future Bills", c);
        //pan.add(lab);
        
        mPan.add(pan);
        
        pan = new JPanel();
        pan.setLayout(new GridLayout(0, 1));
        
        JScrollPane mScr = new JScrollPane(pan);
        mPan.add(mScr);
        
        int color = 0;
        
        ArrayList shots = (ArrayList) bH.getBHSShots();
        for(int i = 0; i < shots.size(); i++) {
            pan2 = new JPanel();
            pan2.setLayout(new GridLayout(1, 9));
            
            if(color == 0) {
                pan2.setBackground(Color.GREEN);   
            }
            color++;
            if(color == 4) {
                color = 0;   
            }
            
            
            BHSnapShot shot = (BHSnapShot) shots.get(i);
            
            lab = new JLabel(shot.getStr(0));
            pan2.add(lab);
            lab = new JLabel(shot.getStr(1));
            pan2.add(lab);
            lab = new JLabel(shot.getStr(2));
            pan2.add(lab);
            lab = new JLabel(shot.getStr(3));
            pan2.add(lab);
            lab = new JLabel(shot.getStr(4));
            pan2.add(lab);
            lab = new JLabel(shot.getStr(5));
            pan2.add(lab);
            lab = new JLabel(shot.getStr(6));
            pan2.add(lab);
            lab = new JLabel(shot.getStr(7));
            pan2.add(lab);
            lab = new JLabel(shot.getStr(8));
            pan2.add(lab);
            
            pan.add(pan2);
        }
        
        this.add(mPan, "BankHealth");
        
        this.add(setUpGLBreakdown(), "GL Breakdown");
        
        this.setSelectedIndex(tPlace);
    }
    
    /**
     * A method that sets up our gLBreakdown tab.
     * 
     * @return tabPane, our tabbed pane.
     */
    private JTabbedPane setUpGLBreakdown() { 
        
        JTabbedPane tabPane = new JTabbedPane();
        
        tabPane.add("Percent", setUpGLBreakdownPercent());
        tabPane.add("Report", setUpGLBreakdownReport());
        
        return tabPane;
    }
    
    /**
     * A helper method that sets up our gLBreakdown tab with our percents.
     * 
     * @return sPan, our main panel.
     */
    private JScrollPane setUpGLBreakdownPercent() { 
                
        //for the percents
        JPanel mPan = new JPanel();
        mPan.setLayout(new BoxLayout(mPan, BoxLayout.PAGE_AXIS));
        mPan.setPreferredSize(new Dimension (100, 600));
        
        JScrollPane sPane = new JScrollPane(mPan, 
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel pan = new JPanel();
        pan.setLayout(new BorderLayout());
        mPan.add(pan);
        
        
        //Setup gl codes
        final Set<GlCode> gLs = new HashSet<GlCode>();
        final Checks checks = bH.getCurrentCheckBook().getChecks();
        final List<Transaction> trans = bH.getCurrentCheckBook().getTransactions();
        
        for(Check ch : checks){
            gLs.add(ch.getGlCode());
        }
        
        for(int i = 0; i < trans.size(); i++) {
            Transaction tr = (Transaction) trans.get(i);
            if(tr.getGLCode() == null){
                throw new RuntimeException("glCode is null: " + tr);
            }
            gLs.add(tr.getGLCode());
        }
        //Iterator it = gLs.iterator();
        
        final List<GlCode> sortedGLs = getSortedGls(gLs);
        
        
        JPanel pan2 = new JPanel();
        pan2.setLayout(new GridLayout(1, 0));
        pan.add(pan2, BorderLayout.NORTH);
        
        JLabel lab = new JLabel("G.L.s");
        pan2.add(lab);
        
        lab = new JLabel("Months");
        pan2.add(lab);
        
        JButton jB = new JButton("Edit G.L. Names");
        final ReturningActionListener editGl = new ReturningActionListener() {
            @Override
            public Object actionPerformedAndReturn(ActionEvent e) {
                gLF = new JFrame();
                Container cP = gLF.getContentPane();
                
                JPanel mPan = new JPanel();
                mPan.setLayout(new GridLayout(3, 0));
                cP.add(mPan);
                
                JLabel lab = new JLabel("G.L. Code");
                mPan.add(lab);
                lab = new JLabel("G.L. Name");
                mPan.add(lab);
                
                
                
                mPan.add(newGLCodeT);
                
                
                forT.setText("");
                mPan.add(forT);
                
                JButton jB = new JButton("OK");
                final ReturningActionListener okAddGl = new ReturningActionListener() {
                    public Object actionPerformedAndReturn(ActionEvent e) {
                        
                        bH.addGLCode(new GlCode(
                                new Code(new Integer(newGLCodeT.getText())), 
                                forT.getText()));
                        
                        gLF.setVisible(false);
                        gLF = null;
                        
                        setUp();
                        
                        //maybe return the glCode if you need it some day
                        return null;
                    }
                };
                        
                jB.addActionListener(okAddGl);
                
                IntegrationTestHelper.putActionListener(IntegrationTestHelper.AL.okAddGl, okAddGl);
                
                mPan.add(jB);
                
                jB = new JButton("Cancel");
                jB.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        gLF.setVisible(false);
                        gLF = null;
                    }
                });
                mPan.add(jB);
                
                gLF.pack();
                gLF.setVisible(true);
                
                //maybe return something useful some day
                return null;
            }
        };
        
        IntegrationTestHelper.putActionListener(IntegrationTestHelper.AL.editGl, editGl);
        
        jB.addActionListener(editGl);
        pan2.add(jB);
        
        pan2 = new JPanel();
        pan2.setLayout(new BoxLayout(pan2, BoxLayout.PAGE_AXIS));
        pan.add(pan2, BorderLayout.CENTER);
        
        
        JPanel pan3 = new JPanel();
        pan3.setLayout(new GridLayout(0, 14));
        pan2.add(pan3);
        
        lab = new JLabel("");
        pan3.add(lab);
        for(int i = 1; i < 13; i++) {
            lab = new JLabel("" + i);
            pan3.add(lab);
        }
        lab = new JLabel("Adv");
        pan3.add(lab);
        
        int color = 0;
        //not sure if we could just use the gl we have or not, as
        //the getGlCode method adds the gl to a map
        for(final GlCode glCode : sortedGLs) {
            
            pan3 = new JPanel();
            pan3.setLayout(new GridLayout(0, 14));
            if(color == 1) {
                pan3.setBackground(Color.GREEN);
                color = -1;
            }
            color++;
            pan2.add(pan3);
            
            JPanel pan4 = new JPanel(); 
            //Color lBlue = new Color(Color.blue.getRGB() + 10);
            pan4.setBackground(Color.orange);
            pan3.add(pan4);
            
            
            //final GlCode glCode = sortedGLs.get(i);
            final Code code = glCode.getCode();
            final GlCode gL = bH.getGlCode(code);
            lab = new JLabel(
                "" + gL.getCode().getCode() + " : " +  gL.getDescription());
            pan4.add(lab);
            
            for(int month = 1; month < 13; month++) {
                           
                Currency cAmount = Currency.Zero;
                for(Check ch : checks){
                    if(ch.getDate().get(Calendar.MONTH) + 1 == month) {
                        if(ch.getGlCode() == gL) {
                            cAmount = cAmount.add(ch.getAmount());   
                        }
                    }
                }
                Currency tAmount = Currency.Zero;
                Currency depAmount = Currency.Zero;
                for(int j = 0; j < trans.size(); j++) {
                    Transaction tr = (Transaction) trans.get(j);
                    
                    if(tr.getDate().get(Calendar.MONTH) + 1 == month) {
                        if(tr.getGLCode() == gL) {
                            tAmount = tAmount.add(tr.getAmount());   
                        }
                        if(tr.getGLCode().getCode().getCode() == 1) {
                            depAmount = depAmount.add(tr.getAmount());   
                        }
                    }
                }
                Currency total = cAmount.subtract(tAmount);
                
                lab = new JLabel(getPercent(depAmount, total));
                
                
                pan3.add(lab);
            }
            
            Currency cAmount = Currency.Zero;
            for(Check ch : checks){
                if(ch.getGlCode() == gL) {
                    cAmount = cAmount.add(ch.getAmount());
                }
            }
            Currency tAmount = Currency.Zero;
            Currency depAmount = Currency.Zero;
            for(int j = 0; j < trans.size(); j++) {
                Transaction tr = (Transaction) trans.get(j);
                if(tr.getGLCode() == gL) {
                    tAmount = tAmount.add(tr.getAmount());   
                }
                if(tr.getGLCode().getCode().getCode() == 1) {
                    depAmount = depAmount.add(tr.getAmount());   
                }
            }
            Currency total = cAmount.subtract(tAmount);
            lab = new JLabel(getPercent(depAmount, total));//"" + Math2.round((total.divide(depAmount)).multiply(100)));
            pan3.add(lab);
        }
        
        return sPane;  
        
    }
    
    /**
     * A helper method that sets up our gLBreakdown tab with our Report.
     * 
     * @return sPan, our main panel.
     */
    private JScrollPane setUpGLBreakdownReport() { 
                
        JPanel mPan = new JPanel();
        mPan.setLayout(new BoxLayout(mPan, BoxLayout.PAGE_AXIS));
        mPan.setPreferredSize(new Dimension (100, 600));
        
        JScrollPane sPane = new JScrollPane(mPan, 
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel pan = new JPanel();
        pan.setLayout(new BorderLayout());
        mPan.add(pan);
        
        
        //Setup gl codes
        final HashSet<GlCode> gLs = new HashSet<GlCode>();
        
        final Checks checks = bH.getCurrentCheckBook().getChecks();
        final List<Transaction> trans = bH.getCurrentCheckBook().getTransactions();
        
        for(Check ch : checks){
            gLs.add(ch.getGlCode());
        }
        
        for(int i = 0; i < trans.size(); i++) {
            Transaction tr = (Transaction) trans.get(i);
            gLs.add(tr.getGLCode());
        }
        
        final List<GlCode> sortedGLs = getSortedGls(gLs);
        
        
        //lables on top
        JPanel pan2 = new JPanel();
        pan2.setLayout(new GridLayout(1, 0));
        pan.add(pan2, BorderLayout.NORTH);
        
        JLabel lab = new JLabel("G.L.");
        pan2.add(lab);
        
        lab = new JLabel("Amount");
        pan2.add(lab);
        
        lab = new JLabel("Per Month");
        pan2.add(lab);
        
        lab = new JLabel("What other");
        pan2.add(lab);
        lab = new JLabel("info");
        pan2.add(lab);
        lab = new JLabel("do you");
        pan2.add(lab);
        lab = new JLabel("want");
        pan2.add(lab);
        lab = new JLabel("here?");
        pan2.add(lab);
        
        pan2 = new JPanel();
        pan2.setLayout(new BoxLayout(pan2, BoxLayout.PAGE_AXIS));
        pan.add(pan2, BorderLayout.CENTER);
        
        
        JPanel pan3; // = new JPanel();
        
        //This loop is just to set our latestMonth so that we aren't doing it over and over in
        //on of the loops below. The purpose of latestMonth is to set our 
        //Aprox. Month Adv. 
        int latestMonth = 0; //January. 
        for(int i = 0; i < trans.size(); i++) {
            Transaction tr = (Transaction) trans.get(i);
            if(tr.getDate().get(Calendar.MONTH) > latestMonth) {
                latestMonth = tr.getDate().get(Calendar.MONTH);   
            }
        }
        
        //for integration testing:
        IntegrationTestHelper.glTotals.clear();
        
        int color = 0;
        for(final GlCode glFromSorted : sortedGLs) {
            
            pan3 = new JPanel();
            pan3.setLayout(new GridLayout(0, 8));
            if(color == 1) {
                pan3.setBackground(Color.GREEN);
                color = -1;
            }
            color++;
            pan2.add(pan3);
            
            //GL:
            JPanel gLP = new JPanel();
            //getting the glCode from the bH should work, but it looks like
            //there is another bug in loading glCodes for transactions and/or
            //checks. So to track down that issue faster, we are just going to 
            //use the glCode we have, which might be the better thing to do anyway. 
            //In the future (once the other bug(s) are fixed, we could go back
            //to the way we were going things and 
            //throw an exception if the returned glCode is null)
            //final GlCode gL = bH.getGlCode(glFromSorted.getCode());
            final GlCode gL = glFromSorted;
            
            lab = new JLabel(
                "" + gL.getCode().getCode() + " : " +  gL.getDescription());
            gLP.add(lab);
            gLP.setBackground(Color.ORANGE);
            pan3.add(gLP);
         
            // Total: Aprox. Month Adv.:
            
            Currency cAmount = Currency.Zero;
            for(Check ch : checks){
                if(ch.getGlCode().equals(gL)) {
                    cAmount = cAmount.add(ch.getAmount());
                }
            }
            Currency tAmount = Currency.Zero;
            //Currency depAmount = Currency.Zero;
            for(int j = 0; j < trans.size(); j++) {
                Transaction tr = (Transaction) trans.get(j);
                if(tr.getGLCode().equals(gL)) {
                    tAmount = tAmount.add(tr.getAmount());   
                }
            }
            //total answer
            Currency total = cAmount.subtract(tAmount);
            lab = new JLabel("" + Math2.round(total));
            pan3.add(lab);
            //aprox month adv answer
            lab = new JLabel("" + Math2.round(total.divide(latestMonth + 1)));
            pan3.add(lab);
            
            //for integration testing:
            IntegrationTestHelper.glTotals.put(gL.getCode().getCode(), total);
        }
            
        
        return sPane;  
        
    }
    
    /**
     * A method that handles our snapshots....
     * 
     * @param index, 0 for add, 1 for...
     * @param snapShot, a snapshot to be messed with according to our index.
     */
    public void snapShot(int index, BHSnapShot snapShot) {
        killMinorFs();
        
        snapShotF = new JFrame();
        Container cP = snapShotF.getContentPane();
        JPanel mPan = new JPanel();
        mPan.setLayout(new BoxLayout(mPan, BoxLayout.PAGE_AXIS)); 
        cP.add(mPan);
        
        JPanel pan = new JPanel();
        pan.setLayout(new GridLayout(0, 2));
        mPan.add(pan);
        
        JLabel lab = new JLabel("Days Forward");
        pan.add(lab);
        
        lab = new JLabel("Daily Adv.");
        pan.add(lab);
        
        daysForwardT.setText("14");
        pan.add(daysForwardT);
        
        if(standAlone == false) {
            dailyAdvT.setText("750");
        }
        else{
            dailyAdvT.setText("33");   
        }
        pan.add(dailyAdvT);
        
        JButton jB = new JButton("Generate");
        jB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bH.addBHSS(new Integer(daysForwardT.getText()), 
                    new Currency(dailyAdvT.getText()));
                
                snapShotF.setVisible(false);
                snapShotF = null;
                
                setUp();
            }
        });
        pan.add(jB);
        
        jB = new JButton("Cancel");
        jB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                snapShotF.setVisible(false);
                snapShotF = null;
                
                //setUp();
            }
        });
        pan.add(jB);
        
        snapShotF.pack();
        snapShotF.setVisible(true);
    }
    
    /**
     * A methot that sorts by date.
     * 
     * @param checks, the checks to sort by.
     * @param sDate, the start date.
     * @param eDate, the end date.
     * 
     * @return sorted, our sorted checks.
     */
    private Checks sortDateChecks(Checks checks, Calendar sDate, 
        Calendar eDate) {
        
        lastCDate = sDate;
        lastCDateE = eDate;
        lastChecks = checks;
        lastAPDisplay = 6;
        
        return checks.getDateChecks(sDate, eDate);
    }
    
    /**
     * A method that combines our sortDateChecks and sortGLChecks...
     * 
     * @param checks, to be sorted.
     * @param gL, our gLCode.
     * @param cDate, the start date.
     * @param cDateE, the end date.
     * 
     * @return sorted, our sorted list.
     */
    public Checks sortDateGLChecks(Checks checks, GlCode gL, Calendar cDate,
        Calendar cDateE) {
        
        Checks sorted = sortDateChecks(sortGLChecks(checks, gL), cDate, 
        cDateE);
        
        lastAPDisplay = 9;
        lastChecks = checks;
        
        return sorted;
    }
    
    /**
     * A method that combines our sortDateTranss and sortGLTrans...
     * 
     * @param trans, to be sorted.
     * @param gL, our gLCode.
     * @param cDate, the start date.
     * @param cDateE, the end date.
     * 
     * @return sorted, our sorted list.
     */
    public ArrayList sortDateGLTrans(List<Transaction> trans, GlCode gL, Calendar cDate,
        Calendar cDateE) {
        
        ArrayList sorted = sortDateTrans(sortGLTrans(trans, gL), cDate, 
        cDateE);
        
        lastAPDisplay = 10;
        lastTransactions = trans;
        
        return sorted;
    }
    
    /**
     * A methot that sorts by date.
     * 
     * @param trans, the trans to sort by.
     * @param sDate, the start date.
     * @param eDate, the end date.
     * 
     * @return sorted, our sorted trans.
     */
    private ArrayList sortDateTrans(List<Transaction> trans, Calendar sDate, 
        Calendar eDate) {
            
        lastCDate = sDate;
        lastCDateE = eDate;
        lastTransactions = trans;
        lastAPDisplay = 7; 
        ArrayList sorted = new ArrayList();
        
        for(int i = 0; i < trans.size(); i++) {
            Transaction tran = (Transaction) trans.get(i);
            Calendar date = tran.getDate();
            
            if(sDate.before(date) && eDate.after(date)) {
                sorted.add(tran);         
                //MiscStuff.writeToLog("sort1");
            }
            else {
                int sDay = sDate.get(Calendar.DAY_OF_YEAR);
                int sYear = sDate.get(Calendar.YEAR);
                int eDay = eDate.get(Calendar.DAY_OF_YEAR);
                int eYear = eDate.get(Calendar.YEAR);
                int day = date.get(Calendar.DAY_OF_YEAR);
                int year = date.get(Calendar.YEAR);
                
                if(sDay == day && sYear == year) {
                    sorted.add(tran);   
                  //  MiscStuff.writeToLog("sort2");
                }
                else {
                    if(eDay == day && eYear == year) {
                        sorted.add(tran);   
                    //    MiscStuff.writeToLog("sort3");
                    }
                }
            }
        }
        
        return sorted;
    }
    
    /**
     * Our method that sorts us all our bills by G.L. Codes
     * 
     * @param bills, the bills to sort.
     * @param gL, the glcode to sort by.
     * 
     * @return sorted, the sorted bills.
     */
    public ArrayList sortGLBills(List<Bill> bills, GlCode gL) {
        lastGL = gL;
        lastAPDisplay = 8;
        lastBills = bills;
        
        ArrayList sorted = new ArrayList();
        
        for(int in = 0; in < bills.size(); in++) {
            Bill bill = (Bill) bills.get(in);
            if(bill.getGLCode().equals(gL)) {
                sorted.add(bill);
            }
        } 
        return sorted;    
    }
    
    /**
     * Our actionPerformed method that shows us all our checks by G.L. Codes
     * 
     * @param checks, the checks to sort.
     * @param gL, the glcode to sort by.
     * 
     * @return sorted, the sorted checks.
     */
    public Checks sortGLChecks(Checks checks, GlCode gL) {
        lastGL = gL;
        lastAPDisplay = 5;
        lastChecks = checks;
        
        return checks.getGlChecks(gL);
    }
    
    /**
     * Our method that sorts us all our transactions by G.L. Codes
     * 
     * @param trans, the transactions to sort.
     * @param gL, the glcode to sort by.
     * 
     * @return sorted, the sorted transactions.
     */
    public ArrayList sortGLTrans(List<Transaction> trans, GlCode gL) {
        lastGL = gL;
        lastAPDisplay = 7;
        lastTransactions = trans;
        
        ArrayList sorted = new ArrayList();
        
        for(int in = 0; in < trans.size(); in++) {
            Transaction tran = (Transaction) trans.get(in);
            if(tran.getGLCode().equals(gL)) {
                sorted.add(tran);
            }
        }    
        return sorted;    
    }
    
    /**
     * A method that sorts our bills by text.
     * 
     *@param bills, the bills to be sorted.
     *@param text, the text to sort by.
     *
     *@return sorted, our sorted bills.
     */
    public ArrayList sortTextBills(List<Bill> bills, String text) {
        lastAPDisplay = 13;
        lastBills = bills;
        lastText = text;
        
        ArrayList sorted = new ArrayList();
        
        for(int i = 0; i < bills.size(); i++) {
            Bill bi = (Bill) bills.get(i);
            String text2 = "";
            
            
            text2 += " " + bi.getAmount() + " " + bi.getDueDate() + " " + 
                bi.getDescription() + " " + bi.getLastMonthPaid() + " " + 
                bi.getRecurrenceCode();
            
            text = text.toLowerCase();
            text2 = text2.toLowerCase();
            
            if(text2.contains(text)) {
                sorted.add(bi);   
            }
        }
        return sorted;
    }
    
    /**
     * A method that sorts our checks by text.
     * 
     *@param checks, the checks to be sorted.
     *@param text, the text to sort by.
     *
     *@return sorted, our sorted checks.
     */
    public Checks sortTextChecks(Checks checks, String text) {
        lastAPDisplay = 11;
        lastChecks = checks;
        lastText = text;
        
        return checks.getTextChecks(text);
    }
    
    /**
     * A method that sorts our trans by text.
     * 
     *@param trans, the transactions to be sorted.
     *@param text, the text to sort by.
     *
     *@return sorted, our sorted transactions.
     */
    public ArrayList sortTextTrans(List<Transaction> trans, String text) {
        lastAPDisplay = 12;
        lastTransactions = trans;
        lastText = text;
        
        ArrayList sorted = new ArrayList();
        
        for(int i = 0; i < trans.size(); i++) {
            Transaction tr = (Transaction) trans.get(i);
            String text2 = "";
            
            text2 += " " + tr.getAmount() + " " + tr.getDateS() + " " + 
                tr.getDescription();
            
            text = text.toLowerCase();
            text2 = text2.toLowerCase();
            
            if(text2.contains(text)) {
                sorted.add(tr);   
            }
        }
        return sorted;
    }
    
    /**
     * Our method that sorts our unposted checks.
     * 
     * @param checks, the checks we are to sort through.
     * 
     * @return unpostChecks, the unposted checks.
     */
    public Checks sortUnpostChecks(Checks checks) {
        lastChecks = checks;
        lastAPDisplay = 0;
        
        return checks.getUnpostedChecks();
    }
    
    /**
     * Out method that sorts out all the unposted transactions.
     * 
     * @param transactons, the transactions to sort through.
     * 
     * @return unpostTransactions, our unposted transactions.
     */
    public ArrayList sortUnpostTrans(List<Transaction> transactions) {
        lastTransactions = transactions;
        lastAPDisplay = 2;  
        
        ArrayList unpostTransactions = new ArrayList();
        
        for(int in = 0; in < transactions.size(); in++) {
            Transaction trans = (Transaction) transactions.get(in); 
            
            if(!trans.getGoneThrough()) {
                unpostTransactions.add(trans);
            }
        }
        return unpostTransactions;
    }
    
    /**
     * A method that handles our transactions...
     * 
     * @param operation, 0 for add Transaction, 1 for a replace (must send a 
     *      transaction not a null with), 2 for adjusting balance trans, 3...
     * @param transaction, a transaction to be replaced, deleted, edited
     * ect. acorcing to the operation...
     */
    public void transaction(int operation, Transaction transaction) {
        killMinorFs();
        
        int c = SwingConstants.CENTER;
        currentTrans = transaction;
        
        transF = new JFrame();
        JPanel mainP = new JPanel();
        JPanel pan = new JPanel();
        transF.getContentPane().add(mainP);
        
        mainP.setLayout(new BoxLayout(mainP, BoxLayout.PAGE_AXIS));  
        pan.setLayout(new GridLayout(1, 5));
        
        JLabel lab = new JLabel("Date", c);
        pan.add(lab);
        lab = new JLabel("Amount", c);
        pan.add(lab);
        lab = new JLabel("G.L. Code", c);
        pan.add(lab);
        lab = new JLabel("For", c);
        pan.add(lab);
        lab = new JLabel("Cleared", c);
        pan.add(lab);
        
        mainP.add(pan);
        
        switch(operation) {
            case 0: 
                pan = new JPanel();
                pan.setLayout(new GridLayout(1, 5));
                
                JPanel dateTP = new JPanel();
                Calendar cal = Calendar.getInstance();
                dateMT.setText("" + (cal.get(Calendar.MONTH) + 1)); 
                dateTP.add(dateMT);
                dateDT.setText("" + cal.get(Calendar.DAY_OF_MONTH));
                dateTP.add(dateDT);
                dateYT.setText("" + cal.get(Calendar.YEAR));
                dateTP.add(dateYT);
                pan.add(dateTP);
               
                amountT.setText("");
                pan.add(amountT);
                
                setAndLoadGLCodeCB();
                pan.add(gLCodeCodeCB);
//                gLCodeT = new JTextField("0");
//                pan.add(gLCodeT);
                
                forT.setText(" ");
                pan.add(forT);
                cashedCB = new JComboBox(new String[]{"Yes", "No"});
                pan.add(cashedCB);
                
                mainP.add(pan);
                
                pan = new JPanel();
                
                
                final ReturningActionListener addTransaction = new ReturningActionListener() {
                    @Override
                    public Transaction actionPerformedAndReturn(ActionEvent e) {
                        boolean goneThrough = true;
                        if(cashedCB.getSelectedItem().equals("No")) {
                            goneThrough = false;   
                        }
                        Calendar cDate = Calendar.getInstance();
                        cDate.set(new Integer(dateYT.getText()), 
                        new Integer((dateMT.getText())) - 1, 
                        new Integer(dateDT.getText()));
                        
                        final Transaction transaction = new Transaction(
                            new Currency(amountT.getText()), 
                            forT.getText(), cDate, //new Integer(gLCodeT.getText()),
                            //bH.getGlCode(new Code(((JComboBoxItem) gLCodeCodeCB.getSelectedItem()).getId())),
                            ((JComboBoxItem) gLCodeCodeCB.getSelectedItem()).getGlCode(),
                            goneThrough);
                        
                        bH.getCurrentCheckBook().addTransaction(transaction);
                        
                        transF.setVisible(false);
                        transF = null;
                        
                        setUp();
                        
                        return transaction;
                    }
                };
                
                IntegrationTestHelper.putActionListener(IntegrationTestHelper.AL.addTransaction, addTransaction);
                
                JButton addB = new JButton("Add Transaction");
                addB.addActionListener(addTransaction);              
                pan.add(addB);
                
                JButton cancel = new JButton("Cancel");
                cancel.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        transF.setVisible(false);
                        transF = null;
                    }
                });
                pan.add(cancel);
                
                mainP.add(pan);
                
                transF.pack();
                transF.setVisible(true);
                //forT.requestFocus();
                break;
                
            case 1:

                pan = new JPanel();
                pan.setLayout(new GridLayout(1, 5));
               
                cal = transaction.getDate();
                dateTP = new JPanel();
                dateMT.setText("" + (cal.get(Calendar.MONTH) + 1)); 
                dateTP.add(dateMT);
                dateDT.setText("" + cal.get(Calendar.DAY_OF_MONTH));
                dateTP.add(dateDT);
                dateYT.setText("" + cal.get(Calendar.YEAR));
                dateTP.add(dateYT);
                pan.add(dateTP);
               
                amountT.setText("" + transaction.getAmount());
                pan.add(amountT);
                
                setSelectedGLCodeCB(transaction.getGLCode());
                pan.add(gLCodeCodeCB);
//                gLCodeT = new JTextField("" + transaction.getGLCode());
//                pan.add(gLCodeT);
                
                forT.setText(transaction.getDescription());
                pan.add(forT);
                
                cashedCB = new JComboBox(new String[]{"No", "Yes"});
                if(transaction.getGoneThrough() == true) {
                    cashedCB.setSelectedIndex(1);   
                }
                pan.add(cashedCB);
                
                mainP.add(pan);
                
                pan = new JPanel();
                JButton replace = new JButton("Replace");
                replace.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
  
                        CheckBook cBook = bH.getCurrentCheckBook();
                        cBook.removeTransaction(currentTrans);
                        
                        boolean goneThrough = false;
                        if(cashedCB.getSelectedItem().equals("Yes")) {
                            goneThrough = true;   
                        }
                        Calendar cDate = Calendar.getInstance();
                        cDate.set(new Integer(dateYT.getText()), 
                        new Integer((dateMT.getText())) - 1, 
                        new Integer(dateDT.getText()));
                        
                        cBook.addTransaction(
                        new Transaction(
                        new Currency(amountT.getText()), 
                        forT.getText(), cDate, //new Integer(gLCodeT.getText()),
                        //bH.getGlCode(new Code( ((JComboBoxItem) gLCodeCodeCB.getSelectedItem()).getId())),
                        ((JComboBoxItem) gLCodeCodeCB.getSelectedItem()).getGlCode(),
                        goneThrough));
                        
                        transF.setVisible(false);
                        transF = null;
                        
                        setUp();
                        redisplayAP();
                        
                    }
                });
                pan.add(replace);
                
                JButton delete = new JButton("Delete");
                delete.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        CheckBook cBook = bH.getCurrentCheckBook();
                        cBook.removeTransaction(currentTrans);
                        
                        transF.setVisible(false);
                        transF = null;
                        
                        setUp();
                        redisplayAP();
                        
                    }
                });
                pan.add(delete);
                
                cancel = new JButton("Cancel");
                cancel.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        
                        transF.setVisible(false);
                        transF = null;
                        
                        redisplayAP();
                    }
                });
                pan.add(cancel);
                
                mainP.add(pan);
                
                transactionsF.setVisible(false);
                transactionsF = null;
                
                transF.pack();
                transF.setVisible(true);
                break;
                
            case 2:    
                mainP.removeAll();
                
                pan = new JPanel();
                pan.setLayout(new GridLayout(1, 4));
                
                lab = new JLabel("Date", c);
                pan.add(lab);
                lab = new JLabel("New Balance", c);
                pan.add(lab);
                lab = new JLabel("G.L. Code", c);
                pan.add(lab);
                lab = new JLabel("For", c);
                pan.add(lab);
                
                mainP.add(pan);
                
                pan = new JPanel();
                pan.setLayout(new GridLayout(1, 4));
                
                dateTP = new JPanel();
                cal = Calendar.getInstance();
                dateMT.setText("" + (cal.get(Calendar.MONTH) + 1)); 
                dateTP.add(dateMT);
                dateDT.setText("" + cal.get(Calendar.DAY_OF_MONTH));
                dateTP.add(dateDT);
                dateYT.setText("" + cal.get(Calendar.YEAR));
                dateTP.add(dateYT);
                pan.add(dateTP);
               
                amountT.setText("");
                pan.add(amountT);
                
                setAndLoadGLCodeCB();
                pan.add(gLCodeCodeCB);
//                gLCodeT = new JTextField("0");
//                pan.add(gLCodeT);
                
                
                forT.setText("Adjusting Trans.");
                pan.add(forT);
                
                mainP.add(pan);
                
                pan = new JPanel();
                
                addB = new JButton("Adjust Balance");
                addB.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        
                        Currency bal = bH.getCurrentCheckBook().getDouble(0);
                        Currency dif = new Currency(amountT.getText()).subtract(bal);
                        
                        Calendar cDate = Calendar.getInstance();
                        cDate.set(new Integer(dateYT.getText()), 
                        new Integer((dateMT.getText())) - 1, 
                        new Integer(dateDT.getText()));
                        
                        bH.getCurrentCheckBook().addTransaction(
                        new Transaction(
                            dif, forT.getText(), cDate, 
                            //new Integer(gLCodeT.getText()),
                            //bH.getGlCode(new Code(((JComboBoxItem) gLCodeCodeCB.getSelectedItem()).getId())),
                            ((JComboBoxItem) gLCodeCodeCB.getSelectedItem()).getGlCode(),
                            true));
                        
                        transF.setVisible(false);
                        transF = null;
                        
                        setUp();
                        
                        //MiscStuff.writeToLog(((ArrayList) 
                          //  bH.getCurrentCheckBook().getChecks()).size());
                    }
                });              
                pan.add(addB);
                
                cancel = new JButton("Cancel");
                cancel.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        transF.setVisible(false);
                        transF = null;
                    }
                });
                pan.add(cancel);
                
                mainP.add(pan);
                
                transF.pack();
                transF.setVisible(true);
                break;
        }
        amountT.requestFocus();
    }

    private List<GlCode> getSortedGls(Set<GlCode> gLs) {
        List<GlCode> sortedGls = new ArrayList<GlCode>(gLs);
        
        {
            Comparator<GlCode> glSorter = new Comparator<GlCode>(){
                @Override
                public int compare(GlCode gl1, GlCode gl2){
                    //we once got a npe here:
                    //it was from a transaction not having a gl code - hunting up the stack for root cause.
//                    if(gl1 == null || gl2 == null){
//                        throw new RuntimeException("One glCode is null? gl1: " + gl1 + ", gl2: " + gl2);
//                    }
                    return gl1.getId() - gl2.getId();
                }
            };
            Collections.sort(sortedGls, glSorter);
        }
        
        return sortedGls;
    }
    
    private String getPercent(Currency depAmount, Currency total){
        
        if(depAmount.equals(Currency.Zero)){
            return "N/A";
        }else{
            return "" + Math2.round((total.divide(depAmount)).multiply(100));
        }
    }
    
    public static class IntegrationTestHelper{
       
        private final static long timeToWait = 1000;
        
        private static final Map<AL, ReturningActionListener> actionListeners = 
                new ConcurrentHashMap<AL, ReturningActionListener>();
        //helps us test gl breakdowns:
        private static final Map<Integer, Currency> glTotals = new ConcurrentHashMap<Integer, Currency>();

        
        
        public static enum AL{
            editGl, okAddGl, addCheck, addCheckPrep, addTransactionPrep, addTransaction;
        }
        
        private static ReturningActionListener getActionListener_thisIsABadIdeaAddThemToTheClassLikeAllChecks(AL key){
            return actionListeners.get(key);
        }
        
        private static void putActionListener(AL key, ReturningActionListener aL){
            actionListeners.put(key, aL);
        }
        
        public static Transaction addTransaction(final String amount, final String forS, final int glCode){
            getActionListener_thisIsABadIdeaAddThemToTheClassLikeAllChecks(AL.addTransactionPrep).actionPerformed(null);
            
            final BHTPane _instance = instance.get();
            _instance.amountT.setText(amount); 
            _instance.forT.setText(forS);
            _instance.setSelectedGLCodeCB(glCode);
            
            return (Transaction) getActionListener_thisIsABadIdeaAddThemToTheClassLikeAllChecks(AL.addTransaction).actionPerformedAndReturn(null);
        }
        
        public static Check addCheck(final String year, final String month, final String day,
                final String clearYear, final String clearMonth, final String clearDay,
                final String checkNum, final String amount, final String forText, final String payTo,
                final String glCode){
            
            final BHTPane _instance = instance.get();
        
            getActionListener_thisIsABadIdeaAddThemToTheClassLikeAllChecks(AL.addCheckPrep).actionPerformed(null);


            //leave cashedCB as "No" for now

            _instance.dateYT.setText(year);
            _instance.dateMT.setText(month);
            _instance.dateDT.setText(day);

            _instance.clearDateYT.setText(clearYear);
            _instance.clearDateMT.setText(clearMonth);
            _instance.clearDateDT.setText(clearDay);

            _instance.checkNumT.setText(checkNum);
            _instance.amountT.setText(amount);

            _instance.setSelectedGLCodeCB(Integer.parseInt(glCode));

            _instance.payToT.setText(payTo); 
            _instance.forT.setText(forText);  


            return (Check) getActionListener_thisIsABadIdeaAddThemToTheClassLikeAllChecks(AL.addCheck).actionPerformedAndReturn(null);
            
        }
        
        public static void addGlCode(final String glCode, final String forText){
            
            getActionListener_thisIsABadIdeaAddThemToTheClassLikeAllChecks(AL.editGl).actionPerformed(null);
            
            final BHTPane _instance = instance.get();
            _instance.newGLCodeT.setText(glCode); 
            _instance.forT.setText(forText);

            getActionListener_thisIsABadIdeaAddThemToTheClassLikeAllChecks(AL.okAddGl).actionPerformed(null);
        }
        
        public static void verifyAllChecksAreDisplayedInOrder(int expectedNumberOfChecks){
            
            final BHTPane _instance = instance.get();
            
            for(int i = 1; i < 50; ++i){
                if(_instance.bH.getCurrentCheckBook().getChecks().size() != expectedNumberOfChecks){
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                    }
                }else{
                    System.out.println("needed " + i + " iterations to get the right state - you have a concurency bug!");
                    break;
                }
            }
            if(_instance.bH.getCurrentCheckBook().getChecks().size() != expectedNumberOfChecks){
                throw new RuntimeException("you need to fix the concurency bug...");
            }
            
            final ReturningActionListener allChecksAction = _instance.allChecksActionListener;
            
            JFrame checksF = (JFrame) allChecksAction.actionPerformedAndReturn(new ActionEvent(_instance, 0, ""));
            ChecksP checksP = (ChecksP) checksF.getContentPane().getComponent(0);
            List<CheckB> checkBs = checksP.getCheckBs();
            
            //panel has 7 components, the first two and the last can be ignored
            Check lastCheck = null;
            for(CheckB checkB : checkBs){
                Check check = checkB.getCheck();
                int checkNum = check.getCheckNum();
                Calendar date = check.getDate();
                
                if(lastCheck != null){
                    boolean datesAreEqual = lastCheck.getDate().equals(check.getDate())
                            || lastCheck.getDateS().equals(check.getDateS());
                    
                    if(lastCheck.getDate().before(date) && !datesAreEqual) {
                        throw new RuntimeException("checks aren't in order by date! The last check was " + lastCheck + ", and this check is " + check);
                    }else if(datesAreEqual && lastCheck.getCheckNum() < checkNum){
                        throw new RuntimeException("checks aren't in order by date/number! The last check was " + lastCheck + ", and this check is " + check);
                    }
                    lastCheck = check;
                }
                
                //the if block before this should be exactly the same as this one with the exception of having more
                //specific error messages
                if((lastCheck == null)
                        || (lastCheck.getDate().after(date)) 
                        || (lastCheck.getDate().equals(date) && lastCheck.getCheckNum() >= checkNum)){
                    lastCheck = check;
                }else{
                    throw new RuntimeException("the if block up above should have thrown an exception, what happened?");
                }
            }
            
            Assert.assertEquals(_instance.bH.getCurrentCheckBook().getChecks().size(), checkBs.size());
        }
        
        public static void verifyGlAmount(GlCode glCode, String targetAmount) {
            
            verifyGlAmount(glCode.getCode().getCode(), targetAmount);
        }
        
        public static void verifyGlAmount(Integer glCode, String targetAmount) {
            
            //this will just test what was last displayed, see GuiIntegrationTest for 
            //some code that checks the db
            
            //this assumes that a setUp() call has been made in the not too distant future
            //and assumes that glCode exists in the map
            
            final Currency value = glTotals.get(glCode);
            final Currency targetValue = new Currency(targetAmount);
            
            if(!value.equals(targetValue)){
                throw new RuntimeException(String.format("Assert failed: %s != %s, glCode: %s, totals: %s", 
                        value, targetValue, glCode, glTotals));
            }
        }
    }
    
    
    
}

