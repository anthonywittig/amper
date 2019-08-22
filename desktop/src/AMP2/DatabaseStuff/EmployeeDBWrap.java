

/*
 * EmployeeDBWrap.java
 *
 * Created on December 2, 2006, 12:20 PM
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
import AMP2.Payroll.Employee;
import AMP2.Payroll.PayPeriod;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TheFamily
 */
public class EmployeeDBWrap {
    
    /** Creates a new instance of EmployeeDBWrap */
    public EmployeeDBWrap() {
    }
    
    /**
     *  This method deletes employees
     *
     *  @param companyID, the id of the companyID to delete from
     *
     *  @return true if all goes well, false otherwise.
     */
     public static void DeleteEmployees(int companyID) throws DataException{
        
         GUI.getCon().delete(Table.employees, new Where(Column.companyID, companyID));
         
//        try {
//            return GUI.getCon().GetANewStatement().execute("delete from " +
//                    "employees where CompanyID = " + companyID);
//        }
//        catch(Exception e) {
//            throw new DataException(e);
//        }

    }
    
    public static int InsertEmployee(Employee emp, int companyID) throws DataException{
        int bool = 0;
        if(emp.getIsCurrentEmp()) {
            bool = 1;
        }
        
        final Where where = Where.getWhere(
                new Column[]{Column.name, Column.SocS, Column.Address, Column.claim, Column.CurrentRate, Column.IsCurrentEmp, Column.companyID}, 
                new Object[]{emp.getName(),emp.getSocS(),emp.getAddress(), emp.getClaim(),emp.getCurrentRate(),bool,companyID});
        
        return GUI.getCon().insert(Table.employees, where);
//        
//        try {
//            return GUI.getCon().GetANewStatement().execute("insert into employees(Name, SocS, Address, Claim, CurrentRate, IsCurrentEmp, CompanyID)" +
//                                                 "values('"+emp.getName()+"','"+emp.getSocS()+"','"+emp.getAddress()+"',"+ emp.getClaim()+","+emp.getCurrentRate()+","+bool+","+companyID+")");
//        }
//        catch(Exception e) {
//            throw new DataException(e);
//        }
    }
    
    /**
     *Returns the id of the employee in the db if the name, ss number, and companyID match
     *
     *@param emp, the employee to search for,
     *@param companyID, the id of the company emp works for.
     *
     *@return the empID from the database, if < 0 they arn't there or an error
     *        occured.
     */
    public static int SelectEmployeeID(Employee emp, int companyID) throws DataException{
//        try{
            
            final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.employees)
                    .where(new Where(Column.name, emp.getName())
                    .and(Column.SocS, emp.getSocS())
                    .and(Column.companyID, companyID));
            
            return GUI.getCon().selectIdOrNegOne(sb.build());
            
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery(
//                    "select id from employees where Name = '"+emp.getName()+
//                    "' and SocS = '"+emp.getSocS()+
//                    "' and CompanyID ="+companyID);
            //MiscStuff.writeToLog("" +  );
//            if(rs.next()){
//                return rs.getInt(1);
//            }else{
//                return -1;
//            }
//        }
//        catch(Exception e){
//            //MiscStuff.writeToLog("SelectCompanyID ex " + e.toString());
//            //MiscStuff.writeToLog("select id, name, year from companies where name = '"+name+"' and year = "+year+";");
//            throw new DataException(e);
//        }
        //return -1;
        
    }
    
    /**
     *  This method returns an arraylist of employees based on the companyID
     *
     *  @param companyID, the id of the company for which we will pull employees
     *
     *  @return employees, an arrayList of all the employees associated with the 
     *                     companyID
     */
    public static ArrayList<Employee> SelectEmployees(int companyID) throws DataException {
        ArrayList<Employee> employees = new ArrayList<Employee>();
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column._star).table(Table.employees)
                .where(new Where(Column.companyID, companyID));
        final Results results = GUI.getCon().select(sb.build());
        
//        try{
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery("select * " +
//                    "from employees where companyID = " + companyID);
            
//            while(rs.next()) {
            for(final Result rs : results){
                
                Employee emp = new Employee(rs.getString(Column.name), rs.getString(Column.SocS),
                    rs.getString(Column.Address), rs.getInteger(Column.claim), rs.getCurrency(Column.CurrentRate));
                
                emp.setIsCurrentEmp( (rs.getInteger(Column.IsCurrentEmp) == 1) ? true : false );
               
                final List<PayPeriod> payPs = PayPeriodDBWrap.SelectPayPeriods(rs.getInteger(Column.ID), companyID);
                for(final PayPeriod payP : payPs) {
                    emp.addPayPeriod((PayPeriod) payP);
                }
                
                employees.add(emp);

            }
//        }
//        catch(SQLException e){
//            throw new DataException(e);
//        }

        return employees;
    }
    
}
