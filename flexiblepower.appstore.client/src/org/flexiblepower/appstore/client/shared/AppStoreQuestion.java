package org.flexiblepower.appstore.client.shared;

import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AppStoreQuestion implements IsSerializable {
    private String question;
    private boolean defaultAnswer;
    private boolean shown;
    private Set<String> coveredPermissions;

    public AppStoreQuestion() {
    }

    public AppStoreQuestion(String question, boolean defaultAnswer, boolean shown, Set<String> coveredPermissions) {
        this.question = question;
        this.defaultAnswer = defaultAnswer;
        this.shown = shown;
        this.coveredPermissions = coveredPermissions;
    }

    public String getQuestion() {
        return question;
    }

    public boolean isDefaultAnswer() {
        return defaultAnswer;
    }

    public boolean isShown() {
        return shown;
    }

    public Set<String> getCoveredPermissions() {
        return coveredPermissions;
    }
}
