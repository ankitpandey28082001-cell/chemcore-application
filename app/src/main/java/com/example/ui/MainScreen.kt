package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AuthRepository
import com.example.data.repository.AuthState
import com.example.ui.components.TimeCalibrationDialog
import com.example.ui.components.UserProfileSheet
import com.example.ui.screens.*
import com.example.util.rememberCurrentIstTime

enum class ChemNavDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    PERIODIC_TABLE("Elements", Icons.Filled.GridView, Icons.Outlined.GridView),
    BALANCER("Reactions", Icons.Filled.Balance, Icons.Outlined.Balance),
    VIRTUAL_LAB("Lab Bench", Icons.Filled.Science, Icons.Outlined.Science),
    LIVE_VOICE("Live Voice", Icons.Filled.GraphicEq, Icons.Outlined.GraphicEq),
    MUSIC_STUDIO("Study Beats", Icons.Filled.MusicNote, Icons.Outlined.MusicNote)
}

enum class ExtraScreen {
    NONE,
    SOLUTIONS,
    LEARN_QUIZ,
    LEARN_REF
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    authRepository: AuthRepository = AuthRepository.getInstance(LocalContext.current)
) {
    val authState by authRepository.authState.collectAsState()

    var currentDestination by remember { mutableStateOf(ChemNavDestination.PERIODIC_TABLE) }
    var extraScreen by remember { mutableStateOf(ExtraScreen.NONE) }
    var selectedReactionSubTab by remember { mutableIntStateOf(0) } // 0: Balancer, 1: Molar Mass
    var balancerInitialEquation by remember { mutableStateOf("") }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showProfileSheet by remember { mutableStateOf(false) }
    var showAuthScreenExplicitly by remember { mutableStateOf(false) }
    var showTimeCalibrationDialog by remember { mutableStateOf(false) }

    val currentIstTime by rememberCurrentIstTime()

    // If user is unauthenticated or explicitly opens login, show AuthScreen
    if (authState is AuthState.Unauthenticated || showAuthScreenExplicitly) {
        AuthScreen(
            authRepository = authRepository,
            onAuthSuccess = {
                showAuthScreenExplicitly = false
            }
        )
        return
    }

    // Custom backstack handling
    BackHandler(enabled = extraScreen != ExtraScreen.NONE || currentDestination != ChemNavDestination.PERIODIC_TABLE) {
        if (extraScreen != ExtraScreen.NONE) {
            extraScreen = ExtraScreen.NONE
        } else {
            currentDestination = ChemNavDestination.PERIODIC_TABLE
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(34.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Science,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "ChemMaster",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            // Live Indian Standard Time (IST) Clock - clickable to calibrate & sync
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { showTimeCalibrationDialog = true }
                                    .testTag("top_bar_ist_clock_btn"),
                                color = Color.Transparent
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 1.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "IST $currentIstTime",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Icon(
                                        Icons.Default.Sync,
                                        contentDescription = "Calibrate IST Clock",
                                        modifier = Modifier.size(10.dp),
                                        tint = Color(0xFF10B981)
                                    )
                                }
                            }
                        }
                    }
                },
                actions = {
                    // User Profile / Login Avatar Chip
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clickable { showProfileSheet = true }
                            .testTag("top_bar_user_profile_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            when (val state = authState) {
                                is AuthState.Authenticated -> {
                                    val user = state.user
                                    val initials = user.fullName
                                        .split(" ")
                                        .mapNotNull { it.firstOrNull()?.toString() }
                                        .take(2)
                                        .joinToString("")
                                        .uppercase()

                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (initials.isNotEmpty()) initials else "U",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                    Text(
                                        text = user.fullName.split(" ").firstOrNull() ?: "Chemist",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                                is AuthState.Guest -> {
                                    Icon(
                                        Icons.Default.AccountCircle,
                                        contentDescription = "Guest",
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Guest",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                else -> {
                                    Text("Sign In", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Quick button for Solutions & pH
                    IconButton(
                        onClick = { extraScreen = ExtraScreen.SOLUTIONS },
                        modifier = Modifier.testTag("top_app_solutions_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WaterDrop,
                            contentDescription = "Solutions & pH",
                            tint = if (extraScreen == ExtraScreen.SOLUTIONS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Quick button for Quiz & Reference
                    IconButton(
                        onClick = { extraScreen = ExtraScreen.LEARN_QUIZ },
                        modifier = Modifier.testTag("top_app_quiz_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.School,
                            contentDescription = "Quiz & Reference",
                            tint = if (extraScreen == ExtraScreen.LEARN_QUIZ || extraScreen == ExtraScreen.LEARN_REF) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box {
                        IconButton(onClick = { showMoreMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More Tools")
                        }
                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("My Profile & Stats") },
                                onClick = {
                                    showProfileSheet = true
                                    showMoreMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Solutions & pH Lab") },
                                onClick = {
                                    extraScreen = ExtraScreen.SOLUTIONS
                                    showMoreMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.WaterDrop, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Chemistry Quiz") },
                                onClick = {
                                    extraScreen = ExtraScreen.LEARN_QUIZ
                                    showMoreMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.Quiz, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Solubility & Reference Tables") },
                                onClick = {
                                    extraScreen = ExtraScreen.LEARN_REF
                                    showMoreMenu = false
                                },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("IST Clock Sync & Calibration") },
                                onClick = {
                                    showMoreMenu = false
                                    showTimeCalibrationDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF10B981)) }
                            )
                            HorizontalDivider()
                            if (authState is AuthState.Authenticated) {
                                DropdownMenuItem(
                                    text = { Text("Log Out") },
                                    onClick = {
                                        authRepository.logout()
                                        showMoreMenu = false
                                        showAuthScreenExplicitly = true
                                    },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text("Sign In / Register") },
                                    onClick = {
                                        showMoreMenu = false
                                        showAuthScreenExplicitly = true
                                    },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                ChemNavDestination.entries.forEach { destination ->
                    val isSelected = extraScreen == ExtraScreen.NONE && currentDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            extraScreen = ExtraScreen.NONE
                            currentDestination = destination
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = destination.title
                            )
                        },
                        label = {
                            Text(
                                text = destination.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        },
                        modifier = Modifier.testTag("nav_item_${destination.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                extraScreen == ExtraScreen.SOLUTIONS -> {
                    SolutionsLabScreen()
                }

                extraScreen == ExtraScreen.LEARN_QUIZ -> {
                    QuizScreen()
                }

                extraScreen == ExtraScreen.LEARN_REF -> {
                    ReferenceScreen()
                }

                currentDestination == ChemNavDestination.PERIODIC_TABLE -> {
                    PeriodicTableScreen(
                        onNavigateToBalancer = { symbol ->
                            balancerInitialEquation = symbol
                            currentDestination = ChemNavDestination.BALANCER
                            selectedReactionSubTab = 0
                        }
                    )
                }

                currentDestination == ChemNavDestination.BALANCER -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            SegmentedButton(
                                selected = selectedReactionSubTab == 0,
                                onClick = { selectedReactionSubTab = 0 },
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                            ) {
                                Text("Equation Balancer")
                            }
                            SegmentedButton(
                                selected = selectedReactionSubTab == 1,
                                onClick = { selectedReactionSubTab = 1 },
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                            ) {
                                Text("Molar Mass & Moles")
                            }
                        }

                        if (selectedReactionSubTab == 0) {
                            EquationBalancerScreen(initialEquation = balancerInitialEquation)
                        } else {
                            MolarMassScreen()
                        }
                    }
                }

                currentDestination == ChemNavDestination.VIRTUAL_LAB -> {
                    ReactionLabScreen()
                }

                currentDestination == ChemNavDestination.LIVE_VOICE -> {
                    VoiceConversationScreen()
                }

                currentDestination == ChemNavDestination.MUSIC_STUDIO -> {
                    MusicGeneratorScreen()
                }
            }
        }
    }

    if (showProfileSheet) {
        UserProfileSheet(
            authRepository = authRepository,
            onDismiss = { showProfileSheet = false },
            onNavigateToAuth = {
                showProfileSheet = false
                showAuthScreenExplicitly = true
            }
        )
    }

    if (showTimeCalibrationDialog) {
        TimeCalibrationDialog(
            onDismiss = { showTimeCalibrationDialog = false }
        )
    }
}
