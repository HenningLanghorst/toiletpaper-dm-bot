package de.henninglanghorst

import de.henninglanghorst.dm.DmApi
import de.henninglanghorst.telegram.SendMessageRequest
import de.henninglanghorst.telegram.TelegramApi
import de.henninglanghorst.telegram.Update
import java.time.Duration

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
        .forEach { message -> sendMessage(message.chat.id, getAnswerFor(message.text)) }
}

private fun sendMessage(chatId: Int, text: String) {
    TelegramApi.instance.sendMessage(SendMessageRequest(chatId.toString(), text)).execute()
}

private fun getAnswerFor(text: String?): String =
    if (text?.matches(dmRegex) == true) {
        val storeNumber = dmRegex.find(text)?.groupValues?.get(1)!!
        sequenceOf(
            DmApi.instance.getStore(storeNumber).execute().body()?.address?.displayText ?: "",
            "\uD83E\uDDFB: ${storeAvailablity(storeNumber)}"
        ).joinToString("\n\n")
    } else {
        "Ich verstehe dich nicht."
    }

private fun storeAvailablity(storeNumber: String): Int =
    DmApi.instance.storeAvailabilty(articleNumbers().joinToString(","), storeNumber)
        .execute()
        .body()
        ?.storeAvailabilities
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


