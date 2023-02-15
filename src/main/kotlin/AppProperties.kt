package ch.frankel.blog.secureboot

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(val opaEndpoint: String)