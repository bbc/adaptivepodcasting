var instances = {};

export function onTimeInputChanged(input) {
  //check if existing instance in instances obj
  var instance = getInstanceForInput(input);

  //check if input is okay
  var inputOk = timeFormatOk(input.value);

  if (!inputOk) instance.show();
  else {
    instance.hide();
  }
}

function getInstanceForInput(input) {
  var id = getInputParentId(input);

  if (!instances[id]) {
    instances[id] = makeNewInstance(input);
  }

  return instances[id];
}

function getInputParentId(input) {
  var id;
  var parent = input.parentElement;
  if (input.className.includes("start")) id = parent.id + ":start";
  else if (input.className.includes("end")) id = parent.id + ":end";
  return id;
}

function timeFormatOk(text) {
  if (text === "") return true;

  var parts;
  if (text.includes(":")) {
    parts = text.split(":");
  } else if (text.includes(".")) {
    parts = text.split(".");
  } else return false;

  if (parts.length !== 2) return false;

  if (isNaN(parts[0]) || isNaN(parts[1])) return false;

  return true;
}

function makeNewInstance(elmt) {
  tippy(elmt, {
    content: "Time format is incorrect (must be in minutes)",
    trigger: "manual",
    interactive: true,
    hideOnClick: false,
    theme: "error",
    placement: "bottom",
    arrow: true,
  
  });

  var instance = elmt._tippy;

  return instance;
}


export function onInputKeyUp(input){
  if(timeFormatOk(input.value))
    onTimeInputChanged(input);
}

export function allTimeFormatsCorrect() {
  var allOk = true;
  var timeInputs = document.getElementsByClassName("timeInput");

  for (var i = 0; i < timeInputs.length; i++) {
    var inputOk = timeFormatOk(timeInputs[i].value);

    if (!inputOk) allOk = false;
  }

  return allOk;
}
