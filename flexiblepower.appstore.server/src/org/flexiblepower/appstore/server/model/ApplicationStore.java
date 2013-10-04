package org.flexiblepower.appstore.server.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.flexiblepower.appstore.common.Application;
import org.flexiblepower.appstore.common.Component;
import org.flexiblepower.appstore.server.DownloadServlet;
import org.osgi.service.permissionadmin.PermissionInfo;

@aQute.bnd.annotation.component.Component(provide = ApplicationStore.class)
public class ApplicationStore implements Iterable<Application> {
    private final Map<Integer, Application> apps;

    public ApplicationStore() throws IOException {
        apps = new HashMap<Integer, Application>();

        initApps();
    }

    private void initApps() throws IOException {
        defApp("PowerMatcher",
               "The PowerMatcher client is able to optimize the energy usage by using a energy market mechanism.",
               null,
               defModule("net.powermatcher.fpai.controller-1.0.1.jar"),
               defModule("net.powermatcher.core.adapter-0.9.0.jar"),
               defModule("net.powermatcher.core.adapter.service-0.9.0.jar"),
               defModule("net.powermatcher.core.agent-0.9.0.jar"),
               defModule("net.powermatcher.core.agent.concentrator-0.9.0.jar"),
               defModule("net.powermatcher.core.agent.marketbasis.adapter-0.9.0.jar"),
               defModule("net.powermatcher.core.configurable-0.9.0.jar"),
               defModule("net.powermatcher.core.messaging.framework-0.9.0.jar"),
               defModule("net.powermatcher.core.messaging.mqttv3-0.9.0.jar"),
               defModule("net.powermatcher.core.messaging.protocol.adapter-0.9.0.jar"),
               defModule("net.powermatcher.core.scheduler.service-0.9.0.jar"),
               defModule("net.powermatcher.fpai.agent-2.0.0.jar"),
               defModule("net.powermatcher.fpai.agent.buffer-2.0.0.jar"),
               defModule("net.powermatcher.fpai.agent.storage-2.0.0.jar"),
               defModule("net.powermatcher.fpai.agent.timeshifter-2.0.0.jar"),
               defModule("net.powermatcher.fpai.agent.uncontrolled-2.0.0.jar"));

        defApp("Miele@Home",
               "Adds support for the Miele@Home gateway with all the Miele appliances that are coupled to it.",
               new BigDecimal("0.99"),
               defModule("flexiblepower.miele.protocol-1.0.1.jar"),
               defModule("flexiblepower.miele.dishwasher.driver-1.0.1.jar"),
               defModule("flexiblepower.miele.dishwasher.manager-1.0.2.jar"),
               defModule("flexiblepower.miele.refrigerator.driver-1.0.1.jar"),
               defModule("flexiblepower.miele.refrigerator.manager-1.0.2.jar"));

        defApp("PV Panel Simulation",
               "This app simulates a PV Panel that can be configured.",
               new BigDecimal("0.49"),
               defModule("flexiblepower.pvpanel.simulation-1.0.2.jar"),
               defModule("flexiblepower.uncontrolled.manager-1.0.2.jar"));

        defApp("Battery Simulation",
               "This app simulaties a Battery that can be configured.",
               new BigDecimal("0.49"),
               defModule("flexiblepower.battery.simulation-1.0.2.jar"),
               defModule("flexiblepower.battery.manager-1.0.2.jar"));

        defApp("iNRG micro CHP",
               "Adds support for micro CHP's through an iNRG interface",
               new BigDecimal("0.49"),
               defModule("flexiblepower.inrg.protocol.driver-1.0.2.jar"),
               defModule("flexiblepower.inrg.resource.driver-1.0.0.jar"),
               defModule("flexiblepower.inrg.microchp.manager-1.0.0.jar"));

        defApp("AnySense Client",
               "Logs resource state etc to an AnySense server",
               null,
               defModule("nl.tno.anysense.client-1.0.0.jar"));

        // defApp("Household Performarce",
        // "This app shows a mock-up of a household performance widget",
        // new BigDecimal("1.99"),
        // defModule("flexiblepower.household.mockup-1.0.0.jar"));
    }

    private void defApp(String name, String description, BigDecimal price, Component... components) {
        int id = apps.size() + 1;
        apps.put(id, new Application(id, name, description, price, Arrays.asList(components)));
    }

    private Component defModule(String jarFile) throws IOException {
        InputStream input = DownloadServlet.openJarFile(jarFile);
        if (input == null) {
            throw new IllegalArgumentException("File does not exist: " + jarFile);
        }
        JarInputStream jis = new JarInputStream(input);

        Manifest manifest = jis.getManifest();

        List<String> permissions = Collections.singletonList("(java.security.AllPermission)");
        ZipEntry entry;
        while ((entry = jis.getNextEntry()) != null) {
            if ("OSGI-INF/permissions.perm".equals(entry.getName())) {
                permissions = readPermissions(jis);
                break;
            }
        }
        Attributes attributes = manifest.getMainAttributes();
        return new Component(attributes.getValue("Bundle-SymbolicName"),
                             attributes.getValue("Bundle-Version"),
                             jarFile,
                             permissions);
    }

    private List<String> readPermissions(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        List<String> permissions = new ArrayList<String>();
        String line = null;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) {
                try {
                    PermissionInfo permissionInfo = new PermissionInfo(line);
                    permissions.add(permissionInfo.getEncoded());
                } catch (IllegalArgumentException ex) {
                    // ignore line
                }
            }
        }
        return permissions;
    }

    @Override
    public Iterator<Application> iterator() {
        return apps.values().iterator();
    }

    public Application get(int id) {
        return apps.get(id);
    }

    public Collection<Application> values() {
        return apps.values();
    }
}
