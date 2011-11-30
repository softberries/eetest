package pl.cube.eetest.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.seam.cron.api.scheduling.Scheduled;
import org.jboss.seam.cron.api.scheduling.Trigger;

import pl.cube.eetest.data.StockProducer;

@ApplicationScoped
public class AutoDownloader {

	@Inject
	private StockProducer stockProducer;
	
	public void importFullData(@Observes @Scheduled("0 30 18 ? * MON-FRI")  Trigger t){
		System.out.println("Updating full data..");
		long start = System.currentTimeMillis();
		long end = 0L;
		try {
			downloadFile();
			unzip(IUrls.TEMP_ZIP_FILE_NAME, IUrls.TEMP_DIRECTORY);
			processFiles();
			end = System.currentTimeMillis();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("TIME ELAPSED: " + (end - start) + " ms, " + (end - start)/ 1000 + " s.");
	}
	private void processFiles() {
		String[] files = new File(IUrls.TEMP_DIRECTORY).list();
		for(String s : files){
			stockProducer.importStocks();
		}
	}
	private void downloadFile() throws MalformedURLException, IOException {
		URL google = new URL(IUrls.FULL_STOCKS_URL);
		File directory = new File(IUrls.TEMP_DIRECTORY);
		if(!directory.exists()){
			directory.mkdir();
		}
	    ReadableByteChannel rbc = Channels.newChannel(google.openStream());
	    FileOutputStream fos = new FileOutputStream(IUrls.TEMP_ZIP_FILE_NAME);
	    fos.getChannel().transferFrom(rbc, 0, 1 << 24);
	    fos.close();
	}

	private void unzip(String zipFileName,
			String directoryToExtractTo) {
		Enumeration entriesEnum;
		ZipFile zipFile;
		try {
			zipFile = new ZipFile(zipFileName);
			entriesEnum = zipFile.entries();

			File directory= new File(directoryToExtractTo);

			/**
			 * Check if the directory to extract to exists
			 */
			if(!directory.exists())
			{
				/**
				 * If not, create a new one.
				 */
				new File(directoryToExtractTo).mkdir();
				System.err.println("...Directory Created -"+directoryToExtractTo);
			}
			while (entriesEnum.hasMoreElements()) {
				try {
					ZipEntry entry = (ZipEntry) entriesEnum.nextElement();

					if (entry.isDirectory()) {
						/**
						 * Currently not unzipping the directory structure.
						 * All the files will be unzipped in a Directory
						 *
						 **/
					} else {

						System.err.println("Extracting file: "
								+ entry.getName());
						/**
						 * The following logic will just extract the file name
						 * and discard the directory
						 */
						int index = 0;
						String name = entry.getName();
						index = entry.getName().lastIndexOf("/");
						if (index > 0 && index != name.length())
							name = entry.getName().substring(index + 1);

						System.out.println(name);

						writeFile(zipFile.getInputStream(entry),
								new BufferedOutputStream(new FileOutputStream(
										directoryToExtractTo + name)));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			zipFile.close();
		} catch (IOException ioe) {
			System.err.println("Some Exception Occurred:");
			ioe.printStackTrace();
			return;
		}
	}
	private void writeFile(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}
}
