/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.command;

import AMP2.DatabaseStuff.command.Where.Operator;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.DataType;
import AMP2.Util.MiscStuff;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author awittig
 */
public final class Where implements Iterable<Entry<Operator, Map<Column, String>>>{
    
    public static enum Operator{
        eq("="), gt(">");
        public final String operator;
        Operator(String operator){
            this.operator = operator;
        }
        public String operator(){return operator;}
    }
    
    private final Map<Column, String> ands = new LinkedHashMap<Column, String>();
    private final Map<Column, String> greaters = new LinkedHashMap<Column, String>();

    public static Where getWhere(Column[] columns, Object[] objects) throws DataException {
        
        if(columns.length != objects.length){
            throw new DataException("columns and objects are not same lenght: " + columns + " --- " + objects);
        }
        
        final Where where = new Where(columns[0], objects[0]);
        
        for(int colIdx = 1; colIdx < columns.length; ++colIdx){
            where.and(columns[colIdx], objects[colIdx]);
        }
        
        return where;
    }
    
    

    public Where(final Column col, final Object value) {
        and(col, value);
    }
    
//    public Where(final Column col, final int value){
//        and(col, value);
//    }
//    
//    public Where(final Column col, final double value){
//        and(col, value);
//    }

    public Where and(final Column col, final Object value) {
        ands.put(col, value.toString());
        logIsValidValueForColumn(col, value);
        return this;
    }
    
//    public Where and(final Column col, final int value){
//        ands.put(col, "" + value);
//        logIsValidValueForColumn(col, value);
//        return this;
//    }
//    
//    public Where and(final Column col, final double value){
//        ands.put(col, "" + value);
//        logIsValidValueForColumn(col, value);
//        return this;
//    }

    

    @Override
    public Iterator<Entry<Operator,Map<Column, String>>> iterator() {
        final Map<Operator, Map<Column, String>> opToValues = new LinkedHashMap<Operator, Map<Column, String>>(2);
        opToValues.put(Operator.eq, ands);
        opToValues.put(Operator.gt, greaters);
        return opToValues.entrySet().iterator();
    }
   
//    private void logIsValidValueForColumn(final Column col, final String val){
//        if(!col.type().javaType().equals(String.class)){
//            final Exception e = new Exception();
//            MiscStuff.writeToLog("Not using right data type, do not expect String for "
//                    + col + " at ", e);
//        }
//    }
//    
//    private void logIsValidValueForColumn(final Column col, final int val){
//        if(!col.type().javaType().equals(Integer.class)){
//            final Exception e = new Exception();
//            MiscStuff.writeToLog("Not using right data type, do not expect int for "
//                    + col + " at ", e);
//        }
//    }
//    
//    private void logIsValidValueForColumn(final Column col, final double val){
//        if(!col.type().javaType().equals(Double.class)){
//            final Exception e = new Exception();
//            MiscStuff.writeToLog("Not using right data type, do not expect double for "
//                    + col + " at ", e);
//        }
//    }
    
    private void logIsValidValueForColumn(final Column col, final Object val){
        if(!col.type().javaType().isInstance(val)){
            final Exception e = new Exception();
            MiscStuff.writeToLog("Not using right data type, do not expect " + val.getClass() + " for "
                    + col + " at ", e);
        }
    }

    public void replaceAllCols(final Map<Column, Column> colToReplacement) {
        
        final java.util.Set<Column> keys = new LinkedHashSet<Column>(ands.keySet());
        for(final Column col : keys){
            if(colToReplacement.containsKey(col)){
                final String value = ands.get(col);
                ands.remove(col);
                ands.put(colToReplacement.get(col), value);
            }
        }
    }

    @Override
    public String toString() {
        return "Where{" + "ands=" + ands + ", greaters=" + greaters + '}';
    }

    public Where andGreater(Column col, Object value) throws DataException{
        if(col.type() == DataType.CURRENCY){
            throw new DataException("Can't use greater than for currency as it's a string");
        }
        greaters.put(col, value.toString());
        logIsValidValueForColumn(col, value);
        return this;
    }
    
}
