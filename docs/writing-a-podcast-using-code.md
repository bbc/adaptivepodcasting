# Creating a podcast using SMIL code

![Adaptive Podcasts unzipped](https://user-images.githubusercontent.com/1649922/188642690-0e9863b5-b7b3-4fdf-b5d7-972255ebc8a5.png)

The web editor allows you to get you going quickly with adaptive podcasting, but doesn’t include everything the adaptive podcasting player/app can do. The editor is made for beginners to intermediate creators. If you really want to unlock the full possibilities of adaptive podcasting, coding the podcast is the way forward.

Writing code can be difficult but the language used is similar to writing web pages and has a logical structure. We used an existing XML language schema called SMIL to take advantage of existing knowledge, process and resources. You can read a lot more about [SMIL at the W3C](https://www.w3.org/TR/SMIL/). We have not implemented most of the SMIL specification but hope others are inspired to add more. You are welcome to [contribute](../README.md#contributing).

Likewise there are many ways to write the SMIL XML and expect converters can be written by the community.

## Building on the editor

We have made transitioning from the editor to code easy by exposing the SMIL code to the creator, which allows you to make changes to the code directly.

Unzipping the podcast/zip file will reveal all the files including the smil.xml, manifest.json and editorFile.json.

![The unzipped podcast zip file](https://user-images.githubusercontent.com/1649922/188482272-1efc7871-80f7-4fb7-a00c-08e5e725082a.png)

### The AUDIO folder

This is where all the audio files are stored.
The player/app will accept mp3, ogg, wav, aac, flac, opus and aiff of almost any size within the practicality of the device which is playing it back. Be aware that older devices may struggle with higher quality media.

It is recommended to consider file sizes of audio files, as no one wants to download a 2 gigabyte podcast over a slow metered 3G connection. The audio files will be the largest part of the zip/podcast file.

### The IMAGES folder

This is where the cover art is stored.
The image should be provided in three different square sizes:

* Large (anything over 500px squared)
* Medium (500px squared)
* Small (250px squared)

The web editor will automatically scale the uploaded image and make it a square ratio.

### The web development file - editorFile.json

This file is best left alone, as it's only used for the web editor, not the player/app. Modification of this file can break the zip file when opening the podcast in the web editor again.

It is not required for the player/app and we recommend deleting it when publishing the podcast or sharing with people not planning on opening it in the web editor.

### Manifest (manifest.json)

The manifest JSON file is a metadata file that describes the podcast for the purposes of the player.

For example the JSON for Adaptive Summer Mindcast looks like this:

```json
{
  "title":"Summer Mindcast: an adaptive experience",
  "series":1,
  "episode":1,
  "imageryURIs":[
    {"large":"IMAGES/large.jpg"},
    {"medium":"IMAGES/medium.jpg"},
    {"small":"IMAGES/small.jpg"}
  ],
  "creditGroups":[
    {
      "name":"Permissions used",
      "credits": [
        {
          "name":"Proximity", "role":"Sensor"
        },
        {
          "name":"Contacts", "role":"Data"
        },
        {
          "name":"Time", "role":"Data"
        },
        {
          "name":"Date", "role":"Data"
        }
      ]
    },
    {
      "name":"Credits",
      "credits": [
        {
          "name": "Code developer - Ian Forrester"
        },
        {
          "name": "Sound producer - Catherine Robinson"
        },
        {
          "name": "Script writer - Sarah Glenister"
        },
        {
          "name": "Female actor - Jasmine Hyde"
        },
        {
          "name": "Male actor - Tayla Kovacevic-Ebong"
        },
        {
          "name": "Production co-ordinator - Eleri Sydney McAuliffe"
        },
        {
          "name": "Project manager - Rebecca Stagg"
        }
      ]
    }
  ]
}
```

The results can be seen clearly once the attributes are opened on the player app.

![The metadata from the JSON as seen in the player app](https://user-images.githubusercontent.com/1649922/188482590-05807b58-daf0-4d78-89f9-a167c5f40c87.jpg)


All the JSON file attributes are all optional but recommended, otherwise you will end up with a blank podcast. The JSON file must be valid or the player will not open the podcast, [online services do exist](https://www.freeformatter.com/json-validator.html) but most code editors will do the check for you.


We recommend including the Permissions used at the top of the JSON file to help the listener to understand which permissions are used and, if you like, why they are used.


Next we recommend Credits, to correctly attribute all involved in the process of the podcast. How you choose to display this is up to you. Long lines are not recommended.

Note: the podcast description comes from the RSS feed, not the JSON file.

# Podcast/SMIL syntax (smil.xml)

SMIL Lite is a simplified implementation of the [W3C SMIL (Synchronized Multimedia Integration Language) specification.](https://www.w3.org/TR/smil/)

It is the language we use to script perceptive media narratives and coordinate playback of separate audio and TTS speech objects which can change depending on arbitrary injected data.

## ```<head>``` Element
![The head element in XML code](https://user-images.githubusercontent.com/1649922/188483055-4f1838a2-eaed-4b12-882b-7440ce72dea6.png)


Custom Tests
Custom tests are a means of making editorial decisions dynamically as a result of data returned from Data Merchants (arbitrary sources of data on the user's device).

Custom tests retrieve data from any specified merchant and compare it with a list of accepted values. If the data returned from the merchant matches any one of the accepted values, the test will pass and the element inside any switch elements which links to the custom test will be marked for playback.

Example
Custom tests are defined inside custom attribute elements inside:

```
<customAttributes>

   <customTest>

   </customTest>

</customAttrtibutes>
```

We then specify an id with which we can refer to the test, the data merchant we want to retrieve data from, and an action we want that merchant to perform as well as some values we want to match:

```
<customAttributes>

   <customTest id="theNorth" relatedMerchant="USER_LOCATION" relatedAction="CITY">

      <acceptedValue>Manchester</acceptedValue>

      <acceptedValue>Newcastle</acceptedValue>

      <acceptedValue>Liverpool</acceptedValue>

   </customTest>

</customAttrtibutes>
```

The above will acquire the city the user is in - from the USER_LOCATION data merchant (assuming one is registered) with the city action - and compare it with the list of values in the acceptedValue elements (Manchester, Newcastle, Liverpool). If the user is in any one of those cities, the test will pass.

Now if an element inside a switch element has a customTest attribute with "theNorth" as a value. It will be the one selected to play.

Some data merchants can return "null". This should be considered when integrating them into content, in order to avoid surfacing this in content. Implementing it as a Custom Test would allow you to skip one or several lines entirely to account for this case.

## ```<body>``` Element

![The body element in XML code](https://user-images.githubusercontent.com/1649922/188483316-23c2b07c-44d8-428c-8279-2f8373d0d98f.png)


### Sequences
Elements defined within <seq></seq> elements will be played, as the name suggests, in sequence - one after the other.

Valid Attributes
inTime - Specifies a time delay before this element will begin to play.. This delay is denoted in Seconds. Example: ```<seq inTime="0.25sec">``` will delay the playback for 1/4 of a Second.

customTest - Specifies which ```<customTest>```, by id should, if passed, trigger this element for playback. This is only a valid attribute when the element is inside ```<switch></switch>``` elements.

### Parallels
Elements defined within <par></par> elements will be played, as the name suggests, in parallel - all at the same time.
Valid Attributes
inTime - Specifies a time delay before this element will begin to play.. This delay is denoted in Seconds. Example: ```<seq inTime="0.25sec">``` will delay the playback for 1/4 of a Second.
customTest - Specifies which ```<customTest>```, by id should, if passed, trigger this element for playback. This is only a valid attribute when the element is inside ```<switch></switch>``` elements.

### Switches
Switches are used in conjunction with custom tests to define a single child item which should play in the event of a passed, referenced test.
Example
```
<customAttributes>
   <customTest id="england" relatedMerchant="USER_LOCATION" relatedAction="COUNTRY">
      <acceptedValue>England</acceptedValue>
   </customTest>
   <customTest id="italy" relatedMerchant="USER_LOCATION" relatedAction="COUNTRY">
      <acceptedValue>Italy</acceptedValue>
   </customTest>
   <customTest id="usa" relatedMerchant="USER_LOCATION" relatedAction="COUNTRY">
      <acceptedValue>USA</acceptedValue>
   </customTest>
</customAttrtibutes>
```
```
<switch>
  <audio customTest="england" sec="audio/englishAnthem.mp3"/>
  <audio customTest="italy" sec="audio/italianAnthem.mp3"/>
  <audio customTest="usa" sec="audio/usaAnthem.mp3"/>
</switch>
```

The above would play the anthem of either England, Italy or the USA depending on which one of those countries the user currently resides.

You can create switches inside of switches but you must use
```
<customTest id="else" relatedMerchant="ELSE" relatedAction="">
        <acceptedValue>else</acceptedValue>
      </customTest>
```

In the custom attributes and…
```
<seq customTest="else"/>
```
In the switch elements.


### Audio
```<audio></audio>``` Defines a specific audio item to be played.

Valid Attributes
volLeft Specifies the volume of the Left channel of this element. This is denoted as a float between 0 & 1, where 1 is max volume. This value is accurate to 3 decimal places.

volRight Specifies the volume of the Right channel of this element. This is denoted as a float between 0 & 1, where 1 is max volume. This value is accurate to 3 decimal places.

src Specifies the path of the audio file to be played. This path is relative to the location of the smil.xml file.
type The only valid value for this is tts which specifies that the element is a text-to-speech element and any text inside child

```<dynscript><text></text></dynscript>``` elements will be synthesized to an audio file upon playback. This means the src attribute is invalid when this attribute is set.

inTime - Specifies a time delay before this element will begin to play.. This delay is denoted in Seconds. Example: ```<seq inTime="0.25sec">``` will delay the playback for 1/4 of a Second.

clipBegin Specifies how far into the audio file defined in src the element should begin playback. This is denoted in the format hh:mm:ss.

Example:
```<audio src"audio/example.mp3" clipBegin"00:20"></audio>``` will play example.mp3 from 20 seconds in.
clipEnd Specifies how far into the audio file defined in src the element should end playback. This is denoted in the format hh:mm:ss. Example: ```<audio src"audio/example.mp3" clipEnd"00:01:00"></audio>``` will play example.mp3 from the start until 1 minute has elapsed.
duration Specifies how long the audio file defined in src should play for. This is denoted in the format hh:mm:ss. This will overrule clipEnd if it is defined.

customTest - Specifies which ```<customTest>```, by id should, if passed, trigger this element for playback. This is only a valid attribute when the element is inside ```<switch></switch>``` elements.src, inTime, clipBegin, clipEnd and duration are all invalid when type is TTS

speed - Specifies the speed of play. Enter a value between 0 <-> 1.0.
1 = normal play speed.

loop - Specifies whether the audio should loop or not.
Example: ```<audio src="audio/example.mp3" loop="true" duration="00:10"></audio>```.
Note: The loop will continue for the duration specified by the duration attribute.
Note: Loop is experimental and creates a slight 10ms delay when used.


### Dynamic Script
This system provides the means to dynamically inject data from Data Merchants into TTS (text-to-speech) text.
Dynamic script is comprised of three elements:
```<dynScript></dynScript>``` Which is the parent container element.
```<text></text>``` Which specify static, predetermined text
```<dynQuery/>``` Short for "dynamic query", this element will be replaced with the result of a specified data merchant and its associated action. These are defined with the merchantID and action attributes, respectively.
```<dynScript></dynScript>``` elements are only valid inside ```<audio></audio>``` elements with a type attribute of "tts"

Example:
```
<audio type="tts>
  <text>Hello, your name is: </text>
  <dynquery merchantId="USER_NAME" action="FULL_NAME"/>
  <text>. How is your day?</text>
</audio>
```
The above will result in: "Hello, your name is [user's full name]. How is your day?" being rendered into a TTS audio file.

### Speech Synthesis Markup Language (SSML)

[Speech Synthesis Markup Language (SSML)](https://cloud.google.com/text-to-speech/docs/ssml) allows the Text to speech to be more expressive. It can not be used in a dynamic script.

```<prosody rate="slow" pitch="-2st">Say this slower</prosody>``` Control TTS speed and pitch.
```<break time="2s"/>``` Pause TTS.
```<emphasis level="strong"> big </emphasis>``` Emphasise a word or words.
```<say-as interpret-as="expletive">naughty word</say-as>``` Bleep out a word.
```<say-as interpret-as="spell-out">hello</say-as>``` Spell out a word.
```<say-as interpret-as="ordinal">1</say-as>``` Describe a number.
```<phoneme alphabet="xsampa" ph="&#34;i:z"/>``` Phonetic pronunciation (only supports xsampa alphabet).


### Vibrate
Will vibrate the phone for a set amount of time, please use carefully and sparingly!
```<Vibrate></Vibrate>``` Defines a vibration. Define duration of the vibrate like by using the duration attribute. ```<vibrate duration="2.0">``` to vibrate for 2 seconds.


## Custom Attributes/Data Merchants

These are data sources which are referenceable in the SMIL code using the adaptive podcasting data merchant system. It is possible to create your own Data Merchant to extend the sensors and functionality available to SMIL files.
Once the code base is open source, details will be available.

### Else
relatedMerchant= "ELSE"

Will always return ‘else’

### Proximity
relatedMerchant= "PROXIMITY"

Returns
* near
* far



### Contacts
relatedMerchant= "USER_CONTACTS"

Returns integer of contacts

### Battery
relatedMerchant = "BATTERY"

relatedAction = “coarse”
* charging
* high
* medium
* low

relatedAction= “chargingMethod”
Returns
* USB charging
* mains charging
* wireless charging

relatedAction= “”
Returns battery percentage (eg 94%)

### Light Sensor
relatedMerchant= “LIGHT”

Returns
* dark
* light

### Device Language
relatedMerchant= “USER_LANGUAGE”

relatedAction = “language_name”
Returns language set on the device (eg English)

relatedAction = “language_code”
Returns language code on the device (eg en_gb)

### Date
relatedMerchant = “DATE”

relatedAction = “day”
Returns
* monday
* tuesday
* wednesday
* thursday
* friday
* saturday
* sunday

relatedAction = “month”
Returns
* january
* february
* march
* april
* may
* june
* july
* august
* september
* october
* november
* december

relatedAction = “season”
Returns
* spring
* summer
* autumn
* winter

relatedAction = “year”
Returns year (eg 2021)

relatedAction = “date”
Returns day of the month (eg 31)


### Time
relatedMerchant = “TIME”

relatedAction = “ofDay”
Returns
* morning
* afternoon
* evening
* night

relatedAction = “hours”
Returns hours (eg 21)

relatedAction = “minutes”
Returns minutes (eg 46)

relatedAction = “”
Returns time (eg 21 46)

### Location
Currently on some older devices another app has to have the location first, so this function only works if the user has opened said app (e.g. google maps) and let that have their location before opening the Adaptive Podcasting app

relatedMerchant = “USER_LOCATION”

relatedAction = “city”
Returns
City (eg Manchester)
null (if location is turned off or can’t be found)

relatedAction = “”
Returns
Country (eg UK)
null (if location is turned off or can’t be found)

### Headphones
relatedMerchant = “HEADPHONES”

Returns:
* headphones
* no headphones

### Device mode
relatedMerchant = “DEVICE_MODE”

Returns:
* Normal
* Vibrate
* Silent

### Media Volume
relatedMerchant = “MEDIA_VOLUME”

relatedAction = “coarse”
Returns:
* low
* medium
* high

relatedAction = “”
Returns volume percentage (eg 80 %)

## Extensions and namespaces

XML has support for [namespaces](https://www.w3.org/TR/xml-names/#ns-decl), which provides a clean way to extend the XML/SMIL code.
The [University of York Audiolab](https://audiolab.york.ac.uk/), have implemented accessibility features using this model. You can learn more about this work in [this BBC paper.](https://www.bbc.co.uk/rd/publications/research-395-object-based-audio-adaptive-personalisable-narrative-metadata-hearing) and [this one](https://www.aes.org/e-lib/browse.cfm?elib=19742).

### Narrative importance
[requires latest version of the player, not currently on the play store]

Narrative importance can be thought about like a explicit switch, which is set in the player slider which appears in the player.
Slider linearly moves between TVMix and Acc. [Accessible] Mix

It is defined by adding the attribute ni (narrative importance)

Accepted values:
* essential
* high
* medium
* low

Essential is dialogue and important effects
High is Music
Medium is Effects
Low is Ambiance (crickets etc.)

**Example of the code**
Note: only one will play

```
<par inTime="0.5">
<audio volLeft="0.5" volRight="0.5" ni="essential" src="AUDIO/TurningForest_NI_0_Essential.ogg"></audio>
<audio volLeft="0.5" volRight="0.5" ni="high" src="AUDIO/TurningForest_NI_1_High_Importance.ogg"></audio>
<audio volLeft="0.5" volRight="0.5" ni="medium" src="AUDIO/TurningForest_NI_2_Med_Imp.ogg"></audio>
<audio volLeft="0.5" volRight="0.5" ni="low" src="AUDIO/TurningForest_NI_3_Low_Importance.ogg"></audio>
</par>
```

### Time squeeze importance
[requires latest version of the player, not currently on the play store]

Time squeeze importance can be thought about like a explicit switch, which is set in the player preference setting
The setting allows - Full, Short, Highlights.

It is defined by adding the attribute tsi (time squeeze importance) and only works in seq and par elements
There is also an optional description attribute, it is good practice to add it for accessibility

Accepted values:
* essential
* high
* medium
* low

Essential is dialogue and important effects
High is Music
Medium is Effects
Low is Ambiance (crickets etc.)

**Example of the code**
Combines Narrative importance and Time squeeze importance

```
<seq>
<par tsi="medium" description="S1 Chat">
<audio ni="essential" src="AUDIO/S_S01Chat.mp3"></audio>
<audio ni="low" src="AUDIO/S_S01Chat_FX.mp3"></audio>
</par>
<par tsi="essential" description="S2 Neuroscience and early development for beginners">
<audio ni="essential" src="AUDIO/S2Neurosci.mp3"></audio>
<audio ni="low" volLeft="0.25" volRight="0.25" clipIn="18" src="AUDIO/M_4BarF.mp3"></audio>
</par>
<par tsi="low" description="Ident">
<audio inTime="1.5" ni="high" src="AUDIO/Ident.mp3"></audio>
<audio ni="low" volLeft="0.5" volRight="0.5" duration="6" loop="true" src="AUDIO/M_2bar48Kzc.mp3"></audio>
</par>
</seq>
```
