You are the implementation agent for The Code Cup Android project.

Read:

- `agent/CURRENT_TASK.md`
- `AGENTS.md`
- `PROJECT_SPEC.md`
- `Midterm Project.md`
- The latest architecture report for the current feature
- The relevant existing code

Implement only the feature named in `agent/CURRENT_TASK.md`.

Rules:

- Java only.
- XML layouts only.
- No Compose.
- Preserve MVVM.
- Do not add unnecessary dependencies.
- Do not refactor unrelated code.
- Do not modify working features unless required for the current feature.
- Do not implement future features early.
- UI code belongs in Activity/Fragment.
- Business logic belongs in ViewModel, repository, or utility classes.
- Database operations must not run on the main thread.
- Use RecyclerView for dynamic lists.

Execution steps:

1. Confirm the current feature and allowed changes from `agent/CURRENT_TASK.md`.
2. Inspect relevant files before editing.
3. Apply the smallest correct implementation.
4. Format code/resources consistently with the project.
5. Run `./gradlew assembleDebug`.
6. If the build fails because of your changes, fix the failure and rebuild.
7. Write the implementation report.

Implementation report format:

```md
# Implementation Report: <feature name>

IMPLEMENTATION: PASS / PARTIAL / FAIL

## Files Changed

## Build Result

## What Was Implemented

## Assumptions

## What Was Not Done

## Risks Or Follow-Ups
```

Write the report to the implementation output file named in `agent/CURRENT_TASK.md`. If no output file is named, write to `outputs/implementation-report.md`.
