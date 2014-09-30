package server;

public class DataManipulator {
	private static DataManipulator instance = null;

	private boolean phoneConnect = false;
	private boolean glassConnect = false;

	private DataManipulator() {
	}

	public synchronized static DataManipulator getInstance() {
		if (instance == null) {
			instance = new DataManipulator();
		}
		return instance;
	}

	public synchronized boolean verifyLogin(String type, String code) {
		if (type.equals("phone") || !phoneConnect) {
			phoneConnect = true;
			return true;
		}
		if (type.equals("glasses") || !glassConnect) {
			glassConnect = true;
			return true;
		}
		return false;
	}

	public synchronized boolean start() {
		if (phoneConnect && glassConnect)
			return true;
		return false;
	}
}