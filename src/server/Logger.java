package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

/**
 *
 * @author Andrei Predoiu
 */
public final class Logger {

   private static Logger instance = null;
   private static String logPath;
   private static final SimpleDateFormat finalDF = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss");
   private static BufferedWriter output;
   private static Date timestamp;

   private Logger(String path) throws IOException {
      Logger.logPath = path;
      timestamp = new Date();
      output = new BufferedWriter(new FileWriter(new File(logPath)));
   }

   public static Logger getInstance(String path) {
      if (instance == null) {
         try {
            instance = new Logger(path);
            instance.write(-1, "Server Started\n\n");
         } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
         }
      }

      return instance;
   }

   /**
    * Writes a message to the log file
    *
    * @param senderID -1 = debug, 0 = server, 1 = patient, 2 = student
    * @param message the message that will be written to the file
    */
   public synchronized static void write(int senderID, String message) {
      if (message == null) {
         message = "**empty**";
      }
      try {
         String header = "";
         timestamp.setTime(System.currentTimeMillis());
         header += finalDF.format(timestamp) + " | ";
         switch (senderID) {
            case -1: {
               header += "DEBUG: ";
               break;
            }
            case 0: {
               header += "Server: ";
               break;
            }
            case 1: {
               header += "Patient: ";
               break;
            }
            case 2: {
               header += "Student: ";
               break;
            }
            default: {
               throw (new IncompatibleClassChangeError(message));
            }
         }
         header += "\n";
         try {
            output.write(header + message + "\n");
            output.flush();
         } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, "CAN'T WRITE", ex);
            System.out.println("CAN'T WRITE");
         }
      } catch (IncompatibleClassChangeError ex) {
         java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.WARNING, "Invalid sender id: " + senderID, ex);
      }
   }
}
