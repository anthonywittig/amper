package AMP2.BankStuff;

 

 

 

 


/**
 * Write a description of class Math2 here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import java.text.*;

public class Math2
{
    

    /**
     * Constructor for objects of class Math2
     */
    public Math2()
    {
        
    }
    
    //a hack to save me some refactoring time
    public static String formatR(Currency c) {
        return c.twoDecFormat();
    }
        
    /**
     * A method that is supposed to round and format the number...
     * 
     * @param number, our number.
     * 
     * @return the new number.
     */
    public static String formatR(double number) {
        NumberFormat numberFormatter = NumberFormat.getNumberInstance();
        numberFormatter.setMinimumFractionDigits(2);
        return numberFormatter.format(Math2.round(number));
        //return Math2.round(number);   
    }

    
    //a hack to save some refactoring time
    public static Currency round(Currency c) {
        return c.round();
    }
    
    /**
     * A method that takes a double and cuts off everything from two decimal
     * places and increased it by .01
     * 
     * @param number, the number to have the above done to it.
     * @return number, the new number.
     */
    public static double round(double number) {
        //MiscStuff.writeToLog(number);
        Double d = new Double(number);
        String str = d.toString();
        str.trim();
      
        if(str.indexOf('.') + 3 < str.length()) {
            if(!str.equals("NaN") && !str.equals("Infinity") 
                && !str.equals("-Infinity")) {
                    
                    
                int i = str.indexOf('.');
                String str1 = str.substring(0, i + 3);
                d = new Double(str1);
                
                //MiscStuff.writeToLog("                  " + str + "        " + d);
                
                if(!str.substring(i+3).equals("") ) {
                    int rounder = new Integer(str.substring(i+3, i+4));
                    
                    if(5 <= rounder) {
                        Double d2 = (10 - rounder) * .001;// d + new Float(.01);
                        d = number + d2;
                      //MiscStuff.writeToLog("Rounder    " + rounder + "      " + d2);
          
                        String str2 = d.toString();
                        str2.trim();
                        
                        if(str2.indexOf('.') + 3 < str2.length()) 
                            d = Math2.round(d); //don't get stuck....
                    }
                }
                    
                number = d;  
            }
            
            else {
                //MiscStuff.writeToLog(number);   
            }
        }
        
        return number;
    }
}

