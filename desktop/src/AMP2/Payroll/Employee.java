package AMP2.Payroll;

import AMP2.BankStuff.Currency;
import AMP2.BankStuff.Math2;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.MainDisplay.GUI;
import java.util.ArrayList;
import java.io.*;
import java.util.List;

public class Employee implements Serializable {

    private String name, socS, address;
    private int claim;
    private Currency currentRate = Currency.Zero;
    private Currency grossPayT = Currency.Zero;
    private Currency sST = Currency.Zero;
    private Currency medT = Currency.Zero;
    private Currency ficaT = Currency.Zero;
    private Currency orWithT = Currency.Zero;
    private Currency netPayT = Currency.Zero;
    private List<PayPeriod> payPeriods;
    private boolean isCurrentEmp;
    private Currency hoursT = Currency.Zero;
    private Currency adjT = Currency.Zero;

    /**
     * Constructor for objects of class Employee
     */
    public Employee(String name, String socS, String address, int claim,
            Currency currentRate) {
        this.name = name;
        this.socS = socS;
        this.address = address;
        this.claim = claim;
        this.currentRate = currentRate;
        isCurrentEmp = true;
        payPeriods = new ArrayList<PayPeriod>();
    }

    /**
     * constructor for testing...
     */
    public Employee() {
//        this("Joe", "123-45-6789", "51450 Hwy 97 LaPine OR 97739", 0, 7.25);
//        
//        PayDates pd1 = new PayDates(3, 2, "05");
//        PayPeriod pp1 = new PayPeriod(claim, pd1, 10.00, 45);
//        payPeriods.add(pp1);
//        PayDates pd2 = new PayDates(4, 1, "2005");
//        PayPeriod pp2 = new PayPeriod(claim, pd2, 10.00, 55.25);
//        payPeriods.add(pp2);
//        figureT();
    }

    /**
     * recalculates our payroll numbers
     */
    public void recalculatePayroll() {
        figureT(true);
    }

    /**
     * A method that adds a pay period to our array.
     *
     * @param pP, the payPeriod to add.
     */
    public void addPayPeriod(PayPeriod pP) {
        PayDates pD1 = pP.getDate();

        for (int i = 0; i < payPeriods.size(); i++) {
            PayPeriod payP = (PayPeriod) payPeriods.get(i);
            PayDates pD2 = payP.getDate();

            if (pD1.getYear() < pD2.getYear()) {
                payPeriods.add(i, pP);

                return;
            }
            if (pD1.getMonthI() < pD2.getMonthI()) {
                payPeriods.add(i, pP);

                return;
            }
            if (pD1.getMonthI() == pD2.getMonthI()) {
                if (pD1.getSectionI() < pD2.getSectionI()) {
                    payPeriods.add(i, pP);

                    return;
                }
                if (pD1.getSectionI() == pD2.getSectionI()) {
                    payPeriods.add(i, pP);
                    payPeriods.remove(payP);

                    return;
                }
            }
        }

        payPeriods.add(pP);

        figureT(true);
    }

    /**
     * A method that figures our totals for our payPeriods.
     */
    private void figureT() {
        figureT(false);
    }

    private void figureT(boolean forceRecalculation) {

        try {

            //only figure if needed
            if (forceRecalculation || hoursT.equals(Currency.Zero)) {

                hoursT = Currency.Zero;
                grossPayT = new Currency("0");
                sST = new Currency("0");
                medT = new Currency("0");
                ficaT = new Currency("0");
                orWithT = new Currency("0");
                netPayT = new Currency("0");
                adjT = Currency.Zero;

                for (int i = 0; i < payPeriods.size(); i++) {
                    final PayPeriod pP = (PayPeriod) payPeriods.get(i);
                    pP.figure();

                    currentRate = pP.getRate();
                    hoursT = hoursT.add(pP.getHours());
                    grossPayT = grossPayT.add(pP.getGrossPay());
                    sST = sST.add(pP.getSS());
                    medT = medT.add(pP.getMed());
                    ficaT = ficaT.add(pP.getFica());
                    orWithT = orWithT.add(pP.getOrWith());
                    netPayT = netPayT.add(pP.getNetPay());
                    adjT = adjT.add(pP.getAdjustmentsTotal());
                }
            }
        } catch (DataException e) {
            GUI.showFatalMessageDialog(e);
        }
    }

    /**
     * A method that returns our address.
     *
     * @return address, our address
     */
    public String getAddress() {
        return address;
    }

    /**
     * A method that returns our claim.
     *
     * @return claim, our claim.
     */
    public int getClaim() {
        return claim;
    }

    /**
     * A method that gets our current Rate, (The current rate is set with the
     * rateT, it is much like it except it doesn't depend on the figureT() to be
     * called to be set...)
     *
     * @return currentRate our current rate.
     */
    public Currency getCurrentRate() {
        figureT();
        return currentRate;
    }

    /**
     * A method that gets our fica.
     *
     * @return fica, our fica tax.
     */
    public Currency getFicaT() {
        figureT();
        return ficaT;
    }

    /**
     * A method that gets our gross pay.
     *
     * @return grossWage, our gross pay.
     */
    public Currency getGrossPayT() {
        figureT();
        //return Math2.round(grossPayT);   
        return grossPayT;
    }

    /**
     * A method that gets our hours.
     *
     * @return hours, our hours.
     */
    public Currency getHoursT() {
        figureT();
        return Math2.round(hoursT);
    }

    /**
     * A method that gets our isCurrentEmp.
     *
     * @return isCurrentEmp, true if they are a current employeee, false if they
     * are not.
     */
    public boolean getIsCurrentEmp() {
        return isCurrentEmp;
    }

    /**
     * A method that gets our last pay period object.
     *
     * @return payP, our last pay period.
     */
    public PayPeriod getLastPayPeriod() {
        int i = payPeriods.size();

        if (i == 0) {
            return null;
        }

        return (PayPeriod) payPeriods.get(i - 1);
    }

    /**
     * A method that gets our medicare tax.
     *
     * @return med, our medicare tax.
     */
    public Currency getMedT() {
        figureT();
        //return Math2.round(medT);   
        return medT;
    }

    /**
     * A method that returns our name.
     *
     * @return name, our name.
     */
    public String getName() {
        return name;
    }

    /**
     * A method that gets our net pay.
     *
     * @return netPay, our net pay.
     */
    public Currency getNetPayT() {
        figureT();
        //return Math2.round(netPayT);   
        return netPayT;
    }

    /**
     * A method that gets our oregon withholding sp?
     *
     * @return orWith, the oregon withholding.
     */
    public Currency getOrWithT() {
        figureT();
        return orWithT;
    }

    /**
     * A method that gets our arraylist of pay periods.
     *
     * @return payPeriods, our array of pay periods.
     */
    public List<PayPeriod> getPayPeriods() {
        return payPeriods;
    }

    /**
     * A method that returns our Social Security number.
     *
     * @return socS, our social security.
     */
    public String getSocS() {
        return socS;
    }

    /**
     * A method that gets our social security tax.
     *
     * @return sS, our social security tax.
     */
    public Currency getSST() {
        figureT();
        //return Math2.round(sST);   
        return sST;
    }
    
    public Currency getAdjustmentsTotalT() {
        figureT();
        return adjT;
    }

    /**
     * A method that sets our address.
     *
     * @param address, our new address.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * A method that sets our claim.
     *
     * @param claim, our new claim.
     */
    public void setClaim(int claim) {
        this.claim = claim;
    }

    /**
     * A method that sets our currentRate, note, this is forced, not gathered as
     * it is in the figueT()
     *
     * @param currentRate, our new currentRate.
     */
    public void setCurrentRate(Currency currentRate) {
        this.currentRate = currentRate;
    }

    /**
     * A method that sets our isCurrentEmp.
     *
     * @param isCurrent, the new setting for our isCurrentEmp.
     */
    public void setIsCurrentEmp(boolean isCurrent) {
        isCurrentEmp = isCurrent;
    }

    /**
     * A method that sets our name.
     *
     * @param name, our new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * A method that sets our Social Security number.
     *
     * @param socS, our new Social Security number.
     */
    public void setSocS(String socS) {
        this.socS = socS;
    }

    /**
     * A to string for our array of PayPeriods
     *
     * @return str, the string representation of our pay Periods.
     */
    public String payPToString() {
        String str = new String();

        for (int i = 0; i < payPeriods.size(); i++) {
            PayPeriod pp = (PayPeriod) payPeriods.get(i);
            str += pp + "\n";
        }
        return str;
    }

    /**
     * A to string for our employee info.
     *
     * @return the employee info.
     */
    public String toString() {
        return name + " " + socS + " " + address + " " + claim;
    }
}
