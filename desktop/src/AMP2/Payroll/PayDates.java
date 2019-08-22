package AMP2.Payroll;
/**
 * Write a description of class PayDates here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import java.io.*;
import java.util.Calendar;

public class PayDates implements Serializable
{
    private final int year, month, section;
    private String monthN, sectionS;
    private int lastDayOfSection;
    
    public PayDates(){
        Calendar calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        section = dayOfMonth < 16 ? 1 : 2;
        year = calendar.get(Calendar.YEAR);
        
        createStrings();
    }
            
    public PayDates(int month, int section, int year)
    {
        this.month = month;
        this.section = section;
        
        if(year < 100){
            year += 2000;
        }
        this.year = year;
        
        createStrings();
    }
    
    
    /**
     * This method makes our strings.
     */
    private void createStrings() {
        
        switch(month) {
            case 1: monthN = "Jan"; break;
            case 2: monthN = "Feb"; break;
            case 3: monthN = "Mar"; break;
            case 4: monthN = "Apr"; break;
            case 5: monthN = "May"; break;
            case 6: monthN = "June"; break;
            case 7: monthN = "July"; break;
            case 8: monthN = "Aug"; break;
            case 9: monthN = "Sept"; break;
            case 10: monthN = "Oct"; break;
            case 11: monthN = "Nov"; break;
            case 12: monthN = "Dec"; break;
            default: monthN = "" + month; break;
        }
        
        switch(section) {
            case 1: 
                sectionS = "1-15"; 
                lastDayOfSection = 15;
                break;
            case 2: if(month == 1 || month == 3 || month == 5 || month == 7
                        || month == 8 || month == 10 || month == 12) {  
                        sectionS = "16-31"; 
                        lastDayOfSection = 31;
                        break;
                    }
                    
                    if(month == 4 || month == 6 || month == 9 || month == 11) {
                        sectionS = "16-30";
                        lastDayOfSection = 30; 
                        break;   
                    }
                    
                    if(month == 2){
                        sectionS = "16-29"; 
                        lastDayOfSection = 29; 
                        break;
                    }
        }
    }
    
    /**
     * A method that gets our month
     * 
     * @return month, our month.
     */
    public int getMonthI() {
        return month;   
    }
    
    /**
     * A method that gets our month
     * 
     * @return month, our month.
     */
    public String getMonthS() {
        return monthN;   
    }
    
    /**
     * A method that gets the next month/section combo after this one.
     * 
     * @return next, the next PayDates object.
     */
    public PayDates getNextPD() {
        int nSection = section + 1;
        int nMonth = month;
        Integer nYear = new Integer(year);
        
        if(nSection == 3) {
            nSection = 1;     
            nMonth = nMonth + 1;
            
            if(nMonth == 13) { 
                nMonth = 1;
                nYear += 1;
            }
        }
        //String nYearS = nYear.toString();
            
        return new PayDates(nMonth, nSection, nYear);
    }
    
    /**
     * A method that gets our section.
     * 
     * @return section, our section.
     */
    public int getSectionI() {
        return section;   
    }
    
    /**
     * A method that gets our section.
     * 
     * @return section, our section.
     */
    public String getSectionS() {
        return sectionS;   
    }
    
    /**
     * A method that gets our year.
     * 
     * @return year, our year.
     */
    public int getYear() {
        return year;   
    }
    
    /**
     * Our toString.
     * 
     * @return a string representation of our date.
     */
    public String toString() {
        //return monthN + " " + sectionS + " " + year;
        return "" + month + "/" + lastDayOfSection + "/" + (year % 2000);
    }

    public boolean after(PayDates other) {
        if(other.year < year){
            return true;
        } else if(other.year == year){
            if(other.month < month){
                return true;
            }else if(other.month == month){
                return other.section < section;
            }
        }
        
        return false;
    }
    
    
}

