package AMP2.Util;
import java.awt.*;
import javax.swing.*;
import java.awt.print.*;
import javax.print.attribute.standard.*; 
import javax.print.attribute.*;
import java.awt.image.*;

/** A simple utility class that lets you very simply print
 *  an arbitrary component. Just pass the component to the
 *  PrintUtilities.printComponent. The component you want to
 *  print doesn't need a print method and doesn't have to
 *  implement any interface or do anything special at all.
 *  <P>
 *  If you are going to be printing many times, it is marginally more 
 *  efficient to first do the following:
 *  <PRE>
 *    PrintUtilities printHelper = new PrintUtilities(theComponent);
 *  </PRE>
 *  then later do printHelper.print(). But this is a very tiny
 *  difference, so in most cases just do the simpler
 *  PrintUtilities.printComponent(componentToBePrinted).
 *
 *  7/99 Marty Hall, http://www.apl.jhu.edu/~hall/java/
 *  May be freely used or adapted.
 */

public class PrintUtilities implements Printable {
  private Component componentToBePrinted;
  private static boolean printDialog = true;
  private static boolean envelopePaper = false;
  private PageFormat pF;

  public static void printComponent(Component c) {
      
      new PrintUtilities(c).print();
  }
  
  public static void printComponent(Component c, boolean bW) {

     Container con = (Container) c;
     //con.add(c); 
     
      if(bW) {
        blackAndWhite(con);
    }
        
      new PrintUtilities(con).print();
  }
  
    public static void printComponent(Component c, int pD) {

        Container con = (Container) c;
     //con.add(c); 
     
        if(pD == 1) {
            printDialog = false;
            blackAndWhite(con);
        }
    
        if(pD == 2 ) {
            printDialog = false;
            envelopePaper = true;
            //blackAndWhite(con);   
        }
        
      new PrintUtilities(con).print();
  }
  
  public PrintUtilities(Component componentToBePrinted) {
    this.componentToBePrinted = componentToBePrinted;
  }
  
  public void print() {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    pF = new PageFormat();
    pF.setOrientation(PageFormat.LANDSCAPE);
    PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
//    aset.add(Chromaticity.MONOCHROME);
    aset.add(MediaSizeName.NA_NUMBER_10_ENVELOPE); 
    aset.add(OrientationRequested.LANDSCAPE);
    
    if(envelopePaper) {
        Paper pa = pF.getPaper();
        //pF = new PageFormat();
    //    pF.setOrientation(PageFormat.REVERSE_LANDSCAPE); //PORTRAIT 
    //    pa.setSize(684, 288);
//        MiscStuff.writeToLog(pa.getImageableHeight());
  //      MiscStuff.writeToLog(pa.getImageableWidth());
    //    MiscStuff.writeToLog(pa.getImageableX() );
      //  MiscStuff.writeToLog(pa.getImageableY() );
        pa.setImageableArea(50, 60, 468, 648);
        pF.setPaper(pa);

        //MiscStuff.writeToLog("hit");
    }
    
    printJob.setPrintable(this, pF);
    
    if(envelopePaper) {
        try {
            printJob.print(aset);
            } 
            catch(PrinterException pe) {
            //MiscStuff.writeToLog("Error printing: " + pe);
            }        
    }
    
    if(printDialog) {
        if (printJob.printDialog());
          try {
            printJob.print();//aset);
            return; //don't go on my friend...
          } 
          catch(PrinterException pe) {
          //  MiscStuff.writeToLog("Error printing: " + pe);
        }
    }
    if(!envelopePaper){
        try {
        printJob.print();
        } 
        catch(PrinterException pe) {
        //MiscStuff.writeToLog("Error printing: " + pe);
        }   
    }
  }

  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
    if (pageIndex > 0) {
      return(NO_SUCH_PAGE);
    } else {
      Graphics2D g2d = (Graphics2D)g;
      //g2d = greyScale(g2d);
      //g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      g2d.translate(pF.getImageableX(), pF.getImageableY());
      g2d.scale(0.8, 1.0); 

      disableDoubleBuffering(componentToBePrinted);
      componentToBePrinted.paint(g2d);
      enableDoubleBuffering(componentToBePrinted);
      return(PAGE_EXISTS);
    }
  }

  /** The speed and quality of printing suffers dramatically if
   *  any of the containers have double buffering turned on.
   *  So this turns if off globally.
   *  @see enableDoubleBuffering
   */
  public static void disableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
  }

  /** Re-enables double buffering globally. */
  
  public static void enableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
  }
  
  /**
     * Apply this filter to an image.
     * 
     * @param  image  The image to be changed by this filter.
     */
    public static void blackAndWhite(Container c) {
        int conC = c.getComponentCount();
     
        for(int i = 0; i < conC; i++) {
            Container c2 = (Container) c.getComponent(i); 

            if(c2 instanceof JButton){
                //no need to print buttons... or is there?
                c2.setVisible(false);
            }else{
                c2.setBackground(Color.WHITE);
            }
             
            if(c2.getComponentCount() > 0) {
                 blackAndWhite(c2);     
            }
        }
    }
}


