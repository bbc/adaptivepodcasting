//previews the podcast from the editor based on values in the Previewer Vales tab


import { checkAllConnected } from "../smil/canBuild.js";
import { createElementArray } from "../smil/smilCreation.js";

import { getPhoneSensorValue } from "../preview/fakePhoneValues.js";
import { getCorrectSwitchPath } from "../preview/decideSwitchPath.js";
import { getParElmtPaths } from "../preview/parElmtPaths.js";

import { getAudioElmtFile } from "../zip/tempFiles.js";

import { restorePreviewButton } from "../interface/changingButtons.js";
import { makeStopPreviewButton } from "../interface/changingButtons.js";
import { toggleSkipButton } from "../interface/changingButtons.js";

import { minsToSecs } from "../elements/timeConversion.js";
import { getEndTimeSecs } from "../elements/timeConversion.js";

var currentElmt;
var currentResolve;

export var previewPlaying = false;

var currentAudioPlayer;
var currentTTS;
var currentTimeout;

var parPromises = [];

var cancelled = false;

export function previewPodcast() {

  if (checkAllConnected()) { //if podcast is okay to play
    makeStopPreviewButton();
    toggleSkipButton(true);
    
    previewPlaying = true;
    cancelled = false;
    var elements = createElementArray(); //get path of elements to play

    playPathWithPromises(elements, true);
  }

}

export function stopPreview() {
  cancelled = true;

  var playingHeaders = document.getElementsByClassName("playing");
  var headers = Array.from(playingHeaders);

  for (var i = 0; i < headers.length; i++) {
    var elmt = headers[i].parentElement;

    stopElement(elmt);
    setHeaderToStopped(elmt);
  }

  for (var j = 0; j < parPromises.length; j++) {
    parPromises[j]();
  }

  currentResolve();
}

function onPreviewStopped() {
  previewPlaying = false;
  restorePreviewButton();
  toggleSkipButton(false);
}

export function skipPlayingElement() {

  stopElement(currentElmt);
  setHeaderToStopped(currentElmt);
  currentResolve();
}

//create a promise chain that plays the elements in the path, one after the other
function playPathWithPromises(pathArray, outerLayer) {
 var i = 0;
  var nextPromise = () => {
    if (i >= pathArray.length) {
      //   console.log("Preview Finished");
      if (outerLayer) onPreviewStopped();
      return;
    }
    if (cancelled) {
      if (outerLayer) onPreviewStopped();
      return;
    }

   var newPromise = Promise.resolve(playNewElmt(pathArray[i]));
    i++;

    return newPromise.then(nextPromise);
  };
  return Promise.resolve().then(nextPromise);
}


function playNewElmt(elmt) {
  return new Promise(function (resolve) {
    setHeaderToPlaying(elmt);
    currentElmt = elmt;
    currentResolve = resolve;

    if (elmt.className.includes("audio")) {
      let promise = playAudioElement(elmt);
      promise.then(function (result) {
        resolve();
        setHeaderToStopped(elmt);
      });
    } else if (elmt.className.includes("tts")) {
      let promise = playTTSElement(elmt);

      promise.then(function (result) {
        resolve();
        setHeaderToStopped(elmt);
      });
    } else if (elmt.className.includes("pause")) {
      let promise = playPauseElement(elmt);
      promise.then(function (result) {
        resolve();
        setHeaderToStopped(elmt);
      });
    } else if (elmt.className.includes("vibrate")) {
      let promise = playPauseElement(elmt);
      promise.then(function (result) {
        resolve();
        setHeaderToStopped(elmt);
      });
    } else if (elmt.className.includes("dynquery")) {
      let promise = playDynqueryElement(elmt);
      promise.then(function (result) {
        resolve();
        setHeaderToStopped(elmt);
      });
    } else if (elmt.className.includes("switch")) {
      let promise = playSwitchElement(elmt);
      promise.then(function (result) {
        resolve();
        setHeaderToStopped(elmt);
      });
    } else if (elmt.className.includes("par")) {
      let promise = playParElement(elmt);
      promise.then(function (result) {
        resolve();
        setHeaderToStopped(elmt);
      });
    } else {
      resolve();
      setHeaderToStopped(elmt);
    }
  });
}


function setHeaderToPlaying(elmt) {
  var header = elmt.getElementsByClassName("blockheader")[0];
  header.className = header.className + " playing";

  if (elmt.className.includes("switch")) {
    var endId = elmt.getAttribute("endSwitch");
    var end = document.getElementById(endId);
    var endHeader = end.getElementsByClassName("blockheader")[0];
    endHeader.className = header.className + " playing";
  } else if (elmt.className.includes("par")) {
    var endId = elmt.getAttribute("endPar");
    var end = document.getElementById(endId);
    var endHeader = end.getElementsByClassName("blockheader")[0];
    endHeader.className = header.className + " playing";
  }
}

function setHeaderToStopped(elmt) {
  var header = elmt.getElementsByClassName("blockheader")[0];
  header.className = header.className.replaceAll(" playing", "");

  if (elmt.className.includes("switch")) {
    var endId = elmt.getAttribute("endSwitch");
    var end = document.getElementById(endId);

    var endHeader = elmt.getElementsByClassName("blockheader")[0];
    endHeader.className = header.className.replaceAll(" playing", "");
  } else if (elmt.className.includes("par")) {
    var endId = elmt.getAttribute("endPar");
    var end = document.getElementById(endId);

    var endHeader = elmt.getElementsByClassName("blockheader")[0];
    endHeader.className = header.className.replaceAll(" playing", "");
  }
}

function playAudioElement(elmt) {
  return new Promise(function (resolve) {
    var audioFile = getAudioElmtFile(elmt);
    var startTime = elmt.getElementsByClassName("start")[0].value;
    var durTime = elmt.getElementsByClassName("dur")[0].value;

    var audioSource = URL.createObjectURL(audioFile);
    const audioPlayer = elmt.querySelector("audio");

    audioPlayer.setAttribute("src", audioSource);

    audioPlayer.addEventListener("ended", resolve);

    if (startTime) {
      var startInSecs = parseInt(minsToSecs(startTime));

      var initDone = false;
      audioPlayer.addEventListener("canplaythrough", function () {
        if (!initDone) {
          audioPlayer.currentTime = startInSecs;
          initDone = true;
        }
      });
    }

    if (durTime) {
      var endSecs = parseInt(getEndTimeSecs(startTime, durTime));

      audioPlayer.ontimeupdate = function () {
        if (audioPlayer.currentTime > endSecs) {
          audioPlayer.pause();
          audioPlayer.currentTime = 0;
          resolve();
        }
      };
    }

    currentAudioPlayer = audioPlayer;

    audioPlayer.play();
  });
}

function playTTSElement(elmt) {
  return new Promise(function (resolve) {
    var elmtText = elmt.getElementsByTagName("textArea")[0].value;
    var msg = new SpeechSynthesisUtterance();
    msg.text = elmtText;
    msg.addEventListener("end", resolve);

    var synth = window.speechSynthesis;
    currentTTS = synth;
    synth.speak(msg);
  });
}

function playPauseElement(elmt) {
  return new Promise(function (resolve, reject) {
    var elmtText = elmt.getElementsByTagName("input")[0].value;
    if (isNaN(elmtText)) {
      alert("Pause value is not a number");
      resolve();
    } else {
      var timeout = elmtText * 1000;
      currentTimeout = setTimeout(resolve, timeout);
    }
  });
}

function playDynqueryElement(elmt) {
  return new Promise(function (resolve) {
    var selects = elmt.getElementsByTagName("select");
    var merchant = selects[0].value;
    var action = selects[1].value;

    var phoneValue = getPhoneSensorValue(merchant, action);

    var msg = new SpeechSynthesisUtterance();
    msg.text = phoneValue;
    msg.addEventListener("end", resolve);

    var synth = window.speechSynthesis;
    currentTTS = synth;
    synth.speak(msg);
  });
}

function playSwitchElement(elmt) {
  return new Promise(function (resolve) {
    var correctPathArray = getCorrectSwitchPath(elmt);

    if (correctPathArray == null) resolve();
    else {
      let promise = playPathWithPromises(correctPathArray, false);
      promise.then(function (result) {
        resolve();
      });
    }
  });
}

function playParElement(elmt) {
  return new Promise(function (resolve) {
    parPromises.push(resolve);

    var paths = getParElmtPaths(elmt);

    var promises = [];

    for (var i = 0; i < paths.length; i++) {
      promises.push(playPathWithPromises(paths[i], false));
    }

    Promise.all(promises).then(function (result) {
      resolve();
    });
  });
}

function stopElement(elmt) {
  if (elmt.className.includes("audio")) {
    currentAudioPlayer.pause();
    currentAudioPlayer.src = "";
    
    
    var partAP=elmt.getElementsByTagName("audio")[0];
   if(partAP){
    partAP.currentTime=0;
    partAP.src="";
   }
    
  } else if (elmt.className.includes("tts")) {
    currentTTS.cancel();
  } else if (elmt.className.includes("dynquery")) {
    currentTTS.cancel();
  } else if (elmt.className.includes("pause")) {
    clearTimeout(currentTimeout);
  } else if (elmt.className.includes("vibrate")) {
    clearTimeout(currentTimeout);
  }
}
