$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$appName = "Golden Opportunity"
$mainClass = "com.GoldenOpportunity.MainUIFrame"
$jarName = "Golden_Opertunity-1.0-SNAPSHOT-jar-with-dependencies.jar"
$dest = Join-Path $root "dist/windows"

Set-Location $root

$jarPath = Join-Path $root "target/$jarName"
$maven = Get-Command mvn -ErrorAction SilentlyContinue

if ($maven) {
  & $maven.Source -DskipTests package
} elseif (-not (Test-Path $jarPath)) {
  throw "Maven is not installed and $jarPath was not found. Install Maven or build the JAR first."
}

New-Item -ItemType Directory -Force -Path $dest | Out-Null

jpackage `
  --type exe `
  --name $appName `
  --input target `
  --main-jar $jarName `
  --main-class $mainClass `
  --dest $dest `
  --java-options "-Dfile.encoding=UTF-8" `
  --win-dir-chooser `
  --win-menu `
  --win-shortcut
