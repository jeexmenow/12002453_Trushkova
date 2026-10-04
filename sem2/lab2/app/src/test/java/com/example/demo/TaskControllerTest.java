package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void addsUpdatesAndDeletesTask() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Пока пусто")));

        mockMvc.perform(post("/addTask")
                        .param("title", "Подготовить отчёт")
                        .param("description", "Лабораторная работа 2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        MvcResult page = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Подготовить отчёт")))
                .andExpect(content().string(containsString("В работе")))
                .andReturn();

        Matcher matcher = Pattern.compile("/updateTask/(\\d+)").matcher(page.getResponse().getContentAsString());
        assertTrue(matcher.find());
        String id = matcher.group(1);

        mockMvc.perform(post("/updateTask/" + id).param("completed", "true"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("Готово")))
                .andExpect(content().string(containsString("task-done")));

        mockMvc.perform(get("/deleteTask/" + id))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("Пока пусто")));
    }
}
