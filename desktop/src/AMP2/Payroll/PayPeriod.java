package AMP2.Payroll;

import AMP2.BankStuff.Currency;
import AMP2.DatabaseStuff.Tax;
import AMP2.DatabaseStuff.db.DataException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Andy
 */
public class PayPeriod {

    protected int companyID = -1;
    private PayDates date;
    private final List<Adjustment> adjustments;
    private Currency hours;
    private Currency rate, grossPay, sS, med, fica, orWith, netPay;
    private int claim; // two digets first for single or married 0/1 and 
    // then what we claim 0-2, single claiming 2 = 02.

    public static PayPeriod getNewInstance(int claim, PayDates date, 
            Currency rate, Currency hours, int companyID,
            final List<Adjustment> adjustments) throws DataException {

        final PayPeriod payP = new PayPeriod(adjustments);

        //some of the setters fire off a "figure()"
        payP.claim = claim;
        payP.date = date;
        payP.rate = rate;
        payP.hours = hours;
        payP.setCompanyID(companyID);

        payP.figure();

        return payP;
    }
    
    private PayPeriod(final List<Adjustment> adjustments){
        this.adjustments = adjustments;
    }

    /**
     * A method that figures our net Pay.
     */
    public void doNetPay() {
        final Currency adjustmentsC = getAdjustmentsTotal();
        netPay = grossPay.subtract(sS).subtract(med)
                .subtract(fica).subtract(orWith).subtract(adjustmentsC);
    }

    /**
     * A method that figures out what all our data should be. Should be called
     * right off the bat.
     */
    public void figure() throws DataException {
        
        grossPay = rate.multiply(hours).round();
        sS = grossPay.multiply(Tax.getSs(companyID)).round();
        med = grossPay.multiply(Tax.getMed(companyID)).round();

        orWith = OrWithEng.getOrWith(claim, grossPay, getCompanyID()).round(); //for married too.
        fica = FicaEng.getFica(claim, grossPay, getCompanyID()).round();

        doNetPay();
    }

    /**
     * A method that gets our date.
     * 
     * @return date, our date.
     */
    public PayDates getDate() {
        return date;
    }

    /**
     * A method that gets our claim.
     * 
     * @return claim, our claim.
     */
    public int getClaim() {
        return claim;
    }

    /**
     * A method that gets our fica.
     * 
     * @return fica, our fica tax.
     */
    public Currency getFica() {
        return fica;
    }

    /**
     * A method that gets our gross pay.
     * 
     * @return grossWage, our gross pay.
     */
    public Currency getGrossPay() {
        //return Math2.round(grossPay);   
        return grossPay;
    }

    /**
     * A method that gets our hours.
     * 
     * @return hours, our hours.
     */
    public Currency getHours() {
        return hours;
    }

    /**
     * A method that gets our medicare tax.
     * 
     * @return med, our medicare tax.
     */
    public Currency getMed() {
        //return Math2.round(med);   
        return med;
    }

    /**
     * A method that gets our net pay.
     * 
     * @return netPay, our net pay.
     */
    public Currency getNetPay() {
        return netPay;
    }

    /**
     * A method that gets our oregon withholding sp?
     * 
     * @return orWith, the oregon withholding.
     */
    public Currency getOrWith() {
        return orWith;
    }

    /**
     * A method that gets our rate.
     * 
     * @return rate, our rate.
     */
    public Currency getRate() {
        return rate;
    }

    /**
     * A method that gets our social security tax.
     * 
     * @return sS, our social security tax.
     */
    public Currency getSS() {
        //return Math2.round(sS);   
        return sS;
    }

    /**
     * A method that changes what we claim.
     * 
     * @param claim, what we claim.
     */
    public void setClaim(int claim) throws DataException {
        this.claim = claim;
        figure();
    }

    /**
     * A method that sets our date.
     * 
     * @param date, our new date.
     */
    public void setDate(PayDates date) {
        this.date = date;
    }

    /**
     * A method that sets our fica.
     * 
     * @param fica, our new fica.
     */
    public void setFica(Currency fica) {
        this.fica = fica;
        doNetPay();
    }
    
    public List<Adjustment> getAdjustments(){
        return Collections.unmodifiableList(adjustments);
    }
    
    public boolean removeAdjustment(final Adjustment adjustment){
        return adjustments.remove(adjustment);
    }

    /**
     * A method that sets our hours.
     * 
     * @param hours, the new hours.
     */
    public void setHours(Currency hours) throws DataException {
        this.hours = hours;
        figure();
    }

    /**
     * A method that sets our orWith in case our data is incomplete.
     * 
     * @param orWith, the or withholding.
     */
    public void setOrWith(Currency orWith) {
        this.orWith = orWith;
        doNetPay();
    }

    /**
     * A method that sets our rate.
     * 
     * @param rate, our new rate.
     */
    public void setRate(Currency rate) throws DataException {
        this.rate = rate;
        figure();
    }

    @Override
    public String toString() {
        return "PayPeriod{" + "companyID=" + companyID + ", date=" + date + ", adjustments=" + adjustments + ", hours=" + hours + ", rate=" + rate + ", grossPay=" + grossPay + ", sS=" + sS + ", med=" + med + ", fica=" + fica + ", orWith=" + orWith + ", netPay=" + netPay + ", claim=" + claim + '}';
    }

    /**
     * Get the value of companyID
     *
     * @return the value of companyID
     */
    public int getCompanyID() {
        return companyID;
    }

    /**
     * Set the value of companyID
     *
     * @param companyID new value of companyID
     */
    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    //just changed to clone2 to see if this is really used, if not, remove it
    public PayPeriod clone2() {
        final List<Adjustment> newAdjustments = new ArrayList<Adjustment>(adjustments);
        final PayPeriod copy = new PayPeriod(newAdjustments);
        copy.companyID = this.companyID;
        copy.date = this.date;
        copy.hours = this.hours.clone();
        copy.grossPay = this.grossPay.clone();
        copy.sS = this.sS.clone();
        copy.med = this.med.clone();
        copy.fica = this.fica.clone();
        copy.orWith = this.orWith.clone();
        copy.netPay = this.netPay.clone();
        copy.claim = this.claim;

        return copy;

    }

    public void addAdjustment(Adjustment adjustment) {
        adjustments.add(adjustment);
    }

    public Currency getAdjustmentsTotal() {
        Currency total = Currency.Zero;
        for(final Adjustment adjustment : adjustments){
            total = total.add(adjustment.getAmount());
        }
        
        return total;
    }
}
