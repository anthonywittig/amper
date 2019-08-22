/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package AMP2.Util;

import AMP2.DatabaseStuff.command.SelectBuilder;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.results.Result;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Table;
import AMP2.MainDisplay.GUI;
import com.wittigweb.util.Logger;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;

/**
 *
 * @author Andy
 */
public class MiscStuff {
    
    private static final SimpleDateFormat dF = new SimpleDateFormat("yyyy.MM.dd");
    
    

    private static void printCompanyInfoForDebugging(String printMessage) {
        
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column._star).table(Table.companies);
        final Results results;
        try {
            final StringBuilder msg = new StringBuilder(printMessage);
            results = GUI.getCon().select(sb.build());
            
            for(final Result result : results){
                msg.append("\n").append(result);
            }

            MiscStuff.writeToLog(msg.toString());
        } catch (Exception ex) {
            MiscStuff.writeToLog("Can't get company info, maybe non exist yet", ex);
        }
        
    }
    
     private static void printTaxInfoForDebugging(String printMessage) {
        
        
        final SelectBuilder sb = new SelectBuilder().addValue(Column._star).table(Table.taxes);
        final Results results;
        try {
            final StringBuilder msg = new StringBuilder(printMessage);
            results = GUI.getCon().select(sb.build());
            for(final Result result : results){
                msg.append("\n").append(result);
            }
            
            MiscStuff.writeToLog(msg.toString());
        } catch (Exception ex) {
            MiscStuff.writeToLog("Can't get data for tax info, maybe the table doesn't exist yet", ex);
        }
        
        
        
    }
    
    public static void writeToLog(String s, Throwable th){
        writeToLogForReal(s + "\n" + getStackTrace(th));
    }
    
    public static void writeToLog(Throwable th){
        writeToLogForReal(getStackTrace(th));
    }
    
    public static void writeToLog(String logText){
        if(logText.contains("Exception") || logText.contains("DEGUGING")){
            writeToLog(logText, new Exception());
        }else{
            writeToLogForReal(logText);
        }
    }
    
    
    /**
     * this writes to our log file
     *
     *  @param text, the text to be written
     */
    private static void writeToLogForReal(String logText){
        
        final String nameOfFile = getNameOfFile();
        
        
        try{
           
            {
                File directory = new File(nameOfFile).getParentFile();
                directory.mkdirs();
            }
            
            
            File toLogTo = new File(nameOfFile);
            
            toLogTo.createNewFile();//.mkdirs());
        } catch(Exception e){
            System.err.println("Error making log file! look at MiscStuff.writeToLog");
        }
        
        System.out.println("logging: " + logText);
        Logger.log(nameOfFile, logText);
        
    }
    
    
    public static String getNameOfFile(){
        
        final Calendar cal = Calendar.getInstance();
        
        final String lastPartOfName = dF.format(cal.getTime()) + "_log.txt";
        
        final String nameOfFile = System.getProperty("user.dir") + 
                File.separator + "logs" + File.separator + lastPartOfName;
        
        return nameOfFile;
    }
    
    public static String getStackTrace(final Throwable th){
        final StringBufferWriter sb = new StringBufferWriter();
        th.printStackTrace(new PrintWriter(sb));
        
        return sb.toString();
    }
    
    //true if all files are deleted
    public static void deleteRecursive(final File file) throws Exception{
        
        if(file.isDirectory()){
            for(final File f : file.listFiles()){
                deleteRecursive(f);
            }
        }
        
        if(!file.delete()){
            throw new Exception("Couldn't delete: " + file.getAbsolutePath());
        }
        
    }

}
