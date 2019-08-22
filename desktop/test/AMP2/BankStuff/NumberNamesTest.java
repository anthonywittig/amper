/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.BankStuff;

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
public class NumberNamesTest {
    
    public NumberNamesTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of numName method, of class NumberNames.
     */
    @Test
    public void testNumName() {
        
        final String[] input = {
            "4567.89", 
            ".89",
            "1000",
            "1234",
            "12.34"
        };
        final String[] expected = {
            "Four Thousand Five Hundred Sixty Seven And 89/100", 
            "Zero And 89/100",
            "One Thousand And 00/100", 
            "One Thousand Two Hundred Thirty Four And 00/100",
            "Twelve And 34/100"
        };
        
        for(int test = 0; test < input.length; ++test){
            Currency num = new Currency(input[test]);
            String expResult = expected[test];
            String result = NumberNames.numName(num);
            assertEquals("testing: " + input[test], expResult, result);
        }
    }
}
