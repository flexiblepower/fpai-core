package org.flexiblepower.ral.wiring;

import java.util.Collection;

public interface ResourceWiringManager {
    Collection<Resource<?, ?>> getResources();

    int size();
}
