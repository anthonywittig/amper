/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.db;

import AMP2.DatabaseStuff.DatabaseHelper;
import AMP2.DatabaseStuff.command.Select;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Table;
import AMP2.Util.MiscStuff;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author awittig
 */
public class MySql implements ConnectionWrapper {

    protected Connection conn;
    
    protected String userName = "";
    protected String userPass = "";
    protected String dataUrl = "";
    
    public MySql() {
        this("awittig", "111111", "jdbc:mysql://localhost/amp");
    }
    
    public MySql(String userName, String userPass, String dataURL) {
        setUserName(userName);
        setUserPass(userPass);
        setDataUrl(dataURL);
        
        Connect();
    }

    /**
     *This method closes our connection
     */
    public void CloseConnection() {

        if (conn != null) {
            try {
                if(!conn.isClosed()){
                    conn.close();
                }
            //System.out.println ("Database connection terminated");
            } catch (Exception e) { /* ignore close errors */ 
                //System.err.println("Error in Connecter.CloseConnection() for: " + e.toString());
                //e.printStackTrace(System.err);
            }
        }
    }

    /**
     *This method sets up our connection
     */
    private void Connect() {

        try{
            if(conn != null && !conn.isClosed()){
                conn.close();
            }
        }
        catch(Exception e){}
        
        conn = null;

            try {

                Class.forName("com.mysql.jdbc.Driver").newInstance();

                conn = DriverManager.getConnection(getDataUrl(), getUserName(), getUserPass());

            } catch (SQLException e) {
                System.err.print("Connecter.connect (using datasource: " + getDataUrl() + "): " + e.toString());
                //MiscStuff.writeToLog("Connecter.connect SQL " + e.toString());
                conn = null;
            } catch (Exception e) {
                System.err.print("Connecter.connect (using datasource = " + getDataUrl() + "): " + e.toString());
                //MiscStuff.writeToLog("Connecter.connect EX " + e.toString());
                conn = null;
            }
    }

    /**
     *returns our connection object
     */
    public Connection GetConnection() {

        if (conn == null) {
            Connect();
        }
        
        try{
            if(conn.isClosed()){
                Connect();
            }
        }
        catch(Exception e){}

        return conn;
    }

    /**
     *This method returns a new statment object from our connection object
     */
    public Statement GetANewStatement() throws SQLException {
        return GetConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
    }
    
    @Override
    public PreparedStatement GetAPreparedStatement(String preparedSql) throws DataException{
        try{
            return GetConnection().prepareStatement(preparedSql);
        }catch(SQLException ex){
            throw new DataException(ex);
        }
    }
    
    @Override
    public PreparedStatement GetAPreparedStatement(String preparedSql, int mode) throws DataException {
        try {
            return GetConnection().prepareStatement(preparedSql, mode);
        } catch (SQLException ex) {
            throw new DataException(ex);
        }
    }


    /**
     * Called before gc'ed
     * @throws java.lang.Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        CloseConnection();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }
    
    public void createTable(Table table) throws DataException{
        
        final Column primaryKey = table.cols()[0];
        
        final StringBuilder createCommand = new StringBuilder();
        createCommand.append("create table ").append(table.tableName()).append("(");
//        "create table employees(" +
        createCommand.append(primaryKey.colName()).append(" int unsigned not null auto_increment primary key,");
//                            "ID int unsigned not null auto_increment primary key," +
        for(int colIdx = 1; colIdx < table.cols().length; ++colIdx){
            final Column thisCol = table.cols()[colIdx];
            createCommand.append(thisCol.colName()).append(" ").append(thisCol.type().sqlType());
            
            if(colIdx + 1 < table.cols().length){
                createCommand.append(", ");
            }else{
                createCommand.append(")");
            }
//                            "Name VARCHAR(200), " +
//                            "SocS VARCHAR(20), " +
//                            "Address VARCHAR(500), " +
//                            "Claim INT, " +
//                            "CurrentRate INT, " +
//                            "IsCurrentEmp INT, " +
//                            "CompanyID INT)"
        }
        try {
            //execute the create:
            GetANewStatement().execute(createCommand.toString());
        } catch (SQLException ex) {
            throw new DataException(ex);
        }
        
        //execute after create command
        if(table.afterCreateCommand() != null){
            try {
                GetANewStatement().execute(table.afterCreateCommand());
            } catch (SQLException ex) {
                throw new DataException(ex);
            }
        }
    }
    
    public void delete(final Table table, final Where where) throws DataException{
        
        DbHelper.logValidateTableCols(table, where);
        
        try {
            //return GetANewStatement().execute(DbHelper.getDeleteStatement(table, where));
            DbHelper.delete(table, where, this);
        } catch (SQLException ex) {
            throw new DataException(ex);
        }
    }
    
    
    
    

    public Set<Table> getExistingTables() throws DataException{
        final Set<Table> tables = new HashSet<Table>();
        
        try {
            ResultSet rs = GetANewStatement().executeQuery("SHOW TABLES");

            while (rs.next()) {
                tables.add(Table.valueOf(rs.getString(1)));
            }
        } catch (Exception e) {
            throw new DataException(e);
        }
        
        return tables;
    }

    

    public int insert(final Table table, final Where where) throws DataException {
        
        DbHelper.logValidateTableCols(table, where);
        
        try {
            return DbHelper.insert(table, where, this);
        } catch (SQLException ex) {
            throw new DataException(ex);
        }
    }
    
    
    /**
     * This method loads our entire database from file
     *
     *  @param path, the path of the folder that holds the database files
     */
    @Override
    public File LoadDatabaseFromFile(String path) throws DataException {
        path += "\\";
        path = path.replace("\\", "\\\\");

        ArrayList<String> tables = new ArrayList<String>();
        try {
            ResultSet rs = GetANewStatement().executeQuery("SHOW TABLES");

            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new DataException(e);
        }

        //delete and remake tables
        for (String name : tables) {
            try {
                GetANewStatement().execute("drop table " +
                        name);

            //MiscStuff.writeToLog("DatabaseHelper.LoadDatabaseFromFile try table drop");

            } catch (SQLException e) {
                throw new DataException(e);
            }
        }

        DatabaseHelper.CreateUpdateTables();

        for (String name : tables) {
            try {
                String name2 = path + name;
                MiscStuff.writeToLog("LoadDatabaseFromFile loading file: " + name2);

                GetANewStatement().execute("LOAD DATA INFILE \'" + name2 + "\' INTO TABLE " + name);

            } catch (SQLException e) {
                throw new DataException(e);
            }
        }

        //this is probably really wrong...
        return new File(path.replaceAll("\\\\", File.separator));

    }

    
    @Override
    public File saveDbToFile() throws DataException {
        
        String path = System.getProperty("user.dir") + DatabaseHelper.SAVE_LOCATION;
//        File directory = new File(path);
//        directory.mkdirs();

        Calendar cal = Calendar.getInstance();
        
        path += DbHelper.DB_NAME_FORMAT.format(cal.getTime()) + "\\";

        File dirPath = new File(path);
        dirPath.mkdirs();

        path = path.replace("\\", "\\\\");
        //MiscStuff.writeToLog(path);
        
        
        
        
        
        for (final Table table : getExistingTables()) {
            try {
                final String name = table.name();
                String name2 = dirPath + name;
                //MiscStuff.writeToLog(name);
//                MiscStuff.writeToLog("BackupDatabase save: " + name2);
//                GUI.getCon().GetANewStatement().execute("SELECT * INTO OUTFILE " +
//                        "\'" + name2 +  "\' FROM " + name + ";");
                GetANewStatement().execute("SELECT * from " + name + " INTO OUTFILE " +
                        "\'" + name2 + "\'");
            //MiscStuff.writeToLog(path + name);

            } catch (SQLException e) {
                throw new DataException(e);
            }
        }
        
        DbHelper.deleteOldBackups(new File(path));
        
        //this is probably really wrong:
        return new File(path.replaceAll("\\\\", File.separator));
    }

    public Results select(final Select select) throws DataException{
        
        DbHelper.logValidateTableCols(select);
        
//        try{
//            final ResultSet rs = GetANewStatement().executeQuery(DbHelper.getSelectStatement(select));
//            return DbHelper.fill(select, rs);
//        }catch(SQLException e){
//            throw new DataException(e);
//        }
        
        
        return DbHelper.select(select, this);
       
    
    }

    

//    @Override
//    public int getLastInsertId() throws DataException {
//        ResultSet rs = null;
//        try {
//            rs = GetANewStatement().executeQuery("select LAST_INSERT_ID()");
//            rs.absolute(1);
//            return rs.getInt(1);
//        } catch (SQLException ex) {
//            throw new DataException(ex);
//        }finally{
//            if(rs != null){
//                try {
//                    rs.close();
//                } catch (SQLException ex) {
//                    throw new DataException(ex);
//                }
//            }
//        }
//    }

    @Override
    public int update(Table table, Where set, Where where) throws DataException {
        DbHelper.logValidateTableCols(table, set, where);
        
        try {
            return DbHelper.update(table, set, where, this);
        } catch (SQLException ex) {
            throw new DataException(ex);
        }
    }

    @Override
    public int selectIdOrNegOne(Select select)throws DataException {
        final Results results = select(select);
        return DbHelper.selectIdOrNegOne(results);
    }

    @Override
    public File getBackupDirectory() {
        return new File(System.getProperty("user.dir") + "\\dbBackUps\\");
    }
    
    
}