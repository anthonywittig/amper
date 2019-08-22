package AMP2.Days;

import AMP2.BankStuff.Currency;
import java.util.ArrayList;
import java.util.List;



public class SearchG
{
    /**
     * Constructor for objects of class SearchG
     */
    public SearchG()
    {
        
    }
    
    /**
     * A method that returns some days by the sort operation.
     * 
     * @param days, the days to sort.
     * @param operation, 0 for <=, 1 for >=, 
     * @param amount, the amount to be tested against.
     * 
     * @return daysS, our sorted days.
     */
    public static List<DayG> sortD(List<DayG> days, int operation, Currency amount) {
        List<DayG> daysS = new ArrayList<DayG>();
        
        for(int i = 0; i < days.size(); i++) {
            DayG day = (DayG) days.get(i);
            Currency gross = day.getGross();
            
            switch(operation) {
                case 0: 
                    if(gross.lte(amount)) {
                        daysS.add(day);    
                    }
                    break;
                case 1:
                    if(/*gross >= amount*/ amount.lte(gross)) {
                        daysS.add(day);   
                    }
                    break;
            }
        }
        return daysS;
    }
}
