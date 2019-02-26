package nu.borjessons.web.game_backend.helpers;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {


  public static String hashPassword(String str) throws NoSuchAlgorithmException {
     
	  try { 
      MessageDigest md = MessageDigest.getInstance("SHA-1"); 

      byte[] messageDigest = md.digest(str.getBytes()); 
     
      BigInteger no = new BigInteger(1, messageDigest); 
      
      String hashStr = no.toString(16); 
    
      while (hashStr.length() < 32) { 
    	  hashStr = "0" + hashStr; 
      }
      return hashStr;
	  
	  } catch (NoSuchAlgorithmException e) { 
          throw new RuntimeException(e); 
      } 
  }
}