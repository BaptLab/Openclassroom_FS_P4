package integration.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.openclassrooms.starterjwt.SpringBootSecurityJwtApplication;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;

@SpringBootTest(classes = SpringBootSecurityJwtApplication.class)
@AutoConfigureMockMvc
public class SessionControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private String authToken;

	private String stringUserId;

	private String registerJsonRequest;
	private String createSessionJsonRequest;

	@BeforeEach
	public void Register_Log_Delete_User() throws Exception {
		authToken = null;
		stringUserId = null;
		registerJsonRequest = null;

		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setEmail("user@test.com");
		signupRequest.setFirstName("UserTest");
		signupRequest.setLastName("UserTest");
		signupRequest.setPassword("testpwd");

		registerJsonRequest = objectMapper.writeValueAsString(signupRequest);

		mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(registerJsonRequest))
				.andExpect(status().isOk());

		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("user@test.com");
		loginRequest.setPassword("testpwd");
		String loginJsonRequest = objectMapper.writeValueAsString(loginRequest);

		MvcResult result = mockMvc
				.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJsonRequest))
				.andExpect(status().isOk()).andReturn();

		String responseBody = result.getResponse().getContentAsString();
		authToken = objectMapper.readTree(responseBody).get("token").textValue();
		Integer userId = objectMapper.readTree(responseBody).get("id").intValue();
		stringUserId = String.valueOf(userId);
	}

	@AfterEach
	public void detele_User() throws Exception {
		mockMvc.perform(get("/api/user/{id}", stringUserId).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + authToken)).andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName", is("UserTest")));

		mockMvc.perform(delete("/api/user/{id}", stringUserId).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + authToken));
	}

	@Test
	public void getSessionByIdTest() throws Exception {
		Long sessionId = 8L;

		mockMvc.perform(get("/api/session/{id}", sessionId).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + authToken)).andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is("Renforcement musculaire.")));
	}

	@Test
	public void getSessionByIdTest_SessionNotFound() throws Exception {
		Long sessionId = 99L;

		mockMvc.perform(get("/api/session/{id}", sessionId).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + authToken)).andExpect(status().isNotFound());
	}

	@Test
	public void getSessionByIdTest_InvalidFormat() throws Exception {
		String invalidSessionId = "invalid_id";

		mockMvc.perform(get("/api/session/{id}", invalidSessionId).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + authToken)).andExpect(status().isBadRequest());
	}

	@Test
	public void getAllSessionsTest() throws Exception {
		mockMvc.perform(get("/api/session").contentType(MediaType.APPLICATION_JSON).header("Authorization",
				"Bearer " + authToken)).andExpect(status().isOk())
				.andExpect(jsonPath("[0].name", is("Renforcement musculaire.")));
	}

	@Test
	public void createSessionTest() throws Exception {
		List<Long> mockUsers = new ArrayList<>();

		SessionDto mockSessionDTO = new SessionDto(null, "Fake Session", new Date(), 1L,
				"This is a fake session for testing purposes.", mockUsers, LocalDateTime.now(), LocalDateTime.now());

		// Perform creation and retrieve the ID from the response
		MvcResult result = mockMvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockSessionDTO))
				.header("Authorization", "Bearer " + authToken)).andExpect(status().isOk()).andReturn();

		String content = result.getResponse().getContentAsString();
		Long sessionId = objectMapper.readTree(content).path("id").asLong();
		String stringSessionId = String.valueOf(sessionId);
		// Perform retrieval and check for the presence of the created session
		mockMvc.perform(get("/api/session").contentType(MediaType.APPLICATION_JSON).header("Authorization",
				"Bearer " + authToken)).andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.id == " + stringSessionId + ")]").exists())
				.andExpect(jsonPath("$[*].name", hasItem("Fake Session")))
				.andExpect(jsonPath("$[*].description", hasItem("This is a fake session for testing purposes.")))
				.andExpect(jsonPath("$[*].teacher_id", hasItem(1)))
				.andExpect(jsonPath("$[*].users", hasItem(mockUsers)))
				.andExpect(jsonPath("$[*].createdAt").isNotEmpty()).andExpect(jsonPath("$[*].updatedAt").isNotEmpty());

		// Perform deletion using the retrieved ID
		mockMvc.perform(delete("/api/session/{id}", stringSessionId).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + authToken));

		// Perform retrieval and check that the session is no longer present
		mockMvc.perform(get("/api/session").contentType(MediaType.APPLICATION_JSON).header("Authorization",
				"Bearer " + authToken)).andExpect(status().isOk())
				.andExpect(jsonPath("$[*].id", not(hasItem(stringSessionId))));
	}

	@Test
	public void updateSessionTest() throws Exception {
		List<Long> mockUsers = new ArrayList<>();
		SessionDto mockSessionDTO = new SessionDto(null, "Fake Session", new Date(), 1L,
				"This is a fake session for testing purposes.", mockUsers, LocalDateTime.now(), LocalDateTime.now());

		SessionDto newSessionDTO = new SessionDto(null, "New Fake Session", new Date(), 1L,
				"This is a new fake session for testing purposes.", mockUsers, LocalDateTime.now(),
				LocalDateTime.now());

		// Perform creation and retrieve the ID from the response
		MvcResult result = mockMvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockSessionDTO))
				.header("Authorization", "Bearer " + authToken)).andExpect(status().isOk()).andReturn();

		String content = result.getResponse().getContentAsString();
		Long sessionId = objectMapper.readTree(content).path("id").asLong();
		String stringSessionId = String.valueOf(sessionId);
		// Perform retrieval and check for the presence of the created session
		mockMvc.perform(get("/api/session").contentType(MediaType.APPLICATION_JSON).header("Authorization",
				"Bearer " + authToken)).andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.id == " + stringSessionId + ")]").exists())
				.andExpect(jsonPath("$[*].name", hasItem("Fake Session")))
				.andExpect(jsonPath("$[*].description", hasItem("This is a fake session for testing purposes.")))
				.andExpect(jsonPath("$[*].teacher_id", hasItem(1)))
				.andExpect(jsonPath("$[*].users", hasItem(mockUsers)))
				.andExpect(jsonPath("$[*].createdAt").isNotEmpty()).andExpect(jsonPath("$[*].updatedAt").isNotEmpty());

		MvcResult putResult = mockMvc.perform(put("/api/session/{id}", stringSessionId)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(newSessionDTO))
				.header("Authorization", "Bearer " + authToken)).andExpect(status().isOk()).andReturn();

		// Now, perform assertions on the MvcResult
		String newContent = putResult.getResponse().getContentAsString();
		JsonNode updatedSession = objectMapper.readTree(newContent);

		// Example assertion on the updated session's name and description
		Assertions.assertEquals("New Fake Session", updatedSession.path("name").asText());
		Assertions.assertEquals("This is a new fake session for testing purposes.",
				updatedSession.path("description").asText());

		// Perform deletion using the retrieved ID
		mockMvc.perform(delete("/api/session/{id}", stringSessionId).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + authToken));

		// Perform retrieval and check that the session is no longer present
		mockMvc.perform(get("/api/session").contentType(MediaType.APPLICATION_JSON).header("Authorization",
				"Bearer " + authToken)).andExpect(status().isOk())
				.andExpect(jsonPath("$[*].id", not(hasItem(stringSessionId))));
	}

	@Test
	public void updateSessionNumberTest_FormatExceptionTest() throws Exception {
		// Setup
		List<Long> mockUsers = new ArrayList<>();
		SessionDto mockSessionDTO = new SessionDto(null, "Fake Session", new Date(), 1L,
				"This is a fake session for testing purposes.", mockUsers, LocalDateTime.now(), LocalDateTime.now());

		// Perform creation and retrieve the ID from the response
		MvcResult result = mockMvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockSessionDTO))
				.header("Authorization", "Bearer " + authToken)).andExpect(status().isOk()).andReturn();

		// Extract session ID from the response
		String content = result.getResponse().getContentAsString();
		Long sessionId = objectMapper.readTree(content).path("id").asLong();
		String invalidSessionId = "invalidId"; // Use an invalid ID to trigger NumberFormatException

		try {
			// Attempt to update session with an invalid ID, expect NumberFormatException
			mockMvc.perform(put("/api/session/{id}", invalidSessionId).contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(mockSessionDTO))
					.header("Authorization", "Bearer " + authToken)).andExpect(status().isBadRequest());

		} catch (NumberFormatException e) {
			// If NumberFormatException is caught, it should not propagate further
			Assertions.fail("NumberFormatException should not be thrown");
		}

		// Perform cleanup: Delete the session created earlier
		mockMvc.perform(delete("/api/session/{id}", sessionId).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + authToken));

		// Assert that the session is no longer present
		mockMvc.perform(get("/api/session").contentType(MediaType.APPLICATION_JSON).header("Authorization",
				"Bearer " + authToken)).andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.id == " + sessionId + ")]").doesNotExist());
	}

	@Test
	public void deleteSessionTest() throws Exception {
		// Setup
		List<Long> mockUsers = new ArrayList<>();
		SessionDto mockSessionDTO = new SessionDto(null, "Fake Session", new Date(), 1L,
				"This is a fake session for testing purposes.", mockUsers, LocalDateTime.now(), LocalDateTime.now());

		MvcResult result = mockMvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockSessionDTO))
				.header("Authorization", "Bearer " + authToken)).andExpect(status().isOk()).andReturn();

		// Extract session ID from the response
		String content = result.getResponse().getContentAsString();
		Long sessionId = objectMapper.readTree(content).path("id").asLong();

		// Perform deletion using the retrieved ID
		mockMvc.perform(delete("/api/session/{id}", sessionId).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + authToken)).andExpect(status().isOk());

		// Perform retrieval and check that the session is no longer present
		mockMvc.perform(get("/api/session").contentType(MediaType.APPLICATION_JSON).header("Authorization",
				"Bearer " + authToken)).andExpect(status().isOk())
				.andExpect(jsonPath("$[*].id", not(hasItem(sessionId))));
	}

	@Test
	public void deleteSessionTest_NotFound() throws Exception {
		// Setup
		String nonExistentSessionId = "999"; // Choose a session ID that does not exist

		// Perform deletion attempt with a non-existent session ID
		mockMvc.perform(delete("/api/session/{id}", nonExistentSessionId).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + authToken)).andExpect(status().isNotFound());
	}

	@Test
	public void deleteSessionTest_NumberFormatException() throws Exception {
		// Setup
		String invalidSessionId = "invalidId"; // Choose an invalid session ID to trigger NumberFormatException

		// Perform deletion attempt with an invalid session ID
		mockMvc.perform(delete("/api/session/{id}", invalidSessionId).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + authToken)).andExpect(status().isBadRequest());
	}

	@Test
	public void participateTest() throws Exception {
	    // Setup
	    List<Long> mockUsers = new ArrayList<>();
	    SessionDto mockSessionDTO = new SessionDto(null, "Fake Session", new Date(), 1L,
	            "This is a fake session for testing purposes.", mockUsers, LocalDateTime.now(), LocalDateTime.now());

	    MvcResult result = mockMvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(mockSessionDTO))
	            .header("Authorization", "Bearer " + authToken)).andExpect(status().isOk()).andReturn();

	    // Extract session ID from the response
	    String content = result.getResponse().getContentAsString();
	    Long sessionId = objectMapper.readTree(content).path("id").asLong();
	    String stringSessionId = String.valueOf(sessionId);

	    // Perform participation using the retrieved IDs
	    mockMvc.perform(post("/api/session/{id}/participate/{userId}", stringSessionId, stringUserId)
	            .contentType(MediaType.APPLICATION_JSON)
	            .header("Authorization", "Bearer " + authToken)).andExpect(status().isOk());

	    mockMvc.perform(delete("/api/session/{id}/participate/{userId}", stringSessionId, stringUserId)
	            .contentType(MediaType.APPLICATION_JSON)
	            .header("Authorization", "Bearer " + authToken)).andExpect(status().isOk());
	    
	    mockMvc.perform(delete("/api/session/{id}", sessionId).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + authToken)).andExpect(status().isOk());

	}
	@Test
	public void participateTest_badRequest() throws Exception {
	    // Setup
	    List<Long> mockUsers = new ArrayList<>();
	    SessionDto mockSessionDTO = new SessionDto(null, "Fake Session", new Date(), 1L,
	            "This is a fake session for testing purposes.", mockUsers, LocalDateTime.now(), LocalDateTime.now());

	    MvcResult result = mockMvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(mockSessionDTO))
	            .header("Authorization", "Bearer " + authToken)).andExpect(status().isOk()).andReturn();

	    // Extract session ID from the response
	    String content = result.getResponse().getContentAsString();
	    Long sessionId = objectMapper.readTree(content).path("id").asLong();
	    String stringSessionId = String.valueOf(sessionId);

	    // Perform participation using an invalid user ID to trigger NumberFormatException
	    String invalidUserId = "invalidUserId";
	    mockMvc.perform(post("/api/session/{id}/participate/{userId}", stringSessionId, invalidUserId)
	            .contentType(MediaType.APPLICATION_JSON)
	            .header("Authorization", "Bearer " + authToken))
	            .andExpect(status().isBadRequest()); // Expecting badRequest response

	    mockMvc.perform(delete("/api/session/{id}", sessionId).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + authToken)).andExpect(status().isOk());
	  
	}
	
	@Test
	public void unParticipateTest_badRequest() throws Exception {
	    // Setup
	    List<Long> mockUsers = new ArrayList<>();
	    SessionDto mockSessionDTO = new SessionDto(null, "Fake Session", new Date(), 1L,
	            "This is a fake session for testing purposes.", mockUsers, LocalDateTime.now(), LocalDateTime.now());

	    MvcResult result = mockMvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(mockSessionDTO))
	            .header("Authorization", "Bearer " + authToken)).andExpect(status().isOk()).andReturn();

	    // Extract session ID from the response
	    String content = result.getResponse().getContentAsString();
	    Long sessionId = objectMapper.readTree(content).path("id").asLong();
	    String stringSessionId = String.valueOf(sessionId);
	    
	    mockMvc.perform(post("/api/session/{id}/participate/{userId}", stringSessionId, stringUserId)
	            .contentType(MediaType.APPLICATION_JSON)
	            .header("Authorization", "Bearer " + authToken)).andExpect(status().isOk());

	    // Perform participation using an invalid user ID to trigger NumberFormatException
	    String invalidUserId = "invalidUserId";
	    mockMvc.perform(delete("/api/session/{id}/participate/{userId}", stringSessionId, invalidUserId)
	            .contentType(MediaType.APPLICATION_JSON)
	            .header("Authorization", "Bearer " + authToken))
	            .andExpect(status().isBadRequest()); // Expecting badRequest response

	    mockMvc.perform(delete("/api/session/{id}/participate/{userId}", stringSessionId, stringUserId)
	            .contentType(MediaType.APPLICATION_JSON)
	            .header("Authorization", "Bearer " + authToken)).andExpect(status().isOk());
	    
	    mockMvc.perform(delete("/api/session/{id}", sessionId).contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + authToken)).andExpect(status().isOk());
	  
	}


}
