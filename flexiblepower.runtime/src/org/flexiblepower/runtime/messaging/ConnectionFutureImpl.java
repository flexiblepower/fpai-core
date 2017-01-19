package org.flexiblepower.runtime.messaging;

import java.util.concurrent.TimeoutException;

import javax.measure.Measurable;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import org.flexiblepower.messaging.ConnectionFuture;
import org.flexiblepower.messaging.ConnectionManager.PotentialConnection;
import org.flexiblepower.messaging.ConnectionManagerException;

public class ConnectionFutureImpl implements ConnectionFuture {

    private final ConnectionManagerImpl connectionManager;
    private final String onePid;
    private final String onePort;
    private final String otherPid;
    private final String otherPort;
    private PotentialConnection potentialConnection = null;

    private final Object syncObject = new Object();

    private boolean isCancelled = false;

    ConnectionFutureImpl(ConnectionManagerImpl cm,
                         String onePid,
                         String onePort,
                         String otherPid,
                         String otherPort) {
        connectionManager = cm;
        this.onePid = onePid;
        this.onePort = onePort;
        this.otherPid = otherPid;
        this.otherPort = otherPort;
    }

    boolean tryConnect() {
        if (!isCancelled) {
            try {
                potentialConnection = connectionManager.connectEndpointPorts(onePid, onePort, otherPid, otherPort);
                synchronized (syncObject) {
                    syncObject.notifyAll();
                }
                connectionManager.removeConnectionFuture(this);
                return true;
            } catch (ConnectionManagerException e) {
                // Maybe next time...
            }
        }
        return false;
    }

    @Override
    public void cancel() {
        isCancelled = true;
        connectionManager.removeConnectionFuture(this);
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public boolean isConnected() {
        return potentialConnection != null;
    }

    @Override
    public PotentialConnection getPotentialConnection() {
        return potentialConnection;
    }

    @Override
    public void awaitConnection() throws InterruptedException {
        if (!isConnected() && !isCancelled) {
            synchronized (syncObject) {
                while (!isConnected()) {
                    syncObject.wait();
                }
            }
        }
    }

    @Override
    public void awaitConnection(Measurable<Duration> timeout) throws TimeoutException, InterruptedException {
        long timeoutMs = timeout.longValue(SI.MILLI(SI.SECOND));
        long deadline = System.currentTimeMillis() + timeoutMs;
        if (!isConnected() && !isCancelled) {
            synchronized (syncObject) {
                do {
                    syncObject.wait(timeoutMs);
                    timeoutMs = deadline - System.currentTimeMillis();
                } while (!isConnected() && timeoutMs > 0);
            }
        }
        if (!isConnected()) {
            throw new TimeoutException("Connection between " + onePid
                                       + ":"
                                       + onePort
                                       + " and "
                                       + otherPid
                                       + ":"
                                       + otherPort
                                       + " was not established withing specified timeout");
        }
    }

}
