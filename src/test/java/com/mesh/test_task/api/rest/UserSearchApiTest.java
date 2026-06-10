package com.mesh.test_task.api.rest;

import com.mesh.test_task.PostgresTestContainer;
import com.mesh.test_task.api.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserSearchApiTest extends PostgresTestContainer {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    void searchesUsersWithDateOfBirthFilter() throws Exception {
        String token = jwtService.generateToken(1L);

        mockMvc.perform(get("/v1/users/search")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .param("name", "Ivan")
                        .param("phone", "79207865432")
                        .param("dateOfBirth", "1990-01-01")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id", is(1)))
                .andExpect(jsonPath("$.items[0].name", is("Ivan Petrov")))
                .andExpect(jsonPath("$.items[0].dateOfBirth", is("1993-05-01")))
                .andExpect(jsonPath("$.items[0].phones", hasItem("79207865432")))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(20)))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }
}
