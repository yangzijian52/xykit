# XyKit Paper 26.1 Upgrade Notes

## Scope

This document records the Paper 26.1 compatibility upgrade for `XyKit`.

- Project version: `1.3.2`
- Target Paper API: `26.1.1.build.20-alpha`
- Required Java version: `25+`
- Upgrade date: `2026-04-05`

## What changed

- Updated Maven dependency from `1.21.11-R0.1-SNAPSHOT` to `26.1.1.build.20-alpha`
- Updated compiler target from Java `21` to Java `25`
- Updated plugin metadata and README to reflect Paper 26.1 support
- Preserved existing local feature changes already present in the working tree

## Notes

- Paper's current 26.1 API artifacts are published as alpha builds in the PaperMC Maven repository.
- The plugin still uses the Bukkit-style `plugin.yml` entrypoint and does not require `paper-plugin.yml` for this upgrade.
- No Paper 26.1 specific code changes were required after a source review; this upgrade is primarily dependency, runtime, and documentation alignment.

## Verification

- Source review completed for version-sensitive files
- Local Java runtime confirmed as `25.0.2`
- Maven was not preinstalled on this machine, so a local build should be run after Maven is available or downloaded temporarily

## Release checklist

1. Build the project with Java 25.
2. Test `/kit`, `/cdk`, `/kit backup`, and `/kit restore confirm` on a Paper 26.1 server.
3. Verify data migration with an existing `data.yml`.
4. Push the release commit and create a GitHub release if needed.
