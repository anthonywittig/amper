
package AMP2.BankStuff;

 

 

 

 


/**
 * Write a description of class BillB here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import javax.swing.*;
import java.awt.event.*; 

public class BillB extends JButton
{
    private Bill bill;
    private BHTPane bHTP;

    /**
     * Constructor for objects of class BillB
     */
    public BillB(BHTPane bHTP, Bill bill)
    {
        super();
        this.bHTP = bHTP;
        this.bill = bill;
        
        actionListener();
    }
    
    /**
     * our seter uper of our actionlistener.
     */
    private void actionListener() {
        addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            bHTP.bill(1, bill);
            }
        });   
    }
}
