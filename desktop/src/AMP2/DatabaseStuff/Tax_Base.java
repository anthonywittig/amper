package AMP2.DatabaseStuff;

import AMP2.BankStuff.Currency;
import AMP2.DatabaseStuff.command.SelectBuilder;
import AMP2.DatabaseStuff.command.Where;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.results.Result;
import AMP2.DatabaseStuff.results.Results;
import AMP2.DatabaseStuff.tables.Column;
import AMP2.DatabaseStuff.tables.Table;
import AMP2.MainDisplay.GUI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;


public class Tax_Base {

    protected int id = -1;
    protected int companyID = -1;
    protected int taxTypeID = -1;
    protected int claim = -1;
    protected Currency underAmount = new Currency("-1");
    protected Currency tax = new Currency("-1");

    public Tax_Base() {
    }

    /**
     * Creates a new instance based on the database id
     * @param id the id from the db
     */
    public static void fill(Tax_Base tax, int id) throws DataException {
 
        if (id > 0) {
//            try {
//                PreparedStatement stmt = GUI.getCon().GetConnection().prepareStatement("select * from taxes where id = ? order by id ");
//                stmt.setInt(1, id);
//                ResultSet rs = stmt.executeQuery();
//                rs.absolute(1);
                
                final SelectBuilder sb = new SelectBuilder().addValue(Column._star).table(Table.taxes)
                        .where(new Where(Column.ID, id))
                        .orderBy(Column.ID);
                final Result rs = GUI.getCon().select(sb.build()).get(0);
                
                
                tax.setId(rs.getInteger(Column.ID));
                tax.setCompanyID(rs.getInteger(Column.companyID));
                tax.setTaxTypeID(rs.getInteger(Column.taxTypeID));
                tax.setClaim(rs.getInteger(Column.claim));
                tax.setUnderAmount(rs.getCurrency(Column.underAmount));
                tax.setTax(rs.getCurrency(Column.tax));
//            } catch (Exception e) {
//                throw new DataException("id: " + id, e);
//            }
        }
    }

    public boolean delete() throws DataException {
        boolean result = DatabaseHelper.delete(getId(), "taxes");
        if (result) {
//reset id to -1
            setId(-1);
        }
        return result;
    }

    public String[][] getNVPairs() {
        String[][] nV = new String[6][2];

        nV[0][0] = "id";
        nV[0][1] = "" + getId() + "";
        nV[1][0] = "companyID";
        nV[1][1] = "" + getCompanyID() + "";
        nV[2][0] = "taxTypeID";
        nV[2][1] = "" + getTaxTypeID() + "";
        nV[3][0] = "claim";
        nV[3][1] = "" + getClaim() + "";
        nV[4][0] = "underAmount";
        nV[4][1] = "" + getUnderAmount() + "";
        nV[5][0] = "tax";
        nV[5][1] = "" + getTax() + "";
        return nV;
    }

    public void update() throws DataException {
        //boolean result = true;
//        try {
            final Where where = new Where(Column.companyID, getCompanyID())
                    .and(Column.taxTypeID, getTaxTypeID())
                    .and(Column.claim, getClaim())
                    .and(Column.underAmount, getUnderAmount())
                    .and(Column.tax, getTax());
            
            //PreparedStatement pstmt;
            if (getId() < 1) {//insert

                //pstmt = GUI.getCon().GetAPreparedStatement("INSERT INTO taxes(companyID, taxTypeID, claim, underAmount, tax) VALUES(?, ?, ?, ?, ?)");
                int id = GUI.getCon().insert(Table.taxes, where);
                setId(id);
            } else {//update

//                pstmt = GUI.getCon().GetAPreparedStatement("UPDATE taxes SET companyID = ?, taxTypeID = ?, claim = ?, underAmount = ?, tax = ? WHERE id = ?");
//                pstmt.setInt(6, getId());
                GUI.getCon().update(Table.taxes, where, new Where(Column.ID, getId()));
            }
//            pstmt.setInt(1, getCompanyID());
//            pstmt.setInt(2, getTaxTypeID());
//            pstmt.setInt(3, getClaim());
//            pstmt.setDouble(4, getUnderAmount());
//            pstmt.setDouble(5, getTax());

            //pstmt.executeUpdate();

//            if (getId() < 1) {//it was an insert
//
////                ResultSet rs = GUI.getCon().GetANewStatement().executeQuery("select LAST_INSERT_ID()");
////                rs.absolute(1);
////                setId(rs.getInt(1));
//                
//                setId(GUI.getCon().getLastInsertId());
//            }


//        } catch (Exception e) {
//            throw new DataException(e);
//            //result = false;
//        }
        //return result;
    }

    public void insertIfUnique() throws DataException {
        //boolean result = false;
        try {
            PreparedStatement pstmt;
            pstmt = GUI.getCon().GetAPreparedStatement("select id from taxes where "
                    + "companyID = ?, taxTypeID = ?, claim = ?, underAmount = ?, tax = ? ");
            pstmt.setInt(1, getCompanyID());
            pstmt.setInt(2, getTaxTypeID());
            pstmt.setInt(3, getClaim());
            pstmt.setString(4, getUnderAmount().toString());
            pstmt.setString(5, getTax().toString());

            ResultSet rs = pstmt.executeQuery();
            
            final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.taxes)
                    .where(new Where(Column.companyID, getCompanyID())
                    .and(Column.taxTypeID, getTaxTypeID())
                    .and(Column.claim, getClaim())
                    .and(Column.underAmount, getUnderAmount())
                    .and(Column.tax, getTax()));

            if (!rs.next()) {
//no one is already in the db
                //result =
                update();
            }

        } catch (Exception e) {
            throw new DataException(e);
            //result = false;
        }
        //return result;
    }

    @Override
    public String toString() {
        String[][] nV = getNVPairs();
        String returnS = "";
        for (int i = 0; i < nV.length; i++) {
            returnS += nV[i][0] + " = " + nV[i][1] + " --- ";
        }
        returnS = returnS.substring(0, returnS.length() - 5);
        return returnS;
    }

    public static ArrayList<Tax> getAllTaxs() throws DataException {
        ArrayList<Tax> ret = new ArrayList<Tax>();
        ResultSet rs;
        //try {
            final SelectBuilder sb = new SelectBuilder().addValue(Column.ID).table(Table.taxes);
            final Results results = GUI.getCon().select(sb.build());
            //rs = GUI.getCon().GetANewStatement().executeQuery("select id from taxes");
            //while (rs.next()) {
            for(final Result result : results){
                ret.add(Tax.getNewInstance(result.getInteger(Column.ID)));
            }
//        } catch (SQLException ex) {
//            throw new DataException(ex);
//        }
        return ret;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public static ArrayList<Tax> getTaxsByCompanyID(int companyID) throws DataException {

        ArrayList<Tax> ourList = new ArrayList<Tax>();

        try {
            PreparedStatement stmt = GUI.getCon().GetConnection().prepareStatement("select * from taxes where companyID = ? order by id ");
            stmt.setInt(1, companyID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ourList.add(Tax.getNewInstance(rs.getInt("id")));
            }
        } catch (Exception e) {
            throw new DataException("Tax_Base.getTaxsByCompanyID(" + companyID + ")", e);
        }
        return ourList;
    }

    public int getTaxTypeID() {
        return taxTypeID;
    }

    public void setTaxTypeID(int taxTypeID) {
        this.taxTypeID = taxTypeID;
    }

    public static ArrayList<Tax> getTaxsByTaxTypeID(int taxTypeID) throws DataException {

        ArrayList<Tax> ourList = new ArrayList<Tax>();

        try {
            PreparedStatement stmt = GUI.getCon().GetConnection().prepareStatement("select * from taxes where taxTypeID = ? order by id ");
            stmt.setInt(1, taxTypeID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ourList.add(Tax.getNewInstance(rs.getInt("id")));
            }
        } catch (Exception e) {
            throw new DataException("Tax_Base.getTaxsByTaxTypeID(" + taxTypeID + ")", e);
        }
        return ourList;
    }

    public int getClaim() {
        return claim;
    }

    public void setClaim(int claim) {
        this.claim = claim;
    }

    public static ArrayList<Tax> getAllTaxsByClaimOrderById(int claim) throws DataException {

        ArrayList<Tax> ourList = new ArrayList<Tax>();

        try {
            PreparedStatement stmt = GUI.getCon().GetConnection().prepareStatement("select id from taxes where claim = ? order by id");
            stmt.setInt(1, claim);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ourList.add(Tax.getNewInstance(rs.getInt(1)));
            }
        } catch (Exception e) {
            throw new DataException("getAllTax_BasesByClaimOrderById(" + claim + ")", e);
        }
        return ourList;
    }

    public Currency getUnderAmount() {
        return underAmount;
    }

    public void setUnderAmount(Currency underAmount) {
        this.underAmount = underAmount;
    }

    public static ArrayList<Tax> getAllTaxsByUnderAmountOrderById(double underAmount) throws DataException {

        ArrayList<Tax> ourList = new ArrayList<Tax>();

        try {
            PreparedStatement stmt = GUI.getCon().GetConnection().prepareStatement("select id from taxes where underAmount = ? order by id");
            stmt.setDouble(1, underAmount);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ourList.add(Tax.getNewInstance(rs.getInt(1)));
            }
        } catch (Exception e) {
            throw new DataException("getAllTax_BasesByUnderAmountOrderById(" + underAmount + ")", e);
        }
        return ourList;
    }

    public Currency getTax() {
        return tax;
    }

    public void setTax(Currency tax) {
        this.tax = tax;
    }

    public static ArrayList<Tax> getAllTaxsByTaxOrderById(double tax) throws DataException {

        ArrayList<Tax> ourList = new ArrayList<Tax>();

        try {
            PreparedStatement stmt = GUI.getCon().GetConnection().prepareStatement("select id from taxes where tax = ? order by id");
            stmt.setDouble(1, tax);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ourList.add(Tax.getNewInstance(rs.getInt(1)));
            }
        } catch (Exception e) {
            throw new DataException("getAllTax_BasesByTaxOrderById(" + tax + ")", e);
        }
        return ourList;
    }
}