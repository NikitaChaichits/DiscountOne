package com.digeltech.appdiscountone.data.source.remote

import android.database.SQLException
import com.digeltech.appdiscountone.data.util.tryCatch
import com.digeltech.appdiscountone.domain.model.Category
import com.digeltech.appdiscountone.domain.model.CategoryWithDeals
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.model.Shop
import com.digeltech.appdiscountone.ui.home.adapter.Banner
import com.digeltech.appdiscountone.util.log
import com.orhanobut.hawk.Hawk
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import javax.inject.Inject

private const val DATABASE_URL = "jdbc:mysql://p3plzcpnl497327.prod.phx3.secureserver.net:3306/main"
private const val KEY_SHOPS = "all-shops"
private const val KEY_CATEGORIES = "all-categories"
private const val KEY_HOME_CATEGORIES = "all-home-categories"

/**
 * При загрузке приложения сначала вызывается метод getBanners
 * Сразу после этого метода вызывается getHomeCategories,
 * для которого нужны наименования категорий (метод getCategories), а также иконки магазинов (метод getAllShops)
 */
class DatabaseConnection @Inject constructor() {
    private var connection: Connection? = null
    private var categories: MutableList<Category> = mutableListOf()
    private var homeCategories: MutableList<CategoryWithDeals> = mutableListOf()
    private var shops: MutableList<Shop> = mutableListOf()

    /**
     * Функция для установления соединения с базой данных
     */
    private fun connect() {
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

    /**
     * Функция для проверки соединения с БД
     */
    private fun checkConnection() {
        if (connection?.isClosed == true || connection == null)
            connect()
    }

    /**
     * Функция для выполнения запросов к базе данных
     */
    private fun executeQuery(query: String): ResultSet? {
        try {
            val statement = connection?.createStatement()
            return statement?.executeQuery(query)
        } catch (e: SQLException) {
            log("Error executing query! Error: ${e.message}")
        }
        return null
    }

    /**
     * Функция для закрытия соединения с базой данных
     */
    fun close() {
        try {
            connection?.close()
            log("Connection to MySQL database closed!")
        } catch (e: SQLException) {
            log("Error closing connection! Error: ${e.message}")
        }
    }

    /**
     * Метод для получения списка Категорий
     */
    fun getAllCategories(): List<Category> {
        if (Hawk.contains(KEY_CATEGORIES)) {
            categories = Hawk.get(KEY_CATEGORIES)
        } else {
            checkConnection()

            categories.forEach {
                it.icon = getImageUrl(it.id)
                log("${it.id} ${it.name} ${it.icon}")
            }

            Hawk.put(KEY_CATEGORIES, categories)
        }
        return categories
    }

    /**
     * Метод для получения списка магазинов
     */
    fun getAllShops(): List<Shop> {
        if (Hawk.contains(KEY_SHOPS)) {
            shops = Hawk.get(KEY_SHOPS)
        } else {
            checkConnection()

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

                    shops.add(
                        Shop(
                            id = id, name = name, countOfItems = count, icon = ""
                        )
                    )
                }
            }

            shops.forEach {
                it.icon = getImageUrl(it.id)
                log("${it.id} ${it.name} ${it.icon}")
            }

            Hawk.put(KEY_SHOPS, shops)
        }
        return shops
    }

    /**
     * Метод для получения Купона по его id
     */
    fun getDealsById(id: Int): List<Deal> {
        checkConnection()

        return getListOfDeals(id)
    }

    /**
     * Метод для получения списка ссылок на картинки баннеров на главном экране
     */
    fun getBanners(): List<Banner> {
        checkConnection()

        val listOfBanners = mutableListOf<Banner>()

        tryCatch {
            val query = "SELECT wp_postmeta.meta_key,wp_postmeta.meta_value, wp_posts.guid\n" +
                    "FROM wp_postmeta\n" +
                    "JOIN wp_posts ON wp_postmeta.meta_value = wp_posts.ID\n" +
                    "WHERE (wp_postmeta.meta_key LIKE \"%slider_1%\" OR wp_postmeta.meta_key LIKE \"%slider_3%\") " +
                    "AND wp_postmeta.post_id = 6\n" +
                    "ORDER BY wp_postmeta.meta_key ASC"
            val resultSet = executeQuery(query)

            val listOfBannersUrl = mutableListOf<String>()
            val listOfDealsId = mutableListOf<Int>()

            while (resultSet?.next() == true) {
                val metaKey = resultSet.getString("meta_key")
                if (metaKey.contains("image")) {
                    val imageUrl = resultSet.getString("guid")
                    listOfBannersUrl.add(imageUrl)
                } else {
                    val dealId = resultSet.getString("meta_value")
                    listOfDealsId.add(dealId.toInt())
                }
            }
            listOfBannersUrl.forEachIndexed { index, url ->
                listOfBanners.add(Banner(urlImage = url, dealId = listOfDealsId[index]))
            }
        }

        return listOfBanners
    }

    /**
     * Метод для получения списка Категорий, которые отображаются на главном экране
     */
    fun getHomeCategories(): List<CategoryWithDeals> {
        if (Hawk.contains(KEY_HOME_CATEGORIES)) {
            homeCategories = Hawk.get(KEY_HOME_CATEGORIES)
        } else {
            checkConnection()

            var listOfCategoriesId = listOf<Int>()

            if (categories.isEmpty())
                getCategories() // вызов метода нужен для получения наименований категорий
            if (shops.isEmpty())
                getAllShops() // вызов метода нужен для получения иконок магазинов

            tryCatch {
                val query = "SELECT meta_value FROM wp_postmeta WHERE post_id=6 AND meta_key=\"another_deals\""
                val resultSet = executeQuery(query)

                while (resultSet?.next() == true) {
                    listOfCategoriesId = extractCategoriesId(resultSet.getString("meta_value"))
                }
                log(listOfCategoriesId)
            }

            listOfCategoriesId.forEach {
                val listOfDeals = getListOfDeals(it)
                val categoryName = categories.find { category ->
                    category.id == it
                }?.name ?: ""

                homeCategories.add(
                    CategoryWithDeals(
                        id = it,
                        name = categoryName,
                        items = listOfDeals
                    )
                )
            }

            Hawk.put(KEY_HOME_CATEGORIES, homeCategories)
        }

        return homeCategories
    }

    /**
     * Метод для получения Купонов по его id и id Категории
     */
    fun getDealById(dealId: Int, categoryId: Int): Deal {
        checkConnection()

        var title = ""
        var description = ""
        var oldPrice = ""
        var price = ""
        var sale = ""
        var shop = ""
        var rating = ""
        var imageId = ""
        var promocode = ""
        var link = ""
        var postDate = ""
        var validDate = ""

        tryCatch {
            val query = "SELECT * FROM wp_posts WHERE ID=$dealId"
            val resultSet = executeQuery(query)

            while (resultSet?.next() == true) {
                title = resultSet.getString("post_title")
                description = resultSet.getString("post_content")
                postDate = resultSet.getString("post_date")
            }
        }

        tryCatch {
            val query = "SELECT * FROM wp_postmeta WHERE post_id=$dealId ORDER BY wp_postmeta.meta_id ASC"
            val resultSet = executeQuery(query)

            while (resultSet?.next() == true) {
                when (resultSet.getString("meta_key")) {
                    "old_price" -> oldPrice = resultSet.getString("meta_value")
                    "price" -> price = resultSet.getString("meta_value")
                    "source" -> shop = resultSet.getString("meta_value")
                    "rating" -> rating = resultSet.getString("meta_value")
                    "link" -> link = resultSet.getString("meta_value")
                    "promocode" -> promocode = resultSet.getString("meta_value")
                    "expiration_date" -> validDate = resultSet.getString("meta_value")
                    "_thumbnail_id" -> imageId = resultSet.getString("meta_value")
                    "sale" -> sale = resultSet.getString("meta_value")
                }
            }
        }

        val imageUrl = getDealImageUrl(imageId)
        val shopImageUrl = shops.find {
            it.name == shop
        }?.icon ?: ""

        return Deal(
            id = dealId,
            categoryId = categoryId,
            title = title,
            description = description,
            imageUrl = imageUrl,
            shopName = shop,
            shopImageUrl = shopImageUrl,
            oldPrice = oldPrice,
            discountPrice = price,
            sale = sale,
            rating = rating,
            promocode = promocode,
            link = link,
            publishedDate = postDate,
            validDate = validDate,
        )
    }

    fun getSimilarDeals(categoryId: Int, dealId: Int): List<Deal> {
        val listOfDeals = homeCategories.find {
            it.id == categoryId
        }?.items?.filter {
            it.id != dealId
        }

        return listOfDeals ?: emptyList()
    }

    /**
     * Метод для получения списка id Купонов по id Категории или id Магазина
     */
    private fun getListOfDealsId(categoryId: Int): List<Int> {
        val listOfDealsId = mutableListOf<Int>()

        tryCatch {
            val query = "SELECT * FROM wp_term_relationships WHERE term_taxonomy_id=$categoryId " +
                    "ORDER BY term_taxonomy_id ASC " +
                    "LIMIT 6"
            val resultSet = executeQuery(query)
            while (resultSet?.next() == true) {
                listOfDealsId.add(resultSet.getInt("object_id"))
            }
        }

        return listOfDealsId
    }

    /**
     * Метод для получения списка Купонов по id Категории или id Магазина
     */
    private fun getListOfDeals(id: Int): List<Deal> {
        val listOfDeals = mutableListOf<Deal>()

        getListOfDealsId(id).forEach { dealId ->
            listOfDeals.add(getDealById(dealId, id))
        }
        return listOfDeals
    }

    /**
     * Метод для получения списка Категорий без картинок
     */
    private fun getCategories(): List<Category> {
        checkConnection()

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

                categories.add(Category(id = id, name = name, countOfItems = count, icon = ""))
            }
        }
        return categories
    }

    /**
     * Метод для получения картинки Категории или Магазина
     */
    private fun getImageUrl(id: Int): String {
        tryCatch {
            val query = "SELECT guid FROM wp_posts\n" +
                    "WHERE ID IN (SELECT meta_value FROM wp_termmeta WHERE meta_key = \"icon\" AND term_id = $id)"
            val resultSet = executeQuery(query)
            while (resultSet?.next() == true) {
                return resultSet.getString("guid")
            }
        }
        return ""
    }

    /**
     * Метод для получения картинки Купона
     */
    private fun getDealImageUrl(photoId: String): String {
        tryCatch {
            val query = "SELECT guid FROM wp_posts WHERE ID=$photoId"
            val resultSet = executeQuery(query)
            while (resultSet?.next() == true) {
                return resultSet.getString("guid")
            }
        }
        return ""
    }

    /**
     * Метод для получения списка id Категорий, которые отображаются на главном экране
     */
    private fun extractCategoriesId(input: String): List<Int> {
        val regex = """"([^"]*)"""".toRegex()
        return regex.findAll(input)
            .map { it.groups[1]?.value?.toInt() }
            .filterNotNull()
            .toList()
    }

}

