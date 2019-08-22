
package AMP2.BankStuff;

/**
 * Write a description of class BankHealth here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import AMP2.BankStuff.GlCode.Code;
import java.util.*;
import java.io.*;

public class BankHealth implements Serializable
{
    private static final Currency NumberOfDaysOpenPerWeek = new Currency(6);
    private CheckBook checkBook;
    private List<Bill> bills;
    private List<BHSnapShot> bHSShots = new ArrayList<BHSnapShot>();
    private List futureBills = new ArrayList();
    private List<CheckBook> checkBooks = new ArrayList<CheckBook>();
    //codes to glCodes
    private Map<Code, GlCode> gLCodes; 
    
    private String today;
    
    /**
     * Constructor for objects of class BankHealth
     */
    public BankHealth() {
        this(new CheckBook(), new ArrayList<Bill>(), new HashMap<Integer, String>());
    }
    
    /**
     * Constructor for objects of class BankHealth
     */
    public BankHealth(CheckBook checkBook, List<Bill> bills, Map<Integer, String> gLCodes) {
        this(new CheckBook(), new ArrayList(), new HashMap<Code, GlCode>(), new ArrayList<BHSnapShot>());
    }
    
    /**
     * Constructor for objects of class BankHealth
     */
    public BankHealth(CheckBook checkBook, List<Bill> bills, Map<Code, GlCode> gLCodes, 
        List<BHSnapShot> bHSShots) {
        
        setCurrentCheckBook(checkBook);
        
        this.bills = bills;
        this.gLCodes = gLCodes;
        this.bHSShots = bHSShots;
        
        setUp();
    }
    
    /**
     * A method that adds todays bankHealthSnapShot.
     * 
     * @param as used in getBHSnapShot(int daysForward, double weeklyAdv)
     */
    public void addBHSS(int daysForward, Currency dailyAdv) {
        bHSShots.add(0, getBHSnapShot(daysForward, dailyAdv));
        
        if(bHSShots.size() == 90) {
            bHSShots.remove(89);   
        }
    }
    
    /**
     * A method that adds a bill.
     * 
     * @param bill, a new bill to be added.
     */
    public void addBill(Bill bill) {
        
        int date1 = bill.getDueDate();
        boolean added = false;
        
        for(int i = 0; i < bills.size(); i++) {
            Bill bill2 = (Bill) bills.get(i);
            int date2 = bill2.getDueDate();
            
            if(date1 <= date2) {
                bills.add(i, bill);
                added = true;
                break;
            }
        }
        if(!added) {
            bills.add(bill);
        }
        setUp();
    }
    
    /**
     * A method that gets our BHSnapShot.
     * 
     * @param daysForward, the amount of days forward to look for future bills.
     * @param weeklyAdv, the amount to figure how many weeks it will take to 
     *      pay our futureBills
     * 
     * @return snapShot, our BHSnapShot.
     */
    public BHSnapShot getBHSnapShot(int daysForward, Currency dailyAdv) {
        setUp();
        
        Currency weeklyAdv = dailyAdv.multiply(NumberOfDaysOpenPerWeek);
        Currency futureBillsA = getFBAmount(daysForward); 
        Currency futureBalance = checkBook.getDouble(3).subtract(futureBillsA);
        
        Calendar cal = Calendar.getInstance();
        today = (cal.get(Calendar.MONTH) + 1) + " " + 
            cal.get(Calendar.DAY_OF_MONTH) + " " + 
            "0" + (cal.get(Calendar.YEAR) - 2000);
        
        return new BHSnapShot(today, 
            //Math2.formatR(checkBook.getDouble(0)), 
                checkBook.getDouble(0).twoDecFormat(),
            //Math2.formatR(checkBook.getDouble(1)),
                checkBook.getDouble(1).twoDecFormat(),
            //Math2.formatR(checkBook.getDouble(2)),
                checkBook.getDouble(2).twoDecFormat(),
            //Math2.formatR(checkBook.getDouble(3)), 
                checkBook.getDouble(3).twoDecFormat(),
            //Math2.formatR(checkBook.getDouble(1) / (checkBook.getDouble(0) + 
            //checkBook.getDouble(2))),
                checkBook.getDouble(1).divide(checkBook.getDouble(0).add(checkBook.getDouble(2))).twoDecFormat(),
//            Math2.formatR(futureBillsA),
                futureBillsA.twoDecFormat(),
//            Math2.formatR(futureBalance),
                futureBalance.twoDecFormat(),
//            Math2.formatR(futureBalance / weeklyAdv), 
                futureBalance.divide(weeklyAdv).twoDecFormat());
            //futureBills);   
    }
    
    /**
     * A method that gets our bHSShots.
     * 
     * @return bHSShots, our bHSShots.
     */
    public List<BHSnapShot> getBHSShots() {
        return bHSShots;   
    }
    
    /**
     * A method that gets our bills.
     * 
     * @return bills, our bills.
     */
    public List<Bill> getBills() {
        return bills;   
    }
    
    /**
     * A method that gets our checkBook.
     * 
     * @return checkBook, our checkBook.
     */
    public CheckBook getCurrentCheckBook() {
        return checkBook;   
    }
    
    /**
     * A method that gets our checkBooks.
     * 
     * @return checkBooks, our checkbooks.
     */
    public List<CheckBook> getCheckBooks() {
        return checkBooks;      
    }
    
    /**
     * A method that gets future bill amount based on the amount of 
     * days forward we look.
     * 
     * @param daysForward, the amount of days forward we will look.
     * 
     * @return the amount of future bills based on our daysForward.
     */
    public Currency getFBAmount(int daysForward) {
        setUp();
        
        Calendar cal = Calendar.getInstance();
        //int today = cal.get(Calendar.DAY_OF_MONTH);
//        cal.roll(Calendar.DAY_OF_YEAR, daysForward);
  //      int future = cal.get(Calendar.DAY_OF_MONTH);
        Currency amount = Currency.Zero;
        
        ArrayList dates = new ArrayList();
            
        for(int i = 0; i < daysForward; i++) {
            dates.add(cal.get(Calendar.DAY_OF_MONTH));
            cal.roll(Calendar.DAY_OF_YEAR, 1);
        }
        
        for(int i = 0; i < bills.size(); i++) {
            Bill bill = (Bill) bills.get(i);
            int dueDate = bill.getDueDate();
    
            if(bill.getIsPaid() != true) { 
                boolean hits = false;
                
                for(int in = 0; in < dates.size(); in++) {
                    int day = (Integer) dates.get(in);           
                    
                    if(day == dueDate) {
                        hits = true;   
                    }
                }
                
                if(hits == true) {
                    amount = amount.add(bill.getAmount());   
                }
            }
        }
        return amount;
    }
    
//    /**
//     * A method that gets our gLCode name, if there is one.
//     * 
//     * @param gLCode, our gLCode.
//     * 
//     * @return name, our name of our gLCode, 
//     *              "none" if there is none.
//     */
//    public GlCode getGLCodeName(int gLCodeCode) {
//        if(gLCodes.containsKey(gLCodeCode)) {
//            return (String) gLCodes.get(gLCode);   
//        }
//        else {
//            return "" + gLCode;   
//        }
//    }
    
    /**
     * A method that gets our gLCodes.
     * 
     * @return gLCodes, our gLCodes.
     */
    public Map<Code, GlCode> getGLCodes() {
        return gLCodes;   
    }
    
    /**
     * A method that sets our bHSShots.
     * 
     * @parm bHSShots, our new bHSShots.
     */
    public void setBHSShots(List<BHSnapShot> bHSShots) {
        this.bHSShots = bHSShots;   
    }
    
    /**
     * A method that sets a gLCode.
     * 
     * @param gLCode, our gLCode.
     * @param name, our gLCode name.
     */
    public void addGLCode(GlCode glCode) {
        gLCodes.put(glCode.getCode(), glCode);   
    }
    
    /**
     * A method that sets our gLCodes.
     * 
     * @param gLCodes, our new gLCodes.
     */
    public void setGLCodes(Map<Code, GlCode> gLCodes) {
        this.gLCodes = gLCodes;   
    }
    
    /**
     * A method that removes a bill.
     * 
     * @param bill, the bill to be removed.
     */
    public void removeBill(Bill bill) {
        bills.remove(bill);   
    }
    
    /**
     * A method that removes all bills that are non-recurrent and are paid.
     */
    public void removeOldBills() {
        
        Calendar cal = Calendar.getInstance();
        ArrayList dates = new ArrayList();
        
        for(int i = 0; i < 20; i++) { // doesn't effect those bills with in
            //MiscStuff.writeToLog(cal.get(Calendar.DAY_OF_MONTH));
            dates.add(cal.get(Calendar.DAY_OF_MONTH)); // 20 days
            cal.roll(Calendar.DAY_OF_YEAR, 1);      
        }
        
        if(bills == null) {
            bills = new ArrayList();   
        }
        
        for(int i = 0; i < bills.size(); i++) {
            Bill bill = (Bill) bills.get(i);
            
            int rC = bill.getRecurrenceCode();
            boolean paid = bill.getIsPaid();
            int due = bill.getDueDate();
            
            if(rC == 0) {
                if(paid == true) {
                    bills.remove(bill);
                    i--;
                }
            }
            boolean hit = false;
            
            for(int in = 0; in < dates.size(); in++) {
                int date = (Integer) dates.get(in);
                
                if(date == due) {
                    hit = true;   
                }
            }
            if(!hit && paid) {
                if(rC == 1) {
                    bill.setIsPaid(false);
                }
            }
        }
    }
    
    /**
     * A method that set our bills
     * 
     * @param bills, our new bills.
     */
    public void setBills(List<Bill> bills) {
        this.bills = bills;   
    }
    
    /**
     * A method that sets our check book.
     * 
     * @param checkBook, our new checkBook...
     */
    public void setCurrentCheckBook(CheckBook checkBook) {
        checkBooks.add(0, checkBook);
        this.checkBook = checkBook;   
        
        setUp();
    }
    
    /**
     * A method that does some setting up.
     */
    public void setUp() { // don't know why I have this.....................
        removeOldBills();
        
        Calendar cal = Calendar.getInstance();
        today = cal.get(Calendar.MONTH) + " " + 
            cal.get(Calendar.DAY_OF_MONTH) + " " + 
            (cal.get(Calendar.YEAR) - 2000);
    }

    GlCode getGlCode(Code code) {
        if(!gLCodes.containsKey(code)){
            gLCodes.put(code, new GlCode(code));
        }
        
        return gLCodes.get(code);
    }

    @Override
    public String toString() {
        return "BankHealth{" + "checkBook=" + checkBook + ", bills=" + bills + ", bHSShots=" + bHSShots + ", futureBills=" + futureBills + ", checkBooks=" + checkBooks + ", gLCodes=" + gLCodes + ", today=" + today + '}';
    }
    
    
}