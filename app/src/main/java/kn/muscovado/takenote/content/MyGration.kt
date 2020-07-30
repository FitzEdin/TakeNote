package kn.muscovado.takenote.content


import io.realm.DynamicRealm
import io.realm.RealmMigration

class MyGration : RealmMigration {
    /**
     * This method will be called if a migration is needed. The entire method is wrapped in a
     * write transaction so it is possible to create, update or delete any existing objects
     * without wrapping it in your own transaction.
     *
     * @param realm the Realm schema on which to perform the migration.
     * @param oldVersion the schema version of the Realm at the start of the migration.
     * @param newVersion the schema version of the Realm after executing the migration.
     */
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var version: Long = oldVersion
        val schema = realm.schema

        // upgrade from version 0 of database
        if(version == 0L){
            schema.get("Notice")!!
                .addField("version", Int::class.java)

            version++
        }
    }
}