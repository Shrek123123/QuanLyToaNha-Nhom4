# Project Testing Script
Write-Host "=== Testing Project Structure ===" -ForegroundColor Cyan
Write-Host ""

# Test 1: Check FXML files
Write-Host "[1/6] Checking FXML files..." -ForegroundColor Yellow
$fxmlFiles = @("login.fxml", "main.fxml", "bms.fxml", "maintenance.fxml", "security.fxml", "cleaning.fxml", "admin.fxml", "customer.fxml")
$missing = @()
foreach ($fxml in $fxmlFiles) {
    $path = "src\main\resources\com\example\quanlytoanhanhom4\fxml\$fxml"
    if (-not (Test-Path $path)) {
        $missing += $fxml
    }
}
if ($missing.Count -eq 0) {
    Write-Host "   OK: All FXML files exist" -ForegroundColor Green
} else {
    Write-Host "   ERROR: Missing FXML files:" -ForegroundColor Red
    $missing | ForEach-Object { Write-Host "     - $_" -ForegroundColor Red }
}

# Test 2: Check config package
Write-Host "[2/6] Checking config package..." -ForegroundColor Yellow
$configFiles = @("DatabaseConnection.java", "DatabaseInitializer.java")
$configMissing = @()
foreach ($file in $configFiles) {
    $path = "src\main\java\com\example\quanlytoanhanhom4\config\$file"
    if (-not (Test-Path $path)) {
        $configMissing += $file
    }
}
if ($configMissing.Count -eq 0) {
    Write-Host "   OK: All config files exist" -ForegroundColor Green
} else {
    Write-Host "   ERROR: Missing config files:" -ForegroundColor Red
    $configMissing | ForEach-Object { Write-Host "     - $_" -ForegroundColor Red }
}

# Test 3: Check for old package references
Write-Host "[3/6] Checking for old package references..." -ForegroundColor Yellow
$oldRefs = Select-String -Path "src\**\*.java", "src\**\*.fxml", "pom.xml" -Pattern "quanlytoanhanhom15|QuanLyToaNha-Nhom15" -ErrorAction SilentlyContinue
if ($oldRefs.Count -eq 0) {
    Write-Host "   OK: No old package references found" -ForegroundColor Green
} else {
    Write-Host "   ERROR: Found $($oldRefs.Count) old package references:" -ForegroundColor Red
    $oldRefs | ForEach-Object { Write-Host "     - $($_.Path):$($_.LineNumber)" -ForegroundColor Red }
}

# Test 4: Check module-info.java
Write-Host "[4/6] Checking module-info.java..." -ForegroundColor Yellow
if (Test-Path "src\main\java\module-info.java") {
    $content = Get-Content "src\main\java\module-info.java" -Raw
    if ($content -match "exports com\.example\.quanlytoanhanhom4\.config") {
        Write-Host "   OK: Config package is exported" -ForegroundColor Green
    } else {
        Write-Host "   ERROR: Config package not exported" -ForegroundColor Red
    }
} else {
    Write-Host "   ERROR: module-info.java not found" -ForegroundColor Red
}

# Test 5: Check pom.xml
Write-Host "[5/6] Checking pom.xml..." -ForegroundColor Yellow
if (Test-Path "pom.xml") {
    $pomContent = Get-Content "pom.xml" -Raw
    if ($pomContent -match "QuanLyToaNha-Nhom4" -and $pomContent -notmatch "QuanLyToaNha-Nhom15|Nhom15") {
        Write-Host "   OK: pom.xml configured correctly" -ForegroundColor Green
    } else {
        Write-Host "   ERROR: pom.xml has issues" -ForegroundColor Red
    }
    if ($pomContent -match "mysql-connector-j") {
        Write-Host "   OK: MySQL dependency found" -ForegroundColor Green
    } else {
        Write-Host "   WARNING: MySQL dependency not found" -ForegroundColor Yellow
    }
} else {
    Write-Host "   ERROR: pom.xml not found" -ForegroundColor Red
}

# Test 6: Check IntelliJ config
Write-Host "[6/6] Checking IntelliJ configuration..." -ForegroundColor Yellow
$ideaFiles = @(".idea\modules.xml", "QuanLyToaNha-Nhom4.iml", ".idea\compiler.xml")
$ideaMissing = @()
foreach ($file in $ideaFiles) {
    if (-not (Test-Path $file)) {
        $ideaMissing += $file
    }
}
if ($ideaMissing.Count -eq 0) {
    Write-Host "   OK: IntelliJ config files exist" -ForegroundColor Green
} else {
    Write-Host "   WARNING: Missing IntelliJ config files:" -ForegroundColor Yellow
    $ideaMissing | ForEach-Object { Write-Host "     - $_" -ForegroundColor Yellow }
}

Write-Host ""
Write-Host "=== Test Complete ===" -ForegroundColor Cyan



