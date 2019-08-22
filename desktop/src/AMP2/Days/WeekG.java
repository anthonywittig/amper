package AMP2.Days;


import AMP2.BankStuff.Currency;
import AMP2.Util.MiscStuff;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeekG implements Serializable
{
    private ArrayList dayArray;
    private Calendar endDate;
    private DayG lowD, highD; 
    private int[] acceptedDatesRange;
    private Currency adverage = Currency.NegativeOne, low = Currency.NegativeOne, high = Currency.NegativeOne, total = Currency.NegativeOne;
    private String note;
    
    /**
     * Constructor for objects of class WeekG
     */
    public WeekG(Calendar date)
    {
        endDate = Calendar.getInstance();
        endDate.set((date.get(Calendar.YEAR)),(date.get(Calendar.MONTH)),
            (date.get(Calendar.DAY_OF_MONTH)));
        acceptedDatesRange = new int[2];
        dayArray = new ArrayList();
        note = "";
        
        setUp();
    }
    
    /**
     * Constructor for objects of class WeekG
     */
    public WeekG(Calendar date, List<DayG> dayGs)
    {
        endDate = Calendar.getInstance();
        endDate.set((date.get(Calendar.YEAR)),(date.get(Calendar.MONTH)),
            (date.get(Calendar.DAY_OF_MONTH)));
        acceptedDatesRange = new int[2];
        dayArray = new ArrayList();
        
        setUp();
        
        addDayGs(dayGs);
    }
    
    /**
     * A test constructor.
     */
    public WeekG(int test) {
        Calendar cal = Calendar.getInstance();
        cal.set(2005, 0, 1);
        ArrayList testArray = new ArrayList();
        
        for(int i = 1; i < 366; i++) {
             DayG day = new DayG(cal, new Currency(i));
             testArray.add(day);
             cal.roll(Calendar.DAY_OF_YEAR, 1);
        }
        
        cal.roll(Calendar.DAY_OF_YEAR, -test);
        WeekG week = new WeekG(cal);
        week.addDayGs(testArray);
        week.zTest();
    }
    
    /**
     * A method that checks if a dayG object is in our range.
     * 
     * @param day, a dayG object.
     * @return true if it is in range,
     *         false otherwise.
     */
    private boolean acceptDayG(DayG day) {
        if(day.getYear() != endDate.get(Calendar.YEAR))
            return false;
        
        int dayOfYear = day.getCalendar().get(Calendar.DAY_OF_YEAR);
        if(dayOfYear == acceptedDatesRange[0] || dayOfYear == 
            acceptedDatesRange[1] || (dayOfYear < acceptedDatesRange[1] &&
            dayOfYear > acceptedDatesRange[0])) {
            
            //MiscStuff.writeToLog("" + dayOfYear + "  " + acceptedDatesRange[0] +
              //  "  " + acceptedDatesRange[1]); //print year.....................................................................................
            return true;
        }
        return false;
    }
    
    /**
     * A method that adds dayG objects by iterating over an arraylist.
     * 
     * @param dayGs, an arraylist with some dayG objects in it.
     */
    public void addDayGs(List<DayG> dayGs) {
        
        for(int i = 0; i < dayGs.size(); i++) {
            if(dayGs.get(i) instanceof DayG) {
                DayG day = (DayG) dayGs.get(i);
                
                if(acceptDayG(day)) {
                    dayArray.add(day);   
                }
            }
        }
    }
    
    /**
     * A method that adds dayG object.
     * 
     * @param dayG, a dayG object.
     */
    public void addDayG(DayG dayG) {
        
        if(acceptDayG(dayG)) {
            dayArray.add(dayG);   
        }   
    }
    
    /**
     * A method that does some figuring.
     */
    public void figure() {
        Currency nAdv = Currency.Zero;
        Currency nLow = Currency.NegativeOne;
        Currency nHigh = Currency.Zero;
        Currency nTotal = Currency.Zero;
        
        for(int i = 0; i < dayArray.size(); i++) {
            DayG dayG = (DayG) dayArray.get(i);
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
        
        adverage = nAdv.divide(dayArray.size()).round();
        low = nLow.round();
        high = nHigh.round();
        
        //no need to round total, right?
        total = nTotal;
    }
    
    /**
     * A method that gets our dayArray.
     * 
     * @return our dayArray...
     */
    public ArrayList getDayArray() {
        return dayArray;   
    }
    
    /**
     * A method that gets our Adverage, Low, or High.
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
     * A method that gets our Adverage, Low, or High DayG.
     * 
     * @param index, 0 for adverage, 1 for low, 2 for high, 3 for total.
     */
    public Currency getFigure(int index) {
        figure();
        
        switch(index) {
            case 0: return adverage; 
            case 1: return low;
            case 2: return high;
            case 3: return total;
            default: throw new RuntimeException("index out of bounds, should be between 0 and 3, was " + index);
        }
    }
    
    /**
     * A method that gets our note.
     * 
     * @return note, our note...
     */
    public String getNote() {
        return note;   
    }
    
    /**
     * A method that sets our note string.
     * 
     * @param note, our new note.
     */
    public void setNote(String note) {
        this.note = note;   
    }
    
    /**
     * A method that sets up our object.
     */
    private void setUp() {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set((endDate.get(Calendar.YEAR)), 
            (endDate.get(Calendar.MONTH)), (endDate.get(Calendar.DAY_OF_MONTH)));
        calendar1.roll(Calendar.DAY_OF_YEAR, -6);
        
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set((endDate.get(Calendar.YEAR)), 
            (endDate.get(Calendar.MONTH)), (endDate.get(Calendar.DAY_OF_MONTH)));
        
        acceptedDatesRange[0] = calendar1.get(Calendar.DAY_OF_YEAR);
        acceptedDatesRange[1] = calendar2.get(Calendar.DAY_OF_YEAR); 
        
        if(acceptedDatesRange[1] < 7) {
            acceptedDatesRange[0] = 1;     
        }
        
        //MiscStuff.writeToLog("Ranges set    " + calendar1.get
        //  (Calendar.DAY_OF_YEAR) + "  " + calendar2.get(Calendar.DAY_OF_YEAR));
    }
    
    /**
     * A method for testing...
     */
    public void zTest() {
        for(int i = 0; i < dayArray.size(); i++) {
            MiscStuff.writeToLog(dayArray.get(i) + "zTest");   
        }
    }
}
