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

@WebServlet(asyncSupported = false, name = "HelloServlet2", urlPatterns = {"/hello2"})
public class TestServlet2 extends HttpServlet {

	@Inject
	private StockMgr stockmgr;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.write("<h2>Hello Servlet Update </h2>");	
		stockmgr.updateStocks();
		out.close();
	}

	
}
