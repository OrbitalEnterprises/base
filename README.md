[![Build Status](https://travis-ci.org/OrbitalEnterprises/orbital-base.svg?branch=master)](https://travis-ci.org/OrbitalEnterprises/orbital-base)

# Base Utilities for for Orbital Enterprises tools

This module provides property management and other simple
utilities used in most Orbital Enterprises tools.  Property management
in a typical Orbital Enterprises application is a combination of property files
and properties persisted in a backing store (like a database).
The usual pattern is to store cross-session configuration properties
in a persistent store which preserves settings across restarts.  
Properties not intended to be changed at runtime, or default values for properties, are stored
in properties files which are loaded at startup.  We explain this pattern in more detail below.

## Build/Depend

We use [Maven](http://maven.apache.org) to build the base library, and publish to [Maven Central](http://search.maven.org/).
Therefore, the easiest way to use the base library is to add the following dependency to your pom.xml:

```xml
<dependency>
    <groupId>enterprises.orbital</groupId>
    <artifactId>base</artifactId>
    <version>2.0.0</version>
</dependency>
```

You can find more details about the artifact [here](http://mvnrepository.com/artifact/enterprises.orbital/base).

The base library has no additional dependencies.  So if you're not using Maven you can simply download the jar from Maven Central.

## Simple Properties Management

The `OrbitalProperties` class is used to manage standard Java properties files.
The typical pattern is to call `OrbitalProperties.addPropertyFile` early in the startup
of your application.  We use this class, instead of a standard Java properties file,
in order to allow multiple Maven modules to each specify their own property files.
In a typical multi-module Orbital Enterprises tool, each module adds its own
property file during startup.

## Persisted Properties

Properties which should persist across multiple sessions or instances of an application
are managed by the `PersistentProperty` class.  This class allows the specification of a
`PersistentPropertyProvider` which determines how properties are saved across sessions.
The default provider is an in-memory provider and therefore does not persist settings.
Applications are required to set the persistent property provider before accessing
saved properties.

### Persistent Property Keys

A common pattern in Orbital Enterprises tools is to associate properties with a particular Java class.
For example, suppose we define a `UserAccount` class and suppose further we want to limit the number
of password attempts before locking the account.  We might define a `maxPasswordAttempts`
property and associate it with the `UserAccount` class.  Further, we might want this setting to be
different for different instances of `UserAccount`, and therefore encode some user-specific information
as part of the key.  That is, the property would be a string of the form
`UserAccount.userInfo.maxPasswordAttempts`, where `userInfo` is an instance specific string 
that is made part of the key.

To avoid having to construct keys in this way (which is tedious and error prone), the `PersistentProperty`
class supports passing an instance of `PersistentPropertyKey` which can be used to map an object
and key value to a string.  Using the example above, an instance of `PersistentPropertyKey` replaces
this call:

```java
UserAccount acct;
PersistentProperty.getIntegerProperty("UserAccount." + acct.getInfo() + ".maxPasswordAttempts");
``` 

with this one:

```java
UserAccount acct;
PersistentProperty.getIntegerProperty(acct, "maxPasswordAttempts") ;
```

In this example, `UserAccount` implements `PersistentPropertyKey<String>` as follows:

```java
public class UserAccount implements PersistentPropertyKey<String> {
  public String getPeristentPropertyKey(String field) {
    return "UserAccount." + acct.getInfo() + "." + field;
  }
  ...
}
```

## Persistent Properties with Defaults Pattern

Most Orbital Enterprises applications use a combination of persisted properties
with default or reference values stored in traditional properties files.  To
facilitate this pattern, the `PersistentProperty` class provides "fallback" methods
which revert to the `OrbitalProperties` class if a property can not be found (and 
optionally a default value if the property is not defined in a properties file).

Continuing with the example in the previous section, a typical call would appear
as follows:

```java
UserAccount acct;
PersistentProperty.getIntegerPropertyWithFallback(acct, "maxPasswordAttempts", 3);
```

This call will first attempt to look up a persistent property of the appropriate form.  If
no such property exists, then the call will check whether the property is defined
in the `OrbitalProperties` class.  If that lookup also fails, then the call will return
'3' as the default value.
