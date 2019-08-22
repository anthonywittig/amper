/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package AMP2.BankStuff.check;

import AMP2.BankStuff.Check;
import AMP2.BankStuff.Currency;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import org.junit.Test;

/**
 *
 * @author awittig
 */
public class ChecksImplTest {
    
    private static Random RAND = new Random();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    @Test
    public void checksWithDifferentDatesAreInOrder(){
        List<Check> input = new ArrayList<Check>();
        input.add(getCheck(2));
        input.add(getCheck(4));
        input.add(getCheck(8));
        input.add(getCheck(3));
        input.add(getCheck(1));
        
        Checks checks = new ChecksImpl(input);
        
        verifyChecksOrder(checks);
    }

    private Check getCheck(int day) {
        Calendar date = Calendar.getInstance();
        date.set(2000, 1, day);
        return new Check(RAND.nextInt(), date, "pay to", new Currency(0));
    }

    private void verifyChecksOrder(Checks checks) {
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.YEAR, 9999);
        
        for(Check check : checks){
            Calendar thisDate = check.getDate();
            if(lastDate.before(thisDate)){
                throw new AssertionError("checks not in order! last date: " + 
                        dateFormat.format(lastDate.getTime()) + ", this date: " + 
                        dateFormat.format(thisDate.getTime()));
            }else if(lastDate.equals(thisDate)){
                throw new AssertionError("not sure if there is a problem here, need to implement other tests");
            }
            
            lastDate = thisDate;
        }
    }
}
