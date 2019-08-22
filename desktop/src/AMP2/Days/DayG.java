package AMP2.Days;

import AMP2.BankStuff.Currency;
import java.io.Serializable;
import java.util.Calendar;


/**
 * Write a description of class DayG here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */


public class DayG implements Serializable
{
    private Currency gross;
    private Calendar calendar;
    private String dayOfWeekS, monthS, note;
    
    /**
     * A constructor for class DayG.
     */
    public DayG(Calendar date) {
        this(date, Currency.Zero);   
    }
    
    /**
     * Constructor for objects of class DayG
     */
    public DayG(Calendar date, Currency gross)
    {
        calendar = Calendar.getInstance();
        calendar.set((date.get(Calendar.YEAR)),(date.get(Calendar.MONTH)),
            (date.get(Calendar.DAY_OF_MONTH)));
        
        this.gross = gross;
        note = "";
        
        setUp();
    }
    
    /**
     * A method that gets our calendar.
     * 
     * @return calendar, our calendar...
     */
    public Calendar getCalendar() {
        return calendar;   
    }
    
    /**
     * A method that gets our day.
     * 
     * @return our day of the month.
     */
    public int getDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);   
    }
    
    /**
     * A method that gets our dayOfWeekS.
     * 
     * @return dayOfWeekS...
     */
    public String getDayOfWeekS() {
        return dayOfWeekS;   
    }
    
    /**
     * A method that gets our gross.
     * 
     * @return gross, our gross...
     */
    public Currency getGross() {
        return gross;   
    }
    
    /**
     * A method thats gets our month.
     * 
     * @return our month.
     */
    public int getMonth() {
        return calendar.get(Calendar.MONTH);
    }   
    
    /**
     * A method that gets our monthN.
     * 
     * @return monthN
     */
    public String getMonthS() {
        return monthS;   
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
     * A method that gets our year.
     * 
     * @return our year.
     */
    public int getYear() {
        return calendar.get(Calendar.YEAR);   
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
     * A method that does some set up.
     */
    private void setUp() {
        
        switch(calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1: dayOfWeekS = "Sun"; break; 
            case 2: dayOfWeekS = "Mon"; break;
            case 3: dayOfWeekS = "Tue"; break;
            case 4: dayOfWeekS = "Wed"; break;
            case 5: dayOfWeekS = "Thur"; break;
            case 6: dayOfWeekS = "Fri"; break;
            case 7: dayOfWeekS = "Sat"; break;
        }
        
        switch(getMonth()) {
            case 0: monthS = "Jan"; break;
            case 1: monthS = "Feb"; break;
            case 2: monthS = "Mar"; break;
            case 3: monthS = "Apr"; break;
            case 4: monthS = "May"; break;
            case 5: monthS = "June"; break;
            case 6: monthS = "July"; break;
            case 7: monthS = "Aug"; break;
            case 8: monthS = "Sept"; break;
            case 9: monthS = "Oct"; break;
            case 10: monthS = "Nov"; break;
            case 11: monthS = "Dec"; break;
            default: monthS = "Not a valad month"; break;
        }
    }
    
    /**
     * Our toString method.
     * 
     * @return a custom string representation...
     */
    public String toStringCal() {
        return "" + dayOfWeekS + " " + getDay() + "/" + getMonthS();  
    }
}
