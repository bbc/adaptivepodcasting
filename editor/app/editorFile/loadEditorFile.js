//loads an editor.json file from an uploaded zip file

import { assignNewElmtListeners } from "../interface/creating.js";
import { setDynqueryUp } from "../interface/creating.js";
import { updateDynqueryDropdown } from "../elements/elementBehaviour.js";

import { movingBox } from "../linking/linkingElmts.js";
import { updateIdCount } from "../interface/creating.js";

import { addIfTest } from "../box/infoBox.js";
import { testUpdated } from "../box/infoBox.js";

import { onTestDropdownChange } from "../tooltips/acceptedValuesTips.js";

var timeline = document.getElementById("timeline");

export function loadEditorFile(obj) {
  addAllElements(obj.elmts);
  //update id count (for making new elements) to match uploaded file
  updateIdCount(obj.idCount);
  //if the file includes tests, import them
  if (obj.tests) importTests(obj.tests);
  //update info in dropdowns
  assignSelects(obj.selects);
}

//add all elements to page
function addAllElements(elements) {
  var addedElements = [];

  for (var i = 0; i < elements.length; i++) {
    addedElements.push(addElement(elements[i]));
  }

  for (var i = 0; i < addedElements.length; i++) {
    if (
      addedElements[i].getAttribute("nextid") ||
      addedElements[i].className.includes("switch")
    )
      movingBox(addedElements[i]); //draw lines connencting elements
  }
}

//add an element from editorFile.json to the page
function addElement(data) {
  var example = document.getElementById(getExampleId(data.class));
  var newElmt = example.cloneNode(true);
  var attribs = Object.keys(data);
  //add element attributes from editorFile
  for (var i = 0; i < attribs.length; i++) {
    newElmt.setAttribute(attribs[i], data[attribs[i]]);
  }

  newElmt = assignValue(data.value, newElmt);

  timeline.appendChild(newElmt);
  assignNewElmtListeners(newElmt, true);

  return newElmt;
}

//find example HTML element from class name
function getExampleId(className) {
  if (className.includes("audio")) return "exampleAudio";
  else if (className.includes("tts")) return "exampleTTS";
  else if (className.includes("dynquery")) return "exampleDynquery";
  else if (className.includes("endS")) return "exampleSwitchEnd";
  else if (className.includes("switch")) return "exampleSwitch";
  else if (className.includes("endP")) return "exampleParEnd";
  else if (className.includes("par")) return "examplePar";
  else if (className.includes("pause")) return "examplePause";
  else if (className.includes("vibrate")) return "exampleVibrate";
}

//add value (audio for audio elmts, text for tts etc) to element
function assignValue(value, elmt) {
  if (elmt.className.includes("tts")) {
    var textarea = elmt.getElementsByTagName("textarea")[0];
    textarea.value = value;
  } else if (
    elmt.className.includes("pause") ||
    elmt.className.includes("vibrate")
  ) {
    var input = elmt.getElementsByTagName("input")[0];
    input.value = value;
  } else if (elmt.className.includes("dynquery")) {
    var selects = elmt.getElementsByTagName("select");
    elmt = setDynqueryUp(elmt);
    selects[0].value = value[0];
    updateDynqueryDropdown(elmt);
    selects[1].value = value[1];
  } else if (elmt.className.includes("audio")) {
    elmt = setUpAudio(elmt, value);
    elmt = assignAudioTimings(elmt);
  }
  return elmt;
}

//add audio to audio element
function setUpAudio(elmt, value) {
  var input = elmt.getElementsByTagName("input")[0];
  input.className = "hidden";

  var uploadedAudio = elmt.getElementsByClassName("uploadedAudio")[0];
  var uploadedAudioName = elmt.getElementsByClassName("uploadedAudioName")[0];

  uploadedAudio.className = uploadedAudio.className.replace("hidden", "");
  uploadedAudioName.innerText = value;

  return elmt;
}

//set up tests from editorFile
function importTests(tests) {
  for (var i = 0; i < tests.length; i++) {
    var newTest = addIfTest(false);
    var inputs = newTest.querySelectorAll("input");
    var selects = newTest.querySelectorAll("select");

    inputs[0].value = tests[i]["name"];
    selects[0].value = tests[i]["merchant"];

    //make second select based on the value of the first
    updateDynqueryDropdown(newTest);
    selects = newTest.querySelectorAll("select");

  
    //on second select change, show tooltips
    selects[1].addEventListener("change", function () {
      onTestDropdownChange(newTest);
    });

    selects[1].value = tests[i]["action"];
    inputs[1].value = tests[i]["value"];

    testUpdated(newTest);
  }
}
//add values to selects
function assignSelects(selectIds) {
  for (var i = 0; i < selectIds.length; i++) {
    var select = document.getElementById(selectIds[i]["id"]);
    select.value = selectIds[i]["value"];
  }
}
//add audio timings from editorFile
function assignAudioTimings(elmt) {
  var startInput = elmt.getElementsByClassName("start")[0];
  var durInput = elmt.getElementsByClassName("dur")[0];
  if (elmt.getAttribute("start")) startInput.value = elmt.getAttribute("start");
  if (elmt.getAttribute("dur")) durInput.value = elmt.getAttribute("dur");

  return elmt;
}
