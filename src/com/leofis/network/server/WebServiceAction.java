package com.leofis.network.server;

import android.util.Log;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class WebServiceAction {

    private final String NAMESPACE = "http://server/";
    private final String URL = "http://192.168.1.12:9999/LeofisService/LeofisService?WSDL";

    public void register(String theComputerID) {

        SoapObject request = new SoapObject(NAMESPACE, "register");
        PropertyInfo propertyInfo = new PropertyInfo();

        Log.i("Computer ID :", theComputerID);
        propertyInfo.setName("arg0");
        propertyInfo.setValue(theComputerID);
        propertyInfo.setType(String.class);
        request.addProperty(propertyInfo);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        String soapAction = getSoapAction("register");
        Log.i("Method : ", soapAction);

        try {
            androidHttpTransport.call(soapAction, envelope);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean registerAndroid(String username, String password, String[] computers) {
        Boolean result = false;
        String myPCs;
        SoapObject request = new SoapObject(NAMESPACE, "registerAndroid");

        PropertyInfo propertyInfoOne = new PropertyInfo();
        propertyInfoOne.setName("username");
        propertyInfoOne.setValue(username);
        propertyInfoOne.setType(String.class);

        PropertyInfo propertyInfoTwo = new PropertyInfo();
        propertyInfoTwo.setName("password");
        propertyInfoTwo.setValue(password);
        propertyInfoTwo.setType(String.class);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < computers.length; i++) {
            builder.append(computers[i]);
            builder.append(" ");
        }
        myPCs = builder.toString();
        AvailableNodes nodes = new AvailableNodes(myPCs);
        Log.i("ManagedPCs", myPCs);

        PropertyInfo propertyInfoThree = new PropertyInfo();
        propertyInfoThree.setName("nodes");
        propertyInfoThree.setValue(nodes);
        propertyInfoThree.setType(nodes.getClass());

        request.addProperty(propertyInfoOne);
        request.addProperty(propertyInfoTwo);
        request.addProperty(propertyInfoThree);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        //envelope.addMapping(NAMESPACE, "availableNodes", nodes.getClass());
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        String soapAction = getSoapAction("registerAndroid");
        Log.i("Method : ", soapAction);

        try {
            androidHttpTransport.call(soapAction, envelope);
            //SoapObject response = (SoapObject)envelope.getResponse();
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Boolean.parseBoolean(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<StatisticalReports> retrieveStatistical(String username, String password) {
        List<StatisticalReports> list = new ArrayList<StatisticalReports>();
        SoapObject request = new SoapObject(NAMESPACE, "retrieveStatistics");

        PropertyInfo propertyInfoOne = new PropertyInfo();
        propertyInfoOne.setName("username");
        propertyInfoOne.setValue(username);
        propertyInfoOne.setType(String.class);

        PropertyInfo propertyInfoTwo = new PropertyInfo();
        propertyInfoTwo.setName("password");
        propertyInfoTwo.setValue(password);
        propertyInfoTwo.setType(String.class);

        request.addProperty(propertyInfoOne);
        request.addProperty(propertyInfoTwo);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        envelope.addMapping(NAMESPACE, "statisticalReports", new StatisticalReports().getClass());
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        String soapAction = getSoapAction("retrieveStatistics");
        Log.i("Method : ", soapAction);

        try {
            androidHttpTransport.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            //SoapObject soapReportList = (SoapObject) response.getProperty("return");
            int itemCount = response.getPropertyCount();
            for (int i = 0; i < itemCount; i++) {
                StatisticalReports report = new StatisticalReports();
                SoapObject soapStatisticalReports = (SoapObject) response.getProperty(i);

                if (soapStatisticalReports.hasProperty("ipFreqS")) {
                    report.setIpFreqS(soapStatisticalReports.getPrimitivePropertyAsString("ipFreqS"));
                }

                if (soapStatisticalReports.hasProperty("patternFreqS")) {
                    report.setPatternFreqS(soapStatisticalReports.getPrimitivePropertyAsString("patternFreqS"));
                }

                if (soapStatisticalReports.hasProperty("liveInterfacesS")) {
                    report.setLiveInterfacesS(soapStatisticalReports.getPrimitivePropertyAsString("liveInterfacesS"));
                }
                list.add(report);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String retrievePattern(String username, String password) {
        String result = null;
        SoapObject request = new SoapObject(NAMESPACE, "retrieveMaliciousPatterns");

        PropertyInfo propertyInfoOne = new PropertyInfo();
        propertyInfoOne.setName("username");
        propertyInfoOne.setValue(username);
        propertyInfoOne.setType(String.class);

        PropertyInfo propertyInfoTwo = new PropertyInfo();
        propertyInfoTwo.setName("password");
        propertyInfoTwo.setValue(password);
        propertyInfoTwo.setType(String.class);

        request.addProperty(propertyInfoOne);
        request.addProperty(propertyInfoTwo);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        String soapAction = getSoapAction("retrieveMaliciousPatterns");
        Log.i("Method : ", soapAction);

        try {
            androidHttpTransport.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean insertPattern(String username, String password, String maliciousIP, String maliciousPattern) {
        Boolean result = false;
        SoapObject request = new SoapObject(NAMESPACE, "insertMaliciousPatterns");

        PropertyInfo propertyInfoOne = new PropertyInfo();
        propertyInfoOne.setName("username");
        propertyInfoOne.setValue(username);
        propertyInfoOne.setType(String.class);

        PropertyInfo propertyInfoTwo = new PropertyInfo();
        propertyInfoTwo.setName("password");
        propertyInfoTwo.setValue(password);
        propertyInfoTwo.setType(String.class);

        PropertyInfo propertyInfoThree = new PropertyInfo();
        propertyInfoThree.setName("maliciousIP");
        propertyInfoThree.setValue(maliciousIP);
        propertyInfoThree.setType(String.class);

        PropertyInfo propertyInfoFour = new PropertyInfo();
        propertyInfoFour.setName("maliciousPattern");
        propertyInfoFour.setValue(maliciousPattern);
        propertyInfoFour.setType(String.class);

        request.addProperty(propertyInfoOne);
        request.addProperty(propertyInfoTwo);
        request.addProperty(propertyInfoThree);
        request.addProperty(propertyInfoFour);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        //envelope.addMapping(NAMESPACE, "availableNodes", nodes.getClass());
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        String soapAction = getSoapAction("insertMaliciousPatterns");
        Log.i("Method : ", soapAction);

        try {
            androidHttpTransport.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Boolean.parseBoolean(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void delete(String theComputerID) {

        SoapObject request = new SoapObject(NAMESPACE, "remove");
        PropertyInfo propertyInfo = new PropertyInfo();

        Log.i("Computer ID :", theComputerID);
        propertyInfo.setName("nodeID");
        propertyInfo.setValue(theComputerID);
        propertyInfo.setType(String.class);
        request.addProperty(propertyInfo);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        String soapAction = getSoapAction("remove");
        Log.i("Method : ", soapAction);

        try {
            androidHttpTransport.call(soapAction, envelope);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean login(String username, String password) {

        Boolean result = false;
        SoapObject request = new SoapObject(NAMESPACE, "login");

        PropertyInfo propertyInfoOne = new PropertyInfo();
        propertyInfoOne.setName("username");
        propertyInfoOne.setValue(username);
        propertyInfoOne.setType(String.class);

        PropertyInfo propertyInfoTwo = new PropertyInfo();
        propertyInfoTwo.setName("password");
        propertyInfoTwo.setValue(password);
        propertyInfoTwo.setType(String.class);

        request.addProperty(propertyInfoOne);
        request.addProperty(propertyInfoTwo);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        String soapAction = getSoapAction("login");
        Log.i("Method : ", soapAction);

        try {
            androidHttpTransport.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Boolean.parseBoolean(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getSoapAction(String method) {
        return "\"" + NAMESPACE + method + "\"";
    }

    private Vector<String> stringArray(String[] strings) {
        Vector vector = new Vector();
        for (int i = 0; i < strings.length; i++) {
            vector.addElement(strings[i]);
        }
        return vector;
    }
}
