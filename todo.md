# TO-DO list

## Country Inspector, v2
- experimental-branch: remove generic type from `interface CommandObject<H>` and see what happens.
- history navigation via arrows (another investigation on working with console in character mode)

----
## Geometry Enthusiast
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

----
## Command Line Parser
- Base project all other may have to rely on: command-line parser for covering more advanced use cases
  like dashed parameters and arguments with spaces. Additional materials:
  - https://yetanotherchris.dev/csharp/command-line-arguments-parser/
  - https://jawher.me/parsing-command-line-arguments-finite-state-machine-backtracking/

- CommandObjectFactory:
  - function `parsePositional` returns `ParseResult` 
  - merge parsing for positional and optional args
