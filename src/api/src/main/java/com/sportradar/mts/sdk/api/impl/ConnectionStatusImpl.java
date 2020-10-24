package com.sportradar.mts.sdk.api.impl;

import com.sportradar.mts.sdk.api.interfaces.ConnectionChangeListener;
import com.sportradar.mts.sdk.api.interfaces.ConnectionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionStatusImpl implements ConnectionStatus {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionStatusImpl.class);

    private final int queueLimit = 10;
    private Date connectionTime;
    private Date disconnectionTime;
    private String lastSendTicketId;
    private String lastReceivedTicketId;
    private final Queue<String> sendTicketIds;
    private final Queue<String> receivedTicketIds;
    private ConnectionChangeListener connectionChangeListener;
    private final ReentrantLock lock = new ReentrantLock();

    public ConnectionStatusImpl(){
        connectionTime = null;
        disconnectionTime = null;
        lastSendTicketId = null;
        lastReceivedTicketId = null;
        sendTicketIds = new LinkedBlockingQueue<>();
        receivedTicketIds = new LinkedBlockingQueue<>();
        connectionChangeListener = null;
    }

    @Override
    public boolean isConnected() { return connectionTime != null && disconnectionTime == null; }

    @Override
    public Date getConnectionTime() { return connectionTime; }

    @Override
    public Date getDisconnectionTime() { return disconnectionTime; }

    @Override
    public String getLastSendTicketId() { return lastSendTicketId; }

    @Override
    public String getLastReceivedTicketId() { return lastReceivedTicketId; }

    public ConnectionChangeListener getConnectionChangeListener(){ return connectionChangeListener; }

    public void setConnectionChangeListener(ConnectionChangeListener connectionChangeListener){
        if(connectionChangeListener != null){
            this.connectionChangeListener = connectionChangeListener;
        }
    }

    public void connect(String message)
    {
        lock.lock();
        if (!isConnected())
        {
            log("Connecting. " + message);
            connectionTime = new Date();
            disconnectionTime = null;
            try{
                if(connectionChangeListener != null){
                    connectionChangeListener.connectionChanged(new ConnectionChangeImpl(isConnected(), message));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
//        else{
//            log("Connecting. Omitted.");
//        }
        lock.unlock();
    }

    public void disconnect(String message)
    {
        lock.lock();
        if (isConnected())
        {
            log("Disconnecting. " + message);
            connectionTime = null;
            disconnectionTime = new Date();
            try{
                if(connectionChangeListener != null){
                    connectionChangeListener.connectionChanged(new ConnectionChangeImpl(isConnected(), message));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
//        else{
//            log("Disconnecting. Omitted.");
//        }
        lock.unlock();
    }

    public void ticketSend(String ticketId)
    {
        log("TicketSend [" + sendTicketIds.size() + "]: " + ticketId);
        lock.lock();
        lastSendTicketId = ticketId;
        sendTicketIds.add(ticketId);
        if (sendTicketIds.size() > queueLimit)
        {
            sendTicketIds.remove();
        }
        lock.unlock();
    }

    public void ticketReceived(String ticketId)
    {
        log("TicketReceived [" + receivedTicketIds.size() + "]: " + ticketId);
        lock.lock();
        lastReceivedTicketId = ticketId;
        receivedTicketIds.add(ticketId);
        if (receivedTicketIds.size() > queueLimit)
        {
            receivedTicketIds.remove();
        }
        lock.unlock();
    }

    private void log(String message){
//        logger.info(message);
    }

    @Override
    public String toString() {
        String listenerSet = connectionChangeListener != null ? "set" : "no set";
        return "ConnectionStatusImpl{ " +
                "isConnected=" + isConnected() +
                ", connectionTime=" + connectionTime +
                ", disconnectionTime=" + disconnectionTime +
                ", lastSendTicketId=" + lastSendTicketId +
                ", lastReceivedTicketId=" + lastReceivedTicketId +
                ", connectionChangeListener=" + listenerSet +
//                ", sendTicketIds=" + sendTicketIds +
//                ", receivedTicketIds=" + receivedTicketIds +
//                ", queueLimit=" + queueLimit +
                " }";
    }
}
