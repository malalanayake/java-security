package app.security.concepts;

import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedHashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
						// app.listEachObjectWithTheirSubjects(document);
						app.listEachSubjectWithTheirObjects(document);
				} catch (Exception ex) {
						System.out.println(ex.getMessage());
				}
		}

		/**
		 * List all objects with the subjects that can be access
		 * 
		 * @param document
		 */
		public void listEachObjectWithTheirSubjects(Document document) {

				try {
						document.getDocumentElement().normalize();
						XPath xPath = XPathFactory.newInstance().newXPath();
						String expression = "/filesystem/object";
						NodeList objectNodeList = (NodeList) xPath.compile(expression).evaluate(document,
						    XPathConstants.NODESET);

						// Run the process for all object nodes
						for (int i = 0; i < objectNodeList.getLength(); i++) {
								// Get the particular object node
								Node objectNode = objectNodeList.item(i);
								// Check if the node is an Element Node
								if (objectNode.getNodeType() == Node.ELEMENT_NODE) {
										// Cast the node to element to get the attributes
										Element objectElement = (Element) objectNode;
										System.out.println("obj:" + objectElement.getAttribute("name"));
										// Get the ACL element (One ACL element is there so directly access
										// the 0th item)
										Node nodeACL = objectElement.getElementsByTagName("acl").item(0);
										int length = nodeACL.getChildNodes().getLength();
										// Run the process for all ACE nodes
										for (int j = 0; j < length; j++) {
												Node nodeACE = nodeACL.getChildNodes().item(j);
												// Check if the node is an Element Node
												if (nodeACE.getNodeType() == Node.ELEMENT_NODE) {
														// If you need to access the attribute or text value inside the
														// node you have to cast to Element
														Element elementACE = (Element) nodeACE;
														System.out.println("--->sub:" + elementACE.getAttribute("subject"));
												}
										}

										System.out.println();
								}
						}
				} catch (XPathExpressionException e) {
						e.printStackTrace();
				}

		}

		/**
		 * List each subject with their objects
		 * 
		 * @param document
		 */
		public void listEachSubjectWithTheirObjects(Document document) {

				try {
						document.getDocumentElement().normalize();
						XPath xPath = XPathFactory.newInstance().newXPath();
						String expression = "/filesystem/object/acl/ace";
						NodeList objectNodeList = (NodeList) xPath.compile(expression).evaluate(document,
						    XPathConstants.NODESET);

						HashSet<String> users = new LinkedHashSet<String>();
						// Run the process for all object nodes
						for (int i = 0; i < objectNodeList.getLength(); i++) {
								// Get the particular object node
								Node aceNode = objectNodeList.item(i);
								// Check if the node is an Element Node
								if (aceNode.getNodeType() == Node.ELEMENT_NODE) {
										// Cast the node to element to get the attributes
										Element objectElement = (Element) aceNode;
										users.add(objectElement.getAttribute("subject"));
										// System.out.println();
								}
						}

						for (String user : users) {
								expression = "/filesystem/object[acl/ace[@subject='" + user + "']]";
								NodeList filteredObjectNodeList = (NodeList) xPath.compile(expression).evaluate(document,
								    XPathConstants.NODESET);
								// Run the process for all object nodes

								System.out.println("user:" + user);
								for (int j = 0; j < filteredObjectNodeList.getLength(); j++) {
										// Get the particular object node
										Node objectNode = filteredObjectNodeList.item(j);
										// Check if the node is an Element Node
										if (objectNode.getNodeType() == Node.ELEMENT_NODE) {
												// Cast the node to element to get the attributes
												Element objectElement = (Element) objectNode;
												System.out.println("----->object:" + objectElement.getAttribute("name"));
										}
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
