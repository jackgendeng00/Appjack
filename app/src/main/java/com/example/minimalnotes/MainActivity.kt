package com.example.minimalnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.minimalnotes.ui.screens.NoteEditScreen
import com.example.minimalnotes.ui.screens.NotesListScreen
import com.example.minimalnotes.ui.theme.MinimalNotesTheme
import com.example.minimalnotes.viewmodel.NotesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repo = (application as MinimalNotesApp).repository

        setContent {
            MinimalNotesTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val nav = rememberNavController()
                    NavHost(navController = nav, startDestination = "list") {
                        composable("list") {
                            val vm: NotesViewModel = viewModel(factory = NotesViewModel.Factory(repo))
                            NotesListScreen(
                                viewModel = vm,
                                onNoteClick = { nav.navigate("edit/$it") },
                                onNewNote = { nav.navigate("edit/0") }
                            )
                        }
                        composable(
                            route = "edit/{noteId}",
                            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
                        ) { entry ->
                            val id = entry.arguments?.getLong("noteId") ?: 0L
                            val vm: NotesViewModel = viewModel(factory = NotesViewModel.Factory(repo))
                            NoteEditScreen(
                                noteId = id, viewModel = vm,
                                onBack = { nav.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}