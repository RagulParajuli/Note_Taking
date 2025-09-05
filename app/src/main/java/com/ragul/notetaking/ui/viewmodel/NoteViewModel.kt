package com.ragul.notetaking.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ragul.notetaking.data.database.NoteDatabase
import com.ragul.notetaking.data.model.Note
import com.ragul.notetaking.data.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    private val _allNotes = MutableStateFlow<List<Note>>(emptyList())
    val allNotes: StateFlow<List<Note>> = _allNotes

    // For undo functionality
    private var recentlyDeletedNote: Note? = null
    private val _undoAvailable = MutableLiveData<Boolean>(false)
    val undoAvailable: LiveData<Boolean> = _undoAvailable

    init {
        val noteDao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
        viewModelScope.launch {
            repository.allNotes.observeForever { notes ->
                _allNotes.value = notes
            }
        }
    }

    fun insertNote(title: String, content: String, color: Int? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentDate = Date()
            val note = Note(
                title = title,
                content = content,
                createdDate = currentDate,
                modifiedDate = currentDate,
                color = color
            )
            repository.insert(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            // Always update the modification date when updating a note
            val updatedNote = note.copy(modifiedDate = Date())
            // Update the note in the database
            repository.update(updatedNote)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            // Store the note before deleting for undo functionality
            recentlyDeletedNote = note
            repository.delete(note)
            _undoAvailable.postValue(true)
        }
    }

    fun undoDelete() {
        viewModelScope.launch(Dispatchers.IO) {
            recentlyDeletedNote?.let { note ->
                repository.insert(note)
                recentlyDeletedNote = null
                _undoAvailable.postValue(false)
            }
        }
    }

    fun clearUndo() {
        recentlyDeletedNote = null
        _undoAvailable.postValue(false)
    }

    fun toggleArchiveStatus(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedNote = note.copy(
                isArchived = !note.isArchived,
                modifiedDate = Date()
            )
            repository.update(updatedNote)
        }
    }

    fun togglePinStatus(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedNote = note.copy(
                isPinned = !note.isPinned,
                modifiedDate = Date()
            )
            repository.update(updatedNote)
        }
    }

    fun searchNotes(query: String): LiveData<List<Note>> {
        return repository.searchNotes(query)
    }

    suspend fun getNoteById(id: Int): Note? {
        return repository.getNoteById(id)
    }
}