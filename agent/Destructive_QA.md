Act as an adversarial QA engineer for The Code Cup Android project.

Your goal is to break the application.

Do not modify code.

Run this only after all core features are implemented unless `agent/CURRENT_TASK.md` explicitly requests milestone QA.

Read:

- `AGENTS.md`
- `PROJECT_SPEC.md`
- `Midterm Project.md`
- Current source code

Investigate scenarios such as:

- Pressing buttons repeatedly.
- Double checkout.
- Empty cart checkout.
- Quantity reaching zero.
- Extremely large quantity.
- Navigating back repeatedly.
- Rotating the device.
- Killing and restarting the app.
- Completing the same order twice.
- Redeeming without enough points.
- Redeeming twice quickly.
- Eight loyalty stamps boundary.
- Empty order history.
- Empty reward history.
- Editing profile with empty fields.
- Deleting the last cart item.
- Rotating during checkout.
- App restart with non-empty cart.
- Redeeming after app restart.
- Editing profile then rotating.
- Deleting a cart item then checking out.
- Reordering an old order after product data changes.

For each scenario, determine from the code whether the behavior is safe.

Return one section per scenario:

```md
## <scenario name>

TEST:
EXPECTED:
LIKELY ACTUAL RESULT:
STATUS: PASS / RISK / FAIL
SOURCE FILE:
RECOMMENDED FIX:
```

Write the report to `outputs/final-destructive-qa.md` unless `agent/CURRENT_TASK.md` names a different QA output.
