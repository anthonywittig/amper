package AMP2.Payroll;

import AMP2.BankStuff.Currency;
import AMP2.DatabaseStuff.Tax;
import AMP2.DatabaseStuff.db.DataException;


/**
 * Write a description of class Ptaxes here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class PTaxes {

    private static final Currency RECALCULATE_TAX = Currency.NegativeOne;
    private Currency grossWages, fica, hours, orWith, grossSales;
    private Currency salesTax = RECALCULATE_TAX;
    protected int companyID = -1;

 
    public PTaxes(int companyID, Currency grossWages, Currency fica, Currency hours, Currency orWith, Currency grossSales)
    {
        this.companyID = companyID;
        this.grossWages = grossWages;
        this.fica = fica;
        this.hours = hours;
        this.orWith = orWith;
        this.grossSales = grossSales;
    }
    
    public PTaxes add(PTaxes pTaxes){
        return new PTaxes(companyID, 
                grossWages.add(pTaxes.grossWages),
                fica.add(pTaxes.fica),
                hours.add(pTaxes.hours),
                orWith.add(pTaxes.orWith),
                grossSales.add(pTaxes.grossSales));
    }
    
    /**
     * the 940
     * 
     * @return the 941 tax total.
     */
    public Currency get941() throws DataException {
        //return AMP2.Util.Math2.round((grossWages * Tax.get941(getCompanyID())) + fica); //.153   
        return grossWages.multiply(Tax.get941(getCompanyID())).add(fica);  
    }
    
    /**
     * the workers' benefit
     * 
     * @param hours, the number of hours worked for all employees
     * @return the benefit tax total.
     */
    public Currency getBenefit() throws DataException {
        //return AMP2.Util.Math2.round(hours * Tax.getBenefit(getCompanyID()));//.034);   
        return hours.multiply(Tax.getBenefit(getCompanyID())).round();
    }
    
    /**
     * A method that gets our fica
     * 
     * @return fica, our fica....
     */
    public Currency getFica() {
        return fica;   
    }
    
    /**
     * A method that returns our grossWages
     * 
     * @return grossWages, our gross wages.
     */
    public Currency getGrossWages() {
        return grossWages;   
    }
    
    /**
     * A method that gets our hours
     * 
     * @return hours, our hours...
     */
    public Currency getHours() {
        return hours;   
    }
    
    /**
     * A method that gets our oregon withholding.
     * 
     * @return orWith, our orWith....
     */
    public Currency getOrWith() {
        return orWith;   
    }
    
    /**
     * the OR state unemployment
     * 
     * @return the unemployment tax total.
     */
    public Currency getUnemployment() throws DataException {
        return grossWages.multiply(Tax.getUnemployment(getCompanyID()).round());
    }
    
    /**
     * A method that sets our fica
     * 
     * @param fica, our fica....
     */
    public void setFica(Currency fica) {
        this.fica = fica;   
    }
    
    /**
     * A method that returns our grossWages
     * 
     * @return grossWages, our gross wages.
     */
    public void setGrossWages(Currency grossWages) {
        this.grossWages = grossWages;   
    }
    
    /**
     * A method that sets our hours
     * 
     * @param hours, our hours...
     */
    public void setHours(Currency hours) {
        this.hours = hours;   
    }
    
    /**
     * A method that sets our oregon withholding.
     * 
     * @param orWith, our new orWith.
     */
    public void setOrWith(Currency orWith) {
        this.orWith = orWith;   
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public Currency getSalesTax() throws DataException {
        if(RECALCULATE_TAX.equals(salesTax)){
            salesTax = grossSales.multiply(Tax.getSalesTax(getCompanyID())).round();
        }
        
        return salesTax;
    }
    
    public Currency getSsFromBusiness() throws DataException {
        return grossWages.multiply(Tax.getSsFromBusiness(companyID)).round();
    }
    
    public Currency getSs() throws DataException {
        return grossWages.multiply(Tax.getSs(companyID)).round();
    }

    public Currency getMedFromBusiness() throws DataException {
        return grossWages.multiply(Tax.getMedFromBusiness(companyID)).round();
    }
    
     public Currency getMed() throws DataException {
        return grossWages.multiply(Tax.getMed(companyID)).round();
    }
    
    /**
     * @param grossSales the grossSales to set
     */
    public void setGrossSales(Currency grossSales) {
        this.grossSales = grossSales;
        salesTax = RECALCULATE_TAX;
    }    
}

