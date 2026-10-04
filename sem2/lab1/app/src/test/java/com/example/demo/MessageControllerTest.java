package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publishesCountsFiltersUpdatesAndDeletesMessages() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, World!"));

        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"first note\""))
                .andExpect(status().isOk())
                .andExpect(content().string("Message published successfully!"));

        mockMvc.perform(get("/messages/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        mockMvc.perform(get("/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("first note"));

        String past = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String future = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        mockMvc.perform(get("/messages/after").param("from", past))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].text").value("first note"));

        mockMvc.perform(get("/messages/after").param("from", future))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(put("/messages/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"updated note\""))
                .andExpect(status().isOk())
                .andExpect(content().string("Message updated successfully!"));

        mockMvc.perform(get("/messages"))
                .andExpect(jsonPath("$[0].text").value("updated note"));

        mockMvc.perform(delete("/messages/0"))
                .andExpect(status().isOk())
                .andExpect(content().string("Message deleted successfully!"));

        mockMvc.perform(get("/messages/count"))
                .andExpect(content().string("0"));

        mockMvc.perform(delete("/messages/3"))
                .andExpect(content().string("Message not found at index 3"));
    }
}
