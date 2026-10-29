package com.example.minimalnotes

import android.app.Application
import com.example.minimalnotes.data.NoteDatabase
import com.example.minimalnotes.data.NoteRepository

class MinimalNotesApp : Application() {
    val repository by lazy { NoteRepository(NoteDatabase.getDatabase(this).noteDao()) }
}