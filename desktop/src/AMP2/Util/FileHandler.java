package AMP2.Util;


/**
 * Write a description of class FileHandler here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import java.io.*;

public class FileHandler
{
    public String lastWrittenFileName;
    
    /**
     * Constructor for objects of class FileHandler
     */
    public FileHandler()
    {
        File f1 = new File("AMP");
        f1.mkdir(); // a nice place to put things...
    }
    
    /**
     * constructor for testing....
     */
//    public FileHandler(int i) {
 //       try{writeOb("test", new TheFile(1)); }
   //     catch(IOException ex){}        
    //}

    /**
     * A method that gets the last saved file.
     * 
     * @return the last saved file.
     */
    public Object getLastSavedFile() 
        throws IOException, ClassNotFoundException {
        
        return readOb(getLastSavedFileName());            
    }
    
    /**
     * A method that gets the last saved file name.
     * 
     * @return the last saved file name.
     */
    public String getLastSavedFileName()
        throws IOException, ClassNotFoundException {
        
        FileInputStream in = new 
            FileInputStream("AMP\\LastLoadFile");
        ObjectInputStream s = new ObjectInputStream(in);
        return (String)s.readObject();     
    }
    
    /**
     * A method that gets the last written file name,
     * 
     * @return lastWrittenFileName...
     */
    public String getLRFN() {
        return lastWrittenFileName;   
    }
    
    /**
     * A method that reads a file and returns the TheFile object.
     * 
     * @param fileName, the name of the file we are to use.
     * 
     * @return oB, the object.
     */
    public Object readOb(File file)
        throws IOException, ClassNotFoundException {
        
        lastWrittenFileName = file.getAbsolutePath();
        return readObAn(file);      
    }
    
    /**
     * A method that reads a file and returns an object with out doing
     * the lastWrittenFileName bit.
     * 
     * @param fileName, the name of the file we are to use.
     * 
     * @return oB, the object.
     */
    public Object readObAn(File file)
        throws IOException, ClassNotFoundException {
        
        FileInputStream in = new FileInputStream(file);
        ObjectInputStream s = new ObjectInputStream(in);
        Object ob = s.readObject();
        return ob;      
    }
    
    /**
     * A method that reads a file and returns the TheFile object.
     * 
     * @param fileName, the name of the file we are to use.
     * 
     * @return ob, the object.
     */
    public Object readOb(String fileName)
        throws IOException, ClassNotFoundException {
        
        lastWrittenFileName = fileName;
        //MiscStuff.writeToLog(lastWrittenFileName);
        return readObAn(fileName);      
    }
    
    /**
     * A method that reads a file and returns the TheFile object. No LastWritten
     * FileName deal.
     * 
     * @param fileName, the name of the file we are to use.
     * 
     * @return ob, the object.
     */
    public Object readObAn(String fileName)
        throws IOException, ClassNotFoundException {
 
        FileInputStream in = new FileInputStream(fileName);
        ObjectInputStream s = new ObjectInputStream(in);
        Object ob = s.readObject();
        //MiscStuff.writeToLog(lastWrittenFileName);
        return ob;      
    }
    
    /**
     * A method that writes a file.
     * 
     * @param fileName, the fileName.
     * @param ob, our object to be saved.
     */
    public void writeOb(String fileName, Object ob) 
        throws IOException {
        lastWrittenFileName = fileName; 
//        MiscStuff.writeToLog("writeOB() hit  " + fileName); //
  //      MiscStuff.writeToLog(ob.toString());
        
        writeObAn(fileName, ob);
    //    MiscStuff.writeToLog("writeOB() hit1");
        writeLastSavedFile(fileName);
      //  MiscStuff.writeToLog("writeOB() hit2");
    }
    
    /**
     * A method that writes a file with out the lastWrittenFileName deal.
     * 
     * @param fileName, the fileName.
     * @param ob, our object to be saved.
     */
    public void writeObAn(String fileName, Object ob) 
        throws IOException {
        
        FileOutputStream out = new FileOutputStream(fileName);
        ObjectOutputStream s = new ObjectOutputStream(out);
        s.writeObject(ob); ////////////////////////////////not Serializable....
        s.flush();  
    }
    
    /**
     * A method that writes our last file saved.
     * 
     * @param fileName, the name of our last saved file.
     */
    public void writeLastSavedFile(String fileName) 
        throws IOException {
        
 //       MiscStuff.writeToLog("writeLastSavedFile() hit");
        FileOutputStream out = new FileOutputStream("AMP\\LastLoadFile");
        ObjectOutputStream s = new ObjectOutputStream(out);
   //     MiscStuff.writeToLog("writeLastSavedFile() hit1");
        s.writeObject(fileName);
        s.flush();    
    }   

    /**
     * A method that writes to our last file name.
     * 
     * @param tF, the file to be saved.
     */    
    public void writeToLastFileName(Object ob) {
        
        //if(ob != null) {
          //  MiscStuff.writeToLog("wTLFN not null");   
        //}
        if(lastWrittenFileName != null) {
            try{writeOb(lastWrittenFileName, ob); }
            catch(IOException e) {} //MiscStuff.writeToLog("ioex, wTLFN");}
        }
            
    }
}
