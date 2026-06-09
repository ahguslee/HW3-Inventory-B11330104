package com.example.inventory.ui.item

import androidx.lifecycle.SavedStateHandle
import com.example.inventory.data.Item
import com.example.inventory.data.ItemsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class ItemDetailsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var itemsRepository: ItemsRepository
    private lateinit var viewModel: ItemDetailsViewModel
    private val testItem = Item(1, "Test Item", 10.0, 5)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        itemsRepository = mock()

        // 模擬 Repository 回傳 Flow
        `when`(itemsRepository.getItemStream(1)).thenReturn(flowOf(testItem))

        val savedStateHandle = SavedStateHandle(mapOf("itemId" to 1))
        viewModel = ItemDetailsViewModel(savedStateHandle, itemsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun reduceQuantityByOne_quantityGreaterThanZero_updatesRepository() = runTest {
        // 【關鍵】等待 uiState 變成預期的 testItem 資料
        viewModel.uiState.first { it.itemDetails.id == testItem.id }

        viewModel.reduceQuantityByOne()

        // 驗證庫存從 5 變 4
        verify(itemsRepository).updateItem(testItem.copy(quantity = 4))
    }

    @Test
    fun reduceQuantityByOne_quantityIsZero_doesNotUpdateRepository() = runTest {
        val outOfStockItem = testItem.copy(quantity = 0)
        `when`(itemsRepository.getItemStream(1)).thenReturn(flowOf(outOfStockItem))

        val viewModel0 = ItemDetailsViewModel(SavedStateHandle(mapOf("itemId" to 1)), itemsRepository)

        // 【關鍵】等待 uiState 更新為庫存 0 的資料
        viewModel0.uiState.first { it.itemDetails.quantity == "0" }

        viewModel0.reduceQuantityByOne()

        verify(itemsRepository, never()).updateItem(any())
    }

    @Test
    fun deleteItem_callsRepositoryDelete() = runTest {
        // 【關鍵】等待 uiState 載入完成（id 不再是 0）
        viewModel.uiState.first { it.itemDetails.id == testItem.id }

        viewModel.deleteItem()

        // 這時 uiState.value 已經是正確的資料了，verify 應該會通過
        verify(itemsRepository).deleteItem(testItem)
    }
}