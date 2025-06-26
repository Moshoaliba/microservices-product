package com.kayy.microservices.product;

import io.restassured.RestAssured;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.testcontainers.containers.MongoDBContainer;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {
	@ServiceConnection //By adding this, No need to manually specify the mongodb uri, spring boot will auto add it
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

	@LocalServerPort //When the application is running, it will inject the port ot which the application is running to the variable "port"
	private Integer port;

	@BeforeEach
	void setup(){
		RestAssured.baseURI = "http://localhost"; //Dynamic check to see which port is running
		RestAssured.port = port;
	}

	static {
		mongoDBContainer.start();

	}

	@Test
	void shouldCreateProduct() {  //Below, is the multiline string in java.
		String requestBody = """            
				{
					"name": "Iphone 14",
					"description": "Iphone 14 pro max",
					"price": 1200
				}
				""";

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/product")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("Iphone 14"))
				.body("description", Matchers.equalTo("Iphone 14 pro max"))
				.body("price", Matchers.equalTo(1200)); //Rest assured will convert the double to float automatically
	}

}
