package com.leofis.network.server;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

public class AvailableNodes implements KvmSerializable {

    private String managedPCs;

    public AvailableNodes() {

    }

    public AvailableNodes(String managedPCs) {
        this.managedPCs = managedPCs;
    }

    public Object getProperty(int arg0) {
        switch (arg0) {
            case 0:
                return managedPCs;
        }
        return null;
    }

    public int getPropertyCount() {
        return 1;
    }

    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch (index) {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "managedPCs";
                break;
            default:
                break;
        }
    }

    public void setProperty(int index, Object value) {
        switch (index) {
            case 0:
                managedPCs = value.toString();
                break;
            default:
                break;
        }
    }
}
