package org.flexiblepower.appstore.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Application {
    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private List<Component> components;

    public Application() {
    }

    public Application(int id, String name, String description, BigDecimal price, List<Component> components) {
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
        return new ArrayList<Component>(components);
    }

    @Override
    public String toString() {
        return "StoredApp [name=" + name
               + ", description="
               + description
               + ", price="
               + price
               + ", components="
               + components
               + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((components == null) ? 0 : components.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((price == null) ? 0 : price.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Application other = (Application) obj;
        if (id != other.id) {
            return false;
        }
        if (components == null) {
            if (other.components != null) {
                return false;
            }
        } else if (!components.equals(other.components)) {
            return false;
        }
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (price == null) {
            if (other.price != null) {
                return false;
            }
        } else if (!price.equals(other.price)) {
            return false;
        }
        return true;
    }
}
