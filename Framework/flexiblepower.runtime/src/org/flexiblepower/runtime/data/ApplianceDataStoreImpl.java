package org.flexiblepower.runtime.data;

import java.net.URI;

import org.flexiblepower.data.appliances.Appliance;
import org.flexiblepower.data.appliances.ApplianceDataStore;

import aQute.bnd.annotation.component.Component;

@Component
public class ApplianceDataStoreImpl extends AbstractDataStore<Appliance> implements ApplianceDataStore {
    static class ApplianceImpl implements Appliance {
        private final String id;
        private String identification;
        private String applianceType;
        private URI informationLocation;

        public ApplianceImpl(String id) {
            this.id = id;
        }

        public ApplianceImpl(Appliance source) {
            id = source.getId();
            identification = source.getIdentification();
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getIdentification() {
            return identification;
        }

        @Override
        public void setIdentification(String identification) {
            this.identification = identification;
        }

        @Override
        public String getApplianceType() {
            return applianceType;
        }

        @Override
        public void setApplianceType(String applianceType) {
            this.applianceType = applianceType;
        }

        @Override
        public URI getInformationLocation() {
            return informationLocation;
        }

        @Override
        public void setInformationLocation(URI informationLocation) {
            this.informationLocation = informationLocation;
        }
    }

    @Override
    protected Appliance newObject(String id) {
        return new ApplianceImpl(id);
    }

    @Override
    protected Appliance copyObject(Appliance object) {
        return new ApplianceImpl(object);
    }
}
