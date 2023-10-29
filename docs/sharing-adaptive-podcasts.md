# How to share adaptive podcasts with friends and the world

After creating your award winning adaptive podcast, it's natural to want to share it with others.
Before sharing the podcast, it's recommended to read through this advice.

## Sharing Zip/Podcast files

![Sharing adaptive podcasts directly](https://user-images.githubusercontent.com/1649922/188499831-4806c578-e691-4888-9c08-4f366a96e3d9.png)!

The zip files which are created in the editor can be shared via email, instant messaging, links, etc. Care should be taken when sharing due to the nature of compressed files as some services will restrict sharing of zip files due to the possibility of malicious use.

It's recommended to:

* Remove the editorFile.json from the zip file before sharing widely outside a community who would use the web editor.
* Update the manifest.json file to include permission information at the top
* Compress the audio files down to a lower bitrate
* Make sure the cover art is clear
* Rename the podcast/zip file to something short, clear and without spaces or special characters

You are responsible for what you share and to whom.

## RSS feeds

A better way to share your podcast creations is via an RSS feed.

The default feed on the player app/device is [https://s3-eu-west-1.amazonaws.com/connectedstudio-perceptivepod/PerceptivePodcasts.xml](https://s3-eu-west-1.amazonaws.com/connectedstudio-perceptivepod/PerceptivePodcasts.xml)

There are many tutorials on how to write an RSS feed and services which can manage one for you. Bear in mind many of the podcast specific ones may not support zip files, only audio files.

This can be changed per device using the Preference menu and modifying the RSS feed link.

![Sharing adaptive podcasts via RSS](https://user-images.githubusercontent.com/1649922/188499830-b41cf3da-f8d7-47df-aae4-a05cc47fe45b.png)!

New listeners will also need to modify the RSS feed in the player app to subscribe to your podcast.

Subscribing to RSS feeds is possible but not user friendly because it sits outside the scope of the project. Likewise the podcast namespace is not supported in the player app but there is nothing stopping you from adding the [podcast 2.0 namespace](https://github.com/Podcastindex-org/podcast-namespace/blob/main/docs/1.0.md#alternate-enclosure) to your RSS feed. Especially because it is possible to link directly to files which are not adaptive podcast zip files but instead flat media files.

This is useful for providing a traditional podcast alongside an adaptive podcast in the same RSS feed.

### Enclosure for an adaptive podcast

```xml
<item>
  <title>Tomorrow's World Electric Sheep (AP remix)</title>
  <guid isPermaLink="false">electricSheep</guid>
  <summary>Meet Mary. She's sentient and she's conscious. So is your phone...</summary>
  <description>Meet Mary. She's sentient and she's conscious. So is your phone...</description>
  <pubDate>Fri, 8 June 2018 13:50:00 +0000</pubDate>
  <author>BBC R&amp;D and Tomorrow's World</author>
  <enclosure url="https://connectedstudio-perceptivepod.s3-eu-west-1.amazonaws.com/tomorrowsWorldEp4.zip" length="58892164" type="application/zip"/>
</item>
```

### Enclosure for a traditional podcast

```xml
<item>
  <title>Tomorrow's World Electric Sheep (Original version)</title>
  <guid isPermaLink="false">electricSheepTX</guid>
  <summary>Meet Mary. She's sentient and she's conscious, just like us?</summary>
  <description>Demostrating the ability to playback audio files directly</description>
  <pubDate>Tue, 28 Nov 2017 12:00:00 +0000</pubDate>
  <author>BBC Tomorrow's World</author>
  <enclosure url="https://open.live.bbc.co.uk/mediaselector/6/redir/version/2.0/mediaset/audio-nondrm-download/proto/https/vpid/p05mfn6t.mp3" length="58933753" type="audio/mpeg"/>
</item>
```
