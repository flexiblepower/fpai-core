package org.flexiblepower.runtime.ui.connectionspage;

public class ConnectionInfo {
    private String source, target;

    public void setSource(String source) {
        this.source = source;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSourceEndpoint() {
        return source.substring(0, source.lastIndexOf(':'));
    }

    public String getSourcePort() {
        return source.substring(source.lastIndexOf(':') + 1);
    }

    public String getTargetEndpoint() {
        return target.substring(0, target.lastIndexOf(':'));
    }

    public String getTargetPort() {
        return target.substring(target.lastIndexOf(':') + 1);
    }

    public boolean isValid() {
        return source != null && target != null && !source.isEmpty() && !target.isEmpty();
    }
}
