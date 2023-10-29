//create editorFile.json (when download file clicked)

import { xmlToString } from "../smil/smilCreation.js";
import { assignNewElmtListeners } from "../interface/creating.js";

import { idCount } from "../interface/creating.js";
import { getAudioElmtFile } from "../zip/tempFiles.js";

export function makeEditorFile() {
  var editorObj = {};
  var allElmts = [];
  var allSelects = [];

  editorObj.version = "2023.5.01";

  var blocks = document.querySelectorAll(".block:not(.hidden):not(.info):not(.spaceText)");

  //add all elements
  for (var i = 0; i < blocks.length; i++) {
    allElmts.push(getElmtAttribs(blocks[i]));
  }

  editorObj.elmts = allElmts;
//add all selects
  var selects = document.querySelectorAll(".select");
  for (var i = 0; i < selects.length; i++) {
    allSelects.push(getSelectAttribs(selects[i]));
  }

  editorObj.selects = allSelects;

  editorObj.idCount = idCount;

  editorObj.tests = addAllTests();

  var editorString = JSON.stringify(editorObj, null, 4);

  return editorString;
}

function getElmtAttribs(elmt) {
  var newElmt = {};
  var attribs = elmt.attributes;

  for (var i = 0; i < attribs.length; i++) {
    newElmt[attribs[i].nodeName] = attribs[i].nodeValue;
  }

  if (elmt.className.includes("audio"))
    newElmt = getAudioTimings(elmt, newElmt);

  newElmt.value = getValue(elmt.className, elmt);
  return newElmt;
}

function getValue(type, elmt) {
  if (type.includes("audio")) {
    return getAudioElmtFile(elmt).name;
  } else if (type.includes("pause") || type.includes("vibrate")) {
    return elmt.getElementsByTagName("input")[0].value;
  } else if (type.includes("tts")) {
    return elmt.getElementsByTagName("textarea")[0].value;
  } else if (type.includes("dynquery")) {
    var selects = elmt.getElementsByTagName("select");
    return [selects[0].value, selects[1].value];
  } else if (type.includes("switch") || type.includes("par")) {
    return null;
  } else if (type.includes("endS") || type.includes("endP")) {
    return null;
  }
}

function getLineAttribs(line) {
  var newLine = {};
  var attribs = line.attributes;
  for (var i = 0; i < attribs.length; i++) {
    newLine[attribs[i].nodeName] = attribs[i].nodeValue;
  }
  return newLine;
}

function getSelectAttribs(select) {
  var newSelect = {};
  newSelect.id = select.id;
  newSelect.value = select.value;

  return newSelect;
}

function addAllTests() {
  var testsObj = [];
  var testElmts = document.querySelectorAll(".if:not(.hidden)");

  for (var i = 0; i < testElmts.length; i++) {
    testsObj.push(addTest(testElmts[i]));
  }

  return testsObj;
}

function addTest(elmt) {
  var testObj = {};
  var inputs = elmt.querySelectorAll("input");
  var selects = elmt.querySelectorAll("select");

  testObj.name = inputs[0].value;
  testObj.merchant = selects[0].value;
  testObj.action = selects[1].value;
  testObj.value = inputs[1].value;

  return testObj;
}

function getAudioTimings(elmt, obj) {
  var start = elmt.getElementsByClassName("start")[0].value;
  var dur = elmt.getElementsByClassName("dur")[0].value;
  obj.start = start;
  obj.dur = dur;
  return obj;
}
