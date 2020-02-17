package kn.muscovado.scholarships.utils

class Constants{

    // field names for the Item object
    val ITEM_ID = "_id"
    val ITEM_TITLE = "title"
    val ITEM_COVERAGE = "coverage"

    val ITEM_LEVEL = "level"
    val ITEM_PROGRAMME = "programme"
    val ITEM_LOCATION = "location"

    val ITEM_OPEN_TO = "open_to"
    val ITEM_LINK = "link"
    val ITEM_STATUS = "status"

    // port and url for API
    val BASE_URL = "http://muscovado.kn"
    val PORT = ":8000"
    val LINKS = "/links"
    val SCHOLARSHIPS = "/scholarships"

    // Tags for Log and intents
    val LOG_TAG = "Network"
    val TAG_ITEM = "itemID"
    val LOG_MSG_ITEM_LINK = "Item Link "

    // share strings
    val TEXT_PLAIN = "text/plain"
    val SHARE_TITLE = "Share this scholarship on..."
    val SHARE_EXTRA_START = "You should check out "
    val SHARE_EXTRA_END = " on Scholarships.kn!\n\nDownload the app from Google Play http://goo.gl/6WSXON"

    // strings for uploading links
    val DROP_ANOTHER_LINK = "Drop another link"
    val QUESTION_LINK_DROP = "Did you drop a link?"
    val SUCCESS_LINK_UPLOAD = "Thanks for the link!"
    val ERROR_LINK_UPLOAD = "Check your internet connection!"
    val SUCCESS_SCHOL_UPLOAD = "Scholarship info updated"
    val ERROR_SCHOL_UPLOAD = "Check your internet connection!"

    val STATUS_NEW = "new"
    val STATUS_OLD = "old"
    val STATUS_VETTED = "vetted"
    val UNVETTED_ITEM = ""

    val PARTIAL = "Partial"
    val FULL = "Full"

    val LEVEL_FOUR = "PostDoc"
    val LEVEL_THREE = "PhD"
    val LEVEL_TWO = "Masters"
    val LEVEL_TWOB = "Postgraduate"
    val LEVEL_ONE = "Bachelors"
    val LEVEL_ZERO = "Short Course"
}