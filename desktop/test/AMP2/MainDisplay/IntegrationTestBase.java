/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.MainDisplay;

import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.db.JavaDb;
import AMP2.DatabaseStuff.tables.Table;
import org.junit.Before;

/**
 *
 * @author awittig
 */
public class IntegrationTestBase {
    
    @Before
    public void before() throws Exception{
        JavaDb._useTestDirectoryForIntegrationTest("GuiIntegrationTestDb");

        GUI.IntegrationTestHelper.createGuiAndInsertCompany("Time Out", "LaPine", "2005");
        assert !showingWarnOrFatalMessageBox();

        //tables should now be created
        assert tablesCreated();
    }
    
    
    private boolean showingWarnOrFatalMessageBox() {
        return MessageDialog._integrationTesting_lastInstance != null;
    }
    
    private boolean tablesCreated() throws DataException {
        return GUI.getCon().getExistingTables().size() == Table.values().length;
    }
    
}
