package generaldemo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

public class getAllManifests {

	public static void main(String[] args) throws IOException {
		// get all pdfs from subfolders and copy into correct folder number on
		// local drive for processing (one time only)
		// gotta figure out which company corresponds to which number
		Process("\\\\CEFS01\\data\\CX Public\\Region-Mid South\\1.Daily Drivers Manifests");
		Process("\\\\CEFS01\\data\\CX Public\\Region-Carolinas\\DAILY DRIVERS MANIFESTS\\2015 Scanned Manifests");
		Process("\\\\CEFS01\\data\\CX Public\\Region-Central and South Florida\\Daily Driver Manifest");
		Process("\\\\CEFS01\\data\\CX Public\\Region-Coastal Carolina\\DRIVER DAILY MANIFEST. RETURNS.GNC BOLS\\2015 Scanned Manifests");
		Process("\\\\CEFS01\\data\\CX Public\\Region-Gulf South\\Daily Driver Manifests\\2015 Scanned Manifests");
		Process("\\\\CEFS01\\data\\CX Public\\Region-Tennessee Valley\\Daily Driver Manifest");
	}

	static void Process(String aFile) throws IOException {
		Path startPath = Paths.get(aFile);
		System.out.println("Copying files from" + startPath.toString());
		copyFiles(startPath.toString());
	}

	static void copyFiles(String dir) {
		String subDir = dir.substring(31, 40);
		File source = new File(dir);
		File dest = new File("./PDFs/" + subDir);
		System.out.println(dest);
		try {
			FileUtils.copyDirectory(source, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
