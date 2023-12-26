package com.binay.recipeapp.uis.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.recipeapp.R
import com.binay.recipeapp.uis.intent.UnitIntent
import com.binay.recipeapp.uis.viewstate.UnitState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class UnitConverterViewModel @Inject constructor(@ApplicationContext val mContext: Context) :
    ViewModel() {

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
                UnitState.ResponseData(convert(toConvertFrom, toConvertInto, amount))
            } catch (e: Exception) {
                UnitState.Error(e.localizedMessage)
            }
        }
    }

    private fun convert(toConvertFrom: String, toConvertInto: String, amount: String): String {
//        1 cup = 16 tablespoon
//        1 cup = 48 teaspoon
//        1 cup = 250 gm
//        1 cup = 236.58 ml
//        1 cup = 8 ounces


//        <item>Cup</item>
//        <item>Table Spoon</item>
//        <item>Tea Spoon</item>
//        <item>Gram</item>
//        <item>Millilitre</item>
//        <item>Ounce</item>

        val amountInDouble = amount.toDouble()

        val unitArray = mContext.resources.getStringArray(R.array.units_array)
        var amountInCup = amountInDouble
//        Converts from other units to cup first
        if (toConvertFrom != unitArray[0]) {
            when (toConvertFrom) {
//                Table spoon
                unitArray[1] -> amountInCup /= 16
//                Tea spoon
                unitArray[2] -> amountInCup /= 48
//                Gm
                unitArray[3] -> amountInCup /= 250
//                Ml.
                unitArray[4] -> amountInCup /= 236.58
//                Ounce
                unitArray[5] -> amountInCup /= 8

            }

        }
//        Finally converts to others as required
        var finalAmt = amountInCup.toString()
        when (toConvertInto) {
            unitArray[1] -> finalAmt = (amountInCup * 16).toString()
            unitArray[2] -> finalAmt = (amountInCup * 48).toString()
            unitArray[3] -> finalAmt = (amountInCup * 250).toString()
            unitArray[4] -> finalAmt = (amountInCup * 236.58).toString()
            unitArray[5] -> finalAmt = (amountInCup * 8).toString()
        }
        return finalAmt
    }
}