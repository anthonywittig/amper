/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.command;

import java.util.HashMap;
import AMP2.DatabaseStuff.tables.Column;
import java.util.Map;
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
public class WhereTest {
    
    public WhereTest() {
    }

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

    
    /**
    @Test
    public void testReplaceAllCols() {
       
        final Map<Column, Column> colToReplacement = new HashMap<Column, Column>();
        colToReplacement.put(Column.Address, Column.BallanceS);
        final Where where = new Where(Column.Address, "5").and(Column.AdjustedB2S, "2");
        
        final String originalClause = where.getCause();
        final String originalClauseExp = "where Address = '5' and AdjustedB2S = '2'";
        
        assertEquals(originalClause, originalClauseExp);
        
        where.replaceAllCols(colToReplacement);
        
        final String replacedClause = where.getCause();
        final String replacedClauseExp = "where AdjustedB2S = '2' and BallanceS = '5'";;
        
        
        assertEquals(replacedClause, replacedClauseExp);
    }
     */
    
    @Test
    public void testPlaceHolder(){
        //noop
    }
}
