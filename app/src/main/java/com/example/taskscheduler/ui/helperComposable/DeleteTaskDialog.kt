package com.example.taskscheduler.ui.helperComposable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun DeleteTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){
    Dialog(
        onDismissRequest = onDismiss,
    ){
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Are you sure you want to delete this task? All records and data will be lost.")
            Row(
                horizontalArrangement = Arrangement.End
            ){
                Button(
                    onClick = onConfirm
                ) {
                    Text("Yes")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onDismiss
                ) {
                    Text("No")
                }
            }
        }

    }
}
