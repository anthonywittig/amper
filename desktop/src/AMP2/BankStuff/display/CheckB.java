package AMP2.BankStuff.display;

/**
 * Write a description of class CheckB here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import AMP2.BankStuff.BHTPane;
import AMP2.BankStuff.Check;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.*;

public class CheckB extends JPanel {

    private static final int CENTER = SwingConstants.CENTER;
    private final CheckButton cb;
    private final Check check;

    /**
     * Constructor for objects of class CheckB
     */
    public CheckB(BHTPane bHTP, Check check, Color color) {
        super();
        cb = new CheckButton(bHTP, check);
        this.check = check;

        initialize(color);

    }

    public Check getCheck() {
        return check;
    }

    private void initialize(Color color) {
        
        
        cb.setLayout(new GridLayout(1, 8));
        cb.setPreferredSize(new Dimension(650, 25));


        cb.setBackground(color);


        JLabel lab = new JLabel("" + check.getCheckNum(), CENTER);
        cb.add(lab);
        lab = new JLabel(check.getDateS(), CENTER);
        cb.add(lab);
        lab = new JLabel(check.getPayTo(), CENTER);
        cb.add(lab);
        lab = new JLabel("" + check.getAmount(), CENTER);
        cb.add(lab);
        lab = new JLabel(check.getGlCode().getDescription(), CENTER);
        cb.add(lab);

        String goneTh = "Yes";
        if (check.getGoneThrough() == false) {
            goneTh = "No";
        }
        lab = new JLabel(goneTh, CENTER);
        cb.add(lab);
        
        lab = new JLabel(check.getClearDate(), CENTER);
        cb.add(lab);
        lab = new JLabel(check.getForS(), CENTER);
        cb.add(lab);
        

        add(cb);
    }

    private static class CheckButton extends JButton {

        CheckButton(final BHTPane bHTP, final Check check) {

            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    bHTP.check(1, check);
                }
            });
        }
    }
}
