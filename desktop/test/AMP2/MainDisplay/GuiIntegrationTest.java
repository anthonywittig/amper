package AMP2.MainDisplay;

import AMP2.Payroll.OrWithEng;
import AMP2.DatabaseStuff.DatabaseHelper;
import java.util.HashMap;
import AMP2.Payroll.PayPeriod;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import AMP2.BankStuff.Transaction;
import java.util.List;
import AMP2.BankStuff.Check;
import AMP2.BankStuff.GlCode.Code;
import AMP2.BankStuff.GlCode;
import AMP2.DatabaseStuff.BankStuffDBWrap;
import AMP2.BankStuff.BHTPane;
import AMP2.Payroll.Employee;
import AMP2.BankStuff.Currency;
import AMP2.BankStuff.check.Checks;
import AMP2.BankStuff.check.ChecksImpl;
import AMP2.DatabaseStuff.BankStuffDBWrapTest;
import AMP2.DatabaseStuff.command.SelectBuilder;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.tables.Column;
import org.junit.Test;
import AMP2.DatabaseStuff.tables.Table;
import AMP2.Payroll.FicaEng;
import AMP2.Util.MiscStuff;
import static junit.framework.Assert.*;

/**
 *
 * @author awittig
 */
public class GuiIntegrationTest extends IntegrationTestBase{

    public final static long timeToWait = 1000;

    @Test
    public void testIntegration() throws Exception {

        

        //add some new employees (addNewEmployee ends up doing a save)
        final String awittig = "Anthony Wittig";
        addNewEmployee(awittig, "123-45-6789", "17234 43rd Ln S. SeaTac WA 98188", 3, new Currency("9.50"));
        addNewEmployee("Rachel Wittig", "123-12-1234", "1095 Basin SW Ephrata WA 98823", 0, new Currency("4.56"));

        {
            //add taxes:
            GUI.IntegrationTestHelper.getActionListener(GUI.IntegrationTestHelper.AlKeys.openTaxManager).actionPerformed(null);

            //fica
            final String[][] taxes = new String[][]{{"300", "0"}, {"500", "5"}};
            TaxManager.IntegrationTestHelper.addFicaClaim("3", taxes);
            final int companyId = 3;
            verifyTax(FicaEng.TaxType, "3", taxes, companyId);

            //with
            TaxManager.IntegrationTestHelper.addWithClaim("3", taxes);
            verifyTax(OrWithEng.TaxType, "3", taxes, companyId);


            //other taxes:
            TaxManager.IntegrationTestHelper.setSs(".042");
            TaxManager.IntegrationTestHelper.setMed(".0145");
        }

        //add pay period
        final Employee emp = GUI.IntegrationTestHelper.getEmployee(awittig);
        addAndAssertPayPeriod(emp, "15", "9.04", "135.60", "5.7", "1.97", "0", "0", "127.93");
        addAndAssertPayPeriod(emp, "17.5", "9.04", "158.20", "6.64", "2.29", "0", "0", "149.27");
        addAndAssertPayPeriod(emp, "15.5", "9.04", "140.12", "5.89", "2.03", "0", "0", "132.20");
        addAndAssertPayPeriod(emp, "19.5", "9.04", "176.28", "7.40", "2.56", "0", "0", "166.32");

        if (OrWithEng.orWithEnabled()) {
            addAndAssertPayPeriod(emp, "38.25", "9.50", "363.38", "15.26", "5.27", "5", "5", "332.85");
            addAndAssertPayPeriod(emp, "35.5", "9.50", "337.25", "14.16", "4.89", "5", "5", "308.20");
        } else {
            addAndAssertPayPeriod(emp, "38.25", "9.50", "363.38", "15.26", "5.27", "0", "5", "337.85");
            addAndAssertPayPeriod(emp, "35.5", "9.50", "337.25", "14.16", "4.89", "0", "5", "313.20");
        }

        addAndAssertPayPeriod(emp, "25.75", "9.04", "232.78", "9.78", "3.38", "0", "0", "219.62");
        addAndAssertPayPeriod(emp, "23.5", "9.04", "212.44", "8.92", "3.08", "0", "0", "200.44");


        final GlCode glCode = new GlCode(new Code(33));
        final GlCode glCode2 = new GlCode(new Code(glCode.getCode().getCode() + 1));
        final GlCode glCodeCheckOnly = new GlCode(new Code(glCode2.getCode().getCode() + 1));
        final GlCode glCodeTransactionOnly = new GlCode(new Code(glCodeCheckOnly.getCode().getCode() + 1));

        //add gl
        BHTPane.IntegrationTestHelper.addGlCode("" + glCode.getCode().getCode(), "Payroll (glCode)");
        BHTPane.IntegrationTestHelper.addGlCode("" + glCode2.getCode().getCode(), "Food Product (glCode2)");
        BHTPane.IntegrationTestHelper.addGlCode("" + glCodeCheckOnly.getCode().getCode(), "Utilities (check only)");
        BHTPane.IntegrationTestHelper.addGlCode("" + glCodeTransactionOnly.getCode().getCode(), "City Fees (trans only)");

        //add checks
        Checks checks = new ChecksImpl();
        checks.add(BHTPane.IntegrationTestHelper.addCheck("2012", "1", "8", "2012", "1", "15", "9812", "54.32", "Anthony Wittig", "babysitting", "" + glCode.getCode().getCode()));
        checks.add(BHTPane.IntegrationTestHelper.addCheck("2012", "1", "9", "2012", "1", "16", "9813", "1.23", "Anthony Wittig", "babysitting", "" + glCode.getCode().getCode()));
        checks.add(BHTPane.IntegrationTestHelper.addCheck("2012", "1", "10", "2012", "1", "17", "9814", "123.45", "Anthony Wittig", "babysitting", "" + glCode.getCode().getCode()));
        checks.add(BHTPane.IntegrationTestHelper.addCheck("2012", "1", "11", "2012", "1", "18", "9815", "87.65", "Anthony Wittig", "more money", "" + glCode2.getCode().getCode()));
        checks.add(BHTPane.IntegrationTestHelper.addCheck("2012", "1", "11", "2012", "1", "19", "9816", "998.32", "Anthony Wittig", "more fun money", "" + glCodeCheckOnly.getCode().getCode()));


        //add transactions
        final List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(BHTPane.IntegrationTestHelper.addTransaction("76.54", "fun", glCode.getCode().getCode()));
        transactions.add(BHTPane.IntegrationTestHelper.addTransaction("11.98", "for joe", glCode2.getCode().getCode()));
        transactions.add(BHTPane.IntegrationTestHelper.addTransaction("7.33", "at last", glCodeTransactionOnly.getCode().getCode()));


        final Map<GlCode, String> glDisplayValues = new LinkedHashMap<GlCode, String>();
        glDisplayValues.put(glCode, "102.46");
        glDisplayValues.put(glCodeTransactionOnly, "-7.33");
        glDisplayValues.put(glCode2, "75.67");
        glDisplayValues.put(glCodeCheckOnly, "998.32");


        //specific to after the first save:
        {

            final int checkBookId = 4; //this is a total guess that may change without warning.

            //this is a total guess that changes based on what glcodes we have.
            //you may be able to use the line below to 'guess' the right value to hard code (after doing a save)
            //BankStuffDBWrap.SelectGLCodeID(glCode, BankStuffDBWrap.SelectBankHealthIDFromCheckBookId(checkBookId));            
            final Map<Integer, Currency> glDbValues = new LinkedHashMap<Integer, Currency>();

            glDbValues.put(3, new Currency("-102.46"));
            glDbValues.put(2, new Currency("7.33"));
            glDbValues.put(4, new Currency("-75.67"));
            glDbValues.put(1, new Currency("-998.32"));


            saveAndVerification(checks, transactions, glDisplayValues, checkBookId, glDbValues);
        }


        //specific to after the second save:
        {
            final int checkBookId = 5; //this is a total guess that may change without warning.

            //this is a total guess that changes based on what glcodes we have.
            //you may be able to use the line below to 'guess' the right value to hard code (after doing a save)
            //BankStuffDBWrap.SelectGLCodeID(glCode, BankStuffDBWrap.SelectBankHealthIDFromCheckBookId(checkBookId));            
            final Map<Integer, Currency> glDbValues = new LinkedHashMap<Integer, Currency>();

            glDbValues.put(7, new Currency("-102.46"));
            glDbValues.put(6, new Currency("7.33"));
            glDbValues.put(8, new Currency("-75.67"));
            glDbValues.put(5, new Currency("-998.32"));

            saveAndVerification(checks, transactions, glDisplayValues, checkBookId, glDbValues);
        }

        GUI.IntegrationTestHelper.createGuiAndExpectExistingCompany();


        //specific to after the third save:
        {
            final int checkBookId = 6; //this is a total guess that may change without warning.

            //this is a total guess that changes based on what glcodes we have.
            //you may be able to use the line below to 'guess' the right value to hard code (after doing a save)
            //BankStuffDBWrap.SelectGLCodeID(glCode, BankStuffDBWrap.SelectBankHealthIDFromCheckBookId(checkBookId));            
            final Map<Integer, Currency> glDbValues = new LinkedHashMap<Integer, Currency>();

            glDbValues.put(11, new Currency("-102.46"));
            glDbValues.put(10, new Currency("7.33"));
            glDbValues.put(12, new Currency("-75.67"));
            glDbValues.put(9, new Currency("-998.32"));

            //the checks and transactions are no longer the real checks and 
            //transactions, make sure that they match before replaceing them:
            final Checks bhChecks = GUI.IntegrationTestHelper.getChecks();
            BankStuffDBWrapTest.assertChecksEqual(checks, bhChecks);
            checks = new ChecksImpl();
            checks.addAll(bhChecks);

            final List<Transaction> bhTransactions = GUI.IntegrationTestHelper.getTransactions();
            BankStuffDBWrapTest.assertTransactionsEqual(transactions, bhTransactions);
            transactions.clear();
            transactions.addAll(bhTransactions);


            //make sure the real ones line up with the old ones:
            saveAndVerification(checks, transactions, glDisplayValues, checkBookId, glDbValues);
        }

        GUI.IntegrationTestHelper.getActionListener(GUI.IntegrationTestHelper.AlKeys.openTaxManager).actionPerformed(null);


        TaxManager.IntegrationTestHelper.assertSs(".042");
        TaxManager.IntegrationTestHelper.assertMed(".0145");



        //Thread.sleep(5 * 60 * 1000);
        MiscStuff.writeToLog("Completed GuiIntegrationTest");

    }

    private void addNewYear(final String name, final int year, final String location) throws DataException {
        new Thread(
                new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(timeToWait);
                        } catch (InterruptedException ex) {
                        }
                        //now fill in info:
                        InsertCompanyDialog.IntegrationTestHelper.okBActionPerformed(name, "" + year, location);

                    }
                }).start();

        //new year, current employees
        GUI.IntegrationTestHelper.getActionListener(GUI.IntegrationTestHelper.AlKeys.newYearCurrentEmployee).actionPerformed(null);


        //now verify
        final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.companies).where(new Where(Column.name, name).and(Column.location, location).and(Column.year, year));
        final Results rs = GUI.getCon().select(sb.build());
        assert rs.size() == 1;
    }

    private void addNewEmployee(final String name, final String ss, final String address, final int claim, final Currency rate) throws DataException {

        //show add employee panel:
        GUI.IntegrationTestHelper.getActionListener(GUI.IntegrationTestHelper.AlKeys.addEmployee).actionPerformed(null);
        //add employee (in memory)
        GUI.IntegrationTestHelper.createEmployeeActionPerformed(name, ss, address, claim, rate);
        //add employee (to db)
        GUI.IntegrationTestHelper.saveActionPerformed();

        //verify employee
        final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.employees).where(new Where(Column.name, name).and(Column.SocS, ss).and(Column.Address, address).and(Column.claim, claim).and(Column.CurrentRate, rate));

        final Results rs = GUI.getCon().select(sb.build());
        assertTrue(rs.size() == 1);
    }

    private void verifyCheckInDb(final Check targetCheck, final int checkBookID) {


        try {
            final int targetCheckId = BankStuffDBWrap.SelectCheckID(targetCheck, checkBookID);
            assertTrue("Check doesn't exist? checkId = " + targetCheckId, targetCheckId > 0);

            Check matchingCheck = null;
            final Checks checksToTest = BankStuffDBWrap.SelectChecks(checkBookID);
            for (final Check checkToTest : checksToTest) {

                final int checkToTestId = BankStuffDBWrap.SelectCheckID(checkToTest, checkBookID);

                //see if this check has the same id in the db as the one we want:
                if (checkToTestId == targetCheckId) {
                    assertTrue("More than one check maps to id " + targetCheckId + ". Check1: " + matchingCheck
                            + ", Check2: " + checkToTest, matchingCheck == null);

                    matchingCheck = checkToTest;
                }
            }

            assertTrue("Couldn't find check with id " + targetCheckId + " (this is check: " + targetCheck + ") in " + checksToTest,
                    matchingCheck != null);


            //make sure checks are the same
            BankStuffDBWrapTest.assertChecksEqual(targetCheck, matchingCheck);


        } catch (DataException ex) {
            throw new AssertionError(ex);
        }

    }

    private void verifyGlAmountInDb(int glCodeId, Currency currency, int checkBookID) {

        //a fake gl code for the real deal:
        final GlCode glCode = new GlCode(new Code(-1));
        glCode.setId(glCodeId);

        final StringBuilder debugString = new StringBuilder(
                String.format("glCodeId: %s, currency: %s, checkbookId: %s", glCodeId, currency, checkBookID));

        try {
            final Currency transactionsTotal = BankStuffDBWrap.getTotalPerGlCodeTransactions(glCode, checkBookID);
            final Currency checksTotal = BankStuffDBWrap.getTotalPerGlCodeChecks(glCode, checkBookID);
            final Currency total = transactionsTotal.subtract(checksTotal);

            if (!currency.equals(total)) {
                debugString.append(", transTotal: ").append(transactionsTotal).append(", checksTotal: ").append(checksTotal);


                final SelectBuilder sb = new SelectBuilder().addValue(Column._star).table(Table.transactions);
                final Results results = GUI.getCon().select(sb.build());
                debugString.append("\nAll Transactions:\n").append(results);

                throw new AssertionError(/*new RuntimeException(*/String.format("expected %s but got %s%n%s", currency, total, debugString.toString()))/*)*/;
            }

        } catch (DataException ex) {
            throw new AssertionError(ex);
        }
    }

    private void verifyTransactionInDb(final Transaction transaction, final int checkBookId) {

        try {
            final int transactionId = BankStuffDBWrap.SelectTransactionID(transaction, checkBookId);
            assertTrue("Transaction doesn't exist? transactionId = " + transactionId, transactionId > 0);

            Transaction matchingTrans = null;
            final List<Transaction> transToTest = BankStuffDBWrap.SelectTransactions(checkBookId);
            for (final Transaction tranToTest : transToTest) {

                final int transToTestId = BankStuffDBWrap.SelectTransactionID(tranToTest, checkBookId);

                //see if this check has the same id in the db as the one we want:
                if (transToTestId == transactionId) {
                    assertTrue("More than one transaction maps to id " + transactionId + ". trans1: " + matchingTrans
                            + ", trans2: " + transToTest, matchingTrans == null);

                    matchingTrans = tranToTest;
                }
            }

            assertTrue("Couldn't find trans with id " + transactionId + " (this is transaction: " + transaction + ") in " + transToTest,
                    matchingTrans != null);


            //make sure checks are the same
            BankStuffDBWrapTest.assertTransactionsEqual(transaction, matchingTrans);


        } catch (Exception e) {
            throw new AssertionError(e);
        }

    }

    private void saveAndVerification(final Checks checks, final List<Transaction> transactions,
            final Map<GlCode, String> glDisplayValues, final int checkBookId,
            final Map<Integer, Currency> glDbValues) throws DataException {


        //test db gl amounts:      
        for (final Map.Entry<GlCode, String> glDisplayValue : glDisplayValues.entrySet()) {
            BHTPane.IntegrationTestHelper.verifyGlAmount(glDisplayValue.getKey(), glDisplayValue.getValue());
        }
        for (final Map.Entry<Integer, Currency> glValue : glDbValues.entrySet()) {
            verifyGlAmountInDb(glValue.getKey(), new Currency("0"), checkBookId);
        }

        BankStuffDBWrapTest.assertChecksEqual(checks, GUI.IntegrationTestHelper.getChecks());

        ///
        ///Save
        ///
        GUI.IntegrationTestHelper.saveActionPerformed();




        //verify gl amount
        for (final Map.Entry<GlCode, String> glDisplayValue : glDisplayValues.entrySet()) {
            BHTPane.IntegrationTestHelper.verifyGlAmount(glDisplayValue.getKey(), glDisplayValue.getValue());
        }

        for (final Map.Entry<Integer, Currency> glValue : glDbValues.entrySet()) {
            verifyGlAmountInDb(glValue.getKey(), glValue.getValue(), checkBookId);
        }

        BankStuffDBWrapTest.assertChecksEqual(checks, GUI.IntegrationTestHelper.getChecks());

        //verify checks:
        for (final Check check : checks) {
            verifyCheckInDb(check, checkBookId);
        }

        //post checks

        //verify transactions
        for (final Transaction transaction : transactions) {
            verifyTransactionInDb(transaction, checkBookId);
        }


        //adj bank transaction
    }

    private void assertPayPeriodsEqual(PayPeriod payP, Map<String, Currency> targetP) {

        //todo: fill in the rest of these:
        //assertEquals("message", ..., payP.getClaim() );
        //assertEquals("", ...,payP.getCompanyID());
        //assertEquals("", ..., payP.getDate());

        assertEquals(targetP.get("fica"), payP.getFica());
        assertEquals(targetP.get("gross"), payP.getGrossPay());
        assertEquals("ss should match", targetP.get("ss"), payP.getSS());
        assertEquals("med shold match", targetP.get("med"), payP.getMed());
        assertEquals("withholding should match", targetP.get("with"), payP.getOrWith());
        assertEquals("net should match", targetP.get("net"), payP.getNetPay());

    }

    private void verifyTax(int taxType, String claimS, String[][] taxes, int companyId) throws DataException {
        final int claim = Integer.parseInt(claimS);

        final Currency verySmallAmount = new Currency(".01");

        for (final String[] tax : taxes) {
            final Currency grossPay = new Currency(tax[0]).subtract(verySmallAmount);
            final Currency taxTarget = new Currency(tax[1]);

            assertEquals("taxType: " + taxType + ", claim: " + claim + ", tax: " + tax.toString() + ", companyId: " + companyId,
                    taxTarget, DatabaseHelper.getTaxFromDB(taxType, companyId, claim, grossPay));
        }
    }

    private void addAndAssertPayPeriod(Employee emp, String hrs, String wage,
            String gross, String ss, String med, String wh, String fica, String net) throws InterruptedException {

        final PayPeriod payP = GUI.IntegrationTestHelper.addPayPeriod(emp, hrs, wage);
        final Map<String, Currency> targetP = new HashMap<String, Currency>();

        targetP.put("fica", new Currency(fica));
        targetP.put("gross", new Currency(gross));
        targetP.put("ss", new Currency(ss));
        targetP.put("med", new Currency(med));
        targetP.put("with", new Currency(wh));
        targetP.put("net", new Currency(net));

        assertPayPeriodsEqual(payP, targetP);
    }
}
