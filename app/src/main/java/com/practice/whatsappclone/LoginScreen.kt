package com.practice.whatsappclone

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity

    var currentStep by remember { mutableStateOf("phone") }
    var selectedCountryCode by remember { mutableStateOf("+91") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF075E54)), // WhatsApp green
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        Text(
            text = "WhatsApp",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Simple. Secure. Reliable messaging.",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(40.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (currentStep == "phone") {
                    Text(
                        "Enter your phone number",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF075E54),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = selectedCountryCode,
                            onValueChange = { selectedCountryCode = it },
                            singleLine = true,
                            modifier = Modifier
                                .width(80.dp)
                                .testTag("countryCodeInput"),
                            label = { Text("Code") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = viewModel.phoneNumber,
                            onValueChange = { viewModel.phoneNumber = it },
                            label = { Text("Phone number") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("phoneInput")
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            activity?.let {
                                viewModel.phoneNumber = selectedCountryCode + viewModel.phoneNumber
                                viewModel.startPhoneAuth(it) {
                                    currentStep = "otp"
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("nextBtn"),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(text = "NEXT", color = Color.White, fontSize = 16.sp)
                    }
                } else if (currentStep == "otp") {
                    Text(
                        "Verify your OTP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF075E54),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = viewModel.otp,
                        onValueChange = { viewModel.otp = it },
                        label = { Text("Enter OTP") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otpInput")
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            viewModel.verifyOtp {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("verifyBtn"),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(text = "VERIFY", color = Color.White, fontSize = 16.sp)
                    }
                }
                if (viewModel.isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        color = Color(0xFF075E54),
                        modifier = Modifier.testTag("loginLoading")
                    )
                }
                viewModel.errorMsg?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = it, color = Color.Red, modifier = Modifier.testTag("loginError"))
                }
            }
        }
    }
}
