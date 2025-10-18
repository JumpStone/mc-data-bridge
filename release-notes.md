Features

   * deps: A new script scripts/update_deps.sh has been added to automate the process of updating Maven dependencies. This
     script will check for the latest versions, update the pom.xml, and verify the build, which will streamline the
     development workflow.

  Refactoring

   * potion: The SerializablePotionEffect class has been updated to use the modern, key-based API for serializing and
     deserializing potion effect types. This change fixes the use of deprecated methods and makes the serialization more
     robust and future-proof.

  Chore

   * The project version has been bumped to 1.21.8.2.