/*
 * SmartConnection.java
 *
 * Created on December 2, 2006, 11:36 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.db;

import AMP2.DatabaseStuff.command.Select;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.tables.Table;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Set;

public class SmartConnection implements ConnectionWrapper {

    private static ConnectionWrapper connection;
    
    {
        //for now we'll just hardcode javaDb
//        connection = new MySql();
//        if(null == connection.GetConnection()){
        connection = new JavaDb();
//        }
    }
    
//    public Statement GetANewStatement() throws SQLException {
//        return connection.GetANewStatement();
//    }

    public PreparedStatement GetAPreparedStatement(String preparedSql) throws DataException {
        return connection.GetAPreparedStatement(preparedSql);
    }

    public Connection GetConnection() throws DataException{
        return connection.GetConnection();
    }

    public void createTable(Table table) throws DataException {
        connection.createTable(table);
    }

    public Set<Table> getExistingTables() throws DataException {
        return  connection.getExistingTables();
    }

    public void delete(Table table, Where companies) throws DataException {
        connection.delete(table, companies);
    }

    public int insert(Table table, Where where) throws DataException {
        return connection.insert(table, where);
    }

    public Results select(Select select) throws DataException {
        return connection.select(select);
    }

    @Override
    public File saveDbToFile() throws DataException {
        return connection.saveDbToFile();
    }
    
//    @Override
//    public int getLastInsertId() throws DataException {
//        return connection.getLastInsertId();
//    }

    @Override
    public PreparedStatement GetAPreparedStatement(String preparedSql, int mode) throws DataException {
        return connection.GetAPreparedStatement(preparedSql, mode);
    }

    @Override
    public int update(Table table, Where set, Where where) throws DataException {
        return connection.update(table, set, where);
    }

    @Override
    public File LoadDatabaseFromFile(String path) throws DataException {
        return connection.LoadDatabaseFromFile(path);
    }

    @Override
    public int selectIdOrNegOne(Select select) throws DataException {
        return connection.selectIdOrNegOne(select);
    }

    @Override
    public File getBackupDirectory() {
        return connection.getBackupDirectory();
    }
}