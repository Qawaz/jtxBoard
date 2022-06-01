/*
 * Copyright (c) Techbee e.U.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package at.techbee.jtx.ui.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.preference.PreferenceManager
import at.techbee.jtx.database.StatusTodo
import at.techbee.jtx.database.properties.Reltype
import at.techbee.jtx.database.relations.ICal4ListWithRelatedto
import at.techbee.jtx.database.views.ICal4List
import at.techbee.jtx.ui.IcalListViewModel
import at.techbee.jtx.ui.SettingsFragment


@Composable
fun ListScreen(listLive: LiveData<List<ICal4ListWithRelatedto>>, subtasksLive: LiveData<List<ICal4List>>, scrollOnceId: MutableLiveData<Long?>, navController: NavController, model: IcalListViewModel) {

    val list by listLive.observeAsState(emptyList())
    val subtasks by subtasksLive.observeAsState(emptyList())

    val scrollId by scrollOnceId.observeAsState(null)
    val listState = rememberLazyListState()


    //load settings
    val settings = PreferenceManager.getDefaultSharedPreferences(LocalContext.current)
    val settingShowSubtasks = settings.getBoolean(SettingsFragment.SHOW_SUBTASKS_IN_LIST, true)
    val settingShowAttachments = settings.getBoolean(SettingsFragment.SHOW_ATTACHMENTS_IN_LIST, true)
    val settingShowProgressMaintasks = settings.getBoolean(SettingsFragment.SHOW_PROGRESS_FOR_MAINTASKS_IN_LIST, false)
    val settingShowProgressSubtasks = settings.getBoolean(SettingsFragment.SHOW_PROGRESS_FOR_SUBTASKS_IN_LIST, true)

    val excludeDone by model.isExcludeDone.observeAsState(false)


    if(scrollId != null) {
        LaunchedEffect(list) {
            val index = list.indexOfFirst { iCalObject -> iCalObject.property.id == scrollId }
            if(index > -1) {
                listState.animateScrollToItem(index)
                scrollOnceId.postValue(null)
            }
        }
    }



    LazyColumn(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp),
        state = listState,
    ) {
        items(
            items = list,
            key = { item -> item.property.id }
        ) { iCalObject ->

            var currentSubtasks = subtasks.filter { subtask ->
                iCalObject.relatedto?.any { relatedto ->
                    relatedto.linkedICalObjectId == subtask.id && relatedto.reltype == Reltype.CHILD.name } == true
            }
            if(excludeDone)   // exclude done if applicable
                currentSubtasks = currentSubtasks.filter { subtask -> subtask.percent != 100 }
            if(model.searchStatusTodo.isNotEmpty()) // exclude filtered if applicable
                currentSubtasks = currentSubtasks.filter { subtask -> model.searchStatusTodo.contains(StatusTodo.getFromString(subtask.status)) }

            ICalObjectListCard(
                iCalObject,
                currentSubtasks,
                navController,
                settingShowSubtasks = settingShowSubtasks,
                settingShowAttachments = settingShowAttachments,
                settingShowProgressMaintasks = settingShowProgressMaintasks,
                settingShowProgressSubtasks = settingShowProgressSubtasks,
                onEditRequest = { id -> model.postDirectEditEntity(id) },
                onProgressChanged = { itemId, newPercent, isLinkedRecurringInstance -> model.updateProgress(itemId, newPercent, isLinkedRecurringInstance)  }
            )
        }
    }
}

