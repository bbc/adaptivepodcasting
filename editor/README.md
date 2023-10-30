# Adaptive Podcasting web editor

Adaptive podcasting is a BBC Research & Development project exploring the possibilities of adaptive audio.

![Adaptive podcast editor Screenshot](https://user-images.githubusercontent.com/1649922/188465308-888a1a86-1984-4c32-8dd8-4089b516101b.png)



## Installing the self contained web editor for the [Adaptive podcasting app](../docs/editor.md)

To demonstrate the self contained web editor, you can see and use the copy [we made on Glitch](https://adaptive-podcasting-web-editor.glitch.me/).
This is a duplicate of the one running on [BBC Makerbox](https://www.bbc.co.uk/makerbox/tools/adaptive-podcasting).
You are able to remix it on Glitch too, if installing it locally is difficult or problematic.

## Dependencies

If you want to run this in a production environment, we recommend using Apache or Nginx.

Use a modern browser such as Firefox, Safari, Chrome, or Edge with the capability to run JavaScript.

## Installation

Download the Adaptive Podcasting repo as a zip file.

```bash
wget https://github.com/bbc/adaptivepodcasting/archive/refs/heads/main.zip
```

Extract the zip file into a temporary location:

```bash
unzip adaptivepodcasting-main.zip
```

If you're using a web server such as Apache or Nginx, copy the files to your web server's public HTML folder.

An easy way to try out the editor is to use a web server such as [serve](https://www.npmjs.com/package/serve) with Node.js.

```bash
npm install --global serve
```

Change to the editor folder and start the web server:

```bash
cd adaptivepodcasting-main/editor/app
serve
```

Open your web browser at http://localhost:8080.

## Contributors

* Ian Forrester - BBC R&D
* Rebecca Saw - External contractor
* Kamara Bennett - BBC R&D
