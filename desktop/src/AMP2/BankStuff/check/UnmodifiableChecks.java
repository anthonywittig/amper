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
import java.util.Iterator;

/**
 *
 * @author awittig
 */
public class UnmodifiableChecks implements Checks{

    private final Checks checks;
    
    public UnmodifiableChecks(Checks checks) {
        this.checks = checks;
    }

    @Override
    public void add(Check check) {
        throwException();
    }
    
    @Override
    public void addAll(Collection<Check> checks) {
        throwException();
    }
    
    @Override
    public void addAll(Checks checks) {
        throwException();
    }
    
    @Override
    public void remove(Check check) {
        throwException();
    }
    
    private void throwException(){
        throw new UnsupportedOperationException("This is an unaddable checks instance");
    }

    @Override
    public Iterable<Check> getData() {
        return checks.getData();
    }

    @Override
    public Checks getUnpostedChecks() {
        return new UnmodifiableChecks(checks.getUnpostedChecks());
    }

    @Override
    public Checks getGlChecks(GlCode gL) {
        return new UnmodifiableChecks(checks.getGlChecks(gL));
    }

    @Override
    public int getNextNumber() {
        return checks.getNextNumber();
    }

    @Override
    public Iterator<Check> iterator() {
        return checks.iterator();
    }

    @Override
    public Checks getDateChecks(Calendar sDate, Calendar eDate) {
        return new UnmodifiableChecks(checks.getDateChecks(sDate, eDate));
    }

    @Override
    public int size() {
        return checks.size();
    }

    @Override
    public Checks getTextChecks(String text) {
        return new UnmodifiableChecks(checks.getTextChecks(text));
    }

    @Override
    public boolean isEmpty() {
        return checks.isEmpty();
    }
}
