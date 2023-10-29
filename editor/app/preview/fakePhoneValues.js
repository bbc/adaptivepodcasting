//returns test values based on the fake phone sensor in the Previewer Values in the Info box

export function getPhoneSensorValue(merchant, action) {
  switch (merchant) {
    case "ELSE":
      return "else";
      break;
    case "TIME":
      return time(action);
      break;
    case "DATE":
      return date(action);
      break;
    case "BATTERY":
      return battery(action);
      break;
    case "MEDIA_VOLUME":
      return mediaVolume(action);
      break;
    case "USER_LOCATION":
      return userLocation(action);
      break;
    case "USER_LANGUAGE":
      return userLanguage(action);
      break;
    default:
      return document.getElementById("phone" + merchant).value;
  }
}

function time(action) {
  var time = document.getElementById("phoneTIME").value;
  var timeParts = time.split(":");
  switch (action) {
    case "of-day":
      var hours = timeParts[0];
      if (hours > 5 && hours < 12) {
        return "morning";
      } else if (hours > 11 && hours < 19) {
        return "afternoon";
      } else if (hours > 18 && hours < 23) {
        return "evening";
      } else {
        return "night";
      }
      break;
    case "hours-and-minutes":
      return time.replace(":", " ");
      break;
    case "hours":
      return timeParts[0];
      break;
    case "minutes":
      return timeParts[1];
      break;
    default:
      return time;
  }
}

function battery(action) {
  var battery = document.getElementById("phoneBATTERY").value;
  var batteryCharging = document.getElementById("phoneBATTERYCHARGING").value;

  var batteryInt = battery.replace("%", "");

  switch (action) {
    case "percentage":
      return battery;
      break;
    case "level":
      if (batteryInt > 66) {
        return "high";
      } else if (batteryInt > 33) {
        return "medium";
      } else {
        return "low";
      }
      return null;
      break;

    case "is-charging":
      if (
        batteryCharging === "USB charging" ||
        batteryCharging === "mains charging" ||
        batteryCharging === "wireless charging"
      )
        return "charging";
      else return "not charging";
      break;

    case "charging-method":
      return batteryCharging;
      break;

    default:
      return battery;
  }
}

function mediaVolume(action) {
  var volume = document.getElementById("phoneMEDIA_VOLUME").value;
  var volumeInt = volume.replace("%", "");

  switch (action) {
    case "percentage":
      return volume;
      break;
    case "level":
      if (volumeInt < 40) {
        return "low";
      } else if (volumeInt < 75) {
        return "medium";
      } else {
        return "high";
      }
      break;

    default:
      return volume;
  }
}

function userLocation(action) {
  switch (action) {
    case "city":
      return document.getElementById("phoneCITY").value;
      break;
    case "country":
      return document.getElementById("phoneCOUNTRY").value;
      break;
    default:
      return document.getElementById("phoneCOUNTRY").value;
  }
}

function userLanguage(action) {
  switch (action) {
    case "language-name":
      return document.getElementById("phoneUSER_LANGUAGE_NAME").value;
      break;
    case "language-code":
      return document.getElementById("phoneUSER_LANGUAGE_CODE").value;
      break;
    default:
      return document.getElementById("phoneUSER_LANGUAGE_NAME").value;
  }
}

function date(action) {
  var input = document.getElementById("phoneDATE");
  var date = new Date(input.value);

  switch (action) {
    case "day":
      var days = [
        "sunday",
        "monday",
        "tuesday",
        "wednesday",
        "thursday",
        "friday",
        "saturday",
      ];
      var int = date.getDay();
      return days[int];
      break;
      
    case "date":
      return date.getDate();
      break;
      
    case "month":
      var months = [
        "january",
        "february",
        "march",
        "april",
        "may",
        "june",
        "july",
        "august",
        "september",
        "october",
        "november",
        "december",
      ];
      var int = date.getMonth();
      return months[int];
      break;
      
    case "season":
      var season;
      var int = date.getMonth();
      if (int == 11 || int <= 1) season = "winter";
      else if (int <= 4) season = "spring";
      else if (int <= 7) season = "summer";
      else season = "autumn";
      return season;
      break;
      
    case "year":
      return date.getFullYear();
      break;
    default:
      return date.getDate();
  }
}
