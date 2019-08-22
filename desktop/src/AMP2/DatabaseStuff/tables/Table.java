/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.tables;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author awittig
 */
public enum Table {

    //javaDb tables
//    javadb_companies(null, Column.ID, Column.name, Column.location, Column.javaDb_year),
//    javadb_paydates(null, Column.ID, Column.Month, Column.Section, Column.MonthN, Column.SectionS, Column.javaDb_Year),
    //end javaDb tables
    adjustments(null, Column.ID, Column.PayPeriodID, Column.amount, Column.Note),
    employees(null, Column.ID, Column.name, Column.SocS, Column.Address, Column.claim, Column.CurrentRate, Column.IsCurrentEmp, Column.companyID),
    bankhealth(null, Column.ID, Column.currentCheckBookID, Column.companyID),
    bankhealthsnapshot(null, Column.ID, Column.dateS, Column.BallanceS, Column.UnpostedCBS, Column.UnpostedDBS, Column.AdjustedBS, Column.PercentUS, Column.FutureBillsS, Column.AdjustedB2S, Column.EvenDS, Column.FutureBillsID, Column.BankHealthID),
    bills(null, Column.ID, Column.gLCodeID, Column.dueDate, Column.recurrenceCode, Column.lastMonthPaid, Column.description, Column.amount, Column.isPaid, Column.BankHealthID),
    glcodes(null, Column.ID, Column.code, Column.description, Column.BankHealthID),
    daygs(null, Column.ID, Column.Note, Column.TimeInMills, Column.Gross, Column.DayOfWeekS, Column.companyID, Column.MonthS),
    companies(null, Column.ID, Column.name, Column.location, Column.year),
    checkbooks(null, Column.ID, Column.balance, Column.waitingToGoThroughC, Column.waitingToGoThroughT, Column.adjustedBalance, Column.BankHealthID),
    paydates(null, Column.ID, Column.Month, Column.Section, Column.MonthN, Column.SectionS, Column.year),
    checks(null, Column.ID, Column.dateS, Column.payTo, Column.forS, Column.clearDate, Column.dateTimeInMills, Column.expectedClearDateTimeInMills, Column.amount, Column.checkNum, Column.gLCodeID, Column.goneThrough, Column.checkBookID),
    transactions(null, Column.ID, Column.gLCodeID, Column.amount, Column.description, Column.dateS, Column.TimeInMills, Column.goneThrough, Column.checkBookID),
    payperiods(null, Column.ID, Column.Rate, Column.Hours, Column.GrossPay, Column.SS, Column.Med, Column.Fica, Column.OrWith, Column.NetPay, Column.claim, Column.EmployeeID, Column.PayDateID),
    misc("insert into misc(lastusedcompany) values(-1)", Column.ID, Column.lastusedcompany),
    taxes(null, Column.ID, Column.companyID, Column.taxTypeID, Column.claim, Column.underAmount, Column.tax);

    private final String afterCreateCommand;
    private final Column[] cols;
    Table(String afterCreateCommand, Column... cols) {
        this.afterCreateCommand = afterCreateCommand;
        this.cols = cols;
    }
    
    public static Set<Table> getRegularTables(){
        final Set<Table> regularTables = new HashSet<Table>();
        regularTables.addAll(Arrays.asList(Table.values()));
        
//        regularTables.remove(javadb_companies);
//        regularTables.remove(javadb_paydates);
        
        return regularTables;
    }
    
    public String afterCreateCommand() {
        return afterCreateCommand;
    }

    public Column[] cols() {
        return cols;
    }
    
    public String tableName(){
        return name();
    }
    
   
}
