package main

import (
	"archive/zip"
	"fmt"
	"io"
	"net/http"
	"os"
	"os/exec"
	"path/filepath"

)

func main() {
	fmt.Println("===================================")
	fmt.Println("      Drizzy Search Setup Tool     ")
	fmt.Println("===================================")
	fmt.Println()

	baseDir := filepath.Join(os.Getenv("USERPROFILE"), "DrizzyTools")
	os.MkdirAll(baseDir, 0755)
	fmt.Println("Using base folder:", baseDir)

	check("Java (JDK)", "java", "--version")
	check("Gradle", "gradle", "--version")
	check("ADB", "adb", "version")

	fmt.Println()
	fmt.Println("Checking Android SDK...")
	sdk := filepath.Join(baseDir, "Android")
	sdkCmd := filepath.Join(sdk, "cmdline-tools", "latest", "bin", "sdkmanager.bat")

	if _, err := os.Stat(sdkCmd); os.IsNotExist(err) {
		fmt.Println("→ Command-line tools not found. Downloading…")
		err := downloadAndUnzip(
			"https://dl.google.com/android/repository/commandlinetools-win-10406996_latest.zip",
			filepath.Join(baseDir, "cmdline-tools.zip"),
			filepath.Join(sdk, "cmdline-tools", "latest"),
		)
		if err != nil {
			fmt.Println("Download failed:", err)
			return
		}
		fmt.Println("✓ Command-line tools ready at", sdk)
	} else {
		fmt.Println("✓ Android command-line tools found.")
	}

	fmt.Println()
	fmt.Println("To install platform packages, run:")
	fmt.Printf("  \"%s\" --sdk_root=\"%s\" --licenses\n", sdkCmd, sdk)
	fmt.Printf("  \"%s\" --sdk_root=\"%s\" \"platform-tools\" \"platforms;android-34\" \"build-tools;34.0.0\"\n", sdkCmd, sdk)
	fmt.Println()
	fmt.Println("After installing, set this in your environment variables or local.properties:")
	fmt.Println("  sdk.dir=", sdk)
	fmt.Println()
	fmt.Println("Setup complete. You can now run build_drizzy.bat.")
}

func check(name, cmd string, arg string) {
	_, err := exec.LookPath(cmd)
	if err != nil {
		fmt.Printf("→ %s not found. Please install it manually.\n", name)
	} else {
		out, _ := exec.Command(cmd, arg).CombinedOutput()
		fmt.Printf("✓ %s found.\n", name)
		fmt.Println(string(out))
	}
}

func downloadAndUnzip(url, zipPath, dest string) error {
	resp, err := http.Get(url)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	out, err := os.Create(zipPath)
	if err != nil {
		return err
	}
	defer out.Close()
	_, err = io.Copy(out, resp.Body)
	if err != nil {
		return err
	}
	fmt.Println("Extracting...")
	r, err := zip.OpenReader(zipPath)
	if err != nil {
		return err
	}
	defer r.Close()

	for _, f := range r.File {
		fpath := filepath.Join(dest, f.Name)
		if f.FileInfo().IsDir() {
			os.MkdirAll(fpath, 0755)
			continue
		}
		os.MkdirAll(filepath.Dir(fpath), 0755)
		rc, _ := f.Open()
		defer rc.Close()
		outFile, _ := os.Create(fpath)
		io.Copy(outFile, rc)
		outFile.Close()
	}
	os.Remove(zipPath)
	return nil
}
