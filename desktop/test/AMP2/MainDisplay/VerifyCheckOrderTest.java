/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.MainDisplay;

import AMP2.BankStuff.BHTPane;
import AMP2.BankStuff.Check;
import AMP2.BankStuff.Currency;
import AMP2.BankStuff.GlCode;
import AMP2.BankStuff.check.Checks;
import AMP2.BankStuff.check.ChecksImpl;
import AMP2.DatabaseStuff.BankStuffDBWrap;
import AMP2.DatabaseStuff.command.SelectBuilder;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Table;
import AMP2.Util.MiscStuff;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author awittig
 */
public class VerifyCheckOrderTest extends IntegrationTestBase{
    
    private final GlCode glCode = new GlCode(new GlCode.Code(33));
    private final List<Check> checks = new ArrayList<Check>();
    
    @Test
    public void testIntegration() throws Exception {
//need to add some checks randomly in the checks array and then verify we broke the front end. current mess ignores the "addChecksBackEnd"

//        addChecksDirectly();
//        GUI.IntegrationTestHelper.getChecks()
        addSampleCheckSoThatWeCanEasilyGetReferencesToIds();
        save();
        addChecksBackEnd();
        
        GUI.IntegrationTestHelper.createGuiAndExpectExistingCompany();
        
        BHTPane.IntegrationTestHelper.verifyAllChecksAreDisplayedInOrder(4);
        
        MiscStuff.writeToLog("Completed VerifyCheckOrderTest.testIntegration()");
    }

    private void addGlCode() {
        BHTPane.IntegrationTestHelper.addGlCode("" + glCode.getCode().getCode(), "Payroll (glCode)");
    }
    
    private void addSampleCheckSoThatWeCanEasilyGetReferencesToIds(){
        
        addGlCode();
        checks.add(BHTPane.IntegrationTestHelper.addCheck("2012", "1", "1", "2012", "1", "1", "1", "54.32", "Anthony Wittig", "babysitting", "" + glCode.getCode().getCode()));
    }

    private void addChecksBackEnd() throws Exception{
        
        final int checkBookId = getCheckBookId();
        
        
        final Checks checksToAdd = new ChecksImpl();
        checksToAdd.add(makeCheck(3, "54.32", "Anthony Wittig"));
        checksToAdd.add(makeCheck(4, "1.23", "James"));
        checksToAdd.add(makeCheck(2, "123.45", "Joe Joe"));
        
        BankStuffDBWrap.InsertChecks(checksToAdd, checkBookId);
    }
    
    private Check makeCheck(int checkNum, String amount, String payTo) {
        
        final Currency amountC = new Currency(amount);
        return new Check(checkNum, Calendar.getInstance(), payTo, amountC);
    }

    private void save() {
        GUI.IntegrationTestHelper.saveActionPerformed();
    }

    private int getCheckBookId() {
        final SelectBuilder sb = new SelectBuilder().addValue(Column._star).table(Table.checks);
        
        try{
            final Results results = GUI.getCon().select(sb.build());
            if(results.isEmpty()){
                throw new RuntimeException("results are empty");
            }else{
                return results.get(0).getInteger(Column.checkBookID);
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    
    

    
}
