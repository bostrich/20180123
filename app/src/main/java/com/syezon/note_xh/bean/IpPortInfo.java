package com.syezon.note_xh.bean;

import java.io.Serializable;
import java.net.InetAddress;

public class IpPortInfo implements Serializable{

    private InetAddress inetAddress;
    private int port;

    public IpPortInfo(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
