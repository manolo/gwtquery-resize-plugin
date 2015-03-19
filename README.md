

## Introduction
A gwtQuery plugin which adds `resize` event support to any DOM element.

It uses modern `animationFrame` or `timer` to check when any observed element changes its size.

It is inspired on:
http://www.backalleycoder.com/2013/03/18/cross-browser-event-based-element-resize-detection/
and
http://benalman.com/projects/jquery-resize-plugin/

## Demo

http://manolo.github.io/gwtquery-resize-demo/index.html

## Usage

1. You only have to drop the .jar file in your classpath, or add this dependency to your project:
   
   ```
        <dependency>
            <groupId>com.googlecode.gwtquery.plugins</groupId>
            <artifactId>resize-plugin</artifactId>
            <version>1.0-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
   ```
2. Then use it as any other gQuery plugin through the `as()` method
   ```
   // Bind resize event
   $(selector)
     .as(Resize)
     .on("resize", new Function() {
         public void f() {
           console.log("Element resized");
         }
     });

   // Unbind
   $(selector).off("resize");
   ```
## Browser compatibility

   Any GWT supported browser.
