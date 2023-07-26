# Country inspector, version 1

- A console application written in Kotlin which explores the contents of a particular country.
- There are several levels of commands (available ones are shown  by `help`)
    applied to the corresponding country entities: country itself and cities in it.
- Commands from the levels above are accessible from the lower levels with the only exception being
    lower-level commands override higher-level commands with the same name.

---
- JDK version: `17`
- Kotlin version: `1.9.0`
- Command `gradle build` should do all building, after that the executable jar 
  could be found on `build/libs/country-inspector-v1-<version>-all.jar`.
