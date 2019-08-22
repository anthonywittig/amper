/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.tables;

import AMP2.BankStuff.Currency;


/**
 *
 * @author awittig
 */
public enum DataType {
    INTEGER("INT", Integer.class),
    VARCHAR_200("VARCHAR(200)", String.class),
    VARCHAR_20("VARCHAR(20)", String.class),
    VARCHAR_500("VARCHAR(500)", String.class),
    VARCHAR_100("VARCHAR(100)", String.class),
    VARCHAR_50("VARCHAR(50)", String.class),
    //DECIMAL_14_6("Decimal(14,6)", Double.class),
    CURRENCY("VARCHAR(500)", Currency.class),
    LONG("BIGINT", Long.class);
    
    private final String sqlType;
    private final Class<?> javaType;
    
    DataType(final String sqlType, final Class<?> javaType){
        this.sqlType = sqlType;
        this.javaType = javaType;
    }
    
    public String sqlType(){return sqlType;}
    public Class<?> javaType(){return javaType;}
}
