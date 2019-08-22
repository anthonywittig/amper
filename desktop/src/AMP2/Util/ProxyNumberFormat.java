/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.Util;

import AMP2.BankStuff.Currency;
import java.text.NumberFormat;

/**
 *This class is just to save some refactoring
 */
public class ProxyNumberFormat {
    
    private NumberFormat nf = NumberFormat.getNumberInstance();
    
    public String format(double d){
        return nf.format(d);
    }
    
    public String format(Currency c){
        return c.format3();
    }
    
}
