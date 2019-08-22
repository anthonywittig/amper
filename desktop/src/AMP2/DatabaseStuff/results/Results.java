/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff.results;

import AMP2.BankStuff.Currency;
import AMP2.DatabaseStuff.command.Select;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.tables.Column;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author awittig
 */
public class Results implements Iterable<Result> {

    private List<Result> results = new ArrayList<Result>();
    private List<Column> cols = new ArrayList<Column>();

    private Results(Select select) {
        cols.addAll(select.getValues());
    }

    public static Results getResults(Select select, ResultSet rs) throws DataException {

        final Results results = new Results(select);

        try {
            while (rs.next()) {

                final Result value = new Result();

                for (final Column col : results.cols) {

                    if (col.type().javaType().equals(Integer.class)) {
                        value.put(col, rs.getInt(col.colName()));
                    } else if (col.type().javaType().equals(String.class)) {
                        value.put(col, rs.getString(col.colName()));
                    } else if (col.type().javaType().equals(Double.class)) {
                        value.put(col, rs.getDouble(col.colName()));
                    } else if (col.type().javaType().equals(Long.class)) {
                        value.put(col, rs.getLong(col.colName()));
                    } else if (col.type().javaType().equals(Currency.class)) {
                        value.put(col, new Currency(rs.getString(col.colName())));
                    } else {
                        throw new DataException("Don't have a case for the data type of col: " + col);
                    }
                }

                results.add(value);
            }
        } catch (SQLException sql) {
            throw new DataException(sql);
        }

        return results;
    }

    @Override
    public Iterator<Result> iterator() {
        return results.iterator();
    }

    private void add(Result value) {
        results.add(value);
    }

    public boolean isEmpty() {
        return results.isEmpty();
    }

    public Result get(int i) {
        return results.get(i);
    }

    public int size() {
        return results.size();
    }

    @Override
    public String toString() {
        return "Results{" + "results=" + results + ", cols=" + cols + '}';
    }
}
