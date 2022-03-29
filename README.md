# Codegen

A lightweight framework for code generating.

## Why Codegen?

In Java/Kotlin world, engineers usually use [JavaPoet](https://github.com/square/javapoet) or [KotlinPoet](https://github.com/square/kotlinpoet) for code generating, those tool libraries are really cool, but it increases code maintainability, using [JavaPoet](https://github.com/square/javapoet) or [KotlinPoet](https://github.com/square/kotlinpoet) to generate Java/Kotlin source code like using JSP languages to write a web page, you never know what you have written after a few months.

Codegen separates the generating logic into template and data model, it a little bit like SSR (Server Side Rendering), and provides the well-known template engines integration, e.g. [Mustache](https://mustache.github.io/), [Velocity](https://velocity.apache.org/), it also supports custom template engine.

## Example

See the example of [auto factory](./example/src/main/kotlin/io/johnsonlee/codegen/example/AutoFactoryCodegen)