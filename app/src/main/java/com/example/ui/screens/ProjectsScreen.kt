package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EchoBg
import com.example.ui.theme.EchoBgAlt
import com.example.ui.theme.EchoCard
import com.example.ui.theme.EchoCardStroke
import com.example.ui.theme.EchoCyan
import com.example.ui.theme.EchoMuted
import com.example.ui.theme.EchoOnCyan
import com.example.ui.theme.EchoText
import com.example.ui.viewmodel.WorkspaceViewModel

@Composable
fun ProjectsScreen(
    viewModel: WorkspaceViewModel,
    onNavigateToWorkspaceFolder: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allFolders by viewModel.allFolders.collectAsState()
    val echoComicsFolder = allFolders.find { it.name.contains("Echo Comics", ignoreCase = true) }
    val myProjectsFolder = allFolders.find { it.name.contains("My Projects", ignoreCase = true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EchoBg)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = "Projects & Visual Stories",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoText
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Visual storytelling, active narrative drafts, and dedicated project workspaces.",
                        fontSize = 13.sp,
                        color = EchoMuted,
                        lineHeight = 18.sp
                    )
                }
            }

            // Echo Comics Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("echo_comics_section"),
                    colors = CardDefaults.cardColors(containerColor = EchoCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Brush,
                                contentDescription = null,
                                tint = EchoCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "Echo Comics",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoText
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "All comic files, visual stories, and artwork related to the ECHO universe.",
                            fontSize = 13.sp,
                            color = EchoMuted
                        )

                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = "📁 Comic Storylines & Issues Archive",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EchoCyan
                        )
                        Spacer(Modifier.height(8.dp))

                        val comicFiles = listOf(
                            "Cover Art & Title Page — Echo Comics Main Title",
                            "Issue 1 — The First Frequency (Hilsea 1986)",
                            "Issue 2 — The Farnworth Handshake (Bedrock lock)",
                            "Issue 3 — Across the Solent (Chalk waveguide)",
                            "Character Profiles — G01 Resonator, G02 Observer, and 42 Geniuses",
                            "Concept Sketches — Node Transducers & Subterranean Vaults",
                            "Storyboard Archive — Season 1 & Season 2 Narrative Arcs"
                        )

                        comicFiles.forEach { file ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("• ", color = EchoCyan, fontWeight = FontWeight.Bold)
                                Text(
                                    text = file,
                                    fontSize = 13.sp,
                                    color = EchoText.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (echoComicsFolder != null) {
                                    onNavigateToWorkspaceFolder(echoComicsFolder.id)
                                } else {
                                    onNavigateToWorkspaceFolder("")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EchoCyan,
                                contentColor = EchoOnCyan
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("open_comics_workspace_button")
                        ) {
                            Text("Open in Workspace", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // My Projects Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("my_projects_section"),
                    colors = CardDefaults.cardColors(containerColor = EchoBgAlt),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EchoCardStroke))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = EchoCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "My Projects",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoText
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Your personal workspace — everything you're working on, kept separate from the main archive.",
                            fontSize = 13.sp,
                            color = EchoMuted
                        )

                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = "📁 Active Workspace Projects",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EchoCyan
                        )
                        Spacer(Modifier.height(8.dp))

                        val activeProjects = listOf(
                            "Echo Comics — Visual storytelling project",
                            "42 Geniuses Novel — Full narrative draft & chapters",
                            "ECHO-INTERFACE Docs — Field guides, specs, and reference materials",
                            "Personal Notes & Sketches — Your ideas and ongoing work"
                        )

                        activeProjects.forEach { proj ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("• ", color = EchoCyan, fontWeight = FontWeight.Bold)
                                Text(
                                    text = proj,
                                    fontSize = 13.sp,
                                    color = EchoText.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (myProjectsFolder != null) {
                                    onNavigateToWorkspaceFolder(myProjectsFolder.id)
                                } else {
                                    onNavigateToWorkspaceFolder("")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EchoCyan,
                                contentColor = EchoOnCyan
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("open_projects_workspace_button")
                        ) {
                            Text("Open in Workspace", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
