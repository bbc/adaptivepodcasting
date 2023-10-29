# Data Merchants

In order to allow for the making of editorial choices dynamically, as the user plays the content on their device, the Data Merchant system establishes an extensible collection of data sources that can be called on by SMIL files parsed by the application. It is these data merchants that are used in the [Custom Tests](smil.md#custom-tests) or [Dynamic Queries](smil.md#dynamic-script) of the SMIL Lite file.

If you wish to create your own Data Merchant to extend the sensors and functionality available to SMIL Lite files, we recommend following this [tutorial](creating-a-data-merchant.md).

> Note: Some data merchants can return "null". This should be considered when integrating them into content. If you have a preference as to how this should be handled, consider [creating a pull request](https://help.github.com/articles/creating-a-pull-request/)!

## Contents

- [Battery Level](#battery-level)
- [Date](#Date)
- [Device Mode](#device-mode)
- [Else](#else)
- [Headphones](#headphones)
- [Light Sensor](#light-sensor)
- [Media Volume](#media-volume)
- [Output Device](#output-device)
- [Proximity](#proximity)
- [Time](#Time)
- [User Activity](#user-activity)
- [User Contacts](#user-contacts)
- [User Interaction](#user-interaction)
- [User Location](#user-location)
- [User Name](#user-name)

## Battery Level

A simple description of the battery status of the user's device. Two levels of accuracy are available.

**relatedMerchant:** "BATTERY"

**Actions:**
- coarse

    **Return Values**
    - String
      - `"low"` The battery level is below 33 percent.
      - `"medium"` The battery level is above 33 percent and below 66 percent.
      - `"high"` The battery level is above 66 percent.
      - `"charging"` The battery is actively charging.
- percentage

    **Return Values**
    - Integer
- chargingMethod

    **Return Values**
    - String
      - `"mains charging"` The battery is actively charging via a mains power cable.
      - `"USB charging"` The battery is actively charging via a USB cable.
      - `"wireless charging"` The battery is actively charging via wireless.

## Date

**relatedMerchant** = “DATE”

relatedAction = “day”
**Return Values**
  - monday
  - tuesday
  - wednesday
  - thursday
  - friday
  - saturday
  - sunday

relatedAction = “month”
**Return Values**
  - january
  - february
  - march
  - april
  - may
  - june
  - july
  - august
  - september
  - october
  - november
  - december

relatedAction = “season”
**Return Values**
  - spring
  - summer
  - autumn
  - winter

relatedAction = “year”
	**Return Values**
  year (eg 2021)

relatedAction = “date”
	**Returns**
  day of the month (eg 31)

## Device Mode

**relatedMerchant:** "DEVICE_MODE"

 **Return Values**
  - normal
  - vibrate
  - silent

## Else

Used to tell the app what to do if the conditions of other tests are not fulfilled.

**relatedMerchant:** "ELSE"

 **Return Values**
  - Else

## Headphones

**relatedMerchant:** "HEADPHONES"

 **Return Values**
  - headphones
  - no headphones

## Light Sensor

**relatedMerchant:** "LIGHT"

 **Return Values**
  - dark
  - light

## Media Volume

**relatedMerchant:** "MEDIA_VOLUME"

**Actions:**
- coarse
 **Return Values**
  - low
  - medium
  - high

-""
**Return Values**
  - volume percentage (eg 80 %)

## Output Device

Describes various characteristics of the means by which the current perceptive podcast is being played.

> Note: This implementation is work in progress.

**relatedMerchant:** "OUTPUT_DEVICE_AUDIO"

**Actions:**
- **channelCount**  Returns the number of channels of the currently connected audio output device.

- **isPersonal**    Returns a String of "true"/"false"/null describing whether the currently connected audio output device is indicative of a device intended for individual ("personal") or group listening.

- **isWired**       Returns a String of "true"/"false"/null describing whether the currently connected audio output device is wired or wireless.

## Proximity

**relatedMerchant:** "PROXIMITY"

 **Return Values**
  - Near
  - Far

## Time

**relatedMerchant:** "TIME"

**Actions:**
- ofDay

 **Return Values**
  - morning
  - afternoon
  - evening
  - night

- hours
 **Return Values**
  - hours (eg 21)

-minutes
**Return Values**
  - minutes (eg 46)

 - ""
 **Return Values**
  - time (eg 21 46)

## User Activity

Describes the user's current, "real-world" activity. Much of this focuses on giving information on their current mode of transport.

**relatedMerchant:** "ACTIVITY"

**Return Values**
 - **vehicle**
 - **cycling**
 - **running**
 - **walking**
 - **tilting**
 - **still**
 - **unknown**

## User Contacts

Returns the number of contacts, as an integer.

**relatedMerchant:** "USER_CONTACTS"

## User Interaction

Describes whether the user has recently interacted with their device.

> Note: This implementation is work in progress.

**relatedMerchant:** "USER_INTERACTION"

**Return Values**
- **true**
- **false**

## User Location

**relatedMerchant:** "USER_LOCATION"

**Actions:**
- **country** Returns a String containing the name of the country the user is currently in.
- **city** Returns a String containing the name of the locality the user is currently in.
- **N/A** If no action is provided, or if the above actions don't return a result, then the country code of the user's current country will be returned.

## User Name

Returns a String containing the name of the currently active user account of the device. Users of this merchant should be prepared for the account name to be empty in some cases.

**relatedMerchant:** "USER_NAME"

**Return Values**
- String
