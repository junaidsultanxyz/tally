package app.tally.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.context.GlobalContext

public actual fun databaseBuilder(): RoomDatabase.Builder<TallyDatabase> {
    val context = GlobalContext.get().get<Context>().applicationContext
    val dbFile = context.getDatabasePath("tally.db")
    return Room.databaseBuilder<TallyDatabase>(context = context, name = dbFile.absolutePath)
}
