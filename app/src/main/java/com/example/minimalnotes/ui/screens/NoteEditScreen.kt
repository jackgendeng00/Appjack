package com.example.minimalnotes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.minimalnotes.data.Note
import com.example.minimalnotes.ui.theme.TagColors
import com.example.minimalnotes.viewmodel.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
    noteId: Long,
    viewModel: NotesViewModel,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isPinned by remember { mutableStateOf(false) }
    var colorTag by remember { mutableStateOf(0) }
    var currentNote by remember { mutableStateOf<Note?>(null) }
    var loaded by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        if (noteId != 0L) {
            viewModel.getNote(noteId)?.let {
                title = it.title; content = it.content
                isPinned = it.isPinned; colorTag = it.colorTag
                currentNote = it
            }
        }
        loaded = true
    }

    val onSave: () -> Unit = {
        val note = Note(
            id = if (noteId == 0L) 0 else noteId,
            title = title.trim(), content = content.trim(),
            isPinned = isPinned, colorTag = colorTag,
            createdAt = currentNote?.createdAt ?: System.currentTimeMillis()
        )
        viewModel.saveNote(note) { onBack() }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back",
                            tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                actions = {
                    IconButton(onClick = { isPinned = !isPinned }) {
                        Icon(
                            if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            "Pin", tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    if (noteId != 0L) {
                        IconButton(onClick = {
                            currentNote?.let { viewModel.deleteNote(it) }
                            onBack()
                        }) {
                            Icon(Icons.Default.Delete, "Delete",
                                tint = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                    IconButton(onClick = onSave) {
                        Icon(Icons.Default.Check, "Save",
                            tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (!loaded) return@Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text("Title",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TagColors.forEachIndexed { index, color ->
                    Box(
                        modifier = Modifier
                            .size(24.dp).clip(CircleShape).background(color)
                            .then(
                                if (index == colorTag) Modifier.border(
                                    2.dp, MaterialTheme.colorScheme.onBackground, CircleShape
                                ) else Modifier
                            )
                            .clickable { colorTag = index }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            TextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Start writing...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier.fillMaxSize().padding(bottom = 16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    }
}