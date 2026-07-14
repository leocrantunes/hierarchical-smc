# hierarchical-smc

Monorepo for experiments and tooling around software modularization and clustering, with:

- Java clustering engines (HMD, CRG, MQ)
- A Spring Boot API for storing and serving generated graph data
- A Vue + Vite frontend for graph visualization and metric exploration

## Repository layout

```text
.
├── hmd-clustering/   # Main clustering engine (Maven, Java 17)
├── mq-clustering/    # MQ clustering variant (Java sources + datasets)
├── hmd-gen-api/      # Graph API (Spring Boot, Gradle)
├── hmd-gen-app/      # Web app (Vue 3 + Vite + Tailwind)
└── README.md
```

## Tech stack

- Java 17
- Maven (for `hmd-clustering`)
- Gradle (wrapper included in `hmd-gen-api`)
- Node.js 18+ and npm (for `hmd-gen-app`)
- Optional: Google Cloud SDK (`gcloud`) for App Engine deploy flows

## Quick start

### 1) Clone

```bash
git clone <your-fork-or-repo-url>
cd hierarchical-smc
```

### 2) Run the API (local)

```bash
cd hmd-gen-api
./gradlew bootRun
```

On Windows PowerShell:

```powershell
cd hmd-gen-api
.\gradlew.bat bootRun
```

The API starts on the default Spring Boot port (`8080`) unless overridden.

### 3) Run the frontend (local)

```bash
cd hmd-gen-app
npm install
npm run dev
```

Important: the frontend currently requests data from the hosted endpoint
`https://hmd-gen-api.rj.r.appspot.com` (hardcoded in `hmd-gen-app/src/App.vue`).
If you want to use your local API, update those URLs to `http://localhost:8080`.

## Module details

### `hmd-clustering`

Main clustering tool for original/HMD/CRG processing.

- Build:

```bash
cd hmd-clustering
mvn clean package
```

- Run jar:

```bash
java -jar target/ils-clustering-hmd-1.0-SNAPSHOT.jar --help
```

- Example runs:

```bash
# HMD using data/clustering/odem-2nd-phase
java -jar target/ils-clustering-hmd-1.0-SNAPSHOT.jar --type hmd --data odem-2nd-phase

# CRG with API upload enabled
java -jar target/ils-clustering-hmd-1.0-SNAPSHOT.jar --type crg --data odem-2nd-phase --api https://hmd-gen-api.rj.r.appspot.com

# Incremental processing (skip already generated JSON files)
java -jar target/ils-clustering-hmd-1.0-SNAPSHOT.jar --type hmd --data odem-2nd-phase --skip-existing
```

CLI options:

- `--type <original|hmd|crg>`
- `--data <folder>` (relative to `data/clustering`)
- `--api <base-url>` (optional)
- `--skip-existing`
- `--help`

Outputs:

- `results_<type>.csv`
- `results_<type>/*.json`

### `mq-clustering`

MQ-oriented clustering implementation and datasets.

Notes:

- Source code is under `mq-clustering/src`.
- The current `MainProgram` includes hardcoded absolute paths and API endpoint values.
- There is no Maven/Gradle build file in this module at the moment.

If you want to run it locally, adjust hardcoded paths in
`mq-clustering/src/unirio/teaching/clustering/MainProgram.java` first, then compile/run with `javac`/`java`.

### `hmd-gen-api`

Spring Boot API for graph storage/retrieval and metric calculations.

- Build:

```bash
cd hmd-gen-api
./gradlew build
```

- Run:

```bash
./gradlew bootRun
```

- Test:

```bash
./gradlew test
```

Selected endpoints (see `GraphController` for full list):

- `GET /graphs`
- `GET /graphs/{id}`
- `GET /graphs/{id}/elements`
- `POST /original/graphs`
- `POST /mq/graphs`
- `POST /hmd/graphs`
- `POST /crg25/graphs`, `POST /crg50/graphs`, `POST /crg75/graphs`

Swagger UI is available through springdoc when running the app:

- `http://localhost:8080/swagger-ui/index.html`

Deployment-related tasks (already configured in Gradle):

- `appengineDeploy`
- Jib image build/push configuration

### `hmd-gen-app`

Vue 3 frontend for selecting generated graphs and rendering interactive visualizations.

- Install and run:

```bash
cd hmd-gen-app
npm install
npm run dev
```

- Production build:

```bash
npm run build
npm run serve
```

Google App Engine static deploy artifacts exist in this module (`app.yaml`, `cloudbuild.yaml`).

## Data and results

The repository includes precomputed result artifacts and raw input data, including:

- clustering input data in module `data/` folders
- generated JSON outputs in folders like `results_crg/`
- raw log files used by API-side processing in `hmd-gen-api/src/main/resources`

Because these datasets can be large, prefer adding new generated outputs to dedicated result folders
and avoid committing transient local experiment files.

## Common workflows

### Generate clustering outputs and upload to API

1. Build `hmd-clustering`.
2. Run with `--type` and `--data`.
3. Add `--api <url>` to publish generated graphs.
4. Open `hmd-gen-app` to inspect results.

### Run full stack locally

1. Start `hmd-gen-api` (`bootRun`).
2. Point frontend API URLs in `hmd-gen-app/src/App.vue` to `http://localhost:8080`.
3. Start `hmd-gen-app` (`npm run dev`).

## Troubleshooting

- `mvn`/`gradle`/`npm` not found: install required toolchains and ensure they are on `PATH`.
- Frontend loads but shows no data: verify API URL (hosted vs local) and CORS/network reachability.
- API integration issues with Firestore: check Spring GCP configuration in
	`hmd-gen-api/src/main/resources/application.properties` and your environment credentials/emulator settings.

## License

See `LICENSE`.