package example.app_contacts_hw3

import android.content.Context
import android.provider.ContactsContract

fun Context.fetchAllContacts(): List<Contact> {
    contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        null,
        null,
        null
    )
        .use { cursor ->
            if (cursor == null) return emptyList()
            val builder = ArrayList<Contact>()
            while (cursor.moveToNext()) {
                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        ?: "N/A"
                val phoneNumber =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        ?: "N/A"

                builder.add(Contact(name, phoneNumber))
            }
            return builder
        }
}

fun regexFormat(s: String) = s.replace(Regex("[^0-9]"), "")

val BIGVAL = 10000
val BASIC_SAMPLE_PACKAGE = "example.app_contacts_hw3"

val CONTACT_IVAN = Contact("IVAN", "0123")
val CONTACT_TYOMA = Contact("TYOMA", "2345")
val CONTACT_TYOMA_IVAN = Contact("TYOMA", "012367")
val CONTACT_BOBA = Contact("BOBA", "6789")
val CONTACT_ABA = Contact("ABA", "4567")

val EMPTY_CONTACT_LIST = emptyList<Contact>()
val FULL_CONTACT_LIST =
    listOf(CONTACT_IVAN, CONTACT_BOBA, CONTACT_TYOMA, CONTACT_TYOMA_IVAN, CONTACT_ABA)
val SAME_LIST = listOf(CONTACT_IVAN, CONTACT_TYOMA_IVAN)
val SAME_TEXT_67_CONTACT_LIST = listOf(CONTACT_BOBA, CONTACT_TYOMA_IVAN, CONTACT_ABA)





