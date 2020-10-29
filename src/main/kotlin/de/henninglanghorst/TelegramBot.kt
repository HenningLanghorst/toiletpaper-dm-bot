package de.henninglanghorst

import de.henninglanghorst.ConfigProperties.port
import de.henninglanghorst.ConfigProperties.token
import de.henninglanghorst.ConfigProperties.webhookUrl
import de.henninglanghorst.dm.DmApi
import de.henninglanghorst.telegram.TelegramApi
import de.henninglanghorst.telegram.Update
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory

val log = LoggerFactory.getLogger(Exception().stackTrace.first().className)

fun main() {
    val telegramApi = TelegramApi.instance
    val dmApi1 = DmApi.instance
    val telegramMessageHandler = TelegramMessageHandler(telegramApi, dmApi1)

    telegramApi.configureWebhook()

    embeddedServer(Netty, port, host = "127.0.0.1") {
        install(ContentNegotiation) { jackson() }
        routing {
            route("/telegram") {
                get("/ping") {
                    log.info("Ping")
                    call.respondText("Pong", ContentType.Text.Plain)
                }
                post("/$token") {
                    val update = call.receive<Update>().also { log.debug("Receiving update {}", it) }
                    telegramMessageHandler.handleUpdate(update)
                    call.respondText("", status = HttpStatusCode.Accepted)
                }
            }
        }
    }.start()

}

private fun TelegramApi.configureWebhook() {

    setWebhook(webhookUrl).execute()
        .also { log.info("Create webhook result: {}", it.body() ?: it.errorBody()) }

    Runtime.getRuntime().addShutdownHook(
        Thread {
            deleteWebhook().execute()
                .also { log.info("Delete webhook result: {}", it.body() ?: it.errorBody()) }
        })
}

