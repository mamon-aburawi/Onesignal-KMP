package com.mamon.onesignalkmp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import onesignal.core.network.OneSignalClient
import onesignal.feature_push.domain.model.OneSignalButton
import onesignal.feature_scheduler.domain.OneSignalScheduler
import onesignal.feature_push.domain.model.OneSignalNotification
import onesignalkmp.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import onesignalkmp.composeapp.generated.resources.ic_calendar

enum class TargetType { ALL, SINGLE, LIST }

data class ReceivedNotification(val title: String, val body: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun App() {
    MaterialTheme {
        val scope = rememberCoroutineScope()



        // --- State Variables ---
        var mySubscriptionId by remember { mutableStateOf("") }
        var myOneSignalId by remember { mutableStateOf("") }
        var notificationTitle by remember { mutableStateOf("") }
        var notificationBody by remember { mutableStateOf("") }

        var targetType by remember { mutableStateOf(TargetType.ALL) }

        var singleTargetId by remember { mutableStateOf("") }
        var listTargetIds by remember { mutableStateOf("") }

        // --- User Management State ---
        var loginExternalId by remember { mutableStateOf("") }
        var emailAddress by remember { mutableStateOf("") }

        val receivedNotifications = remember { mutableStateListOf<ReceivedNotification>() }

        // --- Scheduling State ---
        var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
        var selectedHour by remember { mutableStateOf<Int?>(null) }
        var selectedMinute by remember { mutableStateOf<Int?>(null) }

        var showDatePicker by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }

        val datePickerState = rememberDatePickerState()
        val timePickerState = rememberTimePickerState()

        val snakeBarState = remember { SnackbarHostState() }

        val oneSignalClient = remember {
            OneSignalClient(
                appId = Constant.ONE_SIGNAL_APP_ID,
                apiKey = Constant.ONE_SIGNAL_API_KEY
            )
        }

        LaunchedEffect(Unit) {
            oneSignalClient.setNotificationClickListener { title, body ->
                receivedNotifications.add(0, ReceivedNotification(title, body))
            }
        }

        // ==========================================
        // DATE & TIME PICKER DIALOGS
        // ==========================================
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = { TextButton(onClick = { showDatePicker = false; showTimePicker = true }) { Text("Next") } },
                dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
            ) { DatePicker(state = datePickerState) }
        }

        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                title = { Text("Select Time") },
                text = { TimePicker(state = timePickerState) },
                confirmButton = {
                    TextButton(onClick = {
                        selectedDateMillis = datePickerState.selectedDateMillis
                        selectedHour = timePickerState.hour
                        selectedMinute = timePickerState.minute
                        showTimePicker = false
                    }) { Text("Confirm") }
                },
                dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancel") } }
            )
        }

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snakeBarState) },
            topBar = {
                TopAppBar(
                    title = { Text("OneSignal Push Dashboard") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ==========================================
                // SECTION 1: DEVICE INFO & PERMISSIONS
                // ==========================================
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.elevatedCardElevation(4.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "1. Device & Permissions", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Subscription ID:\n${mySubscriptionId.ifEmpty { "Not retrieved yet" }}", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                            Button(onClick = { scope.launch { mySubscriptionId = oneSignalClient.getSubscriptionId() ?: "No ID found" } }, ) { Text("Get") }

                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "OneSignal ID:\n${myOneSignalId.ifEmpty { "Not retrieved yet" }}", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                            Button(onClick = { scope.launch { myOneSignalId = oneSignalClient.getOneSignalId() ?: "No ID found" } }, modifier = Modifier) { Text("Get") }
                        }
                        OutlinedButton(onClick = { scope.launch { oneSignalClient.requestPermission() } }, modifier = Modifier) { Text("Ask Permission") }
                    }
                }

                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.elevatedCardElevation(4.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = "2. User Management", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                        // Login / Logout
                        OutlinedTextField(value = loginExternalId, onValueChange = { loginExternalId = it }, label = { Text("External User ID") }, placeholder = { Text("user_12345") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { scope.launch { oneSignalClient.login(loginExternalId); snakeBarState.showSnackbar("Logged in as $loginExternalId") } }, modifier = Modifier.weight(1f), enabled = loginExternalId.isNotBlank()) { Text("Login") }
                            OutlinedButton(onClick = { scope.launch { oneSignalClient.logout(); loginExternalId = ""; snakeBarState.showSnackbar("Logged out") } }, modifier = Modifier.weight(1f)) { Text("Logout") }
                        }

                        HorizontalDivider()

                        // Add / Remove Email
                        OutlinedTextField(value = emailAddress, onValueChange = { emailAddress = it }, label = { Text("Email Address") }, placeholder = { Text("user@example.com") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { scope.launch { oneSignalClient.addEmail(emailAddress); snakeBarState.showSnackbar("Email added: $emailAddress") } }, modifier = Modifier.weight(1f), enabled = emailAddress.isNotBlank()) { Text("Add Email") }
                            OutlinedButton(onClick = { scope.launch { oneSignalClient.removeEmail(emailAddress); snakeBarState.showSnackbar("Email removed") } }, modifier = Modifier.weight(1f), enabled = emailAddress.isNotBlank(), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Remove") }
                        }
                    }
                }

                // ==========================================
                // SECTION 3: MESSAGE CONTENT
                // ==========================================
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.elevatedCardElevation(4.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "3. Message Content", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                        OutlinedTextField(value = notificationTitle, onValueChange = { notificationTitle = it }, label = { Text("Notification Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(value = notificationBody, onValueChange = { notificationBody = it }, label = { Text("Notification Body") }, modifier = Modifier.fillMaxWidth(), maxLines = 5)
                    }
                }

                // ==========================================
                // SECTION 4: TARGETING & SCHEDULING
                // ==========================================
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.elevatedCardElevation(4.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(text = "4. Target & Schedule", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                        // Target Selection
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) { RadioButton(selected = targetType == TargetType.ALL, onClick = { targetType = TargetType.ALL }); Text("All") }
                            Row(verticalAlignment = Alignment.CenterVertically) { RadioButton(selected = targetType == TargetType.SINGLE, onClick = { targetType = TargetType.SINGLE }); Text("Single") }
                            Row(verticalAlignment = Alignment.CenterVertically) { RadioButton(selected = targetType == TargetType.LIST, onClick = { targetType = TargetType.LIST }); Text("List") }
                        }

                        when (targetType) {
                            TargetType.ALL -> Text("Broadcasts to all users.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                            TargetType.SINGLE -> OutlinedTextField(value = singleTargetId, onValueChange = { singleTargetId = it }, label = { Text("External ID") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                            TargetType.LIST -> OutlinedTextField(value = listTargetIds, onValueChange = { listTargetIds = it }, label = { Text("User IDs (comma separated)") }, modifier = Modifier.fillMaxWidth())
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Schedule Selection
                        if (selectedDateMillis == null) {
                            OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                                Icon(modifier = Modifier.size(20.dp), painter = painterResource(Res.drawable.ic_calendar), contentDescription = "Pick Date")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Schedule for Later")
                            }
                        } else {
                            val amPm = if (selectedHour!! >= 12) "PM" else "AM"
                            val displayHour = if (selectedHour!! == 0) 12 else if (selectedHour!! > 12) selectedHour!! - 12 else selectedHour!!
                            val minStr = selectedMinute!!.toString().padStart(2, '0')

                            Text(text = "Scheduled Time: $displayHour:$minStr $amPm", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.weight(1f)) { Text("Edit") }
                                OutlinedButton(onClick = { selectedDateMillis = null; selectedHour = null; selectedMinute = null }, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Clear") }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION 5: SEND ACTION
                // ==========================================
                Button(
                    onClick = {
                        scope.launch {
                            val scheduleConfig = if (selectedDateMillis != null && selectedHour != null && selectedMinute != null) {
                                OneSignalScheduler.scheduleAt(selectedDateMillis!!, selectedHour!!, selectedMinute!!)
                            } else null

                            try {
                                val pushNote = OneSignalNotification(
                                    title = notificationTitle,
                                    message = notificationBody,
                                    largeIcon = "https://placehold.co/256x256/007BFF/FFFFFF/png?text=TEST",
                                    largeImage = "https://cdn.pixabay.com/photo/2018/01/21/01/46/architecture-3095716_960_720.jpg",
                                    smallIcon = "ic_notification",
                                    actionUrl = "https://github.com/mamon-aburawi",
                                    buttons = listOf(
                                        OneSignalButton(
                                            id = "google",
                                            text = "✅ Google",
                                            url = "https://www.google.com"
                                        ),
                                        OneSignalButton(
                                            id = "youtube",
                                            text = "❌ Youtube",
                                            url = "https://www.youtube.com"
                                        )
                                    ),
                                    scheduledTime = scheduleConfig
                                )

                                val result = when (targetType) {
                                    TargetType.ALL -> oneSignalClient.sendNotificationToAll(params = pushNote)
                                    TargetType.SINGLE -> oneSignalClient.sendNotification(externalId = singleTargetId, params = pushNote)
                                    TargetType.LIST -> oneSignalClient.sendNotification(externalIds = listTargetIds.split(",").map{it.trim()}, params = pushNote)
                                }

                                if (result.isSuccess) {
                                    snakeBarState.showSnackbar("Success: Push Notification Sent")
                                    // RESET SCHEDULER
                                    selectedDateMillis = null
                                    selectedHour = null
                                    selectedMinute = null
                                } else {
                                    snakeBarState.showSnackbar("Failed to send Push Notification")
                                }

                            } catch (e: Exception) {
                                snakeBarState.showSnackbar("Error: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = notificationTitle.isNotBlank() && notificationBody.isNotBlank() && when (targetType) {
                        TargetType.ALL -> true
                        TargetType.SINGLE -> singleTargetId.isNotBlank()
                        TargetType.LIST -> listTargetIds.isNotBlank()
                    }
                ) {
                    Text(if (selectedDateMillis != null) "Schedule Notification" else "Send Notification", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}


//package com.mamon.onesignalkmp
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.launch
//import onesignal.OneSignalClient
//import onesignal.OneSignalScheduler
//import onesignal.model.OneSignalNotification
//import onesignal.model.OneSignalButton
//import onesignalkmp.composeapp.generated.resources.Res
//import org.jetbrains.compose.resources.painterResource
//import onesignalkmp.composeapp.generated.resources.ic_calendar
//
//// Enum to track what kind of notification we are sending
//enum class TargetType {
//    ALL, SINGLE, LIST
//}
//
//data class ReceivedNotification(
//    val title: String,
//    val body: String
//)
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//@Preview(showBackground = true)
//fun App() {
//    MaterialTheme {
//        val scope = rememberCoroutineScope()
//
//        // --- State Variables ---
//        var mySubscriptionId by remember { mutableStateOf("") }
//        var notificationTitle by remember { mutableStateOf("Test Title") }
//        var notificationBody by remember { mutableStateOf("Test Body") }
//
//        var targetType by remember { mutableStateOf(TargetType.ALL) }
//        var singleTargetId by remember { mutableStateOf("") }
//        var listTargetIds by remember { mutableStateOf("") }
//        var newExternalId by remember { mutableStateOf("") }
//
//        val receivedNotifications = remember { mutableStateListOf<ReceivedNotification>() }
//
//        // --- Scheduling State ---
//        var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
//        var selectedHour by remember { mutableStateOf<Int?>(null) }
//        var selectedMinute by remember { mutableStateOf<Int?>(null) }
//
//        var showDatePicker by remember { mutableStateOf(false) }
//        var showTimePicker by remember { mutableStateOf(false) }
//
//        val datePickerState = rememberDatePickerState()
//        val timePickerState = rememberTimePickerState()
//
//        val snakeBarState = remember { SnackbarHostState() }
//
//        val oneSignalClient = remember {
//            OneSignalClient(
//                appId = Constant.ONE_SIGNAL_APP_ID,
//                apiKey = Constant.ONE_SIGNAL_API_KEY
//            )
//        }
////        val userRepository = oneSignalClient.userOneSignalRepository
////        val pushNotificationRepository = oneSignalClient.pushNotificationOneSignalRepository
////        val pushEmailRepository = oneSignalClient.pushEmailOneSignalRepository
//
//
//        LaunchedEffect(Unit) {
//            oneSignalClient.setNotificationClickListener { title, body ->
//                receivedNotifications.add(0, ReceivedNotification(title, body))
//            }
//        }
//
//        // ==========================================
//        // DATE & TIME PICKER DIALOGS
//        // ==========================================
//        if (showDatePicker) {
//            DatePickerDialog(
//                onDismissRequest = { showDatePicker = false },
//                confirmButton = {
//                    TextButton(onClick = {
//                        showDatePicker = false
//                        showTimePicker = true
//                    }) {
//                        Text("Next")
//                    }
//                },
//                dismissButton = {
//                    TextButton(onClick = { showDatePicker = false }) {
//                        Text("Cancel")
//                    }
//                }
//            ) {
//                DatePicker(state = datePickerState)
//            }
//        }
//
//        if (showTimePicker) {
//            AlertDialog(
//                onDismissRequest = { showTimePicker = false },
//                title = { Text("Select Time") },
//                text = { TimePicker(state = timePickerState) },
//                confirmButton = {
//                    TextButton(onClick = {
//                        selectedDateMillis = datePickerState.selectedDateMillis
//                        selectedHour = timePickerState.hour
//                        selectedMinute = timePickerState.minute
//                        showTimePicker = false
//                    }) {
//                        Text("Confirm")
//                    }
//                },
//                dismissButton = {
//                    TextButton(onClick = { showTimePicker = false }) {
//                        Text("Cancel")
//                    }
//                }
//            )
//        }
//
//
//
//
//        // 2. Add the Scaffold and assign the snackbarHost
//
//
//
//        Scaffold(
//            snackbarHost = {
//                SnackbarHost(hostState = snakeBarState)
//            },
//            topBar = {
//                TopAppBar(
//                    title = { Text("OneSignal Push Dashboard") },
//                    colors = TopAppBarDefaults.topAppBarColors(
//                        containerColor = MaterialTheme.colorScheme.primary,
//                        titleContentColor = MaterialTheme.colorScheme.onPrimary
//                    )
//                )
//            }
//        ) { paddingValues ->
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues)
//                    .padding(16.dp)
//                    .verticalScroll(rememberScrollState()),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//
//                // ==========================================
//                // SECTION 1: DEVICE INFO & PERMISSIONS
//                // ==========================================
//                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.elevatedCardElevation(4.dp)) {
//                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                        Text(text = "Device Info & Setup", style = MaterialTheme.typography.titleMedium)
//                        Text(text = "My Subscription ID:\n${mySubscriptionId.ifEmpty { "Not retrieved yet" }}", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
//                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                            OutlinedButton(onClick = { scope.launch { oneSignalClient.requestPermission() } }, modifier = Modifier.weight(1f)) { Text("Ask Permission") }
//                            Button(onClick = { scope.launch { mySubscriptionId = oneSignalClient.getUserSubscriptionId() ?: "" } }, modifier = Modifier.weight(1f)) { Text("Get ID") }
//                        }
//                    }
//                }
//
//                // ==========================================
//                // SECTION 2: NOTIFICATION PAYLOAD
//                // ==========================================
//                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.elevatedCardElevation(4.dp)) {
//                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                        Text(text = "Message Content", style = MaterialTheme.typography.titleMedium)
//                        OutlinedTextField(value = notificationTitle, onValueChange = { notificationTitle = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
//                        OutlinedTextField(value = notificationBody, onValueChange = { notificationBody = it }, label = { Text("Body") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
//                    }
//                }
//
//                // ==========================================
//                // SECTION 3: SCHEDULING OPTIONS
//                // ==========================================
//                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.elevatedCardElevation(4.dp)) {
//                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                        Text(text = "Schedule Notification", style = MaterialTheme.typography.titleMedium)
//
//                        if (selectedDateMillis == null) {
//                            OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
//                                Icon(modifier = Modifier.size(24.dp), painter = painterResource(Res.drawable.ic_calendar), contentDescription = "Pick Date")
//                                Spacer(modifier = Modifier.width(8.dp))
//                                Text("Pick Date & Time")
//                            }
//                        } else {
//                            // Using the updated kotlinx-datetime formatter
//                            val formattedTime = ""
//
//                            Text(
//                                text = "Scheduled for: $formattedTime",
//                                style = MaterialTheme.typography.bodyLarge,
//                                color = MaterialTheme.colorScheme.primary,
//                                fontWeight = FontWeight.Bold
//                            )
//
//                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                                OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.weight(1f)) { Text("Edit Time") }
//                                OutlinedButton(
//                                    onClick = {
//                                        selectedDateMillis = null
//                                        selectedHour = null
//                                        selectedMinute = null
//                                    },
//                                    modifier = Modifier.weight(1f),
//                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
//                                ) { Text("Clear") }
//                            }
//                        }
//                    }
//                }
//
//                // ==========================================
//                // SECTION 4: SEND OPTIONS
//                // ==========================================
//                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.elevatedCardElevation(4.dp)) {
//                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                        Text(text = "Target Audience", style = MaterialTheme.typography.titleMedium)
//
//                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
//                            Row(verticalAlignment = Alignment.CenterVertically) { RadioButton(selected = targetType == TargetType.ALL, onClick = { targetType = TargetType.ALL }); Text("All") }
//                            Row(verticalAlignment = Alignment.CenterVertically) { RadioButton(selected = targetType == TargetType.SINGLE, onClick = { targetType = TargetType.SINGLE }); Text("Single") }
//                            Row(verticalAlignment = Alignment.CenterVertically) { RadioButton(selected = targetType == TargetType.LIST, onClick = { targetType = TargetType.LIST }); Text("List") }
//                        }
//
//                        when (targetType) {
//                            TargetType.ALL -> Text("This will broadcast to all subscribed users.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
//                            TargetType.SINGLE -> OutlinedTextField(value = singleTargetId, onValueChange = { singleTargetId = it }, label = { Text("External ID") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
//                            TargetType.LIST -> OutlinedTextField(value = listTargetIds, onValueChange = { listTargetIds = it }, label = { Text("Database User IDs (comma separated)") }, placeholder = { Text("user_1, user_2, user_3") }, modifier = Modifier.fillMaxWidth())
//                        }
//
//                        Button(
//                            onClick = {
//                                scope.launch {
//                                    // 1. Resolve the schedule configuration
//                                    val scheduleConfig = if (selectedDateMillis != null && selectedHour != null && selectedMinute != null) {
//                                        OneSignalScheduler.scheduleAt(
//                                            dateInMilliSeconds = selectedDateMillis!!,
//                                            localHour = selectedHour!!,
//                                            localMinute = selectedMinute!!
//                                        )
//                                    } else {
//                                        null
//                                    }
//
//                                    // 2. Bundle all common parameters into the new NotificationParams data class
//                                    val oneSignalNotification = OneSignalNotification(
//                                        title = notificationTitle,
//                                        message = notificationBody,
//                                        largeIcon = "https://placehold.co/256x256/007BFF/FFFFFF/png?text=TEST",
//                                        largeImage = "https://cdn.pixabay.com/photo/2018/01/21/01/46/architecture-3095716_960_720.jpg",
//                                        smallIcon = "ic_notification",
//                                        actionUrl = "https://github.com/mamon-aburawi",
//                                        buttons = listOf(
//                                            OneSignalButton(
//                                                id = "google",
//                                                text = "✅ Google",
//                                                url = "https://www.google.com"
//                                            ),
//                                            OneSignalButton(
//                                                id = "youtube",
//                                                text = "❌ Youtube",
//                                                url = "https://www.youtube.com"
//                                            )
//                                        ),
//                                        scheduledTime = scheduleConfig
//                                    )
//
//                                    // 3. Pass the unified params to the SDK and capture the Result
//                                    val result = when (targetType) {
//                                        TargetType.ALL -> {
//                                            oneSignalClient.sendNotificationToAll(params = oneSignalNotification)
//                                        }
//                                        TargetType.SINGLE -> {
//                                            oneSignalClient.sendNotification(
//                                                externalId = singleTargetId,
//                                                params = oneSignalNotification
//                                            )
//                                        }
//                                        TargetType.LIST -> {
//                                            val ids = listTargetIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }
//                                            oneSignalClient.sendNotification(
//                                                params = oneSignalNotification,
//                                                externalIds = ids
//                                            )
//
//                                        }
//                                    }
//
//                                    // 4. Handle Success and Failure explicitly with Snackbars
//                                    result.onSuccess { notificationId ->
//                                        // Reset scheduler after sending successfully
//                                        selectedDateMillis = null
//                                        selectedHour = null
//                                        selectedMinute = null
//
//                                        // Show Success Snackbar
//                                        snakeBarState.showSnackbar(
//                                            message = "Success! Notification id: $notificationId Sent",
//                                            actionLabel = "Dismiss"
//                                        )
//
//                                    }.onFailure { error ->
//                                        // Show Error Snackbar
//                                        snakeBarState.showSnackbar(
//                                            message = "Failed: $error",
//                                            actionLabel = "Dismiss"
//                                        )
//                                    }
//                                }
//                            },
//                            modifier = Modifier.fillMaxWidth(),
//                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
//                            enabled = when (targetType) {
//                                TargetType.ALL -> true
//                                TargetType.SINGLE -> singleTargetId.isNotBlank()
//                                TargetType.LIST -> listTargetIds.isNotBlank()
//                            }
//                        ) {
//                            Text(if (selectedDateMillis != null) "Schedule Notification" else "Send Now")
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//        }
//    }
//}
//
//
///**
// * Combines UTC Date + Local Time + Local Timezone into exact Unix Milliseconds
// */
//
