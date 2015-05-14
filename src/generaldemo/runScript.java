package generaldemo;

import java.io.IOException;

public class runScript {
	public static void main(String[] args) throws IOException {
		AutoManifestBarcodeRead readOne = new AutoManifestBarcodeRead();
		// PdfToJpg convertOne = new PdfToJpg();
		// convertOne.viewDir("1");
		// convertOne.viewDir("2");
		// convertOne.viewDir("3");
		// convertOne.viewDir("4");
		// convertOne.viewDir("5");
		// convertOne.viewDir("7");

		readOne.createPicture("1");
		readOne.createPicture("2");
		readOne.createPicture("3");
		readOne.createPicture("4");
		readOne.createPicture("5");
		readOne.createPicture("7");

	}
}
