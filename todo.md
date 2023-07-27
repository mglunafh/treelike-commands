# TO-DO list

## Country Inspector, v2
- implement `help --all`
- **not-so-well-thought behaviour**: commands changing some state can be called from undesirable places, 
  may introduce subtle bugs, ideally such types of commands should be inaccessible. 
  - Moscow level `country inspect Tver` switches the inspection object to Tver, 
    this doesn't break anything and intuitively happens to work as expected,
    but causes a lot of anxiety.
- command localization (v2)
- history navigation via arrows (another investigation on working with console in character mode)
- write Postmortem a.k.a. lessons learned

----
## Geometry Enthusiast
- Another project: Create and interact with a list of various geometric objects
    which can have different characteristics.
