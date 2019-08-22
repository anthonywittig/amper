package AMP2.Util;
/*
 * TheFile4Converter.java
 *
 * Created on May 15, 2006, 11:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author TheFamily
 */

import java.io.*;
import java.util.*;
import java.text.*;

public class TheFile4Converter {
    
    /** Creates a new instance of TheFile4Converter */
    public TheFile4Converter() {     
    }
    
    /**
     *A method that returns an arraylist with arraylists representing a Human
     *Resource object.
     */
   // public static ArrayList HRToArray(HumanResources HR) {
        
   // }
    
//    /**
//     *Our method that converts our BankHealth
//     *
//     *@param bH, our BankHealth to convert.
//     */
//    private static AMP2.BankStuff.BankHealth convertBH(BankHealth bH){
//        
//        ArrayList cBs = bH.getCheckBooks();
//            
//        //I think there will only be one checkbook with how I set it up...
//        if(cBs.size() != 1) {
//            MiscStuff.writeToLog("There is more than one CheckBook! Exiting");
//            System.exit(0);
//        }
//        
//        CheckBook cB = (CheckBook) cBs.get(0);
//        AMP2.BankStuff.CheckBook aCB = new AMP2.BankStuff.CheckBook();
//
//        ArrayList checks = cB.getChecks();
//        for(int j = 0; j < checks.size(); j++) {
//
//            Check check = (Check) checks.get(j);
//            AMP2.BankStuff.Check aCheck = new AMP2.BankStuff.Check(
//                    check.getCheckNum(), check.getCalendar(0), 
//                    check.getString(1), check.getAmount(), 
//                    check.getGlCode(), check.getGoneThrough(),
//                    check.getCalendar(1), check.getString(3));
//
//           aCB.addCheck(aCheck);
//        }
//        ArrayList trans = cB.getTransactions();
//        for(int j = 0; j < trans.size(); j++) {
//
//            Transaction tran = (Transaction) trans.get(j);
//            AMP2.BankStuff.Transaction aTran = new AMP2.BankStuff.
//                Transaction(tran.getAmount(), tran.getDescription(),
//                    tran.getDate(), tran.getGLCode(), 
//                    tran.getGoneThrough());
//
//            aCB.addTransaction(aTran);
//        }
//        
//        ArrayList bills = bH.getBills();
//        ArrayList aBills = new ArrayList();
//        for(int j = 0; j < bills.size(); j++) {
//            
//            Bill bill = (Bill) bills.get(j);
//            AMP2.BankStuff.Bill aBill = new AMP2.BankStuff.Bill(
//                bill.getAmount(), bill.getDueDate(), bill.getGLCode(),
//                bill.getRecurrenceCode(), bill.getDescription());
//            
//            aBills.add(aBill);
//        }
//        
//        ArrayList bHSSs = bH.getBHSShots();
//        ArrayList aBHSSs = new ArrayList();
//        for(int j = 0; j < bHSSs.size(); j++) {
//            
//            BHSnapShot bHSS = (BHSnapShot) bHSSs.get(j);
//            
//            AMP2.BankStuff.BHSnapShot aBHSS = new AMP2.BankStuff.BHSnapShot(
//                bHSS.getStr(0), bHSS.getStr(1), bHSS.getStr(2),bHSS.getStr(3)
//                , bHSS.getStr(4), bHSS.getStr(5), bHSS.getStr(6),
//                bHSS.getStr(7), bHSS.getStr(8), new ArrayList()); 
//                //We never used the last parameter....
//                
//            aBHSSs.add(aBHSS);
//        }
//        
//        //BankHealth
//        return new AMP2.BankStuff.BankHealth(aCB,
//            aBills, bH.getGLCodes(), aBHSSs);
//        
//    }
//    
//    /**
//     *A method that converts our ControllerG object.
//     *
//     *@param cG, our old ControllerG to be converted.
//     */
//    private static AMP2.Days.ControllerG convertCG(ControllerG cG) {
//        //ControllerG
//        AMP2.Days.ControllerG aCG = new AMP2.Days.ControllerG();
//        
//        ArrayList dayGs = cG.getDayGs();
//        for(int i = 0; i < dayGs.size(); i++) {
//            DayG dayG = (DayG) dayGs.get(i);
//            //DayG
//            AMP2.Days.DayG aDayG = new AMP2.Days.DayG(dayG.getCalendar(),
//                dayG.getGross());
//            aCG.addDayG(aDayG);//Add DayG to ControllerG
//        }
//        return aCG;
//    }
//    
//    /**
//     *A method that handles our convering of the hR.
//     *
//     *@param hR, our humanResources object to convert.
//     */
//    private static AMP2.Payroll.HumanResources convertHR(HumanResources hR) {
//        //HumanResources
//        AMP2.Payroll.HumanResources aHR = 
//                new AMP2.Payroll.HumanResources(hR.getStore());
//        
//        ArrayList emps = hR.getEmployees();
//        for(int i = 0; i < emps.size(); i++) {
//            Employee emp = (Employee) emps.get(i);
//            //Employee
//            AMP2.Payroll.Employee aEmp = new AMP2.Payroll.Employee(emp.getName(),
//                    emp.getSocS(), emp.getAddress(), emp.getClaim(), 
//                    emp.getCurrentRate());
//            aEmp.setIsCurrentEmp(emp.getIsCurrentEmp());
//            
//            aHR.addEmployee(aEmp); //HumanResources adds employee
//            ArrayList payPs = emp.getPayPeriods();
//            for(int j = 0; j < payPs.size(); j++) {
//                PayPeriod payP = (PayPeriod) payPs.get(j);
//                
//                PayDates payD = payP.getDate();
//                
//                //PayDates
//                AMP2.Payroll.PayDates aPayD = 
//                    new AMP2.Payroll.PayDates(payD.getMonthI(), 
//                        payD.getSectionI(), payD.getYear());
//                //PayPeriod
//                AMP2.Payroll.PayPeriod aPayP = 
//                    new AMP2.Payroll.PayPeriod(payP.getClaim(), aPayD,
//                        payP.getRate(), payP.getHours());//PayPeriod uses 
//                aPayP.setFica(payP.getFica());           //PayDates
//                aPayP.setOrWith(payP.getOrWith());
//                
//                aEmp.addPayPeriod(aPayP);//Employee adds PayPeriod
//            }
//        }
//        return aHR;
//    }
//    
//    /**
//     *Our method for converting...
//     *
//     *@param fileName, the name of the file to conver over.
//     */
//    public static void convertToTheFile4(String fileName) throws Exception {
        
//        FileHandler fH = new FileHandler();
//        TheFile3 tF = new TheFile3();
//        HumanResources hR;
//        ControllerG cG;
//        BankStuff.BankHealth bH;
//        Object ob = fH.readOb(new File(fileName));
//        
//        String fileName1 = "amp\\";
//        Calendar cal = Calendar.getInstance();
//        SimpleDateFormat dF = new SimpleDateFormat("MM.dd.yyyy hh.mm.ss.a");
//        fileName1 += dF.format(cal.getTime());
//        fileName1 += " " + fileName.substring(fileName.lastIndexOf("\\") + 1);
//        fH.writeObAn(fileName1, ob);
//        
//        boolean foundFile = false;
//        if(ob instanceof TheFile) {
//            tF = new TheFile3((TheFile) ob);
//            foundFile = true;
//        }
//        if(ob instanceof TheFile2){
//            tF = new TheFile3((TheFile2) ob);
//            foundFile = true;
//        }
//        if(ob instanceof TheFile3){
//            tF = (TheFile3) ob;
//            foundFile = true;
//        }
//        
//        if(foundFile) {
//            hR = tF.getHR();                       
//            cG = tF.getCG();
//            bH = tF.getBH();
//            
//            //startConverting......
//            
//            convertHR(hR);
//            convertCG(cG);
//            convertBH(bH);
//            
//            //save it
//            AMP2.Util.TheFile4 convert = new AMP2.Util.TheFile4();
//            convert.add(convertHR(hR)); convert.add(convertCG(cG)); 
//            convert.add(convertBH(bH));
//            
//            fH.writeOb(fileName + ".amf", convert);
//        }
//    }
    
}
