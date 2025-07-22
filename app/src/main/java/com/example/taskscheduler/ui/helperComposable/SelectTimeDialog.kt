package com.example.taskscheduler.ui.helperComposable

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.taskscheduler.R
import com.example.taskscheduler.data.Task
import kotlin.time.Duration

@Composable
fun SelectTimeDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (Duration) -> Unit,
    themeColor : Color
){
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(){
            InfiniteTimePickerWheel(
                themeColor = themeColor,
                initialHour = 12,
                initialMinute = 0,
                onTimeSelected = { hour, minute ->}
            )

            Row() {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    ),
                    onClick = {}
                ) {
                    Text(
                        text = "Change"
                    )
                }

                Spacer(modifier = Modifier.padding(8.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    ),
                    onClick = onDismiss
                ) {
                    Text(
                        text = stringResource(R.string.cancel)
                    )
                }
            }
        }
    }
}


