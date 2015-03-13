# Howto #

The soundlibs project provides Maven artifacts of several sound libraries you may need for e.g. MP3 or OGG handling.
This page describes how to use these libraries in your project.

# Details #
Here is how you add the libraries as Maven dependencies to your project:
(Hint: as soon as artifacts are releases to Sonatype OSS repository,
SNAPSHOTs are to be replaced by release version numbers.)

  * Tritonus Share:
```
<dependency>
  <groupId>com.googlecode.soundlibs</groupId>
  <artifactId>tritonus-share</artifactId>
  <version>0.3.7-2-SNAPSHOT</version>
</dependency>
```

  * MP3 SPI:
```
<dependency>
  <groupId>com.googlecode.soundlibs</groupId>
  <artifactId>mp3spi</artifactId>
  <version>1.9.5-2-SNAPSHOT</version>
</dependency>
```

  * JLayer:
```
<dependency>
  <groupId>com.googlecode.soundlibs</groupId>
  <artifactId>jlayer</artifactId>
  <version>1.0.1-2-SNAPSHOT</version>
</dependency>
```

  * Vorbis SPI:
```
<dependency>
  <groupId>com.googlecode.soundlibs</groupId>
  <artifactId>vorbisspi</artifactId>
  <version>1.0.3-SNAPSHOT</version>
</dependency>
```

  * JOrbis:
```
<dependency>
  <groupId>com.googlecode.soundlibs</groupId>
  <artifactId>jorbis</artifactId>
  <version>0.0.17-2-SNAPSHOT</version>
</dependency>
```