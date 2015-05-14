package generaldemo;

import com.bytescout.barcodereader.*;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

import javax.imageio.ImageIO;

//test version of AutoManifestBarcodeRead used for testing a single jpg file
public class AutoManifestBarcodeRead2 {
	public static void main(String[] args) throws IOException {
		// Create Barcode Reader instance
		Reader reader = new Reader();
		reader.setRegistrationName("demo");
		reader.setRegistrationKey("demo");

		// Set barcode types to find:
		reader.setBarcodeTypesToFind(EnumSet.of(BarcodeType.Code39,
				BarcodeType.Code128, BarcodeType.QRCode));
		reader.setBarcodeOrientations(EnumSet.of(
				BarcodeOrientation.VerticalFromTopToBottom,
				BarcodeOrientation.VerticalFromBottomToTop));

		// Demonstrate barcode decoding from image file:
		FoundBarcode[] foundBarcodes;
		BufferedImage image = ImageIO.read(new File(
				"10-10-14 482 TI_WO5 III_3.jpg"));
		foundBarcodes = reader.readFromImage(image);
		if (foundBarcodes.length == 0) {
			System.out.println("no barcodes found");
		}
		for (FoundBarcode barcode : foundBarcodes) {
			// System.out.println("Found barcode in file SampleCode39.jpg:");
			// System.out.println("Type = " + barcode.getType());
			System.out.println("Value = " + barcode.getValue());
			// System.out.println("Confidence = " + barcode.getConfidence());
			System.out.println("Rectangle = " + barcode.getRectangle());

			BufferedImage imageCropped = extractSignature(image,
					barcode.getRectangle(), image);
			File outputfile = new File("image"
					+ barcode.getValue().substring(0, 8) + ".jpg");
			ImageIO.write(imageCropped, "jpg", outputfile);
		}
		/*
		 * FoundBarcode[] foundBarcodes =
		 * reader.readFromFile("SampleCode39.jpg"); for (FoundBarcode barcode :
		 * foundBarcodes) { } // Demonstrate barcode decoding from BufferedImage
		 * object BufferedImage image = ImageIO.read(new
		 * File("SampleQRCode.jpg")); foundBarcodes =
		 * reader.readFromImage(image); for (FoundBarcode barcode :
		 * foundBarcodes) { }
		 */

		System.out.flush();
	}

	private static BufferedImage extractSignature(BufferedImage src,
			Rectangle rect, BufferedImage page) {
		if (rect.y < 850) {
			if (rect.y < 0)
				rect.y = 0;
			// BufferedImage dest = src.getSubimage(rect.x, rect.y, rect.width,
			// rect.height);
			// if resolution changes, use 0.1091
			if (rect.width < 240)
				rect.width = 240;
			if (rect.x < 2.5 * rect.width)
				rect.x = (int) (2.4 * rect.width);
			BufferedImage dest = src.getSubimage(
					(int) (rect.x - (2.4 * rect.width)), 0,
					(int) (2.4 * rect.width), page.getHeight());
			return dest;
		} else {
			if (rect.y < 0)
				rect.y = 0;
			// BufferedImage dest = src.getSubimage(rect.x, rect.y, rect.width,
			// rect.height);
			// if resolution changes, use 0.1091
			if (rect.width < 240)
				rect.width = 240;
			if (rect.x < 2.5 * rect.width)
				rect.x = (int) (2.4 * rect.width);
			BufferedImage dest = src.getSubimage((int) (rect.x), 0,
					(int) (2.4 * rect.width), page.getHeight());
			return dest;
		}
	}
}
