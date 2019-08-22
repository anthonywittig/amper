/*
 * BankStuffDBWrap.java
 *
 * Created on June 19, 2007, 7:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package AMP2.DatabaseStuff;

import AMP2.BankStuff.BHSnapShot;
import AMP2.BankStuff.BankHealth;
import AMP2.BankStuff.Bill;
import AMP2.BankStuff.Check;
import AMP2.BankStuff.CheckBook;
import AMP2.BankStuff.Currency;
import AMP2.BankStuff.GlCode;
import AMP2.BankStuff.GlCode.Code;
import AMP2.BankStuff.Transaction;
import AMP2.BankStuff.check.Checks;
import AMP2.DatabaseStuff.check.CheckUtil;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.command.SelectBuilder;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.results.Result;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Table;
import AMP2.MainDisplay.GUI;
import AMP2.Util.MiscStuff;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Anthony Wittig
 */
public class BankStuffDBWrap {
    
    static final public int TRUE = 1;
    static final public int FALSE = 0;

    

    

    
    
    /** Creates a new instance of BankStuffDBWrap */
    public BankStuffDBWrap() {
    }
    
    
    
    //checkBook
    /**
     *  This method deletes a checkBook
     *
     *  @param bankHealthID, the id of the bankHealth for which the checkBook belongs
     */
    public static void DeleteCheckBooks(int bankHealthID, BankHealth bankHealth) throws DataException{
        
//        try {
            ArrayList checkBooks = SelectCheckBooks(bankHealthID);
            
            for(int i = 0; i < checkBooks.size(); i++) {
                
                CheckBook cB = (CheckBook) checkBooks.get(i);
                int checkBookID = SelectCheckBookID(cB, bankHealthID);
                
                DeleteTransactions(checkBookID);
                
                DeleteChecks(checkBookID);
            }
            
//            GUI.getCon().GetANewStatement().execute("delete from checkbooks where " +
//                    "bankHealthID = "+ bankHealthID);
//            
            GUI.getCon().delete(Table.checkbooks, new Where(Column.BankHealthID, bankHealthID));
            
//        } catch(Exception e) {
//            throw new DataException(e);
//        }
            
        //fix up the glCodes since they have they ids, this will be
        //redundant once glCodes are flyweights (or cached by code anyway)
        for(final CheckBook cb : bankHealth.getCheckBooks()){
            for(final Check check : cb.getChecks()){
                check.getGlCode().setId(-1);
            }
            for(final Transaction trans : cb.getTransactions()){
                trans.getGLCode().setId(-1);
            }
        }
    }
    
    /**
     *  This method inserts a checkBook
     *
     *  @param cB, the checkBook from which we will pull the data
     *  @param bankHealthID, the id of the bankHealth for which the checkBook belongs
     */
    public static int InsertCheckBook(CheckBook cB, int bankHealthID) throws DataException{
        
//        try {
//            GUI.getCon().GetANewStatement().execute("insert into checkbooks(balance, 
//            waitingToGoThroughC, 
//            waitingToGoThroughT, 
//            adjustedBalance, 
//            bankHealthID)" +
//                    "values('"+cB.getDouble(0)+"',
//            '"+cB.getDouble(1)+"',
//            '"+cB.getDouble(2)+"',
//            '"+ cB.getDouble(3) +"',
//            "+bankHealthID+")");
            
            int checkBookID = GUI.getCon().insert(Table.checkbooks, 
                    new Where(Column.balance, cB.getBalance())
                    .and(Column.waitingToGoThroughC, cB.getWaitingToGoThroughC())
                    .and(Column.waitingToGoThroughT, cB.getWaitingToGoThroughT())
                    .and(Column.adjustedBalance, cB.getAdjustedBalance())
                    .and(Column.BankHealthID, bankHealthID));
            
   
            
            
            //insert transactions
            InsertTransactions(cB.getTransactions(), checkBookID);
            
            //insert checks
            InsertChecks(cB.getChecks(), checkBookID);
//            
//        } catch(SQLException e) {
//            throw new DataException(e);
//        }
            
            return checkBookID;
    }
    
    /**
     *  This method inserts CheckBooks
     *
     *  @param cBs, the CheckBooks from which we will pull the data
     *  @param bankHealthID, the id of the bankHealth for which the BHSnapShot belongs
     *
     */
    public static void InsertCheckBooks(List<CheckBook> cBs, int bankHealthID) throws DataException{
        
        for(int i = 0; i < cBs.size(); i++) {
            InsertCheckBook((CheckBook) cBs.get(i), bankHealthID);
        }
        
    }
    
    /**
     *  This method returns a CheckBook based on the checkBookID
     *
     *  @param checkBookID, the id of the checkBook for which we will make our checkBook
     *
     *  @return the CheckBook associated with the checkBookID,
     *         null if not found
     */
    public static CheckBook SelectCheckBook(int checkBookID, int bankHealthID) throws DataException {
        
        CheckBook cB = null;
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.checkbooks)
                .where(new Where(Column.ID, checkBookID));
        final Results values = GUI.getCon().select(sb.build());
        
        if(!values.isEmpty()){
            //the checkbook must exist, we could use the id given us or poke the return value
            final int cbId = (Integer) values.get(0).get(Column.ID);
            if(cbId != checkBookID){
                throw new DataException("id given and id received don't match? " + cbId + " vs " + checkBookID);
            }
            
            cB =  new CheckBook(SelectChecks(cbId), SelectTransactions(cbId));
        }
                
        return cB;
    }
    
    /**
     *Returns the id of the CheckBook in the db if a match is found
     *
     *@param cB, the CheckBook to search for,
     *@param bankHealtID, the id of the bankHealth of the BHSnapShot
     *
     *@return the CheckBook ID from the database, if < 0 they arn't there or an error
     *        occured.
     */
    public static int SelectCheckBookID(CheckBook cB, int bankHealthID) throws DataException{
//        try{
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery(
        //"select id from checkbooks where balance = '"+ cB.getDouble(0) +"' and waitingToGoThroughC = '"+ cB.getDouble(1) +"' 
        //and waitingToGoThroughT ='"+ cB.getDouble(2) +"' and adjustedBalance ='"+ cB.getDouble(3) +"' and BankHealthID = "+ bankHealthID);
            
        final SelectBuilder sb = new SelectBuilder().addValue(Column.ID)
                .table(Table.checkbooks)
                .where(new Where(Column.balance, cB.getBalance())
                .and(Column.waitingToGoThroughC, cB.getWaitingToGoThroughC())
                .and(Column.waitingToGoThroughT, cB.getWaitingToGoThroughT())
                .and(Column.adjustedBalance, cB.getAdjustedBalance())
                .and(Column.BankHealthID, bankHealthID));
        final Results values = GUI.getCon().select(sb.build());
        
            if(values.isEmpty()){
                return - 1;
            }else{
                return (Integer) values.get(0).get(Column.ID);
            }
//        } catch(Exception e){
//            throw new DataException(e);
//        }
    }
    
    /**
     *This method returns the result set of the checkBookID passed in
     *
     *@param checkBookID, the id of the checkbook to return
     *
     *@return rs, the result set of the checkBook, null if not found
     */
    public static Result SelectCheckBookRS(int checkBookID) throws DataException{
//        try{
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery("select * from checkbooks where ID = "+ checkBookID);
//            
//            if(rs.next()){
//                return rs;
//            }else{
//                return null;
//            }
//        } catch(Exception e){
//            throw new DataException(e);
//        }
        return GUI.getCon().select(
                new SelectBuilder()
                .addValue(Column._star)
                .table(Table.checkbooks)
                .where(new Where(Column.ID, checkBookID))
                .build()).get(0);
    }
    
    /**
     *  This method returns an arraylist of CheckBooks based on the bankHealthID
     *
     *  @param bankHealthID, the id of the bankHealth for which we will pull BHSnapShots
     *
     *  @return cBs, an arrayList of all the CheckBooks associated with the
     *                     bankHealthID
     */
    public static ArrayList<CheckBook> SelectCheckBooks(int bankHealthID) throws DataException {
        final ArrayList<CheckBook> cBs = new ArrayList<CheckBook>();
        
//        try{
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery("select * " +
//                    "from checkbooks where BankHealthID ="+bankHealthID);
            
        final Results values = GUI.getCon().select(
                new SelectBuilder()
                .addValue(Column.ID)
                .table(Table.checkbooks)
                .where(new Where(Column.BankHealthID, bankHealthID))
                .build());
        
        for(final Result value : values){
//            while(rs.next()) {
            final int id = (Integer) value.get(Column.ID);
                
            final CheckBook cB = new CheckBook(SelectChecks(id), SelectTransactions(id));

            cBs.add(cB);
                
        }
//        } catch(Exception e){
//            throw new DataException(e);
//        }
        
        return cBs;
    }
    
    
    
    
    
    
    
    //Transactions:
    /**
     *  This method deletes Transactions based on the checkBookID
     *
     *  @param checkBookID, the id of the checkBook for which we will delete Transactions
     */
    public static void DeleteTransactions(int checkBookID) throws DataException {
        
//        try{
//            GUI.getCon().GetANewStatement().execute("delete " +
//                    "from transactions where checkBookID = "+checkBookID);
//        } catch(Exception e){
//            throw new DataException(e);
//        }
        
        GUI.getCon().delete(Table.transactions, new Where(Column.checkBookID, checkBookID));
    }
    
    
    public static Currency getTotalPerGlCode(GlCode glCode, int checkBookID) throws DataException{
        
        return getTotalPerGlCodeTransactions(glCode, checkBookID).subtract(getTotalPerGlCodeChecks(glCode, checkBookID));
        
//        final SelectBuilder sb = new SelectBuilder().addValue(Column._star).table(Table.bills)
//                .where(new Where(Column.BankHealthID, bankHealthID));
//        
//        final Results results = GUI.getCon().select(sb.build());
    }
    
    public static Currency getTotalPerGlCodeTransactions(GlCode glCode, int checkBookID) throws DataException{
        //transactions(null, Column.ID, Column.gLCodeID, Column.amount, Column.description, Column.dateS, Column.TimeInMills, Column.goneThrough, Column.checkBookID),
    
        final SelectBuilder sb = new SelectBuilder().addValue(Column.amount).table(Table.transactions)
                .where(new Where(Column.checkBookID, checkBookID).and(Column.gLCodeID, glCode.getId()))
                ;
        
        final Results results = GUI.getCon().select(sb.build());
        
        Currency total = Currency.Zero;
        for(Result result : results){
            total = total.add(result.getCurrency(Column.amount));
        }
        
        return total;
    }
    
    public static Currency getTotalPerGlCodeChecks(GlCode glCode, int checkBookID) throws DataException{
        return CheckUtil.getTotalPerGlCodeChecks(glCode, checkBookID);
    }
    
    /**
     *  This method inserts a Transaction
     *
     *  @param trans, the Transaction from which we will pull the data
     *  @param checkBookID, the id of the checkBook for which the Transaction belongs
     *
     *  @return true if all goes well, false otherwise.
     */
    public static int InsertTransaction(Transaction trans, int checkBookID) throws DataException{
        int goneThrough = FALSE;
        
        if(trans.getGoneThrough()) {
            goneThrough = TRUE;
        }
        
        final int bankHealthID = SelectBankHealthIDFromCheckBookId(checkBookID);
        final int glCodeId = SelectGLCodeID(trans.getGLCode(), bankHealthID);
        if(glCodeId == -1){
            final int bhId = (Integer) SelectCheckBookRS(checkBookID).get(Column.BankHealthID);
            InsertGLCode(trans.getGLCode(), bhId);
        }
        
        return GUI.getCon().insert(Table.transactions, 
                new Where(Column.gLCodeID, trans.getGLCode().getId())
                .and(Column.amount, trans.getAmount())
                .and(Column.description, trans.getDescription())
                .and(Column.dateS, trans.getDateS())
                .and(Column.TimeInMills, trans.getDate().getTimeInMillis())
                .and(Column.goneThrough, goneThrough)
                .and(Column.checkBookID, checkBookID));
        
//        try {
//            return GUI.getCon().GetANewStatement().execute("insert into transactions(glID, amount, description, dateS, "
//                    + "timeInMills, goneThrough, checkBookID)" +
//                    "values('"+glId+"','"+ trans.getAmount() +"','"+ trans.getDescription() +"','"+ trans.getDateS() +"','"+ 
//                    trans.getDate().getTimeInMillis() +"','"+ goneThrough +"',"+checkBookID+")");
//        } catch(Exception e) {
//            throw new DataException("we used to return false in this situation, should we?", e);
//        }
        
    }
    
    /**
     *  This method inserts Transactions
     *
     *  @param tS, the Transactions from which we will pull the data
     *  @param checkBookID, the id of the checkBook for which the Transactions belong
     *
     */
    public static void InsertTransactions(List<Transaction> tS, int checkBookID) throws DataException{
        
        for(int i = 0; i < tS.size(); i++) {
            InsertTransaction((Transaction) tS.get(i), checkBookID);
        }
        
    }
    
    /**
     *Returns the id of the Transaction in the db if a match is found
     *
     *@param trans, the Transaction to search for,
     *@param checkBookID, the id of the checkBook of the Transaction
     *
     *@return the Transaction ID from the database, if < 0 they arn't there or an error
     *        occured.
     */
    public static int SelectTransactionID(Transaction trans, int checkBookID) throws DataException{
        
        int goneThrough = FALSE;
        
        if(trans.getGoneThrough()) {
            goneThrough = TRUE;
        }
        
        //final int glCodeId = SelectGLCodeID(trans.getGLCode(), checkBookID);
        if(trans.getGLCode().getId() == -1){
            //get bankHealthId
            final SelectBuilder sb = new SelectBuilder().addValue(Column.BankHealthID).table(Table.checkbooks)
                    .where(new Where(Column.ID, checkBookID));
            
            final Results values = GUI.getCon().select(sb.build());
            if(values.isEmpty()){
                MiscStuff.writeToLog(new Exception("couldn't get the bank health id from the check books, check book id was: " + checkBookID));
            }
            
            final int bankHealthId = (Integer) values.get(0).get(Column.BankHealthID);
            
            //the whole point of us getting the bankhealthId:
            InsertGLCode(trans.getGLCode(), bankHealthId);
        }
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.transactions)
                .where(new Where(Column.gLCodeID, trans.getGLCode().getId())
                .and(Column.amount, trans.getAmount())
                .and(Column.description, trans.getDescription())
                .and(Column.dateS, trans.getDateS())
                .and(Column.TimeInMills, trans.getDate().getTimeInMillis())
                .and(Column.goneThrough, goneThrough)
                .and(Column.checkBookID, checkBookID)
                );
        
        final Results values = GUI.getCon().select(sb.build());
        if(values.isEmpty()){
            return -1;
        }else{
            return (Integer) values.get(0).get(Column.ID);
        }
        
//        try{
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery(
//                    "select id from transactions where " +
//                    "glID = '"+ glId + "' " +
//                    "and amount = '"+ trans.getAmount() +"' " +
//                    "and description ='"+ trans.getDescription() +"' " +
//                    "and dateS ='"+ trans.getDateS() +"' " +
//                    "and timeInMills ='"+ trans.getDate().getTimeInMillis() +"' " +
//                    Error..."and description ='"+ goneThrough +"' " +
//                    "and checkBookID = "+ checkBookID);
//            
//            if(rs.next()){
//                return rs.getInt(1);
//            }else{
//                return -1;
//            }
//        } catch(Exception e){
//            throw new DataException(e);
//        }
        
        
    }
    
    /**
     *  This method returns an arraylist of Transactions based on the checkBookID
     *
     *  @param checkBookID, the id of the checkBook for which we will pull Transactions
     *
     *  @return tS, an arrayList of all the Transactions associated with the
     *                     checkBookID
     */
    public static List<Transaction> SelectTransactions(int checkBookID) throws DataException {
        ArrayList<Transaction> tS = new ArrayList<Transaction>();
        
        final SelectBuilder sb = new SelectBuilder()
                .addValue(Column.amount).addValue(Column.description).addValue(Column.gLCodeID).addValue(Column.TimeInMills).addValue(Column.goneThrough)
                .table(Table.transactions)
                .where(new Where(Column.checkBookID, checkBookID));
        
       final Results results = GUI.getCon().select(sb.build());
        
        
//        try{
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery("select * " +
//                    "from transactions where checkBookID ="+checkBookID);
            
            //while(rs.next()) {
        for(final Result result : results){

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(result.getLong(Column.TimeInMills));

            boolean goneThrough = false;
            if(result.getInteger(Column.goneThrough) == TRUE) {
                goneThrough = true;
            }

            //final int glCode = SelectGLCode(result.getInteger(Column.glID)).getInteger(Column.code);
            
            final GlCode glCode = SelectGLCode(result.getInteger(Column.gLCodeID));
            
            Transaction trans = new Transaction(result.getCurrency(Column.amount),
                    result.getString(Column.description),
                    cal, glCode, goneThrough);

            trans.setDate(cal);
            
            

            tS.add(trans);

        }
//        } catch(Exception e){
//            throw new DataException(e);
//        }
        return tS;
    }
    
    
    
    
    
    
    //Checks:
    /**
     *  This method deletes Checks based on the checkBookID
     *
     *  @param checkBookID, the id of the checkBook for which we will delete Checks
     */
    public static void DeleteChecks(int checkBookID) throws DataException {
        CheckUtil.DeleteChecks(checkBookID);
    }
    
    /**
     *  This method inserts a Check
     *
     *  @param ch, the Check from which we will pull the data
     *  @param checkBookID, the id of the checkBook for which the checks belongs
     *
     *  @return true if all goes well, false otherwise.
     */
    public static int InsertCheck(Check ch, int checkBookID) throws DataException{
        return CheckUtil.InsertCheck(ch, checkBookID);    
    }
    
    /**
     *  This method inserts Checks
     *
     *  @param cS, the Checks from which we will pull the data
     *  @param checkBookID, the id of the checkBook for which the Checks belong
     *
     */
    public static void InsertChecks(Checks cS, int checkBookID) throws DataException{
        
        for(Check check : cS) {
            InsertCheck(check, checkBookID);
        }
        
    }
    
    /**
     *Returns the id of the check in the db if a match is found
     *
     *@param ch, the check to search for,
     *@param checkBookID, the id of the checkBook of the check
     *
     *@return the check ID from the database, if < 0 they arn't there or an error
     *        occured.
     */
    public static int SelectCheckID(Check ch, int checkBookID) throws DataException{
        return CheckUtil.SelectCheckID(ch, checkBookID);
    }
    
    /**
     *  This method returns an arraylist of Checks based on the checkBookID
     *
     *  @param checkBookID, the id of the checkBook for which we will pull Checks
     *
     *  @return cS, an arrayList of all the Checks associated with the
     *                     checkBookID
     */
    public static Checks SelectChecks(int checkBookID) throws DataException {
        return CheckUtil.SelectChecks(checkBookID);
    }
    
    
    
    
    
    //glcodes:
    
    static void DeleteGLCode(GlCode glCode, int bankHealthId) throws DataException {
        DeleteGLCode(glCode.getCode(), glCode.getDescription(), bankHealthId);
    }
    /**
     *  This method deletes glCodes
     *
     *  @param code, the code of the gl item
     *  @param description, the description of the gl item
     *  @param bankHealthID, the id of the bankHealth for which the glCode belongs
     *
     *  @return true if all goes well, false otherwise.
     */
    public static void DeleteGLCode(Code code, String description, int bankHealthID) throws DataException{
        
        GUI.getCon().delete(Table.glcodes, 
                new Where(Column.code, code.getCode())
                .and(Column.description, description)
                .and(Column.BankHealthID, bankHealthID));
        
//        try {
//            return GUI.getCon().GetANewStatement().execute("delete from " +
//                    "glCodes where code = '" + code +
//                    "' and description =  '"+description+"' +" +
//                    "and BankHealthID = " + bankHealthID);
//        } catch(Exception e) {
//            throw new DataException(e);
//        }
        
    }
    
    /**
     *  This method deletes glCodes
     *
     *  @param bankHealthID, the id of the bankHealth to delete glCodes from
     *
     *  @return true if all goes well, false otherwise.
     */
    public static void DeleteGLCodes(Collection<GlCode> glCodes) throws DataException{
        
//        GUI.getCon().delete(Table.glcodes, 
//                new Where(Column.BankHealthID, bankHealthID));
        
        for(GlCode glCode : glCodes){
            deleteGlCode(glCode);
        }
        
        
        
//        try {
//            return GUI.getCon().GetANewStatement().execute("delete from " +
//                    "glCodes where BankHealthID = " + bankHealthID);
//        } catch(Exception e) {
//            throw new DataException(e);
//        }
        
    }
    
    public static void deleteGlCode(GlCode glCode) throws DataException{
        if(glCode.getId() < 0){
            //throw new DataException("Expecting id greater than 0: " + glCode);
            return;
        }
        
        GUI.getCon().delete(Table.glcodes, 
                new Where(Column.ID, glCode.getId()));
        
        //mark as not in the db:
        glCode.setId(-1);
    }
    
    public static int InsertGLCode(Code code, String description, int bankHealthId) throws DataException{
        return InsertGLCode(new GlCode(code, description, bankHealthId), bankHealthId);
    }
    
    public static int InsertGLCode(GlCode glCode, int bankHealthId) throws DataException {
        //return InsertGLCode(glCode.getCode().getCode(), glCode.getDescription(), glCode.getBankHealthID());
        
        //make sure it isn't already there:
        SelectGLCodeID(glCode, bankHealthId);
        
        if(glCode.getId() != -1){
            return glCode.getId();
        }
        
        final int glCodeId = GUI.getCon().insert(Table.glcodes, 
                new Where(Column.code, glCode.getCode().getCode())
                .and(Column.description, glCode.getDescription())
                .and(Column.BankHealthID, bankHealthId));
        
        glCode.setId(glCodeId);
        glCode.setBankHealthId(bankHealthId);
        
        return glCodeId;
        
        
    }
    
    /**
     *  This method inserts a glCodes
     *
     *  @param code, the code of the gl item
     *  @param description, the description of the gl item
     *  @param bankHealthID, the id of the bankHealth for which the BHSnapShot belongs
     *
     *  
     */
//    public static int InsertGLCode(int code, String description, int bankHealthID) throws DataException{
//        
//        return GUI.getCon().insert(Table.glcodes, 
//                new Where(Column.code, code)
//                .and(Column.description, description)
//                .and(Column.BankHealthID, bankHealthID));
//        
////        try {
////            //MiscStuff.writeToLog(code + ", " + description + ", " + bankHealthID);
////            GUI.getCon().GetANewStatement().execute("insert into glCodes(code, description, BankHealthID) values('"+code+"','"+description+"',"+bankHealthID+")");
////                
////            
//////            return GUI.getCon().GetANewStatement().execute("insert into glCodes(code, description, BankHealthID)" +
//////                    "values('"+code+"','"+description+"',"+bankHealthID+");");
////        } catch(Exception e) {
////            throw new DataException(e);
////        }
//        
//    }
    
    /**
     *  This method inserts gLCodes
     *
     *  @param gLs, the gLCodes from which we will pull the data
     *  @param bankHealthID, the id of the bankHealth for which the BHSnapShot belongs
     *
     */
    public static void InsertGLCodes(Collection<GlCode> gLs, int bankHealthID) throws DataException{
//        Iterator it = gLs.keySet().iterator();
//        //MiscStuff.writeToLog(gLs.values().toString());
//        
//        while(it.hasNext()) {
//            //MiscStuff.writeToLog(".............1");
//            int code = Integer.parseInt(it.next().toString());
//            String description = (String) gLs.get(code);
//            InsertGLCode(code, description, bankHealthID);
//            //MiscStuff.writeToLog(".............................2");
//            
//        }
        
        for(GlCode glCode : gLs){
            InsertGLCode(glCode, bankHealthID);
        }
        
        
    }
    
    /**
     *This method returns the result set for the given gl code based on the
     *gLCodeID and the bankHealthID.
     *
     *@param gLCodeID, the id of the gLCode to return
     *
     *@return rs, the result set of the gLCode, null if not found
     */
    public static GlCode SelectGLCode(int gLCodeID) throws DataException {
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column._star).table(Table.glcodes).where(
                new Where(Column.ID, gLCodeID));
        
        final Results results = GUI.getCon().select(sb.build());
        
        if(results.isEmpty()){
            //return null;
            //maybe returnning null is better?
            
            final SelectBuilder sbTest = new SelectBuilder().addValue(Column._star).table(Table.glcodes);
            final Results resultsTest = GUI.getCon().select(sbTest.build());
            
            throw new DataException("no glCode selected for gLCodeID: " + gLCodeID + ", here's what we've got in the db: " + resultsTest);
        }else{
            final Result result = results.get(0);
            return new GlCode(result.getInteger(Column.ID), new Code(result.getInteger(Column.code)), result.getString(Column.description), result.getInteger(Column.BankHealthID));
        }
        
//        try{
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery("select * from glcodes where ID = "+ gLCodeID);
//            
//            if(rs.next()){
//                return rs;
//            }else{
//                return null;
//            }
//        } catch(Exception e){
//            throw new DataException("SelectGLCode: SelectGLCode: " + gLCodeID, e);
//            
//        }
    }
    
    /**
     *Returns the id of the GLCode in the db if a match is found
     *
     *@param code, the code of the gl to search for,
     *@param bankHealtID, the id of the bankHealth of the BHSnapShot
     *
     *@return the gLCode ID from the database, if < 0 they arn't there or an error
     *        occured.
     */
    public static int SelectGLCodeID(GlCode code, int bankHealthID) throws DataException{
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.glcodes).where(
                new Where(Column.code, code.getCode().getCode())
                .and(Column.BankHealthID, bankHealthID));
        
        final Results results = GUI.getCon().select(sb.build());
        
        if(results.isEmpty()){
            code.setId(-1);
            code.setBankHealthId(bankHealthID);
        }else{
            final Result result = results.get(0);
            code.setId(result.getInteger(Column.ID));
            code.setBankHealthId(bankHealthID);
        }
        
        return code.getId();
//        
//        try{
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery("select id from glcodes where code = '"+code+
//                    "' and BankHealthID = "+ bankHealthID);
//            
//            if(rs.next()){
//                return rs.getInt(1);
//            }else{
//                return -1;
//            }
//        } catch(Exception e){
//            throw new DataException(e);
//        }
        
        
    }
    
    /**
     *  This method returns a hashMap of GLCodes based on the bankHealthID
     *
     *  @param bankHealthID, the id of the bankHealth for which we will pull BHSnapShots
     *
     *  @return gLs, a HashMap of all the GLCodes associated with the
     *                     bankHealthID
     */
    public static Map<Code, GlCode> SelectGLCodes(int bankHealthID) throws DataException {
        final Map<Code, GlCode> gLs = new HashMap<Code, GlCode>();
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column._star).table(Table.glcodes).where(
                new Where(Column.BankHealthID, bankHealthID));
        final Results results = GUI.getCon().select(sb.build());
        
//        try{
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery("select * " +
//                    "from glcodes where BankHealthID ="+bankHealthID);
            
            //while(rs.next()) {
            for(final Result result : results){
                final GlCode glCode = new GlCode(
                        result.getInteger(Column.ID), 
                        new Code(result.getInteger(Column.code)), 
                        result.getString(Column.description),
                        result.getInteger(Column.BankHealthID));
                
                gLs.put(glCode.getCode(), glCode);
            }
//        } catch(SQLException e){
//            throw new DataException(e);
//        }
        
        return gLs;
    }
    
    
    
    
    
    //BHSnapShot:
    
    /**
     *  This method deletes BHSnapShots
     *
     *  @param bankHealthID, the id of the bankHealth to delete BHSnapShots from
     *
     *  @return true if all goes well, false otherwise.
     */
    public static void DeleteBHSnapShots(int bankHealthID) throws DataException{
        
        GUI.getCon().delete(Table.bankhealthsnapshot, new Where(Column.BankHealthID, bankHealthID));
        
//        try {
//            return GUI.getCon().GetANewStatement().execute("delete from " +
//                    "bankhealthsnapshot where BankHealthID = " + bankHealthID);
//        } catch(Exception e) {
//            throw new DataException(e);
//        }
        
    }
    /**
     *  This method inserts a BHSnapShot
     *
     *  @param sS, the BHSnapShot from which we will pull the data
     *  @param bankHealthID, the id of the bankHealth for which the BHSnapShot belongs
     *
     *  @return true if all goes well, false otherwise.
     */
    public static int InsertBHSnapShot(BHSnapShot sS, int bankHealthID) throws DataException{
        
        final Where where = Where.getWhere(
                new Column[]{Column.dateS, Column.BallanceS, Column.UnpostedCBS, Column.UnpostedDBS, Column.AdjustedBS, Column.PercentUS, Column.FutureBillsS, Column.AdjustedB2S, Column.EvenDS, Column.FutureBillsID, Column.BankHealthID},
                new Object[]{sS.getStr(0),sS.getStr(1),sS.getStr(2), sS.getStr(3) , sS.getStr(4) , sS.getStr(5) , sS.getStr(6) , sS.getStr(7) , sS.getStr(8) , -1 ,bankHealthID});
        
        return GUI.getCon().insert(Table.bankhealthsnapshot, where);
//        
//        try {
//            return GUI.getCon().GetANewStatement().execute("insert into bankhealthsnapshot(DateS, BallanceS, UnpostedCBS, UnpostedDBS, AdjustedBS, PercentUS, FutureBillsS, AdjustedB2S, EvenDS, FutureBillsID, BankHealthID)" +
//                    "values('"+sS.getStr(0)+"','"+sS.getStr(1)+"','"+sS.getStr(2)+"','"+ sS.getStr(3) +"','"+ sS.getStr(4) +"','"+ sS.getStr(5) +"','"+ sS.getStr(6) +"','"+ sS.getStr(7) +"','"+ sS.getStr(8) +"',"+ -1 +","+bankHealthID+")");
//        } catch(Exception e) {
//            throw new DataException(e);
//        }
        
    }
    
    /**
     *  This method inserts BHSnapShots
     *
     *  @param sSs, the BHSnapShots from which we will pull the data
     *  @param bankHealthID, the id of the bankHealth for which the BHSnapShot belongs
     *
     */
    public static void InsertBHSnapShots(List<BHSnapShot> sSs, int bankHealthID) throws DataException{
        
        for(int i = 0; i < sSs.size(); i++) {
            InsertBHSnapShot(sSs.get(i), bankHealthID);
        }
        
    }
    
    /**
     *Returns the id of the BHSnapShot in the db if a match is found
     *
     *@param sS, the BHSnapShot to search for,
     *@param bankHealtID, the id of the bankHealth of the BHSnapShot
     *
     *@return the BHSnapShot ID from the database, if < 0 they arn't there or an error
     *        occured.
     */
    public static int SelectBHSnapShotID(BHSnapShot sS, int bankHealthID) throws DataException{
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.bankhealthsnapshot)
                .where(new Where(Column.dateS,  sS.getStr(0))
                .and(Column.BallanceS, sS.getStr(1))
                .and(Column.UnpostedCBS, sS.getStr(2))
                .and(Column.BankHealthID, bankHealthID));
        
        return GUI.getCon().selectIdOrNegOne(sb.build());
        
//        try{
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery("select id from bhsnapshot where DateS = '"+sS.getStr(0)+"' and BallanceS = '"+sS.getStr(1)+"' and UnpostedCBS ='"+sS.getStr(2)+"' and BankHealthID = "+ bankHealthID);
//            
//            if(rs.next()){
//                return rs.getInt(1);
//            }else{
//                return -1;
//            }
//        } catch(Exception e){
//            throw new DataException(e);
//        }
        
        
    }
    
    /**
     *  This method returns an arraylist of bankHealthIDs based on the bankHealthID
     *
     *  @param bankHealthID, the id of the bankHealth for which we will pull BHSnapShots
     *
     *  @return sSs, an arrayList of all the BHSnapShots associated with the
     *                     bankHealthID
     */
    public static List<BHSnapShot> SelectBHSnapShots(int bankHealthID) throws DataException {
        List<BHSnapShot> sSs = new ArrayList<BHSnapShot>();
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column._star).table(Table.bankhealthsnapshot)
                .where(new Where(Column.BankHealthID, bankHealthID));
        final Results results = GUI.getCon().select(sb.build());
        
//        try{
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery("select * " +
//                    "from bankhealthsnapshot where BankHealthID ="+bankHealthID);
            
//            while(rs.next()) {
            for(final Result rs : results){
                
                BHSnapShot sS = new BHSnapShot(rs.getString(Column.dateS), rs.getString(Column.BallanceS),
                        rs.getString(Column.UnpostedCBS), rs.getString(Column.UnpostedDBS),
                        rs.getString(Column.AdjustedBS), rs.getString(Column.PercentUS),
                        rs.getString(Column.FutureBillsS), rs.getString(Column.AdjustedB2S),
                        rs.getString(Column.EvenDS));
                
                sSs.add(sS);
                
            }
//        } catch(SQLException e){
//            throw new DataException(e);
//        }
        
        return sSs;
    }
    
    
    
    
    
    
    
    
    
    //recurrence codes:
    /**
     *  This method inserts a recurrence code
     *
     *  @param code, the code of the recurrence code
     *  @param description, the description of the recurrence code
     *
     *  @return true if all goes well, false otherwise.
     */
//    public static boolean InsertRecurrenceCode(int code, String description) throws DataException{
//        
//        try {
//            return GUI.getCon().GetANewStatement().execute("insert into recurrencecode(code, description)" +
//                    "values('"+ code +"','"+ description+ "')");
//        } catch(Exception e) {
//            throw new DataException(e);
//        }
//    }
    
    /**
     *Returns the description of the RecurrenceCode in the db if a match is found
     *
     *@param code, the code of the RecurrenceCode to search for,
     *
     *@return the description from the database, if < 0 they arn't there or an error
     *        occured.
     */
//    public static int SelectRecurrenceCodeDescription(int code) throws DataException{
//        
//        try{
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery(
//                    "select description from recurrencecode where " +
//                    "code = "+ code);
//            
//            if(rs.next()){
//                return rs.getInt(1);
//            }else{
//                return -1;
//            }
//        } catch(Exception e){
//            throw new DataException(e);
//        }
//    }
    
    
    
    
    
    
    
    
    //Bills:
    
    /**
     *  This method deletes bills
     *
     *  @param bankHealthID, the id of the bankHealth to delete bills from
     *
     *  @return true if all goes well, false otherwise.
     */
    public static void DeleteBills(int bankHealthID, BankHealth bankHealth) throws DataException{
        
        GUI.getCon().delete(Table.bills, new Where(Column.BankHealthID, bankHealthID));
        
        //fix up the glCodes since they have they ids, this will be
        //redundant once glCodes are flyweights (or cached by code anyway)
        for(final Bill bill : bankHealth.getBills()){
            bill.getGLCode().setId(-1);
        }
//        
//        try {
//            return GUI.getCon().GetANewStatement().execute("delete from " +
//                    "bills where BankHealthID = " + bankHealthID);
//        } catch(Exception e) {
//            throw new DataException(e);
//        }
        
    }
    /**
     *  This method inserts a Bill
     *
     *  @param bill, the Bill from which we will pull the data
     *  @param bankHealthID, the id of the bankHealth for which the Bill belongs
     *
     *  @return true if all goes well, false otherwise.
     */
    public static int InsertBill(Bill bill, int bankHealthID) throws DataException{
        
        int paid = FALSE;
        
        if(bill.getIsPaid()) {
            paid = TRUE;
        }
        
        final int glCodeId = SelectGLCodeID(bill.getGLCode(), bankHealthID);
        if(glCodeId == -1){
            InsertGLCode(bill.getGLCode(), bankHealthID);
        }
        
        
        final Where where = Where.getWhere(
                new Column[]{Column.gLCodeID, Column.dueDate, Column.recurrenceCode, Column.lastMonthPaid, Column.description, Column.amount, Column.isPaid, Column.BankHealthID},
                new Object[]{bill.getGLCode().getId(), bill.getDueDate() , bill.getRecurrenceCode() , bill.getLastMonthPaid() , bill.getDescription() , bill.getAmount() , paid, bankHealthID});
        
        return GUI.getCon().insert(Table.bills, where);
        
//        try {
//            return GUI.getCon().GetANewStatement().execute("insert into bills(gLCodeID, dueDate, recurrenceCode, lastMonthPaid, description, amount, isPaid, bankHealthID) " +
//                    "values("+ glCodeId +",'"+ bill.getDueDate() +"','"+ bill.getRecurrenceCode() +"','"+ bill.getLastMonthPaid() +"','"+ bill.getDescription() +"','"+ bill.getAmount() +"','"+ paid +"',"+bankHealthID+")");
//        } catch(Exception e) {
//            throw new DataException(e);
//        }
        
    }
    
    /**
     *  This method inserts Bills
     *
     *  @param bills, the Bills from which we will pull the data
     *  @param bankHealthID, the id of the bankHealth for which the Bills belong
     *
     */
    public static void InsertBills(List<Bill> bills, int bankHealthID) throws DataException{
        
        for(int i = 0; i < bills.size(); i++) {
            InsertBill((Bill) bills.get(i), bankHealthID);
        }
        
    }
    
    /**
     *Returns the id of the Bill in the db if a match is found
     *
     *@param bill, the Bill to search for,
     *@param bankHealtID, the id of the bankHealth of the Bill
     *
     *@return the Bill ID from the database, if < 0 they arn't there or an error
     *        occured.
     */
    public static int SelectBillID(Bill bill, int bankHealthID) throws DataException{
        
        int paid = FALSE;
        
        if(bill.getIsPaid()) {
            paid = TRUE;
        }
        
        final int glCodeId = SelectGLCodeID(bill.getGLCode(), bankHealthID);
        if(glCodeId == -1){
            InsertGLCode(bill.getGLCode(), bankHealthID);
        }
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.bills)
                .where(new Where(Column.gLCodeID , bill.getGLCode().getId())
                    .and(Column.dueDate , bill.getDueDate())
                    .and(Column.recurrenceCode , bill.getRecurrenceCode() )
                    .and(Column.lastMonthPaid , bill.getLastMonthPaid())
                    .and(Column.description , bill.getDescription() )
                    .and(Column.amount , bill.getAmount())
                    .and(Column.isPaid , paid )
                    .and(Column.BankHealthID , bankHealthID));
        
        return GUI.getCon().selectIdOrNegOne(sb.build());
        
//        try{
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery("select id from bills where " +
//                    "gLCodeID = "+ glCodeId + " " +
//                    "and dueDate = '"+ bill.getDueDate() +"' " +
//                    "and recurrenceCode ='"+ bill.getRecurrenceCode() +"' " +
//                    "and lastMonthPaid ='"+ bill.getLastMonthPaid() +"' " +
//                    "and description ='"+ bill.getDescription() +"' " +
//                    "and amount ='"+ bill.getAmount() +"' " +
//                    "and isPaid ='"+ paid +"' " +
//                    "and BankHealthID = "+ bankHealthID);
//            
//            if(rs.next()){
//                return rs.getInt(1);
//            }else{
//                return -1;
//            }
//        } catch(Exception e){
//            throw new DataException(e);
//        }
    }
    
    /**
     *  This method returns an arraylist of Bills based on the bankHealthID
     *
     *  @param bankHealthID, the id of the bankHealth for which we will pull Bills
     *
     *  @return bills, an arrayList of all the Bills associated with the
     *                     bankHealthID
     */
    public static List<Bill> SelectBills(int bankHealthID) throws DataException {
        final List<Bill> bills = new ArrayList<Bill>();
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column._star).table(Table.bills)
                .where(new Where(Column.BankHealthID, bankHealthID));
        
        final Results results = GUI.getCon().select(sb.build());
      
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery("select * " +
//                    "from bills where BankHealthID ="+bankHealthID);
            
//            while(rs.next()) {
            for(final Result rs : results){  
            
                boolean paid = false;
                if(rs.getInteger(Column.isPaid) == TRUE) {
                    paid = true;
                }
                
                final Currency amount = rs.getCurrency(Column.amount);
                final Integer dueDate = rs.getInteger(Column.dueDate);
                final Integer glCodeId = rs.getInteger(Column.gLCodeID);
                final GlCode glCode = SelectGLCode(glCodeId);
                final Integer recurrenceCode = rs.getInteger(Column.recurrenceCode);
                final String description = rs.getString(Column.description);
                
                Bill bill = new Bill(amount, dueDate,
                        glCode,
                        recurrenceCode, description);
                
                //the bills won't be working right........
                //bill.setIsPaid(paid);
                //we need a bill.setLastPaid...
                
                bills.add(bill);
                
            }
        
        
        return bills;
    }
    
    
    
    
    
    //BankHealths:
    /**
     *  This method deletes a BankHealth
     *
     *  @param companyID, the id of the company for which to delete the bankHealth
     */
    public static void DeleteBankHealth(int companyID, BankHealth bankHealth) throws DataException{
        
//        try {
            
            int bankHealthID = SelectBankHealthID(companyID);
           
            //deleteCheckBooks does checks and transactions
            DeleteCheckBooks(bankHealthID, bankHealth);
            DeleteBills(bankHealthID, bankHealth);
            DeleteBHSnapShots(bankHealthID);
            DeleteGLCodes(bankHealth.getGLCodes().values());
            
            GUI.getCon().delete(Table.bankhealth, new Where(Column.ID, bankHealthID));
////            
////            GUI.getCon().GetANewStatement().execute("delete from bankhealth where " +
////                    "ID = "+ bankHealthID);
//        } catch(SQLException e) {
//            throw new DataException(e);
//            
//        }
        
    }
    
    /**
     *  This method inserts a BankHealth
     *
     *  @param bH, the BankHealth from which we will pull the data
     *  @param companyID, the id of the company which owns this bankHealth
     */
    public static int InsertBankHealth(BankHealth bH, int companyID) throws DataException{
        
        final int bankHealthID = GUI.getCon().insert(Table.bankhealth, new Where(Column.companyID, companyID).and(Column.currentCheckBookID, -1));
        
//            GUI.getCon().GetANewStatement().execute("insert into bankhealth(CompanyID, currentCheckBookID) " +
//                    "values("+ companyID +", -1)");
            
            //int bankHealthID = SelectBankHealthID(companyID);
            
            
        InsertGLCodes(bH.getGLCodes().values(), bankHealthID);
        InsertCheckBooks(bH.getCheckBooks(), bankHealthID);
        InsertBills(bH.getBills(), bankHealthID);
        InsertBHSnapShots(bH.getBHSShots(), bankHealthID);

        final int checkBookId = SelectCheckBookID(bH.getCurrentCheckBook(), bankHealthID);

        GUI.getCon().update(Table.bankhealth, new Where(Column.currentCheckBookID, checkBookId), new Where(Column.ID, bankHealthID));

//            GUI.getCon().GetANewStatement().execute("update bankhealth set " +
//                    "currentCheckBookID =" + checkBookId + " " +
//                    "where ID =" + bankHealthID);

        return bankHealthID;
          
        
    }
    
//    /**
//     *  This method inserts BankHealths
//     *
//     *  @param bHs, the BankHealths from which we will pull the data
//     *  @param companyID, the id of the company for which the BankHealths belong
//     *
//     */
//    public static void InsertBankHealths(ArrayList bHs, int companyID) throws DataException{
//        
//        for(int i = 0; i < bHs.size(); i++) {
//            InsertBankHealth((BankHealth) bHs.get(i), companyID);
//        }
//        
//    }
    
    /**
     *  This method returns the result set of a bankHealth based on the companyID
     *
     *  @param companyID, the id of the company for which we will pull the bankHealth
     *
     *  @return the resultSet of the bankHealth that matches our companyID
     */
    public static Result SelectBankHealth(int companyID) throws DataException {
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column._star).table(Table.bankhealth)
                .where(new Where(Column.companyID, companyID));
        
        final Results values = GUI.getCon().select(sb.build());
        
        if(values.size() > 1){
            throw new DataException("More than one bankhealth with companyId = " + companyID);
        }else if(values.size() == 1){
            return values.get(0);
        }else{
            return null;
        }
    }
    
    /**
     *Returns the id of the BankHealth in the db if a match is found
     *
     *@param companyID, the id of the companyID of the BankHealth
     *
     *@return the BankHealth ID from the database, if < 0 they arn't there or an error
     *        occured.
     */
    public static int SelectBankHealthID(int companyID) throws DataException, DataException, DataException{
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.bankhealth)
                .where(new Where(Column.companyID, companyID));
        return GUI.getCon().selectIdOrNegOne(sb.build());
        
//        try{
//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery(
//                    "select id from bankhealth where " +
//                    "companyID = "+ companyID);
//            
//            if(rs.next()){
//                return rs.getInt(1);
//            }else{
//                return -1;
//            }
//        } catch(Exception e){
//            throw new DataException(e);
//        }
        
        
    }
    
    public static int SelectBankHealthIDFromCheckBookId(int checkbookId) throws DataException{
        return (Integer) SelectCheckBookRS(checkbookId).get(Column.BankHealthID);
    }
    
    
    
    
    
}
