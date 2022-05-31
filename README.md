[![Build Status](https://travis-ci.org/Inbot/inbot-utils.svg?branch=master)](https://travis-ci.org/Inbot/inbot-utils)
[![](https://jitpack.io/v/jillesvangurp/inbot-utils.svg)](https://jitpack.io/#jillesvangurp/inbot-utils)

# Introduction

Inbot-utils is a collection of utility classes that I used at [Inbot](http://inbot.io). After Inbot shut down in 2018, I continued using it in other projects and it is still being used at my current start up [FORMATION](https://tryformation.com) where it serves us well.

This is a gathering of small helper functions and utility classes that I created over time that I reach for on new projects.

It includes:

- Some helper functions to create various types of hashes using varargs functions
- A decent bcrypt implementation for password hashing
- Some helpers for dealing with JWTs
- Some helpers for encrypting content using AES in a responsible way
- A replacing input stream for fixing things like new lines in legacy data files
- A String trie implementation (useful if you need autocomplete)
- Various things that made more sense before I switched to Kotlin to deal with Java's verbosity and lack of expressiveness.

# Repository move to jillesvangurp

Inbot the company for which I developed this library originally, is no more. So I forked the repository and put it under my own account. I will likely change the name at some point. Any future releases will be done from here. I always was the main author of this and the license has stayed the same throughout (MIT).

# Install using jitpack.io

[![](https://jitpack.io/v/jillesvangurp/inbot-utils.svg)](https://jitpack.io/#jillesvangurp/inbot-utils)

# Overview

Have a look around. Most of the classes have decent tests.

Note. all dependencies are marked as compileOnly, so you have to add them to your build file. This is to make this library minimally intrusive on your build.

Currently these classes are included:
- JwtTokenCreationService helper around the auth0 JWT implementation for creating JWT tokens in a somewhat opinionated way.
- JwtVerificationService helper to verify JWT tokens (also uses the auth0 implementation)
- KeyPairUtils helper to make parsing and serializing elliptic curve key pairs needed for JWT a bit less tedious
- PasswordHash: we grabbed this implementation from http://crackstation.net/hashing-security.htm and preserved the license info (also MIT). This class implements a secure way of hashing passwords with randomized salt. Don't reinvent this wheel please. Big thanks to Taylor Hornby and his friends at crackstation.net.
- AESUtils: encrypt/decrypt blobs using AES with a randomized salt and specified key. This makes encryption easy, safe (AES cipher in CBC mode with PKCS7, 256 bit key), and free of boilerplate. Uses the bouncy-castle library so you don't have to deal with Oracle's restrictions on key length.
- ArrayFoo: misc static methods for manipulating arrays and sets.
- CompressionUtils: compress/decompess using gzip; convenient wrappers around the built in java compression classes. Reduce the amount of boiler plate needed for the simple job of compressing stuff.
- HashUtils: misc utilities to create md5 sha1 and other hashes without having to deal with checked exceptions or arcane details on how to set this up.
- Md5Appender: helper class to creat md5 hashes incrementally by appending objects to it. We use this for Etags.
- Md5Stream: helper class to build up an md5 stream from an outputstream. Useful when generating e.g. ETags by serializing large object structures without buffering the entire thing in memory. Uses the Md5Appender. Simply stream your content and get the hash afterwards.
- MiscUtils: equalsAny method that returns true if the first arg is equal to any of the remaining ones (varargs)
- StrategyEvaluator: varargs and Optional based implementation of the strategy pattern. Nice alternative to having nested ifs with lots of null checks.
- PatternEvaluator: lambda functions based implementation of matching a pattern and then doing something like you can in many functional languages.
- IOUtils: helper methods to quickly work with streams and readers in a responsible way. Java IO requires a lot of boilerplate; this makes that a bit less verbose.
- Math: a few methods that are missing from java.lang.Math that are useful to have around: `safeAbs` (the existing abs has edgecases), `long pow(long l, int exp)`, `double normalize(double i, double factor)` simple logistic function for getting a value between 0 and 1, `double round(double d, int decimals)`
- MdcContext to have a way of temporarily adding attributes to the logging MDC with cleanup. MdcContext implements Closeable so you can use `try...finally`
- ReinitializingReference - allows you to periodically calculate something expensive in a thread safe way. When you get the reference, it checks whether it needs to be recalculated and if needed does so (blocks until done). If you've ever implemented double checked locking (or made the rooky mistake of not doing double locking or doing it wrong) to do something similar, this is a cleaner and safer way.
- SimpleStringTrie - a straightforward implemnentation of a Trie that uses HashMaps to implement a simple prefix tree. Nothing special, but we needed one and could not be bothered to pull in e.g. Apache Commons Collections.
- MapFactory with some convenience methods, a simple multi map implementation, a delegating map, and a RichMap interface with convenient default methods.


This library requires Java 8.

# Bugs/fixes & license

This code is [licensed](https://github.com/Inbot/inbot-utils/blob/master/LICENSE) under the MIT license.

We welcome pullrequests but please respect that this is a small project and that we use it in production and can't change everything at will. That being said, we love your feedback, suggestions, and pull requests. All this means is that we want to keep things stable, simple, and backwards compatible unless there is a good reason otherwise.

Given the nature of this project and the license, we fully understand if people want to just grab some of the code and copy it over. The license allows this and we don't mind it if you do this at all. However, if doing, so, please retain a comment acknowledging our copyright and the license. 


# Changelog
- 1.30
  - use gradle and update some dependencies 
- 1.29
  - Extract sign method in JwtTokenCreate service so users can use a custom builder.
- 1.28
  - Add some helpers for creating and verifying JWT tokens and dealing with elliptic curve key pairs
- 1.27
  - ReinitializingReference now implements Supplier
- 1.26
  - Add EnumUtils with some nice helper methods
- 1.25
  - fix bug with trie not returing all possible matches
- 1.24
  - bug fix for SimpleStringTrie with new match method
- 1.23
  - bug fix for SimpleStringTrie
- 1.22
  - add utility method for streaming regex matches against a big string input
  - add match method to SimpleStringTrie to allow for returning all strings in the trie that match a prefix.
- 1.21
  - Add helper method to calculate serialized object size. Useful when you need to know how much heap some big object is taking roughly.
  - Add MdcAware for inheriting parent thread MDC when using multi threaded code with Runnable.
- 1.20
  - Minor improvements to Math.pow
  - Refactor MapFactory and add lots of functionality.
- 1.19
  - Add ReplacingInputStream with convenience method to fix dos and mac new lines
- 1.18
  - Add new withContext method that takes a lambda in MdcContext as an alternative to try with resources.
- 1.17
  - add reset method to ReinitializingReference that allows you to force a reinitilize before it is triggered automatically
  - add a readBytes method to IOUtils that returns a byte array.
- 1.16
  - add stringify for collections as well in ArrayFoo
- 1.15
  - Add SimpleStringTrie (we needed a Trie)
- 1.14
  - ReinitializingReference added, useful for things that you want to periodically recalculate in a thread safe way
  - Add IOUtils.lines(String resourcename) method
- 1.13
  - Change AESUtils to rely on the bouncycastle library instead of JDK bundled ciphers. This change is backward/forward compatible. Reason for the change is that as of JDK 8 u101, 256 bit key length can no longer be forced without installing a jdk policy package due to some internal changes. Bouncycastle does not have this restriction and therefore is a bit more robust to get working.
- 1.12
  - Add PatternEvaluator inspired by https://kerflyn.wordpress.com/2012/05/09/towards-pattern-matching-in-java/. This allows for a more functional style of programming in Java similar to what you would do in e.g. Haskell, Kotlin, or Scala. Mind you this is still Java, so beware tedious syntax and some runtime overhead.
- 1.11
  - Minor performance tweak on AESUtils, additional round method in Math
- 1.10
  - add Md5Stream
- 1.9
  - add MdcContext
- 1.8
  - add normalize function to Math for normalizing numeric values to something between 0 and 1 (based on a simple logistic function)
  - add int version of safeAbs to Math
- 1.7
   - use latest commons-lang
- 1.6 - broken release; skip
- 1.5
   - add map factory similar to Jsonj object factory
   - use locale in call to toLowerCase
   - improve ArrayFoo.combine to allocate correct size for ArrayList
- 1.4 - add url encode/decode functions to MiscUtils that uses UTF-8 as RFC 3986 recommends and that throw no checked exceptions related to character encoding
- 1.3 - Minor fixes to javadoc and IOUtils
- 1.2 - Also add the secure password implementation we grabbed from http://crackstation.net/hashing-security.htm; was already MIT licensed
- 1.1 - Add a few more classes: Md5Appender, IOUtils, Math
- 1.0 - Initial release
