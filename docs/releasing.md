# Release preparation

JSeed uses a snapshot version while preparing for Maven Central publication.

JSeed source code is licensed under [Apache-2.0](../LICENSE). Third-party dependencies retain their own licenses. The XXL-Job dependency declares GPL-3.0 in its published metadata. Review the distribution terms before uploading the executable example JAR, which bundles dependencies.

Confirm a Maven Central namespace the maintainer can verify. Set the final group ID, release version, SCM URL and license metadata before publication. The example application is excluded from dependency publication.

Run `mvn -Prelease-artifacts verify` to build and check the binary, source and Javadoc JARs locally.

Use the [Central Portal publishing plugin](https://central.sonatype.org/publish/publish-portal-maven/) and check the [required metadata, sources, Javadoc and signatures](https://central.sonatype.org/publish/requirements/) when preparing the first release.
