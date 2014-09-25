package server.model;

public class Area {
	private String user;
	private String circle_hash;
	private double latitude;
	private double longitude;
	private int radius;
	private Settings settings;

	public Area(String user, double latitude, double longitude, int radius,
			String circle_hash, Settings settings) {
		super();
		this.user = user;
		this.circle_hash = circle_hash;
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
		this.settings = settings;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getCircle_hash() {
		return circle_hash;
	}

	public void setCircle_hash(String circle_hash) {
		this.circle_hash = circle_hash;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}
}
