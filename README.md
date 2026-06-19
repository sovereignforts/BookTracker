# 📚 BookTracker

> **Digital Wellbeing for your books** — tracks exactly which PDF/DOCX files you read and how long, by name.

---

## ✨ Features

- 📄 **Built-in PDF viewer** — renders all pages natively
- 📝 **DOCX support** — tracks reading time for Word documents
- ⏱️ **Per-file timing** — knows exactly which book & how many minutes
- 📊 **14-day bar chart** — see your reading habit visually
- 🌙 **Dark theme** — easy on the eyes

---

## 🚀 Build APK from Phone (GitHub-only, no PC needed)

### Step 1 — Create your GitHub repo

1. Open **github.com** on your phone browser
2. Tap **+** → **New repository**
3. Name it `BookTracker`, set to **Public**, tap **Create**

### Step 2 — Upload the files

You need to create this exact folder structure. In your repo, tap **Add file → Create new file** and paste each file.

> **Tip:** Start with `settings.gradle`, then `build.gradle`, then go folder by folder.

### Step 3 — The folder structure to recreate

```
BookTracker/
├── .github/
│   └── workflows/
│       └── build.yml                ← GitHub Actions (builds your APK!)
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── app/
│   ├── build.gradle
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/booktracker/
│       │   ├── data/
│       │   │   ├── db/              (AppDatabase.java, ReadingSessionDao.java)
│       │   │   ├── model/           (ReadingSession.java, BookSummary.java, DailyStats.java)
│       │   │   └── repository/      (ReadingRepository.java)
│       │   ├── service/             (ReadingSessionService.java, BootReceiver.java)
│       │   ├── ui/
│       │   │   ├── home/            (MainActivity.java, HomeViewModel.java, BookSummaryAdapter.java)
│       │   │   ├── reader/          (ReaderActivity.java, PdfPageAdapter.java)
│       │   │   └── stats/           (StatsActivity.java, StatsViewModel.java)
│       │   └── util/                (DateUtil.java, FileUtil.java)
│       └── res/
│           ├── drawable/            (ic_book.xml, badge_bg.xml)
│           ├── layout/              (activity_main.xml, activity_reader.xml,
│           │                         activity_stats.xml, item_book_summary.xml, item_pdf_page.xml)
│           ├── menu/                (main_menu.xml)
│           ├── mipmap-*/            (ic_launcher.xml, ic_launcher_round.xml — same file in each)
│           ├── values/              (colors.xml, strings.xml, themes.xml)
│           └── xml/                 (file_paths.xml)
├── build.gradle
└── settings.gradle
```

### Step 4 — Watch GitHub build your APK

1. After uploading all files, go to **Actions** tab in your repo
2. You'll see **"Build APK"** workflow running (takes ~3-5 min)
3. When it shows ✅ green, click on it
4. Scroll down to **Artifacts** → tap **BookTracker-debug** to download
5. Open the downloaded `.apk` on your phone and install it!

> ⚠️ **Allow unknown sources:** Go to Settings → Apps → Special access → Install unknown apps → allow your browser

---

## 📱 How to use the app

1. Tap **+** button → pick a PDF or DOCX from your storage
2. The file opens inside BookTracker with a timer running
3. Go back when done reading
4. The home screen shows today's books + time spent
5. Tap the chart icon (top right) for 14-day reading history

---

## 🔧 How it works

| Component | What it does |
|---|---|
| `ReaderActivity` | Opens PDF/DOCX, starts a foreground service timer |
| `ReadingSessionService` | Foreground service — records start/end time |
| `AppDatabase` (Room) | SQLite database storing all sessions |
| `HomeViewModel` | LiveData for today's per-book totals |
| `StatsActivity` | Bar chart + all-time book list |

---

## 📋 Permissions requested

| Permission | Why |
|---|---|
| `READ_MEDIA_DOCUMENTS` | To open PDFs/DOCX from your storage |
| `FOREGROUND_SERVICE` | Keep timer running while you read |
| `POST_NOTIFICATIONS` | Show "Reading: filename.pdf" notification |
| `RECEIVE_BOOT_COMPLETED` | Close any open session after reboot |
