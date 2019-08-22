package AMP2.Days;


//import AMP2.MainDisplay.*;
import AMP2.BankStuff.Currency;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ControllerG implements Serializable
{
    private List<DayG> dayGs;
    private List<WeekG> weekGs;
    Currency high = Currency.NegativeOne, low = Currency.NegativeOne, adverage = Currency.NegativeOne, total = Currency.NegativeOne;
    DayG highD, lowD;
    private final Map<Integer, Currency> monthTotals = new HashMap<Integer, Currency>(12);
    
    /**
     * Constructor for objects of class ControllerG
     */
    public ControllerG() {
        dayGs = new ArrayList();
        figure();
    }
    
    /**
     * Constructor for objects of class ControllerG
     */
    public ControllerG(ArrayList dayGs) {
        this.dayGs = dayGs;
        figure();
    }
    
    /**
     * A method for testing...
     */
    public ControllerG(int test) {
               
        Calendar cal = Calendar.getInstance();
        cal.set(2005, 0, 1);
        ArrayList testArray = new ArrayList();
        
        for(int year = 2005; year < 2007; year++) {
            cal.set(Calendar.YEAR, year);
            for(int i = 1; i < 366; i++) {
                 DayG day = new DayG(cal, new Currency(i));
                 testArray.add(day);
                 cal.roll(Calendar.DAY_OF_YEAR, 1);
            }
        }       
        ControllerG cont = new ControllerG(testArray);  
        //for(int i = 0; i < 4; i++) {
          //  MiscStuff.writeToLog(cont.getFigure(i)); 
        //}
    }
    
    /**
     * A method that adds a dayG to our array of dayGs.
     * 
     * @param dayG, a dayG object;
     */
    public void addDayG(DayG dayG) {
        
        for(int i = 0; i < dayGs.size(); i++) {
            DayG oldDay = (DayG) dayGs.get(i);
            
            if(dayG.getYear() < oldDay.getYear()){
                dayGs.add(i, dayG);
                figure();
                return;
            }
            if(dayG.getMonth() < oldDay.getMonth()) {
                dayGs.add(i, dayG);
                figure();
                return;
            }
            if(dayG.getMonth() == oldDay.getMonth()) {
                if(dayG.getDay() < oldDay.getDay()) {
                    dayGs.add(i, dayG);  
                    figure();
                    return;
                }
                if(dayG.getDay() == oldDay.getDay()) {
                    dayGs.remove(oldDay);
                    dayGs.add(i, dayG);
                    figure();
                    return;
                }
            }
        }
        dayGs.add(dayG);
        
        figure();
    }
    
    /**
     * A method that adds an arrayList of dayGs.
     * 
     * @param dayGs, an arrayList of dayGs.
     */
    public void addDayGs(ArrayList newDayGs) {
        
        for(int i = 0; i < newDayGs.size(); i++) {
            DayG day = (DayG) newDayGs.get(i);
            addDayG(day);
        }
    }
    
    /**
     * A method that figures...
     */
    private void figure() {
        weekGs = new ArrayList<WeekG>();
        getMonthTotals().clear();
        
        //total up months:
        for(int month = 0; month < 12; ++month){
            monthTotals.put(month, Currency.Zero);
        }
        for(final DayG dayG : dayGs){
            //we assume all days to be from the same year
            final Integer month = dayG.getMonth();
            getMonthTotals().put(month, getMonthTotals().get(month).add(dayG.getGross()));
        }
        
        for(int i = 0; i < dayGs.size(); i++) {
            DayG day = (DayG) dayGs.get(i);
            WeekG week = new WeekG(day.getCalendar(), dayGs);
            weekGs.add(week);
            //MiscStuff.writeToLog("          figure");
        }
        
        Currency nAdv = Currency.Zero;
        Currency nLow = Currency.NegativeOne;
        Currency nHigh = Currency.Zero;
        Currency nTotal = Currency.Zero;
        
        for(int i = 0; i < dayGs.size(); i++) {
            DayG dayG = (DayG) dayGs.get(i);
            Currency gross = dayG.getGross();
            
            nAdv = nAdv.add(gross);
            nTotal = nTotal.add(gross);
            if(nLow == Currency.NegativeOne || gross.lt(nLow)) {
                lowD = dayG;
                nLow = gross;
            }
            if(nHigh.lt(gross)) {
                highD = dayG;
                nHigh = gross;
            }
        }
        
        if(dayGs.isEmpty()){
            adverage = Currency.Zero;
        }else{
            adverage =  nAdv.divide(dayGs.size()).round();
        }
        
        low = nLow;
        high = nHigh;
        total = nTotal;
    }
    
    
    /**
     * A method that gets our dayGs arrayList.
     * 
     * @return dayGs, our dayGs array...
     */
    public List<DayG> getDayGs() {
        return dayGs;   
    }
    
    /**
     * A method that gets our Average, Low, or High.
     * 
     * @param index, 1 for low, 2 for high.
     */
    public DayG getFigureDayG(int index) {
        figure();
        
        switch(index) { 
            case 1: return lowD;
            case 2: return highD;
            default: return null;
        }
    }
    
    /**
     * A method that gets our Average, Low, or High DayG.
     * 
     * @param index, 0 for average, 1 for low, 2 for high, 3 for total.
     */
    public Currency getFigure(int index) {
        figure();
        
        switch(index) {
            case 0: return adverage; 
            case 1: return low;
            case 2: return high;
            case 3: return total;
            default: throw new RuntimeException("Index out of bounds, expected 0 to 3 but was: " + index);
        }
    }
    
    /**
     * A method that gets our weedGs arrayList.
     * 
     * @return weekGs, our weekGs array...
     */
    public List<WeekG> getWeekGs() {
        return weekGs;   
    }

    /**
     * @return the monthTotals
     */
    public Map<Integer, Currency> getMonthTotals() {
        return monthTotals;
    }

}
