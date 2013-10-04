package org.flexiblepower.appstore.server;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.flexiblepower.appstore.server.model.ApplicationStore;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component(provide = Servlet.class, properties = "alias=/appstore/catalogue")
public class CatalogueServlet extends HttpServlet {
    private static final long serialVersionUID = 1426936931957347785L;

    private ApplicationStore appDataStore;

    private GsonBuilder gsonBuilder;

    @Reference
    public void setAppDataStore(ApplicationStore appDataStore) {
        this.appDataStore = appDataStore;
        gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = gsonBuilder.create();

        resp.setContentType("application/json");

        OutputStreamWriter osw = new OutputStreamWriter(resp.getOutputStream());
        gson.toJson(appDataStore.values(), osw);
        osw.close();
    }
}
