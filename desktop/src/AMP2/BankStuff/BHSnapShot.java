
package AMP2.BankStuff;

/**
 * Write a description of class BHSnapShot here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import java.util.*;
import java.io.*;

public class BHSnapShot implements Serializable
{
    private String dateS, ballanceS, unpostedCBS, unpostedDBS, adjustedBS,
        percentUS, futureBillsS, adjustedB2S, evenDS;
    
    /**
     * Constructor for objects of class BHSnapShot
     */
    public BHSnapShot(String dateS, String ballanceS, String unpostedCBS,
        String unpostedDBS, String adjustedBS, String percentUS,
        String futureBillsS, String adjustedB2S, String evenDS) {
            
        this.dateS = dateS;
        this.ballanceS = ballanceS;
        this.unpostedCBS = unpostedCBS;
        this.unpostedDBS = unpostedDBS;
        this.adjustedBS = adjustedBS;
        this.percentUS = percentUS;
        this.futureBillsS = futureBillsS;
        this.adjustedB2S = adjustedB2S;
        this.evenDS = evenDS;
    }
    
    
    
    /**
     * A method that gets all the strings.
     * 
     * @param index, for the index of the string, starting at 0 and going up
     * in the same order as our constructor.
     * 
     * @return the string according to the index.
     */
    public String getStr(int index) {
        
        switch(index) {
            case 0: return dateS; 
            case 1: return ballanceS; 
            case 2: return unpostedCBS; 
            case 3: return unpostedDBS; 
            case 4: return adjustedBS; 
            case 5: return percentUS; 
            case 6: return futureBillsS; 
            case 7: return adjustedB2S; 
            case 8: return evenDS;
            default: return "not found";
        }
    }
    
    
    /**
     * A method that sets all the strings.
     * 
     * @param index, for the index of the string, starting at 0 and going up
     * in the same order as our constructor.
     * 
     * @param str, our new string.
     */
    public void setStr(int index, String str) {
       
       switch(index) {
            case 0: dateS = str; break; 
            case 1: ballanceS = str; break; 
            case 2: unpostedCBS = str; break;
            case 3: unpostedDBS = str; break;
            case 4: adjustedBS = str; break;
            case 5: percentUS = str; break;
            case 6: futureBillsS = str; break;
            case 7: adjustedB2S = str; break;
            case 8: evenDS = str; break;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BHSnapShot other = (BHSnapShot) obj;
        if ((this.dateS == null) ? (other.dateS != null) : !this.dateS.equals(other.dateS)) {
            return false;
        }
        if ((this.ballanceS == null) ? (other.ballanceS != null) : !this.ballanceS.equals(other.ballanceS)) {
            return false;
        }
        if ((this.unpostedCBS == null) ? (other.unpostedCBS != null) : !this.unpostedCBS.equals(other.unpostedCBS)) {
            return false;
        }
        if ((this.unpostedDBS == null) ? (other.unpostedDBS != null) : !this.unpostedDBS.equals(other.unpostedDBS)) {
            return false;
        }
        if ((this.adjustedBS == null) ? (other.adjustedBS != null) : !this.adjustedBS.equals(other.adjustedBS)) {
            return false;
        }
        if ((this.percentUS == null) ? (other.percentUS != null) : !this.percentUS.equals(other.percentUS)) {
            return false;
        }
        if ((this.futureBillsS == null) ? (other.futureBillsS != null) : !this.futureBillsS.equals(other.futureBillsS)) {
            return false;
        }
        if ((this.adjustedB2S == null) ? (other.adjustedB2S != null) : !this.adjustedB2S.equals(other.adjustedB2S)) {
            return false;
        }
        if ((this.evenDS == null) ? (other.evenDS != null) : !this.evenDS.equals(other.evenDS)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.dateS != null ? this.dateS.hashCode() : 0);
        hash = 37 * hash + (this.ballanceS != null ? this.ballanceS.hashCode() : 0);
        hash = 37 * hash + (this.unpostedCBS != null ? this.unpostedCBS.hashCode() : 0);
        hash = 37 * hash + (this.unpostedDBS != null ? this.unpostedDBS.hashCode() : 0);
        hash = 37 * hash + (this.adjustedBS != null ? this.adjustedBS.hashCode() : 0);
        hash = 37 * hash + (this.percentUS != null ? this.percentUS.hashCode() : 0);
        hash = 37 * hash + (this.futureBillsS != null ? this.futureBillsS.hashCode() : 0);
        hash = 37 * hash + (this.adjustedB2S != null ? this.adjustedB2S.hashCode() : 0);
        hash = 37 * hash + (this.evenDS != null ? this.evenDS.hashCode() : 0);
        return hash;
    }

    
}
