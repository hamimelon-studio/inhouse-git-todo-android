package com.mikeapp.newideatodoapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.mikeapp.newideatodoapp.ui.theme.NewIdeaTodoAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DebugActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val receivedString = intent.getStringExtra("extra_long_string")
        Log.d("bbbb", "DebugActivity: Intent extras: ${intent.extras}")
        Log.d("bbbb", "receivedString: $receivedString")

//        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
//        val longMessage = sharedPreferences.getString("extra_long_string", "No message received")
//        val editor = sharedPreferences.edit()
//        editor.remove("extra_long_string")
//        editor.apply()

        setContent {
            NewIdeaTodoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        Text(
                            text = "Debug message:"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Scrollable container to handle large texts
                            SelectableLongTextBox(
                                text = receivedString ?: "No message.",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectableLongTextBox(
    text: String,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    var isLongPressed by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Select All button
            Button(
                onClick = {
                    scope.launch {
                        // Select and copy text
                        clipboardManager.setText(AnnotatedString(text))
                        // Request focus to enable selection
                        focusRequester.requestFocus()
                        // Small delay to ensure focus is set
                        delay(100)
                        // You might need to implement a custom selection handler
                        // depending on your specific needs
                    }
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 8.dp)
            ) {
                Text("Select All")
            }

            SelectionContainer {
                Text(
                    text = text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .focusRequester(focusRequester)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    isLongPressed = true
                                    clipboardManager.setText(AnnotatedString(text))
                                }
                            )
                        }
                )
            }
        }
    }
}

@Composable
fun SelectableLongTextBoxWithState(
    text: String,
    modifier: Modifier = Modifier
) {
    var isSelected by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        isSelected = !isSelected
                        if (isSelected) {
                            clipboardManager.setText(AnnotatedString(text))
                            scope.launch {
                                focusRequester.requestFocus()
                                delay(100)
                            }
                        }
                    }
                ) {
                    Text(if (isSelected) "Deselect" else "Select All")
                }

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(text))
                    }
                ) {
                    Text("Copy")
                }
            }

            SelectionContainer {
                Text(
                    text = text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .focusRequester(focusRequester)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    isSelected = true
                                    clipboardManager.setText(AnnotatedString(text))
                                }
                            )
                        }
                )
            }
        }
    }
}