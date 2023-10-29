# Credentials

Private API credentials are stored in `player/app/src/main/res/raw/credentials.json`. This location is and should always be ignored by git.

The `credentials.json` file attempts to simplify the storing of API dependencies to allow for easy switching of source APIs as different clones of the project utilise their own API keys, with potentially different services. Here, we'll use a Weather data merchant as an example.

When researching weather APIs, you'll likely discover many different query formats. A quick Google will soon return myriad, subtly different formats such as the following:
`example.com/weather?lat={lat}&lon={lon}&key={API_key}`
`example.com/{API_key}/?q={lat},{lon}`

As such, the `credentials.json` file is based on a simple system: specify the URL, and any number of arbitrary parameters. If a field relies on a realtime or programmatic value that cannot be specified in a permanent file, create a `$___` placeholder that is as generic as possible, to be replaced in code.

```json
{
    "weather": {
        "url": "api.example.com/current",
        "key": "someAPIKey",
        "lat": "$lat",
        "lon": "$lon"
    }
}
```

After specifying your credentials in the file, you can easily access them using the `Credentials` class. The general pattern should be as follows:

```java
JSONObject weatherCreds = credentials.get("weather");

// Replace "$lat" and "$long" in JSON with lat and long coords
for (Iterator iterator = weatherCreds.keys(); iterator.hasNext();) {
    String key = (String)iterator.next();
    try {
        weatherCreds.put(key, weatherCreds.get(key).toString().replace("$lat", latitude.toString()));
        weatherCreds.put(key, weatherCreds.get(key).toString().replace("$long", longitude.toString()));
    } catch (JSONException e) {
        e.printStackTrace();
    }
}

// Formulate your request using your HTTP client of choice,
// assigning every non-URL field to a parameter of the same name...
```
