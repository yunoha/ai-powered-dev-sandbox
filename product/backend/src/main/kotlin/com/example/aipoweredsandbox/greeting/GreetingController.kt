package com.example.aipoweredsandbox.greeting

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class GreetingController {
    @GetMapping("/greeting")
    fun getGreeting(): GreetingResponse = GreetingResponse(message = "Hello world!")
}
