package org.flexiblepower.runtime.data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.flexiblepower.data.applications.App;
import org.flexiblepower.data.applications.AppDataStore;
import org.osgi.service.permissionadmin.PermissionInfo;

import aQute.bnd.annotation.component.Component;

@Component
public class AppDataStoreImpl extends AbstractDataStore<App> implements AppDataStore {
    static class AppImpl implements App {
        private final String id;
        private String name, description;
        private List<URI> bundleLocations;
        private List<PermissionInfo> acceptedPermissions;

        public AppImpl(String id) {
            this.id = id;
        }

        public AppImpl(App source) {
            id = source.getId();
            name = source.getName();
            description = source.getDescription();
            bundleLocations = new ArrayList<URI>(source.getBundleLocations());
            acceptedPermissions = new ArrayList<PermissionInfo>(source.getAcceptedPermissions());
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public List<URI> getBundleLocations() {
            return bundleLocations;
        }

        @Override
        public void setBundleLocations(List<URI> bundleLocations) {
            this.bundleLocations = bundleLocations;
        }

        @Override
        public List<PermissionInfo> getAcceptedPermissions() {
            return acceptedPermissions;
        }

        @Override
        public void setAcceptedPermissions(List<PermissionInfo> acceptedPermissions) {
            this.acceptedPermissions = acceptedPermissions;
        }

        @Override
        public String getId() {
            return id;
        }
    }

    @Override
    protected App newObject(String id) {
        return new AppImpl(id);
    }

    @Override
    protected App copyObject(App object) {
        return new AppImpl(object);
    }
}
