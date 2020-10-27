package de.henninglanghorst.place

import org.slf4j.LoggerFactory
import java.io.FileReader

object PlaceFinder {
    private val log = LoggerFactory.getLogger(PlaceFinder::class.java)

    private const val INDEX_ZIP = 1
    private const val INDEX_PLACE_NAME = 2
    private const val INDEX_LATITUDE = 9
    private const val INDEX_LONGITUDE = 10

    private val PLACES: List<Place> by lazy {
        FileReader(System.getProperty("places"))
            .useLines { lines ->
                lines.map { it.split('\t').toList() }
                    .map {
                        Place(
                            zip = it[INDEX_ZIP],
                            placeName = it[INDEX_PLACE_NAME],
                            latitude = it[INDEX_LATITUDE].toDouble(),
                            longitude = it[INDEX_LONGITUDE].toDouble()
                        )
                    }.toList()
            }.also { log.info("Number of places: {} ", it.size) }
    }

    fun searchLocation(searchString: String) =
        PLACES.firstOrNull { it.zip == searchString || it.placeName == searchString }
            .also { log.info("Resolved location: {}", it) }

}

data class Place(
    val zip: String,
    val placeName: String,
    val latitude: Double,
    val longitude: Double
)