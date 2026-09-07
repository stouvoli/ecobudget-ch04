import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.example.ui.screens.EcoBudgetScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.EcoBudgetViewModel

fun MainViewController() = ComposeUIViewController {
    val viewModel = remember { EcoBudgetViewModel() }

    MyApplicationTheme {
        EcoBudgetScreen(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )
    }
}