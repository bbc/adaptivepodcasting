# How to get started and how the adaptive podcast editor works

![Adaptive podcasting editor - default screen](https://user-images.githubusercontent.com/1649922/188456294-6220b6b9-b23b-4eaa-b437-ecf054c969c7.png)

The podcast web editor provides a drag and drop node interface. There are two main elements: the floating information box, which can be moved around, and the buttons on the left which stay fixed on the top left. The flow of the player is from left to right, with the leftmost element played first before anything else.

The web editor is meant as a way to get started with adaptive podcasting rather than a complete replacement for writing XML code. Because of this, not every single feature is included.

The web editor exists only in your browser, meaning there is no automatic save option. It's recommended to save regularly using the **Create File** option. You can open the created file again using the **Upload File** option. This decentralised way means everything you create stays on your system and is not shared with anyone unless you choose to later. This has advantages but also the disadvantage that no session is saved if your browser crashes or closed.

The canvas of the podcast web editor extends to the right and down (if needed). There is a button at the top right to extend right. Large complex podcasts will fill the canvas quickly, and it is recommended to consider editing XML code directly for these projects.

The canvas can become messy when removing a lot of elements and may leave old lines around. Don’t worry, they are no longer active and can be ignored. If you would rather have a clean canvas, save your podcast and then refresh the browser tab. This will restart the editor and allow you to load the saved file, starting where you left off.

## Buttons on the left

![Adaptive podcasting editor buttons and info box](https://user-images.githubusercontent.com/1649922/188457243-385e3bb2-cba3-4765-913e-79b0f357545d.jpg)

### Preview File

Previews the current podcast in the editor, if valid and complete.

![Looking at the preview options in the editor](https://user-images.githubusercontent.com/1649922/188458054-626141cb-301d-47cd-996b-7ddd8d658344.png)

### Skip Element

When the editor is playing the created podcast, an option to skip the playing element(s) will become available under the stop preview.

### Create File

Creates a podcast/zip file in the download folder of the host machine. Acts like a save option.

## The Podcast Elements

### Audio

![The standard audio element with its advanced attributes](https://user-images.githubusercontent.com/1649922/188458602-4032d1e4-5424-45dd-82cd-9cc02931c2a7.png)

Creates an audio element on the graph, allowing the user to select an audio file (mp3, ogg, wav, aiff, opus, m4a). There are two optional attributes:

* Start at: Specifies a time delay before this element will begin to play. This delay is denoted in hh:mm:ss.
* Duration: Specifies how long the audio file should play for. This is denoted in hh:mm:ss.

### Text To Speech

![The text to speech element without and with text](https://user-images.githubusercontent.com/1649922/188458984-5a2dd404-48e7-4f99-9a2c-2d3af87f2f10.png)

Creates a Text To Speech element on the graph, allowing the user to write freeform text into the podcast, which will be spoken by the player in the mobile app.

It is recommended to think of each element as everything you would add before a full stop. Longer passages of text are difficult to manage in the editor due to space. The space limit is deliberate due to the jarring nature of playing text to speech in the previewer and on the device.

### Sensor Value

![The sensor value element with its different options](https://user-images.githubusercontent.com/1649922/188459322-dc7886c6-d596-4dd1-bd90-1d0c25945e97.png)

Sensor Value will read out the data of the sensor/data source using the text to speech engine. All of these are set in the previewer values in the web editor, but change to the device in the Android app/player.

* Light sensor (light/dark) - The status of the light sensor
* Date (dd/mm/yyyy) - The current date
* Time (hh:mm) - The current time
* Proximity (near/far) - Is the proximity sensor near or far from an item
* User Contacts (1-1000000) - How many contacts you have stored on the device
* Battery (0-100%) - The current battery level of the device
* City (city/town) - The city or nearest town
* Country (country) - The country the device is in
* Battery charging (No charge, USB, mains or wireless charge) - Battery charging method being used to change the device
* Headphones plugged in (plugged in or not) - Are the headphones plugged into the device or not?
* Device mode (normal, silent, vibrate) - Reads out the current phone profile
* Media Volume (0-100%) - The volume level set on the device currently
* User Language Name (Language ISO name) - The language set on the device
* User Language Code (ISO 639-1) - The language code set on the device

### Switch

![The switch element with the test option exposed](https://user-images.githubusercontent.com/1649922/188459910-584b6d10-6212-40b6-b87b-ce7a5319e819.png)

Creates a Switch start and end element on the graph. (The starting element will include the red (x) and the end one will sit underneath the starting one. Switches are joined by an orange line.) It is important to keep them paired as podcasts get more complex.

Switches, as the name suggests, allow a choice between multiple elements. You can put Switches inside of other Switches for more complex podcasts. There is no limit to the elements or level of structures within a Switch.

Each Switch includes a default test and the ability to add another test. The tests can be defined in the information box.

### Test

![The tests attributes housed in the tests tab inside the info box](https://user-images.githubusercontent.com/1649922/188460216-e689ed68-d66b-49c6-89cf-017704b92035.png)

Tests are controlled in the information box.

Switches, as the name suggests, allow a choice between multiple elements. You can put switches inside other switches for more complex podcasts. There is no limit to the elements or level of structures within a switch.

Each switch includes a default test and the ability to add another test. The tests can be defined in the information box.

### Parallel

![A couple of examples of using the parallel element](https://user-images.githubusercontent.com/1649922/188460467-c29d0e33-68e1-4448-a0cc-11a23868eb54.png)

The Parallel element provides the ability to layer other elements such as audio on top of each other.

Creates a Parallel start and end element on the graph. (The starting element will include the red (x) and the end one will sit underneath the starting one. Parallels are joined by a purple line.) It is important to keep them paired as podcasts get more complex.

Parallels, as the name suggests, allow multiple elements to be played at the same time. You can put Parallels inside of Parallels for more complex podcasts. There is no limit to the elements or level of structures within a Parallel.

### Pause

Creates a pause element on the graph, allowing the user to specify a delay till the next element is played. Similar to the audio element 'Start At', but can be applied to every other element. Time is specified in seconds.

### Vibrate

![Vibrate elements with zero and 1 second specified](https://user-images.githubusercontent.com/1649922/188460889-19000045-49d3-4775-aeb8-222e3ce6293e.png)

Creates a Vibrate element on the graph, allowing the user to tell the mobile app to vibrate a set number of seconds. This will be ignored in the web editor previewer. Time is specified in seconds.

It is a good idea to keep use of this for key moments and not annoy your listeners with long vibrations of more than 2 seconds.

## Info Box

![The default information box](https://user-images.githubusercontent.com/1649922/188464223-f5c120bd-c800-4f81-a869-01857822acc6.jpg)

### Info

The default tab contains information about tutorials, where to download the Adaptive Podcasting Application, and other information.

### Tests

![The tests attributes housed in the tests tab inside the info box](https://user-images.githubusercontent.com/1649922/188464762-fc5b1df9-5318-4585-b6ff-e303801c20d7.png)

Each test is defined and used in conjunction with the switch element. You can define as many as you wish and not all of them have to be used. This can be used to create a library of tests for different podcasts.

Creating a Test requires a Test name. Any alphanumeric characters can be used including spaces.

![The different attributes which can be applied in a test](https://user-images.githubusercontent.com/1649922/188464396-a4f30638-da04-467c-8893-3773f259421b.jpg)

Once named, the next step is to choose the condition for the test. Either:

* Light
* Time
* Date
* Proximity
* User_Contacts
* Battery
* User_Location
* Headphones
* Device_Mode
* Media_Volume
* User_Location

More tests are available outside the editor and can be written via the [merchant system](data-merchants.md).

Each of the conditions has a number of attributes which are visible when hovering over the question marks.

For example, above I wrote a test called - Night time, which happens if the TIME data sensor is Of-Day is Night (Android splits the day into Morning, Afternoon, Evening and Night).

It is also possible to enter multiple values in certain conditions, using a slash.

Using the example above:

Late time, which happens if the TIME data sensor is Of-Day is Evening/Night
Or even Not morning time, which happens if the TIME data sensor is Of-Day is Afternoon/Evening/Night.

![A test example with the test attributes in the info box](https://user-images.githubusercontent.com/1649922/188465308-888a1a86-1984-4c32-8dd8-4089b516101b.png)

However it would be easier or neater to write a test for Morning, then take advantage of the default path for every other condition.

![A test example using the default option to remove the complexity of tests](https://user-images.githubusercontent.com/1649922/188465627-c1ecd40d-c8b3-4576-b33d-66befa502e49.png)

The order of the switch is important.

The player will read from top to bottom, but as it's inside a switch element will only use the top one if the condition of summer is correct. Likewise, the player will play the first autumn one from the top if that is the correct condition. With defaults, it will use the top default if none of the tests are correct.

![A switch element demonstrating the importance of ordering](https://user-images.githubusercontent.com/1649922/188465833-c21f8cb7-33d7-49d0-8e72-ef36d8cd42a2.png)

### Previewer Values

![The preview values attributes housed in the previewer values tab in the info box](https://user-images.githubusercontent.com/1649922/188466195-51ffbf3a-a620-421b-b192-65630bdddee4.jpg)

The Previewer Values tab is used to emulate how the podcast will act on a mobile device. We call them fake phone values.

You can change and emulate these sensors & data.

The mobile application/player can do a lot more but the main ones are emulated in the web editor below.

* light sensor (light/dark) - Usually used to understand ambient brightness and change the backlight of the phone
* Date (dd/mm/yyyy) - The current date or to change to any other date for testing purposes
* Time (hh:mm) - The current time or to change to any other date for testing purposes
* Proximity (near/far) - Usually used to understand if the phone is next to a person's face or out in the open
* User Contacts (1-1000000) - How many contacts you have stored on the device
* Battery (0-100%) - The current battery level of the device
* City (city/town) - The city or nearest town
* Country (country) - The country the device is in
* Battery charging (No charge, USB, mains or wireless charge) - Battery charging method being used to change the device
* Headphones plugged in (plugged in or not) - Are the headphones plugged into the device or not. Less useful now more devices use Bluetooth
* Device mode (normal, silent, vibrate) - Uses the phone profiles as a data source: can rely on the 3 main ones on all devices
* Media Volume (0-100%) - The volume level set on the device currently
* User Language Name (Language ISO name) - The language set on the device in full
* User Language Code (ISO 639-1) - The language code set on the device

### Manifest (podcast metadata)

![The podcast title, credits, and image attributes housed in the manifest tab in the info box
](https://user-images.githubusercontent.com/1649922/188466573-df749652-dd27-48cf-924b-ca7762ccae3c.jpg)

The Manifest is the metadata about the podcast you are creating. Here you can add a podcast title, a square image, and credits.

Images are automatically resized to large (over 500px squared), medium (500px squared) and small (250px squared).

The podcast title is used for the podcast zip file and title in the created metadata/json file. Credits are also added to the same file, which is shown here.

![Example of how the podcast title, credits and image is used in the player app](https://user-images.githubusercontent.com/1649922/188466711-67531863-5a3d-4ddf-95b3-942ce3bce64c.jpg)

All items are optional including the podcast title, but it is good practice to name the podcast otherwise you will end up with zip.zip when saving the podcasts.

### Upload file

![The Upload File option housed in the upload file tab in the info box](https://user-images.githubusercontent.com/1649922/188466869-adadda7f-47ba-4ebd-b626-61943b1a2450.jpg)

The **Upload File** tab allows you to load a podcast zip file into the editor. You can only upload podcasts created with the editor and only one at a time. If you need to load more, you can open another tab in the browser.

Depending on the size of the podcast zip file and speed of the machine, it may take up to 20 seconds to load and display.
