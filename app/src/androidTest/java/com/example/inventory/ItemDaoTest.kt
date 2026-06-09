package com.example.inventory

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.inventory.data.InventoryDatabase
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ItemDaoTest {

    private lateinit var itemDao: ItemDao
    private lateinit var inventoryDatabase: InventoryDatabase

    private val item1 = Item(1, "Apples", 10.0, 20)
    private val item2 = Item(2, "Bananas", 15.0, 97)

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        inventoryDatabase = Room.inMemoryDatabaseBuilder(
            context,
            InventoryDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        itemDao = inventoryDatabase.itemDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        inventoryDatabase.close()
    }

    // --- 要求 1：測試重複 ID 被忽略 ---
    @Test
    @Throws(Exception::class)
    fun daoInsert_duplicateId_keepsOriginalItem() = runBlocking {
        val originalItem = Item(1, "Original Item", 100.0, 10)
        val duplicateItem = Item(1, "Duplicate Item", 200.0, 20)

        itemDao.insert(originalItem)
        itemDao.insert(duplicateItem) // 因為 OnConflictStrategy.IGNORE，這筆會被忽略

        val allItems = itemDao.getAllItems().first()

        assertEquals(1, allItems.size)
        assertEquals("Original Item", allItems[0].name)
    }

    // --- 要求 3：自動新增學號資料測試 ---
    @Test
    @Throws(Exception::class)
    fun daoInsert_studentData_success() = runBlocking {
        val studentId = "B11330104" // 在此填寫你的學號
        val studentItem = Item(
            id = 100,
            name = studentId,
            price = 100.0,
            quantity = 10
        )

        itemDao.insert(studentItem)

        val insertedItem = itemDao.getItem(100).first()

        assertEquals(studentId, insertedItem.name)
        assertEquals(100.0, insertedItem.price, 0.0)
        assertEquals(10, insertedItem.quantity)
    }

    @Test
    fun daoInsert_insertsItemIntoDB() = runBlocking {
        addOneItemToDb()
        val allItems = itemDao.getAllItems().first()
        assertEquals(item1, allItems[0])
    }

    @Test
    fun daoGetAllItems_returnsAllItemsFromDB() = runBlocking {
        addTwoItemsToDb()
        val allItems = itemDao.getAllItems().first()
        assertEquals(item1, allItems[0])
        assertEquals(item2, allItems[1])
    }

    private suspend fun addOneItemToDb() {
        itemDao.insert(item1)
    }

    private suspend fun addTwoItemsToDb() {
        itemDao.insert(item1)
        itemDao.insert(item2)
    }
}