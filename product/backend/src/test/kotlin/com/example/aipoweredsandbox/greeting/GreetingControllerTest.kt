package com.example.aipoweredsandbox.greeting

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class GreetingControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `returns the expected greeting payload`() {
        mockMvc.get("/api/greeting")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.message") { value("Hello world!") }
            }
    }
}
