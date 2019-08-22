/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.db;

import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.command.Select;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Table;
import AMP2.Util.MiscStuff;
import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.derby.iapi.services.io.FileUtil;

/**
 *
 * @author awittig
 */
public class JavaDb implements ConnectionWrapper {

    private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String protocol = "jdbc:derby:";
    private static final AtomicReference<Connection> connHolder = new AtomicReference<Connection>();
    private static final String DERBY_HOME = "derby.system.home"; 
    private static final String TMP_DIR_NAME = "temporary";
    private static final String DB_DIRECTORY = new File("db" + File.separator).getAbsoluteFile().getAbsolutePath();
    static{
        System.setProperty(DERBY_HOME, DB_DIRECTORY);
    }

    //a method for help w/ testing (at least for now)
    public static File _useTestDirectoryForIntegrationTest(String dbDirectory){
        
        final File testDir = new File("integrationTestDbDir" + File.separator + dbDirectory + File.separator);
        
        //try and delete if it exists so old test data doesn't build up
        if(testDir.exists()){
            try{
                MiscStuff.deleteRecursive(testDir);
            }catch(Exception e){
                //ignore
            }
        }
        
        System.setProperty(DERBY_HOME, 
                testDir.getAbsoluteFile().getAbsolutePath());
        
        return testDir;
    }
    
    @Override
    public Connection GetConnection() throws DataException{
        if (connHolder.get() == null) {
            connHolder.set(connect());
        }

        return connHolder.get();
    }
   
    private Connection connect() throws DataException{
        //find the newest db:
        final File db = getNewestChildDirectory(getDbDir());
        
        final String dbName;
        if(db == null){
            dbName = DbHelper.DB_NAME_FORMAT.format(Calendar.getInstance().getTime());
        } else if(TMP_DIR_NAME.equals(db.getName())){
            final String filePath = db.getAbsolutePath();
            throw new DataException(filePath + " exists! it should have been deleted when the last save finished.")
                    .setUserReadableMessage("The directory " + filePath + " exists. This folder is created when saving. "
                    + "It is possible that your last save didn't work. To be safe, you should move this directory into a safe place and "
                    + "run the program to see if you last saves made it. If they haven't, you can move the folder inside " + filePath + " to "
                    + "to the " + DB_DIRECTORY + ". You need to make sure that the file you moved has a time that is the newest (but not a "
                    + "time in the future). Call Anthony if you have any questions on what to do!");
        }else{
            try{
                final Date test = DbHelper.DB_NAME_FORMAT.parse(db.getName());
                if(test == null){
                    throw new Exception("parsed date is null");
                }
                dbName = db.getName();
            }catch(Exception e){
                throw new DataException("Problem loading db folder: " + db.getAbsolutePath(), e).setUserReadableMessage(
                        "There appears to be an unexpected folder at " + db.getAbsolutePath() + ". "
                        + "If you put this folder here, it may be incorrectly named. If you didn't put the folder there, this is "
                        + "very interesting, please call Anthony.");
            }
            
        }
        
        
        return connect("create", "true", dbName);
    }

    private Connection connect(String attribute, String value, String dbName) throws DataException {
        
        try {
            loadDriver();
            Properties props = new Properties();
            props.put("user", "andy");
            props.put("password", "andy");
            
            final String connectionString = protocol + dbName + ";" +
                    attribute + "=" + value;

            final Connection conn = DriverManager.getConnection(
                    connectionString
                    , props);

            //test();

            MiscStuff.writeToLog("Connected to and created database " + dbName + " from connection: " + connectionString + ", DERBY_HOME is " + System.getProperty(DERBY_HOME));
            
            return conn;
        } catch (SQLException e) {
            throw new DataException(e).setUserReadableMessage(
                    "You may have two versions of AMP running. If not, try restarting your computer to make sure one isn't running in the background.");
        }
    }

    @Override
    public PreparedStatement GetAPreparedStatement(String preparedSql) throws DataException {
        try {
            return GetConnection().prepareStatement(preparedSql);
        } catch (SQLException ex) {
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

 
    public Statement GetANewStatement() throws DataException {
        try {
            //        return GetConnection().createStatement();
                    return GetConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException ex) {
            throw new DataException(ex);
        }
    }

    
    private void loadDriver() {

        try {
            Class.forName(driver).newInstance();
            MiscStuff.writeToLog("Loaded the appropriate driver");
        } catch (ClassNotFoundException cnfe) {
            MiscStuff.writeToLog("\nUnable to load the JDBC driver " + driver, cnfe);
        } catch (InstantiationException ie) {
            MiscStuff.writeToLog("\nUnable to instantiate the JDBC driver " + driver, ie);
        } catch (IllegalAccessException iae) {
            MiscStuff.writeToLog("\nNot allowed to access the JDBC driver " + driver, iae);
        }
    }
    
    @Override
    public void createTable(Table table) throws DataException {

//        table = makeSafe(table);

        final Column primaryKey = table.cols()[0];

        final StringBuilder createCommand = new StringBuilder();
        createCommand.append("create table ").append(table.tableName()).append("(");
        createCommand.append(primaryKey.colName()).append(" INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY, ");

        for (int colIdx = 1; colIdx < table.cols().length; ++colIdx) {
            final Column thisCol = table.cols()[colIdx];
            createCommand.append(thisCol.colName()).append(" ").append(thisCol.type().sqlType());

            if (colIdx + 1 < table.cols().length) {
                createCommand.append(", ");
            } else {
                createCommand.append(")");
            }
        }
        try {
            //execute the create:
            GetANewStatement().execute(createCommand.toString());
        } catch (SQLException ex) {
            throw new DataException(ex);
        }

        //execute after create command
        if (table.afterCreateCommand() != null) {
            try {
                GetANewStatement().execute(table.afterCreateCommand());
            } catch (SQLException ex) {
                throw new DataException(ex);
            }
        }
    }


//    private String getSafeColName(final Column col) {
//        return unSafeCols.contains(col) ? "COL_" : "" + col.colName();
//    }
    

    public void delete(Table table, final Where where) throws DataException {

//        table = makeSafe(table, where);
        DbHelper.logValidateTableCols(table, where);

        try {
            //return GetANewStatement().execute(DbHelper.getDeleteStatement(table, where));
            DbHelper.delete(table, where, this);
        } catch (SQLException ex) {
            throw new DataException(ex);
        }
    }
    
    
    public Set<Table> getExistingTables() throws DataException {

        final List<String> systemTables = Arrays.asList(new String[]{
                    "SYSALIASES", "SYSCHECKS", "SYSCOLPERMS", "SYSCOLUMNS",
                    "SYSCONGLOMERATES", "SYSCONSTRAINTS", "SYSDEPENDS",
                    "SYSFILES", "SYSFOREIGNKEYS", "SYSKEYS", "SYSROLES",
                    "SYSROUTINEPERMS", "SYSSCHEMAS", "SYSSTATEMENTS",
                    "SYSSTATISTICS", "SYSTABLEPERMS", "SYSTABLES",
                    "SYSTRIGGERS", "SYSVIEWS", "SYSDUMMY1", "SYSPERMS",
                    "SYSSEQUENCES"});

        final Set<Table> tables = new HashSet<Table>();

        try {
            final DatabaseMetaData md = GetConnection().getMetaData();
            final ResultSet rs = md.getTables(null, null, "%", null);
            while (rs.next()) {
                final String tableName = rs.getString(3);

                if (systemTables.contains(tableName)) {
                    //skip these
                    continue;
                }
                
                final Table table = Table.valueOf(tableName.toLowerCase());

//                if (safeToUnsafeTables.containsKey(table)) {
//                    tables.add(safeToUnsafeTables.get(table));
//                } else {
                    tables.add(table);
//                }
            }
            //tables should already be safe from create
            return tables;
        } catch (SQLException ex) {
            throw new DataException(ex);
        }

    }

    public int insert(Table table, final Where where) throws DataException {

//        table = makeSafe(table, where);
        DbHelper.logValidateTableCols(table, where);

        try {
            //return GetANewStatement().execute(DbHelper.getInsertStatement(table, where));
            return DbHelper.insert(table, where, this);
        } catch (SQLException ex) {
            throw new DataException(ex);
        }
    }
    
    @Override
    public File LoadDatabaseFromFile(String dbName) throws DataException {
        
        final Connection oldConnection = connHolder.get();
        
        if(oldConnection != null){
            try {
//                MiscStuff.printTaxInfoForDebugging("using old connection");
//                printCompanyInfoForDebugging("using old connection");
                oldConnection.close();
            } catch (SQLException ex) {
                throw new DataException("problem closing old connection", ex);
            }
        }
        
        connHolder.set(connect("create", "false", dbName));
        
        //MiscStuff.printTaxInfoForDebugging("using new connection");
        //printCompanyInfoForDebugging("using new connection");
        
        //this could be a bad assumption in the future:
        return new File(getDbDir().getAbsolutePath() + File.separator + dbName);
    }

    
    
    public Results select(final Select select) throws DataException {

        DbHelper.logValidateTableCols(select);
        
        return DbHelper.select(select, this);
        
    }
    
    @Override
    public int selectIdOrNegOne(Select select)throws DataException {
        final Results results = select(select);
        return DbHelper.selectIdOrNegOne(results);
    }

    @Override
    public File saveDbToFile() throws DataException {
        
        final File tempDirectory = new File(getDbDir().getAbsolutePath() + File.separator + TMP_DIR_NAME);
        //clear out tmp dir if we can:
        try{
            MiscStuff.deleteRecursive(tempDirectory);
        }catch(Exception e){
            //MiscStuff.writeToLog(e);
        }
        
        File copyToDirectory;
        String dbName;
        do{
            final Calendar cal = Calendar.getInstance();
            dbName = DbHelper.DB_NAME_FORMAT.format(cal.getTime());
            
            
            //loop if the folder already exists, we sometimes have this problem
            //w/ integration tests
            copyToDirectory = new File(getDbDir().getAbsolutePath() + File.separator + dbName);
        }while(copyToDirectory.exists());
        
        

//        MiscStuff.writeToLog("attempting to save db to: " + directory.getAbsolutePath());
        
        CallableStatement cs; 
        try {
            cs = GetConnection().prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)");
        
            cs.setString(1, tempDirectory.getAbsolutePath());
            cs.execute(); 
            cs.close();
            
//            MiscStuff.writeToLog("backed up database to " + directory.getAbsolutePath());
            
        } catch (SQLException ex) {
            throw new DataException(ex);
        }
        
        //copy from tmp up one level:
        final File copyFromDirectory = getNewestChildDirectory(tempDirectory);
        MiscStuff.writeToLog("attempting to copy from: " + copyFromDirectory.getAbsolutePath() + " to " + copyToDirectory.getAbsolutePath());
        FileUtil.copyDirectory(copyFromDirectory, copyToDirectory);
        
        //try a delete:
        try{
            MiscStuff.deleteRecursive(tempDirectory);
        }catch(Exception e){
            //MiscStuff.writeToLog(e);
        }
        
        //now start using the new db:
        final File loadedFile = LoadDatabaseFromFile(dbName);

        DbHelper.deleteOldBackups(getDbDir());
        
        return loadedFile;
        
    }


    @Override
    public int update(Table table, Where set, Where where) throws DataException {
//        table = makeSafe(table, set, where);
        DbHelper.logValidateTableCols(table, set, where);

        try {
            return DbHelper.update(table, set, where, this);
        } catch (SQLException ex) {
            throw new DataException(ex);
        }
    }
    
    private static File getDbDir(){
        return new File(System.getProperty(DERBY_HOME));
    }

    private static File getNewestChildDirectory(final File directory){
        
        final File[] dbs = directory.listFiles();
        if(dbs != null){
            Arrays.sort(dbs);
        }
        
        if(dbs != null && dbs.length != 0){
            for(int fileIdx = dbs.length - 1; fileIdx >= 0; --fileIdx){
                final File file = dbs[fileIdx];
                if(file.isDirectory()){
                    return file;
                }else{
                    //continue 
                }
            }
            //this is not expected, but really no worse than the else
            return null;
        }else{
            return null;
        }
    }

    @Override
    public File getBackupDirectory() {
        return getDbDir();
    }

   

    
}
//
//PreparedStatement pstmt = connection.prepareStatement(
//     "insert into some_table (col1, col2, ..) values (....)", 
//      new String[] { "ID_COLUMN"} ); 
//
//pstmt.executeUpdate();
//
//ResultSet rs = pstmt.getGeneratedKeys(); // will return the ID in ID_COLUMN
