/*
 * PayDatesDBWrap.java
 *
 * Created on December 2, 2006, 1:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package AMP2.DatabaseStuff;

import AMP2.DatabaseStuff.command.Select;
import AMP2.DatabaseStuff.command.SelectBuilder;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.results.Result;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Table;
import AMP2.MainDisplay.GUI;
import AMP2.Payroll.PayDates;
import java.util.List;
import java.util.Map;
/**
 *
 * @author TheFamily
 */
public class PayDatesDBWrap {
    
    /** Creates a new instance of PayDatesDBWrap */
    public PayDatesDBWrap() {
    }
    
    public static boolean InsertPayDate(PayDates pd) throws DataException {
        try {
            final Where where = new Where(Column.Month, pd.getMonthI())
                    .and(Column.Section, pd.getSectionI())
                    .and(Column.MonthN, pd.getMonthS())
                    .and(Column.SectionS, pd.getSectionS())
                    .and(Column.year, pd.getYear()); 
            //"insert into paydates(Month, Section, MonthN, SectionS, year)"+
            //" values("+pd.getMonthI()+","+pd.getSectionI()+",'"+pd.getMonthS()+"','"+pd.getSectionS()+"','"+pd.getyear()+"');");
            int id = GUI.getCon().insert(Table.paydates, where);
            return 0 < id;
            
        }
        catch (Exception e){
            throw new DataException("insert into paydates(Month, Section, MonthN, SectionS, year)"+
                                                        " values("+pd.getMonthI()+","+pd.getSectionI()+",'"+pd.getMonthS()+"','"+pd.getSectionS()+"','"+pd.getYear()+"')", e);
        }
    }
    
    /**
     *Returns the id of the paydate in the db if the given paydate matches
     *
     *@param pD, the payDate to search for
     *
     *@return the payDateID from the database, if < 0 they aren't there
     */
    public static int SelectPayDateID(PayDates pD) throws DataException{

        final SelectBuilder sb = new SelectBuilder()
                .addValue(Column.ID)
                .table(Table.paydates)
                .where(new Where(Column.Month, pD.getMonthI())
                .and(Column.Section, pD.getSectionI())
                .and(Column.year, pD.getYear()));
        //"select id from payDates where month = "+pD.getMonthI()+" and section = "+pD.getSectionI()+" 
        //and year = '"+pD.getyear()+"';");
        final Select select = sb.build();
        final Results values = GUI.getCon().select(select);


        //rs.absolute(1);
        if(values.isEmpty()){
            return -1;
        }

        final Result value = values.get(0);
        //return rs.getInt(1);
        return (Integer) value.get(Column.ID);
        
    }
    
     /**
     *Returns a payDate based on the payDateID passed in
     *
     *@param payDateID, the payDateID to search for
     *
     *@return the payDate from the database
     */
    public static PayDates SelectPayDate(int payDateID) throws DataException{
        
        try{
            final SelectBuilder sb = new SelectBuilder()
                    .addValue(Column.Month).addValue(Column.Section).addValue(Column.year)
                    .table(Table.paydates)
                    .where(new Where(Column.ID, payDateID));
            //"select * from payDates where ID = "+payDateID+";");
            final Select select = sb.build();
            final Results values = GUI.getCon().select(select);
            
            //rs.absolute(1);
            if(values.size() != 1){
                throw new DataException("Expected one result, got: " + values + ", from: " + select);
            }
            
            final Result value = values.get(0);
            
            //return new PayDates(rs.getInt("Month"), rs.getInt("Section"), rs.getString("year"));
            return new PayDates(
                    value.getInteger(Column.Month),
                    value.getInteger(Column.Section),
                    value.getInteger(Column.year));
        }
        catch(Exception e){
            throw new DataException(e);
        }
        
    }
    
}
