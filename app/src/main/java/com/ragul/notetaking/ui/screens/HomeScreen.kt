package com.ragul.notetaking.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ragul.notetaking.Router
import com.ragul.notetaking.data.model.Note
import com.ragul.notetaking.ui.components.NoteItem
import com.ragul.notetaking.ui.components.SwipeableNoteItem
import com.ragul.notetaking.ui.components.ColorSelector
import com.ragul.notetaking.ui.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val noteViewModel: NoteViewModel = viewModel()
    val notes by noteViewModel.allNotes.observeAsState(initial = emptyList())
    val undoAvailable by noteViewModel.undoAvailable.observeAsState(false)
    val snackbarHostState = remember { SnackbarHostState() }

    // New state to show inline editor on the same screen
    var selectedNote by remember { mutableStateOf<Note?>(null) }

    LaunchedEffect(undoAvailable) {
        if (undoAvailable) {
            val result = snackbarHostState.showSnackbar(
                message = "Note deleted",
                actionLabel = "UNDO",
                duration = SnackbarDuration.Short
            )
            when (result) {
                SnackbarResult.ActionPerformed -> noteViewModel.undoDelete()
                SnackbarResult.Dismissed -> noteViewModel.clearUndo()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Navigate to note editor screen
                    navController.navigate(Router.CreateNote)
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Note")
            }
        }
    ) { paddingValues ->
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No notes yet. Click the + button to add a note.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(notes) { note ->
                    SwipeableNoteItem(
                        note = note,
                        onNoteClick = {
                            // Previously navigated to edit screen, now open inline editor by default
                            selectedNote = it
                        },
                        onDelete = { noteToDelete ->
                            noteViewModel.deleteNote(noteToDelete)
                        },
                        onLongPress = { longPressedNote ->
                            // Keep existing behavior for long press (navigate to full editor)
                            navController.navigate(Router.editNoteRoute(longPressedNote.id))
                        }
                    )
                }
            }
        }

        // Inline editor dialog shown when a note is selected
        selectedNote?.let { noteToEdit ->
            // Local state for editing fields
            var editTitle by remember { mutableStateOf(noteToEdit.title) }
            var editContent by remember { mutableStateOf(noteToEdit.content) }
            var editColor by remember { mutableStateOf(Color(noteToEdit.color ?: 0xFF7CE0D9.toInt())) }

            // Ensure state is updated if selectedNote changes
            LaunchedEffect(noteToEdit) {
                editTitle = noteToEdit.title
                editContent = noteToEdit.content
                editColor = Color(noteToEdit.color ?: 0xFF7CE0D9.toInt())
            }

            AlertDialog(
                onDismissRequest = { selectedNote = null },
                title = { Text(text = "Edit Note") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = editTitle,
                            onValueChange = { editTitle = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxSize()
                        )

                        OutlinedTextField(
                            value = editContent,
                            onValueChange = { editContent = it },
                            label = { Text("Content") },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 8.dp)
                        )

                        ColorSelector(
                            selectedColor = editColor,
                            onColorSelected = { editColor = it }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        // Build updated note and save
                        val updated = noteToEdit.copy(
                            title = editTitle,
                            content = editContent,
                            color = editColor.toArgb()
                        )
                        noteViewModel.updateNote(updated)
                        selectedNote = null
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedNote = null }) {
                        Text("Cancel")
                    }
                }
            )
    }
}

@Composable
fun NotesContent(
    paddingValues: PaddingValues,
    notes: List<Note>,
    onNoteClick: (Note) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (notes.isEmpty()) {
            // Empty state
            Text(
                text = "No notes yet. Tap + to create one.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .align(Alignment.Center),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // Notes list
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(notes) { note ->
                    NoteItem(note = note, onNoteClick = onNoteClick)
                }
            }
        }
    }
}
}