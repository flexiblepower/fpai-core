package org.flexiblepower.efi.buffer;

public class LeakageFunction extends FillLevelFunction<RangeElement> {

    private final int id;

    private final String name;

    public LeakageFunction(int id, String name, RangeElement[] rangeElements) {
        super(rangeElements);
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
