package com.fourw.util;

import java.io.Serializable;

public class Position implements Serializable {
	private static final long serialVersionUID = 1L;
	double latitude;
	double longitude;

	public Position() {
		latitude = 0;
		longitude = 0;
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

}
