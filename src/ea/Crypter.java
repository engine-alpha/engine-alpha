package ea;

import java.nio.charset.Charset;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.*;

public class Crypter {
	 public String verschluesseln(String text, String schluessel){
	 if(schluessel.lenght() == 16){
	   Key key;
	   String encryptedValue=null;
		try {
	  	key = new SecretKeySpec(schluessel.getBytes(Charset.forName("UTF-8")), "AES");
	  	Cipher c = Cipher.getInstance("AES");
	  	c.init(Cipher.ENCRYPT_MODE, key);
	  	byte[] encValue = c.doFinal(text.getBytes());
	  	encryptedValue = new BASE64Encoder().encode(encValue);
		}catch(Exception e){e.printStackTrace();}
	    return encryptedValue;
	    }else{
		 System.out.println("Der Schluessel muss 16 Zeichen haben!");
	    }
	}

	public String entschluesseln(String text,String schluessel){
	if(schluessel.lenght() == 16){
	    Key key;
	    String decryptedValue = null;
		try {
	  	key = new SecretKeySpec(schluessel.getBytes(Charset.forName("UTF-8")), "AES");
	  	Cipher c = Cipher.getInstance("AES");
	  	c.init(Cipher.DECRYPT_MODE, key);
		  byte[] decordedValue = new BASE64Decoder().decodeBuffer(text);
		  byte[] decValue = c.doFinal(decordedValue);
		  decryptedValue = new String(decValue);
		}catch(Exception e){e.printStackTrace();}
	    return decryptedValue;
	     }else{
		 System.out.println("Der Schluessel muss 16 Zeichen haben!");
	    }
	}
}
