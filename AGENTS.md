# Android / Kotlin / Yomiru Development Rules

## General Principles
- **Primary Language:** Kotlin is the primary language for all app and shared code.
- **Modern Android APIs:** Follow official Google Android development guidelines, preferring Jetpack Compose, Material 3 Expressive UI, Kotlin Coroutines, and DataStore.
- **Architecture Consistency:** Maintain existing architecture (Koin DI, Ktor HttpClient, Navigation3, Paging3, StateFlow, ViewModel) rather than introducing conflicting patterns.
- **Dependency Reuse:** Reuse existing libraries and helper utilities before introducing new external dependencies.
- **No API Fabrication:** Do not invent non-existent library APIs. Use Context7 or official documentation when API signatures or behaviors are uncertain.

## Mandatory Responsive UI Screen Matrix (Android Adaptive Guidelines)
Every screen in Yomiru MUST showcase responsive Android UI across all 4 canonical device matrix classes:

1. **Compact Width (< 600dp) — Phones in Portrait:**
   - Single vertical column, 2-column grid cards, floating bottom action bar, full-width headers.
2. **Compact Height (< 480dp) — Phones in Landscape:**
   - Side-by-side 2-pane layouts, reduced hero cover heights (180dp), no tall vertical stacks that block scrolling.
3. **Medium Width (600dp – 840dp) — Foldables in Portrait, Small Tablets:**
   - 3-column media grids, side-by-side header panes, responsive card dimensions (`GridCells.Adaptive(minSize = 160.dp)`).
4. **Expanded Width (> 840dp) — Large Tablets, Unfolded Foldables, Desktop, TV:**
   - Multi-pane layouts (List-Detail / 2-Pane Split), 4+ column grid cells, centered max-width content containers (`widthIn(max = 1200.dp)`).

Always use `currentWindowAdaptiveInfo().windowSizeClass` and `getCarouselHomeSize()` to dynamically compute sizes and layouts for the active form factor.

## Kotlin Multiplatform (KMP) & Compose Multiplatform (CMP) Rules
- **Pure Common Logic:** Keep platform-agnostic business logic, network requests, and data models inside `commonMain`.
- **No Android Imports in commonMain:** Do not import `android.*` or platform-specific Android APIs into `commonMain`.
- **Expect / Actual:** Keep platform-specific bridges in their respective source sets (`androidMain`, `iosMain`) using `expect` / `actual` declarations.

## Yomiru Project-Specific Guidelines
- **UI & Aesthetics:** Minimalist dark slate palette (`#050508` pitch black background, `#0B0C13` surface, `#FFFFFF` / `#FAFAFA` text), high-contrast cut-corner shape geometry (`CutCornerShape`).
- **GraphQL API:** Use Ktor HttpClient to execute AniList GraphQL queries and mutations (`https://graphql.anilist.co`).
- **Paging:** Use Jetpack Paging3 (`LazyPagingItems`, `PagingSource`, `Pager`) for all scrollable media lists, search results, reviews, and recommendations.
- **OAuth Authentication:** AniList OAuth uses implicit token grant (`response_type=token`) redirecting to `yomiru://auth-callback`.

## Agent Skill & Workflow Protocol
- **Skill Discovery:** If a task requires specialized domain knowledge not already installed, use `find-skills` (`npx skills find <query>`) to discover relevant skills before attempting manual implementation.
- **Android Skills:** Leverage official Android skills (`android-cli`, `navigation-3`, `adaptive`, `r8-analyzer`, `android-profiler`, `testing-setup`) for Android-specific workflows.
- **Verification:** Always verify project buildability (`gradle_build`) after making structural changes. Do not modify application source code during environment configuration tasks.
