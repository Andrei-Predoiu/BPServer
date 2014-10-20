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
 * @author 2Xmatch
 */
public final class Logger {

   private final String logPath;
   private final SimpleDateFormat finalDF = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
   private final File logFile;
   private final Date timestamp;

   public Logger(String path) {
      this.logPath = path;
      this.logFile = new File(logPath);
      timestamp = new Date();
      write(-1, "New logging session!!!!!");
   }

   /**
    * Writes a message to the log file
    *
    * @param senderID -1 = debug, 0 = server, 1 = patient, 2 = student
    * @param message the message that will be written to the file
    */
   public synchronized void write(int senderID, String message) {
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
         BufferedWriter output;
         output = new BufferedWriter(new FileWriter(logFile));
         output.write(header + message);
         output.close();
      } catch (IncompatibleClassChangeError ex) {
         java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.WARNING, "Invalid sender id: " + senderID, ex);
      } catch (IOException ex) {
         java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, "File doesn't exist or can't write to file!", ex);
         ex.printStackTrace();
      }
   }
}
