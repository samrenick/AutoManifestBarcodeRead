package generaldemo;

import com.bytescout.barcodereader.*;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.EnumSet;

import javax.imageio.ImageIO;

/**
 * Class receives a single page jpg and outputs smaller images based on barcodes
 * present on the original. The output file name matches the stop represented by
 * the bar code. When execution is complete, the original jpeg page is moved to
 * a folder called Completed_JPGs
 * 
 **/

public class AutoManifestBarcodeRead {

	public AutoManifestBarcodeRead() {

	}

	// company number is taken from subdirectory name and used to prepend
	// 00+company number to the output file to give it a searchable stop id
	public void createPicture(String company) throws IOException {

		// writer used to log pages missing bar codes
		PrintWriter writer = new PrintWriter(company + "notFound", "UTF-8");

		// page size jpgs are located in JPGs file under a company number
		// sub directory. The SOP will specify that manifests are uploaded to
		// these files.
		File dir = new File("./JPGs/" + company);

		// directory for jpgs that have been processed into stops
		File moveDir = new File("./Completed_JPGs/" + company);

		// lists all files in a given subdirectory. PdfToJpg places all the page
		// size jpgs inside a single subdirectory
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			// process each file in the subdirectory for a given company
			for (File child : directoryListing) {
				try {
					// Create Barcode Reader instance
					Reader reader = new Reader();
					reader.setRegistrationName("demo");
					reader.setRegistrationKey("demo");

					// Set barcode types to find:
					reader.setBarcodeTypesToFind(EnumSet.of(BarcodeType.Code39,
							BarcodeType.Code128));
					// Need to try horizontal as well and see if that works
					// better than turning the page. Could produce more false
					// positives though
					reader.setBarcodeOrientations(EnumSet.of(
							BarcodeOrientation.VerticalFromTopToBottom,
							BarcodeOrientation.VerticalFromBottomToTop));

					// Demonstrate barcode decoding from image file:
					FoundBarcode[] foundBarcodes;
					BufferedImage image = ImageIO.read(child);
					BufferedImage image2;
					int width = image.getWidth();
					int height = image.getHeight();
					// page needs to be landscape for barcode reader to function
					// correctly
					if (height > width) {
						// flip orientation
						image2 = new BufferedImage(image.getHeight(),
								image.getWidth(), image.getType());
						Graphics2D graphics = (Graphics2D) image2.getGraphics();
						graphics.rotate(Math.toRadians(90),
								image2.getWidth() / 2, image2.getHeight() / 2);
						graphics.translate(
								(image2.getWidth() - image.getWidth()) / 2,
								(image2.getHeight() - image.getHeight()) / 2);
						graphics.drawImage(image, 0, 0, image.getWidth(),
								image.getHeight(), null);
						ImageIO.write(image2, "JPG", child);
						image = image2;
					}
					// This is where the magic happens. The bytescout
					// barcodereader library reads bar codes from the jpg
					foundBarcodes = reader.readFromImage(image);

					// if no bar codes are found, no jpgs are generated
					if (foundBarcodes.length == 0) {
						// log file names that don't contain any detectable bar
						// codes
						writer.println("no barcodes found in file " + child);
						System.out
								.println("no barcodes found in file " + child);
					} else {// for each bar code found, make a file and name it
							// using the information in the bar code
						for (FoundBarcode barcode : foundBarcodes) {
							// imageCropped will contain the stop specific
							// information below each bar code
							BufferedImage imageCropped = extractSignature(
									image, barcode.getRectangle(), image);
							// output file is placed in stops folder which is
							// not separated by companies.
							File outputfile = new File(".\\stops\\" + "00"
									+ company
									+ barcode.getValue().substring(0, 8));
							int counter = 2;
							// if there is already a file with the outputfile
							// name, append a _ + 2, 3, 4, etc to the file name
							while (outputfile.exists()) {
								outputfile = new File(".\\stops\\" + "00"
										+ company
										+ barcode.getValue().substring(0, 8)
										+ "_" + counter);
								counter++;
							}
							// check the file name for non numeric characters
							// and eliminate them with extreme prejudice
							String str = outputfile.toString();
							str = str.replaceAll("[^\\d.]", "");
							outputfile = new File(str + ".jpg");
							// write the file to stops folder
							ImageIO.write(imageCropped, "jpg", outputfile);
						}
					}

					System.out.flush();
				} catch (Exception e) {
					e.printStackTrace(writer);
				}

				// move the original jpg from JPGs to Completed_JPGs
				Path movefrom = FileSystems.getDefault().getPath(
						dir.getCanonicalPath() + "\\" + child.getName());
				Path target = FileSystems.getDefault().getPath(
						moveDir.getCanonicalPath() + "\\" + child.getName());
				Files.move(movefrom, target,
						StandardCopyOption.REPLACE_EXISTING);
			}
		}
		// close the log file
		writer.close();
	}

	// creates a subimage from the original jpg that contains only information
	// pertaining to one stop
	private static BufferedImage extractSignature(BufferedImage src,
			Rectangle rect, BufferedImage page) {
		if (rect.y < 850) {
			if (rect.y < 0)
				rect.y = 0;
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
			// this gave us the bar code to make sure bytescout knew what it was
			// doing
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
