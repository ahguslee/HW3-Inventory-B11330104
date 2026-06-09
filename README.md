HW3 存貨資料庫測試案例說明

GitHub 連結：
（請貼上自己的 GitHub Repository 連結）

助教權限：
已將 cookiecatowo、penpenpenguin 加入 GitHub Repository 可存取名單。

使用工具：
本次作業使用 Android Studio、GitHub、ChatGPT，以及 Android Studio 內建的 Gemini 輔助產生與修正測試程式。

使用 Prompt：

1. 請幫我在 ItemDaoTest.kt 新增測試，測試插入兩筆相同 ID 的資料時，只會保留原本第一筆資料。
2. 請幫我為 ItemDetailsViewModel 產生 unit test，測試 reduceQuantityByOne() 在數量大於 0 時會呼叫 updateItem()。
3. 請幫我測試 reduceQuantityByOne() 在數量等於 0 時不會呼叫 updateItem()。
4. 請幫我測試 deleteItem() 是否有正確呼叫 itemsRepository.deleteItem()。
5. 請幫我新增一個自動測試，新增 Item Name 為 B11330104、Item Price 為 100、Quantity in Stock 為 10 的資料，並確認新增成功。

完成方式與修正內容：
要求一中，我在 ItemDaoTest.kt 新增 daoInsertSameId_keepsOriginalItem() 測試。測試中先新增一筆 ID 為 1 的資料，再新增另一筆相同 ID 但內容不同的資料，最後用 getItem(1) 取得資料並確認資料仍然是第一筆，表示相同 ID 的第二筆資料沒有覆蓋原本資料。

要求二中，我建立 ItemDetailsViewModelTest.kt，使用 mock repository 測試 ItemDetailsViewModel 的三個行為。第一個測試確認 reduceQuantityByOne() 在庫存數量大於 0 時，會呼叫 updateItem() 更新資料。第二個測試確認數量為 0 時不會呼叫 updateItem()。第三個測試確認 deleteItem() 會正確呼叫 itemsRepository.deleteItem()。過程中有依照實際 ViewModel 的 uiState 與 repository 結構調整測試寫法，讓三個 unit test 都能成功通過。

要求三中，我在 ItemDaoTest.kt 新增 daoInsertStudentData_success() 測試。測試會自動新增一筆 Item，內容為 Item Name：B11330104、Item Price：100、Quantity in Stock：10，接著從資料庫讀取該筆資料，並用 assertEquals 確認名稱、價格與數量都正確。

執行的測試程式：

1. ItemDaoTest.kt

   * daoInsertSameId_keepsOriginalItem()
   * daoInsertStudentData_success()

2. ItemDetailsViewModelTest.kt

   * reduceQuantityByOne_quantityGreaterThanZero_updatesRepository()
   * reduceQuantityByOne_quantityIsZero_doesNotUpdateRepository()
   * deleteItem_callsRepositoryDeleteItem()

測試結果：
ItemDetailsViewModelTest.kt 的三個測試皆成功通過。第三個測試因為 Android instrumented test 版本與環境問題，無法正常直接執行，因此依作業要求提供截圖作為佐證。
