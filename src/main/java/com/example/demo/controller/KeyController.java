package com.example.demo.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.Constants;
import com.example.demo.model.CryptoKeyModel;
import com.example.demo.model.KeyRingModel;
import com.example.demo.utils.CommonUtils;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(tags = "API Key service", value = "API Key service")
@RequestMapping(Constants.KMS_SERVICE_ENDPOINT)
public class KeyController {

	private static final Logger LOG = LoggerFactory.getLogger(KeyController.class);

	@Autowired
	private CommonUtils utils;

	@PostMapping(path = "/keyring", consumes = "application/json")
	@ApiOperation(value = "Create New Key Ring", notes = "return creation status", response = Object.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 401, message = "Authentication credentials are required") })
	public String createKeyRing(@RequestBody KeyRingModel keyRingData) {
		String keyRing = null;
		try {
			keyRing = CommonUtils.createKeyRing(keyRingData);

		} catch (IOException e) {
			LOG.error("Exception caught::" + e.getLocalizedMessage(), e);
		}
		return keyRing;
	}

	@PostMapping(path = "/cryptokey/", consumes = "application/json")
	@ApiOperation(value = "Create New Crypto Key", notes = "return creation status", response = Object.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 401, message = "Authentication credentials are required") })
	public String createCryptoKey(@RequestBody CryptoKeyModel cryptoKeyData) {
		String cryptoKey = null;
		try {
			cryptoKey = CommonUtils.createCryptoKey(cryptoKeyData);

		} catch (IOException e) {
			LOG.error("Exception caught::" + e.getLocalizedMessage(), e);
		}
		return cryptoKey;
	}

	@GetMapping(path = "/keyring/")
	@ApiOperation(value = "Get All key ring", notes = "return creation status", response = Object.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 401, message = "Authentication credentials are required") })
	public List<Object> getAllKeyRing(@RequestParam(name = "projectId") String projectId,
			@RequestParam(name = "locationId") String locationId) {
		List<Object> keyRingList = new ArrayList<Object>();
		try {
			keyRingList = CommonUtils.getAllKeyRing(projectId, locationId);
		} catch (IOException e) {
			LOG.error("Exception caught::" + e.getLocalizedMessage(), e);
		}
		return keyRingList;
	}

	@GetMapping(path = "/cryptokey/")
	@ApiOperation(value = "Get all crypto key", notes = "return creation status", response = Object.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 401, message = "Authentication credentials are required") })
	public List<Object> getcryptoKey(@RequestParam(name = "projectId") String projectId,
			@RequestParam(name = "locationId") String locationId, @RequestParam(name = "keyRingId") String keyRingId) {
		List<Object> keyList = new ArrayList<Object>();
		try {
			keyList = CommonUtils.getAllCryptoKey(projectId, locationId, keyRingId);
		} catch (IOException e) {
			LOG.error("Exception caught::" + e.getLocalizedMessage(), e);
		}
		return keyList;

	}

	@PostMapping(path = "/encrypt")
	@ApiOperation(value = "Encrypt plaintext", notes = "return creation status", response = Object.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 401, message = "Authentication credentials are required") })
	public String encryptData(@RequestBody String plainText) {
		byte[] byteData = plainText.getBytes();
		String cipherText = null;
		try {
			cipherText = new String(utils.encrypt(byteData));
			if (cipherText != null) {
				return "Data encrypted successfully";
			}

		} catch (IOException e) {
			LOG.error("Exception caught::" + e.getLocalizedMessage(), e);
		}
		return " Error in decrypting data";

	}

	@GetMapping(path = "/decrypt")
	@ApiOperation(value = "Decrypt file contents", notes = "return creation status", response = Object.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 401, message = "Authentication credentials are required") })
	public String decreptFileData() {
		String plainText = null;
		try {
			plainText = new String(utils.decrypt());

		} catch (IOException | URISyntaxException e) {
			LOG.error("Exception caught::" + e.getLocalizedMessage(), e);
		}
		return plainText;

	}

}
