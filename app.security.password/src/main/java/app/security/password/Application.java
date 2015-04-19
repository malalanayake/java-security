package app.security.password;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.security.sasl.AuthenticationException;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Application for demonstrate the password save mechanism
 *
 */
public class Application {
	public static String FILE_PASSWORDS = "password.xml";
	public static String FILE_DICTIONARY = "dictionary.xml";

	public static void main(String[] args) throws AuthenticationException {
		Application app = new Application();
		Document pass = app.readXML(Application.FILE_PASSWORDS);
		Document dic = app.readXML(Application.FILE_DICTIONARY);

		// Crack the password
		app.crackPassWord(pass, dic);

	}

	/**
	 * Guessing the password
	 * 
	 * @param passwords
	 * @param dictionary
	 */
	public void crackPassWord(Document passwords, Document dictionary) {
		try {
			passwords.getDocumentElement().normalize();
			XPath xPath = XPathFactory.newInstance().newXPath();
			String expression = "/passwords/user";
			NodeList userNodeList = (NodeList) xPath.compile(expression).evaluate(passwords,
				XPathConstants.NODESET);

			String expression2 = "/words";
			NodeList dictionaryNodeList = (NodeList) xPath.compile(expression2).evaluate(dictionary,
				XPathConstants.NODESET);

			HashMap<String, String> users = new LinkedHashMap<String, String>();
			HashSet<String> possible = new LinkedHashSet<String>();

			// Read the users and store in map
			for (int i = 0; i < userNodeList.getLength(); i++) {
				Node userNode = userNodeList.item(i);

				Element objectElement = (Element) userNode;
				NodeList childNode = objectElement.getChildNodes();
				String name = childNode.item(1).getTextContent();
				String pass = childNode.item(3).getTextContent();
				StringBuffer nameBuf = new StringBuffer(name);
				StringBuffer reversedNameBuf = nameBuf.reverse();
				users.put(name, pass);

				// Add user name and reversed name as possible values
				possible.add(name);
				possible.add(reversedNameBuf.toString());

			}

			for (int i = 0; i < dictionaryNodeList.getLength(); i++) {
				Node wordNode = dictionaryNodeList.item(i);
				Element objectElement = (Element) wordNode;
				NodeList childNode = objectElement.getChildNodes();

				// Add dictionary values as possible value
				possible.add(childNode.item(1).getTextContent());
			}

			for (String name : users.keySet()) {
				String pass = users.get(name);
				System.out.println("=======Guessing password for user: " + name+"========");
				System.out.println("Original password hashed value: " + pass);
				for (String possibleVal : possible) {

					String hashedPass = generatePasswordDigest(possibleVal);
					System.err
						.println("Sample Value for Pass:" + possibleVal + " --> Hashed value :" + hashedPass);
					if (hashedPass.equals(pass)) {
						System.out.println("User " + name + "'s password is " + possibleVal);
						break;
					}
				}
				System.out.println();
			}

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}

	/**
	 * Generate password
	 * 
	 * @param password
	 * @return
	 * @throws AuthenticationException
	 */
	public String generatePasswordDigest(String password) throws AuthenticationException {
		String temp = password;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			return new String(DatatypeConverter.printBase64Binary(md.digest(temp.getBytes())));
		} catch (Exception e) {
			throw new AuthenticationException(e.getMessage(), e);
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
