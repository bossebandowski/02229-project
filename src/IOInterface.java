import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

public class IOInterface {
    private Architecture architecture = new Architecture();

    private void generateNodes(NodeList nodes) {
        Element e;
        String name;
        String t;
        boolean type;

        for (int i = 0; i < nodes.getLength(); i++) {
            e = (Element) nodes.item(i);
            name = e.getAttribute("name");
            t = e.getAttribute("type");
            type = t.equals("EndSystem");
            this.architecture.addNode(new Node(name, type));
        }
    }

    private void generateLinks(NodeList links) {
        Element e;
        Node src;
        Node dest;
        float speed;

        for (int i = 0; i < links.getLength(); i++) {
            e = (Element) links.item(i);
            src = architecture.getNodeByName(e.getAttribute("src"));
            dest = architecture.getNodeByName(e.getAttribute("dest"));
            speed = Float.parseFloat(e.getAttribute("speed"));
            this.architecture.addLink(new Link(src, dest, speed));
        }
    }

    private void generateStreams(NodeList streams) {
        Element e;
        String name;
        Node src;
        Node dest;
        int size;
        int period;
        int deadline;
        int rl;

        for (int i = 0; i < streams.getLength(); i++) {
            e = (Element) streams.item(i);
            name = e.getAttribute("id");
            src = architecture.getNodeByName(e.getAttribute("src"));
            dest = architecture.getNodeByName(e.getAttribute("dest"));
            size = Integer.parseInt(e.getAttribute("size"));
            period = Integer.parseInt(e.getAttribute("period"));
            deadline = Integer.parseInt(e.getAttribute("deadline"));
            rl = Integer.parseInt(e.getAttribute("rl"));
            this.architecture.addStream(new Stream(name, src, dest, size, period, deadline, rl));
        }
    }

    public Architecture parse(String path) {
        // snippet based on https://www.javatpoint.com/how-to-read-xml-file-in-java
        // Using the DOM API to read and write .xml files
        try {
            // create file object
            File file = new File(path);

            // an instance of factory that gives a document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            // an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();

            // create nodes
            NodeList nodes = doc.getElementsByTagName("device");
            generateNodes(nodes);

            // create links
            NodeList links = doc.getElementsByTagName("link");
            generateLinks(links);

            // create streams
            NodeList streams = doc.getElementsByTagName("stream");
            generateStreams(streams);

            return this.architecture;


        } catch (Exception e) {
            System.err.println("ERR: Could not parse " + path);
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }

    public void writeSolution(List<List<Integer>> solution, String path, String name) {

        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("solution");
            document.appendChild(root);
            root.setAttribute("tc_name", name);

            // add task elements
            int idx = 0;
            for (Stream s : architecture.getStreams()) {
                Element sElement = document.createElement("stream");
                root.appendChild(sElement);

                sElement.setAttribute("id", String.valueOf(s.getName()));
                for (int rep = 0; rep < s.getRl(); rep++) {
                    List<Integer> route = solution.get(idx);
                    Element rElement = document.createElement("route");
                    sElement.appendChild(rElement);

                    int parent = route.get(0);
                    int child;
                    for (int rId = 1; rId < route.size(); rId++) {
                        child = route.get(rId);
                        Link l = architecture.getLinks().get(architecture.getGraph()[parent][child]);
                        Element lElement = document.createElement("link");
                        rElement.appendChild(lElement);
                        lElement.setAttribute("src", l.getStart().getName());
                        lElement.setAttribute("dest", l.getEnd().getName());

                        parent = child;
                    }

                    idx++;
                }
            }

            // create the xml file
            // transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(path));

            transformer.transform(domSource, streamResult);

        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        }
    }
}
