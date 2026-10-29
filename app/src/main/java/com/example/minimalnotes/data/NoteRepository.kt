package com.example.minimalnotes.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {
    fun getAllNotes(): Flow<List<Note>> = dao.getAllNotes()
    fun searchNotes(query: String): Flow<List<Note>> = dao.searchNotes(query)
    suspend fun getNoteById(id: Long): Note? = dao.getNoteById(id)
    suspend fun insert(note: Note): Long = dao.insert(note)
    suspend fun update(note: Note) = dao.update(note)
    suspend fun delete(note: Note) = dao.delete(note)
}