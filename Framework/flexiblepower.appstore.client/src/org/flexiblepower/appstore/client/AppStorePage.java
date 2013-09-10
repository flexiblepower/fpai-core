package org.flexiblepower.appstore.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.flexiblepower.appstore.client.shared.AppStoreItem;
import org.flexiblepower.appstore.client.shared.AppStoreQuestion;
import org.flexiblepower.appstore.client.shared.AppStoreService;
import org.flexiblepower.appstore.client.shared.AppStoreStatus;
import org.flexiblepower.appstore.common.Application;
import org.flexiblepower.appstore.common.PermissionQuestion;
import org.flexiblepower.data.applications.App;
import org.flexiblepower.data.applications.AppDataStore;
import org.flexiblepower.provisioning.AppProvisioner;
import org.flexiblepower.provisioning.AppProvisioningStatus;
import org.flexiblepower.security.SecurityManager;
import org.flexiblepower.ui.Widget;
import org.osgi.framework.BundleContext;
import org.osgi.service.permissionadmin.PermissionInfo;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gwt.servlet.RemoteServiceCallback;
import com.google.gwt.user.client.rpc.RemoteService;

@Component(provide = {RemoteService.class, Widget.class}, properties={"widget.name=appstore", "widget.type=full", "contextId=appstore/appstore"})
public class AppStorePage implements AppStoreService, RemoteServiceCallback, Widget {
    private static final String APPSTORE_SERVER = "https://appstore:8443/appstore/";

    @Activate
    public void activate(BundleContext context) {
    }

    private AppProvisioner appProvisioner;

    @Reference
    public void setAppProvisioner(AppProvisioner appProvisioner) {
        this.appProvisioner = appProvisioner;
    }

    private AppDataStore appDataStore;

    @Reference
    public void setAppDataStore(AppDataStore appDataStore) {
        this.appDataStore = appDataStore;
    }

    private UserAdmin userAdmin;

    @Reference
    public void setUserAdmin(UserAdmin userAdmin) {
        this.userAdmin = userAdmin;
    }

    private SecurityManager securityManager;

    @Reference
    public void setSecurityManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    private final Map<Integer, AppProvisioningStatus> statusMap;

    private volatile User user;

    private final GsonBuilder gsonBuilder;

    public AppStorePage() {
        statusMap = new HashMap<Integer, AppProvisioningStatus>();
        gsonBuilder = new GsonBuilder();
    }

    private InputStream openUrl(String path) throws MalformedURLException, IOException {
        URL url = new URL(APPSTORE_SERVER + path);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(securityManager.getSSLSocketFactory());

        InputStream inputStream = connection.getInputStream();
        return inputStream;
    }

    private JsonElement parseJson(String path) throws IOException {
        JsonParser jsonParser = new JsonParser();

        InputStream is = openUrl(path);
        JsonElement element = jsonParser.parse(new InputStreamReader(is));
        is.close();

        return element;
    }

    private <T> T parseObject(String path, Type type) throws IOException {
        return gsonBuilder.create().<T> fromJson(parseJson(path), type);
    }

    @Override
    public List<AppStoreItem> getCatalogue() throws IOException {
        List<Application> apps = parseObject("catalogue", new TypeToken<List<Application>>() {
        }.getType());

        List<AppStoreItem> items = new ArrayList<AppStoreItem>();
        for (Application app : apps) {
            List<AppStoreItem.Component> components = new ArrayList<AppStoreItem.Component>();
            for (org.flexiblepower.appstore.common.Component comp : app.getComponents()) {
                components.add(new AppStoreItem.Component(comp.getSymbolicName(), comp.getVersion(), comp.getJarFile()));
            }
            items.add(new AppStoreItem(app.getId(), app.getName(), app.getDescription(), app.getPrice(), components));
        }
        return items;
    }

    @Override
    public List<AppStoreQuestion> getDetails(int itemId) throws IOException {
        if (!userIsAdmin()) {
            throw new IOException("The current user does not have the right to install applications");
        }

        JsonObject object = (JsonObject) parseJson("detail/" + itemId);
        JsonArray jsonQuestions = object.getAsJsonArray("questions");
        List<PermissionQuestion> questions = gsonBuilder.create().fromJson(jsonQuestions,
                                                                           new TypeToken<List<PermissionQuestion>>() {
                                                                           }.getType());

        List<AppStoreQuestion> result = new ArrayList<AppStoreQuestion>();
        for (PermissionQuestion q : questions) {
            result.add(new AppStoreQuestion(q.getQuestion(),
                                            q.getDefaultAnswer(),
                                            q.isShown(),
                                            q.getCoveredPermissions()));
        }
        return result;
    }

    private boolean userIsAdmin() {
        return userAdmin.getAuthorization(user).hasRole("Administrators");
    }

    @Override
    public void purchase(int itemId, Set<String> acceptedPermissions) throws IOException {
        if (!userIsAdmin()) {
            throw new IOException("The current user does not have the right to install applications");
        }

        JsonObject object = (JsonObject) parseJson("detail/" + itemId);
        Application application = gsonBuilder.create().fromJson(object.getAsJsonObject("app"), Application.class);
        App app = appDataStore.get(Integer.toString(itemId));

        List<PermissionInfo> permissions = new ArrayList<PermissionInfo>();
        for (String acceptedPermission : acceptedPermissions) {
            permissions.add(new PermissionInfo(acceptedPermission));
        }
        app.setAcceptedPermissions(permissions);

        List<org.flexiblepower.appstore.common.Component> components = application.getComponents();
        List<URI> bundleLocations = new ArrayList<URI>(components.size());
        for (org.flexiblepower.appstore.common.Component component : components) {
            try {
                bundleLocations.add(new URI(APPSTORE_SERVER + "download/" + component.getJarFile()));
            } catch (URISyntaxException e) {
                // Should not be possible
            }
        }
        app.setBundleLocations(bundleLocations);

        app.setDescription(application.getDescription());
        app.setName(application.getName());

        appDataStore.save(app);
        AppProvisioningStatus status = appProvisioner.provision(app);
        statusMap.put(itemId, status);
    }

    @Override
    public AppStoreStatus getStatus(int itemId) {
        AppProvisioningStatus status = statusMap.get(itemId);
        if (status == null) {
            return null;
        } else {
            return new AppStoreStatus(status.getCurrentAction(Locale.ENGLISH),
                                      status.getStatus(),
                                      status.getError() == null ? null : status.getError().getMessage());
        }
    }

    @Override
    public Object
            handleCall(HttpServletRequest request, Method method, Object... params) throws IllegalArgumentException,
                                                                                   IllegalAccessException,
                                                                                   InvocationTargetException {
        @SuppressWarnings("unchecked")
        Map<String, Object> session = (Map<String, Object>) request.getAttribute("session");
        if (session != null) {
            user = (User) session.get("user");
        }
        return method.invoke(this, params);
    }

	@Override
	public String getTitle(Locale locale) {
		return "App Store";
	}
}
