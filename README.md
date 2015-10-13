# Energy Flexibility Platform & Interface (EF-Pi)

This repository contains the EF-Pi software platform. The EF-Pi is a software platform on which Energy Applications can be installed.

The Energy Flexibility Platform & Interface (EF-Pi) aims to create an interoperable platform that is able to connect to a variety of appliances and support a host of Demand Side Management (DSM) approaches. This way the energy management system (EMS) hardware does not need to be changed when a consumers switches from one service to another. At the same time the EF-Pi makes it easier for service providers to introduce new services, since they do not have to provide the EMS hardware to their consumers to go with it.

For an explanation of the platform take a look at the [YouTube video](https://www.youtube.com/watch?v=rZq0DkAW5e4). For a better explanation of the platform you can read the [Whitepaper](https://github.com/flexiblepower/flexiblepower.github.io/raw/master/download/Whitepaper%20EF-Pi%20final%20june%201st%202015%20version.pdf).

This repository only contains the EF-Pi platform. For open-source applications running on the EF-Pi take a look at the [fpai-apps repository](https://github.com/flexiblepower/fpai-apps/).

## Where is the FPAI? Or what is the FPAI?
The EF-Pi was previously named the FlexiblePower Application Infrastructure (FPAI). Since this abbreviation was not very suitable for international use, the FPAI was renamed to EF-Pi. Since renaming git repositories can be quite cumbersome, we still use the name FPAI for the git repositories.

## Demo
You can download an archive with the demonstration distribution of the EF-Pi with some applications installed [here](https://github.com/flexiblepower/fpai-apps/releases/download/v15.05/fpai-apps-runtime-release-15-05.zip). The archive contains a manual PDF.

## Developer documentation
The latest version of the documentation can be found [here](http://fpai-ci.sensorlab.tno.nl/builds/fpai-documentation/development/html/index.html).

Documentation for older versions can be found [here](http://fpai-ci.sensorlab.tno.nl/builds/fpai-documentation/). Here you can also find a PDF version of the documentation.

Documentation is generated from the [fpai-documentation repository](https://github.com/flexiblepower/fpai-documentation). We are happy to accept any contributions to the documentation.

## Release notes
Release notes can be found on the [Release page](https://github.com/flexiblepower/fpai-core/releases).

## License
The EF-Pi is released under the Apache License 2.0, which can be found in the LICENSE file.

## Fpai-core Questions?
[Ask your question here](https://github.com/flexiblepower/fpai-core/issues/new?title=Question:My%20Title&body)

## Bugs
[See FAN-wiki](https://github.com/flexiblepower/FAN-wiki/wiki/Bug-tracking-process)

# Contributing
We welcome contributions to the project. If you have any questions, contact the [Community Council](https://github.com/orgs/flexiblepower/teams/community-council).

## Continuous Integration
A CI server is available for the flexiblepower projects on http://fpai-ci.sensorlab.tno.nl/. This server tries to build every commit on every branch for the configured projects (at the time of writing this is fpai-core, fpai-devices). The builds are placed on http://fpai-ci.sensorlab.tno.nl/builds.

Each commit is built as a snapshot, which is saved for a limited amount of time (they will be cleaned up sometime). These can be used to experiment with the latest development version. For the final release, the build will be put here on GitHub as a real release.

## Branching
We use several branches:

- The **master** branch is used for releases. Each commit on this branch should be a merge from the development branch and changing the version to final. Only one of the main developers should touch this branch.
- The **development** branch is used for latest snapshots. Each new feature will first be merged into this branch to be able to test if it works with the rest of the code. These should always be done through pull requests. Developers are discouraged from pushing to this branch directly.
- For each new feature of bugfix a new branch should be created. This branch should start with the issue number and then a short description (e.g. 19-fix-npe-connectionmanager). These branches should simply be pushed to github and then you can create a pull request to merge it back to development. Make sure that this can be done cleanly (when you are behind the current development version, you can always use *git rebase*).
- New features unrelated to a bugfix, should be put in a new branch. The name must not start with a number, but be a short description (e.g. persisting-connections). Same as above, merges should be possible to do cleanly.

## Coding Conventions

The coding conventions for the Ef-Pi projects are captured in the project settings, which provide a Eclipse formatter (e.g. see [fpai.api formatter settings](https://github.com/flexiblepower/fpai-core/blob/development/flexiblepower.api/.settings/org.eclipse.jdt.core.prefs)) and some save actions (e.g. see [fpai.api save actions settings](https://github.com/flexiblepower/fpai-core/blob/development/flexiblepower.api/.settings/org.eclipse.jdt.ui.prefs)). A summary of the conventions:

* Use 4 spaces for indentation.
* Use indentation for each new block of code (e.g. class, method, if statement) except in the switch body (the case statement is on the same level, after the case statement do indent).
* All opening braces are on the same line as the statement starting the block.
* Use default white spaces. This means 1 space after a comma or semicolon, and 1 space before and after each special operator (e.g. + - / * etc.) with the exception of the dot (function call operator).
* There should never be white space at the end of a line.
* Use at least 1 empty line between methods. We may keep several private variable definitions together to indicate that they are related.
* There must be 1 empty line at the end of the file.
* All empty block statements must contains a newline.
* Keep `} else {`, `} else if {`, `} while()`, `} catch() {` and `} finally {` on the same line.
* Maximum line width is 120 characters for both code and comments.
* Wrapping only occurs when it can't fit on a single line. If wrapping is needed, the all elements are placed on a new line.
* Comments are indented the same as the code where they belong to.
* Multi-lined comments or javadoc use a `*` for every new line.
* The import statements should be organized (e.g. no unused imports and no import all from a package).
* Block statements should always use brackets, even when it is a single statement.
* Make private variable in classes final as much as possible. For data object, prefer to make them [immutable](http://en.wikipedia.org/wiki/Immutable_object#Java).
* For member access, the `this.` qualifier should only be used when necessary.

### Example code

Below is an piece of dummy code to show what the effect of these rules are:

```java
/**
 * Javadoc comments with some <code>HTML</code> tags.
 * <ul>
 * <li>And</li>
 * <li>a</li>
 * <li>list</li>
 * </ul>
 */
class Example {
    int[] myArray = { 1, 2, 3, 4, 5, 6 };
    int theInt = 1;

    String someString = "Hello";
    double aDouble = 3.0;

    /*
     * Some non-javadoc comment
     */
    void foo(int parameter1,
             int parameter2,
             int parameter3,
             int parameter4,
             int parameter5,
             int parameter6) {
        switch (parameter1) {
        case 0:
            Other.doFoo();
            break;
        default:
            // Single line comment
            Other.doBaz();
        }
    }

    void bar(List<Integer> v, int max) {
        for (int i = 0; i < max; i++) {
            if (i % 2 == 0) {
                v.add(i);
            } else {
                v.add(-i);
            }
        }
    }
}

enum MyEnum {
    UNDEFINED(0) {
        void foo() {
        }
    }
}

@interface MyAnnotation {
    int count() default 1;
}
```