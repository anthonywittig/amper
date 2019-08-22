/*
 * CompanyDBWrap.java
 *
 * Created on December 2, 2006, 11:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package AMP2.DatabaseStuff;

import AMP2.DatabaseStuff.command.SelectBuilder;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.results.Result;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Table;
import AMP2.MainDisplay.GUI;
import java.util.Calendar;
import java.util.Random;

/**
 *
 * @author TheFamily
 */
public class CompanyDBWrap {
    

    private static int lastID = -1;
    private int ID;
    private String Name = "This text should never been seen!!!!!!!!!!!";
    private String Location = "location";
    private int Year = 2000;
    //no functions for location, not that I think we will need them...

    /** Creates a new instance of CompanyDBWrap */
    private CompanyDBWrap() {
    }

    public static CompanyDBWrap getNewInstance() throws DataException {
        final CompanyDBWrap company = new CompanyDBWrap();

        if (lastID == -1) {
            lastID = DatabaseHelper.GetLastUsedCompanyID();
        }

        company.fill(lastID);
        
        return company;
    }

    /**
     *  This method deletes the company from the database
     *
     *  @param companyName, the name of the company
     *  @param companyLocation, the location of the company
     *  @param year, the year of the company
     *
     *  @return true if deleted, false otherwise
     */
    public static void DeleteCompany(String companyName, String companyLocation, int year) throws DataException {
        try {
            GUI.getCon().delete(Table.companies,
                    new Where(Column.name, companyName).and(Column.location, companyLocation).and(Column.year, year));
            //.GetANewStatement().execute("delete from companies where name = '"+companyName+"' and location = '"+companyLocation+"' and year ="+year+";");
        } catch (DataException e) {
            throw e;
        }
    }

    /**
     *  This method deletes the company from the database
     *
     *  @param companyID, the id of the company
     *
     *  @return true if deleted, false otherwise
     */
    public static void DeleteCompany(int companyID) throws DataException {
        
            GUI.getCon().delete(Table.companies,
                    new Where(Column.ID, companyID));
//            return GUI.getCon().GetANewStatement().execute("delete from companies "
//                    + "where id = " + companyID);    
    }

    /**
     *tries to make a new company and insert it into the database
     *
     *@return true if insert worked, false otherwise
     */
    public static boolean InsertCompany(String companyName, String companyLocation, int year) throws DataException {
        boolean result = false;
        try {
            final int id = GUI.getCon().insert(Table.companies, new Where(Column.name, companyName).and(Column.location, companyLocation).and(Column.year, year));
            result = 0 < id;
            //result = GUI.getCon().GetANewStatement().execute("insert into companies(name, location, year) values('" + companyName +"','"+companyLocation+"', "+year+");");
        } catch (DataException e) {
            throw e;
        }

        //this updates our lastID
        SelectCompanyID(companyName, companyLocation, year);


        return result;
    }

    /**
     * this fills in our company info based on the comanyID passed in
     *
     *  @param companyID, the id of our company
     */
    public void fill(int companyId) throws DataException {
        
        if(companyId == -1){
            //do nothing
            return;
        }


        final SelectBuilder selectBuilder = new SelectBuilder();
        selectBuilder.addValue(Column.location).addValue(Column.year).addValue(Column.name).addValue(Column.ID).table(Table.companies).where(new Where(Column.ID, companyId));

        final Results values = GUI.getCon().select(selectBuilder.build());


//            ResultSet rs = GUI.getCon().GetANewStatement().executeQuery("select * from companies where id = "+companyId+";");
//
//            rs.absolute(1);
//            setLocation(rs.getString("location"));
//            setYear(rs.getInt("year"));
//            setName(rs.getString("name"));
//            setID(rs.getInt("ID"));

        //we expect only one:
        if (values.size() != 1) {
            throw new DataException("Size wasn't 1, it was: " + values.size());
        }

        final Result value = values.get(0);

        setLocation((String) value.get(Column.location));
        setYear((Integer) value.get(Column.year));
        setName((String) value.get(Column.name));
        setID((Integer) value.get(Column.ID));


    }

    /**
     *gets our ID
     */
    public int getID() {
        return ID;
    }

    /**
     *gets our Name
     */
    public String getName() {
        return Name;
    }

    /**
     *gets our year
     */
    public int getYear() {
        return Year;
    }

    /**
     *this method returns the id of the given company if it exists
     *
     *@return ID, -1 if not in database
     */
    public static int SelectCompanyID(String name, String location, int year) throws DataException {
        try {

            final SelectBuilder sb = new SelectBuilder();
            sb.addValue(Column.ID).table(Table.companies).where(new Where(Column.name, name).and(Column.location, location).and(Column.year, year));

            //ResultSet rs = GUI.getCon().GetANewStatement().executeQuery(
            //"select id, name, location, year from companies where name = '"+name+"' and location = '"+location+"' and year ="+year+";");
            //rs.absolute(1);

            final Results values = GUI.getCon().select(sb.build());
            if (values.size() != 1) {
                throw new DataException("expected one result, had: " + values.size());
            }
            lastID = (Integer) values.get(0).get(Column.ID);
            return lastID;
        } catch (DataException e) {
            //MiscStuff.writeToLog("SelectCompanyID :" + e.toString());
            //MiscStuff.writeToLog("select id, name, year from companies where name = '"+name+"' and year = "+year+";");
            //return - 2;//why is this a '2'?
            throw new DataException("we used to return a \"-2\" in this situation... should we do so? name: " + name + ", location: " + location + ", year: " + year, e);
        }
        //return -1;

    }
    
    static boolean doesCompanyExist(String name, String location, int year) throws DataException {
        final SelectBuilder sb = new SelectBuilder();
        sb.addValue(Column.ID).table(Table.companies).where(new Where(Column.name, name).and(Column.location, location).and(Column.year, year));

        final Results values = GUI.getCon().select(sb.build());
        return !values.isEmpty();
    }
    
    public static int getNumberOfCompanies() throws DataException{
        final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.companies);
        
        final Results values = GUI.getCon().select(sb.build());
        return values.size();
    }

    /**
     *Sets our ID field
     */
    public void setID(int id) {
        ID = id;
    }

    /**
     *Sets our Name field
     */
    public void setName(String name) {
        Name = name;
    }

    /**
     *sets our year
     */
    public void setYear(int year) {
        Year = year;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String Location) {
        this.Location = Location;
    }
}
