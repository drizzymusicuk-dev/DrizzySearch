import os, urllib.request, zipfile, subprocess, platform

TOOLS_DIR = os.path.join(os.getcwd(), "tools")
GRADLE_VERSION = "8.5"
GRADLE_ZIP = f"gradle-{GRADLE_VERSION}-bin.zip"
GRADLE_URL = f"https://services.gradle.org/distributions/{GRADLE_ZIP}"
GRADLE_PATH = os.path.join(TOOLS_DIR, f"gradle-{GRADLE_VERSION}")
GRADLE_BIN = os.path.join(GRADLE_PATH, "bin", "gradle.bat" if platform.system()=="Windows" else "gradle")

os.makedirs(TOOLS_DIR, exist_ok=True)

if not os.path.exists(GRADLE_BIN):
    print(f"Downloading Gradle {GRADLE_VERSION}...")
    zip_path = os.path.join(TOOLS_DIR, GRADLE_ZIP)
    urllib.request.urlretrieve(GRADLE_URL, zip_path)
    print("Extracting Gradle...")
    with zipfile.ZipFile(zip_path, 'r') as zip_ref:
        zip_ref.extractall(TOOLS_DIR)
    os.remove(zip_path)

print("Building DrizzyApp...")
subprocess.run([GRADLE_BIN, "assembleDebug"], cwd=os.getcwd())

apk_src = os.path.join("app", "build", "outputs", "apk", "debug", "app-debug.apk")
apk_dst = "DrizzyApp-debug.apk"

if os.path.exists(apk_src):
    os.replace(apk_src, apk_dst)
    print(f"✅ Build complete: {apk_dst}")
else:
    print("❌ Build failed. Check Gradle logs.")
