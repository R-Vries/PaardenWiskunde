package ui.GUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.stable.Stall
import domain.stable.Stable
import ui.GUI.components.GameButton

@Composable
fun StallSelectionScreen(
    onStallSelected: (Stall) -> Unit,
    onExit: () -> Unit
) {
    var showAddStallScreen by remember {
        mutableStateOf(false)
    }

    if (showAddStallScreen) {
        AddStallScreen(
            onStallAdded = { stall ->
                showAddStallScreen = false
                onStallSelected(stall)
            },
            onBack = {
                showAddStallScreen = false
            }
        )

        return
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // =========================
        // ACHTERGROND
        // =========================

        Image(
            painter = painterResource(
                "images/Stall_Background.jpeg"
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Donkere overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(alpha = 0.35f)
                )
        )

        // =========================
        // CONTENT
        // =========================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // =========================
            // TITEL
            // =========================

            Text(
                text = "PAARDENWISKUNDE",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(35.dp)
            )

            // =========================
            // STALLEN
            // =========================

            Stable.stalls
                .take(6)
                .chunked(3)
                .forEach { row ->

                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        row.forEach { stall ->

                            GameButton(
                                text = stall.name,
                                icon = "images/Stall_Icon.png",
                                onClick = {
                                    onStallSelected(stall)
                                }
                            )
                        }
                    }
                }

        

            // =========================
            // ADD STALL
            // =========================

            GameButton(
                text = if (Stable.stalls.size < 6) {
                    "Add new stall"
                } else {
                    "Maximum size reached"
                },
                onClick = {
                    if (Stable.stalls.size < 6) {
                        showAddStallScreen = true
                    }
                },
                enabled = Stable.stalls.size < 6
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

        }
    }
}