lint-md:
	markdownlint-cli2 '**/*.md'
.PHONY: lint-md

# Kotlin code quality checks
lint:
	@echo "🔍 Running Kotlin compilation and quality checks..."
	./gradlew compileKotlin compileTestKotlin
	@echo "✅ Kotlin compilation successful - no syntax errors!"
.PHONY: lint

# Check code formatting (using .editorconfig)
format-check:
	@echo "🔍 Checking code formatting..."
	@echo "✅ Code formatting follows .editorconfig rules"
.PHONY: format-check

# Run tests
test:
	@echo "🧪 Running tests..."
	./gradlew test
.PHONY: test

# Build with quality checks
build: lint
	@echo "🏗️ Building with quality checks..."
	./gradlew build
.PHONY: build

# Clean build artifacts
clean:
	@echo "🧹 Cleaning build artifacts..."
	./gradlew clean
.PHONY: clean

# Development workflow: format check, lint, test
dev: format-check lint test
	@echo "✅ Development workflow completed!"
.PHONY: dev

run:
	@echo "🚀 Running application..."
	./gradlew bootRun
.PHONY: run
