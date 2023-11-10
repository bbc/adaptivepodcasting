# A brief history of perceptive/adaptive podcasting

![Perceptive Radio](https://www.bbc.co.uk/rd/images/dynamic/W1siZmYiLCJwdWJsaWMvcmQvc2l0ZXMvNTAzMzVmZjM3MGI1YzI2MmFmMDAwMDA0L2NvbnRlbnRfZW50cnk1MDMzYjM4YTcwYjVjMjcyYjAwMDAwMjIvNTlkM2I4YzAwNmQ2M2U0NjNhMDA0NzdmL2ZpbGVzLzE1MTcyMTk5MTY5XzEzMjdmYTE4Y2Ffby5qcGciXSxbInAiLCJ0aHVtYiIsIjEyNDh4NzAyIyJdXQ/15172199169_1327fa18ca_o.jpg?sha=a52a6006daeed92f)

Established in 2011 following the public research into a branch of object based media, called [Perceptive media]() and the first [public prototype breaking out](http://futurebroadcasts.com/).([code base](https://github.com/happyworm/PerceptiveMedia)).

BBC R&D designed and created the first [Perceptive Radio](https://www.bbc.co.uk/rd/projects/perceptive-radio) with Mudlark, MCQN, Deferred Procrastination from across the North of England in [2013](https://www.bbc.co.uk/rd/blog/2013-05-collaborative-working-on-perceptive-radio).
Following this a second Perceptive Radio with Lancaster University, Glasgow University and Mudlark in [2016](https://www.research.lancs.ac.uk/portal/en/publications/perceptive-media(ca8f7144-86d4-4b74-a36a-a5473343b395).html).

## 2016

Following this a second Perceptive Radio with Lancaster University, Glasgow University and Mudlark in [2016](https://www.research.lancs.ac.uk/portal/en/publications/perceptive-media(ca8f7144-86d4-4b74-a36a-a5473343b395).html).

Smartphone capability had grown to an extent to which a mobile app could reproduce most of the fuctionality of the Perceptive Radio and could provide a framework for creating adaptive audio experiences for everyone.

## 2017

Chris Bergin worked on the very first Android player.
Ben Newcombe internally added the data merchant system.
Rebecca Saw developed a number of additional merchants and  stabilizing the player.

## 2018

Rebecca Saw independantly started the web editor, which became the base of the currently editor. Proving access for non-developers. 

## 2019

Trunk's Nathan Broadbent and Alex Stone added Vibrate, SSML, Looping and other features.
Rebecca Saw's editor is picked up and further developed adding a basic set of features. Giving non-developers a easy way to get started and understand the perceptive podcasting project.
 

## 2021
In Aug 2021, Adaptive podcasting recieved support from the [European Broadcasting Union](https://www.ebu.ch/) [(EBU)'s media innovation fund](https://www.ebu.ch/media/media-innovation-fund), providing the whole code base to EBU members 12 months ahead of the public open source release.
The University of York start work on a branch of the codebase addomh accessibility features to aid with their [research](https://www.aes.org/e-lib/browse.cfm?elib=19742) into [narrative importance and time squeeze](https://github.com/bbc/adaptivepodcasting/blob/main/docs/writing-a-podcast-using-code.md#extensions-and-namespaces)

## 2022
The first edition of the Adaptive podcasting player is made available in the [Google play store](https://play.google.com/store/apps/details?id=uk.co.bbc.perceptivepodcasts). The web editor is made available under the [BBC's Makerbox project](https://www.bbc.co.uk/makerbox/tools/adaptive-podcasting).

## 2023
University of York's and BBC R&D's codebase are merged and made open source under the Apache licence. A [video](https://www.youtube.com/watch?v=F5ZvlezILOw) is produced for the release and made available under a CC licence.


## 2018


## 2020
The project name Perceptive podcasting is changed to a more descriptive Adaptive podcasting. A [video](https://www.youtube.com/watch?v=zTAryDY3YTQ) is produced during the pandemic.

## 2021
In Aug 2021, Adaptive podcasting recieved support from the [European Broadcasting Union](https://www.ebu.ch/) [(EBU)'s media innovation fund](https://www.ebu.ch/media/media-innovation-fund), providing the whole code base to EBU members 12 months ahead of the public open source release. James Shephard and Anthony Onumonu started work internally to update the code base to Kotlin instead of Android java code. 

## 2022
The first edition of the Adaptive podcasting player is made available in the [Google play store](https://play.google.com/store/apps/details?id=uk.co.bbc.perceptivepodcasts). The web editor is made available under the [BBC's Makerbox project](https://www.bbc.co.uk/makerbox/tools/adaptive-podcasting). This is marked with a [R&D blog post](https://www.bbc.co.uk/rd/blog/2022-09-adaptive-podcasting).

## 2023
In October 2023, the code bases for [Adaptive podcasting were combined and made public](https://github.com/bbc/adaptivepodcasting) under an Apache 2 licence along with other [related research](https://www.bbc.co.uk/rd/publications/adaptive-podcasting-open-source-release). 

## 2024

BBC R&D's support for the project has formally finished, we have provided the code base to the community and industry to take in different ways.

There is a number of feature requests in the [GitHub issues](https://github.com/bbc/adaptivepodcasting/issues), which have been raised from the community of practice over the last 5 years.

Most of them exist around what SMIL already supports for example filters, advance time muliplation, accessing streaming sources, a fallback system providing graceful degradation. 

There is also a need to operate closer to the new podcasting ecosystem using the podcast 2.0 namespace.

The big question exists around a iOS player, as a large number of people use iOS for podcasts. We have always had the question and ultimately rather than create a iOS player, Android player, etc player. WASM/webassembly has matured over the last 6 years and it makes sense for future development of the player.