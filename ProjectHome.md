Project aims to create and publish Maven artifacts for sound processing libraries that are not deployed to Maven Central Repository by their original authors. Since these projects are stable for several years (let's not call them dead, shall we?), it is unlikely that they switch to Maven anytime soon.

Following artifacts are released including source and javadoc artifacts:

  * [Tritonus share](http://tritonus.org) - utility classes for sound processing
  * [JLayer](http://www.javazoom.net/javalayer/javalayer.html) - MP3 decoder and  encoder
  * [MP3SPI](http://www.javazoom.net/mp3spi/mp3spi.html) - javax.sound provider for MP3 decoding and encoding based on JLayer
  * [JOrbis](http://www.jcraft.com/jorbis/) - OGG Vorbis decoder and encoder
  * [VorbisSPI](http://www.javazoom.net/vorbisspi/vorbisspi.html) Service provider for decoding OGG Vorbis

## How to use ##

Add any of these dependencies to your project:

```
<dependency>
  <groupId>com.googlecode.soundlibs</groupId>
  <artifactId>tritonus-share</artifactId>
  <version>0.3.7-2</version>
</dependency>

<dependency>
  <groupId>com.googlecode.soundlibs</groupId>
  <artifactId>jlayer</artifactId>
  <version>1.0.1-1</version>
</dependency>

<dependency>
  <groupId>com.googlecode.soundlibs</groupId>
  <artifactId>mp3spi</artifactId>
  <version>1.9.5-1</version>
</dependency>

<dependency>
  <groupId>com.googlecode.soundlibs</groupId>
  <artifactId>jorbis</artifactId>
  <version>0.0.17-2</version>
</dependency>

<dependency>
  <groupId>com.googlecode.soundlibs</groupId>
  <artifactId>vorbisspi</artifactId>
  <version>1.0.3-1</version>
</dependency>
```