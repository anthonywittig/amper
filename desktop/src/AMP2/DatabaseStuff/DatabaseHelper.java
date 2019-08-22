/*
 * DatabaseHelper.java
 *
 * Created on December 2, 2006, 1:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff;

import AMP2.Payroll.*;
import AMP2.Days.*;
import AMP2.BankStuff.*;
import AMP2.BankStuff.Currency;
import AMP2.DatabaseStuff.command.Select;
import AMP2.DatabaseStuff.command.SelectBuilder;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.results.Result;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Table;
import AMP2.MainDisplay.GUI;
import AMP2.Util.MiscStuff;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TheFamily
 */
public class DatabaseHelper {

    public final static String SAVE_LOCATION = "\\dbBackUps\\";
    
    public final static Currency TAX_NOT_FOUND_AMOUNT = new Currency("999999");

    
    

    /** Creates a new instance of DatabaseHelper */
    public DatabaseHelper() {
    }

    /**
     * This method creates a back up of the entire database
     */
    public static void BackupDatabase() throws DataException {
        GUI.getCon().saveDbToFile();
    }

//    /**
//     * This method deletes our bankHealth from the database
//     *
//     * @param companyID, the id of the company in the database
//     */
//    private static void BHDelete(int companyID) throws DataException {
//
//        BankStuffDBWrap.DeleteBankHealth(companyID);
//    }

    /**
     * This method loads our bankHealth from the database
     *
     * @param bH, the bankHealth which we will fill.
     * @param companyID, the id of the company in the database
     */
    private static void BHFromDatabase(BankHealth bH, int companyID) throws DataException {

        int bankHealthID = BankStuffDBWrap.SelectBankHealthID(companyID);

        bH.setGLCodes(BankStuffDBWrap.SelectGLCodes(bankHealthID));

        final Result bankHealth = BankStuffDBWrap.SelectBankHealth(companyID);
        if(bankHealth == null){
            throw new DataException("weren't able to get a bankhealth for companyId = " + companyID);
        }
        
        final int currentCheckBookId = (Integer) bankHealth.get(Column.currentCheckBookID);

        final CheckBook cb = BankStuffDBWrap.SelectCheckBook(currentCheckBookId, bankHealthID);
        bH.setCurrentCheckBook(cb);

        bH.setBills(BankStuffDBWrap.SelectBills(bankHealthID));
        bH.setBHSShots(BankStuffDBWrap.SelectBHSnapShots(bankHealthID));
    }

    /**
     * This method writes our bankHealth from the database
     *
     * @param bH, the bankHealth from which we will write.
     * @param companyID, the id of the company in the database
     */
    private static void BHToDatabase(BankHealth bH, int companyID) throws DataException {
        BankStuffDBWrap.InsertBankHealth(bH, companyID);
    }

    /**
     * This method cleans the text that will go into the database
     *
     * @param text, the text that needs to be cleaned.
     *
     * @return the cleaned text
     */
    public static String CleanText(String text) {
        text = text.replaceAll("'", "''");
        return text;
    }

    /**
     * This method creates all the tables that are not present, and modifies those
     * that have been updated
     */
    public static void CreateUpdateTables() throws DataException {
        Set<Table> tablesToCreate = new HashSet<Table>();
        tablesToCreate.addAll(Table.getRegularTables());
        
        
//        tablesToCreate.add(Table.employees);
//        tablesToCreate.add("bankhealth");
//        tablesToCreate.add("bankhealthsnapshot");
//        tablesToCreate.add("bills");
//        tablesToCreate.add("glcodes");
//        tablesToCreate.add("daygs");
//        tablesToCreate.add("companies");
//        tablesToCreate.add("checkbooks");
//        tablesToCreate.add("paydates");
//        tablesToCreate.add("checks");
//        tablesToCreate.add("transactions");
//        tablesToCreate.add("payperiods");
//        tablesToCreate.add("misc");
//
//        tableNames.add("taxes");


        
        tablesToCreate.removeAll(GUI.getCon().getExistingTables());
        


        //insert tables
        for (final Table table : tablesToCreate) {
            MiscStuff.writeToLog("Creating table: " + table);

            GUI.getCon().createTable(table);
        }
            
//            if (s.equals("employees")) {
//                try {
//                    GUI.getCon().GetANewStatement().execute("create table employees(" +
//                            "ID int unsigned not null auto_increment primary key," +
//                            "Name VARCHAR(200), " +
//                            "SocS VARCHAR(20), " +
//                            "Address VARCHAR(500), " +
//                            "Claim INT, " +
//                            "CurrentRate INT, " +
//                            "IsCurrentEmp INT, " +
//                            "CompanyID INT)");
//                } catch (Exception e) {
//                    MiscStuff.writeToLog("CreateUpdateTables - employees: " + e.toString());
//                }
//            } else if (s.equals("bankhealth")) {
//                try {
//                    GUI.getCon().GetANewStatement().execute("create table bankhealth(" +
//                            "ID int unsigned not null auto_increment primary key," +
//                            "currentCheckBookID INT, " +
//                            "CompanyID INT)");
//                } catch (Exception e) {
//                    MiscStuff.writeToLog("CreateUpdateTables - bankhealth: " + e.toString());
//                }
//            } else if (s.equals("bankhealthsnapshot")) {
//                try {
//                    GUI.getCon().GetANewStatement().execute("create table bankhealthsnapshot(" +
//                            "ID int unsigned not null auto_increment primary key," +
//                            "DateS VARCHAR(100), " +
//                            "BallanceS VARCHAR(50), " +
//                            "UnpostedCBS VARCHAR(50), " +
//                            "UnpostedDBS VARCHAR(50), " +
//                            "AdjustedBS VARCHAR(50), " +
//                            "PercentUS VARCHAR(50), " +
//                            "FutureBillsS VARCHAR(50), " +
//                            "AdjustedB2S VARCHAR(50), " +
//                            "EvenDS VARCHAR(50), " +
//                            "FutureBillsID INT, " +
//                            "BankHealthID INT)");
//                } catch (Exception e) {
//                    MiscStuff.writeToLog("CreateUpdateTables - bankhealthsnapshot: " + e.toString());
//                }
//            } else if (s.equals("bills")) {
//                try {
//                    GUI.getCon().GetANewStatement().execute("create table bills(" +
//                            "ID int unsigned not null auto_increment primary key," +
//                            "gLCodeID INT, " +
//                            "dueDate VARCHAR(50), " +
//                            "recurrenceCode VARCHAR(50), " +
//                            "lastMonthPaid VARCHAR(50), " +
//                            "description VARCHAR(500), " +
//                            "amount VARCHAR(50), " +
//                            "isPaid VARCHAR(50), " +
//                            "bankHealthID INT)");
//                } catch (Exception e) {
//                    MiscStuff.writeToLog("CreateUpdateTables - bills: " + e.toString());
//                }
//            } else if (s.equals("glcodes")) {
//                try {
//                    GUI.getCon().GetANewStatement().execute("create table glcodes(" +
//                            "ID int unsigned not null auto_increment primary key," +
//                            "code VARCHAR(20), " +
//                            "description VARCHAR(500), " +
//                            "BankHealthID INT)");
//                } catch (Exception e) {
//                    MiscStuff.writeToLog("CreateUpdateTables - glcodes: " + e.toString());
//                }
//            } else if (s.equals("daygs")) {
//                try {
//                    GUI.getCon().GetANewStatement().execute("create table daygs(" +
//                            "ID int unsigned not null auto_increment primary key," +
//                            "Note VARCHAR(500), " +
//                            "TimeInMills VARCHAR(500), " +
//                            "Gross VARCHAR(50), " +
//                            "DayOfWeekS VARCHAR(50), " +
//                            "CompanyID INT, " +
//                            "MonthS VARCHAR(50))");
//                } catch (Exception e) {
//                    MiscStuff.writeToLog("CreateUpdateTables - daygs: " + e.toString());
//                }
//            } else if (s.equals("companies")) {
//                try {
//                    GUI.getCon().GetANewStatement().execute("create table companies(" +
//                            "ID int unsigned not null auto_increment primary key," +
//                            "name VARCHAR(500), " +
//                            "location VARCHAR(500), " +
//                            "year INT)");
//                } catch (Exception e) {
//                    MiscStuff.writeToLog("CreateUpdateTables - companies: " + e.toString());
//                }
//            } else if (s.equals("checkbooks")) {
//                try {
//                    GUI.getCon().GetANewStatement().execute("create table checkbooks(" +
//                            "ID int unsigned not null auto_increment primary key," +
//                            "balance VARCHAR(50), " +
//                            "waitingToGoThroughC VARCHAR(50), " +
//                            "waitingToGoThroughT VARCHAR(50), " +
//                            "adjustedBalance VARCHAR(50), " +
//                            "bankHealthID INT)");
//                } catch (Exception e) {
//                    MiscStuff.writeToLog("CreateUpdateTables - checkbooks: " + e.toString());
//                }
//            } else if (s.equals("paydates")) {
//                try {
//                    GUI.getCon().GetANewStatement().execute("create table paydates(" +
//                            "ID int unsigned not null auto_increment primary key," +
//                            "Month INT, " +
//                            "Section INT, " +
//                            "MonthN VARCHAR(20), " +
//                            "SectionS VARCHAR(20), " +
//                            "Year VARCHAR(20))");
//                } catch (Exception e) {
//                    MiscStuff.writeToLog("CreateUpdateTables - paydates: " + e.toString());
//                }
//            } else if (s.equals("checks")) {
//                try {
//                    GUI.getCon().GetANewStatement().execute("create table checks(" +
//                            "ID int unsigned not null auto_increment primary key," +
//                            "dateS VARCHAR(50), " +
//                            "payTo VARCHAR(50), " +
//                            "dollarsS VARCHAR(50), " +
//                            "forS VARCHAR(500), " +
//                            "clearDate VARCHAR(50), " +
//                            "dateTimeInMills VARCHAR(100), " +
//                            "expectedClearDateTimeInMills VARCHAR(100), " +
//                            "amount VARCHAR(50), " +
//                            "checkNum INT, " + //VARCHAR(50), " +
//                            "glID VARCHAR(50), " +
//                            "goneThrough VARCHAR(20), " +
//                            "checkBookID INT)");
//                } catch (Exception e) {
//                    MiscStuff.writeToLog("CreateUpdateTables - checks: " + e.toString());
//                }
//            } else if (s.equals("transactions")) {
//                try {
//                    GUI.getCon().GetANewStatement().execute("create table transactions(" +
//                            "ID int unsigned not null auto_increment primary key," +
//                            "glID VARCHAR(20), " +
//                            "amount VARCHAR(20), " +
//                            "description VARCHAR(500), " +
//                            "dateS VARCHAR(500), " +
//                            "timeInMills VARCHAR(100), " +
//                            "goneThrough VARCHAR(20), " +
//                            "checkBookID INT)");
//                } catch (Exception e) {
//                    MiscStuff.writeToLog("CreateUpdateTables - transactions: " + e.toString());
//                }
//            } else if (s.equals("payperiods")) {
//                try {
//                    GUI.getCon().GetANewStatement().execute("create table payperiods(" +
//                            "ID int unsigned not null auto_increment primary key," +
//                            "Rate VARCHAR(20), " +
//                            "Hours VARCHAR(20), " +
//                            "GrossPay VARCHAR(20), " +
//                            "SS VARCHAR(20), " +
//                            "Med VARCHAR(20), " +
//                            "Fica VARCHAR(20), " +
//                            "OrWith VARCHAR(20), " +
//                            "NetPay VARCHAR(20), " +
//                            "Claim VARCHAR(20), " +
//                            "EmployeeID VARCHAR(20), " +
//                            "PayDateID VARCHAR(20))");
//                } catch (Exception e) {
//                    MiscStuff.writeToLog("CreateUpdateTables - payperiods: " + e.toString());
//                }
//            } else if (s.equals("misc")) {
//                try {
//                    GUI.getCon().GetANewStatement().execute("create table misc(" +
//                            "ID int unsigned not null auto_increment primary key," +
//                            "lastusedcompany INT)");
//                    
//                    
//                    GUI.getCon().GetANewStatement().execute("insert into misc(ID, lastusedcompany) values(1,-1);");
//                    
//                } catch (Exception e) {
//                    MiscStuff.writeToLog("CreateUpdateTables - misc: " + e.toString());
//                }
//            } else if (s.equals("taxes")) {
//                try {
//                    GUI.getCon().GetANewStatement().execute("create table " + s + "(" +
//                            "id int unsigned not null auto_increment primary key" +
//                            ", companyID INT" +
//                            ", taxTypeID INT" +
//                            ", claim INT" +
//                            ", underAmount Decimal(14,6)" +
//                            ", tax Decimal(14,6)" +
//                            ")");
//
//                } catch (Exception e) {
//                    MiscStuff.writeToLog("CreateUpdateTables - " + s + ": " + e.toString());
//                }
//            }
//
//
//
//        }

        
        
        
        
        
        
        
        
        
        
        
        
        
//        try {
//            //call generator
//
//            File f = new File(System.getProperty("user.dir") + "/dbGeneratedFiles/");
//
//            //MiscStuff.writeToLog(f.getAbsolutePath());
//
//            if (!f.exists()) {
//                f.mkdir();
//            }
//
//            com.wittigweb.util.ClassGenerator.generateClassesFromDB(GUI.getCon().GetConnection(), f, "AMP2.DatabaseStuff;", "_Base", 1);
//        } catch (Exception e) {
//            throw new DataException("DatabaseHelper.CreateUpdateTables(), error near end at generating code", e);
//        }

    }

    /**
     *  this method writes our dayGs to the database
     *
     *  @param cG, the controllerG which holds the dayGs to be written
     *  @param companyID, the id of the company these dayGs belong to
     *
     */
    private static void DayGsToDatabase(ControllerG cG, int companyID) throws DataException {

        List<DayG> dayGs = cG.getDayGs();
        for (int i = 0; i < dayGs.size(); i++) {

            DayG dayG = (DayG) dayGs.get(i);
            DayGDBWrap.InsertDayG(dayG, companyID);
        }
    }

    /**
     *  This method deletes something from the db
     * 
     * @param id the id of the thing
     * @param tableName the name of the table
     *
     *  @return true if the thing is deleted or if we don't know if it exists,
     *          false otherwise.
     */
    public static boolean delete(int id, String tableName) throws DataException {

        boolean result = false;
        if (id == -1) {
            result = true;//for all we know it isn't in the db
        } else {
//            try {
//                result = GUI.getCon().GetANewStatement().execute("delete from " + tableName +
//                        " where id = " + id);
//
//
//            } catch (SQLException e) {
//                throw new DataException(e);
//            }
            final Table toDel;
            try{
                toDel = Table.valueOf(tableName);
            }catch(Exception e){
                throw new DataException(e);
            }
            
            GUI.getCon().delete(toDel, new Where(Column.ID, id));
        }

        return result;
    }

    /**
     * This method deletes a company based on their companyID
     *
     * @param companyID, the ID of the company.
     */
    public static void DeleteCompany(int companyID, BankHealth bH) throws DataException {
        //CompanyDBWrap.InsertCompany(companyName, companyLocation, companyYear);

        //HRToDatabase(hR, companyName, companyLocation, companyYear);
        //DayGsToDatabase(cG, companyID);
        //BHToDatabase(bH, companyID);

        //below is good, above is not

        BankStuffDBWrap.DeleteBankHealth(companyID, bH);
        DayGDBWrap.DeleteDayGs(companyID);
        DeleteEmployees(companyID);
        CompanyDBWrap.DeleteCompany(companyID);
    }

    /**
     *This method throws all of our employees to the database
     *
     *@param companyID, the ID of our company in the database to delete from.
     *
     *@return true if all goes well, false otherwise.
     */
    private static boolean DeleteEmployees(int companyID) throws DataException {

        boolean allWentWell = true;

        ArrayList<Employee> emps = EmployeeDBWrap.SelectEmployees(companyID);
        for (Employee emp : emps) {

            int id = EmployeeDBWrap.SelectEmployeeID(emp, companyID);
            PayPeriodDBWrap.DeletePayPeriods(id);
        }

        EmployeeDBWrap.DeleteEmployees(companyID);

        return allWentWell;
    }

    /**
     * This method loads our dayGs from the database
     *
     * @param cG, the contorollerG which we will fill.
     * @param companyID, the id of the company in the database
     */
    public static void DayGsFromDatabase(ControllerG cG, int companyID) throws DataException {

//        ArrayList dayGs = DayGDBWrap.SelectDayGs(companyID);
        cG.addDayGs(DayGDBWrap.SelectDayGs(companyID));

//        for(int i = 0; i < dayGs.size(); i++) {
//            cG.addDayGs((DayG) dayGs.get(i));
//        }
    }

    /**
     *This method throws all of our employees to the database
     *
     *@param hR, our hR object.
     *@param companyID, the ID of our company in the database.
     *
     *@return true if all goes well, false otherwise.
     */
    private static boolean EmployeesToDatabase(HumanResources hR, int companyID) throws DataException {

        final List<Employee> emps = hR.getEmployees();
        boolean allWentWell = true;

        for (final Employee employee : emps) {
            if (EmployeeDBWrap.SelectEmployeeID(employee, companyID) < 0) {
//                try {
                    EmployeeDBWrap.InsertEmployee(employee, companyID);

                    List<PayPeriod> payPs = employee.getPayPeriods();
                    PayPeriodsToDatabase(payPs, EmployeeDBWrap.SelectEmployeeID(employee, companyID));

//                } catch (DataException e) {
//                    MiscStuff.writeToLog(e);
//                    allWentWell = false;
//                }
            }
        }

        return allWentWell;
    }

    /**
     *gets the last payPeriod from the year before the company passed in
     *
     *@param companyID, the id of the current company, we will use this to get
     *the year before's info
     *
     *@return the PayPeriods for the last payperiod of the previous year
     */
    public static List<PayPeriod> GetLastPayPeriodFromYearBefore(int companyID) throws DataException {

        List<PayPeriod> payPeriods = new ArrayList<PayPeriod>();

        if (companyID > 0) {
            int lastYearsCompanyID = -1;

            final Result rs;
            {
                final SelectBuilder sb = new SelectBuilder();
                sb.addValue(Column.name).addValue(Column.location).addValue(Column.year)
                        .table(Table.companies)
                        .where(new Where(Column.ID, companyID));
                final Select select = sb.build();
                final Results values = GUI.getCon().select(select);
                //ResultSet rs = GUI.getCon().GetANewStatement().executeQuery("select * from companies where ID = " + companyID + ";");
                //rs.absolute(1);

                if(values.size() != 1){
                    throw new DataException("expected only one result, got: " + values.size() + ", from " + select);
                }
                rs = values.get(0);
            }

            final Results rs2;

            {
                final SelectBuilder sb = new SelectBuilder();
                sb.addValue(Column.ID)
                        .table(Table.companies)
                        .where(new Where(Column.year, ((Integer) rs.get(Column.year)) - 1)
                        .and(Column.name, (String) rs.get(Column.name))
                        .and(Column.location, (String) rs.get(Column.location)));
                final Select select = sb.build();
                final Results values = GUI.getCon().select(select);
                //ResultSet rs2 = GUI.getCon().GetANewStatement().executeQuery(
                //    "select * from companies where name = '" + rs.getString("name") + 
                //"' and location = '" + rs.getString("location") + 
                //"' and year = " + (rs.getInt("year") - 1) + ";");
                rs2 = values;
            }
            //we should probably be expecting just one, but I'm not sure 
            //we're enforcing that anywhere
            if (0 < rs2.size()) {
                lastYearsCompanyID = (Integer) rs2.get(0).get(Column.ID);
            }

           
            MiscStuff.writeToLog("last years: " + lastYearsCompanyID);
            if (lastYearsCompanyID > 0) {
                HumanResources hR = new HumanResources();
                HRFromDatabase(hR, lastYearsCompanyID);
                final List<Employee> employees = hR.getEmployees();

                for (int i = 0; i < employees.size(); i++) {
                    Employee emp = (Employee) employees.get(i);
                    List<PayPeriod> pPs = emp.getPayPeriods();

                    for (int j = 0; j < pPs.size(); j++) {
                        PayPeriod pP = (PayPeriod) pPs.get(j);
                        if (pP.getDate().getMonthI() == 12 &&
                                pP.getDate().getSectionI() == 2) {
                            payPeriods.add(pP);
                        }
                    }
                }
            }
        }



        return payPeriods;
    }

    /**
     *gets our last company used id
     *
     *@return the last company id we used, -1 if there isn't one.
     */
    public static int GetLastUsedCompanyID() throws DataException {
//        try {
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery(
//                    "select lastusedcompany from misc where ID = 1"); 
            final SelectBuilder sb = new SelectBuilder().addValue(Column.lastusedcompany)
                    .table(Table.misc).where(new Where(Column.ID, 1));
            final Results values = GUI.getCon().select(sb.build());
            
            if(values.isEmpty()){
                return -1;
            }else{
                return (Integer) values.get(0).get(Column.lastusedcompany);
            }
            
            //MiscStuff.writeToLog("" +  );
//            if(rs.next()){
//            
//                return rs.getInt(1);
//            }else{
//                //there was no last used company
//                return -1;
//            }
//        } catch (SQLException e) {
//            throw new DataException("GetLastUsedCompanyID", e);
//            //MiscStuff.writeToLog("select id, name, year from companies where name = '"+name+"' and year = "+year+";");
//        }
    }

    /**
     *  this gets the tax from the db if it can find it, 
     *  otherwise it returns 999999 (a big number to bring attention to
     *  the issue).
     * 
     * @param taxID is the id of the tax (FICA, OrWith, etc). Right now this is
     *              not in the db, but at some point it may be.
     * @param companyID is the id of the company in the db
     * @param claim is the Claim of the employee in the db
     * @param gross the gross wage of the employee
     * 
     * @return the tax amount, 999999 if not found.
     */
    public static Currency getTaxFromDB(int taxTypeID, int companyID, int claim, Currency gross) throws DataException {
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column.tax).addValue(Column.underAmount).table(Table.taxes)
                .where(new Where(Column.taxTypeID, taxTypeID)
                .and(Column.companyID, companyID)
                .and(Column.claim, claim));

        final Results results = GUI.getCon().select(sb.build());


        Collection<Result> sortedByUnder = new TreeSet<Result>(new Comparator<Result>(){
            @Override
            public int compare(Result o1, Result o2) {
                try {
                    Currency underAmount1 = o1.getCurrency(Column.underAmount);
                    Currency underAmount2 = o2.getCurrency(Column.underAmount);
                    if(underAmount1.lt(underAmount2)){
                        return -1;
                    }else{
                        return 1;
                    }
                }catch(DataException e){
                    throw new RuntimeException(e);
                }
            }
        });
        for(Result result : results){
            Currency underAmount = result.getCurrency(Column.underAmount);
            if(gross.lte(underAmount)){
                return result.getCurrency(Column.tax);
            }
        }
        
        


        return TAX_NOT_FOUND_AMOUNT;
    }

    public static void insertDefaultTaxesToDatabase(int companyID) throws DataException {

        //delete 
        ArrayList<Tax> taxes = Tax.getTaxsByCompanyID(companyID);
        for (Tax t : taxes) {
            t.delete();
        }

        //load default taxes:

        // <editor-fold defaultstate="collapsed" desc="FICA">

        Tax.addTax(10, companyID, new Currency("0"), 1, new Currency("340"));
        Tax.addTax(10, companyID, new Currency("1"), 1, new Currency("350"));
        Tax.addTax(10, companyID, new Currency("2"), 1, new Currency("360"));
        Tax.addTax(10, companyID, new Currency("3"), 1, new Currency("370"));
        Tax.addTax(10, companyID, new Currency("4"), 1, new Currency("380"));
        Tax.addTax(10, companyID, new Currency("5"), 1, new Currency("390"));
        Tax.addTax(10, companyID, new Currency("6"), 1, new Currency("400"));
        Tax.addTax(10, companyID, new Currency("7"), 1, new Currency("410"));
        Tax.addTax(10, companyID, new Currency("8"), 1, new Currency("420"));
        Tax.addTax(10, companyID, new Currency("9"), 1, new Currency("430"));
        Tax.addTax(10, companyID, new Currency("10"), 1, new Currency("440"));
        Tax.addTax(10, companyID, new Currency("11"), 1, new Currency("450"));
        Tax.addTax(10, companyID, new Currency("12"), 1, new Currency("460"));
        Tax.addTax(10, companyID, new Currency("13"), 1, new Currency("470"));
        Tax.addTax(10, companyID, new Currency("14"), 1, new Currency("480"));
        Tax.addTax(10, companyID, new Currency("15"), 1, new Currency("490"));
        Tax.addTax(10, companyID, new Currency("16"), 1, new Currency("500"));
        Tax.addTax(10, companyID, new Currency("18"), 1, new Currency("520"));
        Tax.addTax(10, companyID, new Currency("20"), 1, new Currency("540"));
        Tax.addTax(10, companyID, new Currency("22"), 1, new Currency("560"));
        Tax.addTax(10, companyID, new Currency("24"), 1, new Currency("580"));
        Tax.addTax(10, companyID, new Currency("26"), 1, new Currency("600"));
        Tax.addTax(10, companyID, new Currency("28"), 1, new Currency("620"));
        Tax.addTax(10, companyID, new Currency("30"), 1, new Currency("640"));
        Tax.addTax(10, companyID, new Currency("32"), 1, new Currency("660"));
        Tax.addTax(10, companyID, new Currency("34"), 1, new Currency("680"));
        Tax.addTax(10, companyID, new Currency("36"), 1, new Currency("700"));

        Tax.addTax(11, companyID, new Currency("0"), 1, new Currency("480"));
        Tax.addTax(11, companyID, new Currency("1"), 1, new Currency("490"));
        Tax.addTax(11, companyID, new Currency("2"), 1, new Currency("500"));
        Tax.addTax(11, companyID, new Currency("4"), 1, new Currency("520"));
        Tax.addTax(11, companyID, new Currency("6"), 1, new Currency("540"));
        Tax.addTax(11, companyID, new Currency("8"), 1, new Currency("560"));
        Tax.addTax(11, companyID, new Currency("10"), 1, new Currency("580"));
        Tax.addTax(11, companyID, new Currency("12"), 1, new Currency("600"));
        Tax.addTax(11, companyID, new Currency("14"), 1, new Currency("620"));
        Tax.addTax(11, companyID, new Currency("16"), 1, new Currency("640"));
        Tax.addTax(11, companyID, new Currency("18"), 1, new Currency("660"));
        Tax.addTax(11, companyID, new Currency("20"), 1, new Currency("680"));
        Tax.addTax(11, companyID, new Currency("22"), 1, new Currency("700"));

        Tax.addTax(12, companyID, new Currency("0"), 1, new Currency("620"));
        Tax.addTax(12, companyID, new Currency("2"), 1, new Currency("640"));
        Tax.addTax(12, companyID, new Currency("4"), 1, new Currency("660"));
        Tax.addTax(12, companyID, new Currency("6"), 1, new Currency("680"));
        Tax.addTax(12, companyID, new Currency("8"), 1, new Currency("700"));


        Tax.addTax(13, companyID, new Currency("0"), 1, new Currency("700"));
        Tax.addTax(14, companyID, new Currency("0"), 1, new Currency("700"));
        Tax.addTax(15, companyID, new Currency("0"), 1, new Currency("700"));
        Tax.addTax(16, companyID, new Currency("0"), 1, new Currency("700"));
        Tax.addTax(17, companyID, new Currency("0"), 1, new Currency("700"));
        Tax.addTax(18, companyID, new Currency("0"), 1, new Currency("700"));


        Tax.addTax(0, companyID, new Currency("0"), 1, new Currency("115"));
        Tax.addTax(0, companyID, new Currency("1"), 1, new Currency("120"));
        Tax.addTax(0, companyID, new Currency("1"), 1, new Currency("125"));
        Tax.addTax(0, companyID, new Currency("2"), 1, new Currency("130"));
        Tax.addTax(0, companyID, new Currency("2"), 1, new Currency("135"));
        Tax.addTax(0, companyID, new Currency("3"), 1, new Currency("140"));
        Tax.addTax(0, companyID, new Currency("3"), 1, new Currency("145"));
        Tax.addTax(0, companyID, new Currency("4"), 1, new Currency("150"));
        Tax.addTax(0, companyID, new Currency("4"), 1, new Currency("155"));
        Tax.addTax(0, companyID, new Currency("5"), 1, new Currency("160"));
        Tax.addTax(0, companyID, new Currency("5"), 1, new Currency("165"));
        Tax.addTax(0, companyID, new Currency("6"), 1, new Currency("170"));
        Tax.addTax(0, companyID, new Currency("6"), 1, new Currency("175"));
        Tax.addTax(0, companyID, new Currency("7"), 1, new Currency("180"));
        Tax.addTax(0, companyID, new Currency("7"), 1, new Currency("185"));
        Tax.addTax(0, companyID, new Currency("8"), 1, new Currency("190"));
        Tax.addTax(0, companyID, new Currency("8"), 1, new Currency("195"));
        Tax.addTax(0, companyID, new Currency("9"), 1, new Currency("200"));
        Tax.addTax(0, companyID, new Currency("9"), 1, new Currency("205"));
        Tax.addTax(0, companyID, new Currency("10"), 1, new Currency("210"));
        Tax.addTax(0, companyID, new Currency("10"), 1, new Currency("215"));
        Tax.addTax(0, companyID, new Currency("11"), 1, new Currency("220"));
        Tax.addTax(0, companyID, new Currency("11"), 1, new Currency("225"));
        Tax.addTax(0, companyID, new Currency("12"), 1, new Currency("230"));
        Tax.addTax(0, companyID, new Currency("12"), 1, new Currency("235"));
        Tax.addTax(0, companyID, new Currency("13"), 1, new Currency("240"));
        Tax.addTax(0, companyID, new Currency("13"), 1, new Currency("245"));
        Tax.addTax(0, companyID, new Currency("14"), 1, new Currency("250"));
        Tax.addTax(0, companyID, new Currency("14"), 1, new Currency("260"));
        Tax.addTax(0, companyID, new Currency("15"), 1, new Currency("270"));
        Tax.addTax(0, companyID, new Currency("16"), 1, new Currency("280"));
        Tax.addTax(0, companyID, new Currency("17"), 1, new Currency("290"));
        Tax.addTax(0, companyID, new Currency("18"), 1, new Currency("300"));
        Tax.addTax(0, companyID, new Currency("19"), 1, new Currency("310"));
        Tax.addTax(0, companyID, new Currency("20"), 1, new Currency("320"));
        Tax.addTax(0, companyID, new Currency("21"), 1, new Currency("330"));
        Tax.addTax(0, companyID, new Currency("22"), 1, new Currency("340"));
        Tax.addTax(0, companyID, new Currency("23"), 1, new Currency("350"));
        Tax.addTax(0, companyID, new Currency("24"), 1, new Currency("360"));
        Tax.addTax(0, companyID, new Currency("25"), 1, new Currency("370"));
        Tax.addTax(0, companyID, new Currency("26"), 1, new Currency("380"));
        Tax.addTax(0, companyID, new Currency("27"), 1, new Currency("390"));
        Tax.addTax(0, companyID, new Currency("28"), 1, new Currency("400"));
        Tax.addTax(0, companyID, new Currency("30"), 1, new Currency("410"));
        Tax.addTax(0, companyID, new Currency("31"), 1, new Currency("420"));
        Tax.addTax(0, companyID, new Currency("33"), 1, new Currency("430"));
        Tax.addTax(0, companyID, new Currency("34"), 1, new Currency("440"));
        Tax.addTax(0, companyID, new Currency("36"), 1, new Currency("450"));
        Tax.addTax(0, companyID, new Currency("37"), 1, new Currency("460"));
        Tax.addTax(0, companyID, new Currency("39"), 1, new Currency("470"));
        Tax.addTax(0, companyID, new Currency("40"), 1, new Currency("480"));
        Tax.addTax(0, companyID, new Currency("42"), 1, new Currency("490"));
        Tax.addTax(0, companyID, new Currency("43"), 1, new Currency("500"));
        Tax.addTax(0, companyID, new Currency("45"), 1, new Currency("520"));
        Tax.addTax(0, companyID, new Currency("48"), 1, new Currency("540"));
        Tax.addTax(0, companyID, new Currency("51"), 1, new Currency("560"));
        Tax.addTax(0, companyID, new Currency("54"), 1, new Currency("580"));
        Tax.addTax(0, companyID, new Currency("57"), 1, new Currency("600"));
        Tax.addTax(0, companyID, new Currency("60"), 1, new Currency("620"));
        Tax.addTax(0, companyID, new Currency("63"), 1, new Currency("640"));
        Tax.addTax(0, companyID, new Currency("66"), 1, new Currency("660"));
        Tax.addTax(0, companyID, new Currency("69"), 1, new Currency("680"));
        Tax.addTax(0, companyID, new Currency("72"), 1, new Currency("700"));



        Tax.addTax(1, companyID, new Currency("0"), 1, new Currency("250"));
        Tax.addTax(1, companyID, new Currency("1"), 1, new Currency("260"));
        Tax.addTax(1, companyID, new Currency("2"), 1, new Currency("270"));
        Tax.addTax(1, companyID, new Currency("3"), 1, new Currency("280"));
        Tax.addTax(1, companyID, new Currency("4"), 1, new Currency("290"));
        Tax.addTax(1, companyID, new Currency("5"), 1, new Currency("300"));
        Tax.addTax(1, companyID, new Currency("6"), 1, new Currency("310"));
        Tax.addTax(1, companyID, new Currency("7"), 1, new Currency("320"));
        Tax.addTax(1, companyID, new Currency("8"), 1, new Currency("330"));
        Tax.addTax(1, companyID, new Currency("9"), 1, new Currency("340"));
        Tax.addTax(1, companyID, new Currency("10"), 1, new Currency("350"));
        Tax.addTax(1, companyID, new Currency("11"), 1, new Currency("360"));
        Tax.addTax(1, companyID, new Currency("12"), 1, new Currency("370"));
        Tax.addTax(1, companyID, new Currency("13"), 1, new Currency("380"));
        Tax.addTax(1, companyID, new Currency("14"), 1, new Currency("390"));
        Tax.addTax(1, companyID, new Currency("15"), 1, new Currency("400"));
        Tax.addTax(1, companyID, new Currency("16"), 1, new Currency("410"));
        Tax.addTax(1, companyID, new Currency("17"), 1, new Currency("420"));
        Tax.addTax(1, companyID, new Currency("18"), 1, new Currency("430"));
        Tax.addTax(1, companyID, new Currency("19"), 1, new Currency("440"));
        Tax.addTax(1, companyID, new Currency("20"), 1, new Currency("450"));
        Tax.addTax(1, companyID, new Currency("21"), 1, new Currency("460"));
        Tax.addTax(1, companyID, new Currency("22"), 1, new Currency("470"));
        Tax.addTax(1, companyID, new Currency("23"), 1, new Currency("480"));
        Tax.addTax(1, companyID, new Currency("24"), 1, new Currency("490"));
        Tax.addTax(1, companyID, new Currency("25"), 1, new Currency("500"));
        Tax.addTax(1, companyID, new Currency("26"), 1, new Currency("520"));
        Tax.addTax(1, companyID, new Currency("28"), 1, new Currency("540"));
        Tax.addTax(1, companyID, new Currency("30"), 1, new Currency("560"));
        Tax.addTax(1, companyID, new Currency("33"), 1, new Currency("580"));
        Tax.addTax(1, companyID, new Currency("36"), 1, new Currency("600"));
        Tax.addTax(1, companyID, new Currency("39"), 1, new Currency("620"));
        Tax.addTax(1, companyID, new Currency("42"), 1, new Currency("640"));
        Tax.addTax(1, companyID, new Currency("45"), 1, new Currency("660"));
        Tax.addTax(1, companyID, new Currency("48"), 1, new Currency("680"));
        Tax.addTax(1, companyID, new Currency("51"), 1, new Currency("700"));


        Tax.addTax(2, companyID, new Currency("0"), 1, new Currency("390"));
        Tax.addTax(2, companyID, new Currency("1"), 1, new Currency("400"));
        Tax.addTax(2, companyID, new Currency("2"), 1, new Currency("410"));
        Tax.addTax(2, companyID, new Currency("3"), 1, new Currency("420"));
        Tax.addTax(2, companyID, new Currency("4"), 1, new Currency("430"));
        Tax.addTax(2, companyID, new Currency("5"), 1, new Currency("440"));
        Tax.addTax(2, companyID, new Currency("6"), 1, new Currency("450"));
        Tax.addTax(2, companyID, new Currency("7"), 1, new Currency("460"));
        Tax.addTax(2, companyID, new Currency("8"), 1, new Currency("470"));
        Tax.addTax(2, companyID, new Currency("9"), 1, new Currency("480"));
        Tax.addTax(2, companyID, new Currency("10"), 1, new Currency("490"));
        Tax.addTax(2, companyID, new Currency("11"), 1, new Currency("500"));
        Tax.addTax(2, companyID, new Currency("12"), 1, new Currency("520"));
        Tax.addTax(2, companyID, new Currency("14"), 1, new Currency("540"));
        Tax.addTax(2, companyID, new Currency("16"), 1, new Currency("560"));
        Tax.addTax(2, companyID, new Currency("18"), 1, new Currency("580"));
        Tax.addTax(2, companyID, new Currency("20"), 1, new Currency("600"));
        Tax.addTax(2, companyID, new Currency("22"), 1, new Currency("620"));
        Tax.addTax(2, companyID, new Currency("24"), 1, new Currency("640"));
        Tax.addTax(2, companyID, new Currency("26"), 1, new Currency("660"));
        Tax.addTax(2, companyID, new Currency("28"), 1, new Currency("680"));
        Tax.addTax(2, companyID, new Currency("30"), 1, new Currency("700"));

        Tax.addTax(5, companyID, new Currency("0"), 1, new Currency("700"));
        Tax.addTax(6, companyID, new Currency("0"), 1, new Currency("700"));













        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="OrWith">

        Tax.addTax(10, companyID, new Currency("0"), 2, new Currency("120"));
        Tax.addTax(10, companyID, new Currency("1"), 2, new Currency("140"));
        Tax.addTax(10, companyID, new Currency("3"), 2, new Currency("160"));
        Tax.addTax(10, companyID, new Currency("4"), 2, new Currency("180"));
        Tax.addTax(10, companyID, new Currency("5"), 2, new Currency("200"));
        Tax.addTax(10, companyID, new Currency("7"), 2, new Currency("220"));
        Tax.addTax(10, companyID, new Currency("8"), 2, new Currency("240"));
        Tax.addTax(10, companyID, new Currency("9"), 2, new Currency("260"));
        Tax.addTax(10, companyID, new Currency("10"), 2, new Currency("280"));
        Tax.addTax(10, companyID, new Currency("12"), 2, new Currency("300"));
        Tax.addTax(10, companyID, new Currency("13"), 2, new Currency("320"));
        Tax.addTax(10, companyID, new Currency("14"), 2, new Currency("340"));
        Tax.addTax(10, companyID, new Currency("15"), 2, new Currency("360"));
        Tax.addTax(10, companyID, new Currency("17"), 2, new Currency("380"));
        Tax.addTax(10, companyID, new Currency("18"), 2, new Currency("400"));
        Tax.addTax(10, companyID, new Currency("19"), 2, new Currency("420"));
        Tax.addTax(10, companyID, new Currency("21"), 2, new Currency("440"));
        Tax.addTax(10, companyID, new Currency("22"), 2, new Currency("460"));
        Tax.addTax(10, companyID, new Currency("23"), 2, new Currency("480"));
        Tax.addTax(10, companyID, new Currency("24"), 2, new Currency("500"));
        Tax.addTax(10, companyID, new Currency("25"), 2, new Currency("520"));
        Tax.addTax(10, companyID, new Currency("27"), 2, new Currency("540"));
        Tax.addTax(10, companyID, new Currency("28"), 2, new Currency("560"));
        Tax.addTax(10, companyID, new Currency("29"), 2, new Currency("580"));
        Tax.addTax(10, companyID, new Currency("30"), 2, new Currency("600"));
        Tax.addTax(10, companyID, new Currency("31"), 2, new Currency("620"));
        Tax.addTax(10, companyID, new Currency("32"), 2, new Currency("640"));
        Tax.addTax(10, companyID, new Currency("34"), 2, new Currency("660"));
        Tax.addTax(10, companyID, new Currency("35"), 2, new Currency("680"));
        Tax.addTax(10, companyID, new Currency("36"), 2, new Currency("700"));

        Tax.addTax(11, companyID, new Currency("0"), 2, new Currency("200"));
        Tax.addTax(11, companyID, new Currency("1"), 2, new Currency("220"));
        Tax.addTax(11, companyID, new Currency("2"), 2, new Currency("240"));
        Tax.addTax(11, companyID, new Currency("3"), 2, new Currency("260"));
        Tax.addTax(11, companyID, new Currency("5"), 2, new Currency("280"));
        Tax.addTax(11, companyID, new Currency("6"), 2, new Currency("300"));
        Tax.addTax(11, companyID, new Currency("8"), 2, new Currency("320"));
        Tax.addTax(11, companyID, new Currency("9"), 2, new Currency("340"));
        Tax.addTax(11, companyID, new Currency("10"), 2, new Currency("360"));
        Tax.addTax(11, companyID, new Currency("12"), 2, new Currency("380"));
        Tax.addTax(11, companyID, new Currency("13"), 2, new Currency("400"));
        Tax.addTax(11, companyID, new Currency("14"), 2, new Currency("420"));
        Tax.addTax(11, companyID, new Currency("15"), 2, new Currency("440"));
        Tax.addTax(11, companyID, new Currency("17"), 2, new Currency("460"));
        Tax.addTax(11, companyID, new Currency("18"), 2, new Currency("480"));
        Tax.addTax(11, companyID, new Currency("19"), 2, new Currency("500"));
        Tax.addTax(11, companyID, new Currency("20"), 2, new Currency("520"));
        Tax.addTax(11, companyID, new Currency("22"), 2, new Currency("540"));
        Tax.addTax(11, companyID, new Currency("23"), 2, new Currency("560"));
        Tax.addTax(11, companyID, new Currency("24"), 2, new Currency("580"));
        Tax.addTax(11, companyID, new Currency("25"), 2, new Currency("600"));
        Tax.addTax(11, companyID, new Currency("27"), 2, new Currency("620"));
        Tax.addTax(11, companyID, new Currency("28"), 2, new Currency("640"));
        Tax.addTax(11, companyID, new Currency("29"), 2, new Currency("660"));
        Tax.addTax(11, companyID, new Currency("30"), 2, new Currency("680"));
        Tax.addTax(11, companyID, new Currency("31"), 2, new Currency("700"));


        Tax.addTax(12, companyID, new Currency("0"), 2, new Currency("300"));
        Tax.addTax(12, companyID, new Currency("2"), 2, new Currency("320"));
        Tax.addTax(12, companyID, new Currency("3"), 2, new Currency("340"));
        Tax.addTax(12, companyID, new Currency("4"), 2, new Currency("360"));
        Tax.addTax(12, companyID, new Currency("6"), 2, new Currency("380"));
        Tax.addTax(12, companyID, new Currency("7"), 2, new Currency("400"));
        Tax.addTax(12, companyID, new Currency("9"), 2, new Currency("420"));
        Tax.addTax(12, companyID, new Currency("10"), 2, new Currency("440"));
        Tax.addTax(12, companyID, new Currency("11"), 2, new Currency("460"));
        Tax.addTax(12, companyID, new Currency("13"), 2, new Currency("480"));
        Tax.addTax(12, companyID, new Currency("14"), 2, new Currency("500"));
        Tax.addTax(12, companyID, new Currency("15"), 2, new Currency("520"));
        Tax.addTax(12, companyID, new Currency("16"), 2, new Currency("540"));
        Tax.addTax(12, companyID, new Currency("18"), 2, new Currency("560"));
        Tax.addTax(12, companyID, new Currency("19"), 2, new Currency("580"));
        Tax.addTax(12, companyID, new Currency("20"), 2, new Currency("600"));
        Tax.addTax(12, companyID, new Currency("21"), 2, new Currency("620"));
        Tax.addTax(12, companyID, new Currency("23"), 2, new Currency("640"));
        Tax.addTax(12, companyID, new Currency("24"), 2, new Currency("660"));
        Tax.addTax(12, companyID, new Currency("25"), 2, new Currency("680"));
        Tax.addTax(12, companyID, new Currency("26"), 2, new Currency("700"));


        Tax.addTax(0, companyID, new Currency("0"), 2, new Currency("20"));
        Tax.addTax(0, companyID, new Currency("1"), 2, new Currency("40"));
        Tax.addTax(0, companyID, new Currency("3"), 2, new Currency("60"));
        Tax.addTax(0, companyID, new Currency("4"), 2, new Currency("80"));
        Tax.addTax(0, companyID, new Currency("5"), 2, new Currency("100"));
        Tax.addTax(0, companyID, new Currency("7"), 2, new Currency("120"));
        Tax.addTax(0, companyID, new Currency("8"), 2, new Currency("140"));
        Tax.addTax(0, companyID, new Currency("9"), 2, new Currency("160"));
        Tax.addTax(0, companyID, new Currency("10"), 2, new Currency("180"));
        Tax.addTax(0, companyID, new Currency("12"), 2, new Currency("200"));
        Tax.addTax(0, companyID, new Currency("13"), 2, new Currency("220"));
        Tax.addTax(0, companyID, new Currency("14"), 2, new Currency("240"));
        Tax.addTax(0, companyID, new Currency("15"), 2, new Currency("260"));
        Tax.addTax(0, companyID, new Currency("17"), 2, new Currency("280"));
        Tax.addTax(0, companyID, new Currency("18"), 2, new Currency("300"));
        Tax.addTax(0, companyID, new Currency("19"), 2, new Currency("320"));
        Tax.addTax(0, companyID, new Currency("20"), 2, new Currency("340"));
        Tax.addTax(0, companyID, new Currency("22"), 2, new Currency("360"));
        Tax.addTax(0, companyID, new Currency("23"), 2, new Currency("380"));
        Tax.addTax(0, companyID, new Currency("24"), 2, new Currency("400"));
        Tax.addTax(0, companyID, new Currency("26"), 2, new Currency("420"));
        Tax.addTax(0, companyID, new Currency("27"), 2, new Currency("440"));
        Tax.addTax(0, companyID, new Currency("29"), 2, new Currency("460"));
        Tax.addTax(0, companyID, new Currency("30"), 2, new Currency("480"));
        Tax.addTax(0, companyID, new Currency("32"), 2, new Currency("500"));
        Tax.addTax(0, companyID, new Currency("34"), 2, new Currency("520"));
        Tax.addTax(0, companyID, new Currency("35"), 2, new Currency("540"));
        Tax.addTax(0, companyID, new Currency("37"), 2, new Currency("560"));
        Tax.addTax(0, companyID, new Currency("38"), 2, new Currency("580"));
        Tax.addTax(0, companyID, new Currency("40"), 2, new Currency("600"));
        Tax.addTax(0, companyID, new Currency("41"), 2, new Currency("620"));
        Tax.addTax(0, companyID, new Currency("43"), 2, new Currency("640"));
        Tax.addTax(0, companyID, new Currency("44"), 2, new Currency("660"));
        Tax.addTax(0, companyID, new Currency("46"), 2, new Currency("680"));
        Tax.addTax(0, companyID, new Currency("47"), 2, new Currency("700"));


        Tax.addTax(1, companyID, new Currency("0"), 2, new Currency("100"));
        Tax.addTax(1, companyID, new Currency("1"), 2, new Currency("120"));
        Tax.addTax(1, companyID, new Currency("2"), 2, new Currency("140"));
        Tax.addTax(1, companyID, new Currency("3"), 2, new Currency("160"));
        Tax.addTax(1, companyID, new Currency("5"), 2, new Currency("180"));
        Tax.addTax(1, companyID, new Currency("6"), 2, new Currency("200"));
        Tax.addTax(1, companyID, new Currency("8"), 2, new Currency("220"));
        Tax.addTax(1, companyID, new Currency("9"), 2, new Currency("240"));
        Tax.addTax(1, companyID, new Currency("10"), 2, new Currency("260"));
        Tax.addTax(1, companyID, new Currency("11"), 2, new Currency("280"));
        Tax.addTax(1, companyID, new Currency("13"), 2, new Currency("300"));
        Tax.addTax(1, companyID, new Currency("14"), 2, new Currency("320"));
        Tax.addTax(1, companyID, new Currency("15"), 2, new Currency("340"));
        Tax.addTax(1, companyID, new Currency("16"), 2, new Currency("360"));
        Tax.addTax(1, companyID, new Currency("18"), 2, new Currency("380"));
        Tax.addTax(1, companyID, new Currency("20"), 2, new Currency("400"));
        Tax.addTax(1, companyID, new Currency("21"), 2, new Currency("420"));
        Tax.addTax(1, companyID, new Currency("23"), 2, new Currency("440"));
        Tax.addTax(1, companyID, new Currency("25"), 2, new Currency("460"));
        Tax.addTax(1, companyID, new Currency("26"), 2, new Currency("480"));
        Tax.addTax(1, companyID, new Currency("28"), 2, new Currency("500"));
        Tax.addTax(1, companyID, new Currency("29"), 2, new Currency("520"));
        Tax.addTax(1, companyID, new Currency("31"), 2, new Currency("540"));
        Tax.addTax(1, companyID, new Currency("32"), 2, new Currency("560"));
        Tax.addTax(1, companyID, new Currency("34"), 2, new Currency("580"));
        Tax.addTax(1, companyID, new Currency("35"), 2, new Currency("600"));
        Tax.addTax(1, companyID, new Currency("37"), 2, new Currency("620"));
        Tax.addTax(1, companyID, new Currency("38"), 2, new Currency("640"));
        Tax.addTax(1, companyID, new Currency("40"), 2, new Currency("660"));
        Tax.addTax(1, companyID, new Currency("41"), 2, new Currency("680"));
        Tax.addTax(1, companyID, new Currency("43"), 2, new Currency("700"));



        Tax.addTax(2, companyID, new Currency("0"), 2, new Currency("200"));
        Tax.addTax(2, companyID, new Currency("2"), 2, new Currency("220"));
        Tax.addTax(2, companyID, new Currency("3"), 2, new Currency("240"));
        Tax.addTax(2, companyID, new Currency("4"), 2, new Currency("260"));
        Tax.addTax(2, companyID, new Currency("6"), 2, new Currency("280"));
        Tax.addTax(2, companyID, new Currency("7"), 2, new Currency("300"));
        Tax.addTax(2, companyID, new Currency("9"), 2, new Currency("320"));
        Tax.addTax(2, companyID, new Currency("10"), 2, new Currency("340"));
        Tax.addTax(2, companyID, new Currency("11"), 2, new Currency("360"));
        Tax.addTax(2, companyID, new Currency("13"), 2, new Currency("380"));
        Tax.addTax(2, companyID, new Currency("15"), 2, new Currency("400"));
        Tax.addTax(2, companyID, new Currency("16"), 2, new Currency("420"));
        Tax.addTax(2, companyID, new Currency("18"), 2, new Currency("440"));
        Tax.addTax(2, companyID, new Currency("20"), 2, new Currency("460"));
        Tax.addTax(2, companyID, new Currency("21"), 2, new Currency("480"));
        Tax.addTax(2, companyID, new Currency("23"), 2, new Currency("500"));
        Tax.addTax(2, companyID, new Currency("24"), 2, new Currency("520"));
        Tax.addTax(2, companyID, new Currency("26"), 2, new Currency("540"));
        Tax.addTax(2, companyID, new Currency("28"), 2, new Currency("560"));
        Tax.addTax(2, companyID, new Currency("29"), 2, new Currency("580"));
        Tax.addTax(2, companyID, new Currency("31"), 2, new Currency("600"));
        Tax.addTax(2, companyID, new Currency("32"), 2, new Currency("620"));
        Tax.addTax(2, companyID, new Currency("34"), 2, new Currency("640"));
        Tax.addTax(2, companyID, new Currency("35"), 2, new Currency("660"));
        Tax.addTax(2, companyID, new Currency("37"), 2, new Currency("680"));
        Tax.addTax(2, companyID, new Currency("38"), 2, new Currency("700"));

        Tax.addTax(5, companyID, new Currency("0"), 2, new Currency("560"));
        Tax.addTax(5, companyID, new Currency("1"), 2, new Currency("580"));
        Tax.addTax(5, companyID, new Currency("3"), 2, new Currency("600"));
        Tax.addTax(5, companyID, new Currency("4"), 2, new Currency("620"));
        Tax.addTax(5, companyID, new Currency("6"), 2, new Currency("640"));
        Tax.addTax(5, companyID, new Currency("7"), 2, new Currency("660"));
        Tax.addTax(5, companyID, new Currency("8"), 2, new Currency("680"));
        Tax.addTax(5, companyID, new Currency("10"), 2, new Currency("700"));


        Tax.addTax(6, companyID, new Currency("0"), 2, new Currency("640"));
        Tax.addTax(6, companyID, new Currency("1"), 2, new Currency("660"));
        Tax.addTax(6, companyID, new Currency("2"), 2, new Currency("680"));
        Tax.addTax(6, companyID, new Currency("4"), 2, new Currency("700"));














    // </editor-fold>
        
        //others:
        Tax.updateSs(companyID, new Currency("0.062"));
        Tax.updateMed(companyID, new Currency("0.0145"));
        Tax.update941(companyID, new Currency("0.153"));
        Tax.updateBenefit(companyID, new Currency("0.034"));
        Tax.updateUnemployment(companyID, new Currency("0.033"));
    }

    /**
     *This method formats our text for the database
     *
     *@param str, the string to format
     *
     *@return the formatted string
     */
    public static String FormatString(String str) {
        return str.replace("'", "\\'");
    }

    /**
     *This method takes an HR object and throws it to the database.
     *
     *@param hr, our HR object
     *@param companyName, the companyName
     *@param comapnyLocation, the location of the company
     *@param companyYear, the companyYear
     */
    public static void HRToDatabase(HumanResources hR, String companyName, String companyLocation, int companyYear) throws DataException {


        int coID = CompanyDBWrap.SelectCompanyID(companyName, companyLocation, companyYear);

        /*if (!*/EmployeesToDatabase(hR, coID);/*) {
            return false;
        }

        //insert the taxes (make them ask for them):
        //taxesToDatabase(coID);

        //ArrayList emps = hR.getEmployees();
        return true;*/
    }

    /**
     *  This method fills a humanResource object from the database,
     *  based on the companyID
     *
     *  @param hR, the humanResource object to fill.
     *  @param companyID, the id of the company in the database
     */
    public static void HRFromDatabase(HumanResources hR, int companyID) throws DataException {
        //HumanResources hr = new HumanResources("" + companyID);
        hR.setStore("" + companyID);

        ArrayList emps = EmployeeDBWrap.SelectEmployees(companyID);
        //MiscStuff.writeToLog("num of employees: " + emps.size());
        for (int i = 0; i < emps.size(); i++) {
            hR.addEmployee((Employee) emps.get(i));
        }


    }

    /**
     *  This method inserts a company into the database
     *
     *  @param hr, our HR object
     *  @param companyName, the companyName
     *  @param comapnyLocation, the location of the company
     *  @param companyYear, the companyYear
     *  @param cG, the controlerG object
     *
     *  @return true if is unique company,
     *      false if not unique, old will be deleted/replaced
     */
    public static boolean InsertCompany(HumanResources hR, String companyName,
            String companyLocation, int companyYear, ControllerG cG, BankHealth bH) throws DataException {

        //seems like transacitons are sometimes deleted but not inserted?
        final boolean exists = CompanyDBWrap.doesCompanyExist(companyName, companyLocation, companyYear);
        final int companyIdOld;
        if(exists){
            companyIdOld = CompanyDBWrap.SelectCompanyID(companyName, companyLocation, companyYear);
            DeleteCompany(companyIdOld, bH);
        }else{
            companyIdOld = -1;
        }
        

        CompanyDBWrap.InsertCompany(companyName, companyLocation, companyYear);

        int companyID = CompanyDBWrap.SelectCompanyID(companyName, companyLocation, companyYear);
//TODO: need to insert adjustments when insert payroll
        HRToDatabase(hR, companyName, companyLocation, companyYear);
        DayGsToDatabase(cG, companyID);
        //MiscStuff.writeToLog(bH.getGLCodes().values().toString());
        BHToDatabase(bH, companyID);
        
        Tax.lameUpdateWeNeedToFixTheThinkingHere(companyIdOld, companyID);

        SetLastUsedCompany(CompanyDBWrap.SelectCompanyID(companyName, companyLocation, companyYear));
        
        BackupDatabase();

        return !exists;
    }

    /**
     *This method loads the data from the given company id
     *
     *@param companyID, the id of the company to load
     *@param cG, the controllerG object to reference to
     *@param hR, the humanResources object to reference to
     *@param bH, the bankHealth object to reference to
     */
    public static boolean LoadCompany(int companyID, ControllerG cG,
            HumanResources hR, BankHealth bH) throws DataException {
        
        
//            MiscStuff.writeToLog("DatabaseHelper.LoadCompany, companyID = " + companyID);
        
                    
        //ArrayList dayGs = new ArrayList();

        System.gc();//try to clean an older hR, cG, bH

        HRFromDatabase(hR, companyID);
        DayGsFromDatabase(cG, companyID);
        BHFromDatabase(bH, companyID);

        //update lastUsed id:
        SetLastUsedCompany(companyID);


        return true;
    }

    /**
     *This method throws all of our payPeriods to the database
     *
     *@param payPs, the payPeriods to throw down.
     *@param empID, the employee ID of the payPeriods.
     *
     */
    private static void PayPeriodsToDatabase(List<PayPeriod> payPs, int empID) throws DataException {

        boolean allWentWell = true;

        for (int i = 0; i < payPs.size(); i++) {

            PayPeriod pay = (PayPeriod) payPs.get(i);
            int payDateID = PayDatesDBWrap.SelectPayDateID(pay.getDate());

            if (payDateID < 0) {

                if (PayDatesDBWrap.InsertPayDate(pay.getDate())) {
                    payDateID = PayDatesDBWrap.SelectPayDateID(pay.getDate());
                } else {
                    allWentWell = false;
                }

            }

            if (payDateID > 0) {

                if (PayPeriodDBWrap.SelectPayPeriodID(empID, payDateID) < 0) {

                    //if (!
                    PayPeriodDBWrap.InsertPayPeriod(pay, empID, payDateID);
                            //) {
                        //allWentWell = false;
                    //}
                }
            }
        }

        //return allWentWell;
    }

    /**
     *This method sets the last used company on line 1 in misc
     *
     *@param companyID, the id of the company duh.
     */
    public static void SetLastUsedCompany(int companyID) throws DataException {

//        try {
//            GUI.getCon().GetANewStatement().execute("insert into misc(ID, lastusedcompany) values(1," + companyID + ");");
//
//        } catch (Exception e) {
//            MiscStuff.writeToLog("SetLastUsedCompany1: " + e.toString());

//            try {
//                GUI.getCon().GetANewStatement().execute("update misc set lastusedcompany = " + companyID + " where ID = 1");
//            } catch (SQLException ex) {
//                throw new DataException(ex);
//            }
        
        GUI.getCon().update(Table.misc, 
                new Where(Column.lastusedcompany, companyID), 
                new Where(Column.ID, 1));
        

//        }
    }
    
    
}
