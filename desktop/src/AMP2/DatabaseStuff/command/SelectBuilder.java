/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.command;

import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Order;
import AMP2.DatabaseStuff.tables.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author awittig
 */
public class SelectBuilder {
    private final List<Column> values = new ArrayList<Column>();
    private Table table;
    private Where where;
    private final List<Pair<Column, Order>> orderBy = new ArrayList<Pair<Column, Order>>();
    
    public SelectBuilder table(final Table table){
        if(this.table != null){
            throw new RuntimeException("table already set: " + table.name());
        }
        this.table = table;
        return this;
    }
    
    public SelectBuilder where(final Where where){
        if(this.where != null){
            throw new RuntimeException("where already set: " + where);
        }
        this.where = where;
        return this;
    }
    
    public SelectBuilder addValue(final Column... columns){
        for(final Column col : columns){
            addValue(col);
        }
        return this;
    }
    
    public SelectBuilder addValue(final Column column){
        if(values.contains(column)){
            throw new RuntimeException("columns alrady contains: " + column.name());
        }
        values.add(column);
        return this;
    }
    
    public Select build(){
        if(table == null){
            throw new RuntimeException("no table");
        }else if(values.size() == 0){
            throw new RuntimeException("no values");
        }
        
        //null is valid for where
        
        if(values.contains(Column._star)){
            values.remove(Column._star);
            values.addAll(Arrays.asList(table.cols()));
        }
        
        return new Select(values, table, where, orderBy);
    }

    public SelectBuilder orderBy(Column orderBy) {
        return orderBy(orderBy, Order.ASC);
    }
    
    public SelectBuilder orderBy(Column orderBy, Order order) {
        this.orderBy.add(new ImmutablePair<Column, Order>(orderBy, order));
        return this;
    }
}
