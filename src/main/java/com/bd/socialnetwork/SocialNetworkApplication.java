package com.bd.socialnetwork;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileReader;

@SpringBootApplication
public class SocialNetworkApplication {
	/**
	 * load initial user data by inserting users from given JSON file
	 */
	public static void loadData() {
		JSONParser jsonParser = new JSONParser();
		try {
			String jsonPath = "src/main/resources/data/userData.json";
			Object object = jsonParser.parse(new FileReader(jsonPath));

			//convert Object to JSONObject
			JSONObject jsonObject = (JSONObject) object;
			//System.out.println(jsonObject.get("ctRoot"));
			JSONArray listUsers = (JSONArray) jsonObject.get("ctRoot");
			for (Object user : listUsers) {
				JSONObject userObject = (JSONObject) user;
				HttpPost post = new HttpPost("http://localhost:8080/addUser");

				StringEntity params = new StringEntity(userObject.toString());
				post.addHeader("content-type", "application/json");
				post.setEntity(params);
				try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
					httpClient.execute(post);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(SocialNetworkApplication.class, args);
		//loadData();
	}
}
