package ch.frankel.blog.secureboot

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse

internal fun validateOpa(
    opaWebClient: WebClient,
    req: ServerRequest,
    next: (ServerRequest) -> ServerResponse
): ServerResponse {
    val httpReq = req.servletRequest()
    val account = httpReq.getHeader("X-Account")
    val path = httpReq.servletPath.split('/').filter { it.isNotBlank() }
    val decision = opaWebClient.post()
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(OpaInput(DataInput(account, path)))
        .exchangeToMono { it.bodyToMono(DecisionOutput::class.java) }
        .block() ?: DecisionOutput(ResultOutput(false))
    return if (decision.result.allow) next(req)
    else ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
}

private data class OpaInput(
    val input: DataInput
)

private data class DataInput(
    val user: String,
    val path: List<String>,
)

private data class DecisionOutput(
    val result: ResultOutput
)

private data class ResultOutput(
    val allow: Boolean,
)
