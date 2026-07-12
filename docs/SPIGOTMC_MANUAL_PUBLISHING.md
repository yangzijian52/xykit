# XyKit SpigotMC Manual Publishing Guide

## Release Decision

- **Resource type:** Free
- **Price:** $0.00
- **License:** MIT License
- **Plugin version:** 1.3.3
- **Upload file:** `target/xykit-1.3.3.jar`
- **Upload size:** 22,422 bytes
- **SHA-256:** `1e80e732776263d90c854aecf9d0eaa0a1d683304b4618d1b0b21e727591dea0`
- **GitHub release:** https://github.com/yangzijian52/xykit/releases/tag/1.3.3
- **Source code:** https://github.com/yangzijian52/xykit
- **Resource-page BBCode:** `docs/SPIGOTMC-RESOURCE.md`
- **Full-documentation BBCode:** `docs/SPIGOTMC-RESOURCE-BBCODE.txt`

XyKit is best published as a free resource because it is a small MIT-licensed utility whose complete source and binaries are already public. It has no premium service, licensing system, commercial dependency or separate paid feature set.

## Suggested SpigotMC Fields

| Field | Suggested value |
|---|---|
| Resource title | XyKit |
| Tag line | Starter kits, redeemable CDK codes and safe YAML data management |
| Resource type | Free |
| Price | $0.00 |
| Version | 1.3.3 |
| Category | Tools and Utilities, or the closest server-administration category available |
| Supported software | Paper |
| Supported version | Paper 26.2.x; select the closest matching version offered by the form |
| Required Java | Java 25 or newer |
| External dependencies | None |
| Source URL | https://github.com/yangzijian52/xykit |
| Support URL | https://github.com/yangzijian52/xykit/issues |
| License | MIT |

Paste the complete contents of `docs/SPIGOTMC-RESOURCE.md` into the main resource description. Use `docs/SPIGOTMC-RESOURCE-BBCODE.txt` for the documentation/update page or a dedicated documentation section.

## Important Warnings

- The SpigotMC resource page, documentation and support channel are English-only. Chinese-language support is not provided on SpigotMC.
- The plugin's runtime messages, administrator feedback, console output and bundled configuration comments are currently Chinese.
- Claim support only for Paper 26.2.x with Java 25 or newer. Do not claim native Spigot or fork compatibility without testing it.
- The 1.3.3 Paper 26.2 update has a successful Maven build but no additional live Paper 26.2 server test.
- Storage is YAML-only. The existing MySQL-looking configuration block is not connected to an implemented MySQL backend.
- The configured `cooldown` value is not enforced; starter kits are one-time claims.
- `/kit create` is informational and does not add a kit to the configuration.
- `op:` kit actions temporarily grant operator status. Warn administrators to use only trusted commands.
- Always tell users to back up `plugins/XyKit/data.yml` before upgrades.
- Upload the release JAR, not the repository's source archive.

## Manual Publish Steps

1. Sign in to SpigotMC and choose the option to add a new resource.
2. Select a free resource and set the price to `$0.00` if the form displays a price field.
3. Enter the suggested title, tag line, version, category, platform and links above.
4. Paste `docs/SPIGOTMC-RESOURCE.md` into the resource description without converting the BBCode to Markdown.
5. Upload `target/xykit-1.3.3.jar`.
6. Add `docs/SPIGOTMC-RESOURCE-BBCODE.txt` as the full documentation text or as the first documentation/update entry, depending on the available SpigotMC editor.
7. Confirm that the English-only support notice is visible near the top of the page.
8. Confirm that Paper 26.2.x, Java 25+, the Chinese runtime interface and the lack of live Paper 26.2 testing are clearly disclosed.
9. Preview every BBCode heading, list, code block and link before publishing.
10. Publish manually, then verify the public download returns `xykit-1.3.3.jar` and that the displayed version is `1.3.3`.
11. Add the final SpigotMC resource URL to the GitHub README later if desired.
