
package AMP2.BankStuff;


/**
 * Write a description of class Transaction here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import AMP2.BankStuff.GlCode.Code;
import java.util.*;
import java.io.*;

public class Transaction implements Serializable
{
    private GlCode gLCode;
    private Currency amount;
    private String description = "", dateS = "";
    private Calendar date;
    private boolean goneThrough;

    
    /**
     * Constructor for objects of class Transaction
     */
    public Transaction()
    {
    }
    
    /**
     * Constructor for objects of class Transaction
     */
    public Transaction(Currency amount)
    {
        this(amount, null);
    }
    
    /**
     * Constructor for objects of class Transaction
     */
    public Transaction(Currency amount, String description)
    {
        this(amount, description, null);
    }
    
    /**
     * Constructor for objects of class Transaction
     */
    public Transaction(Currency amount, String description, Calendar date)
    {
        this(amount, description, date, new GlCode(new Code(9999)));
    }
    
    /**
     * Constructor for objects of class Transaction
     */
    public Transaction(Currency amount, String description, Calendar date,
        GlCode gLCode)
    {
        this(amount, description, date, gLCode, true);
    }
    
    /**
     * Constructor for objects of class Transaction
     */
    public Transaction(Currency amount, String description, Calendar date,
        GlCode gLCode, boolean goneThrough)
    {
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.gLCode = gLCode;
        this.goneThrough = goneThrough;
        
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
     * A method that gets our date.
     * 
     * @return date, our date.
     */
    public Calendar getDate() {
        return date;   
    }
    
    /**
     * A method that gets our dateS
     * 
     * @return dateS, our dateS.
     */
    public String getDateS() {
        return dateS;   
    }
    
    /**
     * A method that gets our description.
     * 
     * @return description, our description.
     */
    public String getDescription() {
        return description;   
    }
    
    /**
     * A method that gets our gLCode
     * 
     * @return gLCode, our gLCode...
     */
    public GlCode getGLCode() {
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
    
    /**
     * A method that sets our amount.
     * 
     * @param amount, our new amount.
     */
    public void setAmount(Currency amount) {
        this.amount = amount;   
    }
    
    /**
     * A method that sets our date.
     * 
     * @param date, our new date.
     */
    public void setDate(Calendar date) {
        this.date = date;
        setUp();
    }
    
    /**
     * A method that sets our description.
     * 
     * @param description, our new description.
     */
    public void setDescription(String description) {
        this.description = description;   
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
    private void setUp() {
        if(date != null) {
            dateS = "" + (date.get(Calendar.MONTH) + 1) + "/" + 
                date.get(Calendar.DAY_OF_MONTH) + "/" + 
                date.get(Calendar.YEAR);
        }
    }
    
    @Override
    public String toString() {
        return "Transaction{" + "gLCode=" + gLCode + ", amount=" + amount + ", description=" + description + ", dateS=" + dateS + ", date=" + date + ", goneThrough=" + goneThrough + '}';
    }
    
}
