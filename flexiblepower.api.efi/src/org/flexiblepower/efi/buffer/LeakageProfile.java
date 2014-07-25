package org.flexiblepower.efi.buffer;

import java.util.List;

public class LeakageProfile {

    private final int id;

    private final String name;

    private final List<RangeElement> rangeElements;

    public LeakageProfile(int id, String name, List<RangeElement> rangeElements) {
        super();
        this.id = id;
        this.name = name;
        this.rangeElements = rangeElements;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<RangeElement> getRangeElements() {
        return rangeElements;
    }

}
