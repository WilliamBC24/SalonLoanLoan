# Contributing Guidelines
This document outlines the contribution workflow for our capstone project team.

## Roles
- **Leader & Main Developer (Son Bui):** System design, backend core, code integration, reviews
- **Business Analyst & Documentation, Frontend Developer (Khanh Huy):** Requirements, documentation, client-side dev, overall testing
- **Backend Developer (Dai An):** Backend dev, code integration
- **Tester (Thu Lan):** UI/UX, usability testing
- **Frontend Developer (Ha Phuong):** UI/UX , client-side dev 

## Workflow
1. **Branching Strategy**
   - `main` branch: stable code only
   - Each feature or bug fix should be developed in a new branch:
     ```
     git checkout -b feature/<feature-name>
     git checkout -b bugfix/<bug-name>
     ```

2. **Commit Messages**
   - Use clear, descriptive messages:
     ```
     feat: add appointment booking API
     fix: correct SQL query for customer search
     docs: update README with project overview
     ```

3. **Pull Requests**
   - Push your branch to GitHub and open a Pull Request (PR).
   - Assign the PR to the leader for review before merging into `main`.

4. **Issues**
   - All tasks, bugs, or enhancements must have an Issue.
   - Assign Issues to yourself or teammates.
   - Use labels such as `frontend`, `backend`, `docs`, `bug`.

5. **Code Reviews**
   - The leader reviews all PRs before merging.
   - Reviewers should check for:
     - Code correctness
     - Readability
     - Alignment with project structure

6. **Testing**
   - Member 4 focus on testing workflows.
   - All developers should test their own code locally before opening a PR.

## General Guidelines
- Keep commits small and focused.
- Sync regularly with `main` to avoid conflicts.
- Communicate through Issues and pull request comments.
- Update documentation whenever a new feature is added.

---


