package com.example.SecurityKidsBackend

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableAutoConfiguration
class SecurityKidsBackendApplication

fun main(args: Array<String>) {
	runApplication<SecurityKidsBackendApplication>(*args)
}
