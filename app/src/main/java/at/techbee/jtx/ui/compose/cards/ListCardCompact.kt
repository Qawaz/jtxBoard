/*
 * Copyright (c) Techbee e.U.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package at.techbee.jtx.ui.compose.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.techbee.jtx.database.*
import at.techbee.jtx.database.relations.ICal4ListWithRelatedto
import at.techbee.jtx.database.views.ICal4List
import at.techbee.jtx.flavored.BillingManager
import at.techbee.jtx.ui.compose.elements.ListStatusBar
import at.techbee.jtx.ui.theme.JtxBoardTheme
import at.techbee.jtx.ui.theme.Typography
import at.techbee.jtx.util.DateTimeUtils


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ListCardCompact(
    iCalObjectWithRelatedto: ICal4ListWithRelatedto,
    subtasks: List<ICal4List>,
    modifier: Modifier = Modifier,
    onProgressChanged: (itemId: Long, newPercent: Int, isLinkedRecurringInstance: Boolean) -> Unit,
    goToView: (itemId: Long) -> Unit,
    goToEdit: (itemId: Long) -> Unit
    ) {

    val iCalObject = iCalObjectWithRelatedto.property
    val statusBarVisible by remember {
        mutableStateOf(
            iCalObject.numAttendees > 0 || iCalObject.numAttachments > 0 || iCalObject.numComments > 0 || iCalObject.numResources > 0 || iCalObject.numAlarms > 0 || iCalObject.numSubtasks > 0 || iCalObject.numSubnotes > 0
                    || iCalObject.isReadOnly || iCalObject.uploadPending || iCalObject.url?.isNotEmpty() == true || iCalObject.location?.isNotEmpty() == true
                    || iCalObject.contact?.isNotEmpty() == true || iCalObject.isRecurringInstance || iCalObject.isRecurringOriginal || iCalObject.isLinkedRecurringInstance
                    || iCalObject.priority in 1..9 || iCalObject.status in listOf(
                StatusJournal.CANCELLED.name,
                StatusJournal.DRAFT.name,
                StatusTodo.CANCELLED.name
            )
                    || iCalObject.classification in listOf(
                Classification.CONFIDENTIAL.name,
                Classification.PRIVATE.name
            )
        )
    }
    val color = iCalObject.colorItem?.let { Color(it) } ?: iCalObject.colorCollection?.let { Color(it) } ?: Color.Transparent

        Row(modifier = modifier.height(IntrinsicSize.Min)) {

            Box(
                modifier = Modifier.width(10.dp).alpha(0.5f).fillMaxHeight().background(color, RoundedCornerShape(8.dp))
            )

            Column(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp).fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {

                        if (iCalObject.categories?.isNotEmpty() == true
                            || (iCalObject.module == Module.TODO.name && iCalObject.due != null)
                            || (iCalObject.module == Module.JOURNAL.name && iCalObject.dtstart != null)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                iCalObject.categories?.let {
                                    Text(
                                        it,
                                        style = Typography.labelMedium,
                                        fontStyle = FontStyle.Italic,
                                        modifier = Modifier.padding(end = 16.dp).weight(1f),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                if(iCalObject.module == Module.JOURNAL.name && iCalObject.dtstart != null) {
                                    Text(
                                        DateTimeUtils.convertLongToShortDateTimeString(iCalObject.dtstart, iCalObject.dtstartTimezone),
                                        style = Typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                if(iCalObject.module == Module.TODO.name && iCalObject.due != null) {
                                    Text(
                                        iCalObject.getDueTextInfo(LocalContext.current) ?: "",
                                        style = Typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {

                            if (iCalObject.summary?.isNotBlank() == true)
                                Text(
                                    text = iCalObject.summary ?: "",
                                    textDecoration = if (iCalObject.status == StatusJournal.CANCELLED.name || iCalObject.status == StatusTodo.CANCELLED.name) TextDecoration.LineThrough else TextDecoration.None,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )

                        }

                        if (iCalObject.description?.isNotBlank() == true)
                            Text(
                                text = iCalObject.description ?: "",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                    }


                    if (iCalObject.module == Module.TODO.name)
                        Checkbox(
                            checked = iCalObject.percent == 100,
                            enabled = !iCalObject.isReadOnly,
                            onCheckedChange = {
                                onProgressChanged(
                                    iCalObject.id,
                                    if (it) 100 else 0,
                                    iCalObject.isLinkedRecurringInstance
                                )
                            }
                        )
                }

                AnimatedVisibility(visible = statusBarVisible) {
                    ListStatusBar(
                        numAttendees = iCalObject.numAttendees,
                        numAttachments = iCalObject.numAttachments,
                        numComments = iCalObject.numComments,
                        numResources = iCalObject.numResources,
                        numAlarms = iCalObject.numAlarms,
                        numSubtasks = iCalObject.numSubtasks,
                        numSubnotes = iCalObject.numSubnotes,
                        isReadOnly = iCalObject.isReadOnly,
                        uploadPending = iCalObject.uploadPending,
                        hasURL = iCalObject.url?.isNotBlank() == true,
                        hasLocation = iCalObject.location?.isNotBlank() == true,
                        hasContact = iCalObject.contact?.isNotBlank() == true,
                        isRecurringOriginal = iCalObject.isRecurringOriginal,
                        isRecurringInstance = iCalObject.isRecurringInstance,
                        isLinkedRecurringInstance = iCalObject.isLinkedRecurringInstance,
                        component = iCalObject.component,
                        status = iCalObject.status,
                        classification = iCalObject.classification,
                        priority = iCalObject.priority,
                        modifier = Modifier.padding(top = 4.dp)

                    )
                }

                Column(modifier = Modifier.padding(top = 4.dp)) {
                    subtasks.forEach { subtask ->

                        SubtaskCardCompact(
                            subtask = subtask,
                            onProgressChanged = onProgressChanged,
                            modifier = Modifier
                                .padding(start = 8.dp, end = 8.dp)
                            .combinedClickable(
                                onClick = { goToView(subtask.id) },
                                onLongClick = {
                                    if (!subtask.isReadOnly && BillingManager.getInstance()?.isProPurchased?.value == true)
                                        goToEdit(subtask.id)
                                }
                            )
                        )

                        if (subtask.id != subtasks.last().id)
                            Divider(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                thickness = 1.dp,
                                modifier = Modifier.alpha(0.25f)
                            )
                    }
                }
            }
        }
}

@Preview(showBackground = true)
@Composable
fun ListCardCompact_JOURNAL() {
    JtxBoardTheme {

        val icalobject = ICal4ListWithRelatedto.getSample().apply {
            property.dtstart = System.currentTimeMillis()
            property.colorItem = Color.Blue.toArgb()
            property.colorCollection = Color.Magenta.toArgb()
        }
        ListCardCompact(
            icalobject,
            emptyList(),
            onProgressChanged = { _, _, _ -> },
            goToView = { },
            goToEdit = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ListCardCompact_NOTE() {
    JtxBoardTheme {

        val icalobject = ICal4ListWithRelatedto.getSample().apply {
            property.component = Component.VJOURNAL.name
            property.module = Module.NOTE.name
            property.dtstart = null
            property.dtstartTimezone = null
            property.status = StatusJournal.CANCELLED.name
        }
        ListCardCompact(
            icalobject,
            emptyList(),
            onProgressChanged = { _, _, _ -> },
            goToView = { },
            goToEdit = { }
        )    }
}

@Preview(showBackground = true)
@Composable
fun ListCardCompact_TODO() {
    JtxBoardTheme {

        val icalobject = ICal4ListWithRelatedto.getSample().apply {
            property.component = Component.VTODO.name
            property.module = Module.TODO.name
            property.percent = 89
            property.status = StatusTodo.`IN-PROCESS`.name
            property.classification = Classification.CONFIDENTIAL.name
            property.dtstart = System.currentTimeMillis()
            property.due = System.currentTimeMillis()
            property.summary = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
        }
        ListCardCompact(
            icalobject,
            subtasks = listOf(icalobject.property, icalobject.property),
            onProgressChanged = { _, _, _ -> },
            goToView = { },
            goToEdit = { }
        )
    }
}