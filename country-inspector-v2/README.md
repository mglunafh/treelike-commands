# Country inspector, version 2

- A Kotlin-based console application which explores the contents of a particular country.
- There are several levels of commands (available are shown by `help`)
  applied to the corresponding entities: overview / country / city.
- Commands from the levels above are accessible from the lower levels with the only exception being 
  lower-level commands override higher-level commands with the same name.
- It is a second version of the app with slightly more convenient inheritance hierarchy and better class namings,
  which helped efficiently implement command parsing and transforming commands into actions.

---
- JDK version: `17`
- Kotlin version: `1.9.0`
- Command `gradle build` should do the magic, after that the executable jar
  could be found on `build/libs/country-inspector-v2-<version>-all.jar`.
