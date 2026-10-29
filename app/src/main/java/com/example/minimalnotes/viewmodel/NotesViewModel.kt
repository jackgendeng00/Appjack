package com.example.minimalnotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.minimalnotes.data.Note
import com.example.minimalnotes.data.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class NotesViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val notes: StateFlow<List<Note>> = _searchQuery
        .debounce(200)
        .flatMapLatest { q ->
            if (q.isBlank()) repository.getAllNotes() else repository.searchNotes(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(q: String) { _searchQuery.value = q }

    fun saveNote(note: Note, onSaved: (Long) -> Unit) = viewModelScope.launch {
        val updated = note.copy(updatedAt = System.currentTimeMillis())
        val id = if (updated.id == 0L) repository.insert(updated)
                 else { repository.update(updated); updated.id }
        onSaved(id)
    }

    fun deleteNote(note: Note) = viewModelScope.launch { repository.delete(note) }

    suspend fun getNote(id: Long): Note? = repository.getNoteById(id)

    class Factory(private val repo: NoteRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            NotesViewModel(repo) as T
    }
}