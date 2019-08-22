/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff;

import AMP2.BankStuff.Currency;
import AMP2.DatabaseStuff.command.SelectBuilder;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Table;
import AMP2.MainDisplay.GUI;
import AMP2.Payroll.FicaEng;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Andy
 */
public class Tax extends Tax_Base {

    
    public static enum ReservedTax{
        SS(3), Med(4), Nine41(5), Benefit(6), Unemployment(7), SalesTax(8),
        SsFromBusiness(9), MedFromBusiness(10);
        //note that fica is done in FicaEng.java
        
        
        private final int id;
        ReservedTax(int id){
            this.id = id;
        }
        
        public int id(){return id;}
    }
    
    private static Throwable e;

    public Tax() {
    }

    public static Tax getNewInstance(int id) throws DataException {
        final Tax tax = new Tax();
        fill(tax, id);
        return tax;
    }
    
   
    
    public static void addTax(int claim, int companyID, Currency tax, int taxTypeID, Currency underAmount) throws DataException {

        Tax t = new Tax();

        t.setClaim(claim);
        t.setCompanyID(companyID);
        t.setTax(tax);
        t.setTaxTypeID(taxTypeID);
        t.setUnderAmount(underAmount);

        t.update();
    }

    public static ResultSet getAllTaxesByCompanyIDAndTaxTypeAndClaim(int companyID, int taxType, int claim) throws DataException {
        ResultSet ret = null;

        try {
            PreparedStatement stmt = GUI.getCon().GetConnection().prepareStatement("select * from taxes where companyID = ? and taxTypeID = ? and claim = ? order by id");
            stmt.setInt(1, companyID);
            stmt.setInt(2, taxType);
            stmt.setInt(3, claim);
            ret = stmt.executeQuery();

        } catch (Exception e) {
            throw new DataException("getAllTaxesByCompanyIDAndTaxTypeAndClaim(" + companyID + ", " + taxType + ", " + claim + ")", e);
        }

        return ret;
    }
    
    public static Currency get941(int companyID) throws DataException{
        return getATax(companyID, ReservedTax.Nine41);
    }
    
    public static Currency getSalesTax(int companyID) throws DataException {
        return getATax(companyID, ReservedTax.SalesTax);
    }
    
    public static Currency getBenefit(int companyID) throws DataException{
        return getATax(companyID, ReservedTax.Benefit);
    }
    
    public static Currency getMed(int companyID) throws DataException{
        return getATax(companyID, ReservedTax.Med);
    }

    public static Currency getSsFromBusiness(int companyID) throws DataException {
        return getATax(companyID, ReservedTax.SsFromBusiness);
    }

    public static Currency getMedFromBusiness(int companyID) throws DataException {
        return getATax(companyID, ReservedTax.MedFromBusiness);
    }

    public static Currency getSs(int companyID) throws DataException{
        
        return getATax(companyID, ReservedTax.SS);
    }
    
    private static Currency getATax(int companyId, ReservedTax badIndexThatWeShouldntBeUsing) throws DataException{
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column.tax).table(Table.taxes)
                .where(new Where(Column.companyID, companyId).and(Column.taxTypeID, badIndexThatWeShouldntBeUsing.id));
        final Results results = GUI.getCon().select(sb.build());

        if(results.isEmpty()){
            //this should get their attention
            return Currency.One;
        }else{
            return results.get(0).getCurrency(Column.tax);
        }
    }
    
    public static Currency getUnemployment(int companyID) throws DataException{
        return getATax(companyID, ReservedTax.Unemployment);
    }
    
    public static void lameUpdateWeNeedToFixTheThinkingHere(int companyIdOld, int companyId) throws DataException {   
        if(0 < companyIdOld){
            GUI.getCon().update(Table.taxes, 
                    new Where(Column.companyID, companyId), 
                    new Where(Column.companyID, companyIdOld));
        }
    }
    
    private static void updateATax(int companyID, Currency tax, ReservedTax reservedTax) throws DataException{
            final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.taxes)
                    .where(new Where(Column.companyID, companyID).and(Column.taxTypeID, reservedTax.id));
            final Results results = GUI.getCon().select(sb.build());
            
            if(results.isEmpty()){
                Tax t = new Tax();
                t.setCompanyID(companyID);
                t.setTax(tax);
                t.setTaxTypeID(reservedTax.id);
                t.update();
            }else{
                GUI.getCon().update(Table.taxes, new Where(Column.tax, tax), new Where(Column.ID, results.get(0).getInteger(Column.ID)));
            }
    }
    
    public static void update941(int companyID, Currency tax) throws DataException {
        updateATax(companyID, tax, ReservedTax.Nine41);
    }
    
    public static void updateSalesTax(int companyID, Currency tax) throws DataException {
        updateATax(companyID, tax, ReservedTax.SalesTax);
    }
    
    public static void updateBenefit(int companyID, Currency tax) throws DataException {
        updateATax(companyID, tax, ReservedTax.Benefit);
    }
    
    public static void updateMed(int companyID, Currency tax) throws DataException {
        updateATax(companyID, tax, ReservedTax.Med);   

    }

    public static void updateSs(int companyID, Currency tax) throws DataException {
        updateATax(companyID, tax, ReservedTax.SS);
    }
    
    public static void updateMedFromBusiness(int companyID, Currency taxRate) throws DataException {
        updateATax(companyID, taxRate, ReservedTax.MedFromBusiness);
    }

    public static void updateSsFromBusiness(int companyID, Currency taxRate) throws DataException {
        updateATax(companyID, taxRate, ReservedTax.SsFromBusiness);
    }

    
    public static void updateUnemployment(int companyID, Currency tax) throws DataException {
        updateATax(companyID, tax, ReservedTax.Unemployment);
    }
    
    public static void deleteAllFica(int companyID) throws DataException {
        Where allFica = new Where(Column.companyID, companyID).and(Column.taxTypeID, FicaEng.TaxType);
        
        GUI.getCon().delete(Table.taxes, allFica);
    }
}
