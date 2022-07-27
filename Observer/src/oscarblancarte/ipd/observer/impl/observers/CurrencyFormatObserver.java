package oscarblancarte.ipd.observer.impl.observers;

import oscarblancarte.ipd.observer.impl.ConfigurationManager;
import oscarblancarte.ipd.observer.impl.IObserver;

public class CurrencyFormatObserver implements IObserver {

    @Override
    public void notifyObserver(String command, Object source) {
        if(command.equals("coinType")){
            ConfigurationManager conf = (ConfigurationManager)source;
            System.out.println("Observer ==> CurrencyFormat.currencyTypeChange > "
                    + conf.getCoin());
        }
    }
}
