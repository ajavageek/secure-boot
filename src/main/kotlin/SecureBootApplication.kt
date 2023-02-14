package ch.frankel.blog.secureboot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication
@EnableWebSecurity
class SecureBootApplication

fun main(args: Array<String>) {
    runApplication<SecureBootApplication>(*args) {
        addInitializers(routes(), security())
    }
}
