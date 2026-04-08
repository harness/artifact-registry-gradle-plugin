# Harness Gradle Plugin

A Gradle plugin that intercepts the `publish` task and replaces default deployment behavior with custom Harness deployment to Artifactory.



## Usage

### 1. Add the Plugin

#### For Groovy DSL (`build.gradle`):

```groovy
plugins {
    id 'io.harness.gradle' version '1.0.1'
}
```

#### For Kotlin DSL (`build.gradle.kts`):

```kotlin
plugins {
    id("io.harness.gradle") version "1.0.1"
}
```

### 2. Apply Plugin to All Subprojects

If you want to apply the plugin to all subprojects, add this to your root `build.gradle`:

#### For Groovy DSL:

```groovy
allprojects {
    apply plugin: 'io.harness.gradle'
}
```

#### For Kotlin DSL (`build.gradle.kts`):

```kotlin
allprojects {
    apply(plugin = "io.harness.gradle")
}
```

### 3. Configure Plugin Settings

Add the `harnesspublish` configuration block to your `build.gradle`:

#### For Groovy DSL:

```groovy
harnesspublish {
    apiUrl = System.getenv("DEPLOY_REPO_URL")?: ""
    username = System.getenv("DEPLOY_USERNAME")?: ""
    token = System.getenv("DEPLOY_TOKEN")?: ""
}
```

#### For Kotlin DSL (`build.gradle.kts`):

```kotlin
harnesspublish {
    apiUrl = System.getenv("DEPLOY_REPO_URL")?: ""
    username = System.getenv("DEPLOY_USERNAME")?: ""
    token = System.getenv("DEPLOY_TOKEN")?: ""
}
```

### 4. Set Environment Variables

Before running the publish command, set the required environment variables
You will get the values from the Harness UI 'Set Up client' section :



#### On Linux/macOS:

```bash
export DEPLOY_REPO_URL="https://pkg.harness.io/pkg/<ACCOUNT_ID>/<REGISTRY_NAME>/maven"
export DEPLOY_USERNAME="your-username"
export DEPLOY_TOKEN="your-token"
export DEPLOYMENT_TIME=<TIME_IN_MINUTES>  # Optional: timeout in minutes (default: 120 minutes)
export DEPLOY_THREAD_COUNT=<NUMBER_OF_THREADS>  # Optional: number of threads 
```

#### On Windows (Command Prompt):

```cmd
set DEPLOY_REPO_URL=https://pkg.harness.io/pkg/<ACCOUNT_ID>/<REGISTRY_NAME>/maven
set DEPLOY_USERNAME=your-username
set DEPLOY_TOKEN=your-token
set DEPLOYMENT_TIME=<TIME_IN_MINUTES>
set DEPLOY_THREAD_COUNT=<NUMBER_OF_THREADS>
```

#### On Windows (PowerShell):

```powershell
$env:DEPLOY_REPO_URL="https://pkg.harness.io/pkg/<ACCOUNT_ID>/<REGISTRY_NAME>/maven"
$env:DEPLOY_USERNAME="your-username"
$env:DEPLOY_TOKEN="your-token"
$env:DEPLOYMENT_TIME=<TIME_IN_MINUTES>
$env:DEPLOY_THREAD_COUNT=<NUMBER_OF_THREADS>
```

## Configuration description

| Environment Variable    | Description | Default |
|------------------------|----------------------------------------------|----------|
| `DEPLOY_REPO_URL`      | Repository URL for deployment                | Required | 
| `DEPLOY_USERNAME`      | Username for deployment                      | Required | 
| `DEPLOY_TOKEN`         | Token/password for deployment                | Required |
| `DEPLOY_THREAD_COUNT`  | Number of concurrent threads for deployment  | Optional |
| `DEPLOYMENT_TIME`      | Maximum deployment execution time in minutes | Optional | 
```

### 5. Run Publish

Execute the publish task:

```bash
./gradlew publish
```




