package com.ragul.notetaking.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.RoomWarnings
import androidx.room.Update
import com.ragul.notetaking.data.model.Note

@Dao
@RewriteQueriesToDropUnusedColumns
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note): Int

    @Delete
    suspend fun deleteNote(note: Note): Int

    @Query("SELECT * FROM notes WHERE isArchived = 0 ORDER BY isPinned DESC, modifiedDate DESC")
    fun getAllNotes(): LiveData<List<Note>>
    
    @Query("SELECT * FROM notes WHERE id = :noteId")
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    suspend fun getNoteById(noteId: Int): Note?

    @Query("SELECT * FROM notes WHERE isArchived = 1 ORDER BY modifiedDate DESC")
    fun getArchivedNotes(): LiveData<List<Note>>
    
    @Query("SELECT * FROM notes WHERE title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%'")
    fun searchNotes(searchQuery: String): LiveData<List<Note>>
}