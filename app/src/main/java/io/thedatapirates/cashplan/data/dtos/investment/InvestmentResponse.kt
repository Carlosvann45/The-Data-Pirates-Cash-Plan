package io.thedatapirates.cashplan.data.dtos.investment

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Serializable

/**
 * A class to represent a customer response from the api
 */
@Serializable
class InvestmentResponse(
    var id: Int = 0,
    var dateCreated: String = "",
    var dateUpdated: String = "",
    var investmentType: String = "",
    var name: String = "",
    var sector: String = "",
    var amount: Double = 0.00,
    var buyPrice: Double = 0.00
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }
    companion object CREATOR : Parcelable.Creator<InvestmentResponse> {
        override fun createFromParcel(parcel: Parcel): InvestmentResponse {
            return InvestmentResponse(parcel)
        }

        override fun newArray(size: Int): Array<InvestmentResponse?> {
            return arrayOfNulls(size)
        }
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("Not yet implemented")
    }

}