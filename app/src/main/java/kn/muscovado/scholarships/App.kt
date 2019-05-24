package kn.muscovado.scholarships

import android.app.Application
import io.realm.Realm

class App: Application() {
    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     */
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
    }
}
