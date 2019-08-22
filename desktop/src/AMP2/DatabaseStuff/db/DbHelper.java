/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.db;

import AMP2.BankStuff.Currency;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.command.Select;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.command.Where.Operator;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Order;
import AMP2.DatabaseStuff.tables.Table;
import AMP2.Util.MiscStuff;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author awittig
 */
public final class DbHelper {

    public final static SimpleDateFormat DB_NAME_FORMAT = new SimpleDateFormat("yyyy.MM.dd_hh.mm.ss.a");

    
    private DbHelper() {}

    static void deleteOldBackups(final File backupDirectory) throws DataException{
                
        final List<File> sortedBackups = new ArrayList<File>(Arrays.asList(backupDirectory.listFiles()));
        Collections.sort(sortedBackups); 
        Collections.reverse(sortedBackups);
        
        //keep 50 most recent, keep at least 2 per month
        int currentMonth = -1;
        int filesInMonth = 0;
        final Calendar timeOfSave = Calendar.getInstance();
        final int maxNumberOfBackups = 100;
        for (int fileIdx = 50; fileIdx < sortedBackups.size() && maxNumberOfBackups < sortedBackups.size(); ++fileIdx){
            try {
                timeOfSave.setTime(DbHelper.DB_NAME_FORMAT.parse(sortedBackups.get(fileIdx).getName()));
            } catch (ParseException ex) {
                throw new DataException(ex);
            }
            
            int month = timeOfSave.get(Calendar.MONTH);
            
            if (month != currentMonth){
		currentMonth = month;
		filesInMonth = 0;
            }
            
            ++filesInMonth;
            if (2 < filesInMonth){
                deleteOldBackups_deleteAndRemoveFile(sortedBackups, fileIdx);
                --fileIdx;
            }		
        }


//        //delete all but the newest 100
//        while (maxNumberOfBackups < sortedBackups.size()){
//            deleteOldBackups_deleteAndRemoveFile(sortedBackups, sortedBackups.size());
//        }
        
        
        
        
        
        
        
        
//        
//        try {
//        
//            final Calendar thisMonthC = Calendar.getInstance();
//            thisMonthC.set(Calendar.DAY_OF_MONTH, 0);
//            
//            final List<File> backupsFromThisMonth = getNamesBefore(thisMonthC, sortedBackups);
//            
//            //up to 50 from this month
//            final int backupsForThisMonthCount = 50;
//            while(backupsForThisMonthCount < backupsFromThisMonth.size()){
//                final File toDel = backupsFromThisMonth.remove(0);
//                if(!toDel.delete()){
//                    throw new DataException("can't delete " + toDel.getAbsolutePath());
//                }
//            }
//            
//            //up to 2 from each month for the last 12 months:
//            final int backsupsForEachMonthInLast12Months = 2;
//            for(int monthsAgo = 1; monthsAgo < 13; ++monthsAgo){
//                final Calendar monthToCompare = (Calendar) thisMonthC.clone();
//                monthToCompare.roll(Calendar.MONTH, false);
//                
//                final List<File> filesAfterMonth = getNamesBefore(monthToCompare, sortedBackups);
//                final int backupseForUpToThisMonth = backupsForThisMonthCount + (backsupsForEachMonthInLast12Months * monthsAgo);
//                
//                while(backupseForUpToThisMonth < filesAfterMonth.size()){
//                    final File toDel = filesAfterMonth.remove(0);
//                    if(!toDel.delete()){
//                        throw new DataException("can't delete " + toDel.getAbsolutePath());
//                    }
//                }
//                
//            }
//            
//            
//        } catch (ParseException ex) {
//            throw new DataException("can't parse name", ex);
//        }
//        
//        
//        
//        //up to 100 backups total
//        while(100 < sortedBackups.size()){
//            final File file = sortedBackups.remove(sortedBackups.size() - 1);
//            if(! file.delete()){
//                throw new DataException("can't delete file" + file.getAbsolutePath());
//            }
//        }
        
        
        
        
    }
    
    
    private static void deleteOldBackups_deleteAndRemoveFile(List<File> sortedBackups, int fileIdx) throws DataException {
        try{
            MiscStuff.deleteRecursive(sortedBackups.remove(fileIdx));
//            return --fileIdx;
        }catch(Exception e){
            throw new DataException(e);
        }
    }
    
    static Results fill(final Select select, final ResultSet rs) throws SQLException, DataException {
        final Results values = Results.getResults(select, rs);

        return values;
    }
    
    private static String getOrderByClause(List<Pair<Column, Order>> orderBy){
        if(orderBy.isEmpty()){
            return "";
        }
        
//ORDER BY "column_name1" [ASC, DESC], "column_name2" [ASC, DESC]
        final StringBuilder cause = new StringBuilder();
        cause.append(" ORDER BY");
        
        boolean isFirst = true;
        for(Pair<Column, Order> term : orderBy){
        //for(final Column col : orderBy){
            
            if(!isFirst){
                cause.append(", ");
            }
            isFirst = false;

            cause.append(" ").append(term.getLeft().colName())
                    .append(" ").append(term.getRight());
        }
        
        return cause.toString();
        
        
    }
    
    static String getWhereClause(Where where){
        if(where == null){
            return "";
        }
        
        final StringBuilder cause = new StringBuilder();
        cause.append(" WHERE");
        
        boolean isFirst = true;
        for(final Entry<Operator, Map<Column, String>> opColVal : where){
            
            for(final Entry<Column, String> colVal : opColVal.getValue().entrySet()){
                
                if(!isFirst){
                    cause.append(" AND");
                }
                isFirst = false;
            
                cause.append(" ").append(colVal.getKey().colName()).append(" ").append(opColVal.getKey().operator()).append(" ?");
            }
            
        }
        
        return cause.toString();
    }

    static void logValidateTableCols(final Select select) {
        logValidateTableCols(select.getTable(), select.getWhere());
        logValidateTableCols(select.getTable(), select.getValues());
    }

    static void logValidateTableCols(final Table table, final Where... wheres) {
        final List<Column> cols = new ArrayList<Column>();


        for (final Where where : wheres) {
            if (where != null) {
                for(final Entry<Operator, Map<Column, String>> opColVal : where){
                    for (final Entry<Column, String> colVal : opColVal.getValue().entrySet()) {
                        cols.add(colVal.getKey());
                    }
                }
            }
        }

        logValidateTableCols(table, cols);
    }

    static void logValidateTableCols(final Table table, final List<Column> cols) {
        final List<Column> badCols = new ArrayList<Column>(cols);
        badCols.removeAll(Arrays.asList(table.cols()));

        if (0 < badCols.size()) {
            final Exception e = new Exception();
            MiscStuff.writeToLog("using cols that aren't in table: " + table + ", cols: " + badCols, e);
        }
    }

    static int insert(final Table table, final Where where, final ConnectionWrapper con) throws SQLException, DataException {
        final String pss = getInsertPreparedStatement(table, where);
        final PreparedStatement ps = con.GetAPreparedStatement(pss, Statement.RETURN_GENERATED_KEYS);

        setPreparedStatementValues(ps, where);

        if(0 == ps.executeUpdate()){
            throw new DataException("wasn't successful: " + pss);
        }

        final ResultSet rs = ps.getGeneratedKeys();
        rs.next();

        return rs.getInt(1);


    }

    static String getInsertPreparedStatement(final Table table, final Where where) {
        //"INSERT INTO taxes(companyID, taxTypeID, claim, underAmount, tax) VALUES(?, ?, ?, ?, ?)"
        final StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO ").append(table.tableName());


        final StringBuilder cols = new StringBuilder();
        cols.append("(");
        final StringBuilder vals = new StringBuilder();
        vals.append("(");

        boolean isFirst = true;
        for(final Entry<Operator, Map<Column, String>> opColVal : where){
            for (final Entry<Column, String> colVal : opColVal.getValue().entrySet()) {
                if (!isFirst) {
                    cols.append(", ");
                    vals.append(", ");
                }
                isFirst = false;

                cols.append(colVal.getKey().colName());
                vals.append("?");
            }
        }
        cols.append(")");
        vals.append(")");

        statement.append(cols).append(" VALUES").append(vals);

        return statement.toString();
    }

    private static void setPreparedStatementValues(PreparedStatement ps, Where... wheres) throws SQLException, DataException {
        

        int index = 0;
        for(final Where where : wheres){
            if(where == null){
                continue;
            }
            for(final Entry<Operator, Map<Column, String>> opColVal : where){
                for (final Entry<Column, String> colVal : opColVal.getValue().entrySet()) {
                    ++index;

                    if (colVal.getKey().type().javaType().equals(Currency.class)
                        || colVal.getKey().type().javaType().equals(String.class)) {
                        
                        ps.setString(index, colVal.getValue());

                    } else if (colVal.getKey().type().javaType().equals(Double.class)) {
                        ps.setDouble(index, Double.parseDouble(colVal.getValue()));

                    } else if (colVal.getKey().type().javaType().equals(Integer.class)) {
                        ps.setInt(index, Integer.parseInt(colVal.getValue()));
                        
                    }else if (colVal.getKey().type().javaType().equals(Long.class)) {
                        ps.setLong(index, Long.parseLong(colVal.getValue()));
                        
                    }else{
                        throw new DataException("need a mapping for type: " + colVal.getKey().type());
                    }
                }
            }
        }
    }

    static int update(final Table table, final Where set, final Where where, ConnectionWrapper con) throws SQLException, DataException {

        final String pss = getUpdatePreparedStatement(table, set, where);
        final PreparedStatement ps = con.GetAPreparedStatement(pss);

        setPreparedStatementValues(ps, set, where);

        try{
            return ps.executeUpdate();
        }catch(SQLException ex){
            throw new DataException("prepared statement is: " + pss + ", set is: " + set + ", where is: " + where, ex);
        }

    }

    static String getUpdatePreparedStatement(Table table, Where set, Where where) {

        //"UPDATE companies SET Address = ?, AdjustedB2S = ?, FutureBillsID = ? WHERE ID = ?"
        final StringBuilder statement = new StringBuilder();
        statement.append("UPDATE ").append(table.tableName()).append(" SET ");

        appendValueEqQuestionMark(statement, set);

        statement.append(getWhereClause(where));

        return statement.toString();
    }

    private static void appendValueEqQuestionMark(StringBuilder statement, Where set) {
        boolean isFirst = true;
        for(final Entry<Operator, Map<Column, String>> opColVal : set){
            for (final Entry<Column, String> colVal : opColVal.getValue().entrySet()) {
                if (!isFirst) {
                    statement.append(", ");
                }
                isFirst = false;

                statement.append(colVal.getKey().colName()).append(" ").append(opColVal.getKey().operator()).append(" ?");
            }
        }
    }

    static void delete(Table table, Where where, ConnectionWrapper con) throws SQLException, DataException {
        final String pss = getDeletePreparedStatement(table, where);
        final PreparedStatement ps = con.GetAPreparedStatement(pss);

        setPreparedStatementValues(ps, where);

        //not sure if it should be execute()
//        if(!ps.execute()){
//            throw new DataException("Delete wasn't successful. Where: " + where + ", PreparedStatement:" + pss);
//        }
        ps.execute();
    }

    static String getDeletePreparedStatement(Table table, Where where) {
        
        final StringBuilder statement = new StringBuilder();
        statement.append("DELETE from ").append(table.tableName());

        statement.append(getWhereClause(where));

        return statement.toString();
    }

    static Results select(Select select, ConnectionWrapper con) throws DataException {
        
        final String pss = getSelectPreparedStatement(select);
        
        try{
            final PreparedStatement ps = con.GetAPreparedStatement(pss);

            setPreparedStatementValues(ps, select.getWhere());

            final ResultSet rs = ps.executeQuery();
            return fill(select, rs);
        }catch(SQLException e){
            throw new DataException("PreparedStatement is: " + pss, e);
        }
    }
    
    static int selectIdOrNegOne(Results results) throws DataException {
        if(results.isEmpty()){
            return -1;
        }else{
            return results.get(0).getInteger(Column.ID);
        }
    }

    static String getSelectPreparedStatement(Select select) {
        final StringBuilder statement = new StringBuilder();
        statement.append("SELECT ");
        
        boolean isFirst = true;
        for (final Column col : select.getValues()) {
            if (!isFirst) {
                statement.append(", ");
            }
            isFirst = false;
            statement.append(col.colName());
        }
        
        statement.append(" from ").append(select.getTable().tableName());

        statement.append(getWhereClause(select.getWhere()));
        
        statement.append(getOrderByClause(select.getOrderBy()));


        return statement.toString();
    }

    private static List<File> getNamesBefore(Calendar timeToCompare, List<File> sortedBackups) throws ParseException {
        
            final Date timeToCompareD = timeToCompare.getTime();
            
            final List<File> earlierNames = new LinkedList<File>();
            for(final File backup : sortedBackups){
                if(DB_NAME_FORMAT.parse(backup.getName()).after(timeToCompareD)){
                    earlierNames.add(backup);
                }
            }
            
            return earlierNames;
    }
    
    
    
    
    /*private static String getSelectStatement(final Select select) {
        final StringBuilder statement = new StringBuilder();
        statement.append("select ");

        boolean isFirst = true;
        for (final Column col : select.getValues()) {
            if (!isFirst) {
                statement.append(", ");
            }
            isFirst = false;
            statement.append(col.colName());
        }

        statement.append(" from ").append(select.getTable().tableName());

        final Where where = select.getWhere();
        if (where != null) {
            statement.append(" ").append(where.getCause());
        }
        //";" breaks javadb, but I don't think it'll hurt mysql
        //statement.append(";");
        //"select * from companies where id = "+companyId+";"
        return statement.toString();
    }
     */


    /*
    private static String getDeleteStatement(final Table table, final Where where) {

        final StringBuilder statement = new StringBuilder();
        statement.append("delete from ").append(table.tableName());
        if (where != null) {
            statement.append(" ").append(where.getCause());
        }


        //GetANewStatement().execute("delete from companies where name = '"+companyName+"' 
        //and location = '"+companyLocation+"' and year ="+year+";");

        return statement.toString();
    }
     */

    /*
    public static String getInsertStatement(final Table table, final Where where) {

        final StringBuilder statement = new StringBuilder();
        //"insert into companies(name, location, year) values('" + companyName +"','"+companyLocation+"', "+year+");");
        statement.append("insert into ").append(table.tableName());


        final StringBuilder cols = new StringBuilder();
        cols.append("(");
        final StringBuilder vals = new StringBuilder();
        vals.append("(");

        boolean isFirst = true;
        for (final Entry<Column, String> colVal : where) {
            if (!isFirst) {
                cols.append(", ");
                vals.append(", ");
            }
            isFirst = false;

            cols.append(colVal.getKey().colName());
            vals.append(colVal.getValue());
        }
        cols.append(")");
        vals.append(")");

        statement.append(cols).append(" values").append(vals);

        return statement.toString();
    }
     */
}
