/*
 * Copyright (c) Techbee e.U.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package at.techbee.jtx.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.techbee.jtx.database.ICalObject
import at.techbee.jtx.ui.theme.JtxBoardTheme
import at.techbee.jtx.util.DateTimeUtils
import java.util.*


@Composable
fun VerticalDateBlock(datetime: Long, timezone: String?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = DateTimeUtils.convertLongToDayString(datetime, timezone),
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp
        )
        Text(
            DateTimeUtils.convertLongToMonthString(datetime, timezone),
            fontSize = 12.sp
        )

        Text(
            DateTimeUtils.convertLongToYearString(datetime, timezone),
            fontSize = 12.sp
        )
        if (timezone != ICalObject.TZ_ALLDAY)
            Text(
                DateTimeUtils.convertLongToTimeString(datetime, timezone),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        if (timezone != ICalObject.TZ_ALLDAY && timezone?.isNotEmpty() == true && TimeZone.getTimeZone(timezone).getDisplayName(true, TimeZone.SHORT) != null)
            Text(
                TimeZone.getTimeZone(timezone).getDisplayName(true, TimeZone.SHORT),
                fontSize = 12.sp
            )
    }
}

@Preview(showBackground = true)
@Composable
fun DateBlock_Preview_Allday() {
    JtxBoardTheme {
        VerticalDateBlock(System.currentTimeMillis(), ICalObject.TZ_ALLDAY)
    }
}

@Preview(showBackground = true)
@Composable
fun DateBlock_Preview_WithTime() {
    JtxBoardTheme {
        VerticalDateBlock(System.currentTimeMillis(), null)
    }
}

@Preview(showBackground = true)
@Composable
fun DateBlock_Preview_WithTimezone() {
    JtxBoardTheme {
        VerticalDateBlock(System.currentTimeMillis(), "Europe/Vienna")
    }
}

@Preview(showBackground = true)
@Composable
fun DateBlock_Preview_WithTimezone2() {
    JtxBoardTheme {
        VerticalDateBlock(System.currentTimeMillis(), "Africa/Addis_Ababa")
    }
}
