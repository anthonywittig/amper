/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.BankStuff;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;


/**
 *
 * @author awittig
 */
public class Currency{
    public static final Currency One = new Currency(1);
    public static final Currency Zero = new Currency(0);
    public static final Currency NegativeOne = new Currency("-1");
    
    private BigDecimal value;
    
    public Currency(int i){
        this("" + i);
    }
    
    public Currency(String string) {
        this(new BigDecimal(string));
    }
    
    public Currency(BigDecimal value){
        this.value = value;
    }
    
    public Currency subtract(Currency currency) {
        return new Currency(value.subtract(currency.value));
    }

    public Currency multiply(double d) {
        return multiply(new BigDecimal(d));
    }
    
    public Currency multiply(BigDecimal bd){
        return multiply(new Currency(bd));
    }
    
    public Currency multiply(Currency currency){
        return new Currency(value.multiply(currency.value)); 
    }
    
    public Currency add(Integer i){
        return add(new Currency(i));
    }

    public Currency add(Currency currency) {
        return new Currency(value.add(currency.value));
    }
    
    public Currency divide(int i){
        return divide(new Currency(i));
    }
    
    public Currency divide(Currency currency){
        try{
            return new Currency(value.divide(currency.value, 10, RoundingMode.HALF_UP));
        }catch(java.lang.ArithmeticException ari){
            throw new RuntimeException(String.format("was dividing %s by %s", this, currency), ari);
        }
    }
    
    public boolean ne(int i){
        return ne(new Currency(i));
    }
    
    public boolean ne(Currency currency){
        return !this.equals(currency);
    }
    
    public Currency round(){
        return new Currency(value.add(BigDecimal.ZERO).setScale(2, BigDecimal.ROUND_HALF_UP));
    }
    
    public String twoDecFormat(){
        return round().value.toString();
    }
    
    public String format3(){
        //this is like the twoDecFormat, except that it also adds commas 
        return NumberFormat.getNumberInstance().format(round().value);
    }
    
    public boolean lt(Currency nLow) {
        return -1 == this.value.compareTo(nLow.value);
    }
    
    public boolean lte(Currency c){
        return lt(c) || equals(c);
    }
    
    @Override
    public Currency clone(){
        return this.add(Zero);
    }
    
    @Override
    public String toString(){
        return value.toPlainString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Currency other = (Currency) obj;
        if (this.value != other.value && (this.value == null || 
                //notice we won't use equals: 
                //http://stackoverflow.com/questions/6787142/bigdecimal-equals-versus-compareto
                //!this.value.equals(other.value)
                (this.value.compareTo(other.value) != 0)
                )) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    
    
    
    
}
