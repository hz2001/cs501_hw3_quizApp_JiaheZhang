package com.example.hw3_quizapp_jiahezhang
import com.example.hw3_quizapp_jiahezhang.ui.theme.Hw3_quizApp_JiaheZhangTheme
import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.Nullable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.random.Random
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontLoader
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.layout.* // for layout
import androidx.compose.material3.* // for Material design components
import androidx.compose.runtime.* // for managing the composable state
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.semantics.Role.Companion.Checkbox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Hw3_quizApp_JiaheZhangTheme {
                QuizApp()
            }
        }
    }
}

private val qaList = listOf(
    "What is the capital of France?" to "Paris",
    "Who wrote 'Hamlet'?" to "Shakespeare",
    "What is the square root of 16?" to "Four",
    "Which element has the atomic number 1?" to "Hydrogen",
    "Who was the first president of the United States?" to "Washington",
)


@Composable
fun QuizCard(scope: CoroutineScope, snackbarHostState: SnackbarHostState,
             onSwitch: () -> Unit,
             onIncrement: () -> Unit){
    var index by remember { mutableStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var buttonText by remember { mutableStateOf("Next Question") }
    Card(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp),
    ) {
        Column{
            Text(text = qaList[index].first,
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally))
            Text(text = "Answer Hint: "+ qaList[index].second,
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally))
            TextField(
                value = userAnswer,
                onValueChange = {userAnswer = it},
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally))
            Button(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    // check if the answer is correct

                    // go to the next card
                    buttonText = "Next Question"
                    if (userAnswer == qaList[index].second){
                        onIncrement()
                        scope.launch {
                            snackbarHostState.showSnackbar("Correct Answer, score + 1")
                        }
                    }else{
                        scope.launch {
                            snackbarHostState.showSnackbar("Wrong")
                        }
                    }
                    userAnswer = ""
                    index += 1

                    if (index == qaList.size){
                        // if reach the end of list, return to the front
                        scope.launch {
                            snackbarHostState.showSnackbar("You have finished the quiz!!")
                        }
                        buttonText = "Submit"
                        onSwitch()
                    }

            }
            ){ Text(buttonText)}
        }
    }
}


@SuppressLint("UnrememberedMutableState", "MutableCollectionMutableState") // 什么意思？
@Composable
fun QuizApp() {
    var startQuizApp by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var finished by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }

    Column{
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            })
        {
            contentPadding ->
                if (startQuizApp) {
                    QuizCard(scope = scope,
                        snackbarHostState = snackbarHostState,
                        onSwitch = {startQuizApp = false
                                   finished = true},
                        onIncrement = { score++ }
                        )
                } else {
                    StartQuizPage(contentPadding,
                        onSwitch={startQuizApp = true
                                score = 0},
                        finished = finished,
                        score = score)
                }
        }
    }
}

@Composable
fun StartQuizPage(contentPadding: PaddingValues, onSwitch: () -> Unit, finished: Boolean, score: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (finished){
            Column{
                Text("Your quiz ends, your score is $score")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    onSwitch()
                }) {
                    Text("Retake Quiz")
                }
            }

        }else{
            Button(onClick = {
                onSwitch()
            }) {
                Text("Start Quiz")
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Hw3_quizApp_JiaheZhangTheme() {
        QuizApp()
    }
}