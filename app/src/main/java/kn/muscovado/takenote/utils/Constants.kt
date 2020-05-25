package kn.muscovado.takenote.utils

class Constants{

    // field names for the Item object
    val ITEM_ID = "_id"
    val ITEM_TERRITORY = "territory"
    val ITEM_DEPARTMENT = "department"

    val ITEM_DATE = "date"
    val ITEM_VENUE = "venue"
    val ITEM_DESCRIPTION = "description"

    val ITEM_STATUS = "status"

    // port and url for API
    val BASE_URL = "http://muscovado.kn"
    val PORT = ":8000"
    val NOTICES = "/notices"

    // Tags for Log and intents
    val LOG_TAG = "takenote.kn"
    val TAG_ITEM = "itemID"
    val LOG_MSG_ITEM_LINK = "Item Link "

    // share strings
    val TEXT_PLAIN = "text/plain"
    val SHARE_TITLE = "Share this notice on..."
    val SHARE_EXTRA_START = "You should check out "
    val SHARE_EXTRA_END = " on TakeNote.kn!\n\nDownload the app from Google Play http://goo.gl/6WSXON"

    // strings for uploading links
    val DROP_ANOTHER_LINK = "Drop another link"
    val QUESTION_LINK_DROP = "Did you drop a link?"
    val SUCCESS_LINK_UPLOAD = "Thanks for the link!"
    val ERROR_LINK_UPLOAD = "Check your internet connection!"
    val SUCCESS_SCHOL_UPLOAD = "Scholarship info updated"
    val ERROR_SCHOL_UPLOAD = "Check your internet connection!"
    val SUCCESS_SCHOL_DELETE = "Scholarship deleted"
    val ERROR_SCHOL_DELETE = "Check your internet connection!"

    val STATUS_NEW = "new"
    val STATUS_OLD = "old"
    val STATUS_VETTED = "vetted"
    val UNVETTED_ITEM = ""
}