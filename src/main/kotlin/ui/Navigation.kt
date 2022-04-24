package ui
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EuroSymbol
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
@Composable
fun AppNavigation() {
    var selectedItem by remember { mutableStateOf<MainNavigationDestinations>(MainNavigationDestinations.Exchange) }
    Row(Modifier.fillMaxWidth()) {
        NavigationRail {
            Column(modifier = Modifier.fillMaxWidth().clickable {
                selectedItem = MainNavigationDestinations.Exchange
            }, horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Icon(Icons.Default.EuroSymbol, modifier = Modifier.padding(top = 8.dp).padding(horizontal = 8.dp), contentDescription = null)
                Text("Currency Converter", modifier = Modifier.padding(vertical = 8.dp), textAlign = TextAlign.Center)
            }
            Column(
                modifier = Modifier.fillMaxWidth()
                    .clickable { selectedItem = MainNavigationDestinations.GoldPrice},
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.LocalOffer, modifier = Modifier.padding(top = 8.dp).padding(horizontal = 8.dp), contentDescription = null)
                Text("Price of gold", modifier = Modifier.padding(vertical = 8.dp), textAlign = TextAlign.Center)
            }
        }
        Box(Modifier.fillMaxWidth()) {
            when (selectedItem) {
                MainNavigationDestinations.Exchange -> MainRoute()
                MainNavigationDestinations.GoldPrice -> GoldPriceRoute()
            }
        }
    }
}


sealed interface MainNavigationDestinations {
    object Exchange : MainNavigationDestinations
    object GoldPrice : MainNavigationDestinations
}
