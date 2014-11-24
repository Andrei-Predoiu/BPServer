/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calls.gcm;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 2Xmatch
 */
public class GcmSender {

   private Map<String, String> devices;
   private static GcmSender instance = null;
   private final String apiKey = "AIzaSyB7Rx5MYOQD1fHnCFFD8KUY8S6SZkF-3j0";

   GcmSender() {
      devices = new HashMap<>();
   }

   public static GcmSender getInstance() {
      if (instance == null) {
         instance = new GcmSender();
      }
      return instance;
   }

   public synchronized boolean registerDevice(String deviceType, String registrationId) {
      if ((!deviceType.equals("phone")) && (!deviceType.equals("glasses"))) {
         return false;
      }
      this.devices.put(deviceType, registrationId);
      return true;
   }

   public synchronized String sendMessage(String deviceName, int numOfRetries, String data) {
      if ((!deviceName.equals("phone")) && (!deviceName.equals("glasses"))) {
         return "Invalid Device Name";
      } else if (!this.devices.containsKey(deviceName)) {
         return deviceName + "Not registered";
      }
      System.out.println("Broadcasting Message: " + data + " to:" + devices.get(deviceName));
      Sender sender = new Sender(apiKey);
      Message message = new Message.Builder()
              .addData("message", data)
              .build();
      Result result;
      try {
         result = sender.send(message, devices.get(deviceName), numOfRetries);
      } catch (IOException ex) {
         Logger.getLogger(GcmSender.class.getName()).log(Level.SEVERE, null, ex);
         return "IOException";
      }
      return result.getErrorCodeName();
   }
}
