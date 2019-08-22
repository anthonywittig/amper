package AMP2.Payroll;

import AMP2.BankStuff.Currency;
import java.util.ArrayList;
import java.io.*;
import java.util.List;

public class HumanResources implements Serializable 
{
    private String store;
    private List<Employee> employees;

    /**
     * Constructor for objects of class HumanResources
     */
    public HumanResources()
    {
        store = "";
        employees = new ArrayList<Employee>();
    }

    /**
     * Constructor for testing.
     */
    public HumanResources(int i) {
        store = "Time Out";
        employees = new ArrayList<Employee>();
        
        Employee e1 = new Employee();
        employees.add(e1);
        e1 = new Employee("Rachel", "342-32-1343", "teNe", 0, new Currency("7.25"));
        employees.add(e1);
    }
    
    /**
     * Constructor for objects of class HumanResources
     */
    public HumanResources(String store)
    {
        this.store = store;
        employees = new ArrayList<Employee>();
    }
    
    /**
     * recalculates payroll info on the employees
     */
    public void recalculatePayroll(){
        for(final Employee emp : employees){
            emp.recalculatePayroll();
        }
    }
    
    /**
     * A method that adds an employee to our list.
     * 
     * @param employee, an employee to add to our list.
     */
    public void addEmployee(Employee employee) {
        employees.add(employee);   
    }
    
    /**
     * A method that gets an employee by their name.
     * 
     * @param name, the desired employees name.
     * 
     * @return emp, our employee,
     *              null if not found.
     */
    public Employee getEmployee(String name) {
        
        for(int i = 0; i < employees.size(); i++) {
            Employee emp = employees.get(i);
            
            if(emp.getName().equals(name))
                return emp; // found.
        }
        return null; // not found...
    }
    
    /**
     * A method that gets our employees arraylist
     * 
     * @return employees, our array of employees.
     */
    public List<Employee> getEmployees() {
        return employees;   
    }
    
    /**
     * A method that gets our store name
     * 
     * @return store, our store name.
     */
    public String getStore() {
        return store;   
    }
     
    /**
     * A method that sets our store name.
     * 
     * @param store, our new store name.
     */ 
    public void setStore(String store) {
        this.store = store;   
    }
     
    /**
     * A method that removes an employee.
     * 
     * @param emp, the employee to remove.
     */ 
    public void removeEmp(Employee emp) {
        
        //for(int i = 0; i < employees.size(); i++) {
          //  Employee employee = (Employee) employees.get(i); 
            
            //if(emp.getName().equals(employee.getName())) {
              //  employees.remove(employee);   
            //}   
        //}
        employees.remove(emp);
    }
     
    /**
     * our to String.
     * 
     * @return str, the string representation of our employees.
     */
    public String toString() {
        String str = "";
        
        for(int i = 0; i < employees.size(); i++) {
            Employee e = employees.get(i);
            str += e + "\n";
        }
        return str;
    }
}
