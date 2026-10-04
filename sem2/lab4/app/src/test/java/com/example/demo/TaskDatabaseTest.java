package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskDatabaseTest {

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5432/todo_db_test");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void userTaskIsStoredWithOwnerAndProtectedByRole() throws Exception {
        taskRepository.deleteAll();

        mockMvc.perform(post("/addTask")
                        .param("description", "Купить корм")
                        .with(user("user").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/").with(user("user").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Купить корм")))
                .andExpect(content().string(containsString("Автор: user")));

        Long id = taskRepository.findAll().get(0).getId();

        mockMvc.perform(post("/updateTask/" + id)
                        .param("completed", "true")
                        .with(user("user").roles("USER"))
                        .with(csrf()))
                .andExpect(redirectedUrl("/access-denied"));

        mockMvc.perform(post("/updateTask/" + id)
                        .param("completed", "true")
                        .with(user("editor").roles("EDITOR"))
                        .with(csrf()))
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/deleteTask/" + id)
                        .with(user("editor").roles("EDITOR")))
                .andExpect(redirectedUrl("/access-denied"));

        mockMvc.perform(get("/deleteTask/" + id)
                        .with(user("moderator").roles("MODERATOR")))
                .andExpect(redirectedUrl("/"));
    }
}
