/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.BankStuff;

import java.math.BigDecimal;
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
public class CurrencyTest {
    
    public CurrencyTest() {
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
     * Test of subtract method, of class Currency.
     */
    @Test
    public void testSubtract() {
        Currency currency = new Currency("5.43");
        Currency instance = new Currency("6.54");
        Currency expResult = new Currency("1.11");
        Currency result = instance.subtract(currency);
        assertEquals(expResult, result);
    }

   

    /**
     * Test of multiply method, of class Currency.
     */
    @Test
    public void testMultiply_Currency() {
        Currency currency = new Currency("7.65");
        Currency instance = new Currency("6.54");
        Currency expResult = new Currency("50.031");
        Currency result = instance.multiply(currency);
        assertEquals(expResult, result);
    }

    /**
     * Test of add method, of class Currency.
     */
    @Test
    public void testAdd() {
        Currency currency = new Currency("13.49");
        Currency instance = new Currency("83.23");
        Currency expResult = new Currency("96.72");
        Currency result = instance.add(currency);
        assertEquals(expResult, result);
    }


    /**
     * Test of divide method, of class Currency.
     */
    @Test
    public void testDivide_Currency() {
        Currency currency = new Currency("2.2");
        Currency instance = new Currency("13.2");
        Currency expResult = new Currency("6");
        Currency result = instance.divide(currency);
        assertEquals(expResult, result);
    }

    /**
     * Test of ne method, of class Currency.
     */
    @Test
    public void testNe_Currency_ne() {
        Currency currency = new Currency("55");
        Currency instance = new Currency("55.00");
        boolean expResult = false;
        boolean result = instance.ne(currency);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testNe_Currency_eq() {
        Currency currency = new Currency("55");
        Currency instance = new Currency("55.01");
        boolean expResult = true;
        boolean result = instance.ne(currency);
        assertEquals(expResult, result);
    }

    /**
     * Test of round method, of class Currency.
     */
    @Test
    public void testRound() {
        Currency instance = new Currency("4.5678");
        Currency expResult = new Currency("4.57");
        Currency result = instance.round();
        assertEquals(expResult, result);
    }

    /**
     * Test of twoDecFormat method, of class Currency.
     */
    @Test
    public void testTwoDecFormat() {
        Currency instance = new Currency("33431.245");
        String expResult = "33431.25";
        String result = instance.twoDecFormat();
        assertEquals(expResult, result);
    }

    /**
     * Test of format3 method, of class Currency.
     */
    @Test
    public void testFormat3() {
        Currency instance = new Currency("1234.5678");
        String expResult = "1,234.57";
        String result = instance.format3();
        assertEquals(expResult, result);
    }

    /**
     * Test of lt method, of class Currency.
     */
    @Test
    public void testLt() {
        Currency nLow = new Currency("88.32");
        Currency instance = new Currency("3.233");
        boolean expResult = true;
        boolean result = instance.lt(nLow);
        assertEquals(expResult, result);
    }

    /**
     * Test of clone method, of class Currency.
     */
    @Test
    public void testClone() {
        final String value = "123421423.4562342";
        Currency instance = new Currency(value);
        Currency expResult = new Currency(value);
        Currency result = instance.clone();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class Currency.
     */
    @Test
    public void testToString() {
        final String value = "2134.213421234";
        Currency instance = new Currency(value);
        String result = instance.toString();
        assertEquals(value, result);
    }

    /**
     * Test of equals method, of class Currency.
     */
    @Test
    public void testEquals_eq() {
        final String value = "88342.234322";
        final Currency obj = new Currency(value);
        Currency instance = new Currency(value);
        boolean expResult = true;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
    }

    @Test
    public void testEquals_ne() {
        final Currency obj = new Currency("88342.234322");
        Currency instance = new Currency("111112.879879");
        boolean expResult = false;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
    }

}
