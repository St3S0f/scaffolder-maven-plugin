package st3s0f.scaffoldermavenplugin;

import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.utils.SourceRoot;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.apache.maven.project.MavenProject;
import org.joox.Match;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import st3s0f.scaffoldermavenplugin.restclient.RestClient;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;

import static org.joox.JOOX.$;

public class Utils {

    private Utils() {}

    public static String toPrettyString(String xml, int indent) {
        try {
            // Turn xml string into a document
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

            // Remove whitespaces outside tags
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']",
                    document,
                    XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node node = nodeList.item(i);
                node.getParentNode().removeChild(node);
            }

            // Setup pretty print options
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            // Return pretty print xml string
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Path getPathToPom(Path currentPathToPom, MavenProject project) {
        if (project == null) {
            return currentPathToPom;
        } else {
            Path basedir = project.getBasedir().toPath();
            return Paths.get(basedir.toString(), "pom.xml");
        }
    }

    public static Match findOrCreate(Match parent, String element) {
        Match existing = parent.find(element);
        if (existing.isEmpty()) {
            Match created = $(element);
            parent.append(created);
        }
        return parent.find(element);
    }

    public static BiFunction<String,String,String> lastVersionOfFunction() {
        ConfigurableApplicationContext ac = SpringApplication.run(RestClient.class, new String[]{});
        return (BiFunction<String, String, String>) ac.getBean("getLatestVersionOf");
    }

    public static Path getRootJavaSourcePath(Path pathToPom) {
        return Paths.get(
                pathToPom.getParent().toString(),
                "src/main/java"
        );
    }

    public static Path getMainJavaPackagePath(Match pom, Path pathToPom) {
        return Paths.get(
                getRootJavaSourcePath(pathToPom).toAbsolutePath().toString(),
                pom.child("groupId").text().replaceAll("\\.","/"),
                pom.child("artifactId").text().replace("-","")
        );
    }

    public static String getMainJavaPackage(Match pom, Path pathToPom) {
        return pom.child("groupId").text() + "." + pom.child("artifactId").text().replace("-","");
    }

    public static Tuple2<String,String> getPackageAndClassPartsFor(String s) {
        String[] tokens = s.split("(?=\\p{Upper})");
        if (tokens.length > 1) {
            return Tuple.of(
                    tokens[0].endsWith(".") ? tokens[0].substring(0, tokens[0].length()-1) : tokens[0],
                    tokens[1]
            );
        } else {
            return Tuple.of(
                    "",
                    tokens[0]
            );
        }
    }
}
