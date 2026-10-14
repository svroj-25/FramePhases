# Practice 7: Jetpack Compose Phases (Composition, Layout and Drawing)
**Fan:** Android Development / Software Modeling and Design (SMD)  
**Loyiha nomi:** `FramePhases`  
**Paket (Package):** `uz.ttpu.framephases`  
**Texnologiyalar:** Kotlin, Jetpack Compose, Material 3, Custom Layout, Logcat Phase Tracing  

---

## Part 1 — The frame (Questions 1–3)

### Question 1 — The three phases of a frame (THEORETICAL)

#### (a) 3 ta fazaning ketma-ketlik tartibi va har birining natijasi (output):
1. **1-faza — Composition (Kompozitsiya):**
   * *Natijasi:* Composable funksiyalarni ishga tushirib, ekranda qanday UI ko‘rsatilishi kerakligini tavsiflovchi UI daraxtini (`LayoutNode` daraxti) hosil qiladi.
2. **2-faza — Layout (Maketlash):**
   * *Natijasi:* UI daraxtidagi har bir elementni o‘lchab (measure), ularning aniq o‘lchamlari (kenglik va balandlik) hamda ekrandagi aniq (x, y) koordinatalarini hisoblab chiqadi.
3. **3-faza — Drawing (Chizish):**
   * *Natijasi:* Hisoblangan o‘lcham va koordinatalar asosida elementlarning piksellarini Canvas orqali qurilma ekraniga chizib beradi (render qiladi).

#### (b) Layout fazasining 2 ta asosiy bosqichi:
1. **Measurement (O‘lchash bosqichi):** Har bir bola elementni o‘lchash va ota elementning yakuniy o‘lchamini belgilash.
2. **Placement (Joylashtirish bosqichi):** Har bir bola elementni ota element koordinatalar sistemasida aniq (x, y) nuqtaga joylashtirish.

#### (c) Istisno holati: Nega `LazyColumn` bola elementlarining kompozitsiyasi ota elementning Layout fazasiga bog‘liq?
* **Tushuntirish:** Oddiy Composable'larda avval Composition, keyin Layout ishlaydi. Lekin `LazyColumn` (yoki `LazyRow`, `BoxWithConstraints`) faqat ekranda **ko‘rinib turgan (viewport)** elementlarnigina kompozitsiya qilishi kerak. Buning uchun `LazyColumn` oldin o‘zining balandligi va kengligini (ya’ni Layout fazasida beriladigan cheklovlarni — `Constraints`) hamda foydalanuvchi qancha skroll qilganini bilishi SHART. Faqat shundan keyingina u ekranga aynan qaysi elementlar sig‘ishini aniqlab, ularni kompozitsiya qiladi.
* **Haqiqiy ekrandan misol:** 10 000 ta xabardan iborat Telegram chat ekrani yoki Instagram lentasi. Agar `LazyColumn` oldindan hamma 10 000 ta elementni kompozitsiya qilsa, telefon xotirasi (RAM) to‘lib ketadi (OOM). Shu sababli u oldin o‘zining balandligini bilib oladi (Layout) va faqat ekranga sig‘adigan 6–8 ta xabarnigina kompozitsiya qiladi.

#### (d) Har bir kadrda nazariy jihatdan barcha fazalar ishlasa, nega Compose juda tez ishlaydi?
* **Mexanizm nomi:** **Aqlli qayta kompozitsiya (Smart Recomposition) va Fazalarni o‘tkazib yuborish (Phase Skipping).**
* Compose runtime har bir fazada qaysi holat (snapshot state) o‘qilganini xotirada yozib boradi. Agar kadrda faqat rang o‘zgargan bo‘lsa, Compose Composition va Layout fazalarini **butunlay o‘tkazib yuboradi (skip qiladi)** va faqat Drawing fazasinigina ishga tushiradi. Agar elementning kiruvchi parametrlari o‘zgarmagan bo‘lsa, u umumiy qayta chizilishdan chetda qoladi.

---

### Question 2 — Walk the layout algorithm (THEORETICAL)

**Daraxt:** `Row` ichida `Image` (48×48 px) va `Column` bor. `Column` ichida esa `Text A` (100×20 px) va `Text B` (80×20 px) mavjud.

#### (a) Barcha o‘lchash (measure) va joylashtirish (place) hodisalarining ketma-ketligi:
1. `Row` o‘lchashni boshlaydi (`Row: measure`).
2. `Image` o‘lchanadi: natija **48 × 48 px**.
3. `Column` o‘lchashni boshlaydi (`Column: measure`).
4. `Text A` o‘lchanadi: natija **100 × 20 px**.
5. `Text B` o‘lchanadi: natija **80 × 20 px**.
6. `Column` o‘zining yakuniy o‘lchamini belgilaydi: 
   * Kengligi: `max(100, 80) = 100 px`
   * Balandligi: `20 + 20 = 40 px`
7. `Row` o‘zining yakuniy o‘lchamini belgilaydi:
   * Kengligi: `48 + 100 = 148 px`
   * Balandligi: `max(48, 40) = 48 px`
8. `Row` joylashtirishni boshlaydi (`Row: place`):
   * `Image`ni **(0, 0)** ga joylashtiradi.
   * `Column`ni **(48, 0)** ga joylashtiradi.
9. `Column` joylashtirishni boshlaydi (`Column: place`):
   * `Text A`ni Column ichida **(0, 0)** ga joylashtiradi.
   * `Text B`ni Column ichida **(0, 20)** ga joylashtiradi.

#### (b) Yakuniy o‘lchamlar va `Row`ning yuqori-chap burchagiga nisbatan (x, y) koordinatalari:
* **Column yakuniy o‘lchami:** **100 × 40 px**
* **Row yakuniy o‘lchami:** **148 × 48 px**
* **Image koordinatasi:** **(x = 0, y = 0)**
* **Text A koordinatasi:** **(x = 48, y = 0)**
* **Text B koordinatasi:** **(x = 48, y = 20)**

#### (c) Har bir tugun necha marta ko‘zdan kechiriladi (visit qilinadi)?
* Har bir tugun o‘lchash jarayonida **aniq 1 marta** ko‘zdan kechiriladi (**Single Pass Measurement rule**).
* **Nima uchun ma’ruzada bu ta’kidlangan?** Eski Android View tizimida (masalan, `layout_weight` qo‘llanilgan ichma-ich `LinearLayout`larda) elementlar bir necha marta o‘lchanar edi. Agar har bir qavatda element 2 martadan o‘lchansa, daraxt chuqurlashgani sari vaqt eksponentsial ravishda **O(2^N)** ga o‘sib ketadi va interfeys jiddiy qotishni (jank/stutter) boshlaydi. Compose'da esa o‘lchash chiziqli **O(N)** vaqtda bajariladi.

#### (d) Ushbu daraxtni qaysi faza yaratgan va uning har bir tugunida nima saqlanadi?
* Ushbu daraxtni **Composition (Kompozitsiya)** fazasi yaratgan.
* Har bir tugun (`LayoutNode`) o‘z ichida elementning modifikatorlar zanjiri (modifiers), o‘lchash va joylashtirish qoidalari (MeasurePolicy), bola va ota tugunlariga havolalar hamda semantik ma’lumotlarni saqlaydi.

---

### Question 3 — State reads and restart scopes (THEORETICAL)

#### (a) 1- va 2-kod parchalari (snippets) teng kuchlimi va holat qachon o‘qiladi?
* **Ha, ular to‘liq ekvivalent (teng kuchli).** Kotlin’dagi `by` delegati orqa fonda `paddingState.value` ni chaqiradi.
* **O‘qilish momenti:** Holat u yaratilganda (remember qilinganda) EMAS, balki aynan **ishlatilgan (o‘qilgan) momentda**, ya’ni `Modifier.padding(...)` funksiyasi chaqirilib modifikator zanjiri tuzilayotgan paytda o‘qiladi.

#### (b) 3-kod parchasida Text("A") va Text("B") holatlari qaysi fazalarda o‘qiladi va `offsetX` o‘zgarganda nima sodir bo‘ladi?
* **`Text("A")`:** Holat `Modifier.offset { IntOffset(offsetX.roundToPx(), 0) }` lambda ichida o‘qilmoqda. Bu lambda **Layout fazasining placement (joylashtirish) bosqichida** ishga tushadi. Shuning uchun `offsetX` o‘zgarganda **Composition fazasi butunlay skip qilinadi (o‘tkazib yuboriladi)**, faqat Layout placement va Drawing fazalari qayta ishlaydi.
* **`Text("B")`:** Holat `val offsetPx = ...` satrida, to‘g‘ridan-to‘g‘ri Composable funksiya tanasida o‘qilmoqda. Bu **Composition fazasidagi o‘qish** hisoblanadi. Shuning uchun `offsetX` o‘zgarganda **butun Composable funksiyasi qayta kompozitsiya bo‘ladi (Recomposition)**, so‘ng Layout va Drawing fazalari ham noldan qayta ishlaydi.

#### (c) Restart scope nima va Composable funksiya tanasidagi o‘qish uchun restart scope qayer hisoblanadi?
* **Restart scope:** Bu Compose kompilyatori tomonidan kuzatiladigan eng kichik mustaqil kod bloki bo‘lib, ushbu blok ichida o‘qilgan holat (state) o‘zgarganda, Compose butun UI-ni emas, faqat shu blokning o‘zinigina mustaqil ravishda qayta ishga tushira oladi.
* Composable funksiya tanasida to‘g‘ridan-to‘g‘ri o‘qilgan holat uchun restart scope — **ushbu `@Composable` funksiyaning o‘z tanasidir**.

---

## Part 2 — Seeing the phases in Android Studio (Questions 4–8)

### Question 4 — Make Compose talk: the first frame (HANDS-ON)

* **Loyiha:** `FramePhases` (`uz.ttpu.framephases`, Min SDK 24).
* `PhaseLog.kt` yordamida `Modifier.logPhases(name)` yaratildi (measure, place va draw loglari).
* **Birinchi kadr (First frame) Logcat chiqishi (`tag:Phases`):**
  ```text
  Phases: LectureTree: composition
  Phases: Row: layout/measure
  Phases: Image: layout/measure
  Phases: Column: layout/measure
  Phases: Text A: layout/measure
  Phases: Text B: layout/measure
  Phases: Row: layout/place
  Phases: Image: layout/place
  Phases: Column: layout/place
  Phases: Text A: layout/place
  Phases: Text B: layout/place
  Phases: Row: draw
  Phases: Image: draw
  Phases: Column: draw
  Phases: Text A: draw
  Phases: Text B: draw
  ```
* **Taqqoslash:** Logcat chiqishi 2-savoldagi nazariy tahlil bilan 100% bir xil: avval barcha elementlar o‘lchanadi (measure), keyin joylashtiriladi (place), eng oxirida chiziladi (draw).
* **Ekran aylantirilganda (Rotation):** Android tizimi Activity'ni butunlay qayta tuzadi, shu sababli barcha 3 ta faza (Composition, Layout va Drawing) noldan boshlab to‘liq qayta ishlaydi.

---

### Question 5 — Reading state in the composition phase (HANDS-ON)

* **Kod:** `PaddingDemo.kt`
* `padding` holati `Modifier.padding(padding)` modifikatorini tuzish vaqtida (Composition fazasida) o‘qiladi.
* **Kutilgan natija va Logcat:**
  "Toggle padding" tugmasi bosilganda:
  ```text
  Phases: PaddingDemo: composition
  Phases: padding demo: layout/measure
  Phases: padding demo: layout/place
  Phases: padding demo: draw
  ```
* **Sababi:** Padding qiymati Composable tanasida o‘qilgani uchun butun funksiya qayta kompozitsiya bo‘ladi (`composition`), padding o‘zgargani uchun elementning o‘lchami o‘zgaradi (`layout/measure`), yangi joyga suriladi (`layout/place`) va yangi chegarada qayta chiziladi (`draw`). Barcha 3 faza to‘liq qayta ishlaydi.

---

### Question 6 — Reading state in the layout phase (HANDS-ON)

* **Kod:** `OffsetDemo.kt`
* `offsetX` holati `Modifier.offset { IntOffset(offsetX.roundToPx(), 0) }` lambda ichida o‘qiladi.
* **"Move" bosilgandagi Logcat:**
  ```text
  Phases: offset demo: layout/place
  Phases: offset demo: draw
  ```
  *(Diqqat: `OffsetDemo: composition` hamda `offset demo: layout/measure` loglari chiqmadi!)*
* **Eksperiment (o‘qishni tashqariga chiqarish):**
  `val offsetPx = with(LocalDensity.current) { offsetX.roundToPx() }` qilib Text ustiga chiqarilganda:
  Endi `OffsetDemo: composition` va `layout/measure` loglari ham chiqdi.
* **Xulosa:** Holat lambda ichida o‘qilganda Compose o‘lchash va kompozitsiya fazalarini butunlay o‘tkazib yuboradi, chunki elementning o‘lchami o‘zgarmaydi, faqat koordinatasi siljiydi.

---

### Question 7 — Reading state in the drawing phase (HANDS-ON)

* **Kod:** `ColorDemo.kt`
* `color` holati `Canvas { drawRect(color) }` chizish bloki ichida o‘qiladi.
* **"Change color" bosilgandagi Logcat:**
  ```text
  Phases: color demo: draw
  ```
  *(Composition ham, Layout ham butunlay ISHLAMADI — 0 ta recomposition!)*
* **Eksperiment (Canvas o‘rniga `Box(Modifier.background(color))` qo‘yish):**
  `background(color)` parametri funksiya chaqirilayotgan paytda (Composition fazasida) qiymat oladi. Shu sababli tugma bosilganda `ColorDemo: composition` qayta ishladi.
* **Xulosa:** Holatning qayerda e’lon qilingani (`remember`) fazalarga ta’sir qilmaydi; fazalarga faqat **holat aynan qayerda o‘qilganligi** ta’sir qiladi.

---

### Question 8 — Which phase re-runs? (THEORETICAL)

#### 6 ta holat o‘qilishi bo‘yicha to‘liq tahlil jadvali:

| O‘qish ifodasi | Holat qaysi fazada o‘qiladi? | Composition | Layout | Drawing |
| :--- | :--- | :---: | :---: | :---: |
| **(a) `Modifier.padding(p)`** | Composition | **runs** | **runs** | **runs** |
| **(b) `Modifier.offset { IntOffset(p, 0) }`** | Layout (placement) | **skipped** | **only if position changed (only place)** | **runs** |
| **(c) `Modifier.offset(p)`** | Composition | **runs** | **runs** | **runs** |
| **(d) `Canvas(Modifier.size(50.dp)) { drawRect(c) }`** | Drawing | **skipped** | **skipped** | **runs** |
| **(e) `Layout(content) { m, cs -> /* uses s */ }`** | Layout (measurement) | **skipped** | **runs** | **runs** |
| **(f) `Modifier.drawWithContent { /* uses c */ }`** | Drawing | **skipped** | **skipped** | **runs** |

#### Har bir kadrda o‘zgaruvchi animatsiya qiymati uchun qaysi birini tanlash kerak?
* **Tanlov:** **(b) `Modifier.offset { ... }`** (siljish animatsiyalari uchun) yoki **(d)/(f) Drawing** (rang va ko‘rinish animatsiyalari uchun).
* **Asoslash:** Har bir kadrda (sekundiga 60 yoki 120 marta) o‘zgaradigan animatsiyalarda `Modifier.padding(p)` yoki `Modifier.offset(p)` ishlatilsa, soniyasiga 60-120 marta Recomposition va Measurement ishlaydi va protsessor qizib, interfeys qotadi (jank). Lambda ko‘rinishidagi **(b)** esa Composition va Measurement fazalarini 100% o‘tkazib yuboradi va to‘g‘ridan-to‘g‘ri GPU drayveri bilan tekis 60/120 FPS tezlikni ta’minlaydi.

---

## Part 3 — Optimizing state reads (Questions 9–11)

### Question 9 — Parallax: composition read versus layout read (HANDS-ON)

* **`ParallaxSlow`:** `listState.firstVisibleItemScrollOffset` qiymatini to‘g‘ridan-to‘g‘ri modifikator tuzishda o‘qiydi. Skroll davomida soniyasiga o‘nlab marta Recomposition bo‘ladi (`slow: composition #142...`).
* **`ParallaxFast`:** Qiymat `.offset { IntOffset(0, listState.firstVisibleItemScrollOffset / 2) }` lambda ichida o‘qiladi. Skroll qilinganda kompozitsiya soni **1 da qoladi** (`fast: composition #1`).
* **Xulosa jumlasi:**
  > *Sekin versiya (`ParallaxSlow`) har bir skroll hodisasida barcha 3 ta fazani (Composition, Layout va Drawing) to‘liq qayta ishga tushiradi; tez versiya (`ParallaxFast`) esa Composition va Measurement fazalarini butunlay o‘tkazib yuborib, faqat Layout placement va Drawing fazalarinigina ishga tushiradi.*

---

### Question 10 — derivedStateOf: fewer recompositions for a Boolean (HANDS-ON)

* **`ScrollToTopNaive`:** `val showButton = listState.firstVisibleItemIndex > 0` har bir skroll qadamida qayta hisoblanadi va har safar yangi kompozitsiya chaqiradi (30 ta element aylantirilganda 30+ marta kompozitsiya bo‘ladi).
* **`derivedStateOf` versiyasi:** `val showButton by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }`
* **Nega farq bunchalik katta (tugma ko‘rinishi bor-yo‘g‘i 2 marta o‘zgargan bo‘lsa ham)?**
  * *Javob:* `listState.firstVisibleItemIndex` skroll davomida doimiy o‘zgaruvchi butun son (0, 1, 2, 3...) bo‘ladi. Naive versiyada har bir o‘zgarish holat o‘qilishi sifatida qayd etilib, keraksiz kompozitsiyalarni keltirib chiqaradi. `derivedStateOf` esa ushbu sonli oqimni filtrlash vazifasini bajaradi va faqat yakuniy Boolean natija (`false`dan `true`ga yoki aksincha) o‘zgargandagina kompozitsiyani uyg‘otadi.
* **Tugma mantiqi:** `rememberCoroutineScope` va `coroutineScope.launch { listState.animateScrollToItem(0) }` orqali silliq boshiga qaytish to‘liq ishga tushirildi.

---

### Question 11 — Skipping and deferred reads (HANDS-ON)

* **1-holat (Qiymat uzatish — `Child(count)`):**
  * Tugma 3 marta bosilganda:
    * `Parent: composition` (3 marta)
    * `Child: composition` (3 marta)
    * `Sibling: composition` (0 marta — chunki kiruvchi parametri yo‘q va u skip qilindi).
* **2-holat (Lambda uzatish — `Child(count = { count })`):**
  * Tugma 3 marta bosilganda:
    * `Parent: composition` (0 marta — qayta ishlamadi!)
    * `Child: composition` (3 marta)
* **Nega Parent endi qayta kompozitsiya bo‘lmasligining 2 jumlali tushuntirishi:**
  > *`count` holati Parent ichida emas, balki lambda orqali Child tanasida chaqirilib o‘qilgani sababli, uning restart scope'i Child funksiyasiga birikadi. Natijada `count` qiymati o‘zgarganda faqat Child'ning restart scope'i qayta ishga tushadi, Parent'ning restart scope'i esa holat o‘zgarishidan xabarsiz qolib, butunlay skip qilinadi.*

---

## Part 4 — Cyclic phase dependency (Questions 12–14)

### Question 12 — Two frames for one layout (THEORETICAL)

**Rasmdagi balandlik:** 360 px.

#### 1-kadr va 2-kadr tahlili:
* **Frame 1 (1-kadr):**
  * Kompozitsiya `imageHeightPx = 0` qiymatini o‘qiydi. Matn (`Text`) yuqoridan `0.dp` padding oladi.
  * Layout fazasida rasm 360 px deb o‘lchanadi va `onSizeChanged` orqali `imageHeightPx = 360` qilib yoziladi.
  * Bu o‘zgarish darhol navbatdagi kadr uchun **Recomposition rejalashtiradi**.
  * Drawing fazasida matn rasmning ustiga chiqib qolgan (xato intermediate) holatda chiziladi.
* **Frame 2 (2-kadr):**
  * Kompozitsiya yangi `imageHeightPx = 360` qiymatini o‘qiydi. Matn endi `360.dp` padding oladi.
  * Layout matnni rasmning ostiga to‘g‘ri joylashtiradi va chizadi.

#### Savollarga javoblar:
* **(a) Nega `toDp()` ga o‘girishning o‘ziyoq xavfli signal (warning sign) hisoblanadi?**
  * Layout fazasi piksellar (px) bilan ishlaydi, Kompozitsiya esa Dp bilan. Piksellarni yana Dp ga o‘girish — bu Layout fazasi chiqargan natijani orqaga, Kompozitsiya fazasiga tiqishtirishga urinishning yaqqol belgisidir.
* **(b) Ehtiyot bo‘lish kerak bo‘lgan umumiy 3 bosqichli xato naqsh (anti-pattern):**
  1. Layout fazasida o‘lchamni o‘qish (`onSizeChanged` yoki `layout`).
  2. Ushbu o‘lchamni holatga (`mutableStateOf`) yozish.
  3. Ushbu holatni yana boshqa elementning Kompozitsiyasida yoki modifikatorida o‘qish.
* **(c) Kod qaysi asosiy printsipni buzadi?**
  * **Unidirectional Data Flow (Bir tomonlama ma’lumot oqimi)** printsipini buzadi. Fazalar faqat bir tomonga (Composition -> Layout -> Drawing) harakatlanishi kerak, Layout natijasi orqaga qaytarilmasligi lozim.

---

### Question 13 — Reproduce the loop and fix it with a layout primitive (HANDS-ON)

* **`CyclicDemo`:** Ishga tushganda `imageHeightPx=0` va `imageHeightPx=315` bilan **2 marta kompozitsiya** sodir bo‘ldi (keraksiz oraliq kadr).
* **`CyclicFixed`:** Oddiy `Column` yordamida qurildi. Natijada **faqat 1 marta kompozitsiya** bo‘ldi (`fixed: composition #1`).
* **Yagona haqiqat manbai (Single source of truth):**
  Tuzatilgan versiyada `Column` konteynerining o‘zi yagona haqiqat manbai vazifasini bajaradi. U rasmni o‘lchaydi, matnni o‘lchaydi va ularni hech qanday holat (state) saqlamasdan tabiiy ravishda bitta kadrda ustma-ust joylashtiradi.

---

### Question 14 — A custom layout when no primitive fits (HANDS-ON)

* **`StackBelow` Custom Layout kodi yozildi:**
  * 1-qadam (Measure): `measurables.map { Log.d("StackBelow", "measure child"); it.measure(loose) }`
  * 2-qadam (Size): `width = placeables.maxOfOrNull { it.width } ?: 0`, `height = placeables.sumOf { it.height } + totalGaps`
  * 3-qadam (Place):
    ```kotlin
    layout(constraints.constrainWidth(width), constraints.constrainHeight(height)) {
        Log.d("StackBelow", "place children")
        var y = 0
        placeables.forEach { placeable ->
            placeable.placeRelative(0, y)
            y += placeable.height + spacingPx
        }
    }
    ```
* **Logcat tahlili (`tag:StackBelow`):**
  Barcha `measure child` loglari (2 ta bola uchun 2 marta) birinchi bo‘lib chiqadi, va faqat ularning barchasi o‘lchanib bo‘lgachgina bitta `place children` logi chiqadi.
* **Bu nimani isbotlaydi?**
  Bu Compose'ning **"One Pass" (bitta o‘tish)** qoidasini isbotlaydi: ota layout barcha bolalarini avval to‘liq o‘lchab oladi va joylashtirishni faqat bir marta yakuniy bosqichda amalga oshiradi.
* **3-bola element qo‘shilganda:** `measure child` qatori 3 martaga ko‘payadi va yana bitta umumiy `place children` bloki ichida 3 ta bola ketma-ket joylashadi.
