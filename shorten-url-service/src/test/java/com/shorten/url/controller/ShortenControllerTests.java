package com.shorten.url.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shorten.url.AbstractContainerBaseTest;
import com.shorten.url.dto.ShortenRequest;
import com.shorten.url.entity.Shorten;
import com.shorten.url.repository.ShortenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ShortenControllerTests extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShortenRepository shortenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        shortenRepository.deleteAll();
    }

    @Test
    @DisplayName("integration test - create shorten url")
    public void givenLongURL_whenCreateShortenURL_thenReturnShortenURL() throws Exception {

        // given - precondition or setup
        ShortenRequest request = ShortenRequest.builder()
                .longUrl("https://medium.com").build();

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/api/v1/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then - verify the result or output using assert statements
        response.andDo(print()).
                andExpect(status().isCreated());

    }

    @Test
    @DisplayName("integration test - get long url")
    public void givenShortenURL_whenGetLongURL_thenReturnLongURL() throws Exception {

        // given - precondition or setup
        String shortenURL = "9464b52";

        Shorten willSaveShorten = Shorten.builder()
                .shortenUrl(shortenURL)
                .longUrl("https://medium.com").build();

        shortenRepository.save(willSaveShorten);

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/api/v1/shorten")
                .param("shortUrl", shortenURL)
                .contentType(MediaType.APPLICATION_JSON));

        // then - verify the result or output using assert statements
        response.andDo(print()).
                andExpect(status().isMovedPermanently());

    }

}
