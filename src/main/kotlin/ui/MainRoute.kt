package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import data.Result
import data.model.Rate
import data.nbpapi.NbpRepository
import data.util.clearNumbers
import data.util.roundCurrency
import kotlinx.coroutines.launch
import org.kodein.di.compose.localDI
import org.kodein.di.instance

sealed interface MainState {
    object Loading : MainState
    data class Error(val errorMessage: String) : MainState
    data class Loaded(
        val inputString: String,
        val inputValue: Double,
        val selectedRate: Rate,
        val rates: List<Rate>
    ) : MainState
}

@Composable
fun MainRoute() {
    var uiState by remember { mutableStateOf<MainState>(MainState.Loading) }
    val scope = rememberCoroutineScope()
    val repository: NbpRepository by localDI().instance()
    fun loadData() = scope.launch {
        uiState = when (val rates = repository.getRates()) {
            is Result.Error -> MainState.Error(rates.exception.localizedMessage.toString())
            is Result.Success -> MainState.Loaded(
                inputString = "10.0",
                inputValue = 10.0,
                selectedRate = Rate(
                    code = "PLN",
                    value = 1.0,
                    fullName = ""
                ),
                rates = rates.data.rates
            )
        }
    }

    SideEffect {
        if (uiState is MainState.Loading) loadData()
    }

    when (val state = uiState) {
        is MainState.Error -> ErrorComponent({ loadData() }, state.errorMessage)
        is MainState.Loaded -> ContentWindow(
            onchange = {
                uiState = try {
                    state.copy(
                        inputString = it.clearNumbers(state.inputString),
                        inputValue = it.clearNumbers(state.inputString).toDouble()
                    )
                } catch (_: Exception) {
                    state
                }
            },
            onChangeCurrency = { uiState = state.copy(selectedRate = it) },
            state = state
        )
        MainState.Loading -> LoadingComponent()
    }
}

@Composable
fun ContentWindow(
    onchange: (String) -> Unit,
    onChangeCurrency: (Rate) -> Unit,
    state: MainState.Loaded
) {
    val style = MaterialTheme.typography.headlineSmall
    Column{
        Row (modifier = Modifier.background(MaterialTheme.colorScheme.primary).padding(8.dp)){
            val textStyle = MaterialTheme.typography.headlineLarge.copy(fontFamily = FontFamily.Monospace)
            BasicTextField(
                state.inputString,
                { onchange(it) },
                Modifier.weight(1f),
                textStyle = textStyle,
                maxLines = 1,
            )
            Text(state.selectedRate.code, style = textStyle)
        }
        Row(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(modifier = Modifier.padding(8.dp), maxLines = 1, style = style, text = ((state.inputValue * state.selectedRate.value) / (state.rates.find {
                    it.code == "USD"
                }?.value ?: 0.0)).roundCurrency())
                Text(modifier = Modifier.padding(8.dp), style = style, text ="USD")
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(modifier = Modifier.padding(8.dp), maxLines = 1, style = style, text = ((state.inputValue * state.selectedRate.value) / (state.rates.find {
                    it.code == "EUR"
                }?.value ?: 0.0)).roundCurrency())
                Text(modifier = Modifier.padding(8.dp),style = style ,text = "EUR")
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(modifier = Modifier.padding(8.dp), maxLines = 1, style = style , text = ((state.inputValue * state.selectedRate.value) / (state.rates.find {
                    it.code == "UAH"
                }?.value ?: 0.0)).roundCurrency())
                Text(modifier = Modifier.padding(8.dp),style = style , text = "UAH")
            }
        }
        LazyColumn(Modifier.weight(1f).background(MaterialTheme.colorScheme.surface)) {
            items(state.rates) {
                Row (modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                    RateElement(
                    it, ((state.inputValue * state.selectedRate.value) / it.value).roundCurrency(),
                    { onChangeCurrency(it) },
                ) }
            }
        }
    }
}

@Composable
fun RateElement(rate: Rate, calculatedAmount: String, onClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Text("$calculatedAmount ${rate.code}", style = MaterialTheme.typography.labelLarge)
        Text(
            rate.fullName,
            maxLines = 1,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}