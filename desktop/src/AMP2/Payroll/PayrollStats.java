/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.Payroll;

import AMP2.BankStuff.Currency;
import AMP2.Util.BuilderToVerifyNotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * @author awittig
 */
public class PayrollStats {

    
    private PayDates date;
    @BuilderToVerifyNotNull
    private Currency hours;
    @BuilderToVerifyNotNull
    private Currency grossPay;
    @BuilderToVerifyNotNull
    private Currency ss;
    @BuilderToVerifyNotNull
    private Currency med;
    @BuilderToVerifyNotNull
    private Currency fica;
    @BuilderToVerifyNotNull
    private Currency orWith;
    @BuilderToVerifyNotNull
    private Currency netPay;
    @BuilderToVerifyNotNull
    private Currency adjustment;

    private PayrollStats() {
    }

    public PayDates getDate() {
        return date;
    }

    public void setDate(PayDates date) {
        this.date = date;
    }

    public Currency getHours() {
        return hours;
    }

    public void setHours(Currency hours) {
        this.hours = hours;
    }

    public Currency getGrossPay() {
        return grossPay;
    }

    public void setGrossPay(Currency grossPay) {
        this.grossPay = grossPay;
    }

    public Currency getSs() {
        return ss;
    }

    public void setSs(Currency ss) {
        this.ss = ss;
    }

    public Currency getMed() {
        return med;
    }

    public void setMed(Currency med) {
        this.med = med;
    }

    public Currency getFica() {
        return fica;
    }

    public void setFica(Currency fica) {
        this.fica = fica;
    }

    public Currency getOrWith() {
        return orWith;
    }

    public void setOrWith(Currency orWith) {
        this.orWith = orWith;
    }

    public Currency getNetPay() {
        return netPay;
    }

    public void setNetPay(Currency netPay) {
        this.netPay = netPay;
    }

    public Currency getAdjustment() {
        return adjustment;
    }

    public void setAdjustment(Currency adjustment) {
        this.adjustment = adjustment;
    }

    

    public static class PayrollStatsBuilder {

        private final PayrollStats payrollStats = new PayrollStats();

        PayrollStats build() {
            for(final Field field : PayrollStats.class.getDeclaredFields()){
                final BuilderToVerifyNotNull builderToVerifyNotNullAnnotation = field.getAnnotation(BuilderToVerifyNotNull.class);
                if(null != builderToVerifyNotNullAnnotation){
                    final String methodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                    
                    try{
                        final Method getter = PayrollStats.class.getMethod(methodName);
                        
                        if(getter.invoke(payrollStats) == null){
                            throw new RuntimeException("got a null on a BuilderToVerifyNotNull. Field name: "  + field.getName()
                                    + ", method name: " + methodName);
                        }
                    }catch(Exception e){
                        throw new RuntimeException(e);
                    }
                }
            }
            
            return payrollStats;
            
        }

        PayrollStatsBuilder setDate(PayDates date) {
            payrollStats.setDate(date);
            return this;
        }

        PayrollStatsBuilder setHours(Currency hours) {
            payrollStats.setHours(hours);
            return this;
        }

        PayrollStatsBuilder setGrossPay(Currency grossPay) {
            payrollStats.setGrossPay(grossPay);
            return this;
        }

        PayrollStatsBuilder setSs(Currency sS) {
            payrollStats.setSs(sS);
            return this;
        }

        PayrollStatsBuilder setMed(Currency med) {
            payrollStats.setMed(med);
            return this;
        }

        PayrollStatsBuilder setFica(Currency fica) {
            payrollStats.setFica(fica);
            return this;
        }

        PayrollStatsBuilder setOrWith(Currency orWith) {
            payrollStats.setOrWith(orWith);
            return this;
        }

        PayrollStatsBuilder setNetPay(Currency netPay) {
            payrollStats.setNetPay(netPay);
            return this;
        }
        
        PayrollStatsBuilder setAdjustment(Currency adjustment){
            payrollStats.setAdjustment(adjustment);
            return this;
        }
    }
}
