package com.ragul.notetaking.data.repository

import androidx.lifecycle.LiveData
import com.ragul.notetaking.data.dao.NoteDao
import com.ragul.notetaking.data.model.Note

class NoteRepository(private val noteDao: NoteDao) {
    
    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()
    val archivedNotes: LiveData<List<Note>> = noteDao.getArchivedNotes()
    
    suspend fun insert(note: Note): Long {
        return noteDao.insertNote(note)
    }
    
    suspend fun update(note: Note): Int {
        return noteDao.updateNote(note)
    }
    
    suspend fun delete(note: Note): Int {
        return noteDao.deleteNote(note)
    }
    
    suspend fun getNoteById(id: Int): Note? {
        return noteDao.getNoteById(id)
    }
    
    fun searchNotes(query: String): LiveData<List<Note>> {
        return noteDao.searchNotes(query)
    }
}