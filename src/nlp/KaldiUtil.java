/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.io.File;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author reynhart
 */
public class KaldiUtil {
    
    String SERVER_URL="http://localhost:8888/client/dynamic/recognize";
    //String FILE_URL ="tmp/rec-working-3.wav"; //uncomment this for voice recognition demo
    String FILE_URL ="tmp/rec.wav";
    
    public  String GetSoundInText()
    {
        String retVal="";
        
        retVal = ProcessSound();
        
        return retVal;
    }
    
    private String ProcessSound()
    {
        String retVal = "";
        try{
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            HttpPost httppost = new HttpPost(SERVER_URL);
            File file = new File(FILE_URL);

            MultipartEntity mpEntity = new MultipartEntity();
            ContentBody cbFile = new FileBody(file, "audio/x-wav");
            mpEntity.addPart("userfile", cbFile);


            httppost.setEntity(mpEntity);
            System.out.println("executing request " + httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();
            
            
            JSONObject obj = new JSONObject(EntityUtils.toString(resEntity));
            
            JSONArray objData = obj.getJSONArray("hypotheses");
            int n = objData.length();
            
            JSONObject content = new JSONObject(objData.get(0).toString());
            
            retVal = content.getString("utterance");
            //System.out.println(retVal);
            
            //System.out.println(response.getStatusLine());
//            if (resEntity != null) {
//              System.out.println(EntityUtils.toString(resEntity));
//            }
//            if (resEntity != null) {
//              resEntity.consumeContent();
//            }

            httpclient.getConnectionManager().shutdown();
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
        
        
        
        
        return retVal;
    }
    
}
