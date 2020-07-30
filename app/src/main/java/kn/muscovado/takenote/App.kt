package kn.muscovado.takenote


import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import kn.muscovado.takenote.content.MyGration

class App: Application() {
    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     */
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        // set default configuration
        val realmConfig = RealmConfiguration.Builder()
            .name("notices.realm")
            .schemaVersion(1)
            .migration(MyGration())
            .build()
        Realm.setDefaultConfiguration(realmConfig)
    }
}
