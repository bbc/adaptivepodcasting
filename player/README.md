# The Adaptive Podcasting player

A BBC Research & Development project exploring the possibilities with adaptive media.

[![Adaptive Podcasting](https://www.bbc.co.uk/rd/images/dynamic/W1siZmYiLCJwdWJsaWMvcmQvc2l0ZXMvNTAzMzVmZjM3MGI1YzI2MmFmMDAwMDA0L2NvbnRlbnRfZW50cnk1MDRlMTgxNTcwYjVjMjBhMGMwMDEzNDQvNTE5ZjNmNGNhY2ZiYWI0ZjRkMDU2ZDBkL2ZpbGVzL3BlcmNlcHRpdmVtZWRpYV9yYWRpb19zdHJhaWdodGVuXzY0MC5qcGciXSxbInAiLCJ0aHVtYiIsIjg5Nng1MDQjIl1d/perceptivemedia_radio_straighten_640.jpg?sha=7b2c609fe74a4971)](https://youtu.be/zTAryDY3YTQ "Adaptive Podcasting")

## Introduction

The Player app is essentially a lightweight implementation of a SMIL parser with the ability to schedule media playback and make on-the-fly editorial decisions based on input from external data sources.

You can install the Adaptive Podcasting app via the [Google Play Store](https://play.google.com/store/apps/details?id=uk.co.bbc.perceptivepodcasts), which was last updated Jan 2023. There is also [the updated Android player/app](https://github.com/bbc/adaptivepodcasting/blob/main/player/adaptive-podcasting-2023-09.apk) available to sideload if you require the latest version and cannot compile it yourself.

You will need to [compile the player code](https://developer.android.com/build) to build the [latest version with all the features](https://github.com/bbc/adaptivepodcasting/raw/main/player/adaptive-podcasting-2023-09.apk).

## Adaptive podcast player code

The core of this project are the Perceptive Player classes:

* PerceptivePlayer
* AudioItem
* TTSItem
* Playable
* Sequence
* Parallel
* SmilLiteParser
* DynScriptParser
* TTSPrerenderer
* DataMerchant

A path to a SMIL XML file is passed to the Perceptive Player. This file references all audio and text-to-speech resources, along with scheduling, switching and other information.

The app in this project downloads a list of podcast zip files from an RSS feed. Inside these zip files are a smil.xml file, a manifest.json file, and an AUDIO folder containing the audio resources.

## Documentation

Guides to help with writing the smil.xml and podcast manifest.json.

- [SMIL XML guide](../docs/smil.md)
- [Manifest JSON guide](../docs/manifest.md)

### Guides to help with the player code

- [Credentials guide](../docs/credentials.md)
- [Data merchants guide](../docs/data-merchants.md)
- [Creating a data merchant guide](../docs/creating-a-data-merchant.md)

## Contributors

* Ian Forrester - BBC R&D
* Chris Bergin - External contractor
* Ben Newcombe - BBC
* Rebecca Saw - External contractor
* Kamara Bennett - BBC R&D
* Nathan Broadbent - External contractor
* James Shephard - BBC R&D
* Anthony Onumonu - BBC R&D
* Ken Brown - University of York
