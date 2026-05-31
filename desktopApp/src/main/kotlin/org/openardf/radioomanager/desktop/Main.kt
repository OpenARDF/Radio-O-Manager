package org.openardf.radioomanager.desktop

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.openardf.radioomanager.shared.event.EventCategoryDetails
import org.openardf.radioomanager.shared.event.EventRaceDetails
import org.openardf.radioomanager.shared.event.EventProjectFile
import org.openardf.radioomanager.shared.event.EventProjectSummary

/** Starts the first Compose Desktop shell for Radio-O-Manager. */
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Radio-O-Manager Desktop"
    ) {
        val projectSession = remember { DesktopProjectSession(DesktopProjectFiles) }
        var projectFile by remember { mutableStateOf(projectSession.currentProject) }
        var projectStatusText by remember { mutableStateOf("No project open.") }

        MenuBar {
            Menu("File") {
                Item("Open...", onClick = {
                    DesktopFileDialogs.chooseOpenProject()?.let { path ->
                        runCatching {
                            projectFile = projectSession.open(path)
                            projectStatusText = "Opened ${path.fileName}"
                        }.onFailure { error ->
                            projectStatusText = "Open failed: ${error.message ?: error::class.simpleName}"
                        }
                    }
                })
                Item("Save As...", enabled = projectFile != null, onClick = {
                    DesktopFileDialogs.chooseSaveProject()?.let { path ->
                        runCatching {
                            projectSession.saveAs(path)
                            projectStatusText = "Saved ${path.fileName}"
                        }.onFailure { error ->
                            projectStatusText = "Save failed: ${error.message ?: error::class.simpleName}"
                        }
                    }
                })
            }
        }

        RadioOManagerDesktopApp(
            projectFile = projectFile,
            projectStatusText = projectStatusText
        )
    }
}

/**
 * Builds the launchable desktop app shell.
 *
 * This composable owns only shell state for now. Event data, persistence, and
 * SI-reader workflows should be introduced through shared services in later
 * slices instead of being embedded directly in the desktop UI.
 */
@Composable
fun RadioOManagerDesktopApp(
    projectFile: EventProjectFile? = null,
    projectStatusText: String = "No project open."
) {
    MaterialTheme(
        colors = MaterialTheme.colors.copy(
            primary = DesktopPalette.Primary,
            primaryVariant = DesktopPalette.PrimaryVariant,
            secondary = DesktopPalette.Secondary,
            error = DesktopPalette.Error,
            onPrimary = DesktopPalette.White,
            onSecondary = DesktopPalette.Black,
            onError = DesktopPalette.White
        )
    ) {
        var selectedSection by remember { mutableStateOf(DesktopSection.Races) }

        Surface(modifier = Modifier.fillMaxSize(), color = DesktopPalette.White) {
            Column(modifier = Modifier.fillMaxSize()) {
                AppTopBar()
                Row(modifier = Modifier.weight(1f)) {
                    NavigationRail(
                        selectedSection = selectedSection,
                        onSectionSelected = { selectedSection = it }
                    )
                    SectionWorkspace(selectedSection, projectFile, projectStatusText)
                }
                StatusStrip()
            }
        }
    }
}

/** Renders the Android-style app bar used at the top of the desktop window. */
@Composable
private fun AppTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(DesktopPalette.Primary)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Radio-O-Manager",
            color = DesktopPalette.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Desktop event-admin preview",
            color = DesktopPalette.White,
            fontSize = 14.sp
        )
    }
}

/** Shows the main event-admin sections using the same names as Android. */
@Composable
private fun NavigationRail(
    selectedSection: DesktopSection,
    onSectionSelected: (DesktopSection) -> Unit
) {
    Column(
        modifier = Modifier
            .width(180.dp)
            .fillMaxHeight()
            .background(Color(0xFFF5F5F5))
            .border(1.dp, DesktopPalette.LightGrey)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        DesktopSection.entries.forEach { section ->
            Button(
                onClick = { onSectionSelected(section) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = section.label,
                    fontWeight = if (section == selectedSection) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

/** Displays an Android-style empty state for the selected section. */
@Composable
private fun SectionWorkspace(
    section: DesktopSection,
    projectFile: EventProjectFile?,
    projectStatusText: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = section.label,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DesktopPalette.Black
        )
        Text(
            text = sectionSummary(section, projectFile),
            color = DesktopPalette.Black,
            fontSize = 14.sp
        )
        if (section == DesktopSection.Races && projectFile != null) {
            RaceDetailsPanel(EventRaceDetails.from(projectFile.raceData.race))
        }
        if (section == DesktopSection.Categories && projectFile != null) {
            CategoryDetailsPanel(EventCategoryDetails.from(projectFile.raceData))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DesktopPalette.LightGrey)
        )
        Text(
            text = projectFile?.raceData?.race?.startDateTimeIso ?: projectStatusText,
            color = DesktopPalette.Disconnected,
            fontSize = 13.sp
        )
    }
}

/** Shows read-only category rows using shared effective race settings. */
@Composable
private fun CategoryDetailsPanel(categories: List<EventCategoryDetails>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DetailHeaderRow(listOf("Name", "Type", "Band", "Limit", "Controls"))
        categories.forEach { category ->
            DetailGridRow(
                listOf(
                    category.name,
                    category.raceTypeLabel,
                    category.raceBandLabel,
                    category.timeLimitText,
                    category.controlPointsText
                )
            )
        }
    }
}

/** Shows read-only race metadata using shared display values. */
@Composable
private fun RaceDetailsPanel(details: EventRaceDetails) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DetailRow("Start", details.startDateTimeIso)
        DetailRow("Type", details.raceTypeLabel)
        DetailRow("Level", details.raceLevelLabel)
        DetailRow("Band", details.raceBandLabel)
        DetailRow("Time limit", details.timeLimitText)
    }
}

/** Displays a compact header row for read-only desktop detail grids. */
@Composable
private fun DetailHeaderRow(values: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        values.forEach { value ->
            Text(
                text = value,
                modifier = Modifier.weight(1f),
                color = DesktopPalette.Disconnected,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/** Displays a compact value row for read-only desktop detail grids. */
@Composable
private fun DetailGridRow(values: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        values.forEach { value ->
            Text(
                text = value,
                modifier = Modifier.weight(1f),
                color = DesktopPalette.Black,
                fontSize = 13.sp
            )
        }
    }
}

/** Displays a compact label/value pair for read-only desktop event details. */
@Composable
private fun DetailRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            modifier = Modifier.width(96.dp),
            color = DesktopPalette.Disconnected,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            color = DesktopPalette.Black,
            fontSize = 13.sp
        )
    }
}

/** Provides section-specific content summaries without introducing editing behavior. */
private fun sectionSummary(section: DesktopSection, projectFile: EventProjectFile?): String {
    val summary = projectFile?.let(EventProjectSummary::from)
    return when (section) {
        DesktopSection.Races -> summary?.raceName ?: "No races loaded."
        DesktopSection.Categories -> "${summary?.categoryCount ?: 0} categories loaded."
        DesktopSection.Competitors -> "${summary?.competitorCount ?: 0} competitors loaded."
        DesktopSection.Readouts -> "${summary?.readoutCount ?: 0} SI-card readouts loaded."
        DesktopSection.Results -> "${summary?.resultCount ?: 0} results loaded."
        DesktopSection.Settings -> "Desktop settings."
    }
}

/** Shows the current SI-reader connection state at the bottom of the window. */
@Composable
private fun StatusStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(DesktopPalette.Disconnected)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "SI station disconnected",
            color = DesktopPalette.White,
            fontSize = 13.sp
        )
    }
}
