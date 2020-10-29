package de.henninglanghorst

object ConfigProperties {
    private val hostname: String by lazy {
        System.getProperty("hostname") ?: throw NoSuchElementException("Property 'hostname' must be set.")
    }

    val port: Int by lazy { System.getProperty("httpPort")?.toIntOrNull() ?: 8088 }
    val token: String by lazy {
        System.getProperty("token") ?: throw NoSuchElementException("Property 'token' must be set.")
    }

    val webhookUrl: String by lazy { "https://$hostname/telegram/$token" }

}