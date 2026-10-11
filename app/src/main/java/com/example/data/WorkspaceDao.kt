package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceDao {
    @Query("SELECT * FROM workspace_items WHERE isDeleted = 0 ORDER BY isPinned DESC, kind = 'folder' DESC, name ASC")
    fun getAllLive(): Flow<List<WorkspaceItem>>

    @Query("SELECT * FROM workspace_items WHERE isDeleted = 1 ORDER BY deletedTimestamp DESC")
    fun getTrashItems(): Flow<List<WorkspaceItem>>

    @Query("SELECT * FROM workspace_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: String): WorkspaceItem?

    @Query("SELECT * FROM workspace_items WHERE isDeleted = 0 AND (parentId = :parentId OR (:parentId IS NULL AND parentId IS NULL)) ORDER BY isPinned DESC, kind = 'folder' DESC, name ASC")
    fun getItemsInFolder(parentId: String?): Flow<List<WorkspaceItem>>

    @Query("SELECT * FROM workspace_items WHERE isDeleted = 0 AND (name LIKE '%' || :query || '%' OR text LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%')")
    fun searchItems(query: String): Flow<List<WorkspaceItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: WorkspaceItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<WorkspaceItem>)

    @Update
    suspend fun updateItem(item: WorkspaceItem)

    @Query("UPDATE workspace_items SET isDeleted = 1, deletedTimestamp = :timestamp WHERE id = :id")
    suspend fun moveToTrash(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE workspace_items SET isDeleted = 0, deletedTimestamp = NULL WHERE id = :id")
    suspend fun restoreItem(id: String)

    @Query("DELETE FROM workspace_items WHERE id = :id")
    suspend fun deletePermanently(id: String)

    @Query("DELETE FROM workspace_items WHERE id IN (:ids)")
    suspend fun deletePermanently(ids: List<String>)

    @Query("SELECT COUNT(*) FROM workspace_items")
    suspend fun countAll(): Int
}
