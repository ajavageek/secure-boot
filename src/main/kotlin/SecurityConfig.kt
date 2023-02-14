package ch.frankel.blog.secureboot

import org.springframework.context.support.beans
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


internal fun security() = beans {
    bean { TokenAuthenticationManager(ref(), ref()) }
    bean {
        val http = ref<HttpSecurity>()
        http {
            authorizeRequests {
                authorize("/finance/salary/**", authenticated)
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(TokenAuthenticationFilter(ref()))
            httpBasic { disable() }
            csrf { disable() }
            logout { disable() }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
        }
        http.build()
    }
}
