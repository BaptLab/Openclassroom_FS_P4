package integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.starterjwt.SpringBootSecurityJwtApplication;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;

@SpringBootTest(classes = SpringBootSecurityJwtApplication.class)
@AutoConfigureMockMvc
public class AuthControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void testRegisterUser() throws Exception {

		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setEmail("user@test.com");
		signupRequest.setFirstName("UserTest");
		signupRequest.setLastName("UserTest");
		signupRequest.setPassword("testpwd");

		String jsonRequest = objectMapper.writeValueAsString(signupRequest);

		mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
				.andExpect(status().isOk());
		
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("user@test.com");
		loginRequest.setPassword("testpwd");
		String loginJsonRequest = objectMapper.writeValueAsString(loginRequest);

		MvcResult result = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJsonRequest))
				.andExpect(status().isOk()).andReturn();
		
		String responseBody = result.getResponse().getContentAsString();
		String authToken = objectMapper.readTree(responseBody).get("token").textValue();
		Integer userId = objectMapper.readTree(responseBody).get("id").intValue();
		String stringUserId = String.valueOf(userId);
		
		mockMvc.perform(delete("/api/user/{id}", stringUserId).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + authToken)) // Include the token in the header
				.andExpect(status().isOk());

	}

	@Test
	public void testLoginWithValidCredentials() throws Exception {

		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("yoga@studio.com");
		loginRequest.setPassword("test!1234");
		String jsonRequest = objectMapper.writeValueAsString(loginRequest);

		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testLoginWithInvalidCredentials() throws Exception {

		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("invalid@gmail.com");
		loginRequest.setPassword("wrongpwd");
		String jsonRequest = objectMapper.writeValueAsString(loginRequest);

		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
				.andExpect(status().isUnauthorized());
	}

}
