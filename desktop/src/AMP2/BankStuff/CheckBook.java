package AMP2.BankStuff;

/**
 * Write a description of class CheckBook here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import AMP2.BankStuff.check.Checks;
import AMP2.BankStuff.check.ChecksImpl;
import AMP2.BankStuff.check.UnmodifiableChecks;
import java.io.*;
import java.util.*;

public class CheckBook implements Serializable
{
    private Currency balance = Currency.Zero, waitingToGoThroughC = Currency.Zero, 
        waitingToGoThroughT = Currency.Zero, adjustedBalance = Currency.Zero;

    private final Checks checks = new ChecksImpl();
    private List<Transaction> transactions;

    /**
     * Constructor for objects of class CheckBook
     */
    public CheckBook()
    {
        this(new ChecksImpl(), new ArrayList());
    }
    
    /**
     * Constructor for objects of class CheckBook
     */
    public CheckBook(Checks checks, List<Transaction> transactions)
    {
        this.transactions = transactions;
        addChecks(checks);
        
        setUp();
    }
    
    /**
     * A method that adds a check.
     * 
     * @param check, a check to be added.
     */
    public void addCheck(Check check) {
        checks.add(check);
    }
    
    /**
     * A method that adds a transaction.
     * 
     * @param transaction, a transaction to be added.
     */
    public void addTransaction(Transaction transaction) {
        boolean added = false;
        Calendar cal = transaction.getDate();
        
        for(int i = 0; i < transactions.size(); i++) {
            Calendar cal2 = ((Transaction) transactions.get(i)).getDate();
            
            if(cal.after(cal2)) {
                transactions.add(i, transaction);
                added = true;
                break;
            }
        }
        if(!added) {
            transactions.add(transaction);   
        }
    }
    
    /**
     * A method that gets our checks.
     * 
     * @return checks, our checks.
     */
    public Checks getChecks() {
        return new UnmodifiableChecks(checks);
    }
    
    /**
     * A method that gets our doubles.
     * 
     * @param index, 0 for balance, 1 for waitingToGoThroughC, 
     * 2 for waitingToGoThroughT, 3 for adjustedBalance.
     * 
     * @return the double according to the index.
     */
    public Currency getDouble(int index) {
        setUp();
        
        switch(index) {
            case 0: return balance; 
            case 1: return waitingToGoThroughC;
            case 2: return waitingToGoThroughT;
            case 3: return adjustedBalance;
            
            default: return new Currency(9999);
        }
    }
    
    /**
     * A method that gets our transactions.
     * 
     * @return transactions, our transactions...
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }
    
    /**
     * A method that gets the next check number based on the last check.
     * 
     * @return the next number, 0 if no check is in.
     */
    public int nextNumber() {
        return checks.getNextNumber();
    }
    
    /**
     * A method that removes a given check.
     * 
     * @param check, the check to be removed.
     */
    public void removeCheck(Check check) {
        checks.remove(check);
    }
    
    /**
     * A method that removes a given transaction.
     * 
     * @param transaction, the transaction to be removed.
     */
    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);   
    }
    
    /**
     * A method that sets our checks.
     * 
     * @param checks, our new checks.
     */
    public void addAllChecks(Collection<Check> checks) {
        this.checks.addAll(checks);
    }
    
    /**
     * A method that sets our transactions.
     * 
     * @param transactions, our new transactions...
     */
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;   
    }
    
    /**
     * A method that does some setting up.
     */
    public void setUp() {
        balance = Currency.Zero;
        waitingToGoThroughC = Currency.Zero;
        waitingToGoThroughT = Currency.Zero;
        adjustedBalance = Currency.Zero;
        
        Currency checkTotal = new Currency("0");
        
        for(Check check : checks){ 
            Currency checkT = check.getAmount();
            checkTotal = checkTotal.add(checkT);
            
            if(check.getGoneThrough() == false) {
                waitingToGoThroughC = waitingToGoThroughC.add(checkT);      
            }
        }
        
        Currency transTotal = Currency.Zero;
        
        for(int i = 0; i < transactions.size(); i++) {
            Transaction trans = transactions.get(i);
            Currency transT = trans.getAmount();
            transTotal = transTotal.add(transT);
            
            if(trans.getGoneThrough() == false) {
                waitingToGoThroughT = waitingToGoThroughT.add(transT);      
            }
        }
        
        adjustedBalance = transTotal.subtract(checkTotal);
        balance = adjustedBalance.add(waitingToGoThroughC).subtract(waitingToGoThroughT);
        
    }
    
    
    public Currency getAdjustedBalance() {
        return getDouble(3);
    }

    public Currency getBalance() {
        return getDouble(0);
    }

    public Currency getWaitingToGoThroughC() {
        return getDouble(1);
    }

    public Currency getWaitingToGoThroughT() {
        return getDouble(2);
    }

    @Override
    public String toString() {
        return "CheckBook{" + "balance=" + balance + ", waitingToGoThroughC=" + waitingToGoThroughC + ", waitingToGoThroughT=" + waitingToGoThroughT + ", adjustedBalance=" + adjustedBalance + ", checks=" + checks + ", transactions=" + transactions + '}';
    }    

    private void addChecks(Checks checks) {
        for(Check check : checks){
            addCheck(check);
        }
    }
}

