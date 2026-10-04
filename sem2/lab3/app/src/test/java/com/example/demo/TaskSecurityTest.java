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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void anonymousUserIsSentToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void passwordLoginOpensTheTaskList() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "user")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void userAddsTasksButCannotChangeOrDeleteThem() throws Exception {
        String id = addTask("user", "USER", "Задача пользователя");

        mockMvc.perform(post("/updateTask/" + id)
                        .param("completed", "true")
                        .with(user("user").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));

        mockMvc.perform(get("/deleteTask/" + id)
                        .with(user("user").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));

        mockMvc.perform(get("/").with(user("user").roles("USER")))
                .andExpect(content().string(containsString("Задача пользователя")));
    }

    @Test
    void editorChangesStatusButCannotDelete() throws Exception {
        String id = addTask("editor", "EDITOR", "Задача редактора");

        mockMvc.perform(post("/updateTask/" + id)
                        .param("completed", "true")
                        .with(user("editor").roles("EDITOR"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/").with(user("editor").roles("EDITOR")))
                .andExpect(content().string(containsString("task-done")));

        mockMvc.perform(get("/deleteTask/" + id)
                        .with(user("editor").roles("EDITOR")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    void moderatorChangesStatusAndDeletes() throws Exception {
        String id = addTask("moderator", "MODERATOR", "Задача модератора");

        mockMvc.perform(post("/updateTask/" + id)
                        .param("completed", "true")
                        .with(user("moderator").roles("MODERATOR"))
                        .with(csrf()))
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/deleteTask/" + id)
                        .with(user("moderator").roles("MODERATOR")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/").with(user("moderator").roles("MODERATOR")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("Задача модератора"))));
    }

    private String addTask(String username, String role, String title) throws Exception {
        mockMvc.perform(post("/addTask")
                        .param("title", title)
                        .param("description", "Проверка роли")
                        .with(user(username).roles(role))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        MvcResult page = mockMvc.perform(get("/").with(user(username).roles(role)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(title)))
                .andReturn();

        String html = page.getResponse().getContentAsString();
        int titleAt = html.indexOf(title);
        Matcher matcher = Pattern.compile("/updateTask/(\\d+)").matcher(html);
        matcher.region(titleAt, html.length());
        assertTrue(matcher.find());
        return matcher.group(1);
    }
}
