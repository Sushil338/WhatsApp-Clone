package com.practice.whatsappclone

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity


    var currentStep by remember { mutableStateOf("phone") }

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(modifier = Modifier.height(40.dp))
        Text("Enter Phone Number")
        OutlinedTextField(
            value = viewModel.phoneNumber,
            onValueChange = { viewModel.phoneNumber = it },
            label = { Text("Phone Number (+91 XXX...)") }
        )
        Button(onClick = {
            activity?.let {
                viewModel.startPhoneAuth(it) {
                    currentStep = "otp"
                }
            }
        }) {
            Text(text = "Send OTP")
        }


        if(currentStep == "otp"){
            OutlinedTextField(
                value = viewModel.otp,
                onValueChange = {viewModel.otp = it},
                label = { Text("Enter OTP") }
            )
            Button(onClick = {viewModel.verifyOtp { navController.navigate("home") }}) {
                Text(text = "Verify OTP")
            }
        }

        if(viewModel.isLoading) CircularProgressIndicator()
        viewModel.errorMsg?.let {Text(text =  it, color = Color.Red)}
    }
}