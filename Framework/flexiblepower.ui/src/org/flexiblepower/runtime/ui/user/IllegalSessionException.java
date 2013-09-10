package org.flexiblepower.runtime.ui.user;

public class IllegalSessionException extends Exception {
    private static final long serialVersionUID = -6463294826600532432L;

    public IllegalSessionException() {
    }

    public IllegalSessionException(String msg) {
        super(msg);
    }

    public IllegalSessionException(Throwable tr) {
        super(tr);
    }

    public IllegalSessionException(String msg, Throwable tr) {
        super(msg, tr);
    }

}
