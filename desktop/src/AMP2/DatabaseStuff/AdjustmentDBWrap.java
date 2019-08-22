/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff;

import AMP2.BankStuff.CheckBook;
import AMP2.BankStuff.Currency;
import AMP2.DatabaseStuff.command.SelectBuilder;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.results.Result;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Table;
import AMP2.MainDisplay.GUI;
import AMP2.Payroll.Adjustment;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author awittig
 */
class AdjustmentDBWrap {

    static List<Adjustment> selectAdjustmentsByPayPeriodId(final int payPeriodId) throws DataException {
        final List<Adjustment> adjustments = new ArrayList<Adjustment>();

        final SelectBuilder sb = new SelectBuilder().addValue(Column.amount).addValue(Column.Note).table(Table.adjustments)
                .where(new Where(Column.PayPeriodID, payPeriodId));
        final Results values = GUI.getCon().select(sb.build());

        for(final Result value : values){

            final Currency amount = value.getCurrency(Column.amount);
            final String note = value.getString(Column.Note);

            final Adjustment adjustment = new Adjustment(amount, note);

            adjustments.add(adjustment);
        }

        return adjustments;
    }

    static void deleteAdjustmentsByPayPeriodId(final Integer payPeriodId) throws DataException {
        GUI.getCon().delete(Table.adjustments, new Where(Column.PayPeriodID, payPeriodId));
    }

    static List<Integer> insertAdjustments(final List<Adjustment> adjustments, final int payPeriodId) throws DataException {
        DataException dataException = null;
        final List<Integer> ids = new ArrayList<Integer>(adjustments.size());
        for(final Adjustment adjustment : adjustments){
            try{
                final int id = GUI.getCon().insert(Table.adjustments, 
                    new Where(Column.PayPeriodID, payPeriodId)
                        .and(Column.amount, adjustment.getAmount())
                        .and(Column.Note, adjustment.getNote()));
                ids.add(id);
            }catch(DataException de){
                dataException = de;
                //add -1 for fun, maybe someday we'll return it
                ids.add(-1);
            }
        }
        
        if(dataException != null){
            throw dataException;
        }
        
        return ids;
    }
    
}
