package de.henninglanghorst.dm

import com.fasterxml.jackson.annotation.JsonIgnoreProperties


@JsonIgnoreProperties(ignoreUnknown = true)
data class StoreAvailablityResponse(
        val storeAvailabilities: Map<String, List<ArticleAvailablity>>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Store(
        val storeNumber: String?,
        val address: Address?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CheckStatus(
        val status: String?,
        val code: String?

)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ArticleAvailablity(
        val store: Store,
        val inStock: Boolean,
        val stockLevel: Int,
        val checkStatus: CheckStatus
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class StoreResponse(
        val address: Address
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Address(
        val name: String?,
        val street: String?,
        val streetAdditional: String?,
        val zip: String?,
        val city: String?
) {
    val displayText: String get() = "$name\n$street\n${streetAdditional?.let { "$it\n" } ?: ""}$zip $city"
}


@JsonIgnoreProperties(ignoreUnknown = true)
data class StoreLookupResponse(
        val totalElements: Int,
        val totalPages: Int,
        val size: Int,
        val page: Int,
        val stores: List<Store>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Product(
        val gtin: Long,
        val dan: Int,
        val name: String,
        val brandName: String,
        val subBrandName: String?,
        val title: String,
        val isoCountry: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Pagination(
        val sort: String,
        val pageSize: Int,
        val currentPage: Int,
        val totalResults: Int,
        val totalPages: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProductSearchResponse(
        val pagination: Pagination,
        val freeTextSearch: String,
        val products: List<Product>
)