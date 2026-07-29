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
import com.example.midterm_application.data.model.OrderReview;
import com.example.midterm_application.data.model.RewardState;
import com.example.midterm_application.data.model.RewardTransaction;

@Database(entities = {CartItem.class, Order.class, OrderItem.class, RewardState.class,
        RewardTransaction.class, OrderReview.class}, version = 9, exportSchema = false)
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

    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `orders` ADD COLUMN `subtotal` REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE `orders` ADD COLUMN `promoCode` TEXT");
            database.execSQL("ALTER TABLE `orders` ADD COLUMN `promoDiscount` REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE `orders` ADD COLUMN `loyaltyDiscount` REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE `orders` ADD COLUMN `finalTotal` REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE `orders` ADD COLUMN `deliveryAddress` TEXT");
            database.execSQL("ALTER TABLE `orders` ADD COLUMN `loyaltyRewardUsed` INTEGER NOT NULL DEFAULT 0");
            database.execSQL("UPDATE `orders` SET `subtotal` = `totalPrice`, `finalTotal` = `totalPrice` "
                    + "WHERE `subtotal` = 0 AND `finalTotal` = 0");
        }
    };

    private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `orders` ADD COLUMN `deliveryType` TEXT DEFAULT 'ASAP'");
            database.execSQL("ALTER TABLE `orders` ADD COLUMN `scheduledAt` INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `order_reviews` "
                    + "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "`orderId` INTEGER NOT NULL, "
                    + "`rating` INTEGER NOT NULL, "
                    + "`comment` TEXT, "
                    + "`createdAt` INTEGER NOT NULL, "
                    + "`updatedAt` INTEGER NOT NULL, "
                    + "FOREIGN KEY(`orderId`) REFERENCES `orders`(`id`) "
                    + "ON UPDATE NO ACTION ON DELETE CASCADE)");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_order_reviews_orderId` "
                    + "ON `order_reviews` (`orderId`)");
        }
    };

    private static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `cart_items` ADD COLUMN `rewardSource` TEXT DEFAULT 'NONE'");
            database.execSQL("ALTER TABLE `order_items` ADD COLUMN `rewardSource` TEXT DEFAULT 'NONE'");
        }
    };

    public abstract CartDao cartDao();

    public abstract OrderDao orderDao();

    public abstract OrderReviewDao orderReviewDao();

    public abstract RewardDao rewardDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "code_cup.db")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4,
                                    MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8,
                                    MIGRATION_8_9)
                            .build();
                }
            }
        }
        return instance;
    }
}
