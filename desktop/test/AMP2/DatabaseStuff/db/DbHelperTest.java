/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.db;

import java.util.ArrayList;
import java.util.List;
import AMP2.DatabaseStuff.command.SelectBuilder;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Table;
import AMP2.Util.MiscStuff;
import java.io.File;
import java.util.Calendar;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author awittig
 */
public class DbHelperTest {
    
    

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    
    @Test 
    public void testDeleteOldBackupsOver100() throws Exception{
        
        final File testDir = new File("DbHelperTestDir");
        if(testDir.exists()){
            MiscStuff.deleteRecursive(testDir);
        }
        
        if(!testDir.mkdir()){
            throw new AssertionError("Can't make dir: " + testDir.getAbsolutePath());
        }
        
        final Calendar dbName = Calendar.getInstance();
        
        //create folders
        final int numberOfFolders = 600;
        for(int filesMade = 0; filesMade < numberOfFolders; ++filesMade){
            createFolder(dbName, Calendar.MINUTE, testDir);
        }
        
        
        //verify we have too many files:
        assertEquals(testDir.list().length, numberOfFolders);
        
        //run method
        DbHelper.deleteOldBackups(testDir);
        
        //verify we only have 100 directories
        assertEquals(100, testDir.list().length);
        
        //clean up
        MiscStuff.deleteRecursive(testDir);
    }
    
    
    @Test 
    public void testDeleteOldBackupsKeepMostRecent50() throws Exception{
        
        final File testDir = new File("DbHelperTestDir");
        if(testDir.exists()){
            MiscStuff.deleteRecursive(testDir);
        }
        
        if(!testDir.mkdir()){
            throw new AssertionError("Can't make dir: " + testDir.getAbsolutePath());
        }
        
        final Calendar dbName = Calendar.getInstance();
        final List<File> keepers = new ArrayList<File>(50);
        
        //create folders that are most recent (they won't be deleted)
        final int numberOfRecentFolders = 50;
        for(int filesMade = 0; filesMade < numberOfRecentFolders; ++filesMade){
            keepers.add(createFolder(dbName, Calendar.MINUTE, testDir));
        }
        
        //now we'll make some other folders:
        final int numberOfOtherFolders = 600;
        for(int filesMade = 0; filesMade < numberOfOtherFolders; ++filesMade){
            createFolder(dbName, Calendar.DATE, testDir);
        }
        
        //verify we have too many files:
        assertEquals(testDir.list().length, numberOfRecentFolders + numberOfOtherFolders);
        
        //run method
        DbHelper.deleteOldBackups(testDir);
        
        //verify we only have 100 directories
        assertEquals(100, testDir.list().length);
        
        //verify our keepers still exist:
        for(final File keeper : keepers){
            assertTrue("file doens't exist! " + keeper.getAbsolutePath(), keeper.exists());
        }
        
        //clean up
        MiscStuff.deleteRecursive(testDir);
    }

    @Test
    public void testGetDeletePreparedStatement(){
        final Table table = Table.companies;
        final Where where = new Where(Column.Address, "123").and(Column.AdjustedB2S, "abc").and(Column.FutureBillsID, 3);
        
        final String delete = DbHelper.getDeletePreparedStatement(table, where);
        final String expResult = "DELETE from companies WHERE Address = ? AND AdjustedB2S = ? AND FutureBillsID = ?";
        
        assertEquals(expResult, delete);   
    }
    
    
    
    
    @Test
    public void testGetSelectPreparedStatement(){
        final SelectBuilder sb = new SelectBuilder();
        sb.addValue(Column.Address).addValue(Column.AdjustedB2S)
                .table(Table.taxes)
                .where(new Where(Column.AdjustedBS, "5"));
        
        final String select = DbHelper.getSelectPreparedStatement(sb.build());
        final String expResult = "SELECT Address, AdjustedB2S from taxes WHERE AdjustedBS = ?";
        
        assertEquals(expResult, select);
    }
    
    
    
    @Test
    public void testGetInsertPreparedStatement(){
        
        final Table table = Table.companies;
        final Where where = new Where(Column.Address, "123").and(Column.AdjustedB2S, "abc").and(Column.FutureBillsID, 3);
        
        final String insert = DbHelper.getInsertPreparedStatement(table, where);
        final String expResult = "INSERT INTO companies(Address, AdjustedB2S, FutureBillsID) VALUES(?, ?, ?)";
        
        assertEquals(expResult, insert);
        
    }
    
    @Test
    public void testGetUpdatePreparedStatement(){
        
        final Table table = Table.companies;
        final Where set = new Where(Column.Address, "123").and(Column.AdjustedB2S, "abc").and(Column.FutureBillsID, 3);
        final Where where = new Where(Column.ID, 3);
        
        final String update = DbHelper.getUpdatePreparedStatement(table, set, where);
        final String expResult = "UPDATE companies SET Address = ?, AdjustedB2S = ?, FutureBillsID = ? WHERE ID = ?";
        
        assertEquals(expResult, update);
    }
    
    @Test
    public void testGetWhereClause(){
        final Where where = new Where(Column.Address, "123").and(Column.AdjustedB2S, "abc").and(Column.FutureBillsID, 3);
        
        final String whereClause = DbHelper.getWhereClause(where);
        final String expResult = " WHERE Address = ? AND AdjustedB2S = ? AND FutureBillsID = ?";
        
        assertEquals(expResult, whereClause);
    }
    
    /*
    @Test
    public void testGetInsertStatement() {
        
        final Table table = Table.companies;
        final Where where = new Where(Column.name, "Time Out").and(Column.location, "La Pine").and(Column.year, 2005);
        final String expResult = "insert into companies(name, location, year) values('Time Out', 'La Pine', 2005)";
        
        final String result = DbHelper.getInsertStatement(table, where);

        assertEquals(expResult, result);        
    }
     */
    
    
    /*
    @Test
    public void testGetDeleteStatement() {
        
        final Table table = Table.companies;
        final Where where = new Where(Column.name, "Time Out").and(Column.location, "La Pine").and(Column.year, 2005);
        final String expResult = "delete from companies where "
                + "name = 'Time Out' and "
                + "location = 'La Pine' and "
                + "year = 2005";
        
        final String result = DbHelper.getDeleteStatement(table, where);
        
        assertEquals(expResult, result);
        
    }
     */
    
    /*
    @Test
    public void testGetSelectStatement(){
        final SelectBuilder sb = new SelectBuilder();
        sb.addValue(Column.Address).addValue(Column.AdjustedB2S)
                .table(Table.taxes)
                .where(new Where(Column.AdjustedBS, "5"));
        
        final String select = DbHelper.getSelectStatement(sb.build());
        final String expResult = "select Address, AdjustedB2S from taxes where AdjustedBS = '5'";
        
        assertEquals(expResult, select);
        
    }
     */

    private File createFolder(Calendar time, int fieldToDecBy, File parentDir) {
        time.add(fieldToDecBy, -1);
        final File newDir = new File(parentDir + File.separator + DbHelper.DB_NAME_FORMAT.format(time.getTime()));
        if(!newDir.mkdir()){
            throw new AssertionError("Can't make dir: " + newDir.getAbsolutePath());
        }
        
        return newDir;
    }
    
}
