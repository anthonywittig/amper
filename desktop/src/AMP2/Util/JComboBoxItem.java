/*
 * JComboBoxItem.java
 *
 * Created on June 21, 2007, 3:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package AMP2.Util;

import AMP2.BankStuff.GlCode;

/**
 *
 * @author Anthony Wittig
 */
public class JComboBoxItem {
    
    private final GlCode glCode;
    
    /** Creates a new instance of JComboBoxItem */
    public JComboBoxItem(GlCode glCode) {
        this.glCode = glCode;
    }
    
    public GlCode getGlCode(){
        return glCode;
    }

    @Override
    public String toString()
    {
            return glCode.getDescription();
    }
    
}
