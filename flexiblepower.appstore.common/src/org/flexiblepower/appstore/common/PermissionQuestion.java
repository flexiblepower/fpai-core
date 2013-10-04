package org.flexiblepower.appstore.common;

import java.util.Set;

public class PermissionQuestion {
    private String question;
    private boolean defaultAnswer;
    private boolean shown;
    private Set<String> coveredPermissions;

    public PermissionQuestion() {
    }

    public PermissionQuestion(String question, boolean defaultAnswer, boolean shown, Set<String> coveredPermissions) {
        this.question = question;
        this.defaultAnswer = defaultAnswer;
        this.shown = shown;
        this.coveredPermissions = coveredPermissions;
    }

    public String getQuestion() {
        return question;
    }

    public boolean getDefaultAnswer() {
        return defaultAnswer;
    }

    public boolean isShown() {
        return shown;
    }

    public Set<String> getCoveredPermissions() {
        return coveredPermissions;
    }

    @Override
    public String toString() {
        return "PermissionQuestion [question=" + question
               + ", coveredPermissions="
               + coveredPermissions
               + (isShown() ? ", is shown" : "")
               + (getDefaultAnswer() ? ", selected by default" : "")
               + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((coveredPermissions == null) ? 0 : coveredPermissions.hashCode());
        result = prime * result + (defaultAnswer ? 1231 : 1237);
        result = prime * result + ((question == null) ? 0 : question.hashCode());
        result = prime * result + (shown ? 1231 : 1237);
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
        PermissionQuestion other = (PermissionQuestion) obj;
        if (coveredPermissions == null) {
            if (other.coveredPermissions != null) {
                return false;
            }
        } else if (!coveredPermissions.equals(other.coveredPermissions)) {
            return false;
        }
        if (defaultAnswer != other.defaultAnswer) {
            return false;
        }
        if (question == null) {
            if (other.question != null) {
                return false;
            }
        } else if (!question.equals(other.question)) {
            return false;
        }
        if (shown != other.shown) {
            return false;
        }
        return true;
    }

}
