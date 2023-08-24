package volovyk.guerrillamail.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Email(
    @field:SerializedName("mail_id") @field:PrimaryKey val id: Int,
    @field:SerializedName("mail_from") val from: String,
    @field:SerializedName("mail_subject") val subject: String,
    @field:SerializedName("mail_body") val body: String,
    @field:SerializedName("mail_date") val date: String,
    val viewed: Boolean = false
)