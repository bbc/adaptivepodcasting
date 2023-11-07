# Adaptive / Perceptive Podcasts

A BBC Research & Development project exploring the potential of adaptive media.

<video width="1280" height="720" src="https://github.com/bbc/adaptivepodcasting/assets/1649922/26be9059-b3d6-4e48-b2d3-14b931acc1b5"></video>
> Video: Created by Vicky Barlow / Voice over: Bronnie McCarthy / Licenced [CC-BY-SA](https://creativecommons.org/licenses/by-sa/4.0/deed.en)

> Music: [Sleepwalking by Airtone](http://ccmixter.org/files/airtone/65416)

Adaptive podcasting is an expandable podcasting platform with unique features and [potential for future growth](https://www.bbc.co.uk/rd/publications/adaptive-podcasting-open-source-release). The key features of the app are the use of different playback instructions depending on device and sensor data, and the capabilities of the audio player itself.

## Introduction

This repo is essentially a lightweight implementation of [W3C Synchronized Multimedia Integration Language](https://www.w3.org/TR/SMIL3/) (SMIL) in the context of smart devices, leaning on BBC R&D's [object based media research](https://www.bbc.co.uk/rd/object-based-media) and work on implicit interaction known as [perceptive media](https://www.bbc.co.uk/rd/blog/2012-07-what-is-perceptive-media).

This repo extends BBC R&D's [perceptive radio projects](https://www.bbc.co.uk/rd/projects/perceptive-radio) into the space of smart devices, keeping the focus on object based audio and adding the ability to create new types of audio experience with minimal experience.

There are four parts to the codebase, described below.

### [Player](player/README.md)

An Android application which runs on Android 10 or later, built using Kotlin.

The Player has the ability to schedule media playback and make live editorial decisions based on input from external data sources or sensors.
It is an early implementation of the perceptive media approach to media, applied to podcasting.

### [Editor](editor/README.md)

A simple self contained web based editor for adaptive podcast content, using HTML, CSS and JavaScript. The editor can be run without any server-side dependances. It is meant as a quick and easy way to start creating adaptive podcasts with no coding experience.

### [Specification / Schema](specification/README.md)

The specification is the start of an XML schema to describe the SMIL code.

### [Documentation](docs/README.md)

Documentation for writing the SMIL code directly, working with the editor, understanding the player and its underlying Android Kotlin code.

## License

All components including the player, editor, and specification are licenced under the Apache 2.0 license. See the LICENSE file in each package directory for the specific licensing terms and copyright information.

You may [contact BBC R&D](https://www.bbc.co.uk/rd/contacts) to discuss alternative licensing options. (Please note, the BBC is under no obligation to offer alternative terms.)

## Contributing

Please contact the authors by raising a [GitHub issue](issues/new) and please follow up with a [GitHub pull request](https://github.com/bbc/adaptivepodcasting/pulls).
When submitting the pull request, please accompany it with a description of the problem you are trying to solve and the issue number if available, that this pull request fixes.

## Policy

We follow the BBC privacy policy which you can [view here](https://www.bbc.co.uk/usingthebbc/privacy)

## Credits

Adaptive podcasting has a lot of people who deserve [credit](docs/credits.md) for its inception, advancement, building, and opensourcing.
