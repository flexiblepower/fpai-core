package org.flexiblepower.api.efi.bufferhelper;

import java.util.Map;

import org.flexiblepower.efi.buffer.BufferRegistration;

public class HelperStarter {

    private Map<String, Buffer> bufferMap;

    public static void main(String[] args) {

    }

    public void ReceiveBufferRegistration(BufferRegistration br) {
        if (bufferMap.containsKey(br.getResourceId())) {
            // ignore this registration message.
            return;
        } else {
            bufferMap.put(br.getResourceId(), new Buffer(br));
        }

    }
}
