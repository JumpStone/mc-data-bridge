# Contributing to MC Data Bridge

Thank you for your interest in contributing to MC Data Bridge! We welcome contributions from the community.

## Getting Started

1.  **Fork the Repository**: specific to your account.
2.  **Clone the Repository**: `git clone https://github.com/YOUR_USERNAME/mc-data-bridge.git`
3.  **Create a Branch**: `git checkout -b feature/amazing-feature`

## Development Workflow

1.  Make your changes.
2.  Ensure your code follows the existing style.
3.  **Run Tests**: Ensure all tests pass using `mvn test`.
    - PRs will not be merged if tests fail.

## Release Process (Maintainers Only)

To prepare a new release:

1.  **Update Version**: Run the helper script to verify and update versions across all files.
    ```bash
    ./scripts/update-version.sh
    ```
2.  **Update Release Notes**: Manually edit `release-notes.md` to describe the changes. **The build will fail if this is missed.**
3.  **Commit & Push**: Commit the version changes and push to a feature branch.
4.  **Create PR**: Open a PR to `main`.
5.  **Merge**: Once approved and checks pass, merge to `main` to trigger the automated release.

## Pull Requests

1.  Push your branch to your fork.
2.  Open a Pull Request against the `main` branch.
3.  Describe your changes clearly.
4.  Wait for review. Note that approval from `@westkevin12` is required for merging.

## Rules

- **No Force Pushes**: History must be preserved.
- **Tests Must Pass**: The CI pipeline will verify your build.
