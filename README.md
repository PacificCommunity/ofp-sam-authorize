# Authorize
**Authorize** is a Java tools used to sign JARS and native executables before distribution.

## Dependencies
For native signing, **Autorize** currently depends on jsign, a Java implementation of Microsoft Authenticode.
http://ebourg.github.io/jsign/
https://github.com/ebourg/jsign
We use a custom modified distribution of JSign with added support for modularization.