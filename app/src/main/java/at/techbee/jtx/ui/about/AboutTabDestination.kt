/*
 * Copyright (c) Techbee e.U.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package at.techbee.jtx.ui.about

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.ui.graphics.vector.ImageVector
import at.techbee.jtx.R

sealed class AboutTabDestination (
    val tabIndex: Int,
    val titleResource: Int,
    val icon: ImageVector,
    //val badgeCount: Int?
) {
    object Jtx: AboutTabDestination(
        tabIndex = 0,
        titleResource = R.string.about_tabitem_jtx,
        icon = Icons.Outlined.Info,
    )
    object Releasenotes: AboutTabDestination(
        tabIndex = 1,
        titleResource = R.string.about_tabitem_releasenotes,
        icon = Icons.Outlined.NewReleases,
    )
    object Libraries: AboutTabDestination(
        tabIndex = 2,
        titleResource = R.string.about_tabitem_libraries,
        icon = Icons.Outlined.DataObject,
    )
    object Translations: AboutTabDestination(
        tabIndex = 3,
        titleResource = R.string.about_tabitem_translations,
        icon = Icons.Outlined.Translate,
    )
    object Contributors: AboutTabDestination(
        tabIndex = 4,
        titleResource = R.string.about_tabitem_contributors,
        icon = Icons.Outlined.Handshake,
    )

}
