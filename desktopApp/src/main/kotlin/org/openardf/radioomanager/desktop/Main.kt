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
import androidx.compose.material.TextField
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
import org.openardf.radioomanager.shared.event.EventAliasDetails
import org.openardf.radioomanager.shared.event.EventCategoryDetails
import org.openardf.radioomanager.shared.event.EventCompetitorDetails
import org.openardf.radioomanager.shared.event.EventProjectEditor
import org.openardf.radioomanager.shared.event.EventRaceDetails
import org.openardf.radioomanager.shared.event.EventProjectFile
import org.openardf.radioomanager.shared.event.EventProjectSummary
import org.openardf.radioomanager.shared.event.EventReadoutDetails
import org.openardf.radioomanager.shared.event.EventResultDetails
import java.nio.file.Path
import java.util.UUID

/** Starts the first Compose Desktop shell for Radio-O-Manager. */
fun main(args: Array<String>) = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Radio-O-Manager Desktop"
    ) {
        val startupPath = remember(args.toList()) { args.firstOrNull()?.let(Path::of) }
        val projectSession = remember { DesktopProjectSession(DesktopProjectFiles) }
        val startupStatus = remember(startupPath) { openStartupProject(projectSession, startupPath) }
        var projectFile by remember { mutableStateOf(projectSession.currentProject) }
        var projectStatusText by remember { mutableStateOf(startupStatus) }
        var hasUnsavedChanges by remember { mutableStateOf(projectSession.hasUnsavedChanges) }

        MenuBar {
            Menu("File") {
                Item("Open...", onClick = {
                    DesktopFileDialogs.chooseOpenProject()?.let { path ->
                        runCatching {
                            projectFile = projectSession.open(path)
                            hasUnsavedChanges = projectSession.hasUnsavedChanges
                            projectStatusText = "Opened ${path.fileName}"
                        }.onFailure { error ->
                            projectStatusText = "Open failed: ${error.message ?: error::class.simpleName}"
                        }
                    }
                })
                Item("Save", enabled = projectFile != null && hasUnsavedChanges, onClick = {
                    runCatching {
                        projectSession.save()
                        projectFile = projectSession.currentProject
                        hasUnsavedChanges = projectSession.hasUnsavedChanges
                        projectStatusText = "Saved ${projectSession.currentPath?.fileName ?: "project"}"
                    }.onFailure { error ->
                        projectStatusText = "Save failed: ${error.message ?: error::class.simpleName}"
                    }
                })
                Item("Save As...", enabled = projectFile != null, onClick = {
                    DesktopFileDialogs.chooseSaveProject()?.let { path ->
                        runCatching {
                            projectSession.saveAs(path)
                            projectFile = projectSession.currentProject
                            hasUnsavedChanges = projectSession.hasUnsavedChanges
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
            projectStatusText = projectStatusText,
            hasUnsavedChanges = hasUnsavedChanges,
            onRenameRace = { name ->
                runCatching {
                    projectFile = projectSession.updateCurrentProject { currentProject ->
                        EventProjectEditor.renameRace(currentProject, name)
                    }
                    hasUnsavedChanges = projectSession.hasUnsavedChanges
                    projectStatusText = "Unsaved changes."
                }.onFailure { error ->
                    projectStatusText = "Edit failed: ${error.message ?: error::class.simpleName}"
                }
            },
            onRenameCategory = { categoryId, name ->
                runCatching {
                    projectFile = projectSession.updateCurrentProject { currentProject ->
                        EventProjectEditor.renameCategory(currentProject, categoryId, name)
                    }
                    hasUnsavedChanges = projectSession.hasUnsavedChanges
                    projectStatusText = "Unsaved changes."
                }.onFailure { error ->
                    projectStatusText = "Edit failed: ${error.message ?: error::class.simpleName}"
                }
            },
            onRenameCompetitor = { competitorId, firstName, lastName ->
                runCatching {
                    projectFile = projectSession.updateCurrentProject { currentProject ->
                        EventProjectEditor.renameCompetitor(currentProject, competitorId, firstName, lastName)
                    }
                    hasUnsavedChanges = projectSession.hasUnsavedChanges
                    projectStatusText = "Unsaved changes."
                }.onFailure { error ->
                    projectStatusText = "Edit failed: ${error.message ?: error::class.simpleName}"
                }
            },
            onUpdateCompetitorNumbers = { competitorId, startNumber, siNumber ->
                runCatching {
                    projectFile = projectSession.updateCurrentProject { currentProject ->
                        EventProjectEditor.updateCompetitorNumbers(currentProject, competitorId, startNumber, siNumber)
                    }
                    hasUnsavedChanges = projectSession.hasUnsavedChanges
                    projectStatusText = "Unsaved changes."
                }.onFailure { error ->
                    projectStatusText = "Edit failed: ${error.message ?: error::class.simpleName}"
                }
            },
            onUpdateAlias = { aliasId, siCode, name ->
                runCatching {
                    projectFile = projectSession.updateCurrentProject { currentProject ->
                        EventProjectEditor.updateAlias(currentProject, aliasId, siCode, name)
                    }
                    hasUnsavedChanges = projectSession.hasUnsavedChanges
                    projectStatusText = "Unsaved changes."
                }.onFailure { error ->
                    projectStatusText = "Edit failed: ${error.message ?: error::class.simpleName}"
                }
            },
            onAddAlias = { siCode, name ->
                runCatching {
                    projectFile = projectSession.updateCurrentProject { currentProject ->
                        EventProjectEditor.addAlias(currentProject, UUID.randomUUID().toString(), siCode, name)
                    }
                    hasUnsavedChanges = projectSession.hasUnsavedChanges
                    projectStatusText = "Unsaved changes."
                }.onFailure { error ->
                    projectStatusText = "Edit failed: ${error.message ?: error::class.simpleName}"
                }
            },
            onRemoveAlias = { aliasId ->
                runCatching {
                    projectFile = projectSession.updateCurrentProject { currentProject ->
                        EventProjectEditor.removeAlias(currentProject, aliasId)
                    }
                    hasUnsavedChanges = projectSession.hasUnsavedChanges
                    projectStatusText = "Unsaved changes."
                }.onFailure { error ->
                    projectStatusText = "Edit failed: ${error.message ?: error::class.simpleName}"
                }
            }
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
    projectStatusText: String = "No project open.",
    hasUnsavedChanges: Boolean = false,
    onRenameRace: (String) -> Unit = {},
    onRenameCategory: (String, String) -> Unit = { _, _ -> },
    onRenameCompetitor: (String, String, String) -> Unit = { _, _, _ -> },
    onUpdateCompetitorNumbers: (String, String, String) -> Unit = { _, _, _ -> },
    onUpdateAlias: (String, String, String) -> Unit = { _, _, _ -> },
    onAddAlias: (String, String) -> Unit = { _, _ -> },
    onRemoveAlias: (String) -> Unit = {}
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
                    SectionWorkspace(
                        section = selectedSection,
                        projectFile = projectFile,
                        projectStatusText = projectStatusText,
                        onRenameRace = onRenameRace,
                        onRenameCategory = onRenameCategory,
                        onRenameCompetitor = onRenameCompetitor,
                        onUpdateCompetitorNumbers = onUpdateCompetitorNumbers,
                        onUpdateAlias = onUpdateAlias,
                        onAddAlias = onAddAlias,
                        onRemoveAlias = onRemoveAlias
                    )
                }
                StatusStrip(projectStatusText, hasUnsavedChanges)
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
    projectStatusText: String,
    onRenameRace: (String) -> Unit,
    onRenameCategory: (String, String) -> Unit,
    onRenameCompetitor: (String, String, String) -> Unit,
    onUpdateCompetitorNumbers: (String, String, String) -> Unit,
    onUpdateAlias: (String, String, String) -> Unit,
    onAddAlias: (String, String) -> Unit,
    onRemoveAlias: (String) -> Unit
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
            RaceDetailsPanel(
                details = EventRaceDetails.from(projectFile.raceData.race),
                onRenameRace = onRenameRace
            )
        }
        if (section == DesktopSection.Categories && projectFile != null) {
            CategoryDetailsPanel(
                categories = EventCategoryDetails.from(projectFile.raceData),
                onRenameCategory = onRenameCategory
            )
        }
        if (section == DesktopSection.Competitors && projectFile != null) {
            CompetitorDetailsPanel(
                competitors = EventCompetitorDetails.from(projectFile.raceData),
                onRenameCompetitor = onRenameCompetitor,
                onUpdateCompetitorNumbers = onUpdateCompetitorNumbers
            )
        }
        if (section == DesktopSection.Aliases && projectFile != null) {
            AliasDetailsPanel(
                aliases = EventAliasDetails.from(projectFile.raceData),
                onUpdateAlias = onUpdateAlias,
                onAddAlias = onAddAlias,
                onRemoveAlias = onRemoveAlias
            )
        }
        if (section == DesktopSection.Readouts && projectFile != null) {
            ReadoutDetailsPanel(EventReadoutDetails.from(projectFile.raceData))
        }
        if (section == DesktopSection.Results && projectFile != null) {
            ResultDetailsPanel(EventResultDetails.from(projectFile.raceData))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DesktopPalette.LightGrey)
        )
        Text(
            text = if (projectFile != null) {
                "${projectFile.raceData.race.startDateTimeIso} - $projectStatusText"
            } else {
                projectStatusText
            },
            color = DesktopPalette.Disconnected,
            fontSize = 13.sp
        )
    }
}

/** Shows read-only competitor result rows. */
@Composable
private fun ResultDetailsPanel(results: List<EventResultDetails>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DetailHeaderRow(listOf("Place", "Competitor", "Status", "Points", "Runtime"))
        results.forEach { result ->
            DetailGridRow(
                listOf(
                    result.placeText,
                    result.competitorName,
                    result.statusLabel,
                    result.pointsText,
                    result.runTimeText
                )
            )
        }
    }
}

/** Shows read-only matched and unmatched SI-card readout rows. */
@Composable
private fun ReadoutDetailsPanel(readouts: List<EventReadoutDetails>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DetailHeaderRow(listOf("SI no.", "Competitor", "Status", "Points", "Runtime"))
        readouts.forEach { readout ->
            DetailGridRow(
                listOf(
                    readout.siNumberText,
                    readout.competitorName,
                    readout.statusLabel,
                    readout.pointsText,
                    readout.runTimeText
                )
            )
        }
    }
}

/** Shows editable competitor names using shared category lookup and formatting. */
@Composable
private fun CompetitorDetailsPanel(
    competitors: List<EventCompetitorDetails>,
    onRenameCompetitor: (String, String, String) -> Unit,
    onUpdateCompetitorNumbers: (String, String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DetailHeaderRow(listOf("First", "Last", "Category", "Start no.", "SI no.", "", ""))
        competitors.forEach { competitor ->
            CompetitorDetailRow(
                competitor = competitor,
                onRenameCompetitor = onRenameCompetitor,
                onUpdateCompetitorNumbers = onUpdateCompetitorNumbers
            )
        }
    }
}

/** Shows one editable competitor-name row plus read-only assignment fields. */
@Composable
private fun CompetitorDetailRow(
    competitor: EventCompetitorDetails,
    onRenameCompetitor: (String, String, String) -> Unit,
    onUpdateCompetitorNumbers: (String, String, String) -> Unit
) {
    var firstNameDraft by remember(competitor.id, competitor.firstName) { mutableStateOf(competitor.firstName) }
    var lastNameDraft by remember(competitor.id, competitor.lastName) { mutableStateOf(competitor.lastName) }
    var startNumberDraft by remember(competitor.id, competitor.startNumberText) {
        mutableStateOf(competitor.startNumberText)
    }
    var siNumberDraft by remember(competitor.id, competitor.siNumberText) { mutableStateOf(competitor.siNumberText) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = firstNameDraft,
            onValueChange = { firstNameDraft = it },
            modifier = Modifier.weight(1f),
            label = { Text("First") }
        )
        TextField(
            value = lastNameDraft,
            onValueChange = { lastNameDraft = it },
            modifier = Modifier.weight(1f),
            label = { Text("Last") }
        )
        Text(competitor.categoryName, modifier = Modifier.weight(1f), color = DesktopPalette.Black, fontSize = 13.sp)
        TextField(
            value = startNumberDraft,
            onValueChange = { startNumberDraft = it },
            modifier = Modifier.weight(1f),
            label = { Text("Start") }
        )
        TextField(
            value = siNumberDraft,
            onValueChange = { siNumberDraft = it },
            modifier = Modifier.weight(1f),
            label = { Text("SI") }
        )
        Button(
            onClick = { onRenameCompetitor(competitor.id, firstNameDraft, lastNameDraft) },
            modifier = Modifier.weight(1f),
            enabled = firstNameDraft != competitor.firstName || lastNameDraft != competitor.lastName
        ) {
            Text("Name")
        }
        Button(
            onClick = { onUpdateCompetitorNumbers(competitor.id, startNumberDraft, siNumberDraft) },
            modifier = Modifier.weight(1f),
            enabled = startNumberDraft != competitor.startNumberText || siNumberDraft != competitor.siNumberText
        ) {
            Text("Nos.")
        }
    }
}

/** Shows editable alias rows backed by shared alias validation rules. */
@Composable
private fun AliasDetailsPanel(
    aliases: List<EventAliasDetails>,
    onUpdateAlias: (String, String, String) -> Unit,
    onAddAlias: (String, String) -> Unit,
    onRemoveAlias: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AliasAddRow(onAddAlias)
        DetailHeaderRow(listOf("SI code", "Alias", "", ""))
        aliases.forEach { alias ->
            AliasDetailRow(alias, onUpdateAlias, onRemoveAlias)
        }
    }
}

/** Shows the new-alias entry row above the existing alias mappings. */
@Composable
private fun AliasAddRow(onAddAlias: (String, String) -> Unit) {
    var siCodeDraft by remember { mutableStateOf("") }
    var nameDraft by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = siCodeDraft,
            onValueChange = { siCodeDraft = it },
            modifier = Modifier.weight(1f),
            label = { Text("New SI code") }
        )
        TextField(
            value = nameDraft,
            onValueChange = { nameDraft = it },
            modifier = Modifier.weight(1f),
            label = { Text("New alias") }
        )
        Button(
            onClick = { onAddAlias(siCodeDraft, nameDraft) },
            modifier = Modifier.weight(1f),
            enabled = siCodeDraft.isNotBlank() || nameDraft.isNotBlank()
        ) {
            Text("Add")
        }
    }
}

/** Shows one editable alias row for a SportIdent control code mapping. */
@Composable
private fun AliasDetailRow(
    alias: EventAliasDetails,
    onUpdateAlias: (String, String, String) -> Unit,
    onRemoveAlias: (String) -> Unit
) {
    var siCodeDraft by remember(alias.id, alias.siCodeText) { mutableStateOf(alias.siCodeText) }
    var nameDraft by remember(alias.id, alias.name) { mutableStateOf(alias.name) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = siCodeDraft,
            onValueChange = { siCodeDraft = it },
            modifier = Modifier.weight(1f),
            label = { Text("SI code") }
        )
        TextField(
            value = nameDraft,
            onValueChange = { nameDraft = it },
            modifier = Modifier.weight(1f),
            label = { Text("Alias") }
        )
        Button(
            onClick = { onUpdateAlias(alias.id, siCodeDraft, nameDraft) },
            modifier = Modifier.weight(1f),
            enabled = siCodeDraft != alias.siCodeText || nameDraft != alias.name
        ) {
            Text("Apply")
        }
        Button(
            onClick = { onRemoveAlias(alias.id) },
            modifier = Modifier.weight(1f)
        ) {
            Text("Delete")
        }
    }
}

/** Shows editable category names with read-only effective race settings. */
@Composable
private fun CategoryDetailsPanel(
    categories: List<EventCategoryDetails>,
    onRenameCategory: (String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DetailHeaderRow(listOf("Name", "Type", "Band", "Limit", "Controls", ""))
        categories.forEach { category ->
            CategoryDetailRow(category, onRenameCategory)
        }
    }
}

/** Shows one editable category-name row plus read-only derived category settings. */
@Composable
private fun CategoryDetailRow(
    category: EventCategoryDetails,
    onRenameCategory: (String, String) -> Unit
) {
    var categoryNameDraft by remember(category.id, category.name) { mutableStateOf(category.name) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = categoryNameDraft,
            onValueChange = { categoryNameDraft = it },
            modifier = Modifier.weight(1f),
            label = { Text("Category") }
        )
        Text(category.raceTypeLabel, modifier = Modifier.weight(1f), color = DesktopPalette.Black, fontSize = 13.sp)
        Text(category.raceBandLabel, modifier = Modifier.weight(1f), color = DesktopPalette.Black, fontSize = 13.sp)
        Text(category.timeLimitText, modifier = Modifier.weight(1f), color = DesktopPalette.Black, fontSize = 13.sp)
        Text(category.controlPointsText, modifier = Modifier.weight(1f), color = DesktopPalette.Black, fontSize = 13.sp)
        Button(
            onClick = { onRenameCategory(category.id, categoryNameDraft) },
            modifier = Modifier.weight(1f),
            enabled = categoryNameDraft != category.name
        ) {
            Text("Apply")
        }
    }
}

/** Shows editable race metadata backed by shared project-editing rules. */
@Composable
private fun RaceDetailsPanel(
    details: EventRaceDetails,
    onRenameRace: (String) -> Unit
) {
    var raceNameDraft by remember(details.name) { mutableStateOf(details.name) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = raceNameDraft,
                onValueChange = { raceNameDraft = it },
                modifier = Modifier.weight(1f),
                label = { Text("Race name") }
            )
            Button(
                onClick = { onRenameRace(raceNameDraft) },
                enabled = raceNameDraft != details.name
            ) {
                Text("Apply")
            }
        }
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
        DesktopSection.Aliases -> "${projectFile?.raceData?.aliases?.size ?: 0} aliases loaded."
        DesktopSection.Readouts -> "${summary?.readoutCount ?: 0} SI-card readouts loaded."
        DesktopSection.Results -> "${summary?.resultCount ?: 0} results loaded."
        DesktopSection.Settings -> "Desktop settings."
    }
}

/** Shows the current SI-reader connection state and project-save status. */
@Composable
private fun StatusStrip(
    projectStatusText: String,
    hasUnsavedChanges: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(DesktopPalette.Disconnected)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "SI station disconnected - $projectStatusText${if (hasUnsavedChanges) " *" else ""}",
            color = DesktopPalette.White,
            fontSize = 13.sp
        )
    }
}
