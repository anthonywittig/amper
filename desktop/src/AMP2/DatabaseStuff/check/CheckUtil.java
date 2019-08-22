/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.check;

import AMP2.BankStuff.Check;
import AMP2.BankStuff.Currency;
import AMP2.BankStuff.GlCode;
import AMP2.BankStuff.check.Checks;
import AMP2.BankStuff.check.ChecksImpl;
import AMP2.DatabaseStuff.BankStuffDBWrap;
import AMP2.DatabaseStuff.command.SelectBuilder;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.results.Result;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Order;
import AMP2.DatabaseStuff.tables.Table;
import AMP2.MainDisplay.GUI;
import java.util.Calendar;

/**
 *
 * @author awittig
 */
public class CheckUtil {
    
    public static int InsertCheck(Check ch, int checkBookID) throws DataException{
        int goneThrough = BankStuffDBWrap.FALSE;
        
        if(ch.getGoneThrough()) {
            goneThrough = BankStuffDBWrap.TRUE;
        }
        
        final int bankHealthID = (Integer) BankStuffDBWrap.SelectCheckBookRS(checkBookID).get(Column.BankHealthID);
        if(BankStuffDBWrap.SelectGLCodeID(ch.getGlCode(), bankHealthID) == -1){
            BankStuffDBWrap.InsertGLCode(ch.getGlCode(), bankHealthID);
        }
            
            
            final Where where = new Where(Column.dateS, ch.getDateS())
                    .and(Column.payTo, ch.getPayTo())
                    .and(Column.forS, ch.getForS())
                    .and(Column.clearDate, ch.getClearDate())
                    .and(Column.dateTimeInMills, ch.getDate().getTimeInMillis())
                    .and(Column.expectedClearDateTimeInMills, ch.getExpectedClearDate().getTimeInMillis())
                    .and(Column.amount, ch.getAmount())
                    .and(Column.checkNum, ch.getCheckNum())
                    .and(Column.gLCodeID, ch.getGlCode().getId())
                    .and(Column.goneThrough, goneThrough)
                    .and(Column.checkBookID, checkBookID);
            
            return GUI.getCon().insert(Table.checks, where);
                
            
            
    }
    
    public static void DeleteChecks(int checkBookID) throws DataException {
        GUI.getCon().delete(Table.checks, new Where(Column.checkBookID, checkBookID));
    }
    
    public static Currency getTotalPerGlCodeChecks(GlCode glCode, int checkBookID) throws DataException{
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column._star/*Column.amount*/).table(Table.checks)
                .where(new Where(Column.checkBookID, checkBookID).and(Column.gLCodeID, glCode.getId()))
                ;
        
        final Results results = GUI.getCon().select(sb.build());
        
        Currency total = Currency.Zero;
        for(Result result : results){
            total = total.add(result.getCurrency(Column.amount));
        }
        
        return total;
    }
    
        public static int SelectCheckID(Check ch, int checkBookID) throws DataException{
                
            final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.checks).where(
                    new Where(Column.amount, ch.getAmount())
                    .and(Column.checkNum, ch.getCheckNum())
                    .and(Column.checkBookID, checkBookID));
            
            final Results results = GUI.getCon().select(sb.build());
            
            if(results.isEmpty()){
                return -1;
            }else{
                return results.get(0).getInteger(Column.ID);
            }
        
    }

    
    public static Checks SelectChecks(int checkBookID) throws DataException {
        Checks cS = new ChecksImpl();
            
            final SelectBuilder sb = new SelectBuilder().addValue(Column._star).table(Table.checks)
                    .where(new Where(Column.checkBookID, checkBookID))
                    .orderBy(Column.dateTimeInMills, Order.DESC).orderBy(Column.checkNum, Order.DESC);
            final Results results = GUI.getCon().select(sb.build());
                          
            for(final Result result : results){
                Calendar cal = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                
                cal.setTimeInMillis(result.getLong(Column.dateTimeInMills));
                cal2.setTimeInMillis(result.getLong(Column.expectedClearDateTimeInMills));
                
                boolean goneThrough = false;
                if(result.getInteger(Column.goneThrough) == BankStuffDBWrap.TRUE) {
                    goneThrough = true;
                }
                
                final int glCodeId = result.getInteger(Column.gLCodeID);
                final GlCode glCode = BankStuffDBWrap.SelectGLCode(glCodeId);
                
                if(glCode == null){
                    throw new DataException(String.format("Expected non-null glCode for glCodeId: %s, for check: %s", glCodeId, result));
                }
                
                Check ch = new Check(result.getInteger(Column.checkNum),
                        cal,
                        result.getString(Column.payTo), 
                        result.getCurrency(Column.amount),
                        glCode, 
                        goneThrough,
                        cal2,
                        result.getString(Column.forS) );
                
                cS.add(ch);
            }

        return cS;
    }
    
}
