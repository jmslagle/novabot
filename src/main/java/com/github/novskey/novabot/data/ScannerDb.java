package com.github.novskey.novabot.data;

import com.github.novskey.novabot.core.ScannerType;
import lombok.Data;

@Data
public class ScannerDb {

    private int id;
    private String user;
    private String pass;
    private String ip;
    private String port;
    private String dbName;
    private String protocol;
    private String useSSL;
    private ScannerType scannerType;
    private Integer maxConnections;

    public ScannerDb(String user, String pass, String ip, String port, String dbName, String protocol, String useSSL, ScannerType scannerType, Integer maxConnections, int id) {
        this.user = user;
        this.pass = pass;
        this.ip = ip;
        this.port = port;
        this.dbName = dbName;
        this.protocol = protocol;
        this.useSSL = useSSL;
        this.scannerType = scannerType;
        this.maxConnections = maxConnections;
        this.id = id;
    }


}
