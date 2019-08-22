/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package AMP2.BankStuff.check;

import AMP2.BankStuff.Check;
import AMP2.BankStuff.GlCode;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author awittig
 */
public class ChecksImpl implements Checks{
    
    private final TreeSet<Check> data = new TreeSet<Check>();

    public ChecksImpl(Collection<Check> checks) {
        data.addAll(checks);
    }

    public ChecksImpl() {}
    
    @Override
    public void add(Check check) {
        data.add(check);
    }
    
    @Override
    public void addAll(Collection<Check> checks) {
        data.addAll(checks);
    }
    
    @Override
    public void addAll(Checks checks) {
        for(Check check : checks){
            data.add(check);
        }
    }

    @Override
    public Iterable<Check> getData(){
        return Collections.unmodifiableCollection(data);
    }
    
    @Override
    public Checks getUnpostedChecks(){
        final LinkedList<Check> unpostChecks = new LinkedList<Check>();
        
        for(Check check : data){
            if(!check.getGoneThrough()) {
                unpostChecks.add(check);
            }
        }
                  
        return new ChecksImpl(unpostChecks);
    }
    
    @Override
    public Checks getGlChecks(GlCode gL) {
        LinkedList<Check> sorted = new LinkedList<Check>();
        
        for(Check check : data){
            if(check.getGlCode().equals(gL)) {
                sorted.add(check);
            }
        }
        return new ChecksImpl(sorted);   
    }

    @Override
    public int getNextNumber() {
        if(data.isEmpty()){
            return 0;
        } else {
            return data.first().getCheckNum() + 1;
        }
    }

    @Override
    public void remove(Check check) {
        data.remove(check);
    }

    @Override
    public Iterator<Check> iterator() {
        return Collections.unmodifiableCollection(data).iterator();
    }
    
    @Override
    public Checks getDateChecks(Calendar sDate, Calendar eDate) {
        LinkedList<Check> sorted = new LinkedList<Check>();
        
        for(Check check : data){
            Calendar date = check.getDate();
            
            if(sDate.before(date) && eDate.after(date)) {
                sorted.add(check);         
                //MiscStuff.writeToLog("sort1");
            }
            else {
                int sDay = sDate.get(Calendar.DAY_OF_YEAR);
                int sYear = sDate.get(Calendar.YEAR);
                int eDay = eDate.get(Calendar.DAY_OF_YEAR);
                int eYear = eDate.get(Calendar.YEAR);
                int day = date.get(Calendar.DAY_OF_YEAR);
                int year = date.get(Calendar.YEAR);
                
                if(sDay == day && sYear == year) {
                    sorted.add(check);   
                  //  MiscStuff.writeToLog("sort2");
                }
                else {
                    if(eDay == day && eYear == year) {
                        sorted.add(check);   
                    //    MiscStuff.writeToLog("sort3");
                    }
                }
            }
        }
        
        return new ChecksImpl(sorted);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Checks getTextChecks(String text) {
        
        LinkedList<Check> sorted = new LinkedList<Check>();
        
        for (Check ch : data) {
            String text2 = ch.getDateS();
            text += " " + ch.getPayTo();
            text += " " + ch.getDollarsS();
            text += " " + ch.getForS();
            text += " " + ch.getClearDate();
            text2 += " " + ch.getAmount() + " " + ch.getCheckNum();
            text = text.toLowerCase();
            text2 = text2.toLowerCase();
            if(text2.contains(text)) {
                sorted.add(ch);   
            }
        }
        return new ChecksImpl(sorted);
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }
}
