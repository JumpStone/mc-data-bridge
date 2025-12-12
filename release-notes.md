# Release Notes - v2.0.7

## 🚀 Open Source & Automation Update

This release marks a major milestone for the project's infrastructure, enabling a fully automated release pipeline and preparing the codebase for public contributions.

### ✨ CI/CD Pipeline

- **Automated Releases**: Merging to `main` now automatically builds, verifies, tags, and publishes releases to GitHub.
- **Strict Version Control**: CI now enforces version consistency across `pom.xml`, `plugin.yml`, `bungee.yml`, and `release-notes.md`.
- **Branch Protection Support**: The pipeline is configured to work securely with protected branches using explicit token authentication.

### 🛠 Developer Experience

- **New Helper Script**: Added `scripts/update-version.sh` to streamline version bumps across all project files.
- **Documentation**: Overhauled `CONTRIBUTING.md` and `README.md` with clear instructions for contributors and build processes.
- **Standardization**: Enforced consistent versioning (SNAPSHOTs for dev, semantic versioning for releases).
