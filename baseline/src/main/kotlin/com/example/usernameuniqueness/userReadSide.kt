package com.example.usernameuniqueness

import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import javax.persistence.*

data class EmailRegisteredQuery(val emailAddress: String)

@Entity
data class RegisteredEmailAddress(@Id var emailAddress: String)

@Component
class Projector(val em: EntityManager) {

    @EventHandler
    fun on(evt: UserRegistered) {
        em.persist(RegisteredEmailAddress(evt.email))
    }

    @EventHandler
    fun on(evt: UserEmailAddressUpdated) {
        em.remove(em.find(RegisteredEmailAddress::class.java, evt.oldEmail))
        em.persist(RegisteredEmailAddress(evt.newEmail))
    }

    @QueryHandler
    fun handle(qry: EmailRegisteredQuery) : Boolean {
        return em.find(RegisteredEmailAddress::class.java, qry.emailAddress) != null
    }
}
