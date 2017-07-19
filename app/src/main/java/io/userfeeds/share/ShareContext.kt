package io.userfeeds.share

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.IdRes
import io.userfeeds.sdk.core.RankingContext

class ShareContext(val id: RankingContext, val name: String, @IdRes val imageId: Int): Parcelable {

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
        dest.writeInt(imageId)
    }

    companion object {

        @JvmStatic
        val CREATOR = object : Parcelable.Creator<ShareContext> {

            override fun createFromParcel(source: Parcel) = ShareContext(
                    id = source.readString(),
                    name = source.readString(),
                    imageId = source.readInt()
            )

            override fun newArray(size: Int) = arrayOfNulls<ShareContext>(size)
        }
    }
}
