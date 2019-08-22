
package AMP2.BankStuff;


/**
 * Write a description of class Bill here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import AMP2.BankStuff.GlCode.Code;
import java.util.*;
import java.io.*;

public class Bill implements Serializable
{
    private int dueDate, recurrenceCode, lastMonthPaid;
    // recurrenceCode, 0 for only once, 1 for monthly, 2 for weekly...
    private String description = "";
    private Currency amount;
    private boolean isPaid;
    private GlCode gLCode;
    
    /**
     * Constructor for objects of class Bill
     */
    public Bill() {
        //does nothing....
    }
    
    /**
     * Constructor for objects of class Bill
     */
    public Bill(Currency amount, int dueDate) {
        this(amount, dueDate, new GlCode(new Code(9999)));
    }
    
    /**
     * Constructor for objects of class Bill
     */
    public Bill(Currency amount, int dueDate, GlCode gLCode) {
        this(amount, dueDate, gLCode, 1);
    }
    
    /**
     * Constructor for objects of class Bill
     */
    public Bill(Currency amount, int dueDate, GlCode gLCode, int recurrenceCode) {
        this(amount, dueDate, gLCode, recurrenceCode, "");
    }
    
    /**
     * Constructor for objects of class Bill
     */
    public Bill(Currency amount, int dueDate, GlCode gLCode, int recurrenceCode,
        String description) {
        
        this.amount = amount;
        this.dueDate = dueDate;
        this.gLCode = gLCode;
        this.recurrenceCode = recurrenceCode;
        this.description = description;
        isPaid = false;
        lastMonthPaid = 0; //never.
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
     * A method that gets our description.
     * 
     * @return description, our description.
     */
    public String getDescription() {
        return description;   
    }     
    
    /**
     * A method that gets our dueDate.
     * 
     * @return dueDate, our dueDate
     */
    public int getDueDate() {
        return dueDate;   
    }
    
    /**
     * A method that gets our gLCode.
     * 
     * @return gLCode, our gLCode.
     */
    public GlCode getGLCode() {
        return gLCode;   
    }
    
    /**
     * A method that gets our isPaid,
     * 
     * @return isPaid, our isPaid...
     */
    public boolean getIsPaid() {
        return isPaid;
    }   
    
    /**
     * A method that gets our last month paid.
     * 
     * @return lastMonthPaid, our last month paid.
     */
    public int getLastMonthPaid() {
        return lastMonthPaid;   
    }
    
    /**
     * A method that gets our recurrenceCode
     * 
     * @return recurrenceCode, our recurrenceCode.
     */
    public int getRecurrenceCode() {
        return recurrenceCode;   
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
     * A method that sets our description.
     * 
     * @param description, our new description.
     */
    public void setDescription(String description) {
        this.description = description;   
    }
    
    /**
     * A method that sets our dueDate.
     * 
     * @param dueDate, our new dueDate.
     */
    public void setDueDate(int dueDate) {
        this.dueDate = dueDate;   
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
     * A method that sets our is paid.
     * 
     * @param isPaid, our new is paid. If it is false our lastMonthPaid is
     *      set to 0. If it is true our lastMonthPaid is set to this month, 
     *      1 being January...
     */
    public void setIsPaid(boolean isPaid) {
        this.isPaid = isPaid;  
        
        if(isPaid) {
            Calendar today = Calendar.getInstance();
            lastMonthPaid = today.get(Calendar.MONTH) + 1;   
        }
        //else {
          //  lastMonthPaid = 0;   
        //}
    }
    
    /**
     * A method that sets our recurrenceCode.
     * 
     * @param recurrenceCode, our new recurrenceCode.
     */
    public void setRecurrenceCode(int recurrenceCode) {
        this.recurrenceCode = recurrenceCode;   
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Bill other = (Bill) obj;
        
        if ((this.gLCode == null) ? (other.gLCode != null) : !this.gLCode.equals(other.gLCode)){//this.gLCode != other.gLCode) {
            return false;
        }
        if (this.dueDate != other.dueDate) {
            return false;
        }
        if (this.recurrenceCode != other.recurrenceCode) {
            return false;
        }
        if (this.lastMonthPaid != other.lastMonthPaid) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if (this.amount != other.amount && (this.amount == null || !this.amount.equals(other.amount))) {
            return false;
        }
        if (this.isPaid != other.isPaid) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.gLCode != null ? this.gLCode.hashCode() : 0);//this.gLCode;
        hash = 97 * hash + this.dueDate;
        hash = 97 * hash + this.recurrenceCode;
        hash = 97 * hash + this.lastMonthPaid;
        hash = 97 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 97 * hash + (this.amount != null ? this.amount.hashCode() : 0);
        hash = 97 * hash + (this.isPaid ? 1 : 0);
        return hash;
    }
    
    
}
