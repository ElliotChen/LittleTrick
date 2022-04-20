package tw.elliot.trick04

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = arrayOf(JacksonAutoConfiguration::class))
class Trick04Application

fun main(args: Array<String>) {
	runApplication<Trick04Application>(*args)
}
