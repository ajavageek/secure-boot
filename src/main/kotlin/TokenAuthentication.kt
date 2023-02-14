package ch.frankel.blog.secureboot

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import java.util.*

@Entity
internal data class Account(
    @Id
    val id: String,
    val password: String,
)

internal interface AccountRepository : JpaRepository<Account, String> {
    fun findByPassword(token: String): Optional<Account>
}

internal class TokenAuthenticationManager(
    private val accountRepo: AccountRepository,
    private val employeeRepo: EmployeeRepository
) : AuthenticationManager {

    override fun authenticate(authentication: Authentication): Authentication {
        val token = authentication.credentials as String? ?: throw BadCredentialsException("No token passed")
        val account = accountRepo.findByPassword(token).orElse(null) ?: throw BadCredentialsException("Invalid token")
        val path = authentication.details as List<String>
        val accountId = account.id
        val segment = path.last()
        if (segment == accountId) return authentication.withPrincipal(accountId)
        val employee = employeeRepo.findById(segment).orElse(null)
        val managerUserName = employee?.manager?.userName
        if (managerUserName != null && managerUserName == accountId) return authentication.withPrincipal(accountId)
        throw InsufficientAuthenticationException("Incorrect token")
    }

    private fun Authentication.withPrincipal(principal: String): Authentication {
        if (this is KeyToken) {
            return KeyTokenWithPrincipal(principal, this)
        }
        return this
    }
}

internal class TokenAuthenticationFilter(authManager: AuthenticationManager) :
    AbstractAuthenticationProcessingFilter("/finance/salary/**", authManager) {

    override fun attemptAuthentication(req: HttpServletRequest, resp: HttpServletResponse): Authentication {
        val header = req.getHeader("Authorization")
        val path = req.servletPath.split('/')
        val token = KeyToken(header, path)
        return authenticationManager.authenticate(token)
    }

    override fun successfulAuthentication(
        req: HttpServletRequest,
        resp: HttpServletResponse,
        chain: FilterChain,
        auth: Authentication
    ) {
        val strategy = SecurityContextHolder.getContextHolderStrategy()
        val context = strategy.createEmptyContext()
        auth.isAuthenticated = true
        context.authentication = auth
        strategy.context = context
        chain.doFilter(req, resp)
    }
}

private class KeyToken(private val credentials: String?, private val path: List<String>) : Authentication {

    private var authenticated: Boolean = false

    override fun getName() = principal
    override fun getAuthorities() = emptyList<GrantedAuthority>()
    override fun getCredentials() = credentials
    override fun getDetails() = path
    override fun getPrincipal() = null
    override fun isAuthenticated() = authenticated

    override fun setAuthenticated(isAuthenticated: Boolean) {
        authenticated = isAuthenticated
    }
}

private class KeyTokenWithPrincipal(private val principal: String, private val token: KeyToken) :
    Authentication by token {
    override fun getPrincipal() = principal
}
