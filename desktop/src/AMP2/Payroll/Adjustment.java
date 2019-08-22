/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.Payroll;

import AMP2.BankStuff.Currency;

/**
 *
 * @author awittig
 */
public final class Adjustment {
    
    private final Currency amount;
    private final String note;

    public Adjustment(final Currency amount, final String note) {
        this.amount = amount;
        this.note = note;
    }
    
     public Currency getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }
}
