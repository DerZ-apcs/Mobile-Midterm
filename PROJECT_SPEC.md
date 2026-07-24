# The Code Cup Project Specification

## App Scope

The Code Cup is a native Android application for a fictional coffee shop. Users can browse coffee products, customize drinks, add items to a cart, complete orders, earn loyalty stamps and reward points, redeem rewards, and manage a simple profile.

This file is the implementation source of truth for coding agents. If this file conflicts with `Midterm Project.md`, the grading rubric in `Midterm Project.md` wins.

## Platform And Architecture

- Native Android targeting Android devices.
- Java source files only.
- XML layouts only.
- No Kotlin source files.
- No Jetpack Compose.
- MVVM architecture.
- UI code belongs in Activity/Fragment classes.
- Business logic belongs in ViewModels, repositories, or utility classes.
- Room is used for structured persistent data.
- SharedPreferences is used only for lightweight profile/settings data.
- RecyclerView is used for all dynamic lists.
- Database operations must not run on the main thread.

## Package Structure

Use package `com.example.midterm_application` with these subpackages:

- `data/model`
- `data/local`
- `data/repository`
- `ui`
- `viewmodel`
- `utils`

## Data Model

### Coffee Product

Fields:

- id
- name
- description
- basePrice
- imageResourceName or imageResourceId
- category
- isFavorite

Persistence:

- Room table seeded on first launch.

### Cart Item

Fields:

- id
- coffeeId
- coffeeName
- basePrice
- quantity
- shot: `single` or `double`
- size: `small`, `medium`, or `large`
- ice: `no`, `less`, or `normal`
- itemTotal

Persistence:

- Room table.
- Cart should survive configuration changes and app restart until checkout or deletion.

### Order

Fields:

- id
- createdAt
- totalAmount
- pointsAwarded
- itemSummary

Persistence:

- Room table.

### Reward Transaction

Fields:

- id
- createdAt
- description
- pointsDelta
- orderId when applicable

Persistence:

- Room table.

### Loyalty State

Fields:

- stampCount, from 0 to 8

Persistence:

- Room single-row table or SharedPreferences. Prefer Room if it is tied to order/reward state.

### User Profile

Fields:

- name
- email
- phone
- favoriteDrink optional

Persistence:

- SharedPreferences.

## Business Logic

### Price Calculation

Base price comes from the selected coffee product.

Modifiers:

- Single shot: no extra charge.
- Double shot: add a fixed extra charge.
- Small size: no extra charge.
- Medium size: add a fixed extra charge.
- Large size: add a fixed extra charge.
- Ice option does not change price unless the implementation explicitly documents a small modifier.

Rules:

- Quantity cannot be less than 1.
- Quantity should have a practical upper limit to avoid accidental huge orders.
- Item total equals customized unit price multiplied by quantity.
- Cart total equals the sum of all cart item totals.
- Price calculation should exist in one reusable utility or repository method, not duplicated across screens.

### Checkout

Checkout requires a non-empty cart.

When checkout succeeds:

- Create an order record.
- Create reward transaction for earned points.
- Increment loyalty stamp count by one, up to a maximum of 8.
- Clear the cart.
- Navigate or show confirmation without allowing accidental duplicate checkout.

### Reward Points

Recommended formula:

- `pointsAwarded = floor(orderTotal)`

Rules:

- Points must persist after app restart.
- Total points equals the sum of reward transactions.
- Redeeming a reward creates a negative reward transaction.
- Redeeming must be blocked when total points are insufficient.

### Loyalty Stamps

Rules:

- Each completed order adds one stamp.
- Stamp count cannot exceed 8.
- User can reset the card to 0 only when stamp count is 8.
- Reset behavior should be obvious in the Rewards screen.

## Screens

### Home Screen

Required UI:

- Header for The Code Cup.
- Loyalty card preview showing current stamps out of 8.
- Coffee RecyclerView or grid.
- Bottom navigation.

Required behavior:

- Seed and display coffee products.
- Tapping a coffee item opens the Details screen for that product.
- Favorite and search/filter behavior may be added as user-defined features after core behavior works.

### Details Screen

Required UI:

- Coffee name, description, and price.
- Quantity controls.
- Shot selector: single/double.
- Size selector: small/medium/large.
- Ice selector: no/less/normal.
- Dynamic price display.
- Cart preview.
- Add to cart button.

Required behavior:

- Price updates when options or quantity change.
- Add to cart persists the customized item.
- Invalid quantity is blocked.
- Back navigation returns to Home without losing existing cart data.

### Cart Screen

Required UI:

- RecyclerView of cart items.
- Customization summary for each item.
- Item totals and aggregate total.
- Delete control for cart items. Swipe delete is preferred if practical.
- Checkout button.

Required behavior:

- Empty cart state is displayed.
- Checkout is blocked for an empty cart.
- Deleting the last item returns to the empty cart state.
- Checkout creates an order, rewards, loyalty update, and clears the cart.

### Rewards Screen

Required UI:

- Loyalty stamp card.
- Total points.
- Reward transaction RecyclerView.
- Redeem reward controls.
- Reset stamp card control when eligible.

Required behavior:

- Completed orders increment stamps and points.
- Stamp count resets only at 8.
- Reward transaction history persists.
- Redeeming points decrements total points.
- Redeem without enough points is blocked.

### Profile Screen

Required UI:

- Profile display mode.
- Edit icon or button.
- Editable name, email, phone, and optional favorite drink fields.
- Save/cancel controls.

Required behavior:

- Edit mode is enabled by user action.
- Saved profile data persists in SharedPreferences.
- Empty required fields are handled safely.
- State survives configuration changes where appropriate.

### Order History Screen Or Section

User-defined feature.

Required behavior:

- Show previous orders from Room.
- Show order total and item summary.
- Optional reorder action can add previous items back to cart.

## Navigation

Use one Activity with multiple fragments or multiple Activities. Prefer the simplest implementation that keeps navigation clear.

Required destinations:

- Home
- Details
- Cart
- Rewards
- Profile

Bottom navigation must allow switching between main areas. Details can be opened from Home and returned from using back navigation.

## User-Defined Features

To target the high-value user-defined requirement, implement these after core features compile and pass review:

- Persistent cart after app restart.
- Order history.
- Reorder previous order.
- Favorite coffee items.
- Search/filter coffee list.

Do not implement these before the core rubric features are working.

## Feature Implementation Order

1. Project setup and Java/XML migration.
2. Data layer: Room entities, DAO, database, repositories, seed data.
3. Home screen.
4. Details screen.
5. Cart screen.
6. Checkout/order completion.
7. Rewards screen.
8. Redeem rewards.
9. Profile screen.
10. User-defined features.
11. Final architecture audit, destructive QA, rubric audit, and release build.

## Definition Of Done

A feature is done only when:

- `./gradlew assembleDebug` passes.
- The implemented feature matches this spec and `Midterm Project.md`.
- State survives configuration changes where required.
- Persistent data survives app restart where required.
- Existing navigation still works.
- No unrelated feature was modified unnecessarily.
- The feature output report was written under `outputs/`.
