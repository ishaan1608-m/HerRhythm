package com.herrhythm.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserMemoryRepository {
    private val _memories = MutableStateFlow<List<MemoryItem>>(
        listOf(
            MemoryItem(
                category = "Routines",
                content = "Prefers 30-min moderate morning workouts around 8:00 AM.",
                dateAdded = "Yesterday"
            ),
            MemoryItem(
                category = "Preferences",
                content = "Likes Hinglish conversational tone and lighthearted reminders.",
                dateAdded = "2 days ago"
            ),
            MemoryItem(
                category = "Health Context",
                content = "Experiences slight fatigue during late luteal phase.",
                dateAdded = "3 days ago"
            ),
            MemoryItem(
                category = "Goals",
                content = "Wants to maintain 7.5+ hours sleep consistency and 8,000 steps daily.",
                dateAdded = "5 days ago"
            )
        )
    )
    val memories: StateFlow<List<MemoryItem>> = _memories.asStateFlow()

    fun addMemory(category: String, content: String) {
        val newItem = MemoryItem(category = category, content = content, dateAdded = "Just now")
        _memories.value = listOf(newItem) + _memories.value
    }

    fun removeMemory(id: String) {
        _memories.value = _memories.value.filter { it.id != id }
    }

    fun clearAllMemories() {
        _memories.value = emptyList()
    }
}
