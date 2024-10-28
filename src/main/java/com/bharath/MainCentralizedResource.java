package com.bharath;


import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class MainCentralizedResource {
	public static final Logger LOGGER = Logger.getLogger(MainCentralizedResource.class.toString());
	static{
        try{
        	FileInputStream fileInputStream = new FileInputStream("C:\\Users\\bharath-22329\\workspace\\BharathSocialMediaAppRestFullWebServ\\src\\main\\java\\com\\bharath\\conf\\log.properties");
            LogManager.getLogManager().readConfiguration(fileInputStream);
            FileHandler fileHandler = new FileHandler("C:\\Users\\bharath-22329\\workspace\\BharathSocialMediaAppRestFullWebServ\\src\\main\\java\\com\\bharath\\log\\application"+LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)+".log");
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.severe(e.toString());
        }
	}
	
	 public static String generateHashedPassword(String password){
	        MessageDigest md = null;
	        try {
	            md = MessageDigest.getInstance("SHA-256");
	        } catch (NoSuchAlgorithmException e) {
	            MainCentralizedResource.LOGGER.severe(e.toString());
	            return null;
	        }
	        byte[] shaBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

	        BigInteger number = new BigInteger(1, shaBytes);

	        StringBuilder hexString = new StringBuilder(number.toString(16));

	        while (hexString.length() < 32)
	        {
	            hexString.insert(0, '0');
	        }

	        return hexString.toString();
	    }
}
