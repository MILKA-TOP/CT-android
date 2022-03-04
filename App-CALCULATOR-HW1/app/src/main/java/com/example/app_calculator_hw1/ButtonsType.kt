package com.example.app_calculator_hw1

import android.os.Parcel
import android.os.Parcelable

enum class ButtonsType() : Parcelable {

    NUMBER, OPERATION, CLEAR, RESULT, DOT, NULL, LEFT_BRACKET, RIGHT_BRACKET, CLEAR_ONE,
    MINUS, ERROR, ANSWER;

    constructor(parcel: Parcel) : this()


    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ButtonsType> {
        override fun createFromParcel(parcel: Parcel): ButtonsType {
            return NULL
        }

        override fun newArray(size: Int): Array<ButtonsType?> {
            return arrayOfNulls(size)
        }
    }
}