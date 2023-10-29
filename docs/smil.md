# SMIL for adaptive podcasting

SMIL Lite is a simplified implementation of the [W3C SMIL (Synchronized Multimedia Integration Language) specification.
](https://www.w3.org/TR/smil/ "Synchronized Multimedia Integration Language &#40;SMIL 3.0&#41;")

It is the language we use to script perceptive media narratives and coordinate playback of separate audio and TTS speech objects which can change depending on arbitrary injected data.

## `<head>` Tags

### Custom Tests

Custom tests are a means of making editorial decisions dynamically as a result of data returned from Data Merchants (arbitrary sources of data on the user's device).

Custom tests retrieve data from any specified merchant and compare it with a list of accepted values. If the data returned from the merchant matches any one of the accepted values, the test will pass and the element inside any switch tags which links to the custom test will be marked for playback.

#### Example

Custom tests are defined inside custom attribute tags inside:

```xml
<customAttributes>
  <customTest>
  </customTest>
</customAttrtibutes>
```

We then specify an id with which we can refer to the test, the data merchant we want to retrieve data from, and an action we want that merchant to perform as well as some values we want to match:

```xml
<customAttributes>
  <customTest id="theNorth" relatedMerchant="USER_LOCATION" relatedAction="CITY">
    <acceptedValue>Manchester</acceptedValue>
    <acceptedValue>Newcastle</acceptedValue>
    <acceptedValue>Liverpool</acceptedValue>
  </customTest>
</customAttrtibutes>
```

The above will acquire the city the user is in - from the USER_LOCATION data merchant (assuming one is registered) with the city action - and compare it with the list of values in the `acceptedValue` tags (Manchester, Newcastle, Liverpool). If the user is in anyone of those cities, the test will pass.

Now if an element inside a `switch` tag has a `customTest` attribute with "theNorth" as a value. It will be the one selected to play.

Some data merchants can return "null". This should be considered when integrating them into content, in order to avoid surfacing this in content. Implementing it as a Custom Test would allow you to skip one or several lines entirely to account for this case.

## `<body>` Tags

### Switches

Switches are used in conjunction with custom tests to define a single child item which should play in the event of a passed, referenced test.

#### Example

```xml
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

```xml
<switch>
  <audio customTest="england" sec="audio/englishAnthem.mp3"/>
  <audio customTest="italy" sec="audio/italianAnthem.mp3"/>
  <audio customTest="usa" sec="audio/usaAnthem.mp3"/>
</switch>
```

The above would play the anthem of either England, Italy or the USA depending on which one of those countries the user currently resides.

### Sequences

Elements defined within `<seq></seq>` tags will be played, as the name suggests, in sequence - one after the other.

Valid attributes:

 - `inTime` - Specifies a time delay before this element will begin to play.. This delay is denoted in Seconds. Example: `<seq inTime="0.25sec">` will delay the playback for 1/4 of a Second.
 - `customTest` - Specifies which `<customTest>`, by `id` should, if passed, trigger this element for playback. **This is only a valid attribute when the element is inside `<switch></switch>` tags.**

### Parallels

Elements defined within `<par></par>` tags will be played, as the name suggests, in parallel, i.e.,  all at the same time.

Valid attributes:

 - `inTime` - Specifies a time delay before this element will begin to play.

### Parallels

Elements defined within `<par></par>` tags will be played, as the name suggests, in parallel, i.e., all at the same time.

Valid attributes:

 - `inTime` - Specifies a time delay before this element will begin to play.. This delay is denoted in Seconds. Example: `<seq inTime="0.25sec">` will delay the playback for 1/4 of a Second.
 - `customTest` - Specifies which `<customTest>`, by `id` should, if passed, trigger this element for playback. **This is only a valid attribute when the element is inside `<switch></switch>` tags.**

 - `Audio delay' - This delay is denoted in Seconds. Example: `<seq inTime="0.25sec">` will delay the playback for 1/4 of a Second.
 - `customTest` - Specifies which `<customTest>`, by `id` should, if passed, trigger this element for playback. **This is only a valid attribute when the element is inside `<switch></switch>` tags.**

### Audio

`<audio></audio>` Defines a specific audio item to be played.

### Vibrate

`<vibrate></vibrate>` Defines a vibration. Define duration of the vibrate like by using the `duration` attribute. `<vibrate duration="2.0">` to vibrate for 2 seconds.

Valid attributes:

- `volLeft` Specifies the volume of the Left channel of this element. This is denoted as a float between 0 & 1, where 1 is max volume. This value is accurate to 3 decimal places.
- `volRight` Specifies the volume of the Right channel of this element. This is denoted as a float between 0 & 1, where 1 is max volume. This value is accurate to 3 decimal places.
- `src` Specifies the path of the audio file to be played. This path is relative to the location of the `smil.xml` file.
- `type` The only valid value for this is ***tts*** which specifies that the element is a text-to-speech element and any text inside child `<dynscript><text></text></dynscript>` tags will be synthesized to an audio file upon playback. This means the `src` attribute is invalid when this attribute is set.
- `inTime` - Specifies a time delay before this element will begin to play.. This delay is denoted in Seconds. Example: `<seq inTime="0.25sec">` will delay the playback for 1/4 of a Second.
- `speed` - Specifies the speed of play. Enter a value between 0 <-> 1.  1 = normal play speed.
- `loop` - Specifies whether the audio should loop or not. Example: `<audio src="audio/example.mp3" loop="true"></audio>`. Note: The loop will continue for the duration specific by the `duration` attribute.
- `clipBegin` Specifies how far into the audio file defined in `src` the element should begin playback. This is denoted in the format `hh:mm:ss`.
Example:  `<audio src"audio/example.mp3" clipBegin"00:20"></audio>` will play example.mp3 from 20 seconds in.
- `clipEnd` Specifies how far into the audio file defined in `src` the element should end playback. This is denoted in the format `hh:mm:ss`.
Example:  `<audio src"audio/example.mp3" clipEnd"00:01:00"></audio>` will play example.mp3 from the start until 1 minute has elapsed.
- `duration` Specifies how long the audio file defined in `src` should play for (or action such as Vibrate). This is denoted in the format `hh:mm:ss`. ***This will overrule `clipEnd` if it is defined.***
- `customTest` - Specifies which `<customTest>`, by `id` should, if passed, trigger this element for playback. **This is only a valid attribute when the element is inside `<switch></switch>` tags.**

**`src`, `inTime`, `clipBegin`, `clipEnd` and `duration` are all invalid when type is TTS**

The `<audio>` tag can also support SSML tags to change how text is read out. Note: At the time of writing this Android TTS only supports a subset of the SSML spec.

Note: If you need to add additional attributes to a tag, add it to the getAttributes() function inside SmilLiteParser.java. Example: `typeParams.put("newAttribute", parser.getAttributeValue(null, "newAttribute"));`

SSML tags are only parsed if wrapped a `<speak>` tag. For example:

```xml
<audio type="tts">
  <speak>Let me speak slower... <prosody rate="slow" pitch="-2st">Can you hear me now?</prosody></speak>
</audio>
```

SSML tags which have been tested are:

- `<prosody rate="slow" pitch="-2st">Say this slower</prosody>` Control TTS speed and pitch.
- `<break time="2s"/>` Pause TTS.
- `<emphasis level="strong"> big </emphasis>` Emphasise a word or words.
- `<say-as interpret-as="expletive">naughty word</say-as>` Bleep out a word.
- `<say-as interpret-as="spell-out">hello</say-as>` Spell out a word.
- `<say-as interpret-as="ordinal">1</say-as>` Describe a number.
- `<phoneme alphabet="xsampa" ph="&#34;i:z"/>` Phonetic pronunciation (only supports xsampa alphabet).

More details on SSML tags and supported attributes can be found here: [Speech Synthesis Markup Language (SSML)](https://www.w3.org/TR/speech-synthesis11/ "Speech Synthesis Markup Language (SSML)")

### Dynamic Script

This system provides the means to dynamically inject data from Data Merchants into TTS (text-to-speech) text.

Dynamic script is comprised of three tags:

 - `<dynScript></dynScript>` - the parent container tag.
 - `<text></text>` - specifies static, predetermined text. Note: Dynamic script TTS does not support SSML tags due to how this content needs to be parsed by the player.
 - `<dynQuery/>` - short for "dynamic query", this tag will be replaced with the result of a specified data merchant and its associated action. These are defined with the `merchantID` and `action` attributes, respectively.

**`<dynScript></dynScript>` tags are only valid inside `<audio></audio>` tags with a `type` attribute of "tts"**

#### Example:

```xml
<audio type="tts">
  <text>Hello, your name is: </text>
  <dynquery merchantId="USER_NAME" action="FULL_NAME"/>
  <text>. How is your day?</text>
</audio>
```

The above will result in: **"Hello, your name is [user's full name]. How is your day?"** being rendered into a TTS audio file.
