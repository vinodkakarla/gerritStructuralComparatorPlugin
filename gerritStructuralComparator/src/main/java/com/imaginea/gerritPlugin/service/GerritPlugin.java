package com.imaginea.gerritPlugin.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gerrit.extensions.annotations.Export;
import com.google.gerrit.reviewdb.server.ReviewDb;
import com.google.gerrit.server.patch.PatchListCache;
import com.google.gson.Gson;
import com.google.gwtorm.server.SchemaFactory;
import com.google.inject.Inject;
import com.imaginea.gerritPlugin.model.Change;

@Export("/gerritPlugin")
public class GerritPlugin extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final PatchListCache patchListCache;
	private final SchemaFactory<ReviewDb> dbFactory;

	@Inject
	public GerritPlugin(SchemaFactory<ReviewDb> dbFactory,
			final PatchListCache patchListCache) {
		this.dbFactory = dbFactory;
		this.patchListCache = patchListCache;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		try {
			String reqHostName = req.getServerName() + ":" + req.getServerPort();
			
			GerritChangeProcessingService serviceObj = new GerritChangeProcessingService();
			List<Change> changeList = serviceObj.fetchChanges(dbFactory, patchListCache, reqHostName);
			
			
			Gson gson = new Gson();
			String json = gson.toJson(changeList);
			out.write(json);
		} catch (Exception e) {
			e.printStackTrace();
			out.write("<html><body>Exceptions being thrown</body></html>");
		} finally {
			out.close();
		}
	}
}
