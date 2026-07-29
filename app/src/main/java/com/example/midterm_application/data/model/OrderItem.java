package com.example.midterm_application.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_items",
        foreignKeys = @ForeignKey(entity = Order.class,
                parentColumns = "id",
                childColumns = "orderId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("orderId")})
public class OrderItem {
    public static final String REWARD_SOURCE_NONE = CartItem.REWARD_SOURCE_NONE;
    public static final String REWARD_SOURCE_POINTS = CartItem.REWARD_SOURCE_POINTS;
    public static final String REWARD_SOURCE_STAMP_CARD = CartItem.REWARD_SOURCE_STAMP_CARD;

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long orderId;
    private int coffeeId;
    private String coffeeName;
    private int imageResId;
    private String shot;
    private String size;
    private String ice;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private String note;
    private String rewardSource = REWARD_SOURCE_NONE;
    private double rewardCoveredAmount;

    public OrderItem() {
    }

    @Ignore
    public OrderItem(long orderId, int coffeeId, String coffeeName, int imageResId,
                     String shot, String size, String ice, int quantity,
                     double unitPrice, double totalPrice) {
        this(orderId, coffeeId, coffeeName, imageResId, shot, size, ice, quantity,
                unitPrice, totalPrice, "");
    }

    @Ignore
    public OrderItem(long orderId, int coffeeId, String coffeeName, int imageResId,
                     String shot, String size, String ice, int quantity,
                     double unitPrice, double totalPrice, String note) {
        this(orderId, coffeeId, coffeeName, imageResId, shot, size, ice, quantity,
                unitPrice, totalPrice, note, REWARD_SOURCE_NONE);
    }

    @Ignore
    public OrderItem(long orderId, int coffeeId, String coffeeName, int imageResId,
                     String shot, String size, String ice, int quantity,
                     double unitPrice, double totalPrice, String note, String rewardSource) {
        this(orderId, coffeeId, coffeeName, imageResId, shot, size, ice, quantity,
                unitPrice, totalPrice, note, rewardSource, 0.00);
    }

    @Ignore
    public OrderItem(long orderId, int coffeeId, String coffeeName, int imageResId,
                     String shot, String size, String ice, int quantity,
                     double unitPrice, double totalPrice, String note, String rewardSource,
                     double rewardCoveredAmount) {
        this.orderId = orderId;
        this.coffeeId = coffeeId;
        this.coffeeName = coffeeName;
        this.imageResId = imageResId;
        this.shot = shot;
        this.size = size;
        this.ice = ice;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.note = note;
        setRewardSource(rewardSource);
        setRewardCoveredAmount(rewardCoveredAmount);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getCoffeeId() {
        return coffeeId;
    }

    public void setCoffeeId(int coffeeId) {
        this.coffeeId = coffeeId;
    }

    public String getCoffeeName() {
        return coffeeName;
    }

    public void setCoffeeName(String coffeeName) {
        this.coffeeName = coffeeName;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getShot() {
        return shot;
    }

    public void setShot(String shot) {
        this.shot = shot;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getIce() {
        return ice;
    }

    public void setIce(String ice) {
        this.ice = ice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getRewardSource() {
        return rewardSource == null ? REWARD_SOURCE_NONE : rewardSource;
    }

    public void setRewardSource(String rewardSource) {
        if (REWARD_SOURCE_POINTS.equals(rewardSource)
                || REWARD_SOURCE_STAMP_CARD.equals(rewardSource)) {
            this.rewardSource = rewardSource;
            return;
        }
        this.rewardSource = REWARD_SOURCE_NONE;
    }

    public boolean isRewardItem() {
        return !REWARD_SOURCE_NONE.equals(getRewardSource());
    }

    public double getRewardCoveredAmount() {
        return rewardCoveredAmount;
    }

    public void setRewardCoveredAmount(double rewardCoveredAmount) {
        this.rewardCoveredAmount = Math.max(0.00, rewardCoveredAmount);
    }
}
