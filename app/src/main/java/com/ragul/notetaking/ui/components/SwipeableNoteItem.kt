package com.ragul.notetaking.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.ragul.notetaking.data.model.Note
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeableNoteItem(
    note: Note,
    onNoteClick: (Note) -> Unit,
    onDelete: (Note) -> Unit,
    onLongPress: (Note) -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var isDeleted by remember { mutableStateOf(false) }
    val deleteThreshold = 150f // Absolute threshold for right-swipe deletion
    
    // State for dropdown menu
    var showDropdownMenu by remember { mutableStateOf(false) }
    var pressPosition by remember { mutableStateOf(Offset.Zero) }
    
    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            delay(300) // Animation delay
            onDelete(note)
        }
    }
    
    AnimatedVisibility(
        visible = !isDeleted,
        exit = shrinkHorizontally(animationSpec = tween(durationMillis = 300)) +
                fadeOut(animationSpec = tween(durationMillis = 300))
    ) {
        Box {
            // Delete background - right swipe (shows on left)
            if (offsetX > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Red)
                        .padding(start = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }
            }
            
            // Note item with swipe gesture
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                    .combinedClickable(
                        onClick = { onNoteClick(note) },
                        onLongClick = {
                            showDropdownMenu = true
                        }
                    )
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            // Only allow swiping to the right for deletion visual
                            val newOffset = offsetX + delta
                            offsetX = if (newOffset < 0f) 0f else newOffset
                        },
                        onDragStopped = {
                            // Trigger delete only when swiped to the right beyond threshold
                            isDeleted = offsetX > deleteThreshold
                            if (!isDeleted) offsetX = 0f
                        }
                    )
            ) {
                NoteItem(note = note, onNoteClick = onNoteClick)
                
                // Dropdown menu for long press
                DropdownMenu(
                    expanded = showDropdownMenu,
                    onDismissRequest = { showDropdownMenu = false },
                    properties = PopupProperties(focusable = true)
                ) {
                    DropdownMenuItem(
                        text = { Text("Update Note") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Update") },
                        onClick = {
                            showDropdownMenu = false
                            onLongPress(note) // Navigate to edit screen
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete Note") },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = "Delete") },
                        onClick = {
                            showDropdownMenu = false
                            isDeleted = true // Trigger delete animation
                        }
                    )
                }
            }
        }
    }
}