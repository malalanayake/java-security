package app.security.virus;

import org.w3c.dom.*;
import javax.xml.xpath.*;
import java.io.*;

class LoopInfo {
	private int upperBound;
	private int currentValue = 0;

	public LoopInfo(Node instruction) {
		upperBound = Integer.parseInt(instruction.getAttributes().getNamedItem("upperBound")
			.getNodeValue());
	}

	public int getUpperBound() {
		return upperBound;
	}

	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}

	public int getCurrentValue() {
		return currentValue;
	}
}