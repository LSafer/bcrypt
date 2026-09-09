<h3>
Kotlin Multiplatform BCrypt
<a href="https://jitpack.io/#net.lsafer/bcrypt">
<img src="https://jitpack.io/v/net.lsafer/bcrypt.svg"/>
</a>
<a href="https://central.sonatype.com/namespace/net.lsafer.bcrypt">
<img src="https://img.shields.io/maven-central/v/net.lsafer.bcrypt/bcrypt?color=green"/>
</a>
</h3>

Multiplatform bcrypt library.

### Install

```kts
repositories {
    mavenCentral()
}

dependencies {
    // Replace TAG with the desired version
    implementation("net.lsafer.bcrypt:bcrypt:TAG")
}
```

### Browser Setup

Due to packaging problems. BCrypt in the browser may require adding this:

```html
<script src="https://unpkg.com/bcryptjs@3.0.3/umd/index.js"
        crossorigin="anonymous">
```
