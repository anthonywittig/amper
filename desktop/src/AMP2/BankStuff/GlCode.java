/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.BankStuff;

/**
 *
 * @author awittig
 */
public class GlCode {
    private int id;
    private final Code code; 
    private final String description;
    private int BankHealthID;
    
    public GlCode(Code code){
        this(code, "" + code.getCode());
    }
    
    public GlCode(Code code, String description){
        this(code, description, -1);
    }
    
    public GlCode(Code code, String description, int bankHealthId){
        this(-1, code, description, bankHealthId);
    }

    public GlCode(int id, Code code, String description, int BankHealthID) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.BankHealthID = BankHealthID;
    }

    public int getBankHealthID() {
        return BankHealthID;
    }

    public Code getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GlCode{" + "id=" + id + ", code=" + code + ", description=" + description + ", BankHealthID=" + BankHealthID + '}';
    }
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GlCode other = (GlCode) obj;
        if (this.id != other.id) {
            return false;
        }
        if ((this.code == null) ? (other.code != null) : !this.code.equals(other.code)) {//(this.code != other.code) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if (this.BankHealthID != other.BankHealthID) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.id;
        hash = 67 * hash + (this.code != null ? this.code.hashCode() : 0);//this.code;
        hash = 67 * hash + (this.description != null ? this.description.hashCode() : 0);
        hash = 67 * hash + this.BankHealthID;
        return hash;
    }

    
    public void setBankHealthId(int bankHealthId) {
        this.BankHealthID = bankHealthId;
    }
    
    
    
    public static class Code {
        private final int code;
        
        public Code(int code){
            this.code = code;
        }
        
        public int getCode(){
            return code;
        }

        @Override
        public String toString() {
            return "Code{" + "code=" + code + '}';
        }
        

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Code other = (Code) obj;
            if (this.code != other.code) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 59 * hash + this.code;
            return hash;
        }
        
        
    }
    
    
}
