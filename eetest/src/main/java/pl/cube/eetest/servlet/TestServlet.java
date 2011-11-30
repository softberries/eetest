package pl.cube.eetest.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pl.cube.eetest.controller.StockMgr;
import pl.cube.eetest.data.StockProducer;

@WebServlet(asyncSupported = false, name = "HelloServlet1", urlPatterns = {"/import"})
public class TestServlet extends HttpServlet {

	@Inject
	private StockProducer stockp;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.write("<h2>Hello Servlet One </h2>");
		stockp.importStocks();
		out.write("stocks imported...");
		out.close();
	}

	
}
