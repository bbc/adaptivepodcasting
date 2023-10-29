//functions for time conversions and durations 

export function secsToMins(secs) {
  var mins = Math.floor(secs / 60);
  var remainderSecs = secs % 60;
  return mins + ":" + remainderSecs;
}

export function minsToSecs(minString) {
  var parts;
  if (minString.includes(":")) parts = minString.split(":");
  else parts = minString.split(".");

  var mins = parseInt(parts[0]);
  var remainderSecs = parseInt(parts[1]);

  return mins * 60 + remainderSecs;
}

export function getEndTimeSecs(startTime, duration) {
   if(startTime==="") startTime = "0:00";
  var startSecs = minsToSecs(startTime);
  var durSecs = minsToSecs(duration);

  var endTime = startSecs + durSecs;
  return endTime;
}
