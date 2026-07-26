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
import com.example.midterm_application.data.model.RewardState;
import com.example.midterm_application.data.model.RewardTransaction;

@Database(entities = {CartItem.class, Order.class, OrderItem.class, RewardState.class,
        RewardTransaction.class}, version = 5, exportSchema = false)
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

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `reward_state` "
                    + "(`id` INTEGER NOT NULL, "
                    + "`stampCount` INTEGER NOT NULL, "
                    + "`totalPoints` INTEGER NOT NULL, "
                    + "PRIMARY KEY(`id`))");
            database.execSQL("CREATE TABLE IF NOT EXISTS `reward_transactions` "
                    + "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "`orderId` INTEGER NOT NULL, "
                    + "`createdAt` INTEGER NOT NULL, "
                    + "`type` TEXT, "
                    + "`points` INTEGER NOT NULL, "
                    + "`description` TEXT, "
                    + "FOREIGN KEY(`orderId`) REFERENCES `orders`(`id`) "
                    + "ON UPDATE NO ACTION ON DELETE CASCADE)");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_reward_transactions_orderId_type` "
                    + "ON `reward_transactions` (`orderId`, `type`)");
            database.execSQL("INSERT OR IGNORE INTO `reward_state` "
                    + "(`id`, `stampCount`, `totalPoints`) VALUES (1, 0, 0)");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `reward_transactions_new` "
                    + "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "`orderId` INTEGER, "
                    + "`createdAt` INTEGER NOT NULL, "
                    + "`type` TEXT, "
                    + "`points` INTEGER NOT NULL, "
                    + "`description` TEXT, "
                    + "FOREIGN KEY(`orderId`) REFERENCES `orders`(`id`) "
                    + "ON UPDATE NO ACTION ON DELETE CASCADE)");
            database.execSQL("INSERT INTO `reward_transactions_new` "
                    + "(`id`, `orderId`, `createdAt`, `type`, `points`, `description`) "
                    + "SELECT `id`, `orderId`, `createdAt`, `type`, `points`, `description` "
                    + "FROM `reward_transactions`");
            database.execSQL("DROP INDEX IF EXISTS `index_reward_transactions_orderId_type`");
            database.execSQL("DROP TABLE `reward_transactions`");
            database.execSQL("ALTER TABLE `reward_transactions_new` RENAME TO `reward_transactions`");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_reward_transactions_orderId_type` "
                    + "ON `reward_transactions` (`orderId`, `type`)");
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `cart_items` ADD COLUMN `note` TEXT");
            database.execSQL("ALTER TABLE `order_items` ADD COLUMN `note` TEXT");
        }
    };

    public abstract CartDao cartDao();

    public abstract OrderDao orderDao();

    public abstract RewardDao rewardDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "code_cup.db")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                            .build();
                }
            }
        }
        return instance;
    }
}
