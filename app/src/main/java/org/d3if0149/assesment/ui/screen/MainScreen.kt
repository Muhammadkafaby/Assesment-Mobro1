package org.d3if0149.assesment.ui.screen


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.d3if0149.assesment.Model.currencyRates
import org.d3if0149.assesment.R
import java.util.Locale



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.converted_money)) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = {navController.navigate(org.d3if0149.assesment.navigation.Screen.About.route)}) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.tentang_aplikasi),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )

        }
    ) { padding ->
        ScreenContent(
            modifier = Modifier.padding(padding)
        )
    }
}

@SuppressLint("StringFormatMatches")
@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun ScreenContent(modifier: Modifier) {

    var amount by rememberSaveable { mutableStateOf("") }
    val amountError by rememberSaveable {
        mutableStateOf(false)
    }
    var fromCurrency by rememberSaveable { mutableStateOf("") }
    val fromCurrencyError by rememberSaveable {
        mutableStateOf(false)
    }
    var toCurrency by rememberSaveable { mutableStateOf("") }
    val toCurrencyError by rememberSaveable {
        mutableStateOf(false)
    }
    var result by rememberSaveable { mutableStateOf("") }
    var resultError by rememberSaveable {
        mutableStateOf(false)
    }

    val currentContext = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()), // Add this line
        horizontalAlignment = CenterHorizontally
    ) {
        @Composable
        fun <T> TextFieldMenu(
            otherSelectedOption: T?,
            modifier: Modifier = Modifier,
            label: String,
            options: List<T>,
            selectedOption: T?,
            onOptionSelected: (T?) -> Unit,
            optionToString: (T) -> String,
            filteredOptions: (searchInput: String) -> List<T>,
            optionToDropdownRow: @Composable (T) -> Unit = { option ->
                Text(optionToString(option))
            },
            noResultsRow: @Composable () -> Unit = {
                DropdownMenuItem(
                    onClick = {},
                    text = {
                        Text(
                            text = stringResource(id = R.string.no_matches_found),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontStyle = FontStyle.Italic,
                        )
                    },
                )
            },
            focusRequester: FocusRequester = remember { FocusRequester() },
            keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
            trailingIcon: @Composable (expanded: Boolean) -> Unit = { expanded ->
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            textFieldColors: TextFieldColors = ExposedDropdownMenuDefaults.textFieldColors(
                Color.Black,
            ),
            bringIntoViewRequester: BringIntoViewRequester = remember { BringIntoViewRequester() },
            coroutineScope: CoroutineScope = rememberCoroutineScope(),
            isError: Boolean,
        ) {
            // Get our text for the selected option
            val selectedOptionText = rememberSaveable(selectedOption) {
                selectedOption?.let { optionToString(it) }.orEmpty()
            }

            // Default our text input to the selected option
            var textInput by rememberSaveable(selectedOptionText) {
                mutableStateOf(selectedOptionText)
            }

            var dropDownExpanded by rememberSaveable { mutableStateOf(false) }

            // Update our filtered options everytime our text input changes
            val filteredOptions = rememberSaveable(textInput, dropDownExpanded) {
                when (dropDownExpanded) {
                    true -> filteredOptions(textInput).filter { it != otherSelectedOption } // Filter out the other selected option
                    // Skip filtering when we don't need to
                    false -> emptyList()
                }
            }

            val keyboardController = LocalSoftwareKeyboardController.current
            val focusManager = LocalFocusManager.current

            ExposedDropdownMenuBox(
                expanded = dropDownExpanded,
                onExpandedChange = { dropDownExpanded = !dropDownExpanded },
                modifier = modifier,
            ) {
                // Text Input
                OutlinedTextField(
                    value = textInput,

                    onValueChange = {
                        // Dropdown may auto hide for scrolling but it's important it always shows when a user
                        // does a search
                        dropDownExpanded = true
                        textInput = it.uppercase(Locale.getDefault())
                    },
                    modifier = Modifier
                        // Match the parent width
                        .fillMaxWidth()
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .menuAnchor()
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            // When only 1 option left when we lose focus, selected it.
                            if (!focusState.isFocused) {
                                // Whenever we lose focus, always hide the dropdown
                                dropDownExpanded = false

                                when (filteredOptions.size) {
                                    // Auto select the single option
                                    1 -> if (filteredOptions.first() != selectedOption) {
                                        onOptionSelected(filteredOptions.first())
                                    }
                                    // Nothing to we can auto select - reset our text input to the selected value
                                    else -> textInput = selectedOptionText
                                }
                            } else {
                                // When focused:
                                // Ensure field is visible by scrolling to it
                                coroutineScope.launch {
                                    bringIntoViewRequester.bringIntoView()
                                }
                                // Show the dropdown right away
                                dropDownExpanded = true
                            }
                        },
                    label = { Text(label) },
                    trailingIcon = { trailingIcon(dropDownExpanded) },
                    colors = textFieldColors,
                    keyboardOptions = keyboardOptions.copy(
                        imeAction = when (filteredOptions.size) {
                            // We will either reset input or auto select the single option
                            0, 1 -> ImeAction.Done
                            // Keyboard will hide to make room for search results
                            else -> ImeAction.Search
                        }
                    ),
                    keyboardActions = KeyboardActions(
                        onAny = {
                            when (filteredOptions.size) {
                                // Remove focus to execute our onFocusChanged effect
                                0, 1 -> focusManager.clearFocus(force = true)
                                // Can't auto select option since we have a list, so hide keyboard to give more room for dropdown
                                else -> keyboardController?.hide()
                            }
                        }
                    )
                )

                // Dropdown
                if (dropDownExpanded) {
                    val dropdownOptions = remember(textInput) {
                        if (textInput.isEmpty()) {
                            options
                        } else {
                            filteredOptions(textInput)
                        }
                    }

                    ExposedDropdownMenu(
                        expanded = dropDownExpanded,
                        onDismissRequest = { dropDownExpanded = false },
                    ) {
                        if (dropdownOptions.isEmpty()) {
                            noResultsRow()
                        } else {
                            dropdownOptions.forEach { option ->
                                DropdownMenuItem(
                                    onClick = {
                                        dropDownExpanded = false
                                        onOptionSelected(option)
                                        focusManager.clearFocus(force = true)
                                    },
                                    text = {
                                        optionToDropdownRow(option)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo), // replace 'your_image' with your image file name
                contentDescription = "Logo Convert Money", // replace with your image description
                modifier = Modifier
                    .fillMaxWidth()
                    .size(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = amount,
                onValueChange = {
                    if (!it.contains(" ")) {
                        amount = it
                    }
                },
                label = { Text(stringResource(id = R.string.amount)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                isError = amountError, // Add this line
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 1.dp)
            )

            TextFieldMenu(
                label = stringResource(id = R.string.from_currency),
                options = currencyRates.map { it.currency },
                selectedOption = fromCurrency,
                onOptionSelected = { fromCurrency = it ?: "" },
                otherSelectedOption = toCurrency,
                optionToString = { it },
                filteredOptions = { searchInput ->
                    currencyRates.map { it.currency }.filter {
                        it.contains(searchInput, ignoreCase = true)
                    }
                },
                isError = fromCurrencyError // Add this line
            )



            TextFieldMenu(
                label = stringResource(id = R.string.to_currency),
                options = currencyRates.map { it.currency },
                selectedOption = toCurrency,
                onOptionSelected = { toCurrency = it ?: "" },
                otherSelectedOption = fromCurrency,
                optionToString = { it },
                filteredOptions = { searchInput ->
                    currencyRates.map { it.currency }.filter {
                        it.contains(searchInput, ignoreCase = true)
                    }
                },
                isError = toCurrencyError
            )
        }
        if (resultError) { // Add this block
            Text(
                text = stringResource(id = R.string.error) + result,
                fontSize = 20.sp,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else if (result.isNotEmpty()) {
            Text(
                text = result,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        var isConvertButtonClicked by rememberSaveable { mutableStateOf(false) }
        Button(
            onClick = {
                result = convertCurrency(amount, fromCurrency, toCurrency)
                resultError = result == "One or both currencies not supported"
                isConvertButtonClicked = true
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.convert))
        }
        if (amount.isNotEmpty() && fromCurrency.isNotEmpty() && toCurrency.isNotEmpty() && !amountError && !fromCurrencyError && !toCurrencyError && isConvertButtonClicked) {
            val context = LocalContext.current
            Button(
                onClick = {
                    shareData(
                        context = context,
                        message = context.getString(
                            R.string.share_template,
                            amount, fromCurrency, toCurrency, result
                        )
                    )

                },
                modifier = Modifier.padding(top = 1.dp),
            ) {
                Text(text = stringResource(id = R.string.bagikan))
            }
        }
    }
    }

fun shareData(context: Context, message: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    if (shareIntent.resolveActivity(context.packageManager) != null){
        context.startActivity(shareIntent)
    }
}





fun convertCurrency(amount: String, fromCurrency: String, toCurrency: String): String {
    val fromRate = currencyRates.find { it.currency == fromCurrency }?.rate
    val toRate = currencyRates.find { it.currency == toCurrency }?.rate

    // Tambahkan pengecekan ini
    if (amount.isEmpty() || fromRate == null || toRate == null) {
        return "Please fill in all fields"
    }

    val convertedAmount = amount.toDouble() * (toRate / fromRate)
    return "$convertedAmount $toCurrency"
}






@Composable
@Preview
@Preview(showBackground = true)
fun MainScreenPreview() {
    MainScreen(rememberNavController())
}


