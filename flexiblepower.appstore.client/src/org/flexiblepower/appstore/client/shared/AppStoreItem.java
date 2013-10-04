package org.flexiblepower.appstore.client.shared;

import java.math.BigDecimal;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AppStoreItem implements IsSerializable {
    public static class Component implements IsSerializable {
        private String symbolicName;
        private String version;
        private String jarFile;

        public Component() {
        }

        public Component(String symbolicName, String version, String jarFile) {
            this.symbolicName = symbolicName;
            this.version = version;
            this.jarFile = jarFile;
        }

        public String getSymbolicName() {
            return symbolicName;
        }

        public String getVersion() {
            return version;
        }

        public String getJarFile() {
            return jarFile;
        }
    }

    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private List<Component> components;

    public AppStoreItem() {
    }

    public AppStoreItem(int id, String name, String description, BigDecimal price, List<Component> components) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.components = components;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public List<Component> getComponents() {
        return components;
    }
}
