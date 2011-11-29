package pl.cube.eetest.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.inject.Inject;
import javax.naming.spi.DirectoryManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pl.cube.eetest.controller.StockMgr;

@WebServlet(asyncSupported = false, name = "FullImportServlet", urlPatterns = {"/fullimport"})
public class FullImportServlet extends HttpServlet {

	private static final String FULL_STOCKS_URL = "http://bossa.pl/pub/ciagle/mstock/mstcgl.zip";
	private static final String TEMP_DIRECTORY = "temporary";
	private static final String TEMP_ZIP_FILE_NAME = "full_stock.zip";
	@Inject
	private StockMgr stockmgr;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		downloadFile();
		out.write("<h2>Hello Servlet One </h2>");
		stockmgr.importStocks();
		out.write("stocks imported...");
		out.close();
	}

	private void downloadFile() throws MalformedURLException, IOException {
		URL google = new URL(FULL_STOCKS_URL);
		File directory = new File(TEMP_DIRECTORY);
		if(!directory.exists()){
			directory.mkdir();
		}
	    ReadableByteChannel rbc = Channels.newChannel(google.openStream());
	    FileOutputStream fos = new FileOutputStream(TEMP_DIRECTORY + "/" + TEMP_ZIP_FILE_NAME);
	    fos.getChannel().transferFrom(rbc, 0, 1 << 24);
	    fos.close();
	}

	
}