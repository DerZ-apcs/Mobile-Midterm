package com.example.midterm_application.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.migration.Migration;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.midterm_application.data.model.CartItem;
import com.example.midterm_application.data.model.Order;
import com.example.midterm_application.data.model.OrderItem;

@Database(entities = {CartItem.class, Order.class, OrderItem.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `orders` "
                    + "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "`createdAt` INTEGER NOT NULL, "
                    + "`totalPrice` REAL NOT NULL, "
                    + "`status` TEXT)");
            database.execSQL("CREATE TABLE IF NOT EXISTS `order_items` "
                    + "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "`orderId` INTEGER NOT NULL, "
                    + "`coffeeId` INTEGER NOT NULL, "
                    + "`coffeeName` TEXT, "
                    + "`imageResId` INTEGER NOT NULL, "
                    + "`shot` TEXT, "
                    + "`size` TEXT, "
                    + "`ice` TEXT, "
                    + "`quantity` INTEGER NOT NULL, "
                    + "`unitPrice` REAL NOT NULL, "
                    + "`totalPrice` REAL NOT NULL, "
                    + "FOREIGN KEY(`orderId`) REFERENCES `orders`(`id`) "
                    + "ON UPDATE NO ACTION ON DELETE CASCADE)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_order_items_orderId` "
                    + "ON `order_items` (`orderId`)");
        }
    };

    public abstract CartDao cartDao();

    public abstract OrderDao orderDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "code_cup.db")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return instance;
    }
}
