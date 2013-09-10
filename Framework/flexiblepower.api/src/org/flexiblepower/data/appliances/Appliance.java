package org.flexiblepower.data.appliances;

import java.net.URI;

import org.flexiblepower.data.IdentifyableObject;

public interface Appliance extends IdentifyableObject {
    String getIdentification();

    void setIdentification(String identification);

    String getApplianceType();

    void setApplianceType(String applianceType);

    URI getInformationLocation();

    void setInformationLocation(URI informationLocation);
}
