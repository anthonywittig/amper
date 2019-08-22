package AMP2.Payroll;

import AMP2.BankStuff.Currency;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.DatabaseStuff.*;
import AMP2.Days.ControllerG;
import java.util.ArrayList;
import java.util.List;


public class TaxHandler
{
    private List<PayPeriod> payPeriods;
    private List<PTaxes> months;
    private List<Employee> employees;
    private final ControllerG controllerG;
    
    /**
     * Constructor for objects of class TaxHandler
     * 
     * @param employees, the employees that general taxes will be done on.
     */
    private TaxHandler(ControllerG controllerG) {
        this.controllerG = controllerG;
    }
    
    public static TaxHandler getNewInstance(List<Employee> employees, ControllerG controllerG) throws DataException{
        
        final TaxHandler tax = new TaxHandler(controllerG);
        tax.months = new ArrayList<PTaxes>();
        tax.payPeriods = new ArrayList<PayPeriod>();
        tax.employees = employees;
        
        tax.fillData();
        
        return tax;
    }
    
    public void fillData() throws DataException {
        
        List<List<PayPeriod>> payPeriodsByMonth = new ArrayList<List<PayPeriod>>();
        
        //every month gets an array list
        for(int i = 0; i < 12; i++) {
            payPeriodsByMonth.add(new ArrayList<PayPeriod>());   
        }
        
        //get all of the payperiods loaded into payperiods
        for(Employee emp : employees){ 
            List<PayPeriod> pPs = emp.getPayPeriods();
            
            for(PayPeriod pP : pPs){
                payPeriods.add(pP);
            }
        }
        for(PayPeriod pP : payPeriods){
            //need to add to the right month, i.e. if it is the first section
            //we need to add it to it's month, otherwise we need to add it
            //to the next month:
            int month = pP.getDate().getMonthI() - 1;
            if(pP.getDate().getSectionI() == 2){
                month++;
            }
            
            //the very last payPeriod goes on the next year, don't record it.
            //the very last payPeriod will be in month 12 (note that we are 0 indexed)
            if(month < 12){
                List<PayPeriod> array = payPeriodsByMonth.get(month);
                array.add(pP);
            } 
        }
        
        //we need to add in the last pay periods from last year into our first
        //month:
        List<PayPeriod> otherPayPeriods = DatabaseHelper.GetLastPayPeriodFromYearBefore(DatabaseHelper.GetLastUsedCompanyID());
        for(PayPeriod pP : otherPayPeriods){
            List<PayPeriod> array = payPeriodsByMonth.get(0);//we want to add these to January
            array.add(pP);
        }
        
        
        //add them all up
        for(int monthNumber = 0; monthNumber < payPeriodsByMonth.size(); ++monthNumber){
            List<PayPeriod> month = payPeriodsByMonth.get(monthNumber);
            Currency grossWage = Currency.Zero;
            Currency fica = Currency.Zero;
            Currency hours = Currency.Zero;
            Currency orWith = Currency.Zero;
            
            int companyID = -1;
            
            for(PayPeriod pP : month){
                grossWage = grossWage.add(pP.getGrossPay());
                fica = fica.add(pP.getFica());
                hours = hours.add(pP.getHours()); 
                orWith = orWith.add(pP.getOrWith());
                
                companyID = pP.getCompanyID();
            }
            
            months.add(new PTaxes(companyID, grossWage, fica, hours, orWith, controllerG.getMonthTotals().get(monthNumber)));
        }
    }
    
    /**
     * A method that returns our months PTaxes object.
     * 
     * @param m, the number of the desired month.
     * 
     * @return pT, the PTaxes object related to our month, 
     *             null if no data was available.
     */
    public PTaxes getMonth(int m) {
        PTaxes pT = months.get(m);
        return pT;
    }
    
    public static List<PayrollStats> sortAndSumPayrollStatsByMonth(List<Employee> employees){
        
        //Should be in the human resources class....
        //Employee employeeT = new Employee("", "", "", 0, 0.0);
        final List<PayPeriod> allPayPeriods = new ArrayList<PayPeriod>();
        final List<PayrollStats> sort = new ArrayList<PayrollStats>();

        //get all the payperiods into allPayPeriods.
        for (final Employee emp : employees){//int i = 0; i < employees.size(); i++) {
            //Employee emp = (Employee) employees.get(i);
            final List<PayPeriod> payPeriods = emp.getPayPeriods();

            for (PayPeriod payPeriod : payPeriods) {
                allPayPeriods.add(payPeriod);
            }
        }

        for (PayPeriod pP : allPayPeriods) {
            //arrange according to date and place in sort
            boolean added = false;
            for (final PayrollStats dateSet : sort){
                
                PayDates pD = dateSet.getDate();

                if (pD.toString().equals(pP.getDate().toString())) {
                    dateSet.setHours(dateSet.getHours().add(pP.getHours()));
                    dateSet.setGrossPay(dateSet.getGrossPay().add(pP.getGrossPay()));
                    dateSet.setSs(dateSet.getSs().add(pP.getSS()));
                    dateSet.setMed(dateSet.getMed().add(pP.getMed()));
                    dateSet.setFica(dateSet.getFica().add(pP.getFica()));
                    dateSet.setOrWith(dateSet.getOrWith().add(pP.getOrWith()));
                    dateSet.setNetPay(dateSet.getNetPay().add(pP.getNetPay()));
                    dateSet.setAdjustment(dateSet.getAdjustment().add(pP.getAdjustmentsTotal()));
                    added = true;
                }
            }
            
            if (!added) {
                final PayrollStats.PayrollStatsBuilder payStatsBuilder = new PayrollStats.PayrollStatsBuilder();
                payStatsBuilder.setDate(pP.getDate());
                payStatsBuilder.setHours(pP.getHours());
                payStatsBuilder.setGrossPay(pP.getGrossPay());
                payStatsBuilder.setSs(pP.getSS());
                payStatsBuilder.setMed(pP.getMed());
                payStatsBuilder.setFica(pP.getFica());
                payStatsBuilder.setOrWith(pP.getOrWith());
                payStatsBuilder.setNetPay(pP.getNetPay());
                payStatsBuilder.setAdjustment(pP.getAdjustmentsTotal());

                final PayrollStats payStats = payStatsBuilder.build();
                
                boolean finished = false;

                for (int k = 0; k < sort.size() && !finished; k++) {
                    PayDates pD1 = pP.getDate();
                    PayDates pD2 = sort.get(k).getDate();

                    if (!finished) {
                        if (pD1.getYear() < pD2.getYear()) {
                            sort.add(k, payStats);

                            finished = true;
                        }
                    }
                    if (!finished) {
                        if (pD1.getMonthI() < pD2.getMonthI()) {
                            sort.add(k, payStats);

                            finished = true;
                        }
                    }
                    if (!finished) {
                        if (pD1.getMonthI() == pD2.getMonthI()) {
                            if (pD1.getSectionI() < pD2.getSectionI()) {
                                sort.add(k, payStats);

                                finished = true;
                            }
                        }
                    }

                }
                if (!finished) {
                    sort.add(payStats);
                }
            }
        }
        
        return sort;

    }
}

