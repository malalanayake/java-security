package app.security.concepts;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Application class which is display the Access Control list
 * 
 * @author malalanayake
 *
 */
public class Application {
		public static String FILE_NAME = "student.xml";

		public static void main(String[] args) {
				System.out.println("******Access Control List Processor*****");
				Application app = new Application();
				try {

						Document document = app.readXML(FILE_NAME);
						app.listEachObjectWithTheirSubjects(document);
				} catch (Exception ex) {
						System.out.println(ex.getMessage());
				}
		}

		public void listEachObjectWithTheirSubjects(Document document) {
				XPath xpath = XPathFactory.newInstance().newXPath();
				NodeList list;
				try {
						list = (NodeList) xpath.evaluate("/filesystem/object/acl", document,
										XPathConstants.NODESET);
						int len = list.getLength();
						for (int i = 0; i < len; i++) {
								// Get the ACE node
								Node aclNode = list.item(i);
								int lenofACL = aclNode.getChildNodes().getLength();
								for (int j = 0; j < lenofACL; j++) {
										if (j == 0) {
												Node parentParent = aclNode.getParentNode();
												System.out.println("--" + parentParent.getAttributes().getNamedItem("name").getNodeValue());
										}
										Node aceNode = aclNode.getChildNodes().item(j);
										Node subject = aceNode.getAttributes().getNamedItem("subject");
										System.out.println("---" + subject.getNodeValue());
								}
						}
				} catch (XPathExpressionException e) {
						e.printStackTrace();
				}

		}

		/**
		 * Return the read XML
		 * 
		 * @param fileName
		 * @return
		 */
		public Document readXML(String fileName) {
				try {
						DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
						DocumentBuilder db = dbf.newDocumentBuilder();
						InputStream file = ClassLoader.getSystemResourceAsStream(fileName);
						Document d = db.parse(file);
						return d;
				} catch (Exception ex) {
						System.out.println(ex.getMessage());
						return null;
				}
		}
}
