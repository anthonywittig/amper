package AMP2.Days;

import AMP2.Payroll.PayDates;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.util.List;

public class DayGPanel extends JPanel
{
    private JTextField day, month, year, gross, notes;
    private int monthNum = -1, dayNum = 1;
    private List<DayG> dayGs;
    /**
     * Constructor for objects of class DayGFrame
     */
    public DayGPanel()
    {
        this(new JPanel());
    }
    
    /**
     * constructor that takes a panel to add to our frame.
     */
    public DayGPanel(JPanel buttons) {
        this(buttons, "");
    }
    
    /**
     * constructor that takes a panel to add to our frame with the month...
     */
    public DayGPanel(JPanel buttons, String monthN) {        
        this(buttons, monthN, new ArrayList());
    }
    
    /**
     * constructor that takes a panel to add to our frame with the month...
     */
    public DayGPanel(JPanel buttons, String monthN, List<DayG> dayGs) {
        super();
        this.dayGs = dayGs;
        setUp(buttons, monthN);
    }
    
    /**
     * A method that adds an action listener to a given component.
     * 
     * @param index, 0 for day, 1 for month, 2 for year, 3 for gross and 4 for 
     * notes.
     * @param aC, the new action listener to be added.
     */
    public void addComponentActionListener(int index, ActionListener aC) {
        switch(index) {
            case 0: day.addActionListener(aC); break;
            case 1: month.addActionListener(aC); break;
            case 2: year.addActionListener(aC); break;
            case 3: gross.addActionListener(aC); break;
            case 4: notes.addActionListener(aC); break;
        }      
    }
    
    /**
     * A method that figures our date
     * 
     * @param monthN, a months name.
     */
    private void figureDate(String monthN) {
        for(int i = 1; i < 13; i++) {
            PayDates pD = new PayDates(i, 1, 0);
            
            if(pD.getMonthS().equals(monthN)) {
                monthNum = i - 1;   
            }
        }
        
        if(monthNum != -1) {
            for(int i = 0; i < dayGs.size(); i++) {
                DayG dayG = (DayG) dayGs.get(i);
                
                if(dayG.getMonth() == monthNum) {
                    if(dayG.getDay() >= dayNum) {
                        if(dayG.getDayOfWeekS().equals("Sat")) {
                            dayNum = dayG.getDay() + 2;   
                        }
                        else {
                            dayNum = dayG.getDay() + 1;   
                        }
                    }
                }
            }
        }
    }
    
    /**
     * A method that returns the content of our text fields.
     * 
     * @param index, 0 for day, 1 for month, 2 for year, 3 for gross and 4 for 
     * notes.
     * 
     * @return the content.
     */
    public String getContent(int index) {
        
        switch(index) {
            case 0: return day.getText();
            case 1: return "" + (new Integer(month.getText()) - 1);
            case 2: return year.getText();
            case 3: return gross.getText();
            case 4: return notes.getText();
        }
        return "Error";
    }
    
    /**
     * A method that sets the content of our text fields.
     * 
     * @param index, 0 for day, 1 for month, 2 for year, 3 for gross and 4 for 
     * notes.
     * @param text, the new text.
     */
    public void setContent(int index, String text) {
        
        switch(index) {
            case 0: day.setText(text); break;
            case 1: month.setText(text); break;
            case 2: year.setText(text); break;
            case 3: gross.setText(text); break;
            case 4: notes.setText(text); break;
        }
    }
    
    /**
     * A method that requests focus for a text field...
     * 
     * @param index, 0 for day, 1 for month, 2 for year, 3 for gross and 4 for 
     * notes.
     */
    public void setFocus(int index) {
        switch(index) {
            case 0: day.requestFocus(); break;
            case 1: month.requestFocus(); break;
            case 2: year.requestFocus(); break;
            case 3: gross.requestFocus(); break;
            case 4: notes.requestFocus(); break;
        }      
    }
    
    /**
     * A method that does our setting up.
     */
    private void setUp(JPanel buttons, String monthN) {
        figureDate(monthN);
        
        int c = SwingConstants.CENTER;
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        
        JPanel pan = new JPanel();
        pan.setLayout(new GridLayout(1, 5));
        
        JLabel lab = new JLabel("Day", c);
        pan.add(lab);
        lab = new JLabel("    Month    ", c);
        pan.add(lab);
        lab = new JLabel("Year", c);
        pan.add(lab);
        lab = new JLabel("Gross", c);
        pan.add(lab);
        lab = new JLabel("Notes", c);
        pan.add(lab);
        
        this.add(pan);
        
        pan = new JPanel();
        pan.setLayout(new GridLayout(1, 5));
        
        day = new JTextField("" + dayNum);
        pan.add(day);
        month = new JTextField("" + (monthNum + 1));
        pan.add(month);
        // not editing the year as of yet...
        Calendar now = Calendar.getInstance();
        lab = new JLabel("" + now.get(Calendar.YEAR), c);
        year = new JTextField(lab.getText()); 
        pan.add(lab);
        gross = new JTextField();
        pan.add(gross);
        notes = new JTextField();
        pan.add(notes);
        
        this.add(pan);
        
        this.add(buttons);
    }
}

