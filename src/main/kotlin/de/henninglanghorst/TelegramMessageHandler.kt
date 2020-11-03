package de.henninglanghorst

import de.henninglanghorst.dm.DmApi
import de.henninglanghorst.dm.Product
import de.henninglanghorst.dm.Store
import de.henninglanghorst.place.PlaceFinder
import de.henninglanghorst.telegram.SendMessageRequest
import de.henninglanghorst.telegram.TelegramApi
import de.henninglanghorst.telegram.Update

class TelegramMessageHandler(private val telegramApi: TelegramApi, private val dmApi: DmApi) {

    private val articles: Map<String, String> by lazy {
        productSearch(":relevance:allCategories:060201")
                .filter { it.name.contains("Toilettenpapier", ignoreCase = true) }
                .filter { !it.name.contains("feucht", ignoreCase = true) }
                .associate { it.dan.toString() to it.name }
    }

    private fun productSearch(productQuery: String): Sequence<Product> =
            productSearch(productQuery, 0)
                    .let { firstPage ->
                        (sequenceOf(firstPage) + (2..(firstPage?.pagination?.totalPages ?: 0))
                                .asSequence().map { productSearch(productQuery, it) })
                                .mapNotNull { it?.products }.flatMap { it.asSequence() }
                    }

    private fun productSearch(productQuery: String, currentPage: Int) =
            dmApi.search(
                    productQuery = productQuery,
                    purchasableOnly = false,
                    pageSize = 30,
                    currentPage = currentPage
            ).execute().body()


    fun handleUpdate(update: Update) {
        val message = update.message ?: update.editedMessage ?: update.channelPost ?: update.editedChannelPost
        if (message != null)
            getAnswersForMessage(message.text).forEach { sendMessage(message.chat.id, it) }
    }


    private fun sendMessage(chatId: Int, text: String?) {
        if (text != null) {
            telegramApi.sendMessage(SendMessageRequest(chatId.toString(), text)).execute()
        }
    }

    private fun getAnswersForMessage(text: String?): List<String> =
            try {
                when {
                    text == null -> emptyList()
                    text == "/start" -> listOf("Gib \"/dm <Ort/PLZ>\" ein zum Abfragen der Toilettenpapiervorkommen.")
                    text.toLowerCase().startsWith("/dm ") -> getAnswersForSearchString(
                            searchString = text.substring(3).trim()
                    )
                    else -> emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }

    private fun getAnswersForSearchString(searchString: String): List<String> =
            findStores(searchString)
                    .also { log.debug("Found stores: {}", it) }
                    .let { stores ->
                        if (stores.isEmpty()) {
                            listOf("Ich habe keinen dm-Markt in der Nähe gefunden.")
                        } else {
                            getAnswersPerStore(stores)
                        }
                    }

    private fun findStores(searchString: String): List<Store> =
            PlaceFinder.searchLocation(searchString)
                    ?.let { (_, _, latitude, longitude) -> lookupStores(latitude, longitude) }.orEmpty()


    private fun lookupStores(latitude: Double, longitude: Double): List<Store> =
            dmApi.lookupStores("${latitude + 0.08},${longitude - 0.05},${latitude - 0.05},${longitude + 0.08}")
                    .execute()
                    .body()
                    ?.stores.orEmpty()

    private fun getAnswersPerStore(stores: List<Store>): List<String> =
            getArticlesInStores(stores.mapNotNull { it.storeNumber })
                    .let { articlesInStores ->
                        stores.map { buildAnswerForSingleStore(it, articlesInStores[it.storeNumber].orEmpty()) }
                    }

    private fun buildAnswerForSingleStore(store: Store, articlesInStore: List<ArticleInStore>): String {
        val address = store.address?.displayText ?: ""
        val sum = articlesInStore.sumBy { it.stockLevel }
        val articleAmountStrings = articlesInStore
                .asSequence()
                .filter { it.stockLevel > 0 }
                .map { "${it.stockLevel}x ${it.name}" }
        return (sequenceOf("$address\n", "\uD83E\uDDFB: $sum") + articleAmountStrings).joinToString("\n")
    }

    private fun getArticlesInStores(storeIds: List<String>): Map<String, List<ArticleInStore>> =
            dmApi.storeAvailabilty(articles.keys.joinToString(","), storeIds.joinToString(","))
                    .execute()
                    .body()
                    ?.storeAvailabilities
                    .orEmpty()
                    .asSequence()
                    .flatMap { (dan, articleAvailablities) -> articleAvailablities.asSequence().map { dan to it } }
                    .mapNotNull { (dan, articleAvailablity) ->
                        ArticleInStore(
                                dan = dan,
                                name = articles[dan] ?: "",
                                storeNumber = articleAvailablity.store.storeNumber ?: "",
                                stockLevel = articleAvailablity.stockLevel
                        )
                    }.groupBy { it.storeNumber }


    private data class ArticleInStore(
            val dan: String,
            val name: String,
            val storeNumber: String,
            val stockLevel: Int
    )


}