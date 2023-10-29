# Creating a Data Merchant

In this tutorial, we'll walk through the creation of a new data merchant, and a correctly formatted perceptive podcast bundle to utilise it. This is intended to provide a guide for altaring and extending the existing collection of [Data Merchants](data-merchants.md) to incorporate new or different sensors to those that were initially prioritised.

If you are looking only for further advice on creating a perceptive podcast bundle, we recommend you skip to the description in Step 5, and use one of the [existing data merchants](data-merchants.md) in lieu of the one created here.

## 1. Clone or fork

We highly encourage you to fork the Adaptive Podcasting repo and add to or adapt the project with your own ideas.

## 2. Create and register a new DataMerchant class

In this tutorial, we will be implementing a very simple random number-generating data merchant.

Open the project in Android Studio, navigate to the `app/src/main/java/uk/co/bbc/perceptivepodcasts/merchant` folder.

Create a new file named `RandomNumberMerchant.kt` which will contain our `RandomNumberMerchant` class:

```java
class RandomNumberMerchant(private val context: Context) : DataMerchant {
    override fun getData(param: String?, onComplete: (String?) -> Unit) {
        invokeOnCheckerThread {
          val random = Random()
          val result = String.valueOf(random.nextInt())

          onComplete(result)
        }
    }
}
```

The `getData` function simply returns a random number, as a `String`.

Now we add this to `Merchants.kt`:

```java
class Merchants(context: Context) {
    // etc.
    val randomNumberMerchant = RandomNumberMerchant()
}
```

and `PlayerBuilder.kt`:

```java

object PlayerBuilder {
  // etc.
  private const val MERCHANT_ID_RANDOM_NUMBER = "RANDOM_NUMBER"

  fun build(context: Context, mainScope: CoroutineScope): PerceptivePlayer {
    // etc.
    perceptivePlayer.addDataMerchant(MERCHANT_ID_RANDOM_NUMBER, merchants.randomNumberMerchant)

    return perceptivePlayer
  }
}
```

Add your new data merchant to the end of the `Merchants` class, as shown above.

## 3. Implementing Actions

Now, we want to allow the writer of the SMIL file to choose between a number of "actions". For most cases, these might be different treatments of the data being returned - coarse vs. fine detail, for example - or as a way of tidily grouping together access to many different forms of related data - first, last and full name in a single user information data merchant, for example.

For our `RandomNumberMerchant` class, we want to allow users to specify a maximum number for the return value. To achieve this, we can check whether the parameter passed to the `getData` function contains a value:

```java
class RandomNumberMerchant(private val context: Context) : DataMerchant {
    override fun getData(param: String?, onComplete: (String?) -> Unit) {
        invokeOnCheckerThread {
          val random = Random()
          val max = Integer.MAX_VALUE

          if (param != null) {
            max = Integer.parseInt(param);
          }

          val result = String.valueOf(random.nextInt(max))

          onComplete(result)
        }
    }
}
```

## 4. Creating an Adaptive Podcast

Create a `manifest.json` file. As discussed in depth [here](manifest.md), the manifest file describes your adaptive podcast metadata, such as credits. We'll keep ours fairly simple:

```json
{
    "title":"Number Station",
    "series":1,
    "episode":1,
    "imageryURIs":[
        {"large":"IMAGES/dice_large.jpg"},
        {"medium":"IMAGES/dice_medium.jpg"},
        {"small":"IMAGES/dice_small.jpg"}
    ],
    "creditGroups":[
  	{"name":"Author",
  		"credits": [
    	{"name":"Joe Bloggs", "role":null}
    ]}
  ]
}
```

Create a `smil.xml` file. There is a comprehensive overview of the SMIL tags currently utilised by Perceptive Podcasts [here](smil.md), but for now we only need a very simple text-to-speech layer around a usage of our data merchant.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<smil>
  <body>
      <audio type="tts">
        <dynscript>
          <dynquery merchantId="RANDOM_NUMBER" relatedAction="100"/>
        </dynscript>
      </audio>
  </body>
</smil>
```

Other than these two files, you have free reign to structure your adaptive podcast bundle however you wish. In our case, we will create an `IMAGES` folder, and provide some cover art representing the function of the podcast, for each of the sizes we specified in the manifest above.

In our example, we don't have any need to provide custom audio, as our SMIL file utilises TTS audio only. However, it is also common to provide recorded audio in a separate `AUDIO` folder.

Once you've finished your content, use an archive tool to zip the content files together. Upload it, and include it as the enclosure in a new entry in your RSS feed. Enclosures can be described as either a ZIP file, which must be a podcast containing assets, SMIL and manifest, or it can be a link to a media asset file (MP3, WAV, OGG).
See examples of both below:

```xml
<enclosure
  url="https://s3-eu-west-1.amazonaws.com/connectedstudio-perceptivepod/StarterDemo.zip"
  length="7286839"
  type="application/zip" />
```

```xml
<enclosure
  url="https://s3-eu-west-1.amazonaws.com/connectedstudio-perceptivepod/560515__klankbeeld__edge-forest-706am-210221-0257.wav"
  length="11023646"
  type="audio/wav" />
```

**Note:** Take care when zipping the content not to alter the file hierarchy. Zipping the folder containing your perceptive podcast, for example, will likely result in an error when the application attempts to parse it. Your `manifest.json` must be accessible at the top level of your bundled zip file.

## 5. You're done!

You've implemented and tested your data merchant! Add it to the documentation in [`docs/data-merchants.md`](data-merchants.md) and share it with the world.
