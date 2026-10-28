# COC Helper — Project Plan

An Android companion app for Clash of Clans: base layout recommendations, upgrade suggestions,
enemy base scouting, and attack plans with army + deployment hints.

> **Living document** — update the status checkboxes and the Changelog at the bottom as work lands.

---

## 1. Ground rules (Terms of Service)

Supercell's ToS bans bots and third-party software that interacts with the game client.
Accounts using them get banned. So the app **never**:

- taps/plays the game for you (no Accessibility-Service automation, no input injection),
- reads the game screen live during attacks, or draws an overlay on top of the game,
- modifies the game client or its traffic.

"Auto-execute" uses the game's **own share links** (`link.clashofclans.com`). The app opens a
link, and the game shows its native "Copy layout?" or "Copy army?" prompt. You confirm with one tap.

### ToS review (Terms of Service effective 2024-11-06, §1.1 "Use Limitations")

| Clause | Effect on this app |
|---|---|
| No "automation software, emulators, bots, hacks, mods or any unauthorized third-party software designed to modify or interfere with … any Supercell game experience" | No auto-tapping, no input injection, no overlay on the game. Share links only. |
| No obtaining "any information from the Service or any Supercell game using any method not expressly permitted by Supercell" | All data comes from permitted sources: the **official API**, **in-game share links**, and the **in-game data export** (if it exists). No packet sniffing, private endpoints, or reading game files/memory. |
| No reverse engineering or modifying game files | Don't extract game assets. Icons/art come from the official Fan Kit or our own art (§2 Ownership). |
| Don't share Login Information | The app **never** asks for Supercell ID or game login. It only needs a public player tag + your own API key. |
| Don't overburden servers | Cache API responses in Room; refresh no more than every few minutes. |
| Non-commercial use; Supercell owns names and art | Fine for personal use. Publishing it (e.g. Play Store) requires following the **Fan Content Policy** (disclaimer, no official-looking branding). |
| "Any act Supercell deems in conflict with the spirit … of the Service" | Supercell decides; another reason to stay fully outside the game client. |

**Feature verdicts**
- Upgrades (official API) — OK
- Layout apply / army equip (in-game share links) — OK
- Deployment hints shown in-app *before* an attack — OK (same as a guide or video)
- Screenshot scouting — **Gray area, low risk.** It's offline analysis of a screenshot you take, with no contact with the game.
  It could be read as "obtaining information by a method not expressly permitted". Keep it user-initiated, never run it automatically or during an attack.
- Live overlay or auto-play during attacks — **Prohibited**, out of scope

**Still to read:** Safe and Fair Play Policy, Fan Content Policy, and the developer API Terms of Use
(check that sending your key through the RoyaleAPI proxy is acceptable, or run your own proxy instead).

## 2. Features

| Feature | How it works |
|---|---|
| **Base layout recommend + apply** | Pick a layout by Town Hall and purpose (war / CWL / farming / trophy) from the bundled library and your own saved links. **Apply** opens the official layout link. |
| **Upgrade suggestions** | Heroes, troops, spells, pets and equipment come from the official API. A rule engine ranks upgrades against Town Hall caps. Buildings come later, via the game's account-data export, if available. |
| **Enemy base detection** | You share a screenshot of the scouted base. Defenses are marked by hand at first, then detected by an on-device TFLite model. |
| **Best army + deployment hints** | Base analysis feeds a strategy picker. **Equip** opens the army share link in the game. A step-by-step plan (entry side, funnel, spell timing) is shown before you attack. |

## 3. Tech stack

- Kotlin, Jetpack Compose (Material 3), Navigation Compose
- MVVM + unidirectional state (`StateFlow`), Hilt for DI
- Coroutines/Flow, Retrofit or OkHttp + kotlinx.serialization
- Room (cache, saved layouts/plans), DataStore (settings)
- Photo Picker + TensorFlow Lite (scouting)
- JUnit for pure-Kotlin domain/link logic
- Toolchain (from the Android Studio wizard, 2026-10-28; Studio build AI-262):

| Item | Version |
|---|---|
| Gradle | 9.6.0 |
| Android Gradle Plugin (`agp`) | 9.4.1 |
| Kotlin | **2.4.10** (wizard default was 2.2.10; current libraries need 2.4) |
| KSP / Hilt | 2.3.12 / 2.60.1 |
| Navigation / Lifecycle | 2.10.2 / 2.11.0 |
| kotlinx-serialization / coroutines | 1.12.0 / 1.11.0 |
| Compose BOM | 2026.02.01 |
| compileSdk / targetSdk | 37 (`compileSdk { version = release(37) }`) |
| minSdk | 26 |
| Java target | 17 |

  **AGP 9 has Kotlin built in.** Don't apply `org.jetbrains.kotlin.android` and don't use a `kotlinOptions {}` block.
  Plugins are `com.android.application` + `org.jetbrains.kotlin.plugin.compose` (+ `kotlin.plugin.serialization`).
  Release builds use the new `optimization {}` block. Use a version catalog (`gradle/libs.versions.toml`).

## 4. Module structure

```
COCHelper/
├─ app/                    # MainActivity, nav graph, Hilt app, theme wiring
├─ core/
│  ├─ model/               # Pure Kotlin data classes: Player, Unit, Layout, Strategy, BaseMarker…
│  ├─ network/             # CocApi, DTOs → model mappers, auth interceptor
│  ├─ database/            # Room: cached player, user layouts, saved attack plans
│  ├─ datastore/           # Player tag, API token, proxy base URL, prefs
│  ├─ gamedata/            # Bundled JSON + loaders: unit ids, TH caps, layouts, strategies
│  ├─ links/               # Build/parse layout & army share links, launch intents (pure, tested)
│  └─ ui/                  # Shared Compose components, theme
├─ feature/
│  ├─ profile/             # Account setup, player overview
│  ├─ upgrades/            # UpgradeAdvisor + screen
│  ├─ layouts/             # LayoutRecommender + browse / filter / import-from-link
│  ├─ scout/               # Screenshot pick, marker editor, BaseDetector (manual → TFLite)
│  └─ attack/              # ArmyRecommender, AttackPlan screen (army + Equip + steps)
└─ ml/                     # Not an Android module: Python notebooks for labeling + YOLO training → .tflite
```

Each feature module uses the same layout:

```
feature/<name>/
├─ domain/   # pure Kotlin logic (unit-tested on the JVM)
├─ data/     # repositories
└─ ui/       # Screen.kt, ViewModel.kt, components
```

### Data flow

```
Official API ──► network ──► Room cache ──► UpgradeAdvisor ──► Upgrades screen
gamedata JSON ─┬─► LayoutRecommender ──► Layouts screen ──► share link ──► game
               └─► ArmyRecommender ◄── BaseAnalyzer ◄── BaseDetector ◄── screenshot
                         └──► AttackPlan (army link + deployment steps)
```

## 5. Known constraints and open questions

- **API keys are tied to an IP address.** A phone's IP keeps changing, so use the RoyaleAPI proxy
  (`https://cocproxy.royaleapi.dev/v1`, key whitelisted for `45.79.218.79`) or run your own backend.
- **The API only gives each unit's overall `maxLevel`, not the Town Hall cap.** We need a maintained TH-cap table in `core/gamedata`.
- **Building levels aren't in the API.** Check whether the in-game account-data export (JSON) exists and what format it uses.
- **Army link unit IDs aren't officially documented.** Partly checked against real links (see §5.1); keep collecting samples.

### 5.1 Share link formats (from real links, 2026-10-28)

**Layout:** `https://link.clashofclans.com/{lang}?action=OpenLayout&id=TH11%3AHV%3A{opaque}`
- `id` decodes to `TH{level}:{village}:{opaque}`. `HV` = home village. Parse the TH level + village from it when importing.
- `{lang}` (e.g. `en`, `cn`) is only the language; any value works.

**Army:** `https://link.clashofclans.com/{lang}?action=CopyArmy&army={spec}`

Sample: `h2m1e22_5-1e17_48-6e42_44i1x2-1x51-1x6-3x83d1x5u8x59-20x0s11x0`

| Section | Format | Sample | Meaning |
|---|---|---|---|
| `h` | `{heroId}[m{petId}][e{equipA}_{equipB}]`, joined by `-` | `2m1e22_5` | Grand Warden + pet 1 + equipment 22, 5 |
| `i` | `{count}x{id}`, joined by `-` | `1x2-1x51-1x6-3x83` | Clan castle troops/siege (**unverified**) |
| `d` | `{count}x{id}` | `1x5` | Clan castle spell (**unverified**) |
| `u` | `{count}x{troopId}`, joined by `-` | `8x59-20x0` | 8 Electro Dragon + 20 Barbarian = 260 housing |
| `s` | `{count}x{spellId}`, joined by `-` | `11x0` | 11 Lightning |

**Cross-checked IDs** (export indices vs. API levels, in matching order). These are in `core/gamedata/.../game_ids.json`:
troops 0–13, 15, 17, 22–24, 58, 59; spells 0–3, 5, 9–11, 16, 17, 28, 35; heroes 0, 1, 2, 6.
Earlier confirmed from links: **Troops:** 0 Barbarian, 59 Electro Dragon. **Spells:** 0 Lightning. **Heroes:** 1 Archer Queen, 2 Grand Warden, 6 Minion Prince
(0 Barbarian King and 4 Royal Champion assumed). **Siege (probable):** 51 Wall Wrecker.
Save more in-game army links as test fixtures to fill in the ID table.

### 5.2 In-game data export (JSON)

The player copies it in the game (Settings → export base data) and pastes it into the app. Top-level keys:
`tag, timestamp, helpers, buildings, traps, decos, obstacles, units, siege_machines, heroes, spells, pets,
equipment, house_parts, skins, sceneries, buildings2 (builder base), …`

- Entries look like `{"data": <globalId>, "lvl": n, "cnt": n}`. Same-level items are grouped by `cnt`.
  An item being upgraded appears on its own with `timer` (seconds left), plus optional `helper_timer` / `gear_up`.
- **Global ID = category prefix + index**, and the index equals the share-link ID:

| Prefix | Category | Example |
|---|---|---|
| `1000xxx` | Buildings | `1000001` Town Hall, `1000010` Walls, `1000015` Builder's Hut, `1000097` Crafted defense |
| `4000xxx` | Troops | `4000059` Electro Dragon = army link `x59` |
| `8000xxx` | Obstacles | |
| `12000xxx` | Traps | |
| `18000xxx` | Decorations | |
| `26000xxx` | Spells | `26000000` Lightning = army link `s…x0` |
| `28000xxx` | Heroes | `28000006` Minion Prince = army link `h6` |
| `90000xxx` | Hero equipment | `90000022` = army link `e22` |
| `93000xxx` | Helpers | Builder's Apprentice / Lab Assistant / Alchemist |

- So `core/gamedata` keeps **one ID table** (index → name, category, housing, TH caps). It is used for the export, army links and the API.
- **Source rule (ToS):** build ID→name, costs, times and TH caps from public community sources (Clash of Clans wiki).
  **Never** extract them from the game files.
- The export has timers, so the app can show a **builder / lab schedule** (when each builder frees up).
- Troop 58 is **Ice Golem** (a regular dark troop). The API lists it among the super troops; the advisor must not filter it out.
- **Truncation risk:** two pastes both stopped at the same spot (~6.4k chars, inside `buildings2`). The parser must accept
  a truncated export: keep every complete home-village section and ignore the cut-off tail. If the phone clipboard is the
  limit, also support importing from a text file or the Android share sheet.
- **The detection model needs training data.** That means a few hundred labeled base screenshots. The manual marker editor produces those labels.

## 6. Milestones

### P0 — Pre-flight checks (no code)
- [x] Read Terms of Service and map its clauses to features (§1)
- [ ] Read the Safe and Fair Play Policy, Fan Content Policy, and developer API Terms of Use
- [x] Create an API key (IP `45.79.218.79`) and `curl` your own player through the proxy
- [~] **Deferred (user's call):** keep using the current key during development. Revoke and replace it before
  any release or sharing of the app. Keep it only in `local.properties`, never committed.
- [x] Copy a layout link + army link in the game; tapping each on the phone opens the game's copy prompt
- [x] Look for the in-game account-data export (exists: Settings → export base data as JSON)
- [x] Save a sample export → `samples/export_home_truncated.json` (home village complete; cut at `buildings2`, closed by hand)
- [ ] Find where the truncation happens: paste the export into a phone notes app. Does it end with `}`?
  If the **phone clipboard** cuts it off, the app can't rely on paste and needs another import path (see §5.2).
- [x] Android Studio fixed (missing Start Menu shortcut) + phone connected over adb (Galaxy S25 Ultra, SM-S938U1)
- [x] Create the "Empty Activity" template (`D:\documents\web\Project\StudioTest`); versions recorded in §3
- [x] Run the template on the phone (▶) and see "Hello Android!" (installed as `com.example.studiotest`)

### M0 — Project setup ✅
- [x] Multi-module Gradle project (`app`, `core/{model,network,datastore,gamedata,ui}`, `feature/{profile,upgrades,layouts,scout,attack}`), version catalog
- [x] Convention plugins in `build-logic/` (`cochelper.android.application|library|compose|feature`, `cochelper.hilt`, `cochelper.jvm.library`)
- [x] Hilt, type-safe Navigation, theme, bottom nav shell (4 tabs + Account in top bar)
- [x] Build + run on the phone (`gradlew installDebug`), verified by screenshot
- [x] Early scaffold moved into `core/model`, `core/network`, `core/datastore`, `core/gamedata`; old Gradle files replaced
- [x] Dev API token + player tag read from `local.properties` → `BuildConfig` defaults in `core/datastore`
- `core/database` and `core/links` will be created when first needed (M1 and M3).

### M1 — Profile (official API)
- [ ] Settings screen: player tag, API token, base URL
- [ ] `CocApi.player()` + error handling (403 invalid IP / bad key, 404 tag)
- [ ] Cache the player in Room; show the profile overview

### M2 — Upgrade advisor
- [ ] TH-cap table for heroes / troops / spells / pets / equipment
- [ ] `UpgradeAdvisor` scoring (category weight × distance to TH cap) + unit tests
- [ ] Upgrades screen with ranked list and reasons
- [ ] Import the data export (paste JSON) → buildings, traps, walls, pets, siege; parser + unit tests using `samples/export.json`
- [ ] Building upgrade suggestions (defenses, resources, army buildings) against TH caps
- [ ] Builder / lab schedule from `timer` fields: when each builder frees up, what to start next

### M3 — Layouts
- [ ] `layouts.json` format + loader
- [ ] Import a layout by pasting a share link (parse TH from the link id), saved in Room
- [ ] `LayoutRecommender` (TH + purpose + rating) + Layouts screen
- [ ] "Apply" opens the share link in the game

### M4 — Scout (manual)
- [ ] Photo Picker → screenshot view
- [ ] Marker editor: tap to place TH / Eagle / AD / Inferno / Scattershot / CC / Monolith…
- [ ] `BaseAnalyzer`: threat per side (4 diamond edges), air vs. ground threat → weak side
- [ ] Export markers as labels (feeds M6)

### M5 — Attack plan
- [ ] `strategies.json` (troops, spells, TH range, type, steps with `{side}`)
- [ ] `ArmyRecommender` picks a strategy from the analysis + player levels
- [ ] Army share-link builder + unit tests; check IDs against a real link
- [ ] Attack plan screen: army, **Equip** button, numbered deployment steps
- [ ] Save plans per war target

### M6 — On-device detection
- [ ] Collect + label a screenshot dataset (`ml/`)
- [ ] Train a YOLO model, export a quantized `.tflite`
- [ ] `TfliteDetector` behind the `BaseDetector` interface; user can correct the detections

### M7 — Polish
- [ ] Clan war integration (current war opponents via API)
- [ ] Release build, icon, README

## 7. Changelog

- **2026-10-28** — Plan created. An early single-module scaffold already exists: Gradle files, manifest,
  `app/src/main/java/com/cochelper/data/{Models,CocApi,SettingsStore,AssetRepository}.kt`.
  These move into the core modules in M0.
- **2026-10-28** — Reviewed the Terms of Service (2024-11-06). Added the clause-to-feature table (§1) and the P0 pre-flight milestone.
  Result: the plan is compliant. Screenshot scouting is a low-risk gray area. The app never handles login credentials.
- **2026-10-28** — P0 API test passed via the RoyaleAPI proxy (player #GVJGG2JQV, TH11). Findings that affect the design:
  - `maxLevel` is the overall game max (e.g. Barbarian King 110), not the TH cap. This confirms the TH-cap table is required for M2.
  - `troops` includes **super troops** (all level 1). They are temporary boosts, not upgrades, so the advisor must filter them out by name.
  - `troops` also includes builder-base units; filter by `village == "home"`. Heroes now include Minion Prince.
  - `heroEquipment` is present (17 items), so equipment upgrades are in scope for M2.
  - Other useful fields: `clan`, `warPreference`, `leagueTier`, `labels`, `achievements`.
- **2026-10-28** — P0 share links passed: layout and army links both open the game's copy prompt on the phone.
  Decoded the formats (§5.1). Army links also carry **heroes, pets, equipment and clan castle**, so M5's "Equip" can set
  the full loadout. The layout id contains the TH level, so imports can be sorted automatically.
- **2026-10-28** — P0 data export confirmed: the game exports base data as JSON (§5.2). Its global IDs share indices with army
  links, so one ID table covers both. **Building upgrades and a builder/lab schedule move from M7 into M2.**
  Corrected Ice Golem (troop 58) as a regular troop. Rule added: ID names, costs and times come from public sources only.
- **2026-10-28** — Saved `samples/export_home_truncated.json` (validated: 41 building entries, 21 units, 36 equipment).
  The export was cut at `buildings2` twice, in the same place. M2 parser must handle truncated input; still need to find
  where the cut happens.
- **2026-10-28** — Android Studio launches (added the missing Start Menu shortcut). The phone is authorized over adb.
  The wizard template uses Gradle 9.6 / AGP 9.4.1 / Kotlin 2.2.10 / Compose BOM 2026.02.01 / SDK 37, recorded in §3.
  AGP 9's built-in Kotlin makes the early scaffold's Gradle files obsolete; they get replaced in M0.
- **2026-10-28** — Template app builds and runs on the S25 Ultra. The toolchain is fully confirmed. Remaining P0 items
  (policy reading, the clipboard truncation check) don't block M0.
- **2026-10-28** — **M0 done.** 11-module project with `build-logic` convention plugins builds and runs on the S25 Ultra
  (bottom-nav shell + Account page reading settings via Hilt/DataStore). Bumped Kotlin 2.2.10 → 2.4.10 because
  kotlinx-serialization 1.12 and current AndroidX need it. Java target 17. Seeded `game_ids.json` with troop, spell and hero
  IDs cross-checked between the export and the API.
  Build from the CLI: `JAVA_HOME="D:\Program Files\Android\jbr"` then `gradlew installDebug`.
