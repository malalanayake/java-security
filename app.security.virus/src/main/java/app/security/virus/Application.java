package app.security.virus;

/**
 * Hello world!
 *
 */
import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.xml.xpath.*;
import java.io.*;

public class Application {

	public Application() {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File("program.xml"));
			addVirus(doc);
			printProgram(doc);

			Object[] instructions = new Object[doc.getDocumentElement().getChildNodes().getLength()];
			int n = 0;

			// "compile the program"

			NodeList instructionNodes = doc.getDocumentElement().getChildNodes();
			int numInstructions = instructionNodes.getLength();
			for (int i = 0; i < numInstructions; i++) {
				Node instruction = instructionNodes.item(i);
				if (instruction.getNodeName().equals("#text"))
					continue;
				int addr = Integer.parseInt(instruction.getAttributes().getNamedItem("addr").getNodeValue());
				if (addr != n)
					throw new Exception("Address is not sequential");
				if (instruction.getNodeName().equals("loop"))
					instructions[addr] = new LoopInfo(instruction);
				else
					instructions[addr] = instruction;
				n++;
			}

			// "execute the program"

			int currentInstruction = 0;
			boolean stopped = false;
			while (!stopped) {
				Object instruction = instructions[currentInstruction];
				if (instruction instanceof LoopInfo) {
					LoopInfo loopInfo = (LoopInfo) instruction;
					loopInfo.setCurrentValue(0); // start loop
					currentInstruction++; // first instruction of body of loop
				} else if (instruction instanceof Node) {
					Node instructionNode = (Node) instruction;
					String instructionName = instructionNode.getNodeName();
					if (instructionName.equals("stop"))
						stopped = true;
					else if (instructionName.equals("print")) {
						System.out.print(instructionNode.getAttributes().getNamedItem("value").getNodeValue());
						currentInstruction++;
					} else if (instructionName.equals("goto"))
						currentInstruction = Integer.parseInt(instructionNode.getAttributes().getNamedItem("toAddr")
							.getNodeValue());
					else if (instructionName.equals("endLoop")) {
						int loopAddress = Integer.parseInt(instructionNode.getAttributes().getNamedItem("toAddr")
							.getNodeValue());
						if (!(instructions[loopAddress] instanceof LoopInfo))
							throw new Exception("Illegal endLoop toAddr");
						else // decide whether to do another iteration or exit the loop
						{
							LoopInfo loopInfo = (LoopInfo) instructions[loopAddress];
							loopInfo.setCurrentValue(loopInfo.getCurrentValue() + 1);
							if (loopInfo.getCurrentValue() == loopInfo.getUpperBound())
								currentInstruction++; // instruction following endLoop
							else
								currentInstruction = loopAddress + 1; // first instruction in loop
						}
					} else
						throw new Exception("Illegal instruction");
				}
			}

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

		System.out.println(); // flush the buffer

	}

	void addVirus(Document doc) throws Exception {
		NodeList instructionNodes = doc.getDocumentElement().getChildNodes();
		int numInstructions = instructionNodes.getLength();
		numInstructions++;

		NodeList list = doc.getDocumentElement().getChildNodes();
		int len = list.getLength();
		int n = 0;
		for (int i = 0; i < len; i++) {
			Node node = list.item(i);
			if (!node.getNodeName().equals("#text"))
				n++;
		}
		String toaddr =instructionNodes.item(1).getAttributes().getNamedItem("toAddr").getNodeValue();
		instructionNodes.item(1).getAttributes().getNamedItem("toAddr").setNodeValue(""+n);
		Element node1 = doc.createElement("print");
		node1.setAttribute("addr", ""+n);
		node1.setAttribute("value", "v");
		n++;
		
		Element node2 = doc.createElement("print");
		node2.setAttribute("addr", ""+n);
		node2.setAttribute("value", "i");
		n++;
		
		Element node3 = doc.createElement("print");
		node3.setAttribute("addr", ""+n);
		node3.setAttribute("value", "r");
		n++;
		
		Element node4 = doc.createElement("print");
		node4.setAttribute("addr", ""+n);
		node4.setAttribute("value", "u");
		n++;
		
		Element node5 = doc.createElement("print");
		node5.setAttribute("addr", ""+n);
		node5.setAttribute("value", "s");
		n++;
		
		//Goto previous node
		Node node6 = createGotoInstruction(doc,n,toaddr);

		doc.getDocumentElement().appendChild(node1);
		doc.getDocumentElement().appendChild(node2);
		doc.getDocumentElement().appendChild(node3);
		doc.getDocumentElement().appendChild(node4);
		doc.getDocumentElement().appendChild(node5);
		doc.getDocumentElement().appendChild(node6);
		
		
	}

	void printProgram(Document doc) {
		NodeList instructions = doc.getDocumentElement().getChildNodes();
		int numInstructions = instructions.getLength();
		for (int i = 0; i < numInstructions; i++) {
			Node instruction = instructions.item(i);
			if (instruction.getNodeName().equals("#text"))
				continue;

			System.out.format("<%s ", instruction.getNodeName());
			NamedNodeMap attributes = instruction.getAttributes();
			int numAttributes = attributes.getLength();
			for (int j = 0; j < numAttributes; j++) {
				Node att = attributes.item(j);
				System.out.format("%s='%s' ", att.getNodeName(), att.getNodeValue());
			}
			System.out.println("/>");
		}
	}

	// Used in conjunction with appendChild()
	Node createPrintInstruction(Document doc, String letter, int addr) {
		Node node = doc.createElement("print");
		Attr attr = doc.createAttribute("value");
		attr.setValue(letter);
		node.getAttributes().setNamedItem(attr);

		attr = doc.createAttribute("addr");
		attr.setValue(Integer.toString(addr));
		node.getAttributes().setNamedItem(attr);

		return node;
	}
	
	Node createGotoInstruction(Document doc, int addr, String toAddr)
 {
		Element node = doc.createElement("goto");
		node.setAttribute("addr", ""+addr);
		node.setAttribute("toAddr", ""+toAddr);
		return node;
 }

	// Hack. Count non #text nodes. What is a better way to do this?
	int countInstructions(Document doc) {
		NodeList list = doc.getDocumentElement().getChildNodes();
		int len = list.getLength();
		int n = 0;
		for (int i = 0; i < len; i++) {
			Node node = list.item(i);
			if (!node.getNodeName().equals("#text"))
				n++;
		}

		return n;
	}

	public static void main(String[] args) {
		new Application();
	}
}
