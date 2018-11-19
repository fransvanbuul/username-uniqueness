package com.example.usernameuniqueness

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import springfox.documentation.swagger2.annotations.EnableSwagger2
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import com.thoughtworks.xstream.XStream
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.xml.XStreamSerializer
import org.springframework.beans.factory.annotation.Autowired

@SpringBootApplication
@EnableSwagger2
class UsernameUniquenessApplication {

    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example"))
                .paths(PathSelectors.any())
                .build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Autowired
    fun config(serializer: Serializer) {
        if (serializer is XStreamSerializer) {
            XStream.setupDefaultSecurity(serializer.xStream)
            serializer.xStream.allowTypesByWildcard(arrayOf(
                    "com.example.usernameuniqueness.**",
                    "org.axonframework.**"))
        }
    }

}

fun main(args: Array<String>) {
    runApplication<UsernameUniquenessApplication>(*args)
}
