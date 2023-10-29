//making elements and their behaviour

import { dragElement,closeButtonPressed,updateDynqueryDropdown,addSpace,addSpaceVertical } from "../elements/elementBehaviour.js";
import { testSwitch } from "../elements/testSwitch.js";

import { initTooltips } from "../tooltips/tooltips.js";
import { onTimeInputChanged } from "../tooltips/errorTips.js";
import { onInputKeyUp } from "../tooltips/errorTips.js";
import { refreshBlockTooltips } from "../tooltips/blockTooltips.js";

import { movingBox } from "../linking/linkingElmts.js";

import { onLineDotMouseDown } from "../linking/linkingElmts.js";

import { initInfoBox } from "../box/infoBox.js";
import { showBoxContent } from "../box/infoBox.js";
import { addIfTest } from "../box/infoBox.js";

import { previewPodcast } from "../preview/previewer.js";
import { stopPreview } from "../preview/previewer.js";
import { previewPlaying } from "../preview/previewer.js";
import { skipPlayingElement } from "../preview/previewer.js";

import { makeZipFile } from "../zip/zip.js";
import { setOnImageUploaded } from "../zip/imageResizing.js";
import { currentlyDownloading } from "../zip/zip.js";

const buttons = document.getElementsByClassName("button");
const timeline = document.getElementById("timeline");

export var idCount = 0;
export var merchants = [
  "LIGHT",
  "TIME",
  "DATE",
  "PROXIMITY",
  "USER_CONTACTS",
  "BATTERY",
  "USER_LOCATION",
  "HEADPHONES",
  "DEVICE_MODE",
  "MEDIA_VOLUME",
  "USER_LANGUAGE",
];

init();

function init() {
  assignButtons();
  initInfoBox();
  setOnImageUploaded();
  initAddSpace();
  initTooltips();
}

//assign listeners to buttons
function assignButtons() {
  var audioButton = document.getElementById("audioButton");
  audioButton.addEventListener("click", function () {
    addNewTextInputElement("exampleAudio");
  });

  var ttsButton = document.getElementById("ttsButton");
  ttsButton.addEventListener("click", function () {
    addNewTextInputElement("exampleTTS");
  });

  var dynqueryButton = document.getElementById("dynqueryButton");
  dynqueryButton.addEventListener("click", function () {
    addNewDynqueryElement();
  });
  var switchButton = document.getElementById("switchButton");
  switchButton.addEventListener("click", function () {
    addNewTwoPartElement("exampleSwitch");
  });
  var switchButton = document.getElementById("parButton");
  switchButton.addEventListener("click", function () {
    addNewTwoPartElement("examplePar");
  });

  var ifButton = document.getElementById("ifButton");
  ifButton.addEventListener("click", function () {
    addIfTest(true);
  });
  var pauseButton = document.getElementById("pauseButton");
  pauseButton.addEventListener("click", function () {
    addNewTextInputElement("examplePause");
  });

  var vibrateButton = document.getElementById("vibrateButton");
  vibrateButton.addEventListener("click", function () {
    addNewTextInputElement("exampleVibrate");
  });

  var previewButton = document.getElementById("previewButton");
  previewButton.addEventListener("click", function () {
    if (!previewPlaying) {
      previewPodcast();
    } else {
      stopPreview();
    }
  });

  var skipButton = document.getElementById("skipButton");
  skipButton.addEventListener("click", function () {
    skipPlayingElement();
  });
  var createFileButton = document.getElementById("createFileButton");
  createFileButton.addEventListener("click", function () {
    if (!currentlyDownloading) {
      makeZipFile();
    }
  });

  var addTestButton = document.getElementById("addTestButton");
  addTestButton.addEventListener("click", function () {
    addIfTest(true);
  });
}
//add new elements like Text to Speech, Pause or Vibrate
function addNewTextInputElement(exampleId) {
  let example = document.getElementById(exampleId);
  let newElmt = example.cloneNode(true);
  var timeline = document.getElementById("timeline");
  timeline.appendChild(newElmt);
  assignNewElmtListeners(newElmt);
}

//add a Sensor Value element
function addNewDynqueryElement() {
  let example = document.getElementById("exampleDynquery");
  let newElmt = example.cloneNode(true);

  newElmt = setDynqueryUp(newElmt);
  var timeline = document.getElementById("timeline");
  timeline.appendChild(newElmt);
  assignNewElmtListeners(newElmt);
}

export function setDynqueryUp(elmt) {
  let select = elmt.getElementsByTagName("select")[0];

  for (var i = 0; i < merchants.length; i++) {
    var option = document.createElement("option");
    option.setAttribute("value", merchants[i]);
    option.innerHTML = merchants[i];
    select.appendChild(option);
  }

  select.addEventListener("change", function () {
    updateDynqueryDropdown(elmt);
  });
  return elmt;
}
//add switch or parallel element
function addNewTwoPartElement(exampleId) {
  let exampleEnd = document.getElementById(exampleId + "End");
  let newElmtEnd = exampleEnd.cloneNode(true);
  var timeline = document.getElementById("timeline");
  timeline.appendChild(newElmtEnd);
  assignNewElmtListeners(newElmtEnd);

  let example = document.getElementById(exampleId);
  let newElmt = example.cloneNode(true);
  var timeline = document.getElementById("timeline");
  var secondPartAttribute = exampleId.replace("example", "end");
  newElmt.setAttribute(secondPartAttribute, newElmtEnd.id);
  timeline.appendChild(newElmt);
  assignNewElmtListeners(newElmt);
}

//assign js listeners for new element
export function assignNewElmtListeners(elmt, existingElmt) {
  refreshBlockTooltips();

  //if element doesn't exist yet, make it at the spawn point
  if (!existingElmt) {
    var width = window.innerWidth / 2;
    var left = width + window.scrollX;

    var height = window.innerHeight / 2;
    var top = height + window.scrollY;
    elmt.setAttribute("style", "top: " + top + "px; left: " + left + "px;");
  }

  elmt.className = elmt.className.replace("hidden", "");
  if (!existingElmt) {
    elmt.id = idCount;
    idCount++;
  }

  const header = elmt.getElementsByClassName("blockheader")[0];
  const closeButton = header.getElementsByClassName("closeButtons")[0];
  if (closeButton) {
    closeButton.addEventListener("click", function () {
      closeButtonPressed(elmt);
    });
  }
  let leftLineDot = elmt.getElementsByClassName("lineDot")[0];
  let rightLineDot = elmt.getElementsByClassName("lineDot")[1];
  leftLineDot.addEventListener("mousedown", function () {
    onLineDotMouseDown(elmt, 0);
  });
  rightLineDot.addEventListener("mousedown", function () {
    onLineDotMouseDown(elmt, 1);
  });

  if (elmt.className.includes("audio")) assignAudioFileListeners(elmt);
  if (elmt.className.includes("tts")) assignTTSListeners(elmt);
  if (elmt.className.includes("switch")) assignSwitchListeners(elmt);

  dragElement(elmt);
}

//update Id count from Editor File when loaded uploaded zip file
export function updateIdCount(count) {
  idCount = count;
}

function assignAudioFileListeners(elmt) {
  var input = elmt.getElementsByTagName("input")[0];
  var audio = elmt.getElementsByTagName("audio")[0];

  var deleteAudioButton = elmt.getElementsByClassName("deleteAudioButton")[0];
  var uploadedAudio = elmt.getElementsByClassName("uploadedAudio")[0];
  var uploadedAudioName = elmt.getElementsByClassName("uploadedAudioName")[0];
  var changeTimesButton = elmt.getElementsByClassName("changeTimes")[0];
  var expandedAudio = elmt.getElementsByClassName("expandedAudio")[0];

  deleteAudioButton.addEventListener("click", function () {
    uploadedAudio.className = uploadedAudio.className + " hidden";
    uploadedAudioName.innerText = "";
    input.className = input.className.replace("hidden", "");
  });

  //edit times listeners
  var timeInputs = elmt.getElementsByClassName("timeInput");
  timeInputs[0].addEventListener("change", function () {
    onTimeInputChanged(timeInputs[0]);
  });
  timeInputs[0].addEventListener("keyup", function () {
    onInputKeyUp(timeInputs[0]);
  });

  timeInputs[1].addEventListener("change", function () {
    onTimeInputChanged(timeInputs[1]);
  });
  timeInputs[1].addEventListener("keyup", function () {
    onInputKeyUp(timeInputs[1]);
  });

  changeTimesButton.addEventListener("click", function () {
    if (expandedAudio.className.includes("hidden"))
      expandedAudio.className = expandedAudio.className.replace(" hidden", "");
    else expandedAudio.className = expandedAudio.className + " hidden";

    movingBox(elmt);
  });
}

function assignTTSListeners(elmt) {
  var textarea = elmt.getElementsByTagName("textarea")[0];
  textarea.addEventListener("mouseup", function () {
    movingBox(elmt);
  });
}

function assignSwitchListeners(elmt) {
  var button = elmt.getElementsByTagName("button")[1];
  button.addEventListener("click", function () {
    testSwitch(elmt);
  });
}

function initAddSpace() {
  var addSpaceButton = document.getElementById("addSpace");
  addSpaceButton.addEventListener("click", function () {
    addSpace();
  });
  
      var addVerticalSpaceButton = document.getElementById("addSpaceVertical");
  addVerticalSpaceButton.addEventListener("click", function () {
    addSpaceVertical();
  });
  
}
