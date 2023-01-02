package net.group.androidlocalmessanger.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.talhafaki.composablesweettoast.util.SweetToastUtil
import net.group.androidlocalmessanger.data.DataOrException
import net.group.androidlocalmessanger.module.ResponseCode
import net.group.androidlocalmessanger.module.User
import net.group.androidlocalmessanger.module.UserWithGroups
import net.group.androidlocalmessanger.ui.auth.UserViewModel
import net.group.androidlocalmessanger.ui.component.ActivityView
import net.group.androidlocalmessanger.ui.component.LoadingErrorDataView
import net.group.androidlocalmessanger.ui.component.OutlinedInput
import net.group.androidlocalmessanger.ui.theme.AndroidLocalMessangerTheme
import net.group.androidlocalmessanger.utils.FileUtil
import java.io.File

@Composable
fun SettingScreen(navController: NavController, userViewModel: UserViewModel) {
    LaunchedEffect(true) {
        userViewModel.resetUpdatedUser()
    }
    val user = userViewModel.userState.value.data!!.user
    val name = remember { mutableStateOf(user.name) }
    val userName = remember { mutableStateOf(user.userName ?: "") }
    val password = remember { mutableStateOf(user.password) }
    val profilePhotoPath = remember { mutableStateOf(user.profilePath) }
    val phoneNumber = remember { mutableStateOf(user.phoneNumber ?: "") }
    val showProfile = remember { mutableStateOf(user.showProfile) }
    val showPhoneNumber = remember { mutableStateOf(user.showPhoneNumber) }

    val email = user.userEmail

    ActivityView(tittle = "Setting") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 10.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ProfileImage(profilePhotoPath, userViewModel)
                val modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                OutlinedInput(
                    modifier = modifier,
                    valueState = name,
                    label = "Name"
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = {},
                    readOnly = true,
                    modifier = modifier,
                    label = { Text("Email") }
                )
                OutlinedInput(
                    modifier = modifier,
                    valueState = password,
                    label = "Password"
                )
                OutlinedInput(
                    modifier = modifier,
                    valueState = userName,
                    label = "Username"
                )

                LoadingErrorDataView(
                    dataOrException = userViewModel.updatedUserState.value,
                    onSuccessContent = {
                        SweetToastUtil.SweetSuccess("User Updated")
                        LaunchedEffect(true) {
                            navController.popBackStack()
                        }
                    })

                OutlinedInput(
                    modifier = modifier,
                    valueState = phoneNumber,
                    label = "Phone Number"
                )
                LabelSwitch(
                    label = "Show Phone Number",
                    checked = showPhoneNumber.value,
                    onCheckedChange = { showPhoneNumber.value = !showPhoneNumber.value },
                    modifier = modifier,
                )
                LabelSwitch(
                    label = "Show Profile Photo",
                    checked = showProfile.value,
                    onCheckedChange = { showProfile.value = !showProfile.value },
                    modifier = modifier,
                )
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                onClick = {
                    val newUser = User(email, password.value, name.value)
                    newUser.showProfile = showProfile.value
                    newUser.profilePath = profilePhotoPath.value
                    newUser.showPhoneNumber = showPhoneNumber.value
                    newUser.userName = userName.value
                    newUser.phoneNumber = phoneNumber.value
                    userViewModel.updateUser(newUser)
                }) {
                Text("Save")
            }

        }

    }
}

@Composable
fun LabelSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SwitchColors = SwitchDefaults.colors()
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = Modifier.weight(1f), text = label)
        Switch(
            checked, onCheckedChange, Modifier, enabled, interactionSource, colors
        )
    }
}

@Composable
fun ProfileImage(profilePhotoPath: MutableState<String?>, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val pickPictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { imageUri ->
        if (imageUri != null) {
            val imgFile = FileUtil.from(context, imageUri)
            Log.d("Screen", "ProfileImage: ${imgFile.path}")
            profilePhotoPath.value = imgFile?.path
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            pickPictureLauncher.launch("image/*")
        } else {
            //SweetToastUtil.SweetError("need storage permission")
        }
    }
    val imageModifier = Modifier.size(100.dp).clickable {

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                pickPictureLauncher.launch("image/*")
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
    Card(shape = CircleShape, elevation = 0.dp, backgroundColor = MaterialTheme.colors.background) {
        if (profilePhotoPath.value == null)
            Image(
                modifier = imageModifier,
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "ProfilePhoto"
            )
        else {
            Log.d("Screen", "ProfileImage: ${profilePhotoPath.value}")
            if (File(profilePhotoPath.value!!).exists()) {

                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(profilePhotoPath.value)
                            .build()
                    ),
                    contentDescription = "ProfilePhoto",
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier
                )
            } else {
                //  userViewModel.getProfilePhoto()

            }

        }

    }


}

@Preview(showBackground = true)
@Composable
fun SettingPreview() {
    AndroidLocalMessangerTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            val userViewModel = UserViewModel()
            userViewModel.userState.value = DataOrException(
                UserWithGroups(
                    User("ffff", "123", "f"), listOf()
                ), false, ResponseCode.OK
            )
            SettingScreen(rememberNavController(), userViewModel)
        }
    }
}