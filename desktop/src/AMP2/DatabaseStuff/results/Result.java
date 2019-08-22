/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.results;

import AMP2.BankStuff.Currency;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.tables.Column;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author awittig
 */
public class Result {
    
    //private final List<Table> tables = new ArrayList<Table>();
    private final Map<Column, Object> value = new HashMap<Column, Object>();
    
//    Result(Table ... tables){
//        this.tables.addAll(Arrays.asList(tables));
//    }
    
    Result(){}

    public void replaceCol(Column col1, Column col2) {
        if (value.containsKey(col1)) {
            value.put(col2, value.get(col1));
            value.remove(col1);
        }
    }

    void put(Column column, Object val) {
        //we assume a good type is put in since only Results
        //should be filling this value
        value.put(column, val);
    }

    public Object get(Column col) throws DataException{
        //anyone could be calling this, make sure they want the right col:
        if(!value.containsKey(col)){
            throw new DataException(col + " is not a valid column, we only have: " + value.keySet());
        }
        
        final Object val = value.get(col);
        
        if(!col.type().javaType().isInstance(val)){
            throw new DataException("col's type doesn't match values type. Col: " + col + ", val: " + val);
        }
        
        return val;
    }
    
    public Integer getInteger(Column col) throws DataException{
        return (Integer) get(col);
    }
    
    public String getString(Column col) throws DataException{
        return (String) get(col);
    }
    
    public Double getDouble(Column col) throws DataException{
        return (Double) get(col);
    }

    public long getLong(Column column) throws DataException {
        return (Long) get(column);
    }

    public Currency getCurrency(Column column) throws DataException {
        return (Currency) get(column);
    }
    
    @Override
    public String toString(){
        return value.toString();
    }
    
}
