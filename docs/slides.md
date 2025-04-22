---
theme: seriph
transition: slide-left
highlighter: shiki
title: "SC2002 Slides"
info: |
    SC2002 Object-Oriented Design & Programming
    Assignment Presentation
    Group 1
---

# SC2002 OODP 
## BTO Management System

<div class="opacity-75">
Group 1 | 22 April 2025
</div>

<img src="https://download.logo.wine/logo/Nanyang_Technological_University/Nanyang_Technological_University-Logo.wine.png" alt="NTU Logo" class="w-32 absolute bottom-6 right-6" />

---
layout: intro
---

# Our Team <span class="ml-2 opacity-80"><i-carbon-group-account /></span>

<div class="grid grid-cols-5 gap-4 text-center mt-12">
  <!-- Column 1 -->
  <div>
    <img src="https://avatars.githubusercontent.com/u/85245154?v=4" class="w-24 h-24 rounded-full mx-auto mb-2 object-cover"/>
    <p class="font-semibold">Tang Xinbo</p>
    <p class="text-xs opacity-70">Tester/Designer</p>
  </div>
  <!-- Column 2 -->
  <div>
    <img src="https://avatars.githubusercontent.com/u/52971804?v=4" class="w-24 h-24 rounded-full mx-auto mb-2 object-cover"/>
    <p class="font-semibold">Teo Wei Xiang</p>
    <p class="text-xs opacity-70">Team Leader</p>
  </div>
  <!-- Column 3 -->
  <div>
    <img src="https://avatars.githubusercontent.com/u/155806360?v=4" class="w-24 h-24 rounded-full mx-auto mb-2 object-cover"/>
    <p class="font-semibold">Toh Xin Yi</p>
    <p class="text-xs opacity-70">Programmer</p>
  </div>
  <!-- Column 4 -->
  <div>
    <img src="https://avatars.githubusercontent.com/u/171407011?v=4" class="w-24 h-24 rounded-full mx-auto mb-2 object-cover"/>
    <p class="font-semibold">Vishal JG</p>
    <p class="text-xs opacity-70">Programmer</p>
  </div>
  <!-- Column 5 -->
  <div>
    <img src="https://avatars.githubusercontent.com/u/181351248?v=4" class="w-24 h-24 rounded-full mx-auto mb-2 object-cover"/>
    <p class="font-semibold">Murugaraj Vishvaajit</p>
    <p class="text-xs opacity-70">Tester/Designer</p>
  </div>
</div>


---
layout: default # Standard layout
---

# Approach

<v-clicks>

* <span class="text-xl mr-1"><carbon-layers /></span> **Layered Architecture:** Decomposed the system into View, Controller, Service, Repository, Entity layers for <span v-mark.highlight.teal>Separation of Concerns</span>.

* <span class="text-xl mr-1"><carbon-connect-source /></span> **Abstraction & Interfaces:** Defined `IService` and `IRepository` interfaces to enable <span v-mark.highlight.orange>Loose Coupling</span> (DIP/ISP).

* <span class="text-xl mr-1"><carbon-rule /></span> **Design Patterns:** Applied the <span v-mark.underline.blue>Repository Pattern</span> for data access (CSVs) and Composition via <span v-mark.underline.purple>UI Helpers</span> for view logic reuse.

* <span class="text-xl mr-1"><carbon-cube /></span> **Modularity:** Resulting in maintainable and testable components.

</v-clicks>

---
layout: default
---

#  High-Level Architecture:

<div v-after class="mx-auto text-center mt-4">
  <img src="./3-tier-chart.png" alt="Layered Architecture Diagram" class="h-56 mx-auto object-contain" />
</div>

<br/>

1. **View Layer:** User interface components (CLI) for user interaction.
2. **Controller Layer:** Handles user input, coordinates between View and Service layers.
3. **Service Layer:** Business logic and application flow management.
4. **Repository Layer:** Data access layer for CSV file operations.
5. **Entity Layer:** Represents core data structures (e.g., Application, Booking).


---
layout: default
---

# Feature Walkthrough <span class="text-2xl"><carbon-video /></span>

**Scenario:** application-to-booking flow **(Live Demo)**

<div class="grid grid-cols-2 gap-4">

<div>

- **Project: "Maple Grove" (PROJ004)**
    - Created by T4000001F (Alice Lim)
    - Visible: ON
    - Application Period: Active (e.g., Apr 1 - Apr 30, today is Apr 15)
    - Offers ONLY TWO_ROOM (80 total, 80 remaining)

</div>

<div class="-mt-4">

- **<span v-mark.underline.blue>Applicant:</span>** Bob Lee (S1000002B)
    - Married, 30yo
    - No active application.

- **<span v-mark.underline.green>Officer:</span>** Charlie Tan (T3000001D)
    - No active application.
    - Approved for "Maple Grove"

- **<span v-mark.underline.purple>Manager:</span>** Alice Lim (T4000001F)
    - Manager for "Maple Grove"

</div>

</div>

---
layout: default
---

# Design & OOD Justification

*   **Architecture:** <span v-mark.highlight.cyan>Layered Design</span> (View-Controller-Service-Repository) reviewed - promotes maintainability.
*   **Key Patterns:**
    *   <span v-mark.underline.blue>Repository Pattern</span>: Abstracted CSV access via `IRepository`. Consistent interface for data operations.
    *   <span v-mark.underline.purple>UI Helpers (Composition)</span>: Reusable display logic (e.g., `ProjectUIHelper`). Reduced duplication in main UI classes.
*   **Interfaces:** Decoupled layers -> <span v-mark.highlight.green>Testability & Flexibility</span>.

<br/>

**Entity Decisions:**
*  **Application & Booking:** Clear separation reflecting workflow stages.
*  **Applicant Preference:** Added `preferredFlatType` to `Application` for better workflow context non-binding.



---
layout: default
---

# SOLID Principles: SRP & OCP <span class="text-2xl"><carbon-rule-test /></span>

*   **S**ingle **R**esponsibility **P**rinciple (SRP):
    *   Components have a single, well-defined responsibility.
    *   *Example:* `AuthenticationService` handles only login/logout, `BookingService` manages booking logic.

*   **O**pen/**C**losed **P**rinciple (OCP):
    *   Software entities should be open for extension, but closed for modification.
    *   *Example:* Services use `IRepository` interfaces. We can add new repository implementations (e.g., for a database) without changing the service code.

---
layout: default
---

# SOLID Principles: LSP & ISP <span class="text-2xl"><carbon-rule-test /></span>

*   **L**iskov **S**ubstitution **P**rinciple (LSP):
    *   Subtypes must be substitutable for their base types without altering correctness.
    *   *Example:* `Applicant` and `HDBStaff` inherit from `User` and can be used wherever a `User` object is expected (e.g., in authentication results).

*   **I**nterface **S**egregation **P**rinciple (ISP):
    *   Clients should not be forced to depend on interfaces they do not use.
    *   *Example:* Specific interfaces like `IApplicationService`, `IBookingService` ensure that controllers only depend on the methods relevant to their function.

---
layout: default
---

# SOLID Principles: DIP <span class="text-2xl"><carbon-rule-test /></span>

*   **D**ependency **I**nversion **P**rinciple (DIP):
    *   High-level modules should not depend on low-level modules. Both should depend on abstractions.
    *   Abstractions should not depend on details. Details should depend on abstractions.
    *   *Example:* Controllers and Services depend on abstractions (`IService`, `IRepository`), not concrete implementations. Dependencies are injected via constructors.

```java
// Example: ProjectService depends on Repository/Service interfaces
public class ProjectService implements IProjectService {
    // Dependencies are interfaces (abstractions)
    private final IProjectRepository projectRepo;
    private final IEligibilityService eligibilityService;
    // ... other dependencies

    // Dependencies are injected (Inversion of Control)
    public ProjectService(IProjectRepository projectRepo, IEligibilityService eligibilityService, ...) {
        this.projectRepo = projectRepo;
        this.eligibilityService = eligibilityService;
        // ...
    }
}
```

---
layout: default
---

# Testing Overview <span class="text-2xl"><carbon-debug /></span>

* **Approach:** <span v-mark.underline.blue>Manual Testing</span> based on Use Cases & Requirements/FAQ.
* **Focus:** Positive Paths, **<span v-mark.highlight.red>Error Handling</span>**, Business Rules, Edge Cases.

<br/>

**Test Case Coverage Examples:** *(Referencing Demo)*
*  <span class="text-lime-600 mr-1">✓</span>   Logins (Valid/Invalid) & Role Permissions
*  <span class="text-lime-600 mr-1">✓</span>  **Input Validation:** <span v-mark.circle.orange>NRIC, IDs, Selections, Empty</span>
*  <span class="text-lime-600 mr-1">✓</span>   State Transitions (Application/Registration Statuses)
*  <span class="text-lime-600 mr-1">✓</span>   Business Rules: <span v-mark.circle.purple>Single App Limit, Reg Conflicts, Unit Counts</span>
*  <span class="text-lime-600 mr-1">✓</span>   Data Persistence (CSV Load/Save)


---
layout: center
class: text-center
---

# Conclusion <span class="text-3xl text-green-500"><carbon-checkmark-outline /></span>

<div class="text-xl mt-4">
Successfully developed a CLI BTO Management System applying <span v-mark.highlight.teal>OO principles</span> for a <span v-mark.highlight.lime>modular</span> and <span v-mark.highlight.lime>robust</span> design, meeting core requirements.
</div>

---
layout: center
class: text-center
---

# Thank You

<div class="text-2xl my-4">
Q & A
</div>