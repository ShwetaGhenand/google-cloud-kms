package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.example.demo.common.Constants;

@Configuration
public class EnvironmentConfig {

	@Autowired
	private Environment env;

	public String getProjectId() {
		return env.getProperty(Constants.PROJECT_ID);
	}

	public String getLocationId() {
		return env.getProperty(Constants.LOCATION_ID);
	}

	public String getKeyRingId() {
		return env.getProperty(Constants.KEY_RING_ID);
	}

	public String getCryptoKey() {
		return env.getProperty(Constants.CRYPTO_KEY_ID);
	}
}
