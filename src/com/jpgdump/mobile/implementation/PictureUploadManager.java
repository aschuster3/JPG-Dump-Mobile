package com.jpgdump.mobile.implementation;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import android.util.Base64;
import android.webkit.MimeTypeMap;

import com.jpgdump.mobile.BuildConfig;
import com.jpgdump.mobile.interfaces.UploadInterface;
import com.jpgdump.mobile.util.ContextFormattingLogger;

public class PictureUploadManager implements UploadInterface
{
    private final ContextFormattingLogger log = ContextFormattingLogger.getLogger(this);
    
    @Override
    public int uploadPicture(String filePath, String fileName, String sessionKey, String sessionId)
    {
        String urlToConnect = "http://jpgdump.com/api/v1/uploads";
        File fileToUpload = new File(filePath);
        String boundary = "27" + Long.toString(System.currentTimeMillis()); // Just generate some unique random value.
        byte[] data;
        
        String[] fileType = fileName.split("\\.");
        
        String  hyphens = "-----------------------------",
                twoHyphens = "--",
                imageInfo = " Content-Disposition: form-data; name=\"file\"; filename=\""+ fileName + 
                        "\" Content-Type: " + 
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileType[fileType.length - 1]) + " ";
        
        int responseCode = 1337;
        try
        {
            URL obj = new URL(urlToConnect);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            
            data = IOUtils.toByteArray(new FileInputStream(fileToUpload));
            
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Jpgdump-Session-Key", sessionKey);
            con.setRequestProperty("X-Jpgdump-Session-Id", sessionId);
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + hyphens + boundary);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            
            
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            
            String wholeDamnThing = hyphens + boundary +imageInfo + 
                    Base64.encodeToString(data, Base64.NO_PADDING + Base64.NO_WRAP) +
                    hyphens + boundary + twoHyphens;
            
            wr.writeChars(wholeDamnThing);
//            wr.writeChars(Base64.encodeToString(data, Base64.DEFAULT));
//            wr.writeChars("\n" + hyphens + boundary + twoHyphens);
            
            wr.flush();
            wr.close();

            responseCode = con.getResponseCode();
            
            log.i("%s", wholeDamnThing);
            log.i("Response Message: \n%s", con.getResponseMessage());
        }
        catch (Exception e)
        {
            if(BuildConfig.DEBUG)
            {
                log.i(e.getMessage(), e);
            }
        }
        return responseCode;
    }
    
   // -----------------------------277462624428218 Content-Disposition: form-data; name="file"; filename="dog-hat-pipe.jpg" Content-Type: image/jpeg [picture data] -----------------------------277462624428218--

}
