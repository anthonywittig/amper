/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.tables;

/**
 *
 * @author awittig
 */
public enum Column {
    
    //column that represents *
    //datatype will be ignored
    _star("*", DataType.CURRENCY),

 
    ID("ID", DataType.INTEGER),
    //Name("Name", DataType.VARCHAR_200),
    SocS("SocS", DataType.VARCHAR_20),
    Address("Address", DataType.VARCHAR_500),
    //Claim("Claim", DataType.INTEGER),
    CurrentRate("CurrentRate", DataType.CURRENCY),
    IsCurrentEmp("IsCurrentEmp", DataType.INTEGER),
    //CompanyID("CompanyID", DataType.INTEGER),
    ////MiscStuff.writeToLog("CreateUpdateTables - employees: " + e.toString());
    //}
    //} else if (s.equals("bankhealth")) {
    //try {
    //ID("ID", DataType.INTEGER),
    currentCheckBookID("currentCheckBookID", DataType.INTEGER),
    //CompanyID("CompanyID", DataType.INTEGER),

    //MiscStuff.writeToLog("CreateUpdateTables - bankhealth: " + e.toString());
    //}
    //} else if (s.equals("bankhealthsnapshot")) {
    //try {
    //ID("ID", DataType.INTEGER),
    //DateS("DateS", DataType.VARCHAR_100),
    BallanceS("BallanceS", DataType.VARCHAR_50),
    UnpostedCBS("UnpostedCBS", DataType.VARCHAR_50),
    UnpostedDBS("UnpostedDBS", DataType.VARCHAR_50),
    AdjustedBS("AdjustedBS", DataType.VARCHAR_50),
    PercentUS("PercentUS", DataType.VARCHAR_50),
    FutureBillsS("FutureBillsS", DataType.VARCHAR_50),
    AdjustedB2S("AdjustedB2S", DataType.VARCHAR_50),
    EvenDS("EvenDS", DataType.VARCHAR_50),
    FutureBillsID("FutureBillsID", DataType.INTEGER),
    BankHealthID("BankHealthID", DataType.INTEGER),
    //MiscStuff.writeToLog("CreateUpdateTables - bankhealthsnapshot: " + e.toString());
    //}
    //} else if (s.equals("bills")) {
    //try {
    //ID("ID", DataType.INTEGER),
    gLCodeID("gLCodeID", DataType.INTEGER),
    dueDate("dueDate", DataType.INTEGER),
    recurrenceCode("recurrenceCode", DataType.INTEGER),//DataType.VARCHAR_50),
    lastMonthPaid("lastMonthPaid", DataType.INTEGER),
    description("description", DataType.VARCHAR_500),
    amount("amount", DataType.CURRENCY),//.VARCHAR_50),
    isPaid("isPaid", DataType.INTEGER),
    //bankHealthID("bankHealthID", DataType.INTEGER),
    //MiscStuff.writeToLog("CreateUpdateTables - bills: " + e.toString());
    //}
    //} else if (s.equals("glcodes")) {
    //try {
    //ID("ID", DataType.INTEGER),
    code("code", DataType.INTEGER),//.VARCHAR_20),
    //      description("description", DataType.VARCHAR_500),
    //    BankHealthID("BankHealthID", DataType.INTEGER),

    //MiscStuff.writeToLog("CreateUpdateTables - glcodes: " + e.toString());
    //}
    //} else if (s.equals("daygs")) {
    //try {
    //ID("ID", DataType.INTEGER),
    Note("Note", DataType.VARCHAR_500),
    TimeInMills("TimeInMills", DataType.LONG),
    Gross("Gross", DataType.CURRENCY),//.VARCHAR_50),
    DayOfWeekS("DayOfWeekS", DataType.VARCHAR_50),
    //      CompanyID("CompanyID", DataType.INTEGER),
    MonthS("MonthS", DataType.VARCHAR_50),
    //MiscStuff.writeToLog("CreateUpdateTables - daygs: " + e.toString());
    //}
    //} else if (s.equals("companies")) {
    //try {
    //ID("ID", DataType.INTEGER),
    name("name", DataType.VARCHAR_500),
    location("location", DataType.VARCHAR_500),
    year("col_year", DataType.INTEGER),
    //MiscStuff.writeToLog("CreateUpdateTables - companies: " + e.toString());
    //}
    //} else if (s.equals("checkbooks")) {
    //try {
    //ID("ID", DataType.INTEGER),
    balance("balance", DataType.CURRENCY),//.VARCHAR_50),
    waitingToGoThroughC("waitingToGoThroughC", DataType.CURRENCY),//.VARCHAR_50),
    waitingToGoThroughT("waitingToGoThroughT", DataType.CURRENCY),//.VARCHAR_50),
    adjustedBalance("adjustedBalance", DataType.CURRENCY),//.VARCHAR_50),
    //      bankHealthID("bankHealthID", DataType.INTEGER),

    //MiscStuff.writeToLog("CreateUpdateTables - checkbooks: " + e.toString());
    //}
    //} else if (s.equals("paydates")) {
    //try {
    //ID("ID", DataType.INTEGER),
    Month("Month", DataType.INTEGER),
    Section("Section", DataType.INTEGER),
    MonthN("MonthN", DataType.VARCHAR_20),
    SectionS("SectionS", DataType.VARCHAR_20),
    //Year("Year", DataType.VARCHAR_20),
    //MiscStuff.writeToLog("CreateUpdateTables - paydates: " + e.toString());
    //}
    //} else if (s.equals("checks")) {
    //try {
    //ID("ID", DataType.INTEGER),
    //dateS("dateS", DataType.VARCHAR_50),
    payTo("payTo", DataType.VARCHAR_50),
    //dollarsS("dollarsS", DataType.VARCHAR_50),
    forS("forS", DataType.VARCHAR_500),
    clearDate("clearDate", DataType.VARCHAR_50),
    dateTimeInMills("dateTimeInMills", DataType.LONG),
    expectedClearDateTimeInMills("expectedClearDateTimeInMills", DataType.LONG),
    //      amount("amount", DataType.VARCHAR_50),
    checkNum("checkNum", DataType.INTEGER), //VARCHAR(50), " +
    //glID("glID", DataType.INTEGER), //.VARCHAR_50),
    goneThrough("goneThrough", DataType.INTEGER),
    checkBookID("checkBookID", DataType.INTEGER),
    //MiscStuff.writeToLog("CreateUpdateTables - checks: " + e.toString());
    //}
    //} else if (s.equals("transactions")) {
    //try {
    //ID("ID", DataType.INTEGER),
    //glID_transactions("glID", DataType.VARCHAR_20),
    //amount_transactions("amount", DataType.CURRENCY),//.VARCHAR_20),
    //  description("description", DataType.VARCHAR_500),
    dateS("dateS", DataType.VARCHAR_500),
    //timeInMills("timeInMills", DataType.VARCHAR_100),
    //goneThrough("goneThrough", DataType.VARCHAR_20),
    //checkBookID("checkBookID", DataType.INTEGER),

    //MiscStuff.writeToLog("CreateUpdateTables - transactions: " + e.toString());
    //}
    //} else if (s.equals("payperiods")) {
    //try {
    //ID("ID", DataType.INTEGER),
    Rate("Rate", DataType.CURRENCY),// .VARCHAR_20),
    Hours("Hours", DataType.CURRENCY),// .VARCHAR_20),
    GrossPay("GrossPay", DataType.CURRENCY),//.VARCHAR_20),
    SS("SS", DataType.CURRENCY),//.VARCHAR_20),
    Med("Med", DataType.CURRENCY),//.VARCHAR_20),
    Fica("Fica", DataType.CURRENCY),//.VARCHAR_20),
    OrWith("OrWith", DataType.CURRENCY),//.VARCHAR_20),
    NetPay("NetPay", DataType.CURRENCY),//.VARCHAR_20),
    //Claim_payperiods("Claim", DataType.VARCHAR_20),
    EmployeeID("EmployeeID", DataType.INTEGER), //.VARCHAR_20),
    PayDateID("PayDateID", DataType.INTEGER), //.VARCHAR_20),
    //MiscStuff.writeToLog("CreateUpdateTables - payperiods: " + e.toString());
    //}
    //} else if (s.equals("misc")) {
    //try {
    //ID("ID", DataType.INTEGER),
    lastusedcompany("lastusedcompany", DataType.INTEGER),
    //MiscStuff.writeToLog("CreateUpdateTables - misc: " + e.toString());
    //}
    //} else if (s.equals("taxes")) {
    //try {
    //ID("ID", DataType.INTEGER),
    companyID("companyID", DataType.INTEGER),
    taxTypeID("taxTypeID", DataType.INTEGER),
    claim("claim", DataType.INTEGER),
    underAmount("underAmount", DataType.CURRENCY),
    tax("tax", DataType.CURRENCY),
    PayPeriodID("payPeriodId", DataType.INTEGER);

    
    private final String colName;
    private final DataType type;
    
    Column(String colName, DataType type) {
        this.colName = colName;
        this.type = type;
    }
    
    public String colName(){
        return colName;
    }
    
    public DataType type(){
        return type;
    }
}
