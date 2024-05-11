# TO-DO list

----
## Command Line Parser
- Base project all other may have to rely on: command-line parser for covering more advanced use cases
  like dashed parameters and arguments with spaces. Additional materials:
  - https://yetanotherchris.dev/csharp/command-line-arguments-parser/
  - https://jawher.me/parsing-command-line-arguments-finite-state-machine-backtracking/

- CommandObjectFactory:
  - interface 'CommandObjectParser' should include polymorphic validation calls
  - think about positional arguments which currently do not support type conversions
  - add javadoc for the rest of the `generic-parsing` submodule:
    CommandLineArgumentParser, ParseResult, ParseError, ParseSuccess etc.

----

## Country Inspector, v2
- experimental-branch: remove generic type from `interface CommandObject<H>` and see what happens.
- history navigation via arrows (another investigation on working with console in character mode)

----
## Geometry Enthusiast
- execution errors in InspectionScope
- remove Id registry, it's bug-inducing
- implement triangle (tag, show --point)
- implement rectangle
- think about adding physical parameters to the models:
  - section length 
  - area of the 2D figures
  - volume of the 3D figures
  - height of the pyramid
- Ways to show the contents of the figures:
  - nested tree structure
  - list of points, sections, 2d figures
- ways to show the info about hierarchy of the figure, what entities it belongs to.
- ways to show the properties of the higher-order entities this figure belongs to.
- Write down the entities required for the implementation based on the Country Inspector Postmortem.
