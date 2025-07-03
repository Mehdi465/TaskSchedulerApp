package com.example.taskscheduler.ui.HelperDialog

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable

@Composable
fun RemoveSessionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){
    Dialog(
        onDismissRequest = onDismiss
    ){
        Text("Would you like to remove this session, it would be impossible to recover it later")

        Row(){
            Button(onClick = onConfirm){
                Text("Yes")
            }
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    }
}