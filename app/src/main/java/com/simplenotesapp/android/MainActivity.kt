package com.simplenotesapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simplenotesapp.android.ui.theme.SimpleNotesAppTheme
import java.util.UUID


data class Note(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var content: String,
    )

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val list = remember {
                mutableStateListOf(Note(title = "PRESS ME!!!", content = "Hi welcome to the SimpleNotesApp it's as simple as it seems you can create notes, delete or change notes."))
            }
            val appState: MutableState<String> = remember {
                mutableStateOf("Main")
            }
            val noteToEdit = remember {
                mutableStateOf(Note(title = "", content = ""))
            }
            SimpleNotesAppTheme {
                // A surface container using the 'background' color from the theme
                NoteApp(list, appState, noteToEdit)
            }

        }
        }
    }
@Composable
fun NoteApp(list: MutableList<Note>, appState: MutableState<String>, noteToEdit: MutableState<Note>) {
    // ... (Your existing code for conditional rendering based on appState) ...
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (appState.value) {
        "Main" -> MainScreen(list = list, appState = appState, noteToEdit = noteToEdit)
        "Add" -> {
            BackHandler { appState.value = "Main" }
            TextInputView(list = list, appState = appState)
        }
        "Edit" -> {
            BackHandler { appState.value = "Main" }
            TextEditView(noteToEdit.value, appState = appState, list = list)
        }
        else -> { /* Handle unknown states or default behavior */ }
    }
    }
}
@Composable
fun MainScreen(list: MutableList<Note>, modifier: Modifier = Modifier, appState: MutableState<String>, noteToEdit: MutableState<Note>) {
    Box(modifier = modifier.fillMaxSize()) { // Use Box for positioning
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            //TextInputView(list = list)
            ListView(list = list, appState = appState, noteToEdit = noteToEdit)
        }
        Button(
            onClick = { appState.value = "Add" },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth() // Stretch across the width
                .padding(8.dp)
        ) {
            Text("Create")
        }
    }
}
@Composable
fun TextInputView(list: MutableList<Note>, appState: MutableState<String>) {
    var titleText by rememberSaveable { mutableStateOf("") }
    var contentText by rememberSaveable { mutableStateOf("") }
    var isTitleError by remember { mutableStateOf(true) } // Initially true
    var isContentError by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = titleText,
            onValueChange = {
                titleText = it.take(50) // Limit to 50 characters
                isTitleError = it.isBlank() // Check for empty title
            },
            label = { Text("Title") },
            isError = isTitleError, // Show error state if title is empty
            modifier = Modifier.fillMaxWidth()
        )
        if (isTitleError) {
            Text("Title cannot be empty", color = Color.Red, fontSize = 12.sp)
        }

        OutlinedTextField(
            value = contentText,
            onValueChange = {
                contentText = it.take(250) // Limit to 250 characters
                isContentError = it.isBlank() // Check for empty content
            },
            label = { Text("Content") },
            isError = isContentError, // Show error state if content is empty
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        if (isContentError) {
            Text("Content cannot be empty", color = Color.Red, fontSize = 12.sp)
        }

        Button(
            onClick = {
                if (!isTitleError && !isContentError) { // Check if both fields are valid
                    list.add(Note(title = titleText, content = contentText))
                    titleText = ""
                    contentText = ""
                    appState.value = "Main"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isTitleError && !isContentError // Disable button if either field is invalid
        ) {
            Text("Add")
        }
    }
}
@Composable
fun ListView (list: List<Note>, appState: MutableState<String>, noteToEdit: MutableState<Note>) {
    LazyColumn {
        items(list) { note ->
            RowView(note, appState, noteToEdit)
        }
    }
}
@Composable
fun TextEditView(note: Note, appState: MutableState<String>, list: MutableList<Note>) { // Add list parameter
    var titleText by rememberSaveable { mutableStateOf(note.title) }
    var contentText by rememberSaveable { mutableStateOf(note.content) }
    var isTitleError by remember { mutableStateOf(note.title.isBlank()) }
    var isContentError by remember { mutableStateOf(note.content.isBlank()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = titleText,
            onValueChange = {
                titleText = it.take(50) // Limit to 50 characters
                isTitleError = it.isBlank() // Check for empty title
            },
            label = { Text("Title") },
            isError = isTitleError, // Show error state if title is empty
            modifier = Modifier.fillMaxWidth()
        )
        if (isTitleError) {
            Text("Title cannot be empty", color = Color.Red, fontSize = 12.sp)
        }

        OutlinedTextField(
            value = contentText,
            onValueChange = {
                contentText = it.take(250) // Limit to 250 characters
                isContentError = it.isBlank() // Check for empty content
            },
            label = { Text("Content") },
            isError = isContentError, // Show error state if content is empty
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        if (isContentError) {
            Text("Content cannot be empty", color = Color.Red, fontSize = 12.sp)
        }

        Button( // Delete button
            onClick = {
                list.remove(note) // Remove the note from the list
                appState.value = "Main" // Navigate back to Main screen
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red) // Red color for delete button
        ) {
            Text("Delete", color = Color.White)
        }

        Button( // Save button
            onClick = {
                if (!isTitleError && !isContentError) {
                    note.title = titleText
                    note.content = contentText
                    appState.value = "Main"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isTitleError && !isContentError
        ) {
            Text("Save")
        }
    }
}
@Composable
fun RowView(note: Note, appState: MutableState<String>, noteToEdit: MutableState<Note>) {
    val isDarkTheme = isSystemInDarkTheme()
    Button(
        onClick = {
            noteToEdit.value = note
            appState.value = "Edit"
        }, // Pass the onClick lambda here
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                1.dp,
                Color.Gray,
                shape = RoundedCornerShape(4.dp)
            ),
        colors = ButtonDefaults.buttonColors( // Optional: Customize button colors
            containerColor = Color.Transparent,
            contentColor = if (isDarkTheme) Color.White else Color.Black
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(text = note.title)
                //Text(text = note.content)
            }
        }
    }
}