package com.digeltech.appdiscountone.data.source.remote

import android.database.SQLException
import com.digeltech.appdiscountone.data.constants.RemoteConstants.DATABASE_URL
import com.digeltech.appdiscountone.data.util.tryCatch
import com.digeltech.appdiscountone.domain.model.Category
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.model.Shop
import com.digeltech.appdiscountone.util.log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import javax.inject.Inject

class DatabaseConnection @Inject constructor() {
    private var connection: Connection? = null

    // Функция для установления соединения с базой данных
    fun connect() {
        try {
            val user = "pv7cyzxu6n54"
            val password = "CdM9uwNOZu"
            connection = DriverManager.getConnection(DATABASE_URL, user, password)
            log("Connection to MySQL database successful!")
        } catch (e: SQLException) {
            log("Connection to MySQL database failed! Error: ${e.message}")
        } catch (e: ClassNotFoundException) {
            log("MySQL JDBC Driver not found! Error: ${e.message}")
        }
    }

    // Функция для выполнения запросов к базе данных
    fun executeQuery(query: String): ResultSet? {
        try {
            val statement = connection?.createStatement()
            return statement?.executeQuery(query)
        } catch (e: SQLException) {
            log("Error executing query! Error: ${e.message}")
        }
        return null
    }

    // Функция для закрытия соединения с базой данных
    fun close() {
        try {
            connection?.close()
            log("Connection to MySQL database closed!")
        } catch (e: SQLException) {
            log("Error closing connection! Error: ${e.message}")
        }
    }

    fun getAllCategories(): List<Category> {
        connect()

        val listOfCategories = mutableListOf<Category>()

        tryCatch {
            val query = "SELECT wp_terms.term_id, wp_terms.name, wp_term_taxonomy.count, wp_termmeta.*\n" +
                    "FROM wp_terms\n" +
                    "JOIN wp_term_taxonomy ON wp_terms.term_id = wp_term_taxonomy.term_id\n" +
                    "JOIN wp_termmeta ON wp_termmeta.term_id = wp_term_taxonomy.term_id\n" +
                    "WHERE wp_term_taxonomy.taxonomy = \"categories\"\n" +
                    "AND wp_termmeta.meta_key = \"popular\"\n" +
                    "AND wp_termmeta.meta_value = 1\n" +
                    "ORDER BY wp_terms.name ASC"

            val resultSet = executeQuery(query)
            while (resultSet?.next() == true) {
                val id = resultSet.getInt("term_id")
                val name = resultSet.getString("name")
                val count = resultSet.getInt("count")

                listOfCategories.add(
                    Category(
                        id = id, name = name, countOfItems = count, icon = ""
                    )
                )
            }
        }

        listOfCategories.forEach {
            it.icon = getImageUrl(it.id)
            log("${it.id} ${it.name} ${it.icon}")
        }

        return listOfCategories
    }

    fun getAllShops(): List<Shop> {
        connect()

        val listOfShops = mutableListOf<Shop>()

        tryCatch {
            val query = "SELECT wp_terms.term_id, wp_terms.name, wp_term_taxonomy.count, wp_termmeta.*\n" +
                    "FROM wp_terms\n" +
                    "JOIN wp_term_taxonomy ON wp_terms.term_id = wp_term_taxonomy.term_id\n" +
                    "JOIN wp_termmeta ON wp_termmeta.term_id = wp_term_taxonomy.term_id\n" +
                    "WHERE wp_term_taxonomy.taxonomy = \"categories-shops\"\n" +
                    "AND wp_termmeta.meta_key = \"popular\"\n" +
                    "AND wp_termmeta.meta_value = 1\n" +
                    "ORDER BY wp_terms.name ASC"

            val resultSet = executeQuery(query)
            while (resultSet?.next() == true) {
                val id = resultSet.getInt("term_id")
                val name = resultSet.getString("name")
                val count = resultSet.getInt("count")

                listOfShops.add(
                    Shop(
                        id = id, name = name, countOfItems = count, icon = ""
                    )
                )
            }
        }

        listOfShops.forEach {
            it.icon = getImageUrl(it.id)
            log("${it.id} ${it.name} ${it.icon}")
        }

        return listOfShops
    }

    fun getCategoryDeals(categoryId: Int): List<Deal> {
        val listOfDeals = mutableListOf<Deal>()

        getCategoryDealsId(categoryId).forEach { dealId ->
            var title = ""
            var description = ""
            var imageUrl = ""
            var postDate = ""
            var oldPrice = 0
            var price = 0
            var company = ""
            var rating = 0
            var imageId = 0
//                    var promocode = resultSet.getString("promocode")
//                    val expirationDate = resultSet.getDate("expiration_date")

            tryCatch {
//                val query = "SELECT wp_posts.post_title, wp_posts.post_content, wp_posts.post_date,\n" +
//                        "wp_postmeta.meta_key, wp_postmeta.meta_value\n" +
//                        "FROM wp_posts\n" +
//                        "JOIN wp_postmeta ON wp_posts.ID = wp_postmeta.post_id\n" +
//                        "WHERE wp_posts.ID = $dealId\n" +
//                        "ORDER BY wp_postmeta.post_id ASC"
                val query = "SELECT * FROM wp_posts WHERE ID=$dealId"
                val resultSet = executeQuery(query)

                while (resultSet?.next() == true) {
                    title = resultSet.getString("post_title")
                    description = resultSet.getString("post_content")
                    postDate = resultSet.getDate("post_date").toString()
                }
            }

            tryCatch {
                val query = "SELECT * FROM wp_postmeta WHERE post_id=$dealId ORDER BY wp_postmeta.meta_id ASC"
                val resultSet = executeQuery(query)

                while (resultSet?.next() == true) {
                    when (resultSet.getString("meta_key")) {
                        "old_price" -> oldPrice = resultSet.getString("meta_value").toInt()
                        "price" -> price = resultSet.getString("meta_value").toInt()
                        "source" -> company = resultSet.getString("meta_value")
                        "rating" -> rating = resultSet.getString("meta_value").toInt()
//                        "promocode" -> promocode = resultSet.getString("meta_value")
                        "_thumbnail_id" -> imageId = resultSet.getString("meta_value").toInt()
                    }
                }
            }

            imageUrl = getDealImageUrl(imageId)

            listOfDeals.add(
                Deal(
                    id = dealId,
                    title = title,
                    description = description,
                    imageUrl = imageUrl,
                    companyName = company,
                    categoryId = categoryId,
                    oldPrice = oldPrice,
                    discountPrice = price,
                    rating = rating,
                    promocode = "promocode",
                    publishedDate = postDate,
                )
            )

            log(
                Deal(
                    id = dealId,
                    title = title,
                    description = description,
                    companyName = company,
                    categoryId = categoryId,
                    oldPrice = oldPrice,
                    discountPrice = price,
                    rating = rating,
                    promocode = "promocode",
                )
            )
        }

        return listOfDeals
    }

    private fun getCategoryDealsId(categoryId: Int): List<Int> {
        val listOfDealsId = mutableListOf<Int>()

        tryCatch {
            val query =
                "SELECT * FROM wp_term_relationships WHERE term_taxonomy_id=$categoryId ORDER BY term_taxonomy_id ASC"
            val resultSet = executeQuery(query)
            while (resultSet?.next() == true) {
                listOfDealsId.add(resultSet.getInt("object_id"))
            }
        }

        return listOfDealsId
    }

    private fun getImageUrl(id: Int): String {
        tryCatch {
            val query = "SELECT * FROM wp_posts\n" +
                    "WHERE ID IN (SELECT meta_value FROM wp_termmeta WHERE meta_key = \"icon\" AND term_id = $id)"
            val resultSet = executeQuery(query)
            while (resultSet?.next() == true) {
                return resultSet.getString("guid")
            }
        }
        return ""
    }

    private fun getDealImageUrl(photoId: Int): String {
        tryCatch {
            val query = "SELECT * FROM wp_posts WHERE ID=$photoId"
            val resultSet = executeQuery(query)
            while (resultSet?.next() == true) {
                return resultSet.getString("guid")
            }
        }
        return ""
    }

}

