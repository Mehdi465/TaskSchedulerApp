package com.example.taskscheduler.ui.helperComposable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun DeleteTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){
    Dialog(
        onDismissRequest = onDismiss,
    ){
        Column() {
            Text("Are you sure you want to delete this task? All records and data will be lost.")
            Row(){
                Button(
                    onClick = onConfirm
                ) {
                    Text("Yes")
                }
                Button(
                    onClick = onDismiss
                ) {
                    Text("No")
                }
            }
        }

    }
}
