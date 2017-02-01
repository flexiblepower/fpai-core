package org.flexiblepower.runtime.efpiid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;

@Component(immediate = true, provide = { EfpiIdProvider.class })
public class EfpiIdProvider {

    private static final Logger LOG = LoggerFactory.getLogger(EfpiIdProvider.class);
    private static final String EFPI_ID_FILE = "efpi-id";

    private String efpiId = null;

    @Activate
    public void activate() {
        try {
            File efpiIdFile = new File(System.getProperty("user.dir") + System.getProperty("file.separator")
                                       + EFPI_ID_FILE);
            if (efpiIdFile == null || !efpiIdFile.exists() || efpiIdFile.length() == 0) {
                String uuid = UUID.randomUUID().toString();
                FileOutputStream out;
                out = new FileOutputStream(efpiIdFile);

                try {
                    out.write(uuid.getBytes());
                    efpiId = uuid;
                } finally {
                    out.close();
                }
            } else {
                byte[] bytes = new byte[(int) efpiIdFile.length()];
                FileInputStream in = new FileInputStream(efpiIdFile);

                try {
                    in.read(bytes);
                    efpiId = new String(bytes).trim();
                    if (efpiId.isEmpty()) {
                        LOG.warn("EF-Pi id was not valid, generating a temporory identifier");
                        efpiId = UUID.randomUUID().toString();
                    }
                } finally {
                    in.close();
                }
            }
        } catch (Exception e) {
            LOG.warn("Could not read EF-Pi id from file, generating a temporory identifier");
            efpiId = UUID.randomUUID().toString();
        }
    }

    public String efpiId() {
        return efpiId;
    }
}
