package AMP2.MainDisplay;


import AMP2.Util.FileHandler;
import AMP2.Util.PrintUtilities;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;

public class EnvelopeF extends JFrame
{
    JMenuBar menuB;
    Container cP;
    JPanel mainPanel, senderP, receiverP, pan;
    JTextArea senderTA, receiverTA;
    JComboBox receiver, sender;
    HashMap addresses;
    ArrayList order;
    
    /**
     * Constructor for objects of class EnvelopeF
     * 
     * take a hashmap with addresses and a nick name......
     */
    public EnvelopeF(int i)
    {
        super();
        //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cP = this.getContentPane();
        this.setSize(475, 250); //205 - 300
        try { FileHandler fH = new FileHandler();
            addresses = (HashMap) fH.readObAn("AMP\\Addresses");
            order = (ArrayList) addresses.get("ArrayList");
            addresses.remove("ArrayList");
            //MiscStuff.writeToLog("try");
        }
        catch(IOException ex) {}
        catch(ClassNotFoundException cnf) {}
        
        if(addresses == null) {
            addresses = new HashMap();
            order = new ArrayList();
            //MiscStuff.writeToLog("new");
        }
//        addresses = new HashMap();
  //      order = new ArrayList();
    //    addresses.put("ArrayList", order);
      //  addresses.put("Time Out Ephrata", 
        //    "Time Out Pizza\n51450 Hwy. 97\n" +
          //  "LaPine, OR 97739");
        setUp();
    }
    
    /**
     * Closes our frame.
     */
    private void closeFrame() {
        String senText = senderTA.getText();
        if(!senText.equals("")) {
            int first = senText.indexOf('\n');
            String str = senText.substring(0, first);
            order.remove(str);
            order.add(0, str);
            
            if(!addresses.containsValue(senText)) {
                addresses.put(str, senText);
            }   
        }
        String recText = receiverTA.getText();
        if(!senText.equals("")) {
            int first = recText.indexOf('\n');
            String str = recText.substring(0, first);
            addresses.put(str, recText);
            order.remove(str);
            order.add(1, str);
            
            if(!addresses.containsValue(recText)) {
                addresses.put(str, recText);
            }   
        }
        
        if(order.size() > 30) {
            for(int i = 30; i < order.size(); i++) {
               addresses.remove(order.get(i));
            }
            while(order.size() > 30) {
               order.remove(30);
            }
        }
        try{
            FileHandler fH = new FileHandler();
            addresses.put("ArrayList", order);
            fH.writeObAn("AMP\\Addresses",
                addresses);   
                //MiscStuff.writeToLog("close");
        }
        catch(IOException exc) {
        }
        
        this.setVisible(false);
        //this = null;
    }
    
    /**
     * A method that does our setting up.
     */
    private void setUp() {
        this.addWindowListener(new WindowListener() {
            public void windowActivated(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {}
            public void windowClosing(WindowEvent e) {
                closeFrame();}
            public void windowDeactivated(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowOpened(WindowEvent e) {}
        });
        
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        //mainPanel.setBackground(Color.LIGHT_GRAY);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH ;
        
        cP.add(mainPanel);
        
        String senderT = ":\n";
        if(order.size() > 0) {
            senderT = (String) addresses.get(order.get(0));   
        }
        senderTA = new JTextArea(senderT, 6, 25);
        c.weighty = 8;//8
        c.weightx = 6;
        c.gridx = 0;
        c.gridy = 0;
   
        mainPanel.add(senderTA, c);
        
        String receiverT = ":\n";
        if(order.size() > 1) {
            receiverT = (String) addresses.get(order.get(1));   
        }
        
        receiverTA = new JTextArea(receiverT, 6, 25);
        c.weighty = 4; //4
        c.weightx = 4;
        c.gridx = 3;
        c.gridy = 1;
        //c.anchor = GridBagConstraints.CENTER;
        mainPanel.add(receiverTA, c);
        
        menuB = new JMenuBar();
        this.setJMenuBar(menuB);
        
        JMenu file = new JMenu("File");
        menuB.add(file);
        
        JMenuItem print = new JMenuItem("Print & Exit");
        print.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PrintUtilities.printComponent(cP, 2);
                closeFrame();
            }
        });
        file.add(print);
        
        //JMenu receiver = new JMenu("Receiver");
        //String[] recStrings = { "Receiver", "New"};
        //int length = recStrings.length;
        ArrayList list = new ArrayList(addresses.keySet());
        list.add(0, "Sender");
      
        sender = new JComboBox(list.toArray());
        sender.setSelectedIndex(0);
        sender.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = (String) sender.getSelectedItem();
                
                senderTA.setText((String) addresses.get(selected));
            }
        });
        menuB.add(sender);
        
        list = new ArrayList(addresses.keySet());
        list.add(0, "Receiver");
        
        receiver = new JComboBox(list.toArray());
        receiver.setSelectedIndex(0);
        receiver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = (String) receiver.getSelectedItem();
                
                receiverTA.setText((String) addresses.get(selected));
            }
        });
        menuB.add(receiver);

        
        
        this.setVisible(true);
    }

}
