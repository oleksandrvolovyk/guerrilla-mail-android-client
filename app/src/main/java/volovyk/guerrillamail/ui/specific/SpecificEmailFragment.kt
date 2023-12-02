package volovyk.guerrillamail.ui.specific

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.compose.GuerrillaMailTheme
import dagger.hilt.android.AndroidEntryPoint
import volovyk.guerrillamail.data.emails.model.Email

@AndroidEntryPoint
class SpecificEmailFragment : Fragment() {

    private val viewModel: SpecificEmailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                GuerrillaMailTheme {
                    val uiState by viewModel.uiState.collectAsState()
                    SpecificEmailScreen(
                        uiState = uiState,
                        onHtmlRenderSwitchCheckedChange = { viewModel.setHtmlRender(it) }
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SpecificEmailFragmentPreviewTextBody() {
    GuerrillaMailTheme {
        SpecificEmailScreen(
            uiState = SpecificEmailUiState(
                email = Email(
                    id = "1",
                    from = "from@example.com",
                    subject = "Test subject",
                    body = "Test body",
                    htmlBody = "Test html body",
                    date = "Today",
                    viewed = false
                ),
                renderHtml = false
            )
        )
    }
}