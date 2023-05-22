package com.beyzasker.housemateapp.model

import android.os.Parcel
import android.os.Parcelable

data class UserModel(
    var uid: String,
    var fullName: String,
    var email: String,
    var entryYear: String,
    var gradYear: String,
    var number: String,
    var photo: String,
    var education: String,
    var state: String,
    var distance: String,
    var time: String,
    var nameArr: List<String>,
    var isAdmin: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: listOf(),
        parcel.readByte() != 0.toByte()
    )

    constructor() : this(
        "", "", "", "", "", "", "", "", "", "", "", emptyList(), false
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(fullName)
        parcel.writeString(email)
        parcel.writeString(entryYear)
        parcel.writeString(gradYear)
        parcel.writeString(number)
        parcel.writeString(photo)
        parcel.writeString(education)
        parcel.writeString(state)
        parcel.writeString(distance)
        parcel.writeString(time)
        parcel.writeStringList(nameArr)
        parcel.writeByte(if (isAdmin) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserModel> {
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel)
        }

        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }
}
