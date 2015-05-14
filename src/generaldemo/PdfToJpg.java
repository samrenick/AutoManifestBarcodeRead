package generaldemo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 * Class is used to convert scanned pdfs into jpgs, one file per page. The
 * output is used by AUtoManifestBarcodeRead.java to split each jpg into
 * individual stops using bar codes printed on manifests.
 */
public class PdfToJpg {
	// pdfs are placed in the PDF folder with subfolders for each company by
	// number
	public void viewDir(final String company) throws IOException {
		File[] files = new File("./PDFs/" + company).listFiles();
		showFiles(files, company);
	}

	// recursive call used to drill down through sub directories to individual
	// files
	public static void showFiles(File[] files, String company)
			throws IOException {
		for (File file : files) {
			if (file.isDirectory()) {
				// System.out.println("Directory: " + file.getName());
				showFiles(file.listFiles(), company); // Calls same method
														// again.
			} else {
				// convert individual files from pdf to jpg. needs error
				// checking to make sure other file types aren't converted
				createJpg(company, file);
			}
		}
	}

	// creates jpgs from pdfs, one per page of each pdf
	public static void createJpg(String company, File dir) throws IOException {
		try {
			// Pdf files are read from this folder
			// converted images from pdf document are written to this folder
			String destinationDir = "./jpgs";
			File destinationFile = new File(destinationDir);

			// make sure there is a destination file in place
			if (!destinationFile.exists()) {
				destinationFile.mkdir();

				// println used for debugging
				// System.out.println("Folder Created -> "
				// + destinationFile.getAbsolutePath());
			}
			if (dir.exists()) {

				// println used in debugging
				// System.out.println("Images copied to Folder: "
				// + destinationFile.getName());

				// pdfbox used to convert
				PDDocument document = PDDocument.load(dir);
				List<PDPage> list = document.getDocumentCatalog().getAllPages();

				// println used in debugging
				// System.out.println("Total files to be converted -> "
				// + list.size());

				// remove pdf file extension
				String fileName = dir.getName().replace(".pdf", "");

				int pageNumber = 1;// keep track of how many pages in each pdf
				for (PDPage page : list) {// iterate through pages in pdf

					// size and resolution are kept constant on purpose for use
					// in AutoManifestBarcodeRead.java: allows creation of
					// subimage to use constants.
					BufferedImage image = page.convertToImage(5, 200);

					// output file is a jpg placed in the JPGs folder under the
					// company subdirectory
					File outputfile = new File(".\\JPGs\\" + company + "\\"
							+ fileName + "_" + pageNumber + ".jpg");

					// println used in debugging
					// System.out.println("Image Created -> "
					// + outputfile.getName());

					// ImageIO used to write the jpg
					ImageIO.write(image, "jpg", outputfile);
					pageNumber++; // increment page number value
				}
				document.close();
				System.out.println("Converted Images are saved at -> "
						+ destinationFile.getCanonicalPath());
			} else {
				System.err.println(dir.getName() + " File not exists");
			}
		} catch (Exception e) {
		}
	}
}
