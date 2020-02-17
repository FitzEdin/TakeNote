package kn.muscovado.scholarships.content

import io.realm.RealmObject

/**
 * The item being used within the application
 */
open class Item(
    var _id: String = "",        // unique MongoID
    var title: String = "",      // name of scholarship
    var coverage: String = "",   // how much of the expenses are covered
    var level: String = "",      // academic level of eligible programme(s)
    var programme: String = "",  // degree or set of fields covered
    var location: String = "",   // where the scholarship is tenable
    var open_to: String = "",    // the nationalities that can take up the offer
    var link: String = "",       // URL of the scholarship's webpage
    var status: String = ""      // whether the URL was vetted
    ) : RealmObject() {

    override fun toString(): String =
        "Title: $title for $level $programme open to $open_to"

    constructor() : this(
        "", "", "",
        "", "", "",
        "", "", ""
    )
}
