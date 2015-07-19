package mibh.mis.oilandgasrequest.AllService;

import android.util.Log;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import LibClass.SOAPWebserviceProperty;

/**
 * Created by ponlakiss on 06/30/2015.
 */
public class CallWebService {

    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String url = "http://www.mibholding.com/fuel.asmx";
    private static String SOAP_ACTION = "http://tempuri.org/";
    private static String METHOD_NAME = "";
    private static SOAPWebserviceProperty soap_property = null;

    public String callForResult(String fuelid, String userid, String userscan, String latlng) {
        try {
            METHOD_NAME = "GetDataDocInfo";
            soap_property = new SOAPWebserviceProperty();
            soap_property.urlWebservice = url;
            soap_property.namespaceWebservice = NAMESPACE;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            JSONObject polydata = new JSONObject();
            polydata.put("FUEL_DOCID", fuelid);
            polydata.put("USER_ID", userid);
            polydata.put("USERSCAN", userscan);
            polydata.put("LATLNG", latlng);
            request.addProperty("JsonOb_FuelData", polydata.toString());
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
            androidHttpTransport.call(SOAP_ACTION + METHOD_NAME, envelope);
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            Log.d("ForResult", result.toString());
            return result.toString();
        } catch (Exception e) {
            Log.d("Error ForResult", e.toString());
            return "error";
        }
    }

    public String saveRegister(String ID_CARD, String EMP_ID, String FNAME, String LNAME, String TEL) {
        try {
            METHOD_NAME = "SaveRegisterFuel";
            soap_property = new SOAPWebserviceProperty();
            soap_property.urlWebservice = url;
            soap_property.namespaceWebservice = NAMESPACE;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            JSONObject polydata = new JSONObject();
            polydata.put("ID_CARD", ID_CARD);
            polydata.put("EMP_ID", EMP_ID);
            polydata.put("FNAME", FNAME);
            polydata.put("LNAME", LNAME);
            polydata.put("TEL", TEL);
            request.addProperty("JsonOb_UserData", polydata.toString());
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
            androidHttpTransport.call(SOAP_ACTION + METHOD_NAME, envelope);
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            return result.toString();
        } catch (Exception e) {
            Log.d("Error Regist", e.toString());
            return "error";
        }
    }

    public String checkLogin(String ID_CARD, String EMP_ID) {
        try {
            METHOD_NAME = "CheckLogInFuel";
            soap_property = new SOAPWebserviceProperty();
            soap_property.urlWebservice = url;
            soap_property.namespaceWebservice = NAMESPACE;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("Id_Card", ID_CARD);
            request.addProperty("EMP_ID", EMP_ID);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
            androidHttpTransport.call(SOAP_ACTION + METHOD_NAME, envelope);
            SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            return result.toString();
        } catch (Exception e) {
            Log.d("Error Login", e.toString());
            return "error";
        }
    }

}
