/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.MainDisplay;

/**
 *
 * @author awittig
 */
public class InvalidUserInput extends Exception implements UserReadableMessage{
    
    public String userReadableMessage = null;

    public InvalidUserInput(Throwable thrwbl) {
        super(thrwbl);
    }

    public InvalidUserInput(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public InvalidUserInput(String string) {
        super(string);
    }

    @Override
    public String getUserReadableMessage() {
        if(userReadableMessage != null){
            return userReadableMessage;
        }else{
            return this.getMessage();
        }
    }

    public InvalidUserInput setUserReadableMessage(final String userReadableMessage){
        this.userReadableMessage = userReadableMessage;
        return this;
    }
    
    
}
