# 

<a>
<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/b/b0/Logo_Universidad_Polit%C3%A9cnica_Salesiana_del_Ecuador.png/800px-Logo_Universidad_Polit%C3%A9cnica_Salesiana_del_Ecuador.png" alt="Ups logo" title="Aimeos" align="right" height="60" />
</a>

# Tarea de Patrones Estructurales

**Integrantes:**

* Bryam David Vega Moreno

* Pablo Sebastian Calderon Maldonado

**Repositorio:** [design-patterns-pair/structural-pattern at main · pscalderonm/design-patterns-pair · GitHub](https://github.com/pscalderonm/design-patterns-pair/tree/main/structural-pattern)

-------------------

## Patron Decorator

> Agreguemos un nuevo decorador que inserte al pie de pagina los datos de la empresa que esta mandando el correo; por ejemplo nombre de la empresa, direccion, telefonos y correo electronico (ficticios).

### Solucion del problema

**Clase Company (informacion de pie de pagina)**

En esta clase se indica los atributos para el pie de pagina que corresponde a la informacion de la empresa.

```java
public class CompanyMessage implements  IMessage{

    private String name;
    private String email;
    private String number;
    private String direction;

    public CompanyMessage(String name, String email, String number, String direction) {
        this.name = name;
        this.email = email;
        this.number = number;
        this.direction = direction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "CompanyMessage{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", direction='" + direction + '\'' +
                '}';
    }

    @Override
    public IMessage processMessage() {
        return this;
    }

    @Override
    public String getContent() {
        return toString();
    }

    @Override
    public void setContent(String content) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
```

<div style="page-break-after: always"></div>

**Clase mail (para armar el mail completo)**

Esta es una clase sencilla que lo unico que nos permite hacer es armar el mail con su cuerpo y pie del correo.

```java
public class Mail implements  IMessage{

    @Override
    public String toString() {
        return "Mail{}";
    }

    @Override
    public IMessage processMessage() {
        return this;
    }

    @Override
    public String getContent() {
        return toString();
    }

    @Override
    public void setContent(String content) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
```

<div style="page-break-after: always"></div>

**Clase MailMessageDecorator**

Esta clase abstracta permite crear decoradores para armar un mail que tenga un cuerpo y un pie que despues se uniran para obtener el mesaje Mail. El codigo se presenta a continuacion

```java
public abstract class MailMessageDecorator implements IMessage {

    protected IMessage bodyMessage;
    protected IMessage footerMessage;
    protected IMessage mailMessage;

    public MailMessageDecorator(IMessage bodyMessage, IMessage footerMessage) {
        this.bodyMessage = bodyMessage;
        this.footerMessage = footerMessage;
        this.mailMessage = new Mail();
    }

    @Override
    public String getContent() {
        return mailMessage.getContent();
    }

    @Override
    public void setContent(String content) {
        mailMessage.setContent(content);
    }
```

<div style="page-break-after: always"></div>

**Clase BuildMailMessage**

Por ultimo tenemos una clase que nos permite constuir el mail, la cual extiende de MailMessageDecorator para realizar dicho proceso, para ello se obtiene el cuerpo del mensaje como su pie y despues se los une en un solo mail.

```java
public class BuildMailMessage extends MailMessageDecorator{

    public BuildMailMessage(IMessage messageBody, IMessage messageFooter) {
        super(messageBody,messageFooter);
    }

    @Override
    public IMessage processMessage() {
        bodyMessage.processMessage();
        footerMessage.processMessage();
        mailMessage.processMessage();
        mailMessage = concatMessage();
        return mailMessage;
    }

    public IMessage concatMessage(){
        return new TextMessage(bodyMessage.getContent()+"\n"+footerMessage.getContent());
    }
}
```

<div style="page-break-after: always"></div>

**Clase Main**

Por ultimo en dicha clase se generan las pruebas necesarias para indicar el funcionamiento de nuestro nuevo decorator.

```java
public class DecoratorMain {

    public static void main(String[] args) {

        CustomerMessage customerMessage = new CustomerMessage(
                "Oscar Blancarte", "oscarblancarte3@gmail.com", "554433445566");
        System.out.println("Original Message ==> " + customerMessage);
        CompanyMessage companyMessage = new CompanyMessage(
                "Kruger corp","kruger@kruger.com.ec","0985164142","Quito");
        System.out.println("Company Info ==> " + companyMessage);

        IMessage message1 = new EncryptMessage("user", "HG58YZ3CR9123456",
                new SOAPEnvelopMessage(new BuildMailMessage(
                        new XMLFormatterDecorate(customerMessage),
                        new XMLFormatterDecorate(companyMessage)))).processMessage();
        System.out.println("message1 =====================================>\n" 
                + message1.getContent() + "\n");

        IMessage message2 =  new SOAPEnvelopMessage(new BuildMailMessage(
                        new XMLFormatterDecorate(customerMessage),
                        new XMLFormatterDecorate(companyMessage))).processMessage();
        System.out.println("message2 =====================================>\n"
                + message2.getContent());

        IMessage message3 =  new SOAPEnvelopMessage(new EncryptMessage("user", "HG58YZ3CR9123456",new BuildMailMessage(
                new XMLFormatterDecorate(customerMessage),
                new XMLFormatterDecorate(companyMessage)))).processMessage();
        System.out.println("message3 =====================================>\n"
                + message3.getContent());
    }

}
```

<div style="page-break-after: always"></div>

**Resultados obtenidos**

Por cuestiones de longitud del resultado, solamente se muestra el mensaje 2.

```bash
message2 =====================================>
<soapenv:Envelope xmlns:soapenv=
"http://schemas.xmlsoap.org/soap/envelope/" 
xmlns:ser="http://service.dishweb.cl.com/">
   <soapenv:Header/>
   <soapenv:Body>
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<oscarblancarte.ipd.decorator.impl.message.CustomerMessage>
    <content>CustomerMessage{name=Oscar Blancarte,email=oscarblancarte3@gmail.com, telephone=554433445566}</content>
    <email>oscarblancarte3@gmail.com</email>
    <name>Oscar Blancarte</name>
    <telephone>554433445566</telephone>
</oscarblancarte.ipd.decorator.impl.message.CustomerMessage>

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<oscarblancarte.ipd.decorator.impl.message.CompanyMessage>
    <content>CompanyMessage{name='Kruger corp', number='0985164142', direction='Quito'}</content>
    <direction>Quito</direction>
    <email>kruger@kruger.com.ec</email>
    <name>Kruger corp</name>
    <number>0985164142</number>
</oscarblancarte.ipd.decorator.impl.message.CompanyMessage>

   </soapenv:Body>
</soapenv:Envelope>
Process finished with exit code 0
```