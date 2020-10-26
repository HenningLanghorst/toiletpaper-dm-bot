package de.henninglanghorst.dm

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class StoreAvailablityResponse(
    val storeAvailabilities: Map<String, List<ArticleAvailablity>>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Store(val storeNumber: String?)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class CheckStatus(
    val status: String?,
    val code: String?

)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ArticleAvailablity(
    val store: Store,
    val inStock: Boolean,
    val stockLevel: Int,
    val checkStatus: CheckStatus
)


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class StoreResponse(
    val address: Address
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Address(
    val name: String?,
    val street: String?,
    val streetAdditional: String?,
    val zip: String?,
    val city: String?
) {
    val displayText: String get() = "$name\n$street\n${streetAdditional?.let { "$it\n" }?:""}$zip $city"
}

