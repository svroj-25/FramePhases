package uz.ttpu.framephases

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FramePhasesApp()
                }
            }
        }
    }
}

enum class DemoScreen {
    MENU,
    LECTURE_TREE, // Q4
    PADDING_DEMO, // Q5
    OFFSET_DEMO,  // Q6
    COLOR_DEMO,   // Q7
    PARALLAX_SLOW, // Q9
    PARALLAX_FAST, // Q9
    SCROLL_TO_TOP_NAIVE, // Q10
    SCROLL_TO_TOP_DERIVED, // Q10
    SKIP_VALUE, // Q11
    SKIP_LAMBDA, // Q11
    CYCLIC_DEMO, // Q13
    CYCLIC_FIXED, // Q13
    STACK_BELOW_DEMO // Q14
}

@Composable
fun FramePhasesApp() {
    var currentDemo by remember { mutableStateOf(DemoScreen.MENU) }

    if (currentDemo == DemoScreen.MENU) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Jetpack Compose Phases: Lab Demos",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Select any demo below to test its Logcat logs (tag:Phases, tag:Parallax, tag:Derived, tag:Skip, tag:Cycle, tag:StackBelow):",
                style = MaterialTheme.typography.bodyMedium
            )

            DemoButton("Q4: LectureTree (The First Frame)") { currentDemo = DemoScreen.LECTURE_TREE }
            DemoButton("Q5: PaddingDemo (Composition Phase Read)") { currentDemo = DemoScreen.PADDING_DEMO }
            DemoButton("Q6: OffsetDemo (Layout Phase Read)") { currentDemo = DemoScreen.OFFSET_DEMO }
            DemoButton("Q7: ColorDemo (Drawing Phase Read)") { currentDemo = DemoScreen.COLOR_DEMO }
            DemoButton("Q9: ParallaxSlow (Composition Read)") { currentDemo = DemoScreen.PARALLAX_SLOW }
            DemoButton("Q9: ParallaxFast (Layout Read)") { currentDemo = DemoScreen.PARALLAX_FAST }
            DemoButton("Q10: ScrollToTopNaive (No derivedStateOf)") { currentDemo = DemoScreen.SCROLL_TO_TOP_NAIVE }
            DemoButton("Q10: ScrollToTopDerived (With derivedStateOf)") { currentDemo = DemoScreen.SCROLL_TO_TOP_DERIVED }
            DemoButton("Q11: Skip Value (Parent Recomposes)") { currentDemo = DemoScreen.SKIP_VALUE }
            DemoButton("Q11: Skip Lambda (Parent Skips)") { currentDemo = DemoScreen.SKIP_LAMBDA }
            DemoButton("Q13: CyclicDemo (Loop Across Frames)") { currentDemo = DemoScreen.CYCLIC_DEMO }
            DemoButton("Q13: CyclicFixed (Fixed with Column)") { currentDemo = DemoScreen.CYCLIC_FIXED }
            DemoButton("Q14: StackBelow (Custom Layout)") { currentDemo = DemoScreen.STACK_BELOW_DEMO }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { currentDemo = DemoScreen.MENU },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("← Back to Menu")
                }
                Text(
                    text = currentDemo.name,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            when (currentDemo) {
                DemoScreen.LECTURE_TREE -> LectureTree()
                DemoScreen.PADDING_DEMO -> PaddingDemo()
                DemoScreen.OFFSET_DEMO -> OffsetDemo()
                DemoScreen.COLOR_DEMO -> ColorDemo()
                DemoScreen.PARALLAX_SLOW -> ParallaxSlow()
                DemoScreen.PARALLAX_FAST -> ParallaxFast()
                DemoScreen.SCROLL_TO_TOP_NAIVE -> ScrollToTopNaive()
                DemoScreen.SCROLL_TO_TOP_DERIVED -> ScrollToTopDerived()
                DemoScreen.SKIP_VALUE -> ParentValue()
                DemoScreen.SKIP_LAMBDA -> ParentLambda()
                DemoScreen.CYCLIC_DEMO -> CyclicDemo()
                DemoScreen.CYCLIC_FIXED -> CyclicFixed()
                DemoScreen.STACK_BELOW_DEMO -> StackBelowDemo()
                DemoScreen.MENU -> {}
            }
        }
    }
}

@Composable
private fun DemoButton(title: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(title)
    }
}
