package server;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import server.model.Area;
import server.model.Auth;
import server.model.Settings;
import server.security.Encription;

public class DataManipulator {
	private static DataManipulator instance = null;
	private SecureRandom sr;
	private Encription enc = Encription.getInstance();
	private final String databaseAdress = "jdbc:mysql://127.0.0.1:3306/mobile";

	private DataManipulator() {
		try {
			sr = SecureRandom.getInstanceStrong();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException!!!");
			e.printStackTrace();
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public synchronized static DataManipulator getInstance() {
		if (instance == null) {
			instance = new DataManipulator();
		}
		return instance;
	}

	public synchronized ArrayList<Area> getAreas(String username) {
		ArrayList<Area> areas = new ArrayList<Area>();

		String query = "Select latitude,longitude,radius,circle_hash,silent,vibrate FROM areas where user='"
				+ username + "';";

		try {
			Connection con = DriverManager.getConnection(databaseAdress,
					"root", "112");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {

				Area temp = new Area(username, rs.getDouble(1),
						rs.getDouble(2), rs.getInt(3), rs.getString(4),
						new Settings(rs.getBoolean(5), rs.getBoolean(6)));

				areas.add(temp);
			}

			con.close();

		}

		catch (SQLException e) {
			e.printStackTrace();
		}
		return areas;
	}

	public synchronized ArrayList<Auth> getUsers() {
		ArrayList<Auth> users = new ArrayList<Auth>();

		String query = "SELECT username,salt,hash FROM users;";

		try {
			Connection con = DriverManager.getConnection(databaseAdress,
					"root", "112");
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				Auth temp = new Auth(rs.getString(1), rs.getString(2),
						rs.getString(3));

				users.add(temp);
			}

			con.close();

		}

		catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}

	public synchronized boolean newUser(String username, String password) {
		byte[] salt = sr.generateSeed(32);
		String hash;
		try {
			hash = enc.encode(password, salt);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException, action Aborted");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.out.println("IOException, action Aborted");
			e.printStackTrace();
			return false;
		}
		String query = "INSERT INTO `mobile`.`users` (`hash`, `username`, `salt`) VALUES ('"
				+ hash
				+ "','"
				+ username
				+ "','"
				+ enc.byteToBase64(salt)
				+ "')";
		System.out.println(query);
		try {
			Connection con = DriverManager.getConnection(databaseAdress,
					"root", "112");

			System.out.println("Created new account:\nUsername: " + username);

			Statement stmt = con.createStatement();
			if (stmt.executeUpdate(query) != 0)
				return true;

			con.close();

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public synchronized void newAreas(String user, ArrayList<Area> areas) {
		int index = 0;
		String query;

		// ToDo check if circles have hashes, update the ones that do and create
		// a hash for ones that don't
		try {
			Connection con = DriverManager.getConnection(databaseAdress,
					"root", "112");

			Statement st = con.createStatement();
			for (int i = index; i < areas.size(); i++) {
				query = "INSERT INTO areas(name, latitude, longitude, radius,circle_hash, silent, vibrate) VALUES ('"
						+ user
						+ "', "
						+ areas.get(i).getLatitude()
						+ ", "
						+ areas.get(i).getLongitude()
						+ ", "
						+ areas.get(i).getRadius()
						+ ", "
						+ areas.get(i).getCircle_hash()
						+ ", "
						+ areas.get(i).getSettings().isSilent()
						+ ", "
						+ areas.get(i).getSettings().isVibrate() + ")";
				st.executeUpdate(query);
				index++;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public synchronized void updateAreas(String user, ArrayList<Area> areas) {
		int index = 0;
		String query;

		// ToDo check if circles have hashes, update the ones that do and create
		// a hash for ones that don't
		try {
			Connection con = DriverManager.getConnection(databaseAdress,
					"root", "112");

			Statement st = con.createStatement();
			for (int i = index; i < areas.size(); i++) {
				query = "UPDATE areas SET radius=" + areas.get(i).getRadius()
						+ ", silent =" + areas.get(i).getSettings().isSilent()
						+ ", vibrate ="
						+ areas.get(i).getSettings().isVibrate()
						+ " WHERE circle_hash ="
						+ areas.get(i).getCircle_hash() + ";";
				st.executeUpdate(query);
				index++;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public synchronized void deleteAreas(String user) {
		String query = "DELETE FROM areas WHERE user ='" + user + "'";

		try {
			Connection con = DriverManager.getConnection(databaseAdress,
					"root", "112");
			Statement stmt = con.createStatement();
			stmt.executeUpdate(query);

			con.close();
		}

		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public synchronized boolean verifyLogin(String username, String password) {
		String hashGet = "SELECT hash,salt FROM users WHERE username ='"
				+ username + "'";

		String hash, salt;

		try {
			Connection con = DriverManager.getConnection(databaseAdress,
					"root", "112");
			Statement stmt = con.createStatement();
			ResultSet val = stmt.executeQuery(hashGet);
			String first, last;
			if (val.absolute(1)) {
				first = val.getString(1);
			} else {
				con.close();
				return false;
			}
			if (val.absolute(-1)) {
				last = val.getString(1);
			} else {
				con.close();
				return false;
			}
			if (first.equals(last)) {
				hash = first;
				salt = val.getString(2);
				if (hash.equals(enc.encode(password, salt))) {
					con.close();
					return true;
				}

			} else {
				con.close();
				return false;
			}

			con.close();
		}

		catch (SQLException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException, userExsists aborted");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.out.println("IOException, userExsists aborted");
			e.printStackTrace();
			return false;
		}
		return false;

	}

	public synchronized boolean isUsernameUnique(String username) {
		String countUsers = "SELECT COUNT(username) FROM users WHERE username ='"
				+ username + "'";

		try {
			Connection con = DriverManager.getConnection(databaseAdress,
					"root", "112");
			Statement stmt = con.createStatement();
			ResultSet val = stmt.executeQuery(countUsers);
			if (val.absolute(1)) {
				if (val.getInt(1) == 0) {
					return true;
				}
			} else {
				con.close();
				return false;
			}

			con.close();
		}

		catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}
}