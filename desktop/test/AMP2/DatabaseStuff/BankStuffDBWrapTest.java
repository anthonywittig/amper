/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff;

import java.util.Collection;
import AMP2.BankStuff.GlCode.Code;
import java.util.Map.Entry;
import java.util.Map;
import AMP2.BankStuff.GlCode;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.command.SelectBuilder;
import AMP2.BankStuff.Currency;
import AMP2.MainDisplay.GUI;
import java.util.Calendar;
import java.util.Random;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import AMP2.BankStuff.BHSnapShot;
import AMP2.BankStuff.BankHealth;
import AMP2.BankStuff.Bill;
import AMP2.BankStuff.Check;
import AMP2.BankStuff.CheckBook;
import AMP2.BankStuff.Transaction;
import AMP2.BankStuff.check.Checks;
import AMP2.BankStuff.check.ChecksImpl;
import AMP2.DatabaseStuff.db.JavaDb;
import AMP2.DatabaseStuff.results.Result;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Table;
import java.util.ArrayList;
import java.util.HashMap;
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
public class BankStuffDBWrapTest {
    
    //private static final AtomicInteger bankHealthId = new AtomicInteger();
    //private static final AtomicInteger checkNum = new AtomicInteger();
    private static final AtomicInteger someId = new AtomicInteger(555);
    
    private static final Random rand = new Random();

    

    

    

    
    
    public BankStuffDBWrapTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        JavaDb._useTestDirectoryForIntegrationTest("BankStuffDBWrapTestDb");
        GUI.IntegrationTestHelper.createGuiAndInsertCompany("Time Out", "LaPine", "2012");
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
     * Test of DeleteCheckBooks method, of class BankStuffDBWrap.
     */
    @Test
    public void testDeleteCheckBooks() throws Exception {
        
        //make sure we have a bank health:
        final int bankHealthID = someId.incrementAndGet();
        //make sure we have some check books:
        final int numOfCheckBooks = 4;
        
        final BankHealth bh = new BankHealth();
        
        for(int i = 0; i < numOfCheckBooks; ++i){
            final CheckBook cB = getANewCheckBook();
            bh.setCurrentCheckBook(cB);
            BankStuffDBWrap.InsertCheckBook(cB, bankHealthID);
        }
        
        //make sure our bank health has some check books
        assertEquals(BankStuffDBWrap.SelectCheckBooks(bankHealthID).size(), numOfCheckBooks);
        
        //delete books
        BankStuffDBWrap.DeleteCheckBooks(bankHealthID, bh);
        
        //make sure we don't have any check books
        assertTrue(BankStuffDBWrap.SelectCheckBooks(bankHealthID).isEmpty());
        
        //make sure the gl codes ids have been set to -1
        for(final CheckBook cb : bh.getCheckBooks()){
            for(final Check check : cb.getChecks()){
                assertEquals(check.getGlCode().getId(), -1);
            }
            for(final Transaction trans : cb.getTransactions()){
                assertEquals(trans.getGLCode().getId(), -1);
            }
        }
    }

    /**
     * Test of InsertCheckBook method, of class BankStuffDBWrap.
     */
    @Test
    public void testInsertCheckBook() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        
        final CheckBook cB = getANewCheckBook();
        final int cbId = BankStuffDBWrap.InsertCheckBook(cB, bankHealthID);
        
        final CheckBook cB2 = BankStuffDBWrap.SelectCheckBook(cbId, bankHealthID);
        
        assertCheckBooksEqual(cB, cB2);
        
    }

    /**
     * Test of InsertCheckBooks method, of class BankStuffDBWrap.
     */
    @Test
    public void testInsertCheckBooks() throws Exception {
        
        //make sure we have a bank health:
        final int bankHealthID = someId.incrementAndGet();
        
        final int preExistingBooks = BankStuffDBWrap.SelectCheckBooks(bankHealthID).size();
        
        //make sure we have some check books:
        final int numOfCheckBooks = 4;
        final List<CheckBook> cBs = new ArrayList<CheckBook>();
        for(int i = 0; i < numOfCheckBooks; ++i){
            final CheckBook cB = getANewCheckBook();
            cBs.add(cB);
        }
            
        BankStuffDBWrap.InsertCheckBooks(cBs, bankHealthID);
        
        //make sure our bank health has some check books
        assertTrue(BankStuffDBWrap.SelectCheckBooks(bankHealthID).size() == numOfCheckBooks + preExistingBooks);
    }

    /**
     * Test of SelectCheckBook method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectCheckBook() throws Exception {
        final int bankHealthID = someId.incrementAndGet();
        
        final CheckBook cB = getANewCheckBook();
        final int cbId = BankStuffDBWrap.InsertCheckBook(cB, bankHealthID);
        
        final CheckBook cB2 = BankStuffDBWrap.SelectCheckBook(cbId, bankHealthID);
        
        assertCheckBooksEqual(cB, cB2);
    }

    /**
     * Test of SelectCheckBookID method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectCheckBookID() throws Exception {
        
        
        final int bankHealthID = someId.incrementAndGet();
        
        final CheckBook cB = getANewCheckBook();
        final int cbId = BankStuffDBWrap.InsertCheckBook(cB, bankHealthID);
        
        final int cbId0 = BankStuffDBWrap.SelectCheckBookID(cB, bankHealthID);
        
        assertTrue(cbId == cbId0);
        
        
    }

    /**
     * Test of SelectCheckBookRS method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectCheckBookRS() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        
        final CheckBook cB = getANewCheckBook();
        final int cbId = BankStuffDBWrap.InsertCheckBook(cB, bankHealthID);
        
        Result result = BankStuffDBWrap.SelectCheckBookRS(cbId);
        
        assertEquals(cB.getAdjustedBalance(), result.getCurrency(Column.adjustedBalance));
        assertEquals(cB.getBalance(), result.getCurrency(Column.balance));
        assertEquals(cB.getWaitingToGoThroughC(), result.getCurrency(Column.waitingToGoThroughC));
        assertEquals(cB.getWaitingToGoThroughT(), result.getCurrency(Column.waitingToGoThroughT));
        
        
    }

    /**
     * Test of SelectCheckBooks method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectCheckBooks() throws Exception {
        
        //make sure we have a bank health:
        final int bankHealthID = someId.incrementAndGet();
        
        assertTrue(BankStuffDBWrap.SelectCheckBooks(bankHealthID).isEmpty());
        
        //make sure we have some check books:
        final int numOfCheckBooks = 4;
        final List<CheckBook> cBs = new ArrayList<CheckBook>();
        for(int i = 0; i < numOfCheckBooks; ++i){
            final CheckBook cB = getANewCheckBook();
            cBs.add(cB);
        }
            
        BankStuffDBWrap.InsertCheckBooks(cBs, bankHealthID);
        
        List<CheckBook> result = BankStuffDBWrap.SelectCheckBooks(bankHealthID);
        
        assertTrue(result.size() == cBs.size());
        
        //not sure if the order will always be the same...
        for(final CheckBook cb1 : cBs){
            boolean found = false;
            for(final CheckBook cb2 : result){
                if(cb1.getAdjustedBalance().equals(cb2.getAdjustedBalance())
                    && cb1.getBalance().equals(cb2.getBalance())){
                    //assume they are a match
                    assertCheckBooksEqual(cb1, cb2);
                    found = true;
                    break;
                }
            }
            if(!found){
                throw new AssertionError("didn't find a matching checkbook for: " + cb1 + " in " + result);
            }
        }
        
    }

    /**
     * Test of DeleteTransactions method, of class BankStuffDBWrap.
     */
    @Test
    public void testDeleteTransactions() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        
        final CheckBook cB = new CheckBook();
        final int checkBookID = BankStuffDBWrap.InsertCheckBook(cB, bankHealthID);
        
        assertTrue(BankStuffDBWrap.SelectTransactions(checkBookID).isEmpty());
        
        final Transaction trans = getATransactionAndInsertGL();
        
        BankStuffDBWrap.InsertTransaction(trans, checkBookID);
        
        assertTrue(BankStuffDBWrap.SelectTransactions(checkBookID).size() == 1);
        
        BankStuffDBWrap.DeleteTransactions(checkBookID);
        
        assertTrue(BankStuffDBWrap.SelectTransactions(checkBookID).isEmpty());
        
    }

    /**
     * Test of InsertTransaction method, of class BankStuffDBWrap.
     */
    @Test
    public void testInsertTransaction() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        
        final CheckBook cB = new CheckBook();
        final int checkBookID = BankStuffDBWrap.InsertCheckBook(cB, bankHealthID);
        
        assertTrue(BankStuffDBWrap.SelectTransactions(checkBookID).isEmpty());
        
        final Transaction trans = getATransactionAndInsertGL();
        
        BankStuffDBWrap.InsertTransaction(trans, checkBookID);
        
        final List<Transaction> transes = BankStuffDBWrap.SelectTransactions(checkBookID);
        
        assertTrue(transes.size() == 1);
        
        assertTransactionsEqual(trans, transes.get(0));
        
    }

    /**
     * Test of InsertTransactions method, of class BankStuffDBWrap.
     */
    @Test
    public void testInsertTransactions() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        
        final CheckBook cB = new CheckBook();
        final int checkBookID = BankStuffDBWrap.InsertCheckBook(cB, bankHealthID);
        
        assertTrue(BankStuffDBWrap.SelectTransactions(checkBookID).isEmpty());
        
        final List<Transaction> transes = new ArrayList<Transaction>();
        final int numTranses = 4;
        for(int i = 0; i < numTranses; ++i){
            transes.add(getATransactionAndInsertGL());
        }
        
        BankStuffDBWrap.InsertTransactions(transes, checkBookID);
        
        
        final List<Transaction> transes2 = BankStuffDBWrap.SelectTransactions(checkBookID);
        
        assertTrue(transes.size() == numTranses);
        
        assertTransactionsEqual(transes, transes2);
        
        
    }

    /**
     * Test of SelectTransactionID method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectTransactionID() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        
        final CheckBook cB = new CheckBook();
        final int checkBookID = BankStuffDBWrap.InsertCheckBook(cB, bankHealthID);
        
        assertTrue(BankStuffDBWrap.SelectTransactions(checkBookID).isEmpty());
        
        final Transaction trans = getATransactionAndInsertGL();
        
        final int transId = BankStuffDBWrap.InsertTransaction(trans, checkBookID);
        
        final int transId2 = BankStuffDBWrap.SelectTransactionID(trans, checkBookID);
        
        assertTrue(transId == transId2);
    }

    /**
     * Test of SelectTransactions method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectTransactions() throws Exception {
        final int bankHealthID = someId.incrementAndGet();
        
        final CheckBook cB = new CheckBook();
        final int checkBookID = BankStuffDBWrap.InsertCheckBook(cB, bankHealthID);
        
        assertTrue(BankStuffDBWrap.SelectTransactions(checkBookID).isEmpty());
        
        final List<Transaction> transes = new ArrayList<Transaction>();
        final int numTranses = 4;
        for(int i = 0; i < numTranses; ++i){
            transes.add(getATransactionAndInsertGL());
        }
        
        BankStuffDBWrap.InsertTransactions(transes, checkBookID);
        
        
        final List<Transaction> transes2 = BankStuffDBWrap.SelectTransactions(checkBookID);
        
        assertTrue(transes.size() == numTranses);
        
        assertTransactionsEqual(transes, transes2);
    }

    /**
     * Test of DeleteChecks method, of class BankStuffDBWrap.
     */
    @Test
    public void testDeleteChecks() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        
        final CheckBook cB = new CheckBook();
        final int checkBookID = BankStuffDBWrap.InsertCheckBook(cB, bankHealthID);
        
        assertTrue(BankStuffDBWrap. SelectChecks(checkBookID).isEmpty());
        
        final int numOfChecks = 40;
        final List<Check> checks = new ArrayList<Check>(numOfChecks);
        for(int i = 0; i < numOfChecks; ++i){
            final Check check = getANewCheckAndInsertGL();
            checks.add(check);
            assertTrue(BankStuffDBWrap.SelectCheckID(check, checkBookID) == -1);
        }
        
        assertTrue(BankStuffDBWrap.SelectChecks(checkBookID).isEmpty());
        
        //insert the checks:
        for(final Check check : checks){
            final int checkId = BankStuffDBWrap.InsertCheck(check, checkBookID);
            assertTrue(BankStuffDBWrap.SelectCheckID(check, checkBookID) == checkId);
        }
        
        //make sure we have the checks:
        assertTrue(BankStuffDBWrap.SelectChecks(checkBookID).size() == numOfChecks);
        
        //delete the checks
        BankStuffDBWrap.DeleteChecks(checkBookID);
        
        //make sure they were deleted:
        for(final Check check : checks){
            assertTrue(BankStuffDBWrap.SelectCheckID(check, checkBookID) == -1);
        }
        assertTrue(BankStuffDBWrap.SelectChecks(checkBookID).isEmpty());
        
    }

    /**
     * Test of InsertCheck method, of class BankStuffDBWrap.
     */
    @Test
    public void testInsertCheck() throws Exception {

        final Check check = getANewCheckAndInsertGL();
        final BankHealth bankHealth = new BankHealth();
        final int bankHealthID = BankStuffDBWrap.InsertBankHealth(bankHealth, someId.incrementAndGet());
        final CheckBook cb = new CheckBook();
        final int checkBookID = BankStuffDBWrap.InsertCheckBook(cb, bankHealthID);
        
        BankStuffDBWrap.InsertCheck(check, checkBookID);

        final int checkId = BankStuffDBWrap.SelectCheckID(check, checkBookID);
        
        assertTrue(-1 < checkId);
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column._star).table(Table.checks)
                .where(new Where(Column.ID, checkId));
        final Results results = GUI.getCon().select(sb.build());
        
        assertFalse(results.isEmpty());
        
        final Result result = results.get(0);
        
        assertTrue(check.getClearDate().equals(result.getString(Column.clearDate)));
        assertTrue(check.getDateS().equals(result.getString(Column.dateS)));
        assertTrue(check.getForS().equals(result.getString(Column.forS)));
        final boolean goneThrough = result.getInteger(Column.goneThrough) == 1;
        assertTrue(check.getGoneThrough() == goneThrough);
        assertTrue(check.getPayTo().equals(result.getString(Column.payTo)));
        assertTrue(check.getAmount().equals(result.getCurrency(Column.amount)));
        assertTrue(check.getCheckNum() == result.getInteger(Column.checkNum));
        assertTrue(check.getGlCode().getId() == result.getInteger(Column.gLCodeID));
        
        
    }

    /**
     * Test of InsertChecks method, of class BankStuffDBWrap.
     */
    @Test
    public void testInsertChecks() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        
        final CheckBook cB = new CheckBook();
        final int checkBookID = BankStuffDBWrap.InsertCheckBook(cB, bankHealthID);
        
        assertTrue(BankStuffDBWrap.SelectChecks(checkBookID).isEmpty());
        
        final int numOfChecks = 40;
        final Checks checks = new ChecksImpl();
        for(int i = 0; i < numOfChecks; ++i){
            final Check check = getANewCheckAndInsertGL();
            checks.add(check);
            assertTrue(BankStuffDBWrap.SelectCheckID(check, checkBookID) == -1);
        }
        
        assertTrue(BankStuffDBWrap.SelectChecks(checkBookID).isEmpty());
        
        //insert the checks:
        BankStuffDBWrap.InsertChecks(checks, checkBookID);
        
        //make sure we have the checks:
        assertTrue(BankStuffDBWrap.SelectChecks(checkBookID).size() == numOfChecks);
        
        for(final Check check : checks){
            assertTrue(BankStuffDBWrap.SelectCheckID(check, checkBookID) != -1);
        }
        
    }

    /**
     * Test of SelectCheckID method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectCheckID() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        
        final CheckBook cB = new CheckBook();
        final int checkBookID = BankStuffDBWrap.InsertCheckBook(cB, bankHealthID);
        
        assertTrue(BankStuffDBWrap.SelectChecks(checkBookID).isEmpty());
        
        
        final Check check = getANewCheckAndInsertGL();
        assertTrue(BankStuffDBWrap.SelectCheckID(check, checkBookID) == -1);
        
        
        assertTrue(BankStuffDBWrap.SelectChecks(checkBookID).isEmpty());
        
        //insert the check:
        final int checkId = BankStuffDBWrap.InsertCheck(check, checkBookID);
        
        //make sure we have the checks:
        assertTrue(BankStuffDBWrap.SelectChecks(checkBookID).size() == 1);
        
        
        assertTrue(BankStuffDBWrap.SelectCheckID(check, checkBookID) == checkId);
        
        
    }

    /**
     * Test of SelectChecks method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectChecks() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        
        final CheckBook cB = new CheckBook();
        final int checkBookID = BankStuffDBWrap.InsertCheckBook(cB, bankHealthID);
        
        assertTrue(BankStuffDBWrap.SelectChecks(checkBookID).isEmpty());
        
        final int numOfChecks = 40;
        final Checks checks = new ChecksImpl();
        for(int i = 0; i < numOfChecks; ++i){
            final Check check = getANewCheckAndInsertGL();
            checks.add(check);
            assertTrue(BankStuffDBWrap.SelectCheckID(check, checkBookID) == -1);
        }
        
        assertTrue(BankStuffDBWrap.SelectChecks(checkBookID).isEmpty());
        
        //insert the checks:
        BankStuffDBWrap.InsertChecks(checks, checkBookID);
        
        //select the checks:
        final Checks checksSelected = BankStuffDBWrap.SelectChecks(checkBookID);
        assertTrue(checksSelected.size() == numOfChecks);
        
        for(final Check origCheck : checks){
            boolean found = false;
            for(final Check selCheck : checksSelected){
                if(origCheck.getCheckNum() == selCheck.getCheckNum()){
                    assertChecksEqual(origCheck, selCheck);
                    found = true;
                }
            }
            if(!found){
                throw new AssertionError("couldn't find " + origCheck + " in " + checksSelected);
            }
        }
        
        
    }

    /**
     * Test of DeleteGLCode method, of class BankStuffDBWrap.
     */
    @Test
    public void testDeleteGLCode() throws Exception {
        
        
        final int bnakHealthId = someId.incrementAndGet();
        final GlCode glCode = getAndInsertGlCode(bnakHealthId);
        
        //insert a glcode
        final int glId = BankStuffDBWrap.InsertGLCode(glCode, bnakHealthId);
        
        //verify insert
        final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.glcodes)
                .where(new Where(Column.code, glCode.getCode().getCode()).and(Column.description, glCode.getDescription()).and(Column.BankHealthID, bnakHealthId));
        final Results results = GUI.getCon().select(sb.build());
        
        assertFalse(results.isEmpty());
        assertTrue(results.get(0).getInteger(Column.ID) == glId);
        
        //delete gl
        BankStuffDBWrap.DeleteGLCode(glCode, bnakHealthId);
        
        //verify delete
        final Results deleteResults = GUI.getCon().select(sb.build());
        
        assertTrue(deleteResults.isEmpty());
    }

    /**
     * Test of DeleteGLCodes method, of class BankStuffDBWrap.
     */
    @Test
    public void testDeleteGLCodes() throws Exception {
        
        final int numOfGlCodes = 40;
        final int bnakHealthId = someId.incrementAndGet();
        
        assertTrue(BankStuffDBWrap.SelectGLCodes(bnakHealthId).keySet().size() == 0);
        
        for(int i = 0; i < numOfGlCodes; ++i){
            final GlCode glCode = getAndInsertGlCode(bnakHealthId);
            
            //insert a glcode
            final int glId = BankStuffDBWrap.InsertGLCode(glCode, bnakHealthId);


            //verify insert
            final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.glcodes)
                    .where(new Where(Column.code, glCode.getCode().getCode()).and(Column.description, glCode.getDescription()).and(Column.BankHealthID, bnakHealthId));
            final Results results = GUI.getCon().select(sb.build());

            assertFalse(results.isEmpty());
            assertTrue(results.get(0).getInteger(Column.ID) == glId);
        }
        
        assertTrue(BankStuffDBWrap.SelectGLCodes(bnakHealthId).keySet().size() == numOfGlCodes);
        
        //delete gl codes
        Collection<GlCode> codes = BankStuffDBWrap.SelectGLCodes(bnakHealthId).values();
        BankStuffDBWrap.DeleteGLCodes(codes);
        
        //verify delete
        assertTrue(BankStuffDBWrap.SelectGLCodes(bnakHealthId).keySet().size() == 0);
        
    }

    /**
     * Test of InsertGLCode method, of class BankStuffDBWrap.
     */
    @Test
    public void testInsertGLCode() throws Exception {
        
        final int bankHealthId = someId.incrementAndGet();
        
        for(int i = 0; i < 2; ++i){
            final Code code = new Code(someId.incrementAndGet());
            final String description = getAString();

            //insert a glcode
            final int glId;
            if(i == 0){
                glId = BankStuffDBWrap.InsertGLCode(code, description, bankHealthId);
            }else if(i == 1){
                glId = BankStuffDBWrap.InsertGLCode(new GlCode(-1, code, description, bankHealthId), bankHealthId);
            }else{
                throw new AssertionError("i is not 0 or 1");
            }

            
            {
                //for testing:
//                final SelectBuilder sb = new SelectBuilder().addValue(Column._star).table(Table.glcodes);
//                final Results results = GUI.getCon().select(sb.build());
//                final int breakPoint = 1;
            }

            //verify insert
            final SelectBuilder sb = new SelectBuilder().addValue(Column._star).table(Table.glcodes)
                    .where(new Where(Column.ID, glId).and(Column.description, description).and(Column.BankHealthID, bankHealthId));
            final Results results = GUI.getCon().select(sb.build());

            assertFalse(results.isEmpty());

            final Result result = results.get(0);
            assertTrue(result.getInteger(Column.ID) == glId);
            assertTrue(result.getInteger(Column.code) == code.getCode());
            assertTrue(result.getString(Column.description).equals(description));
        }
        
    }

    /**
     * Test of InsertGLCodes method, of class BankStuffDBWrap.
     */
    @Test
    public void testInsertGLCodes() throws Exception {
        
        final int numOfGlCodes = 40;
        final int bankHealthId = someId.incrementAndGet();
        
        assertTrue(BankStuffDBWrap.SelectGLCodes(bankHealthId).keySet().isEmpty());
        
        final Map<Code, GlCode> codeToDescription = new HashMap<Code, GlCode>();
        for(int i = 0; i < numOfGlCodes; ++i){
            final Code code = new Code(someId.incrementAndGet());
            final GlCode glCode = new GlCode(code, getAString());
            codeToDescription.put(code, glCode);
        }
        
        //insert:
        BankStuffDBWrap.InsertGLCodes(codeToDescription.values(), bankHealthId);
        
        assertTrue(BankStuffDBWrap.SelectGLCodes(bankHealthId).keySet().size() == numOfGlCodes);
        
        final Map<Code, GlCode> codeToDescriptionSelected = BankStuffDBWrap.SelectGLCodes(bankHealthId);
        
        assertTrue(codeToDescription.size() == codeToDescriptionSelected.size());
        
        for(final Entry<Code, GlCode> orig : codeToDescription.entrySet()){
            final GlCode glCode = codeToDescriptionSelected.get(orig.getKey());
            assertTrue(glCode.equals(orig.getValue()));
        }
    }

    /**
     * Test of SelectGLCode method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectGLCode() throws Exception {

        final int bankHealthId = someId.incrementAndGet();
        
        final Code code = new Code(someId.incrementAndGet());
        final String description = getAString();

        //insert a glcode
        //we're doing this crazy insert and creation as part of testing that 
        //the glcode and the insert are working together nicely
        final GlCode glCode = new GlCode(
                BankStuffDBWrap.InsertGLCode(code, description, bankHealthId), 
                code, 
                description, 
                bankHealthId);

        final GlCode glCode2 = BankStuffDBWrap.SelectGLCode(glCode.getId());
        
        assertTrue(glCode.equals(glCode2));
    }

    /**
     * Test of SelectGLCodeID method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectGLCodeID() throws Exception {
        
        
        final int bankHealthId = someId.incrementAndGet();
        
        final Code code = new Code(someId.incrementAndGet());
        final String description = getAString();

        //insert a glcode
        //we're doing this crazy insert and creation as part of testing that 
        //the glcode and the insert are working together nicely
        final GlCode glCode = new GlCode(
                BankStuffDBWrap.InsertGLCode(code, description, bankHealthId), 
                code, 
                description, 
                bankHealthId);

        final int glCodeId = BankStuffDBWrap.SelectGLCodeID(glCode, bankHealthId);
        
        assertTrue(glCode.getId() == glCodeId);
        
    }
    
    /**
     * Test of SelectGLCodeID method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectGLCodeID_2() throws Exception {
        
        
        final int bankHealthId = someId.incrementAndGet();
        
        final Code code = new Code(someId.incrementAndGet());
        final String description = getAString();

        final int glCodeIdThatIsWrong = someId.incrementAndGet();
        final GlCode glCode = new GlCode(
                glCodeIdThatIsWrong, 
                code, 
                description, 
                bankHealthId);
        
        final int glCodeIdInDb = BankStuffDBWrap.SelectGLCodeID(glCode, bankHealthId);
        
        assertTrue(glCodeIdInDb == -1);
        assertTrue(glCode.getId() == -1);
    }

    /**
     * Test of SelectGLCodes method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectGLCodes() throws Exception {
        
        final int numOfGlCodes = 40;
        final int bankHealthId = someId.incrementAndGet();
        
        assertTrue(BankStuffDBWrap.SelectGLCodes(bankHealthId).keySet().isEmpty());
        
        final Map<Code, GlCode> codeToDescription = new HashMap<Code, GlCode>();
        for(int i = 0; i < numOfGlCodes; ++i){
            final GlCode glCode = new GlCode(new Code(someId.incrementAndGet()), getAString());
            codeToDescription.put(glCode.getCode(), glCode);
        }
        
        //insert:
        BankStuffDBWrap.InsertGLCodes(codeToDescription.values(), bankHealthId);
        
        assertTrue(BankStuffDBWrap.SelectGLCodes(bankHealthId).keySet().size() == numOfGlCodes);
        
        final Map<Code, GlCode> codeToDescriptionSelected = BankStuffDBWrap.SelectGLCodes(bankHealthId);
        
        assertTrue(codeToDescription.size() == codeToDescriptionSelected.size());
        
        for(final Entry<Code, GlCode> orig : codeToDescription.entrySet()){
            final GlCode GlCode = codeToDescriptionSelected.get(orig.getKey());
            assertTrue(GlCode.equals(orig.getValue()));
        }
        
    }

    /**
     * Test of DeleteBHSnapShots method, of class BankStuffDBWrap.
     */
    @Test
    public void testDeleteBHSnapShots() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        final int numOfSs = 40;
        assertTrue(BankStuffDBWrap.SelectBHSnapShots(bankHealthID).isEmpty());
        
        final List<BHSnapShot> sss = new ArrayList<BHSnapShot>();
        for(int i = 0; i < numOfSs; ++i){
            sss.add(getABHSnapShot());
        }
        
        BankStuffDBWrap.InsertBHSnapShots(sss, bankHealthID);
        
        
        assertTrue(BankStuffDBWrap.SelectBHSnapShots(bankHealthID).size() == sss.size());
        
        BankStuffDBWrap.DeleteBHSnapShots(bankHealthID);
        
        assertTrue(BankStuffDBWrap.SelectBHSnapShots(bankHealthID).size() == 0);
        
    }

    /**
     * Test of InsertBHSnapShot method, of class BankStuffDBWrap.
     */
    @Test
    public void testInsertBHSnapShot() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        
        assertTrue(BankStuffDBWrap.SelectBHSnapShots(bankHealthID).isEmpty());
        
        final BHSnapShot ss = getABHSnapShot();
        BankStuffDBWrap.InsertBHSnapShot(ss, bankHealthID);
        
        final List<BHSnapShot> sss = BankStuffDBWrap.SelectBHSnapShots(bankHealthID);
        assertTrue(sss.size() == 1);
        
        assertTrue(sss.get(0).equals(ss));
    }

    /**
     * Test of InsertBHSnapShots method, of class BankStuffDBWrap.
     */
    @Test
    public void testInsertBHSnapShots() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        final int numOfSs = 40;
        assertTrue(BankStuffDBWrap.SelectBHSnapShots(bankHealthID).isEmpty());
        
        final List<BHSnapShot> sss = new ArrayList<BHSnapShot>();
        for(int i = 0; i < numOfSs; ++i){
            sss.add(getABHSnapShot());
        }
        
        BankStuffDBWrap.InsertBHSnapShots(sss, bankHealthID);
        
        
        final List<BHSnapShot> sssSelected = BankStuffDBWrap.SelectBHSnapShots(bankHealthID);
        assertTrue(sssSelected.size() == sss.size());
        
        for(final BHSnapShot ss : sss){
            boolean found = false;
            for(final BHSnapShot ssSelected : sssSelected){
                if(ss.equals(ssSelected)){
                    found = true;
                }
            }
            if(!found){
                throw new AssertionError("couldn't find " + ss + " in " + sssSelected);
            }
        }
    }

    /**
     * Test of SelectBHSnapShotID method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectBHSnapShotID() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        
        assertTrue(BankStuffDBWrap.SelectBHSnapShots(bankHealthID).isEmpty());
        
        final BHSnapShot ss = getABHSnapShot();
        final int ssId = BankStuffDBWrap.InsertBHSnapShot(ss, bankHealthID);
        
        final int ssIdSelected = BankStuffDBWrap.SelectBHSnapShotID(ss, bankHealthID);
        
        assertTrue(ssId == ssIdSelected);
    }

    /**
     * Test of SelectBHSnapShots method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectBHSnapShots() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        final int numOfSs = 40;
        assertTrue(BankStuffDBWrap.SelectBHSnapShots(bankHealthID).isEmpty());
        
        final List<BHSnapShot> sss = new ArrayList<BHSnapShot>();
        for(int i = 0; i < numOfSs; ++i){
            sss.add(getABHSnapShot());
        }
        
        BankStuffDBWrap.InsertBHSnapShots(sss, bankHealthID);
        
        
        final List<BHSnapShot> sssSelected = BankStuffDBWrap.SelectBHSnapShots(bankHealthID);
        assertTrue(sssSelected.size() == sss.size());
        
        for(final BHSnapShot ss : sss){
            boolean found = false;
            for(final BHSnapShot ssSelected : sssSelected){
                if(ss.equals(ssSelected)){
                    found = true;
                }
            }
            if(!found){
                throw new AssertionError("couldn't find " + ss + " in " + sssSelected);
            }
        }
        
    }

    /**
     * Test of DeleteBills method, of class BankStuffDBWrap.
     */
    @Test
    public void testDeleteBills() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        final BankHealth bh = new BankHealth();
        
        assertTrue(BankStuffDBWrap.SelectBills(bankHealthID).isEmpty());
        
        final int numOfBills = 40;
        for(int i = 0; i < numOfBills; ++i){
            final Bill bill = getABillAndInsertGL();
            bh.addBill(bill);
            final int billId = BankStuffDBWrap.InsertBill(bill, bankHealthID);

            final int billIdSelected = BankStuffDBWrap.SelectBillID(bill, bankHealthID);
            assertTrue(billId == billIdSelected);
        }
        
        
        final List<Bill> billsSelected = BankStuffDBWrap.SelectBills(bankHealthID);
        assertTrue(billsSelected.size() == bh.getBills().size());
        
        BankStuffDBWrap.DeleteBills(bankHealthID, bh);
        
        final List<Bill> billsSelected2 = BankStuffDBWrap.SelectBills(bankHealthID);
        assertTrue(billsSelected2.isEmpty());
        
        for(final Bill bill : bh.getBills()){
            assertEquals(bill.getGLCode().getId(), -1);
        }
    }

    /**
     * Test of InsertBill method, of class BankStuffDBWrap.
     */
    @Test
    public void testInsertBill() throws Exception {
        final int bankHealthID = someId.incrementAndGet();
        
        assertTrue(BankStuffDBWrap.SelectBills(bankHealthID).isEmpty());
        
        final Bill bill = getABillAndInsertGL();
        final int billId = BankStuffDBWrap.InsertBill(bill, bankHealthID);
        
        final int billIdSelected = BankStuffDBWrap.SelectBillID(bill, bankHealthID);
        assertTrue(billId == billIdSelected);
        
        final List<Bill> bills = BankStuffDBWrap.SelectBills(bankHealthID);
        assertTrue(bills.size() == 1);
        
        assertTrue(bills.get(0).equals(bill));
        
    }

    /**
     * Test of InsertBills method, of class BankStuffDBWrap.
     */
    @Test
    public void testInsertBills() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        
        assertTrue(BankStuffDBWrap.SelectBills(bankHealthID).isEmpty());
        
        final int numOfBills = 40;
        final List<Bill> bills = new ArrayList<Bill>();
        for(int i = 0; i < numOfBills; ++i){
            final Bill bill = getABillAndInsertGL();
            bills.add(bill);
            final int billId = BankStuffDBWrap.InsertBill(bill, bankHealthID);

            final int billIdSelected = BankStuffDBWrap.SelectBillID(bill, bankHealthID);
            assertTrue(billId == billIdSelected);
        }
        
        
        final List<Bill> billsSelected = BankStuffDBWrap.SelectBills(bankHealthID);
        assertTrue(billsSelected.size() == bills.size());
        
        for(final Bill bill : bills){
            boolean found = false;
            for(final Bill billSelected : billsSelected){
                if(bill.equals(billSelected)){
                    found = true;
                }
            }
            if(!found){
                throw new AssertionError("couldn't find " + bill + " in " + billsSelected);
            }
        }
    }

    /**
     * Test of SelectBillID method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectBillID() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        
        assertTrue(BankStuffDBWrap.SelectBills(bankHealthID).isEmpty());
        
        final Bill bill = getABillAndInsertGL();
        final int billId = BankStuffDBWrap.InsertBill(bill, bankHealthID);
        
        final int billIdSelected = BankStuffDBWrap.SelectBillID(bill, bankHealthID);
        assertTrue(billId == billIdSelected);
        
        final List<Bill> bills = BankStuffDBWrap.SelectBills(bankHealthID);
        assertTrue(bills.size() == 1);
        
        assertTrue(bills.get(0).equals(bill));
        
    }

    /**
     * Test of SelectBills method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectBills() throws Exception {
        
        final int bankHealthID = someId.incrementAndGet();
        
        assertTrue(BankStuffDBWrap.SelectBills(bankHealthID).isEmpty());
        
        final int numOfBills = 40;
        final List<Bill> bills = new ArrayList<Bill>();
        for(int i = 0; i < numOfBills; ++i){
            final Bill bill = getABillAndInsertGL();
            bills.add(bill);
            final int billId = BankStuffDBWrap.InsertBill(bill, bankHealthID);

            final int billIdSelected = BankStuffDBWrap.SelectBillID(bill, bankHealthID);
            assertTrue(billId == billIdSelected);
        }
        
        
        final List<Bill> billsSelected = BankStuffDBWrap.SelectBills(bankHealthID);
        assertTrue(billsSelected.size() == bills.size());
        
        for(final Bill bill : bills){
            boolean found = false;
            for(final Bill billSelected : billsSelected){
                if(bill.equals(billSelected)){
                    found = true;
                }
            }
            if(!found){
                throw new AssertionError("couldn't find " + bill + " in " + billsSelected);
            }
        }
    }

    /**
     * Test of DeleteBankHealth method, of class BankStuffDBWrap.
     */
    @Test
    public void testDeleteBankHealth() throws Exception {
        
        final int companyID = someId.incrementAndGet();
        
        
        final Result result = BankStuffDBWrap.SelectBankHealth(companyID);
        assertNull(result);
        
        
        final BankHealth bH = getANewBankHealth();
        BankStuffDBWrap.InsertBankHealth(bH, companyID);
        
        final Result result2 = BankStuffDBWrap.SelectBankHealth(companyID);
        assertNotNull(result2);
        
        
        final int bankHealthID = BankStuffDBWrap.SelectBankHealthID(companyID);
        final int checkBookId = BankStuffDBWrap.SelectCheckBookID(bH.getCurrentCheckBook(), bankHealthID);
        
        assertTrue("current checkBookId is: " + result2.getInteger(Column.currentCheckBookID), 
                result2.getInteger(Column.currentCheckBookID) == checkBookId);
        assertTrue(result2.getInteger(Column.companyID) == companyID);
        
        
        BankStuffDBWrap.DeleteBankHealth(companyID, bH);
        
        
        final Result result3 = BankStuffDBWrap.SelectBankHealth(companyID);
        assertNull(result3);
        
    }

    /**
     * Test of InsertBankHealth method, of class BankStuffDBWrap.
     */
    @Test
    public void testInsertBankHealth() throws Exception {
        
        final int companyID = someId.incrementAndGet();
        
        
        final Result result = BankStuffDBWrap.SelectBankHealth(companyID);
        assertNull(result);
        
        
        final BankHealth bH = getANewBankHealth();
        BankStuffDBWrap.InsertBankHealth(bH, companyID);
        
        final Result result2 = BankStuffDBWrap.SelectBankHealth(companyID);
        assertNotNull(result2);
        
        final int bankHealthID = BankStuffDBWrap.SelectBankHealthID(companyID);
        final int checkBookId = BankStuffDBWrap.SelectCheckBookID(bH.getCurrentCheckBook(), bankHealthID);
        
        assertTrue(result2.getInteger(Column.currentCheckBookID) == checkBookId);
        assertTrue(result2.getInteger(Column.companyID) == companyID);
        
    }


    /**
     * Test of SelectBankHealth method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectBankHealth() throws Exception {
        
        final int companyID = someId.incrementAndGet();
        
        
        final Result result = BankStuffDBWrap.SelectBankHealth(companyID);
        assertNull(result);
        
        
        final BankHealth bH = getANewBankHealth();
        BankStuffDBWrap.InsertBankHealth(bH, companyID);
        
        final Result result2 = BankStuffDBWrap.SelectBankHealth(companyID);
        assertNotNull(result2);
        
        final int bankHealthID = BankStuffDBWrap.SelectBankHealthID(companyID);
        final int checkBookId = BankStuffDBWrap.SelectCheckBookID(bH.getCurrentCheckBook(), bankHealthID);
        assertTrue(result2.getInteger(Column.currentCheckBookID) == checkBookId);
        assertTrue(result2.getInteger(Column.companyID) == companyID);
        
    }

    /**
     * Test of SelectBankHealthID method, of class BankStuffDBWrap.
     */
    @Test
    public void testSelectBankHealthID() throws Exception {
        
        final int companyID = someId.incrementAndGet();
        
        
        final Result result = BankStuffDBWrap.SelectBankHealth(companyID);
        assertNull(result);
        
        
        final BankHealth bH = getANewBankHealth();
        BankStuffDBWrap.InsertBankHealth(bH, companyID);
        
        final int bankHealthID = BankStuffDBWrap.SelectBankHealthID(companyID);
        final Result result2 = BankStuffDBWrap.SelectBankHealth(companyID);
        assertNotNull(result2);
        
        assertTrue(result2.getInteger(Column.ID) == bankHealthID);
        
        final int checkBookId = BankStuffDBWrap.SelectCheckBookID(bH.getCurrentCheckBook(), bankHealthID);
        assertTrue(result2.getInteger(Column.currentCheckBookID) == checkBookId);
        assertTrue(result2.getInteger(Column.companyID) == companyID);
        
    }

    private static CheckBook getANewCheckBook() throws DataException {
        return new CheckBook(getSomeNewChecks(5), getSomeNewTransactions(6));
    }
    
    private static Checks getSomeNewChecks(int numChecks) throws DataException{
        final Checks checks = new ChecksImpl();
        for(int i = 0; i < numChecks; ++i){
            checks.add(getANewCheckAndInsertGL());
        }
        
        return checks;
    }

    
    
    
    private static String getAString(){
        final int stringSize = rand.nextInt(50);
        final StringBuilder sb = new StringBuilder();
        for(int i = 0; i < stringSize; ++i){
            char c = (char) (32 + rand.nextInt(94));
            sb.append(c);
        }
        
        return sb.toString();
    }
    
    private static Calendar getANewCalendar(){
        final Calendar c = Calendar.getInstance();
        final int fieldsToRoll = rand.nextInt(50);
        
        for(int i = 0; i < fieldsToRoll; ++i){
            c.roll(rand.nextInt(15), rand.nextInt());
        }
        
        return c;
    }
    
    public static List<Transaction> getSomeNewTransactions(int numTransactions) throws DataException{
        final List<Transaction> transactions = new ArrayList<Transaction>();
        
        for(int i = 0; i < numTransactions; ++i){
            transactions.add(getATransactionAndInsertGL());
        }
        
        return transactions;
    }
    
    public static Transaction getATransactionAndInsertGL() throws DataException {
        
        final GlCode code = new GlCode(new Code(someId.incrementAndGet()), getAString());
        final int bankHealthID = someId.incrementAndGet();
        
        BankStuffDBWrap.InsertGLCode(code, bankHealthID);
        
        return new Transaction(getNewCurrency(), getAString(), getANewCalendar(),
            code, rand.nextBoolean());
    }
    
    public static double getNiceDouble(){
        final String doubleS = "" + rand.nextDouble();
        final int period = doubleS.indexOf(".");
        
        final String niceD = doubleS.substring(0, Math.min(period + 3, doubleS.length()));
        
        return Double.parseDouble(niceD);
    }

    public static Currency getNewCurrency() {
        return new Currency("" + getNiceDouble());
    }

    public static void assertChecksEqual(Checks checks, Checks checks0) {
        if(checks == null && checks0 == null){
            return;
        }
        
        assertTrue(checks.size() == checks0.size());
        
        for(final Check check : checks){
            boolean found = false;
            for(final Check check0 : checks0){
                //this will eventually fail, but for now we'll use the check number
                if(check.getCheckNum() == check0.getCheckNum()){
                    assertChecksEqual(check, check0);
                    found = true;
                    break;
                }
            }
            
            if(!found){
                throw new AssertionError("Couldn't find check: " + check + ", in " + checks0);
            }
        }
//        
//        for(int i = 0; i < checks.size(); ++i){
//            assertChecksEqual(checks.get(i), checks0.get(i));
//        }
    }
    
    public static void assertChecksEqual(Check check, Check check0) {
        if(!check.getClearDate().equals(check0.getClearDate())){
            throw new AssertionError("clear dates don't match for " + check + " " + check0);
        }
        assertTrue(check.getDateS().equals(check0.getDateS()));
        assertTrue(check.getDollarsS().equals(check0.getDollarsS()));
        assertTrue(check.getForS().equals(check0.getForS()));
        assertTrue(check.getGoneThrough() == check0.getGoneThrough());
        assertTrue(check.getPayTo().equals(check0.getPayTo()));
        assertTrue(check.getAmount().equals(check0.getAmount()));
        assertTrue(check.getCheckNum() == check0.getCheckNum());
        assertGlCodesEqual(check.getGlCode(), check0.getGlCode());
    }

    public static void assertTransactionsEqual(List<Transaction> transactions, List<Transaction> transactions0) {
        if(transactions == null && transactions0 == null){
            return;
        }
        
        assertTrue(transactions.size() == transactions0.size());
        
        for(final Transaction trans : transactions){
            boolean found = false;
            for(final Transaction trans0 : transactions0){
                //this will break in the future, but for now... assume description
                //is unique
                if(trans.getDescription().equals(trans0.getDescription())){
                    assertTransactionsEqual(trans, trans0);
                    found = true;
                    break;
                }
            }
            
            if(!found){
                throw new AssertionError("Couldn't find transaction: " + trans + ", in " + transactions0);
            }
        }
//        for(int i = 0; i < transactions.size(); ++i){
//            assertTransactionsEqual(transactions.get(i), transactions0.get(i));
//        }
    }

    public static void assertTransactionsEqual(Transaction trans, Transaction trans0) {
        assertEquals(trans.getDateS(), trans0.getDateS());
        assertEquals(trans.getDate(), trans0.getDate());
        assertEquals(trans.getDateS(), trans0.getDateS());
        assertEquals(trans.getDescription(), trans0.getDescription());
        assertGlCodesEqual(trans.getGLCode(), trans0.getGLCode());
        assertTrue(trans.getGoneThrough() == trans0.getGoneThrough());
    }

    public static void assertCheckBooksEqual(CheckBook cB, CheckBook cB2) {
        
        assertChecksEqual(cB.getChecks(), cB2.getChecks());
        assertTransactionsEqual(cB.getTransactions(), cB2.getTransactions());
        
        final String debugString = "Comparing checkbooks:\n" + cB.toString() + "\n" + cB2.toString();
        
        assertEquals(debugString, cB.getAdjustedBalance(), cB2.getAdjustedBalance());
        assertEquals(debugString, cB.getBalance(), cB2.getBalance());
        assertEquals(debugString, cB.getWaitingToGoThroughC(), cB2.getWaitingToGoThroughC());
        assertEquals(debugString, cB.getWaitingToGoThroughT(), cB2.getWaitingToGoThroughT());
    }

    public static BHSnapShot getABHSnapShot() {
        return new BHSnapShot(getAString(), getAString(), getAString(),
        getAString(), getAString(), getAString(),
        getAString(), getAString(), getAString());
    }

    public static Bill getABillAndInsertGL() throws DataException {
        
        final GlCode code = new GlCode(new Code(someId.incrementAndGet()), getAString());
        final int bankHealthID = someId.incrementAndGet();
        
        BankStuffDBWrap.InsertGLCode(code, bankHealthID);
        
        return new Bill(getNewCurrency(), rand.nextInt(), code, rand.nextInt(),
        getAString());
    }

    public static BankHealth getANewBankHealth() throws DataException {
        
        final List<Bill> bills = new ArrayList<Bill>();
        final int numOfBills = 40;
        for(int i = 0; i < numOfBills; ++i){
            bills.add(getABillAndInsertGL());
        }
        
        
        final CheckBook cb = getANewCheckBook();
        
        final List<BHSnapShot> sss = new ArrayList<BHSnapShot>();
        for(int i = 0; i < 40; ++i){
            sss.add(getABHSnapShot());
        }
        
        final Map<Code, GlCode> glCodes = new HashMap<Code, GlCode>();
        
        
        return new BankHealth(cb, bills, glCodes, sss);
    }
    
    private static Check getANewCheckAndInsertGL() throws DataException {
        
        final GlCode code = new GlCode(new Code(someId.incrementAndGet()), getAString());
        final int bankHealthID = someId.incrementAndGet();
        
        BankStuffDBWrap.InsertGLCode(code, bankHealthID);
        
        return new Check(someId.incrementAndGet(), getANewCalendar(), getAString(), getNewCurrency(), 
            code, rand.nextBoolean(), getANewCalendar(), 
            getAString());
    }
    
    private static GlCode getAndInsertGlCode() throws DataException{
        return getAndInsertGlCode(someId.incrementAndGet());
    }
    
    private static GlCode getAndInsertGlCode(int bankHealthId) throws DataException{
        final GlCode glCode = new GlCode(new Code(someId.incrementAndGet()), getAString());

        BankStuffDBWrap.InsertGLCode(glCode, bankHealthId);
        
        return glCode;
    }
    
    private static void assertGlCodesEqual(GlCode glCode, GlCode glCode0) {
        
        final String debugString = "comparing " + glCode + " to " + glCode0;
        assertEquals(debugString, glCode.getDescription(), glCode0.getDescription());
        assertEquals(debugString, glCode.getBankHealthID(), glCode0.getBankHealthID());
        assertEquals(debugString, glCode.getCode(), glCode0.getCode());
        assertEquals(debugString, glCode.getId(), glCode0.getId());
        assertEquals(debugString, glCode, glCode0);
        
    }
}
