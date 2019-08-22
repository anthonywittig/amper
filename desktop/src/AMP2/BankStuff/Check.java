package AMP2.BankStuff;

/**
 * Write a description of class Check here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import AMP2.BankStuff.GlCode.Code;
import java.util.*;
import java.io.*;

public class Check implements Serializable, Comparable<Check>
{
    private String dateS = "",payTo = "", forS = "", 
        clearDate = "";
    private final Calendar date, expectedClearDate;
    private Currency amount = Currency.Zero;
    private int checkNum;
    private GlCode gLCode;
    private boolean goneThrough = false;
    
    
    /**
     * Constructor for objects of class Check
     */
    public Check(int checkNum, Calendar date, String payTo, Currency amount)
    {
        this(checkNum, date, payTo, amount, new GlCode(new Code(9999)), false, date);
    }
    
    /**
     * Constructor for objects of class Check
     */
    public Check(int checkNum, Calendar date, String payTo, Currency amount,
        GlCode gLCode)
    {
        this(checkNum, date, payTo, amount, gLCode, false, date);
    }
    
    /**
     * Constructor for objects of class Check
     */
    public Check(int checkNum, Calendar date, String payTo, Currency amount, 
        GlCode gLCode, boolean goneThrough, Calendar clearDate)
    {
        this(checkNum, date, payTo, amount, gLCode, goneThrough, clearDate, "");
    }
    
    /**
     * Constructor for objects of class Check
     */
    public Check(int checkNum, Calendar date, String payTo, Currency amount, 
        GlCode gLCode, boolean goneThrough, Calendar expectedClearDate, 
        String forS)
    {
        this.checkNum = checkNum;
        this.date = date;
        this.payTo = payTo;
        this.amount = amount;
        this.gLCode = gLCode;
        this.goneThrough = goneThrough;
        this.expectedClearDate = expectedClearDate;
        this.forS = forS;
        
        setUp();
    }
    
    /**
     * A method that gets our amount.
     * 
     * @return amount, our amount.
     */
    public Currency getAmount() {
        return amount;   
    }
    
    /**
     * A method that gets our calandars.
     * 
     * @param index, 0 for date, 1 for expectedClearDate.
     * 
     * @return our calendar according to the index. Null if not found.
     */
//    public Calendar getCalendar(int index) {
//        
//        switch(index) {
//            case 0: return date;
//            case 1: return expectedClearDate;
//            default: return null;
//        }
//    }
    
    public Calendar getDate(){
        return date;
    }
    
    public Calendar getExpectedClearDate(){
        return expectedClearDate;
    }
    
    /**
     * A method that gets our check number;
     * 
     * @return checkNum, our checkNumber.
     */
    public int getCheckNum() {
        return checkNum;   
    }
    
    /**
     * A method that gets our gLCode.
     * 
     * @return gLCode, our gLCode.
     */
    public GlCode getGlCode() {
        return gLCode;   
    }
    
    /**
     * A method that gets our goneThrough, to see if the check has gone through.
     * 
     * @return goneThrough, our goneThrough.
     */
    public boolean getGoneThrough() {
        return goneThrough;   
    }

    public void setClearDate(String clearDate) {
        this.clearDate = clearDate;
    }

    public void setDateS(String dateS) {
        this.dateS = dateS;
    }

    public void setForS(String forS) {
        this.forS = forS;
    }

    public void setPayTo(String payTo) {
        this.payTo = payTo;
    }

    public String getClearDate() {
        return clearDate;
    }

    public String getDateS() {
        return dateS;
    }

    public String getDollarsS() {
        return NumberNames.numName(amount);
    }

    public String getForS() {
        return forS;
    }

    public String getPayTo() {
        return payTo;
    }
    
    /**
     * A method that sets our amount.
     * 
     * @param amount, our new amount.
     */
    public void setAmount(Currency amount) {
        this.amount = amount;   
    }
    
    /**
     * A method that sets our check number.
     * 
     * @param checkNum, our new checkNumber.
     */
    public void setCheckNum(int checkNum) {
        this.checkNum = checkNum;   
    }
    
    /**
     * A method that sets our gLCode.
     * 
     * @param gLCode, our new gLCode.
     */
    public void setGLCode(GlCode gLCode) {
        this.gLCode = gLCode;   
    }
    
    /**
     * A method that sets our goneThrough.
     * 
     * @param goneThrough, our new goneThrough.
     */
    public void setGoneThrough(boolean goneThrough) {
        this.goneThrough = goneThrough;   
    }
    
    /**
     * A method that does some setting up...
     */
    public void setUp() {
        
        if(date != null) {
            dateS = "" + (date.get(Calendar.MONTH) + 1) + "/" + 
                date.get(Calendar.DAY_OF_MONTH) + "/" + 
                date.get(Calendar.YEAR);
        }
            
        if(expectedClearDate != null) {
            clearDate = "" + (expectedClearDate.get(Calendar.MONTH) + 1) + "/" + 
                expectedClearDate.get(Calendar.DAY_OF_MONTH) + 
                "/" + expectedClearDate.get(Calendar.YEAR);
        }
    }

    @Override
    public String toString() {
        return "Check{" + "dateS=" + dateS + ", payTo=" + payTo + ", dollarsS=" + getDollarsS() + ", forS=" + forS + ", clearDate=" + clearDate + ", date=" + date.getTimeInMillis() + ", expectedClearDate=" + expectedClearDate.getTimeInMillis() + ", amount=" + amount + ", checkNum=" + checkNum + ", gLCode=" + gLCode + ", goneThrough=" + goneThrough + '}';
    }

    @Override
    public int compareTo(Check o) {
        if(o == null){
            return -1;
        }
        
        //note we compare their date to our date (not the other way around)
        //hacky test for year, day, month are same check:
        if(!o.getDateS().equals(getDateS())){
            int dateCompare = o.getDate().compareTo(date);
            if(dateCompare != 0){
                return dateCompare;
            }
        }
        
        int numCompare = o.getCheckNum() - checkNum;
        if(numCompare != 0){
            return numCompare;
        }
        
        //if the data and check numbers are equal, it doesn't matter which goes first, as long as it's deterministic:
        return hashCode() - o.hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.dateS != null ? this.dateS.hashCode() : 0);
        hash = 79 * hash + (this.payTo != null ? this.payTo.hashCode() : 0);
        hash = 79 * hash + (this.forS != null ? this.forS.hashCode() : 0);
        hash = 79 * hash + (this.clearDate != null ? this.clearDate.hashCode() : 0);
        hash = 79 * hash + (this.date != null ? this.date.hashCode() : 0);
        hash = 79 * hash + (this.expectedClearDate != null ? this.expectedClearDate.hashCode() : 0);
        hash = 79 * hash + (this.amount != null ? this.amount.hashCode() : 0);
        hash = 79 * hash + this.checkNum;
        hash = 79 * hash + (this.gLCode != null ? this.gLCode.hashCode() : 0);
        hash = 79 * hash + (this.goneThrough ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Check other = (Check) obj;
        if ((this.dateS == null) ? (other.dateS != null) : !this.dateS.equals(other.dateS)) {
            return false;
        }
        if ((this.payTo == null) ? (other.payTo != null) : !this.payTo.equals(other.payTo)) {
            return false;
        }
        if ((this.forS == null) ? (other.forS != null) : !this.forS.equals(other.forS)) {
            return false;
        }
        if ((this.clearDate == null) ? (other.clearDate != null) : !this.clearDate.equals(other.clearDate)) {
            return false;
        }
        if (this.date != other.date && (this.date == null || !this.date.equals(other.date))) {
            return false;
        }
        if (this.expectedClearDate != other.expectedClearDate && (this.expectedClearDate == null || !this.expectedClearDate.equals(other.expectedClearDate))) {
            return false;
        }
        if (this.amount != other.amount && (this.amount == null || !this.amount.equals(other.amount))) {
            return false;
        }
        if (this.checkNum != other.checkNum) {
            return false;
        }
        if (this.gLCode != other.gLCode && (this.gLCode == null || !this.gLCode.equals(other.gLCode))) {
            return false;
        }
        if (this.goneThrough != other.goneThrough) {
            return false;
        }
        return true;
    }
    
    
    
    
}
