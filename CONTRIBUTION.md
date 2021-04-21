# Contributing to `astminer`

We are happy to see `astminer` find use beyond our own mining tasks thanks to its extensibility.
Please help make `astminer` easier to use by sharing your use cases.

We welcome contributions that improve quality of code and documentation, address issues, or expand the scope of use — for example, by adding wrappers for new third-party parsers.

We invite you to collaborate on ensuring the quality of contributions by folowing several essential rules. 

- **Use pull requests**. Automated assignee selection should work fine. When in doubt, please assign the PRs to @vovak, @egor-bogomolov, or @spirinegor.
- **Keep the code clean.** Please use the "Reformat code" IDE action on all modified code, do not ignore IDE or compiler warnings.
- **Write unit tests**. This is particularly important when adding new features to the core modules to ensure that subsequent changes do not break anything.
- **Keep the build green.** Adding support for external tools might involve adding new dependencies and modifying the build process. Please make sure that external dependencies are either included as package dependencies or are otherwise installed during the build of `astminer` without modifying the system environment outside the folder of this repository. Please do not assume any additional environment requirements in the CI build so that it represents a build on a freshly cloned repository.

