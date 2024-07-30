package com.example.tsumiageapp.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tsumiageapp.R
import com.example.tsumiageapp.ui.common.components.BottomNavigationBar
import com.example.tsumiageapp.ui.common.enums.NaviItemEnum
import com.example.tsumiageapp.ui.goal.AddGoalScreen
import com.example.tsumiageapp.ui.goal.GoalListScreen
import com.example.tsumiageapp.ui.home.HomeScreen
import com.example.tsumiageapp.ui.report.ReportScreen
import com.example.tsumiageapp.ui.task.AddTaskScreen
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TsumiageApp(
    windowSize: WindowWidthSizeClass
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val drawerWidth = when(windowSize) {
        WindowWidthSizeClass.Compact -> 0.65F
        WindowWidthSizeClass.Medium -> 0.4F
        WindowWidthSizeClass.Expanded -> 0.3F
        else -> 0.5F
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth(drawerWidth)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 12.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.app_icon),
                    contentDescription = "ツミアゲ",
                    alignment = Alignment.Center,
                )
                Spacer(modifier = Modifier.height(12.dp))
                NaviItemEnum.entries.forEach { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1F)
                            .height(60.dp)
                            .padding(start = 24.dp)
                            .clickable {
                                navController.navigate(item.id)
                                coroutineScope.launch { drawerState.close() }
                            },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ){
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                item.label,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    Divider(modifier = Modifier.height(1.dp))
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    navController = navController,
                    onMenuClicked = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    },
                    currentRoute = currentRoute
                )
            },
            floatingActionButton = {
                if (currentRoute == "home") {
                    FloatingActionButton(
                        onClick = { navController.navigate("addTask") },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "Add Task"
                        )
                    }
                } else if (currentRoute == "goal") {
                    FloatingActionButton(
                        onClick = { navController.navigate("addGoal") },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "Add Goal"
                        )
                    }
                }
            },
            bottomBar = {
                BottomNavigationBar(currentRoute, navController)
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                composable("addTask") {
                    AddTaskScreen(
                        navController = navController,
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                composable("report") {
                    ReportScreen(
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                composable("goal") {
                    GoalListScreen(
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                composable("addGoal") {
                    AddGoalScreen(
                        navController = navController,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    navController: NavController,
    onMenuClicked: () -> Unit,
    currentRoute: String?
) {
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(R.drawable.app_icon),
                contentDescription = "アイコン",
                modifier = Modifier.size(50.dp)
            )
        },
        navigationIcon = {
            if (currentRoute == "addTask" || currentRoute == "addGoal") {
                IconButton(
                    onClick = { navController.popBackStack() },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "戻る"
                    )
                }
            } else {
                IconButton(
                    onClick = onMenuClicked,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "メニュー"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
        modifier = if(currentRoute == "report" || currentRoute == "goal") Modifier else if(currentRoute == "addTask") Modifier else Modifier.graphicsLayer {
            shadowElevation = 4.dp.toPx()
        }
    )
}