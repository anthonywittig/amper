/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.db;

import java.util.Arrays;
import AMP2.DatabaseStuff.command.SelectBuilder;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Table;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static junit.framework.Assert.*;


/**
 *
 * @author awittig
 */
public class JavaDbTest {
    
    
    

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
    public void testSaveAndRestore() throws SQLException, DataException, IOException{
        final File dbDir = JavaDb._useTestDirectoryForIntegrationTest("JavaDbTest");
        final ConnectionWrapper db = new JavaDb(); 
        //put some data in
        db.createTable(Table.taxes);
        db.insert(Table.taxes, new Where(Column.companyID, 54321));
        
        //save
        final File curDbFile = db.saveDbToFile();
        
        //put some more data in
        db.insert(Table.taxes, new Where(Column.companyID, 98765));
        
        //verify all data:
        int idOfFirstInsert = db.selectIdOrNegOne(new SelectBuilder().addValue(Column.ID).table(Table.taxes).where(new Where(Column.companyID, 54321)).build());
        int idOfSecondInsert = db.selectIdOrNegOne(new SelectBuilder().addValue(Column.ID).table(Table.taxes).where(new Where(Column.companyID, 98765)).build());
        assertEquals(1, idOfFirstInsert);
        assertEquals(2, idOfSecondInsert);
        
        //roll back
        //verify that we only have two db files (plus one derby.log)
        Assert.assertEquals("we should only have two files! " + Arrays.toString(dbDir.list()), 3, dbDir.list().length);
        Assert.assertTrue("There is no derby.log in " + Arrays.toString(dbDir.list()), Arrays.asList(dbDir.list()).contains("derby.log"));
   
        //find the db that we aren't using:
        boolean rolledBack = false;
        for(final File dbFile : dbDir.listFiles()){
            if(!dbFile.getName().equals(curDbFile.getName())){
                if(dbFile.getName().equals("derby.log")){
                    //do nothing
                }else if(!rolledBack){
                    db.LoadDatabaseFromFile(dbFile.getName());
                    rolledBack = true;
                }else{
                    throw new AssertionError("already rolled back the db!");
                }
            }
        }
        
        //verify some data is in
        idOfFirstInsert = db.selectIdOrNegOne(new SelectBuilder().addValue(Column.ID).table(Table.taxes).where(new Where(Column.companyID, 54321)).build());
        assertEquals(1, idOfFirstInsert);
        
        //verify some more data is not in
        idOfSecondInsert = db.selectIdOrNegOne(new SelectBuilder().addValue(Column.ID).table(Table.taxes).where(new Where(Column.companyID, 98765)).build());
        assertEquals(-1, idOfSecondInsert);
        
    }
}
