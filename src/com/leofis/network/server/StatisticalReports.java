package com.leofis.network.server;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

public class StatisticalReports implements KvmSerializable {
    private String ipFreqS;
    private String patternFreqS;
    private String liveInterfacesS;

    public StatisticalReports() {
    }

    public StatisticalReports(String ipFreqS, String patternFreqS, String liveInterfacesS) {
        this.ipFreqS = ipFreqS;
        this.patternFreqS = patternFreqS;
        this.liveInterfacesS = liveInterfacesS;
    }

    public Object getProperty(int arg0) {
        switch (arg0) {
            case 0:
                return ipFreqS;
            case 1:
                return patternFreqS;
            case 2:
                return liveInterfacesS;
        }
        return null;
    }

    public int getPropertyCount() {
        return 3;
    }

    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch (index) {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "ipFreqS";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "patternFreqS";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "liveInterfacesS";
                break;
            default:
                break;
        }
    }

    public void setProperty(int index, Object value) {
        switch (index) {
            case 0:
                ipFreqS = value.toString();
                break;
            case 1:
                patternFreqS = value.toString();
                break;
            case 2:
                liveInterfacesS = value.toString();
                break;
            default:
                break;
        }
    }

    public String getIpFreqS() {
        return ipFreqS;
    }

    public void setIpFreqS(String ipFreqS) {
        this.ipFreqS = ipFreqS;
    }

    public String getPatternFreqS() {
        return patternFreqS;
    }

    public void setPatternFreqS(String patternFreqS) {
        this.patternFreqS = patternFreqS;
    }

    public String getLiveInterfacesS() {
        return liveInterfacesS;
    }

    public void setLiveInterfacesS(String liveInterfacesS) {
        this.liveInterfacesS = liveInterfacesS;
    }
}
