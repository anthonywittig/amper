package AMP2.Payroll;

import AMP2.BankStuff.Currency;
import AMP2.DatabaseStuff.DatabaseHelper;
import AMP2.DatabaseStuff.db.DataException;

/**
 * Write a description of class FicaEng here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class FicaEng
{

    public static final int TaxType = 1;
    
    /**
     * Constructor for objects of class FicaEng
     */
    public FicaEng()
    {
    }

    /**
     * A method to find the fica for single Persons.
     * 
     * @param claim, what they claim.
     * @param grossPay, the gross wage.
     * 
     * @return fica, the fica amount 
     *              or 9999 if out of range.
     */
    public static Currency getFica(int claim, Currency grossPay, int companyID) throws DataException {
        
        return DatabaseHelper.getTaxFromDB(TaxType, companyID, claim, grossPay);
    }
    
}

