/*
 * Copyright (c) Techbee e.U.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package at.techbee.jtx.ui.compose.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.NewLabel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.techbee.jtx.R
import at.techbee.jtx.database.properties.Category
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DetailsCardCategories(
    categories: MutableState<List<Category>>,
    isEditMode: MutableState<Boolean>,
    allCategories: List<Category>,
    onCategoriesUpdated: () -> Unit,
    modifier: Modifier = Modifier
) {

    val headline = stringResource(id = R.string.categories)
    val newCategory = remember { mutableStateOf("") }

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()


    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.Label, headline)
                Text(headline, style = MaterialTheme.typography.titleMedium)
            }

            AnimatedVisibility(categories.value.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                ) {
                    categories.value.asReversed().forEach { category ->
                        InputChip(
                            onClick = {
                                categories.value = categories.value.filter { it != category }
                            },
                            label = { Text(category.text) },
                            /* leadingIcon = {
                                Icon(
                                    Icons.Outlined.Label,
                                    stringResource(id = R.string.categories)
                                )
                            }, */
                            trailingIcon = {
                                if (isEditMode.value)
                                    Icon(Icons.Outlined.Close, stringResource(id = R.string.delete))
                            },
                            selected = false
                        )
                    }
                }
            }

            AnimatedVisibility(newCategory.value.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                ) {

                    if(categories.value.none { existing -> existing.text == newCategory.value }) {
                        InputChip(
                            onClick = {
                                categories.value = categories.value.plus(Category(text = newCategory.value))
                                newCategory.value = ""
                            },
                            label = { Text(newCategory.value) },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.NewLabel,
                                    stringResource(id = R.string.add)
                                )
                            },
                            selected = false,
                            modifier = Modifier.onPlaced {
                                coroutineScope.launch {
                                    bringIntoViewRequester.bringIntoView()
                                }
                            }
                        )
                    }

                    allCategories.filter { all -> all.text.lowercase().contains(newCategory.value.lowercase()) && categories.value.none { existing -> existing.text.lowercase() == all.text.lowercase() }}
                        .forEach { category ->
                            InputChip(
                                onClick = {
                                    categories.value = categories.value.plus(Category(text = category.text))
                                    newCategory.value = ""
                                },
                                label = { Text(category.text) },
                                leadingIcon = {
                                        Icon(
                                            Icons.Outlined.NewLabel,
                                            stringResource(id = R.string.add)
                                        )
                                },
                                selected = false
                            )
                        }
                }
            }

            Crossfade(isEditMode) {
                if (it.value) {

                    OutlinedTextField(
                        value = newCategory.value,
                        leadingIcon = { Icon(Icons.Outlined.Label, headline) },
                        trailingIcon = {
                            if (newCategory.value.isNotEmpty()) {
                                IconButton(onClick = { newCategory.value = "" }) {
                                    Icon(
                                        Icons.Outlined.Close,
                                        stringResource(id = R.string.delete)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        label = { Text(headline) },
                        onValueChange = { newCategoryName ->
                            newCategory.value = newCategoryName
                            /* TODO */
                        },
                        colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
                        modifier = Modifier.fillMaxWidth().bringIntoViewRequester(bringIntoViewRequester),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if(newCategory.value.isNotEmpty() && categories.value.none { existing -> existing.text == newCategory.value } )
                                categories.value = categories.value.plus(Category(text = newCategory.value))
                            newCategory.value = ""
                        })
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsCardCategories_Preview() {
    MaterialTheme {
        DetailsCardCategories(
            categories = remember { mutableStateOf(listOf(Category(text = "asdf"))) },
            isEditMode = remember { mutableStateOf(false) },
            allCategories = listOf(Category(text = "category1"), Category(text = "category2"), Category(text = "Whatever")),
            onCategoriesUpdated = { /*TODO*/ }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DetailsCardCategories_Preview_edit() {
    MaterialTheme {
        DetailsCardCategories(
            categories = remember { mutableStateOf(listOf(Category(text = "asdf"))) },
            isEditMode = remember { mutableStateOf(true) },
            allCategories = listOf(Category(text = "category1"), Category(text = "category2"), Category(text = "Whatever")),
            onCategoriesUpdated = { /*TODO*/ }
        )
    }
}