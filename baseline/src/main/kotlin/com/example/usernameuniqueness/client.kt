package com.example.usernameuniqueness

import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RestController
import java.lang.IllegalArgumentException
import java.util.*

data class RegisterUserRequest(val email: String, val password: String)
data class RegisterUserResponse(val id: UUID)

@RestController
@RequestMapping("/user")
class UserController(val commandGateway: CommandGateway,
                     val queryGateway: QueryGateway,
                     val passwordEncoder: PasswordEncoder) {

    @RequestMapping(method = [POST])
    fun register(@RequestBody request: RegisterUserRequest): RegisterUserResponse {
        verifyEmailAvailable(request.email)
        val id = UUID.randomUUID()
        val passwordHash = passwordEncoder.encode(request.password)
        commandGateway.sendAndWait<Any>(RegisterUser(id, request.email, passwordHash))
        return RegisterUserResponse(id)
    }

    private fun verifyEmailAvailable(emailAddress: String) {
        val registered = queryGateway.query(
                EmailRegisteredQuery(emailAddress),
                ResponseTypes.instanceOf(Boolean::class.java))
                .join()
        if(registered) {
            throw IllegalArgumentException("Email address already in use")
        }
    }

}