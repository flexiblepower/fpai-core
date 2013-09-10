package org.flexiblepower.runtime.resource.wiring;

import java.util.Collection;

public interface ResourceWiringManager {
    Collection<Resource<?>> getResources();

    int size();
}
