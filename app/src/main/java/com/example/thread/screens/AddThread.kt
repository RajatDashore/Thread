package com.example.thread.screens

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.thread.R
import com.example.thread.application.ThreadApplication
import com.example.thread.utils.SharedPref
import com.example.thread.utils.sendBroadCastNotification
import com.example.thread.viewModel.AddThreadViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AddThread(navHostController: NavHostController) {
    val context = LocalContext.current
    val threadViewModel: AddThreadViewModel = viewModel()

    val isPosted by threadViewModel.isPosted.observeAsState(false)
    var thread by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val isUploading by threadViewModel.isUploading.observeAsState(false)
    var count = remember { mutableStateOf(1) }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                launcher.launch("image/*")
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_IMAGES
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // Reset state after post
    LaunchedEffect(isPosted) {
        if (isPosted) {
            thread = ""
            imageUri = null
            Toast.makeText(context, "Thread added", Toast.LENGTH_SHORT).show()
            navHostController.popBackStack()
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val (headerRow, inputSection, attachMedia, button, imageBox) = createRefs()

        // 🔹 Header (close + title)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.constrainAs(headerRow) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_close_24),
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        navHostController.popBackStack()
                    }
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Add Thread",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        // 🔹 Profile + Text Input
        Row(
            modifier = Modifier.constrainAs(inputSection) {
                top.linkTo(headerRow.bottom, margin = 24.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            verticalAlignment = Alignment.Top
        ) {
            // Profile Image
            AsyncImage(
                model = SharedPref.getImage(context)?.takeIf { it.isNotBlank() }
                    ?: R.drawable.baseline_person_24,
                imageLoader = ThreadApplication.imageLoader,
                contentDescription = "Logo",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Username
                Text(
                    text = SharedPref.getUserName(context)?.ifBlank { "Guest" } ?: "Guest",
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Thread input
                basicTextFieldWithHint(
                    hint = "What's on your mind?",
                    value = thread.orEmpty(),
                    onValuesChange = { thread = it },
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                )
            }
        }

        // 🔹 Attached Image
        if (imageUri != null) {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .constrainAs(imageBox) {
                        top.linkTo(inputSection.bottom, margin = 12.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .height(250.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.Gray.copy(alpha = 0.2f))
            ) {
                AsyncImage(
                    model = imageUri,
                    imageLoader = ThreadApplication.imageLoader,
                    contentDescription = "Attached image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove Image",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable { imageUri = null }
                )
            }
        } else {
            Icon(
                painter = painterResource(id = R.drawable.baseline_attach_file_24),
                contentDescription = "Attach file",
                tint = Color.Gray,
                modifier = Modifier
                    .constrainAs(attachMedia) {
                        top.linkTo(inputSection.bottom, margin = 16.dp)
                        start.linkTo(inputSection.start)
                    }
                    .size(26.dp)
                    .clickable {
                        val isGranted = ContextCompat.checkSelfPermission(
                            context,
                            permissionToRequest
                        ) == PackageManager.PERMISSION_GRANTED
                        if (isGranted) {
                            launcher.launch("image/*")
                        } else {
                            permissionLauncher.launch(permissionToRequest)
                        }
                    }
            )
        }


        // 🔹 Post Button
        TextButton(
            onClick = {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val safeThread = thread
                val safeUserName = SharedPref.getUserName(context)?.ifBlank { "Guest" } ?: "Guest"

                if (imageUri != null && safeThread.isNotBlank() && currentUser != null) {
                    if (count.value == 1) {
                        threadViewModel.uploadThread(
                            imageUri!!,
                            safeUserName,
                            safeThread,
                            currentUser.uid
                        )
                        sendBroadCastNotification("Thread", safeUserName)
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Please select an image and write thread",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                ++count.value
            },
            enabled = !isUploading,
            modifier = Modifier.constrainAs(button) {
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, margin = 16.dp)
            }
        ) {
            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "Post",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Composable
fun basicTextFieldWithHint(
    hint: String,
    value: String,
    onValuesChange: (String) -> Unit,
    modifier: Modifier
) {
    val textColor = LocalContentColor.current.copy(alpha = 1f)
    Box(modifier.padding(1.dp)) {
        if (value.isEmpty()) {
            Text(hint, style = LocalTextStyle.current.copy(color = textColor.copy(alpha = 0.6f)))
        }
        BasicTextField(
            value =
                value,
            onValueChange = { onValuesChange(it.orEmpty()) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = textColor)
        )
    }
}