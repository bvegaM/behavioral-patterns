# 

<a>
<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/b/b0/Logo_Universidad_Polit%C3%A9cnica_Salesiana_del_Ecuador.png/800px-Logo_Universidad_Polit%C3%A9cnica_Salesiana_del_Ecuador.png" alt="Ups logo" title="Aimeos" align="right" height="60" />
</a>

# Tarea de Patrones de comportamiento

**Integrante:**

* Bryam David Vega Moreno

**Repositorio:** ([GitHub - bvegaM/behavioral-patterns](https://github.com/bvegaM/behavioral-patterns.git))

-------------------

## Patron State

> Usando el patron State implementar un apagado seguro. Una vez establecido no permite enviar mas mensajes y el servidor no debe apagarse hasta que todos los mensajes que entraron (antes de establecer el estado) sean procesados. Una vez que el servidor se apague, cambiara su estado al de apagado.

### Solucion del problema

**Clase StopSafeServerState**

En esta clase se implementa la logica del apagadoSeguro, el problema es que actualmente tenemos una clase que se llama StopServerState que lo unico que hace es hacer un stop al server. Sin embargo, ahora lo que necesitamos es procesar todos los mensajes antes de realizar el apagado, para ello utilizamos la clase nueva llamada `StopSafeServerSatate` para realizar el proceso de apagado seguro. El mismo extiende de un AbstractServerState para obtener el metodo handleMessage.

```java
public class StopSafeServerState extends AbstractServerState{

    private Thread monitoringThread;

    public StopSafeServerState(Server server) {
        server.getMessageProcess().start();
        monitoringThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (server.getMessageProcess()
                                .countMessage() == 0) {
                            server.setState(
                                    new StopServerState(server));
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        monitoringThread.start();
    }

    @Override
    public void handleMessage(Server server, String message) {
        System.out.println("Can't send requests,the server making a safe stop with the remaining requests");
    }
}
```

Como podemos darnos cuenta el constructor realiza un proceso de lectura de mensajes en cola, con el objetivo de terminar de ejecutar todos los procesos encolados y despues se procede a cambiar el estado a apagado.

<div style="page-break-after: always"></div>

**Clase MessageProcess**

En esta clase se crea un nuevo metodo que lo unico que nos permite es validar si existen procesos en cola o no 

```java
public boolean emptyQueueMessage(){
        return messageQueue.size() == 0;
    }
```

<div style="page-break-after: always"></div>

**Clase ServerPanel**

En esta clase se modifica el metodo startAction y ahora se procede a preguntar si existen mensajes encolados, en caso de que si se procede a cambiar el estado a un estado de apagado seguro, el cual ejecutara la clase `StopSafeServerState` y procedera a ir procesando los mensajes que quedaron en cola para despues proceder con el apagado. Ademas de ello tambien se modifico el metodo sendMessageEvent para verificar que no pueda enviar mensajes mientras que el proceso de apagado seguro, se este ejecutando, con ello, el proceso queda de la siguiente manera.

```java
private void sendMessageEvent(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendMessageEvent
        // TODO add your handling code here:
        boolean stopSafe = server.getMessageProcess().emptyQueueMessage();
        if(stopSafe && server.getState() instanceof  StopSafeServerState)
            this.startAction(evt);
        server.handleMessage("Send Message + " + ++messageCouter);
    }//GEN-LAST:event_sendMessageEvent

    private void startAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startAction
        // TODO add your handling code here:
        AbstractServerState state = server.getState();
        if (state instanceof StopServerState) {
            btnStart.setText("Stop");
            server.setState(new StartingServerState(server));
        } else {
            if (state instanceof StartingServerState) {
                server.setState(new StopServerState(server));
            } else {
                boolean stopSafe = server.getMessageProcess().emptyQueueMessage();
                if(!stopSafe)
                    server.setState(new StopSafeServerState(server));
                else {
                    btnStart.setText("Start");
                    server.setState(new StopServerState(server));
                }

            }
        }
        
    }
```

<div style="page-break-after: always"></div>

**Resultados obtenidos**

Lo que se presenta a continuacion es un conjunto de mensajes de consola que simula el apagado seguro del servidor. A continuacion se presenta el restulado.

```bash
Server change state > StartingServerState
Server Starting
Queue message (1)
Queue message (2)
Server change state > StartServerState
Server Start
Queue message (3)
Server change state > StopSafeServerState
Can't send requests,the server making a safe stop with the remaining requests
Message process > Send Message + 1(2)
Can't send requests,the server making a safe stop with the remaining requests
Can't send requests,the server making a safe stop with the remaining requests
Can't send requests,the server making a safe stop with the remaining requests
Message process > Send Message + 2(1)
Can't send requests,the server making a safe stop with the remaining requests
Can't send requests,the server making a safe stop with the remaining requests
Message process > Send Message + 3(0)
Server change state > StopServerState

Process finished with exit code 0

```

![sdf](C:\Users\PERSONAL-PC\Pictures\Saved%20Pictures\state.png)

<div style="page-break-after: always"></div>

## Patron Observer

> Usando el patron Observer, tomando como base el ejemplo del administrador de configuracion, implementar una nueva configuracion en la clase ConfigurationManager, la cual nos permita administrar configurar el formato de moneda, es decir, el usuario podra indicar que su moneda sera dolar o euro. Ademas tendremos que crear un nuevo observador que nos permita ser notificados de que el formato ha cambiado.

### Solucion del problema

**Clase ConfigurationManager**

En la clase ConfigurationManager colocamos el atributo `coin` el cual contiene un metodo denominado `getCoin` y otro el cual se llama `setCoin` que a su vez contiene un notify que permite notificar el cambio de estado de dicha variable, con ello damos a notar el patron observer en donde se puede apreciar que se procede a notificar cuando se cambia el estado de un objeto, en este caso, la variable `coin`.

```java
public class ConfigurationManager extends AbstractObservable {

    private SimpleDateFormat defaultDateFormat;
    private NumberFormat moneyFormat;
    private String coin;

    private static ConfigurationManager configurationManager;

    private ConfigurationManager() {
    }

    public static ConfigurationManager getInstance() {
        if (configurationManager == null) {
            configurationManager = new ConfigurationManager();
        }
        return configurationManager;
    }

    public SimpleDateFormat getDefaultDateFormat() {
        return defaultDateFormat;
    }

    public void setDefaultDateFormat(SimpleDateFormat defaultDateFormat) {
        this.defaultDateFormat = defaultDateFormat;
        notifyAllObservers("defaultDateFormat", this);
    }

    public String getCoin(){return coin;}

    public void setCoin(String coin){
        this.coin = coin;
        notifyAllObservers("coinType",this);
    }


    public NumberFormat getMoneyFormat() {
        return moneyFormat;
    }

    public void setMoneyFormat(NumberFormat moneyFormat) {
//        System.out.println("Date Format change > ");
        this.moneyFormat = moneyFormat;
        notifyAllObservers("moneyFormat", this);
    }
}
```

<div style="page-break-after: always"></div>

**Clase CurrencyFormatObserver**

Este clase contiene el observador que permite notificar el cambio de estado en caso de que se este cambiando, para ello dicha clase implementa la interfaz IObserver que contiene el metodo `notifyObserver` que permite notificar del cambio de estado. A continuacion el desarrollo de la clase

```java
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
```

**Clase Main**

En esta clase procedemos a instanciar un nuevo observador, el cual es el que creamos y lo agregamos en nuestra lista de observadores de la variable conf. Con ello, cuando se cambie el formato de la moneda se procedera a notificar del cambio de dicha variable mediante el observer.

```java
public class ObserverMain {

    public static void main(String[] args) {
        ConfigurationManager conf = ConfigurationManager.getInstance();
        
        //Se establecen los valores por default.
        conf.setDefaultDateFormat(new SimpleDateFormat("yyyy/MM/dd"));
        conf.setMoneyFormat(new DecimalFormat("##.00"));
        System.out.println("Established configuration");
        
        //Se dan de alta lo observers
        DateFormatObserver dateFormatObserver = new DateFormatObserver();
        MoneyFormatObserver moneyFormatObserver = new MoneyFormatObserver();
        CurrencyFormatObserver currencyFormatObserver = new CurrencyFormatObserver();

        conf.addObserver(dateFormatObserver);
        conf.addObserver(moneyFormatObserver);
        conf.addObserver(currencyFormatObserver);
        System.out.println("");
        
        //Se cambia la fonfiguratión
        conf.setDefaultDateFormat(new SimpleDateFormat("dd/MM/yyyy"));
        conf.setMoneyFormat(new DecimalFormat("###,#00.00"));
        conf.setCoin("$");
        System.out.println("");
        
        //Se realiza otro cambio en la configuración.
        conf.setDefaultDateFormat(new SimpleDateFormat("MM/yyyy/dd"));
        conf.setMoneyFormat(new DecimalFormat("###,#00"));
        conf.setCoin("€");
        
        conf.removeObserver(dateFormatObserver);
        conf.removeObserver(moneyFormatObserver);
        conf.removeObserver(currencyFormatObserver);
        System.out.println("");
    }
}
```

<div style="page-break-after: always"></div>

**Resultados obtenidos**

Como podemos apreciar en el siguiente bash, se esperan los resultados deseados.

```bash
Established configuration

Observer ==> DateFormatObserver.dateFormatChange > 26/07/2022
Observer ==> MoneyFormatObserver.moneyFormatChange > 01.11
Observer ==> CurrencyFormat.currencyTypeChange > $

Observer ==> DateFormatObserver.dateFormatChange > 07/2022/26
Observer ==> MoneyFormatObserver.moneyFormatChange > 01
Observer ==> CurrencyFormat.currencyTypeChange > € 

Process finished with exit code 0

```

<div style="page-break-after: always"></div>

## Patron Template

> Usando el patron template, agregamos un nuevo archivo que nos permita recibir los pagos realizados por medio de cargos automaticos, este archivo nos lo envia el banco y contiene los cagos realizados a los clientes directamente sobre su tarjeta de credito/debito. El formato del archivo sera en XML y tendra la estructura que mas te guste. El procesamiento y el log deberan tener las reglas ya conocidas.

### Solucion del problema

**Clase XmlFileProcess**

En esta clase se procede a crear con el objetivo de ejecutar los procesos para archivos xml. El objetivo de este patron es crear clases que tengan casi el mismo comportamiento pero su logica de procesamiento pueda ser diferente, en este caso el xml se procesa de otra forma, por lo que mediante el patron template se puede crear una clase que identifique el proceso unico de un xml, para ello se crea la clase que extiende de la clase AbstractFileProcessTemplate, este clase contiene un metodo `execute` que permite pasar por todos los procesos por los que pasan los otros archivos, garantizando asi el mismo flujo para el archivo xml. 

Lo que se cambia en esta clase de xml es el metodo `processFile` ya que como se habia mencionado, este tiene otra forma de lectura. Ademas de ello tambien se modifica el metodo `validateName` ya que se tiene que validar ahora que sea un archivo xml. El codigo se presenta a continuacion.

```java
public class XmlFileProcess extends AbstractFileProcessTemplete {

    private String log = "";

    public XmlFileProcess(File file, String logPath, String movePath) {
        super(file, logPath, movePath);
    }

    @Override
    protected void validateName() throws Exception {
        String fileName = file.getName();
        if (!fileName.endsWith(".xml")) {
            throw new Exception("Invalid file name format"
                    + ", must end with .xml");
        }

        if (fileName.length() != 16) {
            throw new Exception("Invalid document format");
        }
    }

    @Override
    protected void processFile() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
        Document document = documentBuilder.parse(file);

        document.getDocumentElement().normalize();
        NodeList listaEmpleados = document.getElementsByTagName("employee");

        for (int temp = 0; temp < listaEmpleados.getLength(); temp++) {
            Node nodo = listaEmpleados.item(temp);
            if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nodo;
                String id = element.getAttribute("id");
                String customer = element.getElementsByTagName("costumer").item(0).getTextContent();
                double amount = Double.parseDouble(element.getElementsByTagName("amount").item(0).getTextContent());
                String date = element.getElementsByTagName("date").item(0).getTextContent();
                boolean exist = OnMemoryDataBase.customerExist(
                        Integer.parseInt(customer));
                if (!exist) {
                    log += id + " E" + customer + "\t\t" + date
                            + " Customer not exist\n";
                } else if (amount > 200) {
                    log += id + " E" + customer + "\t\t" + date
                            + " The amount exceeds the maximum\n";
                } else {

                    log += id + " E" + customer + "\t\t" + date
                            + " Successfully applied\n";
                }
            }
        }
    }

    @Override
    protected void createLog() throws Exception {
        FileOutputStream out = null;
        try {
            File outFile = new File(logPath + "/" + file.getName());
            if (!outFile.exists()) {
                outFile.createNewFile();
            }
            out = new FileOutputStream(outFile, false);
            out.write(log.getBytes());
            out.flush();
        } finally {
            out.close();
        }
    }
}
```

<div style="page-break-after: always"></div>

**Clase Main**

En la clase main se crean las rutas en donde estaran ubicadas los logs, los process y los files ( para mayor comodidad, los directorios se encuentran en el proyecto con e objetivo de que no sea necesario cambiar la ruta de los mismos y ejecutar el programa sin problemas).  Con ello procedemos a leer el archivo xml y ejecutarlo para procesar dicho archivo.

```java
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
```

<div style="page-break-after: always"></div>

**Resultado Obtenido**

###### Archivo xml de ejemplo:

```xml
<?xml version="1.0"?>
<payments>
    <employee id="1">
        <costumer>10</costumer>
        <amount>150</amount>
        <date>13/03/2015</date>
    </employee>
    <employee id="2">
        <costumer>09</costumer>
        <amount>250</amount>
        <date>12/03/2015</date>
    </employee>
</payments>
```

###### Log de respuesta:

```log
1 E10		13/03/2015 Successfully applied
2 E09		12/03/2015 The amount exceeds the maximum

```

##### archivo process

```xml
<?xml version="1.0"?>
<payments>
    <employee id="1">
        <costumer>10</costumer>
        <amount>150</amount>
        <date>13/03/2015</date>
    </employee>
    <employee id="2">
        <costumer>09</costumer>
        <amount>250</amount>
        <date>12/03/2015</date>
    </employee>
</payments>
```

###### terminal del programa

```bash
> Monitoring start
> Read Path files\xml
> File found > 123456789123.xml
Processed file > 123456789123.xml
> Monitoring start
> Read Path files\xml
> File found > 123456789123.xml
The file '123456789123.xml' has already been processed

```