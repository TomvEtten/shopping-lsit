package hva.nl.shoppinglist

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "productTable")
data class Product (

    @ColumnInfo(name = "quantity")
    val quantity: Int,
    @ColumnInfo(name = "name")
    val name: String,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null
) : Parcelable