package com.digeltech.appdiscountone.data.source.remote

import android.database.SQLException
import com.digeltech.appdiscountone.data.util.tryCatch
import com.digeltech.appdiscountone.domain.model.Category
import com.digeltech.appdiscountone.domain.model.CategoryWithDeals
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.domain.model.Shop
import com.digeltech.appdiscountone.ui.common.addedDealToCache
import com.digeltech.appdiscountone.ui.common.getDealFromCache
import com.digeltech.appdiscountone.ui.home.adapter.Banner
import com.digeltech.appdiscountone.util.log
import com.orhanobut.hawk.Hawk
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import javax.inject.Inject

private const val DATABASE_URL = "jdbc:mysql://p3plzcpnl497327.prod.phx3.secureserver.net:3306/main"
const val KEY_CATEGORIES = "all-categories"
const val KEY_SHOPS = "all-shops"
const val KEY_HOME_CATEGORIES = "all-home-categories"

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
    private var homeCategoriesId = listOf<Int>()

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
    fun getAllCategories(isCacheUpdate: Boolean): List<Category> {
        if (!isCacheUpdate && Hawk.contains(KEY_CATEGORIES)) {
            categories = Hawk.get(KEY_CATEGORIES)
        } else {
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
    fun getAllShops(isCacheUpdate: Boolean): List<Shop> {
        if (!isCacheUpdate && Hawk.contains(KEY_SHOPS)) {
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
                        "ORDER BY wp_terms.name ASC"

                val resultSet = executeQuery(query)
                while (resultSet?.next() == true) {
                    val id = resultSet.getInt("term_id")
                    val name = resultSet.getString("name")
                    val count = resultSet.getInt("count")
                    val metaValue = resultSet.getInt("meta_value")

                    shops.add(
                        Shop(
                            id = id, name = name, countOfItems = count, icon = "", popular = metaValue == 1
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
     * Метод для получения всех Скидок
     */
    fun getAllDeals(limit: Int, offset: Int): List<Deal> {
        checkConnection()

        val listOfDeals = mutableListOf<Deal>()
        val listOfId = mutableListOf<Pair<Int, Int>>()

        tryCatch {
            val query = "SELECT * FROM wp_term_relationships " +
                    "GROUP BY wp_term_relationships.object_id DESC " +
                    "LIMIT $limit " +
                    "OFFSET $offset"

            val resultSet = executeQuery(query)
            while (resultSet?.next() == true) {
                val dealId = resultSet.getInt("object_id")
                val categoryId = resultSet.getInt("term_taxonomy_id")
                listOfId.add(Pair(dealId, categoryId))
            }
        }

        listOfId.forEach {
            listOfDeals.add(getDeal(it.first, it.second))
        }

        return listOfDeals
    }

    /**
     * Метод для получения всех Скидок
     */
    fun getAllCoupons(limit: Int, offset: Int): List<Deal> {
        checkConnection()

        val listOfDeals = mutableListOf<Deal>()
        val listOfId = mutableListOf<Pair<Int, Int>>()

        tryCatch {
            val query = "SELECT * FROM wp_term_relationships " +
                    "JOIN wp_postmeta ON wp_term_relationships.object_id = wp_postmeta.post_id " +
                    "WHERE wp_postmeta.meta_key = \"promocode\" " +
                    "AND wp_postmeta.meta_value != \"\" " +
                    "GROUP BY wp_term_relationships.object_id DESC " +
                    "LIMIT $limit " +
                    "OFFSET $offset"

            val resultSet = executeQuery(query)
            while (resultSet?.next() == true) {
                val dealId = resultSet.getInt("object_id")
                val categoryId = resultSet.getInt("term_taxonomy_id")
                listOfId.add(Pair(dealId, categoryId))
            }
        }

        listOfId.forEach {
            listOfDeals.add(getDeal(it.first, it.second))
        }

        return listOfDeals
    }

    /**
     * Метод для получения списка ссылок на картинки баннеров на главном экране
     */
    fun getBanners(): List<Banner> {
        checkConnection()

        val listOfBanners = mutableListOf<Banner>()

        tryCatch {
            // первый запрос для получения ссылок на картинки для баннеров
            val queryForImageUrl = "SELECT wp_postmeta.meta_key, wp_posts.guid " +
                    "FROM wp_postmeta JOIN wp_posts ON wp_postmeta.meta_value = wp_posts.ID " +
                    "WHERE (wp_postmeta.meta_key LIKE \"%slider_1%\" OR wp_postmeta.meta_key LIKE \"%slider_3%\") " +
                    "AND wp_postmeta.post_id = 6 " +
                    "ORDER BY wp_postmeta.meta_key ASC"

            // второй запрос для получения id Deal и id Category
            val queryForId =
                "SELECT DISTINCT wp_postmeta.meta_key,wp_term_relationships.term_taxonomy_id, wp_postmeta.meta_value\n" +
                        "FROM wp_postmeta\n" +
                        "JOIN wp_posts ON wp_postmeta.meta_value = wp_posts.ID\n" +
                        "JOIN wp_term_relationships ON wp_posts.ID = wp_term_relationships.object_id\n" +
                        "WHERE (wp_postmeta.meta_key LIKE \"%slider_1%\" OR wp_postmeta.meta_key LIKE \"%slider_3%\") " +
                        "AND wp_postmeta.post_id = 6\n" +
                        "GROUP BY wp_postmeta.meta_value\n" +
                        "ORDER BY wp_postmeta.meta_key ASC"

            val resultSetUrl = executeQuery(queryForImageUrl)
            val listOfBannersUrl = mutableListOf<String>()

            while (resultSetUrl?.next() == true) {
                val metaKey = resultSetUrl.getString("meta_key")
                if (metaKey.contains("image")) {
                    val imageUrl = resultSetUrl.getString("guid")
                    listOfBannersUrl.add(imageUrl)
                }
            }

            val resultSetId = executeQuery(queryForId)
            val listOfDealsId = mutableListOf<Pair<Int, Int>>()

            while (resultSetId?.next() == true) {
                val dealId = resultSetId.getString("meta_value")
                val categoryId = resultSetId.getString("term_taxonomy_id")
                listOfDealsId.add(Pair(dealId.toInt(), categoryId.toInt()))
            }

            listOfBannersUrl.forEachIndexed { index, url ->
                listOfBanners.add(
                    Banner(
                        urlImage = url,
                        dealId = listOfDealsId[index].first,
                        categoryId = listOfDealsId[index].second
                    )
                )
            }
        }

        return listOfBanners
    }

    /**
     * Метод для получения списка Категорий, которые отображаются на главном экране
     */
    fun getInitHomeCategories(): List<CategoryWithDeals> {
        checkConnection()

        if (categories.isEmpty())
            getAllCategories(false) // вызов метода нужен для получения наименований категорий
        if (shops.isEmpty())
            getAllShops(false) // вызов метода нужен для получения иконок магазинов

        /**
         * получение списка id Категорий, которые отображаются на главном экране
         */
        tryCatch {
            val query = "SELECT meta_value FROM wp_postmeta WHERE post_id=6 AND meta_key=\"another_deals\""
            val resultSet = executeQuery(query)

            while (resultSet?.next() == true) {
                homeCategoriesId = extractCategoriesId(resultSet.getString("meta_value"))
            }
            log(homeCategoriesId)
        }

        for (i in 0..4) { // предзагрузка 5 категорий, а после грузим в фоновом режиме
            val listOfDeals = getListOfDeals(homeCategoriesId[i], 6, 0)
            val categoryName = categories.find { category ->
                category.id == homeCategoriesId[i]
            }?.name ?: ""

            homeCategories.add(
                CategoryWithDeals(
                    id = homeCategoriesId[i],
                    name = categoryName,
                    items = listOfDeals
                )
            )
            log("Home $categoryName loaded")
        }
        return homeCategories
    }

    fun getAllHomeCategories(): List<CategoryWithDeals> {
        for (i in 5..homeCategoriesId.size.dec()) {
            val listOfDeals = getListOfDeals(homeCategoriesId[i], 6, 0)
            val categoryName = categories.find { category ->
                category.id == homeCategoriesId[i]
            }?.name ?: ""

            homeCategories.add(
                CategoryWithDeals(
                    id = homeCategoriesId[i],
                    name = categoryName,
                    items = listOfDeals
                )
            )
            log("Home $categoryName loaded")
        }
        Hawk.put(KEY_HOME_CATEGORIES, homeCategories)
        return homeCategories
    }

    fun updateHomeCategoriesDealsInCache() {
        checkConnection()

        if (categories.isEmpty())
            getAllCategories(false)
        if (shops.isEmpty())
            getAllShops(false)

        /**
         * получение списка id Категорий, которые отображаются на главном экране
         */
        tryCatch {
            val query = "SELECT meta_value FROM wp_postmeta WHERE post_id=6 AND meta_key=\"another_deals\""
            val resultSet = executeQuery(query)

            while (resultSet?.next() == true) {
                homeCategoriesId = extractCategoriesId(resultSet.getString("meta_value"))
            }
            log(homeCategoriesId)
        }

        homeCategoriesId.forEach { id ->
            val listOfDeals = getListOfDeals(id, 6, 0)
            val categoryName = categories.find { category ->
                category.id == id
            }?.name ?: ""

            homeCategories.add(
                CategoryWithDeals(
                    id = id,
                    name = categoryName,
                    items = listOfDeals
                )
            )
            log("$categoryName loaded")
        }
        Hawk.put(KEY_HOME_CATEGORIES, homeCategories)
        log("Home categories updated in cache")
    }

    /**
     * Метод для получения Купонов по id Категории/Магазина
     */
    fun getDealsById(id: Int, limit: Int, offset: Int): List<Deal> {
        checkConnection()

        return getListOfDeals(id, limit, offset)
    }

    /**
     * Метод для получения Купона по его id и id Категории
     */
    fun getDeal(dealId: Int, categoryId: Int): Deal {
        val cachedDeal = getDealFromCache(dealId)
        if (cachedDeal != null) {
            log("Loaded deal=$dealId from cache")
            return cachedDeal
        } else {
            checkConnection()

            var title = ""
            var description = ""
            var oldPrice = ""
            var price = ""
            var sale = ""
            var shopName = ""
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
                        "source" -> shopName = resultSet.getString("meta_value")
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
                it.name.equals(shopName.trim(), true)
            }?.icon ?: ""

            log("Loaded deal=$dealId from database")
            val deal = Deal(
                id = dealId,
                categoryId = categoryId,
                title = title,
                description = description,
                imageUrl = imageUrl,
                shopName = shopName,
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
            addedDealToCache(deal)
            return deal
        }
    }

    /**
     * Метод для получения списка id Купонов по id Категории или id Магазина
     */
    private fun getListOfDealsId(categoryId: Int, limit: Int, offset: Int): List<Int> {
        val listOfDealsId = mutableListOf<Int>()

        tryCatch {
            val query = if (limit > 0) {
                "SELECT * FROM wp_term_relationships WHERE term_taxonomy_id=$categoryId " +
                        "ORDER BY object_id DESC " +
                        "LIMIT $limit " +
                        "OFFSET $offset"
            } else {
                "SELECT * FROM wp_term_relationships WHERE term_taxonomy_id=$categoryId " +
                        "ORDER BY object_id DESC " +
                        "OFFSET $offset"
            }

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
    private fun getListOfDeals(id: Int, limit: Int, offset: Int): List<Deal> {
        val listOfDeals = mutableListOf<Deal>()

        getListOfDealsId(id, limit, offset).forEach { dealId ->
            listOfDeals.add(getDeal(dealId, id))
        }
        return listOfDeals
    }

    private fun getCategoryIdByDealId(dealId: Int): Int {
        tryCatch {
            val query = "SELECT term_taxonomy_id FROM wp_term_relationships \n" +
                    "WHERE object_id=$dealId \n" +
                    "LIMIT 1"
            val resultSet = executeQuery(query)
            while (resultSet?.next() == true) {
                return resultSet.getInt("term_taxonomy_id")
            }
        }
        return 0
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

