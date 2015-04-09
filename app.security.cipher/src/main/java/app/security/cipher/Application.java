package app.security.cipher;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

/**
 * Application for encryption and decryption
 *
 */
public class Application {
	public static void main(String[] args) {
		Application application = new Application();
		String doc = application.readXMLAsString("ciphertext.xml");
		String output = application.decrypt(doc);
		System.out.println(output);
	}

	/**
	 * Encrypt the given xml
	 * 
	 * @param xml
	 * @return
	 */
	public String encrypt(String xml) {
		StringBuilder sb = new StringBuilder(xml.length());
		for (int i = 0; i < xml.length(); i++) {
			char c = xml.charAt(i);
			sb.append((char) (c + 1)); // add 1 to the ASCII value of the character.
		}

		return sb.toString();
	}

	/**
	 * Decrypt the given string
	 * 
	 * @param xml
	 * @return
	 */
	public String decrypt(String xml) {
		StringBuilder sb = new StringBuilder(xml.length());
		for (int i = 0; i < xml.length(); i++) {
			char c = xml.charAt(i);
			sb.append((char) (c - 1)); // subtract 1 to the ASCII value of the character.
		}

		return sb.toString();
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

	/**
	 * Read XML as string
	 * 
	 * @param fileName
	 * @return
	 */
	public String readXMLAsString(String fileName) {
		try {
			InputStream file = ClassLoader.getSystemResourceAsStream(fileName);
			InputStreamReader is = new InputStreamReader(file);
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(is);
			String read = br.readLine();
			while (read != null) {
				sb.append(read);
				read = br.readLine();
			}
			return sb.toString();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return null;
		}
	}

}
