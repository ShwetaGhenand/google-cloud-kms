package com.example.demo.model;

public class CryptoKeyModel {

	private String projectId;
	
	private String locationId;
	
	private String keyRingId;
	
	private String cryptoKeyId;

	public CryptoKeyModel() {
		super();
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getKeyRingId() {
		return keyRingId;
	}

	public void setKeyRingId(String keyRingId) {
		this.keyRingId = keyRingId;
	}

	public String getCryptoKeyId() {
		return cryptoKeyId;
	}

	public void setCryptoKeyId(String cryptoKeyId) {
		this.cryptoKeyId = cryptoKeyId;
	}
	
}
