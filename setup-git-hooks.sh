#!/bin/bash

# Git Hooks Setup Script for Pocket Ledger
# This script sets up pre-commit hooks for code quality checks

echo "Setting up Git hooks for Pocket Ledger..."

# Create hooks directory if it doesn't exist
mkdir -p .git/hooks

# Pre-commit hook
cat > .git/hooks/pre-commit << 'EOF'
#!/bin/bash

echo "Running pre-commit checks..."

# Run code formatting check
echo "Checking code format..."
./gradlew spotlessCheck
if [ $? -ne 0 ]; then
    echo "❌ Code format check failed. Run './gradlew spotlessApply' to fix."
    exit 1
fi

# Run tests
echo "Running tests..."
./gradlew test
if [ $? -ne 0 ]; then
    echo "❌ Tests failed. Please fix failing tests before committing."
    exit 1
fi

# Run static analysis
echo "Running static analysis..."
./gradlew pmdMain checkstyleMain
if [ $? -ne 0 ]; then
    echo "❌ Static analysis failed. Please fix code quality issues."
    exit 1
fi

echo "✅ All pre-commit checks passed!"
EOF

# Make hooks executable
chmod +x .git/hooks/pre-commit

# Pre-push hook (optional - more comprehensive checks)
cat > .git/hooks/pre-push << 'EOF'
#!/bin/bash

echo "Running pre-push checks..."

# Run full quality check
./gradlew qualityCheck
if [ $? -ne 0 ]; then
    echo "❌ Quality checks failed. Please fix all issues before pushing."
    exit 1
fi

echo "✅ All pre-push checks passed!"
EOF

chmod +x .git/hooks/pre-push

echo "✅ Git hooks setup complete!"
echo ""
echo "Hooks installed:"
echo "  - pre-commit: Format check, tests, basic static analysis"  
echo "  - pre-push: Full quality check suite"
echo ""
echo "To bypass hooks (not recommended):"
echo "  git commit --no-verify"
echo "  git push --no-verify"