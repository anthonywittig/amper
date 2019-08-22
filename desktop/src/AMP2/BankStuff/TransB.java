package AMP2.BankStuff;

 

 

 

 


/**
 * Write a description of class TransB here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import javax.swing.*;
import java.awt.event.*; 


public class TransB extends JButton
{
    private Transaction trans;
    private BHTPane bHTP;

    /**
     * Constructor for objects of class TransB
     */
    public TransB(BHTPane bHTP, Transaction trans)
    {
        super();
        this.bHTP = bHTP;
        this.trans = trans;
        
        actionListener();
    }
    
    /**
     * our seter uper of our actionlistener.
     */
    private void actionListener() {
        addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            bHTP.transaction(1, trans);
            }
        });   
    }
}
