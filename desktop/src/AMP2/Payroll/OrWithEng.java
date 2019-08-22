package AMP2.Payroll;

import AMP2.BankStuff.Currency;
import AMP2.DatabaseStuff.DatabaseHelper;
import AMP2.DatabaseStuff.db.DataException;

/**
 * Write a description of class FicaEng here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class OrWithEng
{
    
    public static final int TaxType = 2;
    
    
    
    public static boolean orWithEnabled(/*int companyId*/){
        //this will eventually need to read from db if
        //we want to use withholding
        return false;
    }

    /**
     * A method for figuring out the OrWith tax for single persons.
     * 
     * @param claim, the dependants claimed.
     * @param grossPay, the amount of gross Pay.
     * 
     * @return the amount owed, 99999 if not found, and 0 if not enabled.
     */
    public static Currency getOrWith(int claim, Currency grossPay, int companyID) throws DataException {
        
        if(orWithEnabled(/*companyID*/)){
            return DatabaseHelper.getTaxFromDB(TaxType, companyID, claim, grossPay);
        }else{
            return Currency.Zero;
        }
        
//        if(claim == -1) {
//            return 0;   
//        }
//        if(claim == 0) {
//            return sClaim0(grossPay);
//        }
//        if(claim == 1) {
//            return sClaim1(grossPay);   
//        }
//        if(claim == 2) {
//            return sClaim2(grossPay);
//        }
//        
//        
//        
//        if(claim == 5) {
//            return sClaim5(grossPay);
//        }
//        if(claim == 6) {
//            return sClaim6(grossPay);
//        }
//        
//        
//        if(claim == 10) {
//            return mClaim0(grossPay);   
//        }
//        if(claim == 11) {
//            return mClaim1(grossPay);   
//        }
//        if(claim == 12) {
//            return mClaim2(grossPay);   
//        }
//        else{
//            return 9999;
//        }
    }
        
    /**
     * A method to return the OrWith for maried claiming 0 with 700 or less 
     * gross pay.
     * 
     * @param grossPay, the gross Pay.
     * @return the amount owed, 9999 if out of our range.
     */
    private static double mClaim0(double grossPay) {
        
        if(grossPay < 120) 
        return 0;
        if(grossPay < 140)
        return 1;
        if(grossPay < 160)
        return 3;
        if(grossPay < 180)
        return 4;
        if(grossPay < 200) 
        return 5;
        if(grossPay < 220)
        return 7;
        if(grossPay < 240)
        return 8;
        if(grossPay < 260)
        return 9;
        if(grossPay < 280)
        return 10;
        if(grossPay < 300) 
        return 12;
        if(grossPay < 320)
        return 13;
        if(grossPay < 340)
        return 14;
        if(grossPay < 360)
        return 15;
        if(grossPay < 380)
        return 17;
        if(grossPay < 400) 
        return 18;
        if(grossPay < 420)
        return 19;
        if(grossPay < 440)
        return 21;
        if(grossPay < 460)
        return 22;
        if(grossPay < 480)
        return 23;
        if(grossPay < 500) 
        return 24;
        if(grossPay < 520)
        return 25;
        if(grossPay < 540)
        return 27;
        if(grossPay < 560)
        return 28;
        if(grossPay < 580)
        return 29;
        if(grossPay < 600) 
        return 30;
        if(grossPay < 620)
        return 31;
        if(grossPay < 640)
        return 32;
        if(grossPay < 660)
        return 34;
        if(grossPay < 680)
        return 35;
        if(grossPay < 700) 
        return 36;
        
        else
        return 9999;              
    }
    
    /**
     * A method to return the OrWith for maried claiming 1 with 700 or less 
     * gross pay.
     * 
     * @param grossPay, the gross Pay.
     * @return the amount owed, 9999 if out of our range.
     */
    private static double mClaim1(double grossPay) {
        
        if(grossPay < 200) 
        return 0;
        if(grossPay < 220)
        return 1;
        if(grossPay < 240)
        return 2;
        if(grossPay < 260)
        return 3;
        if(grossPay < 280)
        return 5;
        if(grossPay < 300) 
        return 6;
        if(grossPay < 320)
        return 8;
        if(grossPay < 340)
        return 9;
        if(grossPay < 360)
        return 10;
        if(grossPay < 380)
        return 12;
        if(grossPay < 400) 
        return 13;
        if(grossPay < 420)
        return 14;
        if(grossPay < 440)
        return 15;
        if(grossPay < 460)
        return 17;
        if(grossPay < 480)
        return 18;
        if(grossPay < 500) 
        return 19;
        if(grossPay < 520)
        return 20;
        if(grossPay < 540)
        return 22;
        if(grossPay < 560)
        return 23;
        if(grossPay < 580)
        return 24;
        if(grossPay < 600) 
        return 25;
        if(grossPay < 620)
        return 27;
        if(grossPay < 640)
        return 28;
        if(grossPay < 660)
        return 29;
        if(grossPay < 680)
        return 30;
        if(grossPay < 700) 
        return 31;
        
        else
        return 9999;       
    }
        
    /**
     * A method to return the OrWith for maried claiming 2 with 700 or less 
     * gross pay.
     * 
     * @param grossPay, the gross Pay.
     * @return the amount owed, 9999 if out of our range.
     */
    private static double mClaim2(double grossPay) {
        
        if(grossPay < 300) 
        return 0;
        if(grossPay < 320)
        return 2;
        if(grossPay < 340)
        return 3;
        if(grossPay < 360)
        return 4;
        if(grossPay < 380)
        return 6;
        if(grossPay < 400) 
        return 7;
        if(grossPay < 420)
        return 9;
        if(grossPay < 440)
        return 10;
        if(grossPay < 460)
        return 11;
        if(grossPay < 480)
        return 13;
        if(grossPay < 500) 
        return 14;
        if(grossPay < 520)
        return 15;
        if(grossPay < 540)
        return 16;
        if(grossPay < 560)
        return 18;
        if(grossPay < 580)
        return 19;
        if(grossPay < 600) 
        return 20;
        if(grossPay < 620)
        return 21;
        if(grossPay < 640)
        return 23;
        if(grossPay < 660)
        return 24;
        if(grossPay < 680)
        return 25;
        if(grossPay < 700) 
        return 26;
        
        else
        return 9999;       
    }
    
    /**
     * A method to return the OrWith for singles claiming 0 with 700 or less gross
     * pay.
     * 
     * @param grossPay, the gross Pay.
     * @return the amount owed, 9999 if out of our range.
     */
    private static double sClaim0(double grossPay) {
        
        if(grossPay < 20)
        return 0;
        if(grossPay < 40)
        return 1;
        if(grossPay < 60)
        return 3;
        if(grossPay < 80)
        return 4;
        if(grossPay < 100) 
        return 5;
        if(grossPay < 120)
        return 7;
        if(grossPay < 140)
        return 8;
        if(grossPay < 160)
        return 9;
        if(grossPay < 180)
        return 10;
        if(grossPay < 200) 
        return 12;
        if(grossPay < 220)
        return 13;
        if(grossPay < 240)
        return 14;
        if(grossPay < 260)
        return 15;
        if(grossPay < 280)
        return 17;
        if(grossPay < 300) 
        return 18;
        if(grossPay < 320)
        return 19;
        if(grossPay < 340)
        return 20;
        if(grossPay < 360)
        return 22;
        if(grossPay < 380)
        return 23;
        if(grossPay < 400) 
        return 24;
        if(grossPay < 420)
        return 26;
        if(grossPay < 440)
        return 27;
        if(grossPay < 460)
        return 29;
        if(grossPay < 480)
        return 30;
        if(grossPay < 500) 
        return 32;
        if(grossPay < 520)
        return 34;
        if(grossPay < 540)
        return 35;
        if(grossPay < 560)
        return 37;
        if(grossPay < 580)
        return 38;
        if(grossPay < 600) 
        return 40;
        if(grossPay < 620)
        return 41;
        if(grossPay < 640)
        return 43;
        if(grossPay < 660)
        return 44;
        if(grossPay < 680)
        return 46;
        if(grossPay < 700) 
        return 47;
        
        else
        return 9999;        
    }
    
    /**
     * A method to return the OrWith for singles claiming 1 with 700 or less gross
     * pay.
     * 
     * @param grossPay, the gross Pay.
     * @return the amount owed, 9999 if out of our range.
     */
    private static double sClaim1(double grossPay) {
        
        if(grossPay < 100) 
        return 0;
        if(grossPay < 120)
        return 1;
        if(grossPay < 140)
        return 2;
        if(grossPay < 160)
        return 3;
        if(grossPay < 180)
        return 5;
        if(grossPay < 200) 
        return 6;
        if(grossPay < 220)
        return 8;
        if(grossPay < 240)
        return 9;
        if(grossPay < 260)
        return 10;
        if(grossPay < 280)
        return 11;
        if(grossPay < 300) 
        return 13;
        if(grossPay < 320)
        return 14;
        if(grossPay < 340)
        return 15;
        if(grossPay < 360)
        return 16;
        if(grossPay < 380)
        return 18;
        if(grossPay < 400) 
        return 20;
        if(grossPay < 420)
        return 21;
        if(grossPay < 440)
        return 23;
        if(grossPay < 460)
        return 25;
        if(grossPay < 480)
        return 26;
        if(grossPay < 500) 
        return 28;
        if(grossPay < 520)
        return 29;
        if(grossPay < 540)
        return 31;
        if(grossPay < 560)
        return 32;
        if(grossPay < 580)
        return 34;
        if(grossPay < 600) 
        return 35;
        if(grossPay < 620)
        return 37;
        if(grossPay < 640)
        return 38;
        if(grossPay < 660)
        return 40;
        if(grossPay < 680)
        return 41;
        if(grossPay < 700) 
        return 43;
        
        else
        return 9999;        
    }
    
    /**
     * A method to return the OrWith for singles claiming 2 with 700 or less gross
     * pay.
     * 
     * @param grossPay, the gross Pay.
     * @return the amount owed, 9999 if out of our range.
     */
    private static double sClaim2(double grossPay) {
        
        if(grossPay < 200) 
        return 0;
        if(grossPay < 220)
        return 2;
        if(grossPay < 240)
        return 3;
        if(grossPay < 260)
        return 4;
        if(grossPay < 280)
        return 6;
        if(grossPay < 300) 
        return 7;
        if(grossPay < 320)
        return 9;
        if(grossPay < 340)
        return 10;
        if(grossPay < 360)
        return 11;
        if(grossPay < 380)
        return 13;
        if(grossPay < 400) 
        return 15;
        if(grossPay < 420)
        return 16;
        if(grossPay < 440)
        return 18;
        if(grossPay < 460)
        return 20;
        if(grossPay < 480)
        return 21;
        if(grossPay < 500) 
        return 23;
        if(grossPay < 520)
        return 24;
        if(grossPay < 540)
        return 26;
        if(grossPay < 560)
        return 28;
        if(grossPay < 580)
        return 29;
        if(grossPay < 600) 
        return 31;
        if(grossPay < 620)
        return 32;
        if(grossPay < 640)
        return 34;
        if(grossPay < 660)
        return 35;
        if(grossPay < 680)
        return 37;
        if(grossPay < 700) 
        return 38;
        
        else
        return 9999;        
    }
    
    /**
     * A method to return the OrWith for single claiming 5 with 700 or less 
     * gross pay. 2006
     * 
     * @param grossPay, the gross Pay.
     * @return the amount owed, 9999 if out of our range.
     */
    private static double sClaim5(double grossPay) {
        
        if(grossPay < 560)
        return 0;
        if(grossPay < 580)
        return 1;
        if(grossPay < 600) 
        return 3;
        if(grossPay < 620)
        return 4;
        if(grossPay < 640)
        return 6;
        if(grossPay < 660)
        return 7;
        if(grossPay < 680)
        return 8;
        if(grossPay < 700) 
        return 10;
        
        else
        return 9999;       
    }
    /**
     * A method to return the OrWith for single claiming 6 with 700 or less 
     * gross pay. 2006
     * 
     * @param grossPay, the gross Pay.
     * @return the amount owed, 9999 if out of our range.
     */
    private static double sClaim6(double grossPay) {
        
        if(grossPay < 640)
        return 0;
        if(grossPay < 660)
        return 1;
        if(grossPay < 680)
        return 2;
        if(grossPay < 700) 
        return 4;
        
        else
        return 9999;       
    }
    
}
