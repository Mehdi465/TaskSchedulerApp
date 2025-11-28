package com.example.taskscheduler.ui.helperComposable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.taskscheduler.R


@Composable
fun RemoveSessionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){
    Dialog(
        onDismissRequest = onDismiss
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.remove_this_session))

            Row() {
                Button(onClick = onConfirm) {
                    Text(stringResource(R.string.yes))
                }

                Spacer(modifier = Modifier.padding(10.dp))

                Button(onClick = onDismiss) {
                    Text(stringResource(R.string.no))
                }
            }
        }
    }
}