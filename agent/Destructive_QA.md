Act as an adversarial QA engineer.

Your goal is to break the application.

Do not modify code.

Investigate scenarios such as:

- pressing buttons repeatedly
- double checkout
- empty cart checkout
- quantity reaching zero
- extremely large quantity
- navigating back repeatedly
- rotating the device
- killing and restarting the app
- completing the same order twice
- redeeming without enough points
- redeeming twice quickly
- eight loyalty stamps boundary
- empty order history
- empty reward history
- editing profile with empty fields
- deleting the last cart item

For each scenario determine from the code whether the behavior is safe.

Return:

TEST
EXPECTED
LIKELY ACTUAL RESULT
PASS / RISK / FAIL
SOURCE FILE