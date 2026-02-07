.PHONY: build install-phone clean test lint

# Build debug APK
build:
	./gradlew assembleDebug --no-daemon

# Build and install on connected device
install-phone: build
	adb install -r app/build/outputs/apk/debug/app-debug.apk

# Clean build artifacts
clean:
	./gradlew clean --no-daemon

# Run unit tests
test:
	./gradlew test --no-daemon

# Run lint checks
lint:
	./gradlew lint --no-daemon

# Build release APK (requires signing config)
release:
	./gradlew assembleRelease --no-daemon

# Quick rebuild (incremental)
quick:
	./gradlew assembleDebug --no-daemon
	adb install -r app/build/outputs/apk/debug/app-debug.apk

# Uninstall from device
uninstall:
	adb uninstall com.interfast || true

# Full reinstall
reinstall: uninstall install-phone

# Show connected devices
devices:
	adb devices

# View app logs
logs:
	adb logcat -s "Interfast" "*:E"
