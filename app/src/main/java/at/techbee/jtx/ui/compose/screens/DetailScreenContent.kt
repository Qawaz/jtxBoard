/*
 * Copyright (c) Techbee e.U.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package at.techbee.jtx.ui.compose.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.GppMaybe
import androidx.compose.material.icons.outlined.PublishedWithChanges
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.techbee.jtx.R
import at.techbee.jtx.database.*
import at.techbee.jtx.database.properties.Category
import at.techbee.jtx.database.properties.Resource
import at.techbee.jtx.database.relations.ICalEntity
import at.techbee.jtx.database.views.ICal4List
import at.techbee.jtx.ui.compose.cards.*
import at.techbee.jtx.ui.compose.dialogs.RequestContactsPermissionDialog
import at.techbee.jtx.ui.compose.elements.CollectionsSpinner
import at.techbee.jtx.ui.compose.elements.ColoredEdge
import at.techbee.jtx.ui.compose.elements.VerticalDateCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenContent(
    iCalEntity: State<ICalEntity>,
    isEditMode: MutableState<Boolean>,
    subtasks: List<ICal4List>,
    subnotes: List<ICal4List>,
    //attachments: List<Attachment>,
    allCollections: List<ICalCollection>,
    modifier: Modifier = Modifier,
    //player: MediaPlayer?,
    onProgressChanged: (itemId: Long, newPercent: Int, isLinkedRecurringInstance: Boolean) -> Unit,
    onExpandedChanged: (itemId: Long, isSubtasksExpanded: Boolean, isSubnotesExpanded: Boolean, isAttachmentsExpanded: Boolean) -> Unit
) {

    val context = LocalContext.current
    var permissionsDialogShownOnce by rememberSaveable { mutableStateOf(false) }  // TODO: Set to false for release!
    if(LocalInspectionMode.current)  // only for previews
        permissionsDialogShownOnce = true

    var summary by remember { mutableStateOf(iCalEntity.value.property.summary ?: "") }
    var description by remember { mutableStateOf(iCalEntity.value.property.description ?: "") }
    val contact = remember { mutableStateOf(iCalEntity.value.property.contact ?: "") }
    val url = remember { mutableStateOf(iCalEntity.value.property.url ?: "") }
    val location = remember { mutableStateOf(iCalEntity.value.property.location ?: "") }
    val geoLat = remember { mutableStateOf(iCalEntity.value.property.geoLat) }
    val geoLong = remember { mutableStateOf(iCalEntity.value.property.geoLong) }
    var status by remember { mutableStateOf(iCalEntity.value.property.status) }
    var classification by remember { mutableStateOf(iCalEntity.value.property.classification) }
    var priority by remember { mutableStateOf(iCalEntity.value.property.priority ?: 0) }
    val categories = remember { mutableStateOf(iCalEntity.value.categories ?: emptyList()) }
    val resources = remember { mutableStateOf(iCalEntity.value.resources ?: emptyList()) }


    if (!permissionsDialogShownOnce) {
        RequestContactsPermissionDialog(
            onConfirm = { permissionsDialogShownOnce = true },
            onDismiss = { permissionsDialogShownOnce = true }
        )
    }

    /*
    var markwon = Markwon.builder(LocalContext.current)
        .usePlugin(StrikethroughPlugin.create())
        .build()
     */

    Box(Modifier.verticalScroll(rememberScrollState())) {

        ColoredEdge(iCalEntity.value.property.color, iCalEntity.value.ICalCollection?.color)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            AnimatedVisibility(!isEditMode.value) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ElevatedCard(
                        modifier = Modifier.weight(1f)
                    ) {

                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Folder, stringResource(id = R.string.collection))
                            Text(iCalEntity.value.ICalCollection?.displayName + iCalEntity.value.ICalCollection?.accountName?.let { " (" + it + ")" })
                        }
                    }
                }
            }

            AnimatedVisibility(isEditMode.value) {

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        CollectionsSpinner(
                            collections = allCollections,
                            preselected = iCalEntity.value.ICalCollection
                                ?: allCollections.first(),   // TODO: Load last used collection for new entries
                            includeReadOnly = false,
                            includeVJOURNAL = false,
                            includeVTODO = false,
                            onSelectionChanged = { /* TODO */ },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Outlined.ColorLens, stringResource(id = R.string.color))
                        }
                    }
                }
            }

            if (iCalEntity.value.property.module == Module.JOURNAL.name && iCalEntity.value.property.dtstart != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VerticalDateCard(
                        datetime = iCalEntity.value.property.dtstart,
                        timezone = iCalEntity.value.property.dtstartTimezone
                    )
                }
            }

            AnimatedVisibility(!isEditMode.value) {
                SelectionContainer {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        if (summary.isNotBlank())
                            Text(
                                summary,
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.titleMedium,
                                //fontWeight = FontWeight.Bold
                            )
                        if (description.isNotBlank())
                            Text(
                                description,
                                modifier = Modifier.padding(8.dp)
                            )
                    }
                }
            }

            AnimatedVisibility(isEditMode.value) {

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(
                        value = summary,
                        onValueChange = {
                            summary = it
                        },
                        label = { Text(stringResource(id = R.string.summary)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            description = it
                        },
                        label = { Text(stringResource(id = R.string.description)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }

            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    label = {
                        if (iCalEntity.value.property.component == Component.VJOURNAL.name)
                            Text(StatusJournal.getStringResource(context, status) ?: status ?: "")
                        else
                            Text(StatusTodo.getStringResource(context, status) ?: status ?: "")
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.PublishedWithChanges,
                            stringResource(id = R.string.status)
                        )
                    },
                    onClick = {
                        if (iCalEntity.value.property.component == Component.VJOURNAL.name) {
                            status = try {
                                StatusJournal.getNext(StatusJournal.valueOf(status ?: "")).name
                            } catch (e: IllegalArgumentException) {
                                StatusJournal.getNext(null).name
                            }
                        } else {
                            status = try {
                                StatusTodo.getNext(StatusTodo.valueOf(status ?: "")).name
                            } catch (e: IllegalArgumentException) {
                                StatusTodo.getNext(null).name
                            }
                        }
                    }
                )

                AssistChip(
                    label = {
                        Text(
                            Classification.getStringResource(context, classification)
                                ?: classification ?: ""
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.GppMaybe,
                            stringResource(id = R.string.classification)
                        )
                    },
                    onClick = {
                        classification = try {
                            Classification.getNext(
                                Classification.valueOf(
                                    classification ?: ""
                                )
                            ).name
                        } catch (e: IllegalArgumentException) {
                            Classification.getNext(null).name
                        }
                    },
                )
            }
            if (iCalEntity.value.property.component == Component.VTODO.name) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Slider(
                        value = priority.toFloat(),
                        onValueChange = { priority = it.toInt() },
                        valueRange = 0f..9f,
                        steps = 9,
                        modifier = Modifier.width(200.dp)
                    )
                    Text(
                        stringArrayResource(id = R.array.priority)[priority],
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }


            AnimatedVisibility(categories.value.isNotEmpty() || isEditMode.value) {
                DetailsCardCategories(
                    categories = categories,
                    isEditMode = isEditMode,
                    onCategoriesUpdated = { /*TODO*/ },
                    allCategories = listOf(Category(text = "category1"), Category(text = "category2"), Category(text = "Whatever")), // TODO
                )
            }

            AnimatedVisibility(resources.value.isNotEmpty() || isEditMode.value) {
                DetailsCardResources(
                    resources = resources,
                    isEditMode = isEditMode,
                    onResourcesUpdated = { /*TODO*/ },
                    allResources = listOf(Resource(text = "projector"), Resource(text = "overhead-thingy"), Resource(text = "Whatever")),
                )
            }

            AnimatedVisibility(contact.value.isNotBlank() || isEditMode.value) {
                DetailsCardContact(
                    contact = contact,
                    isEditMode = isEditMode,
                    onContactUpdated = { /*TODO*/ },
                )
            }

            AnimatedVisibility(url.value.isNotEmpty() || isEditMode.value) {
                DetailsCardUrl(
                    url = url,
                    isEditMode = isEditMode,
                    onUrlUpdated = { /*TODO*/ },
                )
            }

            AnimatedVisibility((location.value.isNotEmpty() || (geoLat.value != null && geoLong.value != null)) || isEditMode.value) {
                DetailsCardLocation(
                    location = location,
                    geoLat = geoLat,
                    geoLong = geoLong,
                    isEditMode = isEditMode,
                    onLocationUpdated = { /*TODO*/ },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenContent_JOURNAL() {
    MaterialTheme {
        val entity = ICalEntity().apply {
            this.property = ICalObject.createJournal("MySummary")
            //this.property.dtstart = System.currentTimeMillis()
        }
        entity.property.description = "Hello World, this \nis my description."
        entity.property.contact = "John Doe, +1 555 5545"
        entity.categories = listOf(
            Category(1, 1, "MyCategory1", null, null),
            Category(2, 1, "My Dog likes Cats", null, null),
            Category(3, 1, "This is a very long category", null, null),
        )

        DetailScreenContent(
            iCalEntity = remember { mutableStateOf(entity) },
            isEditMode = remember { mutableStateOf(false) },
            subtasks = emptyList(),
            subnotes = emptyList(),
            //attachments = emptyList(),
            allCollections = listOf(ICalCollection.createLocalCollection(LocalContext.current)),
            //player = null,
            onProgressChanged = { _, _, _ -> },
            onExpandedChanged = { _, _, _, _ -> }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DetailScreenContent_TODO_editInitially() {
    MaterialTheme {
        val entity = ICalEntity().apply {
            this.property = ICalObject.createTask("MySummary")

            //this.property.dtstart = System.currentTimeMillis()
        }
        entity.property.description = "Hello World, this \nis my description."
        entity.property.contact = "John Doe, +1 555 5545"

        DetailScreenContent(
            iCalEntity = remember { mutableStateOf(entity) },
            isEditMode = remember { mutableStateOf(true) },
            subtasks = emptyList(),
            subnotes = emptyList(),
            //attachments = emptyList(),
            allCollections = listOf(ICalCollection.createLocalCollection(LocalContext.current)),
            //player = null,
            onProgressChanged = { _, _, _ -> },
            onExpandedChanged = { _, _, _, _ -> }
        )
    }
}

