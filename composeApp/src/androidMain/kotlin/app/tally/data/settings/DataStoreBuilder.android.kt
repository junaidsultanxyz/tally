package app.tally.data.settings

import android.content.Context
import org.koin.core.context.GlobalContext

public actual fun dataStorePath(): String {
    val context = GlobalContext.get().get<Context>().applicationContext
    return context.filesDir.resolve("tally.preferences_pb").absolutePath
}
