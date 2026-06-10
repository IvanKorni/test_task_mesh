package com.mesh.test_task.api.rest;

import com.mesh.test_task.PostgresTestContainer;
import com.mesh.test_task.api.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransferApiTest extends PostgresTestContainer {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Sql(statements = "update account set balance = start_balance")
    void transfersMoneyWithMockMvcAndPostgresContainer() throws Exception {
        String token = jwtService.generateToken(1L);

        mockMvc.perform(post("/v1/transfers")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"toUserId\":2,\"amount\":100.50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromUserId", is(1)))
                .andExpect(jsonPath("$.toUserId", is(2)))
                .andExpect(jsonPath("$.amount", is(100.5)))
                .andExpect(jsonPath("$.fromBalance", is(899.5)))
                .andExpect(jsonPath("$.toBalance", is(2600.5)));

        assertEquals(new BigDecimal("899.50"), balance(1L));
        assertEquals(new BigDecimal("2600.50"), balance(2L));
    }

    @Test
    void rejectsTransferToSelfWithBadRequest() throws Exception {
        String token = jwtService.generateToken(1L);

        mockMvc.perform(post("/v1/transfers")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"toUserId\":1,\"amount\":10.00}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Cannot transfer money to yourself")));
    }

    private BigDecimal balance(Long userId) {
        return jdbcTemplate.queryForObject(
                "select balance from account where user_id = ?",
                BigDecimal.class,
                userId
        );
    }
}
