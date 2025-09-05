package com.ragul.notetaking.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ragul.notetaking.data.model.Note
import com.ragul.notetaking.ui.components.ColorSelector
import com.ragul.notetaking.ui.theme.GreenishBlue
import com.ragul.notetaking.ui.viewmodel.NoteViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    navController: NavController,
    noteId: Int? = null
) {
    val noteViewModel: NoteViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isNewNote by remember { mutableStateOf(noteId == null) }
    var currentNote by remember { mutableStateOf<Note?>(null) }
    var selectedColor by remember { mutableStateOf<Color>(GreenishBlue) }
    var isLoaded by remember { mutableStateOf(false) }

    // Load existing note if editing
    LaunchedEffect(noteId) {
        if (noteId != null && !isLoaded) {
            coroutineScope.launch {
                val note = noteViewModel.getNoteById(noteId)
                note?.let {
                    currentNote = it
                    title = it.title
                    content = it.content
                    it.color?.let { colorInt ->
                        selectedColor = Color(colorInt)
                    }
                    isLoaded = true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNewNote) "New Note" else "Edit Note") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isNewNote) {
                        IconButton(onClick = {
                            currentNote?.let {
                                noteViewModel.deleteNote(it)
                                navController.popBackStack()
                            }
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete Note")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isNewNote) {
                        if (title.isNotBlank() || content.isNotBlank()) {
                            noteViewModel.insertNote(title, content, selectedColor.toArgb())
                            navController.popBackStack()
                        }
                    } else {
                        // For existing notes, update with current content
                        currentNote?.let {
                            // Create a new note object with updated fields
                            val updatedNote = Note(
                                id = it.id,
                                title = title,
                                content = content,
                                createdDate = it.createdDate,
                                modifiedDate = Date(), // This will be updated in viewModel
                                isArchived = it.isArchived,
                                isPinned = it.isPinned,
                                color = selectedColor.toArgb()
                            )
                            // Update the note in the database
                            noteViewModel.updateNote(updatedNote)
                            // Navigate back
                            navController.popBackStack()
                        }
                    }
                }
            ) {
                Icon(Icons.Filled.Check, contentDescription = "Save Note")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(selectedColor.copy(alpha = 0.3f))
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = {
                    content = it
                },
                label = { Text("Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                maxLines = Int.MAX_VALUE
            )

            Spacer(modifier = Modifier.height(16.dp))

            ColorSelector(
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it }
            )
        }
    }
}