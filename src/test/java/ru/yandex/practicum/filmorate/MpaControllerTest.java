package ru.yandex.practicum.filmorate;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MpaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllMpa() throws Exception {
        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].mpaId").value(1))
                .andExpect(jsonPath("$[0].mpaName").value("G"))
                .andExpect(jsonPath("$[1].mpaId").value(2))
                .andExpect(jsonPath("$[1].mpaName").value("PG"))
                .andExpect(jsonPath("$[2].mpaId").value(3))
                .andExpect(jsonPath("$[2].mpaName").value("PG-13"))
                .andExpect(jsonPath("$[3].mpaId").value(4))
                .andExpect(jsonPath("$[3].mpaName").value("R"))
                .andExpect(jsonPath("$[4].mpaId").value(5))
                .andExpect(jsonPath("$[4].mpaName").value("NC-17"));
    }

    @Test
    void shouldGetMpaById() throws Exception {
        mockMvc.perform(get("/mpa/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mpaId").value(1))
                .andExpect(jsonPath("$.mpaName").value("G"));
    }

    @Test
    void shouldReturn404WhenMpaNotFound() throws Exception {
        mockMvc.perform(get("/mpa/999"))
                .andExpect(status().isNotFound());
    }
}
