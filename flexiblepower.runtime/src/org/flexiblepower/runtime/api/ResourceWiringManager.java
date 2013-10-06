package org.flexiblepower.runtime.api;

import java.util.Collection;

public interface ResourceWiringManager {
    Collection<Resource<?, ?>> getResources();

    int size();
}
