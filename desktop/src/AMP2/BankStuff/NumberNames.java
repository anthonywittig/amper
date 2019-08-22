
package AMP2.BankStuff;

/**
 * Write a description of class NumberNames here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class NumberNames
{
    /**
     * Constructor for objects of class NumberNames
     */
    public NumberNames() {
    }
    
    /**
     * A method that returns the name of a double, such as 5 is five, 
     * Handles numbers up to 9,999.99
     * 
     * @param num, the number.
     * 
     * @return name, the name of the number.
     */
    public static String numName(Currency num) {
        String numberS = "" + num;
        int dot = numberS.indexOf('.');
        if(dot == -1){
            numberS += ".00";
            dot = numberS.length() - 3;
        }
        
        String wholeS = numberS.substring(0, dot);
        String fractionS = "0";
        String name = "";
        if(dot != numberS.length()) {
            fractionS = numberS.substring(dot + 1);   
        }
        
        int wholeL = wholeS.length();
        
        if(wholeL == 0 || wholeS.equals("0")){
            name += "Zero ";
        }
        
        for(int i = 0; i < wholeL; i++) {
            int place = wholeL - i;
            int digit = new Integer("" + wholeS.charAt(i));
            String label;
            
            
            switch(place) {
                case 2: label = "2"; break;
                case 3: label = "Hundred";
                    if(digit == 0) {
                        label = "2"; 
                    }
                    break;
                case 4: label = "Thousand"; break;
//                case 5: label = "2"; break;
  //              case 6: label = "Hundred"; break;
                default: label = "";    
            }
            String nName;
            
            if(label.equals("2")) {
                int digit1 = new Integer("" + wholeS.substring(i, i+2));
                //MiscStuff.writeToLog(diget1);
                
                switch(digit) {
                    case 2: nName = "Twenty"; break;
                    case 3: nName = "Thirty"; break;
                    case 4: nName = "Fourty"; break;
                    case 5: nName = "Fifty"; break;
                    case 6: nName = "Sixty"; break;
                    case 7: nName = "Seventy"; break;
                    case 8: nName = "Eighty"; break;
                    case 9: nName = "Ninety"; break;
           
                    default: nName = "";   
                }
                
                switch(digit1) {
                    case 10: nName = "Ten"; i++; break;
                    case 11: nName = "Eleven"; i++; break;
                    case 12: nName = "Twelve"; i++; break;
                    case 13: nName = "Thirteen"; i++; break;
                    case 14: nName = "Fourteen"; i++; break;
                    case 15: nName = "Fifteen"; i++; break;
                    case 16: nName = "Sixteen"; i++; break;
                    case 17: nName = "Seventeen"; i++; break;
                    case 18: nName = "Eighteen"; i++; break;
                    case 19: nName = "Nineteen"; i++; break;
                    
                    //default: nName = "";   
                }
            }
            else {
                switch(digit) {
                    case 1: nName = "One"; break;
                    case 2: nName = "Two"; break;
                    case 3: nName = "Three"; break;
                    case 4: nName = "Four"; break;
                    case 5: nName = "Five"; break;
                    case 6: nName = "Six"; break;
                    case 7: nName = "Seven"; break;
                    case 8: nName = "Eight"; break;
                    case 9: nName = "Nine"; break;
                    
                    default: nName = "";   
                }      
            }
            
            if(label.equals("2")) {
                label = "";   
            }
            
            boolean added = false;
            if(nName.equals("") && label.equals("")) {
               //nothing 
               added = true;
            }
            if(label.equals("") && !added) {
                name += (nName + " ");
                added = true;
            }   
            if(!added) {
                name += (nName + " " + label + " "); 
            }
            //MiscStuff.writeToLog("" + (place) + "  " + wholeS.charAt(i));   
        }
        
        name += "And ";
        
        int fractionL = fractionS.length();
             
        //MiscStuff.writeToLog(fractionS);
        
        if(!fractionS.equals("0")) {
            for(int i = 0; i < fractionL; i++) {
                int place = fractionL - i;
                
                int digit = new Integer("" + fractionS.charAt(i));
                String nName = ".00";
                
                String label = "";
                
                switch(place) {
                    case 0: label = "2"; break;
                    default: label = ""; break;
                }
                
                if(fractionL == 1 || digit == 0) {
                    nName = "00";      
                }
                if(label.equals("2") || fractionL == 2) {
                   // int digit1 = new Integer("" + wholeS.substring(i, i+2));
                    //MiscStuff.writeToLog(diget1);
                    
                    nName = "" + digit;
                }
                else {
                    
                    nName = "" + (digit * 10);
                    
                }
               
               
               name += (nName + ""); 
                
            }
        }
        else {
            name += ("00");   
        }
        name +=("/100");
        
        return name;
    }
}
