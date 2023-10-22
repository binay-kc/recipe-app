package com.binay.recipeapp.uis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.recipeapp.data.repository.UnitConverterRepository
import com.binay.recipeapp.uis.intent.UnitIntent
import com.binay.recipeapp.uis.viewstate.UnitState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class UnitConverterViewModel(private val mRepository: UnitConverterRepository) : ViewModel() {

    val unitIntent = Channel<UnitIntent>(Channel.UNLIMITED)
    val unitState = MutableStateFlow<UnitState>(UnitState.Inactive)

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            unitIntent.consumeAsFlow().collect {
                when (it) {
                    is UnitIntent.ConvertUnit -> convertUnit(
                        it.toConvertFrom,
                        it.toConvertInto,
                        it.amount
                    )
                }
            }
        }
    }

    private fun convertUnit(toConvertFrom: String, toConvertInto: String, amount: String) {
        viewModelScope.launch {
            unitState.value = UnitState.Loading
            unitState.value = try {
                UnitState.ResponseData(mRepository.convert(toConvertFrom, toConvertInto, amount))
            } catch (e: Exception) {
                UnitState.Error(e.localizedMessage)
            }
        }
    }
}