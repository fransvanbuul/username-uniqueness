package com.example.usernameuniqueness

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.TargetAggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

/* Toy command model of a user aggregate, closely resembling the one mentioned in
   http://codebetter.com/gregyoung/2010/08/12/eventual-consistency-and-set-validation/ */

/* Events */
data class UserRegistered(val id: UUID, val email: String, val hashedPassword: String)
data class UserEmailAddressUpdated(val id: UUID, val oldEmail: String, val newEmail: String)

/* Commands */
data class RegisterUser(@TargetAggregateIdentifier val id: UUID, val email: String, val hashedPassword: String)
data class UpdateEmailAddressForUser(@TargetAggregateIdentifier val id: UUID, val email: String)

/* Aggregate */
@Aggregate
class User {
    @AggregateIdentifier
    private lateinit var id: UUID
    private lateinit var email: String

    @CommandHandler
    constructor(cmd: RegisterUser) {
        apply(UserRegistered(cmd.id, cmd.email, cmd.hashedPassword))
    }

    @CommandHandler
    fun handle(cmd: UpdateEmailAddressForUser) {
        if(email != cmd.email)
            apply(UserEmailAddressUpdated(cmd.id, email, cmd.email))
    }

    @EventSourcingHandler
    fun on(evt: UserRegistered) {
        id = evt.id
        email = evt.email
    }

    @EventSourcingHandler
    fun on(evt: UserEmailAddressUpdated) {
        email = evt.newEmail
    }
}