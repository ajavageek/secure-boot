package ch.frankel.blog.secureboot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AppProperties::class)
class SecureBootApplication

fun main(args: Array<String>) {
    runApplication<SecureBootApplication>(*args) {
        addInitializers(routes())
    }
}
