package com.herrhythm.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herrhythm.app.data.NyraActionCard
import com.herrhythm.app.data.NyraMessage
import com.herrhythm.app.ui.theme.*

@Composable
fun NyraScreen(
    messages: List<NyraMessage>,
    isLoading: Boolean = false,
    onSendMessage: (String) -> Unit,
    onExecuteAction: (NyraActionCard) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PookieDarkBg)
    ) {
        // NYRA Header Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PookieCardBg)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(PookiePinkPrimary, PookieLavender))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌸", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("NYRA", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PookiePinkPrimary.copy(alpha = 0.25f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("AI Companion", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PookiePinkGlow)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(HealthGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isLoading) "Thinking..." else "Cycle & Health Context Synced",
                                fontSize = 11.sp,
                                color = if (isLoading) PookiePinkGlow else PookieTextMuted
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = PookieCardLight, thickness = 1.dp)

        // Chat Conversation List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            items(messages) { msg ->
                NyraMessageBubble(
                    message = msg,
                    onExecuteAction = onExecuteAction,
                    onQuickOptionSelected = onSendMessage
                )
            }

            if (isLoading) {
                item {
                    NyraTypingIndicator()
                }
            }
        }

        // Quick Suggestion Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickChip(label = "🤕 I have cramps") { onSendMessage("I am having pain in my lower abdomen, what should I do?") }
            QuickChip(label = "🥗 Phase nutrition") { onSendMessage("What foods should I eat during my current phase?") }
            QuickChip(label = "📞 Fake Call") { onSendMessage("Fake call me from Bada Bhai now") }
        }

        // Input Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PookieCardBg)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask NYRA anything about your health or day...", fontSize = 13.sp, color = PookieTextMuted) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PookiePinkPrimary,
                        unfocusedBorderColor = PookieCardLight,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = PookieCardLight,
                        unfocusedContainerColor = PookieCardLight
                    ),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() && !isLoading) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    enabled = inputText.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank() && !isLoading)
                                Brush.linearGradient(listOf(PookiePinkPrimary, PookieLavender))
                            else
                                Brush.linearGradient(listOf(PookieCardLight, PookieCardLight))
                        )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank() && !isLoading) Color.White else PookieTextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun NyraMessageBubble(
    message: NyraMessage,
    onExecuteAction: (NyraActionCard) -> Unit,
    onQuickOptionSelected: (String) -> Unit
) {
    val isUser = message.sender == "USER"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp
                    )
                )
                .background(
                    if (isUser) Brush.linearGradient(listOf(PookiePinkPrimary, PookiePinkGlow))
                    else Brush.linearGradient(listOf(PookieCardBg, PookieCardLight))
                )
                .border(
                    width = if (isUser) 0.dp else 1.dp,
                    color = PookieCardLight,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(14.dp)
        ) {
            Text(
                text = message.text,
                fontSize = 14.sp,
                color = Color.White,
                lineHeight = 20.sp
            )
        }

        // Action Card attachment if present
        message.actionCard?.let { card ->
            Spacer(modifier = Modifier.height(8.dp))
            var isExecuted by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .width(280.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(PookieCardBg)
                    .border(1.dp, PookiePinkPrimary.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TaskAlt, contentDescription = null, tint = PookiePinkPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(card.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(card.subtitle, fontSize = 12.sp, color = PookieTextMuted)
                    Text(card.timeOrDuration, fontSize = 11.sp, color = PookiePinkGlow)
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            onExecuteAction(card)
                            isExecuted = true
                        },
                        enabled = !isExecuted,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isExecuted) HealthGreen.copy(alpha = 0.3f) else PookiePinkPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isExecuted) "✓ Executed" else "Accept Suggestion ✨",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Quick Options if present
        if (message.options.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                message.options.forEach { option ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(PookieCardBg)
                            .border(1.dp, PookiePinkPrimary, RoundedCornerShape(16.dp))
                            .clickable { onQuickOptionSelected(option) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(option, fontSize = 12.sp, color = PookiePinkGlow, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun QuickChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PookieCardBg)
            .border(1.dp, PookieCardLight, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, fontSize = 11.sp, color = Color.White)
    }
}

@Composable
fun NyraTypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PookieCardBg)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("NYRA is typing", fontSize = 12.sp, color = PookieTextMuted)
            Text("...", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PookiePinkGlow.copy(alpha = alpha))
        }
    }
}
