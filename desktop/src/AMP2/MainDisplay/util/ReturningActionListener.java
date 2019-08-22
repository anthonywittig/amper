/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.MainDisplay.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author awittig
 */
public abstract class ReturningActionListener implements ActionListener{

    public abstract Object actionPerformedAndReturn(ActionEvent e);
    
    @Override
    public void actionPerformed(ActionEvent e) {
        actionPerformedAndReturn(e);
    }
    
}
