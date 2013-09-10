package org.flexiblepower.appstore.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.flexiblepower.appstore.common.Application;
import org.flexiblepower.appstore.common.PermissionQuestion;
import org.flexiblepower.appstore.server.model.ApplicationStore;
import org.flexiblepower.appstore.server.model.RightsQuestionGenerator;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.google.gson.GsonBuilder;

@Component(provide = Servlet.class, properties = "alias=/appstore/detail")
public class DetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 8742645529626427663L;

    private final GsonBuilder gsonBuilder;

    public DetailsServlet() {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
    }

    private ApplicationStore applicationStore;

    @Reference
    public void setApplicationStore(ApplicationStore applicationStore) {
        this.applicationStore = applicationStore;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getPathInfo().substring(1));

            Application application = applicationStore.get(id);
            if (application != null) {
                RightsQuestionGenerator rqg = new RightsQuestionGenerator(req.getLocale());

                List<String> allPermissions = new ArrayList<String>();
                for (org.flexiblepower.appstore.common.Component c : application.getComponents()) {
                    allPermissions.addAll(c.getPermissions());
                }

                List<PermissionQuestion> questions = rqg.makeQuestions(allPermissions);

                Map<String, Object> result = new HashMap<String, Object>();
                result.put("app", application);
                result.put("questions", questions);

                OutputStreamWriter writer = new OutputStreamWriter(resp.getOutputStream());
                gsonBuilder.create().toJson(result, writer);
                writer.close();
                return;
            }
        } catch (NumberFormatException ex) {
        }

        resp.sendError(404);
    }
}
