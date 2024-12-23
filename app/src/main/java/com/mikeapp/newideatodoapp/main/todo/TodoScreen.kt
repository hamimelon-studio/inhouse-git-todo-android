package com.mikeapp.newideatodoapp.main.todo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mikeapp.newideatodoapp.R
import com.mikeapp.newideatodoapp.main.todo.component.DrawerMenu
import com.mikeapp.newideatodoapp.main.todo.component.TodoItemCardWithSwipe
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(navController: NavController, paddingValues: PaddingValues) {
    val viewModel: TodoViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerMenu(navController)
            }
        }
    ) {
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = {
                viewModel.forceRefresh()
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_menu_24),
                                    contentDescription = "Menu",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        },
                        title = { Text(uiState.currentList?.name ?: "") },
                        actions = {
                            IconButton(onClick = { /* Edit action */ }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        navController.navigate("add")
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Task")
                    }
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    items(uiState.tasks, key = { it.id }) { todo ->
                        TodoItemCardWithSwipe(
                            navController = navController,
                            todo = todo,
                            onComplete = {
                                viewModel.deleteTask(todo)
                            },
                            onDismiss = {})
                    }
                }
            }
        }
    }

    BackHandler(onBack = {
        when {
            drawerState.isClosed -> {
                scope.launch {
                    drawerState.open()
                }
            }

            else -> {
                navController.popBackStack()
            }
        }
    })
}