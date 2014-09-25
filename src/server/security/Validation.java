package server.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import server.DataManipulator;
import server.model.Area;
import server.model.SplitAreas;

public class Validation {
	private static Encription enc = Encription.getInstance();
	private static DataManipulator dbh = DataManipulator.getInstance();
	private static Validation instance = null;

	private Validation() {

	}

	public static Validation getInstance() {
		if (instance == null) {
			instance = new Validation();
		}

		return instance;
	}

	public synchronized ArrayList<Area> validateAreas(ArrayList<Area> areas) {
		for (Area a : areas) {
			String computed = enc.areaToHash(a);
			if (computed != a.getCircle_hash()) {
				areas.remove(a);
				System.out.println("Hash mismatch for circle:\nLatitude: "
						+ a.getLatitude() + "\nLongitude: " + a.getLongitude()
						+ "\nRadius: " + a.getRadius() + "\nExpected: "
						+ a.getCircle_hash() + "\nComputed: " + computed);
			}
		}
		return areas;
	}

	// Probably big performance drain below
	public synchronized SplitAreas splitAreas(String username,
			ArrayList<Area> areas) {
		SplitAreas result = new SplitAreas();
		ArrayList<Area> serverAreas = dbh.getAreas(username);
		Collections.sort(areas, new Comparator<Area>() {
			@Override
			public int compare(Area e1, Area e2) {
				String id1 = ((Area) e1).getCircle_hash();
				String id2 = ((Area) e2).getCircle_hash();

				// ascending order
				return id1.compareTo(id2);
			}
		});

		Collections.sort(serverAreas, new Comparator<Area>() {
			@Override
			public int compare(Area e1, Area e2) {
				String id1 = ((Area) e1).getCircle_hash();
				String id2 = ((Area) e2).getCircle_hash();

				// ascending order
				return id1.compareTo(id2);
			}
		});
		int i = 0;
		for (Area a : areas) {
			if (a.getCircle_hash().equals(serverAreas.get(i).getCircle_hash())) {
				result.existingAreas.add(a);
				i++;
			} else {
				result.newAreas.add(a);
			}
		}
		return result;
	}
}
