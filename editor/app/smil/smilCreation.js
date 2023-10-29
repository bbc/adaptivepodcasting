//Creates smil.xml file to go in zip

import { getAudioElmtFile } from "../zip/tempFiles.js";

import { startingId } from "../smil/canBuild.js";
import { checkAllConnected } from "../smil/canBuild.js";

import { minsToSecs } from "../elements/timeConversion.js";
import { getEndTimeSecs } from "../elements/timeConversion.js";

var removed = [];
var doc;
var xText;


export function createSMILFile() {
  if (checkAllConnected()) {
    return buildSmil();
  }
}

function buildSmil() {
  //get all elements connected by 'next' and 'last' in order
  var elements = createElementArray();
  removed = [];

  //create empty SMIL file
  doc = document.implementation.createDocument("", "", null);
  var smil = doc.createElement("smil");
  var head = doc.createElement("head");
  var customAttributes = doc.createElement("customAttributes");
  var body = doc.createElement("body");
  var seq = doc.createElement("seq");

  //get all tests
  //add to custom attributes of SMIL filee
  var elseTest = customTestElement("else", "ELSE", "", "else");
  customAttributes.appendChild(elseTest);

  var tests = document.getElementsByClassName("if");

  for (var l = 0; l < tests.length; l++) {
    if (!tests[l].className.includes("hidden")) {
      var newTest = addTest(tests[l]);
      if (newTest) customAttributes.appendChild(newTest);
    }
  }

  for (var i = 0; i < elements.length; i++) {
    var newElement = addElement(elements[i]);
    if (newElement) seq.appendChild(newElement);
  }
  head.appendChild(customAttributes);
  if (tests.length != 0) smil.appendChild(head);
  body.appendChild(seq);
  smil.appendChild(body);
  doc.appendChild(smil);
  xText = xmlToString(doc);
  return xText;
}

export function createElementArray() {
  var elements = [];

  var starting = document.getElementById(startingId);

  var current = starting;
  var finished = false;

  var broken = false;
  var count = 0;

  while (!finished) {
    if (count++ > 100000) {
      count = 0;
      broken = true;
      break;
    }

    elements.push(current);
    var nextId = current.getAttribute("nextid");
    var endSwitch = current.getAttribute("endswitch");
    var endPar = current.getAttribute("endpar");

    if (nextId != null && nextId != undefined) {
      var current = document.getElementById(nextId);
    } else if (endSwitch) {
      var current = document.getElementById(endSwitch);
    } else if (endPar) {
      var current = document.getElementById(endPar);
    } else {
      finished = true;
    }
  }
  if (broken) return null;
  return elements;
}

function customTestElement(id, merchant, action, value) {
  var customTest = doc.createElement("customTest");
  customTest.setAttribute("id", id);
  customTest.setAttribute("relatedMerchant", merchant);

  value = value.toLowerCase();
  value = value.replace("usb charging", "USB charging");

  if (action == null || action == undefined || action == "none") action = "";

  customTest.setAttribute("relatedAction", action);

  var someValues = value.split("/");
  for (var v = 0; v < someValues.length; v++) {
    var acceptedValue = doc.createElement("acceptedValue");
    acceptedValue.innerHTML = someValues[v];
    customTest.appendChild(acceptedValue);
  }

  return customTest;
}

function addTest(elmt) {
  var inputs = elmt.getElementsByTagName("input");
  var selects = elmt.getElementsByTagName("select");

  var testName = inputs[0].value;
  var merchant = selects[0].value;
  var action = selects[1].value;
  var acceptedValues = inputs[1].value;

  return customTestElement(testName, merchant, action, acceptedValues);
}

//converts element on canvas to the correct SMIL
function addElement(elmt) {
  var i = elmt.id;
  var type = elmt.className;

  if (type.includes("audio")) {
    var value = getAudioElmtFile(elmt).name;

    var timingObj = {};
    var start = elmt.getElementsByClassName("start")[0].value;
    var dur = elmt.getElementsByClassName("dur")[0].value;

    var endSecs = getEndTimeSecs(start, dur);

    timingObj.start = minsToSecs(start);
    timingObj.end = endSecs;

    return audioElement(value, timingObj);
  } else if (type.includes("tts")) {
    var value = elmt.getElementsByTagName("textarea")[0].value;

    return recursiveTextElement(elmt);
  } else if (type.includes("dynquery")) {
    return recursiveTextElement(elmt);
  } else if (type.includes("switch")) {
    var par = doc.createElement("par");
    var mySwitch = doc.createElement("switch");
    var endSwitch = elmt.getAttribute("endSwitch");

    var childrenIds = arrayFromString(elmt.getAttribute("children"));

    var seq = doc.createElement("seq"); //create seq          add else seq
    seq.setAttribute("customTest", "else");

    for (var t = 0; t < childrenIds.length; t++) {
      var child = document.getElementById(childrenIds[t]);
      var selectId = elmt.id + "line" + child.id + "select";
      var customTestValue = document.getElementById(selectId).value;

      if (customTestValue == "Default") {
        var atEnd = false;
        while (!atEnd) {
          var newElement = addElement(child);
          if (newElement) seq.appendChild(newElement);
          var nextId = child.getAttribute("nextid");
          if (nextId != endSwitch) {
            if (child.className.includes("switch")) {
              var endSwitchId = child.getAttribute("endswitch");
              child = document.getElementById(endSwitchId);
            } else if (child.className.includes("par")) {
              var endParId = child.getAttribute("endpar");
              child = document.getElementById(endParId);
            } else {
              child = document.getElementById(nextId);
            }

            if (!child) atEnd = true;
          } else {
            atEnd = true;
          }
        }
        childrenIds.splice(t, 1); //remove from array with splice
      }
    }
    mySwitch.appendChild(seq);

    for (var t = 0; t < childrenIds.length; t++) {
      //add all other seqs
      var seq = doc.createElement("seq"); //create seq

      var child = document.getElementById(childrenIds[t]);
      var selectId = elmt.id + "line" + child.id + "select"; //get select id

      var customTestValue = document.getElementById(selectId).value; //get custom test value from select
      if (customTestValue == "Default") customTestValue = "else";
      seq.setAttribute("customTest", customTestValue);

      var atEnd = false;
      while (!atEnd) {
        var newElement = addElement(child);
        if (newElement) seq.appendChild(newElement);
        var nextId = child.getAttribute("nextid");
        if (nextId != endSwitch) {
          if (child.className.includes("switch")) {
            var endSwitchId = child.getAttribute("endswitch");
            child = document.getElementById(endSwitchId);
          } else if (child.className.includes("par")) {
            var endParId = child.getAttribute("endpar");
            child = document.getElementById(endParId);
          } else {
            child = document.getElementById(nextId);
          }

          if (!child) atEnd = true;
        } else {
          atEnd = true;
        }
      }
      mySwitch.appendChild(seq);
    }

    par.appendChild(mySwitch);
    return par;
  } else if (type.includes("par")) {
    // if element is parallel element
    var par = doc.createElement("par");

    var endPar = elmt.getAttribute("endPar"); //find corresponding end element
    var childrenIds = arrayFromString(elmt.getAttribute("children")); //get elements inside parallel element

    //for each child
    for (var t = 0; t < childrenIds.length; t++) {
      var child = document.getElementById(childrenIds[t]);

      //define seq here
      var seq = doc.createElement("seq");

      var atEnd = false;
      while (!atEnd) {
        var newElement = addElement(child);
        if (newElement) seq.appendChild(newElement);

        var nextId = child.getAttribute("nextid");
        if (nextId != endPar) {
          //add this for par here and for switch element as well TODO
          if (child.className.includes("switch")) {
            var endSwitchId = child.getAttribute("endswitch");
            child = document.getElementById(endSwitchId);
          } else if (child.className.includes("par")) {
            var endParId = child.getAttribute("endpar");
            child = document.getElementById(endParId);
          } else {
            child = document.getElementById(nextId);
          }

          if (!child) atEnd = true;
        } else {
          atEnd = true;
        }
      } //end of atEnd
      par.appendChild(seq);
    } //end of for loop

    return par;
  } else if (type.includes("pause")) {
    var value = elmt.getElementsByTagName("input")[0].value;
    return pauseElement(value);
  } else if (type.includes("vibrate")) {
    var value = elmt.getElementsByTagName("input")[0].value;
    return vibrateElement(value);
  } else {
    return null;
  }
}

function audioElement(source, timingObj) {
  var audio = doc.createElement("audio");
  audio.setAttribute("volLeft", "0.2");
  audio.setAttribute("volRight", "0.2");
  audio.setAttribute("src", "AUDIO/" + source);

  if (timingObj.start) audio.setAttribute("clipBegin", timingObj.start);
  if (timingObj.end) audio.setAttribute("clipEnd", timingObj.end);

  return audio;
}

function ttsElement(words) {
  var audio = doc.createElement("audio");
  audio.setAttribute("type", "tts");
  audio.setAttribute("volLeft", "0.2");
  audio.setAttribute("volRight", "0.2");

  var dynscript = doc.createElement("dynscript");
  var text = doc.createElement("text");

  text.innerHTML = words;

  dynscript.appendChild(text);
  audio.appendChild(dynscript);
  return audio;
}

function dynqueryElement(merchantId, relatedAction) {
  var audio = doc.createElement("audio");
  audio.setAttribute("type", "tts");
  audio.setAttribute("volLeft", "0.2");
  audio.setAttribute("volRight", "0.2");
  var dynscript = doc.createElement("dynscript");

  var dynquery = doc.createElement("dynquery");
  dynquery.setAttribute("merchantId", merchantId);
  if (
    relatedAction == null ||
    relatedAction == undefined ||
    relatedAction == "none"
  )
    relatedAction = "";
  dynquery.setAttribute("relatedAction", relatedAction);

  dynscript.appendChild(dynquery);
  audio.appendChild(dynscript);
  return audio;
}

function justTtsElement(words) {
  var text = doc.createElement("text");
  text.innerHTML = words;
  return text;
}

function justDynqueryElement(merchantId, relatedAction) {
  var dynquery = doc.createElement("dynquery");
  dynquery.setAttribute("merchantId", merchantId);
  if (
    relatedAction == null ||
    relatedAction == undefined ||
    relatedAction == "none"
  )
    relatedAction = "";
  dynquery.setAttribute("relatedAction", relatedAction);
  return dynquery;
}

function textAudioElement(contents) {
  var audio = doc.createElement("audio");
  audio.setAttribute("type", "tts");
  audio.setAttribute("volLeft", "0.2");
  audio.setAttribute("volRight", "0.2");
  audio.appendChild(contents);
  return audio;
}

function pauseElement(time) {
  var seq = doc.createElement("seq");
  seq.setAttribute("inTime", time + "sec");
  return seq;
}

function vibrateElement(time) {
  var vibrate = doc.createElement("vibrate");
  vibrate.setAttribute("duration", time);
  return vibrate;
}

//groups tts and dynqueries next to each other together into one element
function recursiveTextElement(parent) {
  var finished = false;

  if (removed.indexOf(parent.id) != -1) {
    return null;
  } else {
    var dynscript = doc.createElement("dynscript");
    var child = parent;

    while (!finished) {
      if (child.className.includes("tts")) {
        var textBox = child.getElementsByTagName("textarea")[0];
        var value = textBox.value;
        var content = justTtsElement(value);
      } else if (child.className.includes("dynquery")) {
        var selects = child.getElementsByTagName("select");

        var merchant = selects[0].value;
        var action = selects[1].value;

        var content = justDynqueryElement(merchant, action);
      }
      dynscript.appendChild(content);
      removed.push(child.id);
      child = nextElement(child);

      if (child == null) {
        finished = true;
      } else if (
        !child.className.includes("tts") &&
        !child.className.includes("dynquery")
      ) {
        finished = true;
      }
    } //end of while loop

    return textAudioElement(dynscript);
  }
}
//gets next element of an element
function nextElement(current) {
  var nextId = current.getAttribute("nextid");
  var next = document.getElementById(nextId);
  return next;
}

//gets previous element of an element
function lastElement(current) {
  var lastId = current.getAttribute("previd");
  var last = document.getElementById(lastId);
  return last;
}

function arrayFromString(string) {
  var array = JSON.parse("[" + string + "]");
  return array;
}

function createFile() {
  var xmltext = xText;
  var pom = document.createElement("a");

  var filename = "smil.xml";
  var pom = document.createElement("a");
  var bb = new Blob([xmltext], { type: "text/xml" });

  pom.setAttribute("href", window.URL.createObjectURL(bb));
  pom.setAttribute("download", filename);

  pom.dataset.downloadurl = ["text/xml", pom.download, pom.href].join(":");
  pom.draggable = true;
  pom.classList.add("dragout");

  pom.click();
}

export function xmlToString(xmlData) {
  var xmlString;
  //IE
  if (window.ActiveXObject) {
    xmlString = xmlData.xml;
  }
  // code for Mozilla, Firefox, Opera, etc.
  else {
    xmlString = new XMLSerializer().serializeToString(xmlData);
  }
  xmlString = prettifyXml(xmlString);
  return xmlString;
}

function prettifyXml(sourceXml) {
  var xmlDoc = new DOMParser().parseFromString(sourceXml, "application/xml");
  var xsltDoc = new DOMParser().parseFromString(
    [
      // describes how we want to modify the XML - indent everything
      '<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">',
      '  <xsl:strip-space elements="*"/>',
      '  <xsl:template match="para[content-style][not(text())]">', // change to just text() to strip space in text nodes
      '    <xsl:value-of select="normalize-space(.)"/>',
      "  </xsl:template>",
      '  <xsl:template match="node()|@*">',
      '    <xsl:copy><xsl:apply-templates select="node()|@*"/></xsl:copy>',
      "  </xsl:template>",
      '  <xsl:output indent="yes"/>',
      "</xsl:stylesheet>",
    ].join("\n"),
    "application/xml"
  );

  var xsltProcessor = new XSLTProcessor();
  xsltProcessor.importStylesheet(xsltDoc);
  var resultDoc = xsltProcessor.transformToDocument(xmlDoc);
  var resultXml = new XMLSerializer().serializeToString(resultDoc);
  return resultXml;
}
