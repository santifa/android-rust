package com.santifa.android_rust

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.measureTimedValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                RustApp()
            }
        }
    }
}

enum class RustAppWorkflow() {
    Start,
    String,
    Addition,
    ComplexOperation
}

@Composable
fun RustApp(
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = RustAppWorkflow.valueOf(
        backStackEntry?.destination?.route ?: RustAppWorkflow.Start.name
    )

    Scaffold(
        topBar = {
            RustAppBar(
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            RustNavigation(navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RustAppBar(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = "Android Rust Example") },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    painterResource(id = R.drawable.img),
                    contentDescription = "Start",
                    Modifier.clickable {
                        navigateUp()
                    })
            }
        },
    )
}

@Composable
fun RustNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = RustAppWorkflow.Start.name
    ) {
        composable(RustAppWorkflow.Start.name) {
            StartScreen(navController = navController)
        }
        composable(RustAppWorkflow.String.name) {
            StringScreen(navController = navController)
        }
        composable(RustAppWorkflow.Addition.name) {
            AddScreen(navController = navController)
        }
        composable(RustAppWorkflow.ComplexOperation.name) {
            ComplexOpScreen(navController = navController)
        }
    }
}

@Composable
fun BaseScreen(
    introText: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = introText
        )
        Spacer(modifier = Modifier.weight(1f))
        content()
    }
}

@Composable
fun StartScreen(navController: NavController) {
    val introText = "This is an Android Rust example application " +
            "which demonstrates the interfacing between the languages. " +
            "The application has three views which offer different examples."
    BaseScreen(introText = introText, content =
    {
        Buttons(
            currentScreen = RustAppWorkflow.Start.name,
            nextView = { navController.navigate(RustAppWorkflow.String.name) },
            function = { }
        )
    }
    )
}


@Composable
fun StringScreen(navController: NavController) {
    val introText = "This is the first example. It calls the native Rust library " +
            "and answers with a hello world and the architecture used by the application."
    var helloWorldText by remember {
        mutableStateOf("Click the call rust button")
    }

    BaseScreen(introText = introText, content = {
        Text(text = helloWorldText)
        Buttons(
            currentScreen = RustAppWorkflow.String.name,
            nextView = { navController.navigate(RustAppWorkflow.Addition.name) },
            function = {
                helloWorldText = NativeLibrary.helloWorld()
            }
        )
    })
}

@Composable
fun AddScreen(navController: NavController) {
    val introText = "This is the second example. It calls the native Rust library " +
            "with two arguments and the library adds them together."
    var additionResult by remember {
        mutableStateOf("Click the call rust button")
    }

    var leftHand by remember {
        mutableStateOf("1")
    }

    var rightHand by remember {
        mutableStateOf("1")
    }

    BaseScreen(introText = introText, content = {
        Text(text = additionResult)
        TextField(
            value = leftHand,
            onValueChange = { leftHand = it },
            label = { Text(text = "Left Hand") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        TextField(
            value = rightHand,
            onValueChange = { rightHand = it },
            label = { Text(text = "Right Hand") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Buttons(
            currentScreen = RustAppWorkflow.Addition.name,
            nextView = { navController.navigate(RustAppWorkflow.ComplexOperation.name) },
            function = {
                val result = NativeLibrary.add(leftHand.toInt(), rightHand.toInt());
                additionResult = "The result is $result"
            }
        )
    })
}

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ComplexOpScreen(navController: NavController) {
    val introText = "This screen demonstrates a more complex operation. " +
            "The task is to calculate the first perfect numbers limited by the input number. " +
            "A perfect number is a positive integer that is the sum of its proper positive divisors excluding the number itself. " +
            "This task is implemented in Kotlin and in Rust for demonstration purposes and shows timing information about each run."

    var complexResult by remember {
        mutableStateOf("Click the call Rust or Kotlin button")
    }

    var benchTime by remember {
        mutableStateOf("Benchmark Time")
    }

    var inputLength by remember {
        mutableStateOf("33550336")
    }

    BaseScreen(introText = introText, content = {
        Text(text = complexResult)
        Text(text = benchTime)
        TextField(
            value = inputLength,
            onValueChange = { inputLength = it },
            label = { Text(text = "Input") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(onClick = {
                GlobalScope.launch {
                    suspend {
                        val (result: String, duration: Duration) = measureTimedValue {
                            listPerfect(inputLength.toInt())
                        }
                        complexResult = result
                        benchTime = "Got Kotlin result after ${duration.inWholeSeconds} s"
                    }.invoke()
            }
        }) {
            Text(text = "Call Kotlin")
        }
        Buttons(
            currentScreen = RustAppWorkflow.ComplexOperation.name,
            nextView = { navController.navigate(RustAppWorkflow.Start.name) },
            function = {

                GlobalScope.launch {
                    suspend {
                        val (result: String, duration) = measureTimedValue {
                            NativeLibrary.perfectNumbers(inputLength.toInt()).joinToString(", ")
                        }
                        complexResult = result
                        benchTime = "Got Rust result after ${duration.inWholeSeconds} s"
                    }.invoke()
                }
            }
        )
    })
}

@Composable
fun Buttons(
    currentScreen: String,
    nextView: () -> Unit,
    function: () -> Unit
) {
    Row(
        Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        if (currentScreen != RustAppWorkflow.Start.name) {
            Button(onClick = function) {
                Text(text = "Call Rust")
            }
        }
        Button(onClick = nextView) {
            Text(text = "Next")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RustPreview() {
    AppTheme {
        RustApp()
    }
}

fun listPerfect(n: Int): String {
    val list: MutableList<Int> = mutableListOf()
    for (i in 2..n) {
        if (isPerfect(i)) list.add(i)
    }
    return list.joinToString(", ")
}

fun isPerfect(n: Int): Boolean = when {
    n < 2 -> false
    n % 2 == 1 -> false  // there are no known odd perfect numbers
    else -> {
        var tot = 1
        var q: Int
        for (i in 2..Math.sqrt(n.toDouble()).toInt()) {
            if (n % i == 0) {
                tot += i
                q = n / i
                if (q > i) tot += q
            }
        }
        n == tot
    }
}