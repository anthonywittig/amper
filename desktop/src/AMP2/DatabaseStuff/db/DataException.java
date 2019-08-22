/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.db;

import AMP2.MainDisplay.UserReadableMessage;

/**
 *
 * @author awittig
 */
public class DataException extends Exception implements UserReadableMessage{

    public String userReadableMessage = null;
    
    public DataException(Throwable thrwbl) {
        super(thrwbl);
    }

    public DataException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
    
    public DataException(String string) {
        super(string);
    }

    public DataException() {
    }

    @Override
    public String getUserReadableMessage() {
        return userReadableMessage;
    }
    
    public DataException setUserReadableMessage(final String userReadableMessage){
        this.userReadableMessage = userReadableMessage;
        return this;
    }
}
