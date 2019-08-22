/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.BankStuff.display;

import AMP2.BankStuff.BHTPane;
import AMP2.BankStuff.Check;
import AMP2.BankStuff.Currency;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

/**
 *
 * @author awittig
 */
public class ChecksP extends JScrollPane{
    
    final List<CheckB> checkBs = new ArrayList<CheckB>();
    
    public ChecksP(final BHTPane bHTP, final Iterable<Check> checks, final ActionListener closeAction){
        final int c = SwingConstants.CENTER;
        final JPanel checksP = new JPanel();
        checksP.setLayout(new BoxLayout(checksP, BoxLayout.PAGE_AXIS)); 
        
        setViewportView(checksP);
        
        
        JPanel pan = new JPanel();
        pan.setLayout(new GridLayout(1, 2));
        pan.setBackground(new Color(63000));
        checksP.add(pan);
        
        JLabel lab = new JLabel("Total:");
        pan.add(lab);
        
        JLabel totalL = new JLabel();
        pan.add(totalL);
        
        pan = new JPanel();
        pan.setLayout(new GridLayout(1, 5));
        
        lab = new JLabel("Check #", c);
        pan.add(lab);
        lab = new JLabel("Date", c);
        pan.add(lab);
        lab = new JLabel("To", c);
        pan.add(lab);
        lab = new JLabel("Amount", c);
        pan.add(lab);
        lab = new JLabel("G.L.", c);
        pan.add(lab);
        lab = new JLabel("Posted", c);
        pan.add(lab);
        lab = new JLabel("  Likely Post  ", c);
        pan.add(lab);
        lab = new JLabel("For", c);
        pan.add(lab);
        checksP.add(pan);
        
        int color = 0;
        Currency checkTotal = Currency.Zero;
        
        for(Check check : checks){
            checkTotal = checkTotal.add(check.getAmount());
            
            Color colorValue;
            if(color == 2) {
                colorValue = Color.YELLOW;
                color = 0;
            }
            else {
                color++;  
                colorValue = Color.WHITE;
            }
            
            CheckB panB = new CheckB(bHTP, check, colorValue);
            checkBs.add(panB);
            checksP.add(panB);
        }
        totalL.setText(checkTotal.twoDecFormat());
        
        JButton done = new JButton("Done");
        done.addActionListener(closeAction);
        
        checksP.add(done);
    }
    
    public List<CheckB> getCheckBs(){
        return checkBs;
    }
}
