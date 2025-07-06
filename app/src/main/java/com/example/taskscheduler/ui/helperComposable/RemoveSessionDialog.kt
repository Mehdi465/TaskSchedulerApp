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
import androidx.compose.ui.unit.dp


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
            Text("Would you like to remove this session, it would be impossible to recover it later")

            Row() {
                Button(onClick = onConfirm) {
                    Text("Yes")
                }

                Spacer(modifier = Modifier.padding(10.dp))

                Button(onClick = onDismiss) {
                    Text("No")
                }
            }
        }
    }
}