package com.ragul.notetaking.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val createdDate: Date,
    val modifiedDate: Date,
    val isArchived: Boolean = false,
    val isPinned: Boolean = false,
    val color: Int? = null
)