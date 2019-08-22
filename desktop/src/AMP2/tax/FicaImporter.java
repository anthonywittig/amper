/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AMP2.tax;

import AMP2.BankStuff.Currency;
import AMP2.DatabaseStuff.Tax;
import AMP2.DatabaseStuff.db.DataException;
import AMP2.MainDisplay.GUI;
import AMP2.MainDisplay.TaxManager;
import AMP2.MainDisplay.UserReadableMessage;
import AMP2.Payroll.FicaEng;
import AMP2.Util.MiscStuff;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFileChooser;

public class FicaImporter {
    
    private final boolean singleData;
    private final int companyId;
    
    public FicaImporter(TaxManager taxManager, boolean singleData) throws Exception{
        
        this.singleData = singleData;
        this.companyId = taxManager.getCompanyID();
        
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(taxManager);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try{
                importData(file);
            }catch(Exception e){
                throw new DataException(e).setUserReadableMessage("Problem importing the data.");
            }
        }
        
    }
    
    private void importData(File file) throws Exception{
        Scanner lines = new Scanner(file);
        List<FicaData> ficaDatas = new ArrayList<FicaData>();
        
        while(lines.hasNextLine()){
            //assume format is:
            //[$]atLeast [$]lessThan [$]claiming0 ... [$]claimingk
            
            String fullLine = lines.nextLine();
            String[] line = fullLine
                    .trim()
                    .replaceAll("\\$", "")
                    .replaceAll(",", "")
                    .replaceAll("\\t", " ")
                    .split(" ");
            
            if(line.length == 1){
                continue;
            }
            
            int lessThan = Integer.parseInt(line[1]);
            int marriedOffset = singleData ? 0 : 10;
            for(int index = 2; index < line.length; ++index){
                int claim = index - 2 + marriedOffset;
                if(9 < claim && singleData){
                    //can't input anything over 9 for singles
                    continue;
                }
                int amount = Integer.parseInt(line[index]);
                
                FicaDataBuilder builder = new FicaDataBuilder();
                builder.withAmount(amount);
                builder.withClaim(claim);
                builder.withLessThan(lessThan);
                
                ficaDatas.add(builder.build());
                MiscStuff.writeToLog("add fica for less than: " + lessThan + ", claim: " + claim + ", ammount: " + amount);
            }
        }
        
        importData(ficaDatas);
    }

    private void importData(List<FicaData> ficaDatas) {
        for(FicaData ficaData : ficaDatas){
            Tax tax = new Tax();

            tax.setClaim(ficaData.getClaim());
            tax.setCompanyID(companyId);
            tax.setTaxTypeID(FicaEng.TaxType);
            tax.setUnderAmount(new Currency(ficaData.getLessThan()));
            tax.setTax(new Currency(ficaData.getAmount()));
            
            try{
                tax.update();
            }catch(DataException de){
                GUI.showFatalMessageDialog(de);
            }
        }
        MiscStuff.writeToLog("done importing fica data");
        
    }
    
    private static interface FicaData{
        int getLessThan();
        int getClaim();
        int getAmount();
    }
    
    private static class FicaDataBuilder{
        private int lessThan, claim, amount;
        
        FicaDataBuilder withLessThan(int lessThan){
            this.lessThan = lessThan;
            return this;
        }
        
        FicaDataBuilder withClaim(int claim){
            this.claim = claim;
            return this;
        }
        
        FicaDataBuilder withAmount(int amount){
            this.amount = amount;
            return this;
        }
        
        FicaData build(){
            return new FicaData(){

                @Override
                public int getLessThan() {
                    return lessThan;
                }

                @Override
                public int getClaim() {
                    return claim;
                }

                @Override
                public int getAmount() {
                    return amount;
                }
                
            };
        }
    }
    
}
