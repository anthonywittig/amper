/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.command;

import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Order;
import AMP2.DatabaseStuff.tables.Table;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author awittig
 */
public class Select {
    
    private final List<Column> values;
    private Table table;
    private final Where where;
    private final List<Pair<Column, Order>> orderBy; 
    
    public Select(List<Column> values, Table table, Where where, List<Pair<Column, Order>> orderBy) {
        this.values = values;
        this.table = table;
        this.where = where;
        this.orderBy = orderBy;
    }

    public Table getTable() {
        return table;
    }

    public List<Column> getValues() {
        return values;
    }

    public Where getWhere() {
        return where;
    }
    
    public List<Pair<Column, Order>> getOrderBy(){
        return orderBy;
    }

    @Override
    public String toString() {
        return "Select{" + "values=" + values + ", table=" + table + ", where=" + where + ", orderBy=" + orderBy + '}';
    }

    public void setTable(Table table) {
        this.table = table;
    }
    
    
}
