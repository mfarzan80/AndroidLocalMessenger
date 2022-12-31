package net.group.androidlocalmessanger.ui.component


import android.util.Log
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.*
import kotlinx.coroutines.GlobalScope.coroutineContext
import net.group.androidlocalmessanger.data.DataOrException
import net.group.androidlocalmessanger.module.ResponseCode
import net.group.androidlocalmessanger.ui.theme.getStringByResponse

@Composable
fun <T> LoadingErrorDataView(
    modifier: Modifier = Modifier,
    dataOrException: DataOrException<T, Boolean, ResponseCode>,
    onSuccessContent: @Composable () -> Unit
) {
    val loading = remember { mutableStateOf(false) }
    val loadingJob = remember { mutableStateOf<Job?>(null) }
    if (loading.value) {
        Dialog(
            onDismissRequest = { },
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
    Box(modifier = modifier) {
        if (dataOrException.data != null) {
            loadingJob.value?.cancel()
            loading.value = false
            onSuccessContent()
        } else if (dataOrException.loading == true) {
            LaunchedEffect(key1 = dataOrException.loading) {

                loadingJob.value = CoroutineScope(Dispatchers.Main).launch {
                    delay(50)
                    loading.value = true
                    coroutineContext.job.join()
                }
            }

        } else if (dataOrException.info != null) {
            loadingJob.value?.cancel()
            loading.value = false
            Text(
                text = getStringByResponse(dataOrException.info!!)!!,
                color = MaterialTheme.colors.error
            )
        } else
            Text(text = " ")
    }
}


@Composable
fun ActivityView(
    modifier: Modifier = Modifier,
    tittle: String = "Android Local Messenger",
    floatingActionButton: @Composable () -> Unit = {},
    topAppBar: @Composable () -> Unit = {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp),
            text = tittle,
            style = MaterialTheme.typography.h6
        )
    },
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier, topBar = {
            TopAppBar(contentColor = MaterialTheme.colors.onPrimary) {
                topAppBar()
            }
        },
        floatingActionButton = floatingActionButton
    ) {
        content()
    }
}
