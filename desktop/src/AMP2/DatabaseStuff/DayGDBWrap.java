/*
 * DayGDBWrap.java
 *
 * Created on June 16, 2007, 2:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package AMP2.DatabaseStuff;

import AMP2.DatabaseStuff.command.SelectBuilder;
import AMP2.DatabaseStuff.command.Where;
import AMP2.Days.DayG;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.results.Result;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Table;
import AMP2.MainDisplay.GUI;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author TheFamily
 */
public class DayGDBWrap {
    
    /** Creates a new instance of DayGDBWrap */
    public DayGDBWrap() {
    }
    
    /**
     *  This method delets dayGs
     *
     *  @param companyID, the id of the company from which the dayGs will be deleted
     *
     *  @return true if all goes well, false otherwise.
     */
     public static void DeleteDayGs(int companyID) throws DataException{
        
         GUI.getCon().delete(Table.daygs, new Where(Column.companyID, companyID));
         
//        try {
//            return GUI.getCon().GetANewStatement().execute("delete from daygs where CompanyID = "+companyID);
//        }
//        catch(Exception e) {
//            throw new DataException(e);
//        }
    }
    
    /**
     *  This method inserts a dayG
     *
     *  @param dayG, the dayG from which we will pull the data
     *  @param companyID, the id of the company for which the dayG belongs
     *
     *  @return true if all goes well, false otherwise.
     */
     public static int InsertDayG(DayG dayG, int companyID) throws DataException{
        
         final Where where = Where.getWhere(
                 new Column[]{Column.Note, Column.TimeInMills, Column.Gross, Column.DayOfWeekS, Column.companyID, Column.MonthS}, 
                 new Object[]{dayG.getNote(),dayG.getCalendar().getTimeInMillis(),dayG.getGross(), dayG.getDayOfWeekS(),companyID,dayG.getMonthS()});
         
         return GUI.getCon().insert(Table.daygs, where);
         
//        try {
//            return GUI.getCon().GetANewStatement().execute("insert into daygs(Note, TimeInMills, Gross, DayOfWeekS, CompanyID, MonthS)" +
//                    "values('"+dayG.getNote()+"','"+dayG.getCalendar().getTimeInMillis()+"','"+dayG.getGross()+"','"+ dayG.getDayOfWeekS()+"',"+companyID+",'"+dayG.getMonthS()+"')");
//        }
//        catch(Exception e) {
//            throw new DataException(e);
//        }
    }
    
    /**
     *Returns the id of the dayG in the db if a match is found
     *
     *@param dayG, the dayG to search for,
     *@param companyID, the id of the company of the dayG
     *
     *@return the dayG ID from the database, if < 0 they arn't there or an error
     *        occured.
     */
    public static int SelectDayGID(DayG dayG, int companyID) throws DataException{
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.daygs).where(new Where(Column.TimeInMills, dayG.getCalendar().getTimeInMillis()).and(Column.Gross, dayG.getGross()).and(Column.companyID, companyID));
        return GUI.getCon().selectIdOrNegOne(sb.build());
        
//        try{
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery("select id from daygs where TimeInMills = '"+dayG.getCalendar().getTimeInMillis()+"' and Gross = '"+dayG.getGross()+"' and CompanyID ="+companyID);
//    
//            if(rs.next()){
//                return rs.getInt(1);
//            }else{
//                return -1;
//            }
//        }
//        catch(Exception e){
//            throw new DataException(e);
//        }
        
    }
    
    /**
     *  This method returns an arraylist of dayGs based on the companyID
     *
     *  @param companyID, the id of the company for which we will pull dayGs
     *
     *  @return dayGs, an arrayList of all the dayGs associated with the 
     *                     companyID
     */
    public static ArrayList<DayG> SelectDayGs(int companyID) throws DataException {
        ArrayList<DayG> dayGs = new ArrayList<DayG>();
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column._star).table(Table.daygs)
                .where(new Where(Column.companyID, companyID));
        final Results results = GUI.getCon().select(sb.build());
        
//        try{
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery("select * " +
//                    "from daygs where companyID ="+companyID);
            
//            while(rs.next()) {
        for(final Result rs : results){
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(rs.getLong(Column.TimeInMills));
                
                DayG dayG = new DayG(cal, rs.getCurrency(Column.Gross));
                dayG.setNote(rs.getString(Column.Note));
                
                dayGs.add(dayG);

            }
//        }
//        catch(Exception e){
//            throw new DataException(e);
//        }

        return dayGs;
    }
}
