package org.flexiblepower.efi.uncontrolled;

import org.flexiblepower.rai.values.Commodity;

public class CurtailmentInfo {
    private final Commodity commodity;
    private final double[] curtailmentLevels;

    public CurtailmentInfo(Commodity commodity, double[] curtailmentLevels) {
        this.commodity = commodity;
        this.curtailmentLevels = curtailmentLevels;
    }
}
