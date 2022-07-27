package oscarblancarte.ipd.templetemethod;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import oscarblancarte.ipd.templetemethod.impl.DrugstoreFileProcess;
import oscarblancarte.ipd.templetemethod.impl.GroceryFileProcess;
import oscarblancarte.ipd.templetemethod.impl.XmlFileProcess;

/**
 * @author Oscar Javier Blancarte Iturralde
 * @see <a href="http://www.oscarblancarteblog.com">...</a>
 */
public class TempleteMethodMain extends TimerTask {
    
    private static final String[] PATHS = 
        new String[]{"files\\xml"};
    private static final String LOG_DIR = "logs";
    private static final String PROCESS_DIR = "process";

    public static void main(String[] args) {
        new TempleteMethodMain().start();
    }

    public void start() {
        try {
            Timer timer = new Timer();
            timer.schedule(this, new Date(), (long) 1000 * 10);
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("> Monitoring start");
        File f = new File(PATHS[0]);
        if(!f.exists()){
            throw new RuntimeException("El path '"+PATHS[0]+"' no existe");
        }
        System.out.println("> Read Path " + PATHS[0]);
        File[] xmlFiles = f.listFiles();
        for(File file: xmlFiles){
            try {
                System.out.println("> File found > " + file.getName());
                new XmlFileProcess(file,LOG_DIR,PROCESS_DIR).execute();
                System.out.println("Processed file > " + file.getName());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}