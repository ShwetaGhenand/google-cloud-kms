package com.example.demo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.common.Constants;
import com.example.demo.config.EnvironmentConfig;
import com.example.demo.model.CryptoKeyModel;
import com.example.demo.model.KeyRingModel;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.kms.v1.CryptoKey;
import com.google.cloud.kms.v1.CryptoKey.CryptoKeyPurpose;
import com.google.cloud.kms.v1.CryptoKeyName;
import com.google.cloud.kms.v1.DecryptResponse;
import com.google.cloud.kms.v1.EncryptResponse;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.kms.v1.KeyManagementServiceClient.ListKeyRingsPagedResponse;
import com.google.cloud.kms.v1.KeyRing;
import com.google.cloud.kms.v1.KeyRingName;
import com.google.cloud.kms.v1.LocationName;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;

@Component
public class CommonUtils {

	private static final Logger LOG = LoggerFactory.getLogger(CommonUtils.class);

	@Autowired
	private EnvironmentConfig env;

	// Get all objects from bucket
	public static void authExplicit(String jsonPath) throws IOException {

		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath))
				.createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

		Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

		Page<Bucket> buckets = storage.list();
		for (Bucket bucket : buckets.iterateAll()) {
			LOG.info("bucket data::" + bucket.toString());
		}
	}

	public static String createKeyRing(KeyRingModel keyRingData) throws IOException {

		LOG.info("inside method");
		// Create the Cloud KMS client.
		try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {

			// The resource name of the location associated with the KeyRing.
			String parent = LocationName.format(keyRingData.getProjectId(), keyRingData.getLocationId());

			// Create the KeyRing for your project.
			KeyRing keyRing = client.createKeyRing(parent, keyRingData.getKeyRingId(), KeyRing.newBuilder().build());

			return keyRing.toString();
		}
	}

	/**
	 * Creates a new crypto key with the given id.
	 */
	public static String createCryptoKey(CryptoKeyModel cryptokeyData) throws IOException {

		// Create the Cloud KMS client.
		try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
			// The resource name of the location associated with the KeyRing.
			String parent = KeyRingName.format(cryptokeyData.getProjectId(), cryptokeyData.getLocationId(),
					cryptokeyData.getKeyRingId());

			// This will allow the API access to the key for encryption and decryption.
			CryptoKey cryptoKey = CryptoKey.newBuilder().setPurpose(CryptoKeyPurpose.ENCRYPT_DECRYPT).build();

			// Create the CryptoKey for your project.
			CryptoKey createdKey = client.createCryptoKey(parent, cryptokeyData.getCryptoKeyId(), cryptoKey);

			return createdKey.toString();
		}
	}

	public static List<Object> getAllKeyRing(String projectId, String locationId) throws IOException {

		List<Object> cryptoKeyList = new ArrayList<Object>();
		// Create the KeyManagementServiceClient using try-with-resources to manage
		// client cleanup.
		try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {

			// The resource name of the location to search
			String locationPath = LocationName.format(projectId, locationId);

			// Make the RPC call
			ListKeyRingsPagedResponse response = client.listKeyRings(locationPath);

			for (KeyRing keyRing : response.iterateAll()) {
				cryptoKeyList.add(keyRing.getName());
			}
		}
		return cryptoKeyList;
	}

	public static List<Object> getAllCryptoKey(String projectId, String locationId, String keyRingId)
			throws IOException {

		List<Object> keyList = new ArrayList<Object>();
		try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {
			KeyRingName parent = KeyRingName.of(projectId, locationId, keyRingId);
			for (CryptoKey element : client.listCryptoKeys(parent).iterateAll()) {
				keyList.add(element.getName());
			}
		}
		return keyList;
	}

	public byte[] encrypt(byte[] plaintext) throws IOException {

		try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {

			// The resource name of the cryptoKey
			String resourceName = CryptoKeyName.format(env.getProjectId(), env.getLocationId(), env.getKeyRingId(),
					env.getCryptoKey());

			// Encrypt the plaintext with Cloud KMS.
			EncryptResponse response = client.encrypt(resourceName, ByteString.copyFrom(plaintext));

			File file = new File(Constants.FILE_PATH);
			boolean fvar = file.createNewFile();
			if (!fvar) {
				LOG.error("File already present at the specified location");
			}
			FileOutputStream fos = new FileOutputStream(Constants.FILE_PATH);
			fos.write(response.getCiphertext().toByteArray());
			fos.flush();
			fos.close();

			// Extract the ciphertext from the response.
			return response.getCiphertext().toByteArray();
		}
	}

	/**
	 * Decrypts the provided ciphertext with the specified crypto key.
	 * 
	 * @throws URISyntaxException
	 */
	public byte[] decrypt() throws IOException, URISyntaxException {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream(Constants.ENCRYPTED_FILE_NAME);

		byte[] fileContent = IOUtils.toByteArray(is);

		try (KeyManagementServiceClient client = KeyManagementServiceClient.create()) {

			// The resource name of the cryptoKey
			String resourceName = CryptoKeyName.format(env.getProjectId(), env.getLocationId(), env.getKeyRingId(),
					env.getCryptoKey());

			// Decrypt the ciphertext with Cloud KMS.
			DecryptResponse response = client.decrypt(resourceName, ByteString.copyFrom(fileContent));

			// Extract the plaintext from the response.
			return response.getPlaintext().toByteArray();
		}

	}

}
