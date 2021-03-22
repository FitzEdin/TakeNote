package kn.muscovado.takenote.content

import io.realm.RealmObject

/**
 * The item being used within the application
 */
open class Item(
    var _id: String = "",        // unique MongoID
    var territory: String = "",  // where the notice applies to
    var department: String = "", // the ministry the notice applies to
    var date: String = "",       // possible date of event
    var venue: String = "",      // possible location of event
    var description: String = "",// text of the actual notice
    var status: String = ""     // whether the notice was verified
    ) : RealmObject() {

    override fun toString(): String =
        description

    constructor() : this(
        "", "", "",
        "", "", "",
        ""
    )
}
