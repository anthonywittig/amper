/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.db;

import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.command.Select;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.tables.Table;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Set;

/**
 *
 * @author awittig
 */
public interface ConnectionWrapper {
    /**
     *This method returns a new statement object from our connection object
     */
    //public Statement GetANewStatement() throws SQLException;
    
    /**
     * This gets a new prepared statement
     */
    PreparedStatement GetAPreparedStatement(String preparedSql) throws DataException;
    
    PreparedStatement GetAPreparedStatement(String preparedSql, int mode) throws DataException;
    
    /**
     *returns our connection object
     */
    public Connection GetConnection() throws DataException;
    
    /**
     * creates the given table if it doesn't exist
     */
    public void createTable(Table table) throws DataException;

    public Set<Table> getExistingTables() throws DataException;

    public void delete(Table table, Where where) throws DataException;

    public int insert(Table table, Where where) throws DataException;
    
    public File LoadDatabaseFromFile(String path) throws DataException;

    public Results select(Select select) throws DataException;
    
    public int selectIdOrNegOne(Select select) throws DataException;

    public File saveDbToFile() throws DataException;

    //public int getLastInsertId() throws DataException;

    public int update(Table table, Where set, Where where) throws DataException;

    //returns the directory that holds all the backups
    public File getBackupDirectory();

}
