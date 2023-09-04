package app.vcampus.client.scene

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import app.vcampus.client.scene.components.pageTitle
import app.vcampus.client.scene.components.shadowCustom
import app.vcampus.client.viewmodel.LoginViewModel
import moe.tlaster.precompose.viewmodel.viewModel


@ExperimentalMaterialApi
@Composable
fun LoginScene(
        onLogin: () -> Unit,
) {
    val viewModel = viewModel(LoginViewModel::class, listOf()) {
        LoginViewModel()
    }

    var username by remember { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val loginState by viewModel.loginState
    val errorMessage by viewModel.errorMessage
    val snackbarHostState = remember{SnackbarHostState()}
    var textVisible by rememberSaveable{ mutableStateOf(false) }

    when (loginState) {
        true -> onLogin()
        false -> {}
    }

    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank()) {
            //snackbarHostState.showSnackbar(errorMessage.toString())
            viewModel.errorMessage.value = null // Clear the error message after displaying it
            password = "" // Clear the password field
            textVisible=true
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(Modifier.size(1064.dp, 600.dp).shadowCustom(
                blurRadius = 10.dp).background(Color.White)) {
            Row {
                Image(
                        painterResource("seu-side.png"),
                        contentDescription = "SEU Sidebar",
                        modifier = Modifier.width(600.dp).height(600.dp)
                )

                Row(
                        modifier = Modifier
                                .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                ) {
                    Box(
                            modifier = Modifier
                                    .requiredWidth(328.dp)
                                    .fillMaxHeight()
                    ) {
                        Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(
                                        16.dp, Alignment.CenterVertically)
                        ) {
                            Column {
                                pageTitle("统一验证登录", "VCampus")
//                                Text(
//                                    text = "统一登录验证1",
//                                    style = TextStyle(
//                                        fontSize = 34.sp,
//                                        lineHeight = 36.sp,
//                                        fontWeight = FontWeight(700),
//                                        color = Color(0xDE000000),
//                                        textAlign = TextAlign.Start,
//                                        fontFamily = sarasaUiSc
//                                    )
//                                )
//
//                                Text(
//                                    text = "VCampus1",
//                                    style = TextStyle(
//                                        fontSize = 14.sp,
//                                        lineHeight = 20.sp,
//                                        fontWeight = FontWeight(400),
//                                        color = Color(0x99000000),
//                                        letterSpacing = 0.25.sp,
//                                        fontFamily = sarasaUiSc
//                                    )
//                                )
                            }

                            Column(
                                    verticalArrangement = Arrangement.spacedBy(
                                            8.dp, Alignment.CenterVertically)
                            ) {
                                OutlinedTextField(
                                        value = username,
                                        onValueChange = { username = it },
                                        label = { Text("一卡通号") },
                                        modifier = Modifier.fillMaxWidth().onPreviewKeyEvent {
                                            event:KeyEvent->
                                            if(event.type== KeyEventType.KeyDown&&event.key==Key.Enter){
                                                viewModel.login(username, password)
                                                true
                                            }else{false}
                                        }
                                )

                                OutlinedTextField(
                                        value = password,
                                        onValueChange = {
                                            password = it
                                            textVisible=false
                                                        },
                                        label = { Text("密码") },
                                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        modifier = Modifier.fillMaxWidth().onPreviewKeyEvent {
                                                                                      event:KeyEvent->
                                                                                      if(event.type== KeyEventType.KeyDown&&event.key==Key.Enter){
                                                                                          viewModel.login(username, password)
                                                                                          true
                                                                                      }else{false}
                                        },
                                        trailingIcon = {
                                            val image = if (passwordVisible)
                                                Icons.Filled.Visibility
                                            else Icons.Filled.VisibilityOff

                                            // Please provide localized description for accessibility services
                                            val description = if (passwordVisible) "隐藏密码" else "显示密码"

                                            IconButton(
                                                    onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(image, description)
                                            }
                                        }
                                )

//                                SnackbarHost(
//                                    hostState = snackbarHostState,
//                                   // modifier = Modifier.align(Alignment.BottomCenter)
//                                ) { snackbarData ->
//                                    Snackbar(
//                                        snackbarData = snackbarData,
//                                        modifier = Modifier.padding(16.dp)
//                                    )
//                                }

                                if(textVisible){
                                    Text(
                                        text = "！一卡通号或密码错误",
                                        color = Color.Red
                                    )
                                }

                            }

                            Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(
                                        onClick = {
                                            println("忘记密码")
                                        }
                                ) {
                                    Text("忘记密码？")
                                }

                                Button(
                                        onClick = {
                                            viewModel.login(username, password)

//                                        if (viewModel.login(username, password)) {
//                                            println("successful!")
//                                            onLogin()
//                                        } else {
//                                            println("failed!")
//                                        }
                                        }
                                ) {
                                    Text("登录")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}