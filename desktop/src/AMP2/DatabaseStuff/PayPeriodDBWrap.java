/*
 * PayPeriodDBWrap.java
 *
 * Created on December 2, 2006, 12:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package AMP2.DatabaseStuff;

import AMP2.DatabaseStuff.command.SelectBuilder;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.results.Result;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Table;
import AMP2.MainDisplay.GUI;
import AMP2.Payroll.*;
import java.util.*;

/**
 *
 * @author TheFamily
 */
public class PayPeriodDBWrap {

    
    /** Creates a new instance of PayPeriodDBWrap */
    public PayPeriodDBWrap() {
    }
    
    /**
     *  This method deletes payperiods from the database
     *
     *  @param employeeID, the id of the employee
     *
     *  @return true if deleted, false otherwise
     */
    public static void DeletePayPeriods(int employeeID) throws DataException {
        
        final List<Integer> payPeriodIds = SelectPayPeriodIds(employeeID);
        for(final Integer payPeriodId : payPeriodIds){
            AdjustmentDBWrap.deleteAdjustmentsByPayPeriodId(payPeriodId);
        }
        
        GUI.getCon().delete(Table.payperiods, new Where(Column.EmployeeID, employeeID));
        
        
//        try {
//            return GUI.getCon().GetANewStatement().execute("delete from payperiods where EmployeeID ="+employeeID);
//        }
//        catch(Exception e){
//            throw new DataException(e);
//        }
    }
    
    public static void InsertPayPeriod(PayPeriod pay, int empID, int payDateID) throws DataException {

        
        
        final int payPeriodId = GUI.getCon().insert(Table.payperiods, 
                new Where(Column.Rate, pay.getRate())
                .and(Column.Hours, pay.getHours())
                .and(Column.GrossPay, pay.getGrossPay())
                .and(Column.SS, pay.getSS())
                .and(Column.Med, pay.getMed())
                .and(Column.Fica, pay.getFica())
                .and(Column.OrWith, pay.getOrWith())
                .and(Column.NetPay, pay.getNetPay())
                .and(Column.claim, pay.getClaim())
                .and(Column.EmployeeID, empID)
                .and(Column.PayDateID, payDateID));
        
        
        AdjustmentDBWrap.insertAdjustments(pay.getAdjustments(), payPeriodId);
        
        
    }
    
    /**
     *Returns the id of the payPeriod in the db if the empID and payDateID match (assumes that no employee shall have more than one payPeriod per payDate(pay term) )
     *
     *@param empID, the employee ID to search for,
     *@param payDateID, the payDateID to search for,
     *
     *@return the payPeriodID from the database, if < 0 it isn't there\
     */
    public static int SelectPayPeriodID(int empID, int payDateID) throws DataException{
        
        //"select id from payperiods where empID = '"+empID+"' and paydateid = '"+payDateID+"'"
        final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.payperiods)
                .where(new Where(Column.EmployeeID, empID).and(Column.PayDateID, payDateID));
        
        final Results values = GUI.getCon().select(sb.build());
        
        if(values.isEmpty()){
            return -1;
        }else{
            return (Integer) values.get(0).get(Column.ID);
        }
        
//        try{
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery("select id from payperiods where empID = '"+empID+"' and paydateid = '"+payDateID+"'");
//            //MiscStuff.writeToLog("" +  );
//            if(rs.next()){
//                return rs.getInt(1);
//            }else{
//                return -1;
//            }
//        }
//        catch(SQLException e){
//            //MiscStuff.writeToLog("SelectCompanyID ex " + e.toString());
//            //MiscStuff.writeToLog("select id, name, year from companies where name = '"+name+"' and year = "+year+";");
//            throw new DataException(e);
//        }
//        //return -1;
        
    }
    
    /**
     *This method returns payPeriods in an arrayList, based on the employeeID.
     *
     *@param empID, the id of the employee.
     *
     *@return payPeriods, the payPeriods matching the employeeID
     */
    public static List<PayPeriod> SelectPayPeriods(int empID, int companyID) throws DataException {
        final List<PayPeriod> payPeriods = new ArrayList<PayPeriod>();
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column._star)
                .table(Table.payperiods).where(new Where(Column.EmployeeID, empID));
        
        final Results values = GUI.getCon().select(sb.build());
        
        for(final Result value : values){
 
            final PayDates pD = PayDatesDBWrap.SelectPayDate(value.getInteger(Column.PayDateID));
            final List<Adjustment> adjustments = AdjustmentDBWrap.selectAdjustmentsByPayPeriodId(value.getInteger(Column.ID));
            
            final PayPeriod pP = PayPeriod.getNewInstance(
                    value.getInteger(Column.claim),//rs.getInt("Claim"), 
                    pD, 
                    value.getCurrency(Column.Rate),//rs.getDouble("Rate"),
                    value.getCurrency(Column.Hours),//rs.getDouble("Hours"), 
                    companyID,
                    adjustments); 
                
                pP.setFica(value.getCurrency(Column.Fica));//rs.getDouble("Fica"));
                pP.setOrWith(value.getCurrency(Column.OrWith));//rs.getDouble("OrWith"));
                
                payPeriods.add(pP);
            }     
          
        return payPeriods;

    }
    
    private static List<Integer> SelectPayPeriodIds(int employeeID) throws DataException {
        
        final List<Integer> payPeriodIds = new ArrayList<Integer>();
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.payperiods)
                .where(new Where(Column.EmployeeID, employeeID));
        
        final Results values = GUI.getCon().select(sb.build());
        
        for(final Result value : values){
            payPeriodIds.add(value.getInteger(Column.ID));
        }
        
        return payPeriodIds;
    }
    
}
