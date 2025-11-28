package com.example.taskscheduler.ui.helperComposable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.taskscheduler.R

@Composable
fun ValidateOrDeleteSession(
    onValidate: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
){
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.confirm_delete_session))
            Row(
                horizontalArrangement = Arrangement.End
            ){
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onValidate,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green
                    )
                ) {
                    Text(stringResource(R.string.confirm))
                }
            }
        }
    }
}