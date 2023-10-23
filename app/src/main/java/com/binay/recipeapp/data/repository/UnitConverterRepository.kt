package com.binay.recipeapp.data.repository

import android.content.Context
import com.binay.recipeapp.R


class UnitConverterRepository(private val mContext: Context) {

    suspend fun convert(toConvertFrom: String, toConvertInto: String, amount: String): String {
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