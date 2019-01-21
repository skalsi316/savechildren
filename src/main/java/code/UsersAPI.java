package code;
 
import java.util.Arrays;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import code.beans.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class UsersAPI {
    @Autowired
	static UsersOptionsMapper mUsersOptions;	

    private static final Logger log = LoggerFactory.getLogger(UsersAPI.class);

    static final String URL = "http://my-json-server.typicode.com/skalsi316/restapi/users";
    public static final String USER_NAME = "admin";
    public static final String PASSWORD = "123";

    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private static KeySpec ks;
    private static SecretKeyFactory skf;
    private static Cipher cipher;
    static byte[] arrayBytes;
    private static String myEncryptionKey;
    private static String myEncryptionScheme;
    static SecretKey key;
    
    public static void main(String[] args) {

    	// HttpHeaders
        HttpHeaders headers = new HttpHeaders(); 
        headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));        
        
        // Authentication
        String auth = USER_NAME + ":" + PASSWORD;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader);
        // 
        
        // Request to return JSON format
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UsersOptions[]> entity = new HttpEntity<UsersOptions[]>(headers);
 
        // RestTemplate
        RestTemplate restTemplate = new RestTemplate();
 
        // Send request with GET method, and Headers.
        ResponseEntity<UsersOptions[]> response = restTemplate.exchange(URL, HttpMethod.GET, entity, UsersOptions[].class);
        log.info("response: " + response);

        HttpStatus statusCode = response.getStatusCode();
        log.info("Response Satus Code: " + statusCode);
                
        // Status Code: 200
        if (statusCode == HttpStatus.OK) {
            // Response Body Data
        	UsersOptions[] users = response.getBody();

            if (users != null) {
            	for (UsersOptions options : users) {
                    log.info("User: " + options.toString());

            		String userID = options.getMsisdn();
            		String userPswd = options.getPswd();
            		
            		//encrypting username
            		String encryptUsername = encrypt(userID);
                    log.info("Encrypt UserID: " + encrypt(userID));
            		
            		//check if user entry is in the table
                	checkUserEntry(encryptUsername, options);
                }
            }
        } 
        else {
        	log.info("Error in connecting to: " + URL);
        }
    }
    
    public static String encrypt(String userID){
    	String encrypted = null;
    	        
        try {
        	myEncryptionKey = "ThisIsSpartaThisIsSparta";
            myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
            arrayBytes = myEncryptionKey.getBytes("UTF-8");
            ks = new DESedeKeySpec(arrayBytes);
            skf = SecretKeyFactory.getInstance(myEncryptionScheme);
            cipher = Cipher.getInstance(myEncryptionScheme);
            key = skf.generateSecret(ks);

            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = userID.getBytes("UTF8");
            byte[] encryptedText = cipher.doFinal(plainText);
            encrypted = new String(Base64.encodeBase64(encryptedText));
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return encrypted;
    }
    
    public static String decrypt(String encstr) {
    	String decryptedText=null;
        try {
        	cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.decodeBase64(encstr);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText= new String(plainText);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }
    
    public static void checkUserEntry(String encryptedUserId, UsersOptions userInfo){
    	String json="";
    	
    	String userId = userInfo.getMsisdn();
    	String email = userInfo.getEmail();
    	String name = userInfo.getName();
    	String userPswd = userInfo.getPswd();
    	    
        //DB connection 
        UsersOptions userDBInfo = mUsersOptions.selectByUserID(encryptedUserId);
		if (userDBInfo == null){
	        log.info("User does not existing in the table, adding entry");

			//insert the user in the db
			// fills the Bean with data
			UsersOptions pinBean = new UsersOptions();

			pinBean.setMsisdn(encryptedUserId);		    	
			pinBean.setPswd(userPswd);

			//fill in the bean data
			mUsersOptions.insertUsersOptionsData(pinBean);
			json = "{ user: " + userId + ", response: accessValidate"	+ "}"; 
		} 
		else {
	        log.info("User is in the table");

	        String decryptedUserID = decrypt(encryptedUserId);
	        log.info("Decrypt for user: " + decryptedUserID);

			String msisdn = userDBInfo.getMsisdn();
			if(decryptedUserID.equals(userId)){
				json = "{ user: " + userId + ", response: UpdateAlreadyDone" + "}"; 
			}
			else {
				json = "{ user: " + userId + ", response: accessNOTOK" + "}"; 					
			}
		}

    }

}