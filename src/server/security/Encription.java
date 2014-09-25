package server.security;

/**
 * File Name : PasswordEncoder.java
 * Created on: 1 Jul 2010
 * Created by: Ashish Shukla
 * Orange Hut Solution Limited.
 * http://www.orangehut.com
 */

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import server.model.Area;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author Ashish Shukla, Modified by Andrei Predoiu
 *
 */
public class Encription {
	/**
	 * Logger for this class
	 */

	private static Encription instance;

	/**
	 * Count for the number of time to hash, more you hash more difficult it
	 * would be for the attacker
	 */
	private final static int ITERATION_COUNT = 5;

	/**
	 * Empty Constructor
	 */
	private Encription() {
	}

	/**
	 * @return
	 * @author Ashish Shukla
	 */
	public static synchronized Encription getInstance() {

		if (instance == null) {
			Encription returnPasswordEncoder = new Encription();

			return returnPasswordEncoder;
		} else {

			return instance;
		}
	}

	/**
	 *
	 * @param password
	 * @param saltKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @author Ashish Shukla, modified by Andrei Predoiu
	 */
	public synchronized String encode(String password, byte[] saltKey)
			throws NoSuchAlgorithmException, IOException {

		String encodedPassword = null;

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.reset();
		digest.update(saltKey);

		byte[] btPass = digest.digest(password.getBytes("UTF-8"));
		for (int i = 0; i < ITERATION_COUNT; i++) {
			digest.reset();
			btPass = digest.digest(btPass);
		}

		encodedPassword = byteToBase64(btPass);

		return encodedPassword;
	}

	/**
	 *
	 * @param password
	 * @param saltKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @author Andrei Predoiu
	 */
	public synchronized String encode(String password, String saltKey)
			throws NoSuchAlgorithmException, IOException {
		byte[] salt = base64ToByte(saltKey);

		return encode(password, salt);
	}

	/**
	 * @param str
	 * @return byte[]
	 * @throws IOException
	 */
	private byte[] base64ToByte(String str) throws IOException {

		BASE64Decoder decoder = new BASE64Decoder();
		byte[] returnbyteArray = decoder.decodeBuffer(str);

		return returnbyteArray;
	}

	/**
	 * @param bt
	 * @return String
	 * @throws IOException
	 */
	public String byteToBase64(byte[] bt) {

		BASE64Encoder endecoder = new BASE64Encoder();
		String returnString = endecoder.encode(bt);

		return returnString;
	}

	// Probably performance drain below
	public String areaToHash(Area area) {
		try {
			return this.encode(String.valueOf(area.getLatitude()),
					(String.valueOf(area.getLongitude())));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("areaToHash failed NoSuchAlgorithmException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("areaToHash failed IOException");
			e.printStackTrace();
		}
		return null;
	}
}
