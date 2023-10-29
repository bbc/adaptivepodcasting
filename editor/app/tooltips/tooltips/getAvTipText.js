export function getAvText(merchant, action) {
  switch (merchant) {
    case "LIGHT":
      return " light \n dark";
      break;
    case "TIME":
      return time(action);
      break;
    case "DATE":
      return date(action);
      break;
    case "PROXIMITY":
      return " near \n far";
      break;
    case "USER_CONTACTS":
      return " any integer, eg 23";
      break;
    case "BATTERY":
      return battery(action);
      break;
    case "USER_LOCATION":
      return userLocation(action);
      break;
    case "HEADPHONES":
      return " headphones \n no headphones";
      break;
    case "DEVICE_MODE":
      return " normal \n vibrate \n silent";
      break;
    case "MEDIA_VOLUME":
      return mediaVolume(action);
      break;
    case "USER_LANGUAGE":
      return userLanguage(action);
      break;
    default:
  }
}

function time(action) {
  switch (action) {
    case "of-day":
      return " morning \n afternoon \n evening \n night";
      break;
    case "hours-and-minutes":
      return " any 24 hour time, eg 23:18";
      break;
    case "hours":
      return " an integer between 0 and 23";
      break;
    case "minutes":
      return " an integer between 0 and 59";
      break;
    default:
      return "";
  }
}

function battery(action) {
  switch (action) {
    case "percentage":
      return " a percentage between 0% and 100% \n Include a % sign, eg 42%";
      break;
    case "level":
      return " high \n medium \n low";
      break;
    case "is-charging":
      return " charging \n not charging";
      break;
    case "charging-method":
      return " USB charging \n mains charging \n wireless charging \n not charging";
      break;
    default:
  }
}

function mediaVolume(action) {
  switch (action) {
    case "percentage":
      return " a percentage between 0% and 100% \n Include a % sign, eg 40%";
      break;
    case "level":
      return " high \n medium \n low";
      break;
    default:
  }
}

function userLocation(action) {
  switch (action) {
    case "city":
      return " any City name, eg Manchester";
      break;
    case "country":
      return " any Country name, eg England";
      break;
    default:
  }
}

function userLanguage(action) {
  switch (action) {
    case "language-name":
      return " any language, eg English";
      break;
    case "language-code":
      return " any language code, eg en_gb";
      break;
    default:
  }
}

function date(action) {
  switch (action) {
    case "day":
      return " monday \n tuesday \n wednesday \n thursday \n friday \n saturday \n sunday";
      break;
    case "date":
      return " an integer from 1 to 31";
      break;
    case "month":
      return " january \n february \n march \n april \n may \n june \n july \n august \n september \n october \n november \n december";
      break;
    case "season":
      return " spring \n summer \n autumn \n winter";
      break;
    case "year":
      return " any year, eg 2022";
      break;
    default:
  }
}
