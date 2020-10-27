package de.henninglanghorst

import de.henninglanghorst.dm.ArticleAvailablity
import de.henninglanghorst.dm.DmApi
import de.henninglanghorst.dm.Store
import de.henninglanghorst.place.PlaceFinder
import de.henninglanghorst.telegram.SendMessageRequest
import de.henninglanghorst.telegram.TelegramApi
import de.henninglanghorst.telegram.Update
import org.slf4j.LoggerFactory
import java.time.Duration

val log = LoggerFactory.getLogger(Exception().stackTrace.first().className)

val dmRegex = Regex("/dm\\s+(\\d+).*$", RegexOption.IGNORE_CASE)

fun main() {
    repeatEvery(30.seconds) {
        val updates = TelegramApi.instance.getUpdates().execute().body()?.result.orEmpty()
        handleUpdates(updates)
        val maxOffset = updates.asSequence().map { t -> t.updateId }.maxOrNull()
        if (maxOffset != null) {
            TelegramApi.instance.getUpdates(maxOffset + 1, 0).execute()
        }
    }
}

fun repeatEvery(duration: Duration, action: () -> Unit) {
    while (true) {
        action()
        Thread.sleep(duration.toMillis())
    }

}

val Int.seconds: Duration get() = Duration.ofSeconds(this.toLong())

private fun handleUpdates(updates: List<Update>) {
    updates.asSequence()
        .mapNotNull { it.message ?: it.editedMessage ?: it.channelPost ?: it.editedChannelPost }
        .forEach { message ->
            getAnswersFor(message.text).forEach { sendMessage(message.chat.id, it) }
        }
}

private fun sendMessage(chatId: Int, text: String?) {
    if (text != null) {
        TelegramApi.instance.sendMessage(SendMessageRequest(chatId.toString(), text)).execute()
    }
}

private fun getAnswersFor(text: String?): List<String> =
    try {
        when {
            text == null -> emptyList()
            text == "/start" -> listOf("Gib \"/dm <Ort/PLZ>\" ein zum Abfragen der Toilettenpapiervorkommen.")
            text.toLowerCase().startsWith("/dm ") -> getAnswersForSearchString(searchString = text.substring(3).trim())
            else -> emptyList()
        }
    } catch (e: Exception) {
        emptyList()
    }

private fun getAnswersForSearchString(searchString: String): List<String> {
    val stores = findStores(searchString).also { log.info("Found: {}", it) }
    val storeAvailablity = getStoreAvailablity(stores.mapNotNull { it.storeNumber })
    return stores.zip(storeAvailablity)
        .map { (store, storeAvailablity) ->
            sequenceOf(
                store.address?.displayText ?: "",
                "\uD83E\uDDFB: $storeAvailablity"
            ).joinToString("\n\n")
        }
}

private fun findStores(searchString: String): List<Store> =
    PlaceFinder.searchLocation(searchString)
        ?.let { (_, _, latitude, longitude) -> lookupStores(latitude, longitude) }.orEmpty()

private fun lookupStores(latitude: Double, longitude: Double): List<Store> =
    DmApi.instance.lookupStores("${latitude + 0.08},${longitude - 0.05},${latitude - 0.05},${longitude + 0.08}")
        .execute()
        .body()
        ?.stores.orEmpty()


private fun getStoreAvailablity(storeNumbers: List<String>): List<Int> =
    if (storeNumbers.isEmpty())
        emptyList()
    else
        DmApi.instance.storeAvailabilty(articleNumbers().joinToString(","), storeNumbers.joinToString(","))
            .execute()
            .body()
            ?.storeAvailabilities
            .let { storeAvailabilities ->
                storeNumbers.map { storeNumber ->
                    sumForStoreNumber(
                        storeAvailabilities,
                        storeNumber
                    )
                }
            }

private fun sumForStoreNumber(storeAvailabilities: Map<String, List<ArticleAvailablity>>?, storeNumber: String): Int =
    storeAvailabilities
        ?.mapValues { it.value.filter { aa -> aa.store.storeNumber == storeNumber } }
        ?.entries
        .orEmpty()
        .asSequence()
        .flatMap { it.value.asSequence() }
        .map { it.stockLevel }.sum()

private fun articleNumbers(): List<String> {
    return listOf(
        "595420",
        "708997",
        "137425",
        "28171",
        "485698",
        "799358",
        "863567",
        "452740",
        "610544",
        "846857",
        "709006",
        "452753",
        "879536",
        "452744",
        "485695",
        "853483",
        "594080",
        "504606",
        "593761",
        "525943",
        "842480",
        "535981",
        "127048",
        "524535"
    )
}


