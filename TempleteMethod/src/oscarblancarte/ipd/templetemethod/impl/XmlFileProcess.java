package oscarblancarte.ipd.templetemethod.impl;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import oscarblancarte.ipd.templetemethod.util.OnMemoryDataBase;

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
