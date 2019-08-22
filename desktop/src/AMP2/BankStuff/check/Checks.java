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

/**
 *
 * @author awittig
 */
public interface Checks extends Iterable<Check>{

    public void add(Check check);
    public void addAll(Collection<Check> checks);
    public void addAll(Checks checks);
    public void remove(Check check);
    public Iterable<Check> getData();
    public Checks getUnpostedChecks();
    public Checks getGlChecks(GlCode gL);
    public Checks getDateChecks(Calendar sDate, Calendar eDate);
    public Checks getTextChecks(String text);
    public int getNextNumber();
    public int size();
    public boolean isEmpty();
}
