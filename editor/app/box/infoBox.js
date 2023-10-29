import { dragElement } from "../elements/elementBehaviour.js";
import { closeButtonPressed } from "../elements/elementBehaviour.js";
import { updateDynqueryDropdown } from "../elements/elementBehaviour.js";

import { merchants } from "../interface/creating.js";

import { addCreditsPerson } from "../box/manifestInterface.js";

import { uploadZipFile } from "../upload/upload.js";

import { deleteManifestImageFile } from "../zip/tempFiles.js";

import { refreshTestTips } from "../tooltips/tooltips.js";
import { onTestDropdownChange } from "../tooltips/acceptedValuesTips.js";
import { testCreatedTip } from "../tooltips/acceptedValuesTips.js";

//variable tests is used in switch dropdowns
export var tests = ["Default", "Add test..."];
var testIdCount = 0;
var infoBox;
var openInfoButton;

//inits everything on info box
export function initInfoBox() {
  infoBox = document.getElementById("infoBox");
  dragElement(infoBox); //make infoBox draggable around the canvas

  //init tab links in info box
  var tablinks = infoBox.getElementsByClassName("tablinks");
  for (var i = 0; i < tablinks.length; i++) {
    tablinks[i].addEventListener("click", function () {
      showBoxContent(event.target, event.target.getAttribute("tab"));
    });
  }
  var addPersonButton = document.getElementById("addPersonButton");
  addPersonButton.addEventListener("click", function () {
    addCreditsPerson();
  });

  addCreditsPerson(); //add first credits row now

  var zipUpload = document.getElementById("uploadZipInput");

  zipUpload.addEventListener("change", function () {
    uploadZipFile();
  });

  var deleteImageButton = document.getElementById("deleteImageButton");
  deleteImageButton.addEventListener("click", function () {
    deleteUploadedImage();
  });

  var infoCloseButton = document.getElementById("infoCloseButton");
  infoCloseButton.addEventListener("click", function () {
    closeInfoBox();
  });

  openInfoButton = document.getElementById("openInfoButton");
  openInfoButton.addEventListener("click", function () {
    openInfoBox();
  });

  assignDate();
}
//when tab is clicked, show that content in info box
export function showBoxContent(tabButton, content) {
  var options = [
    "infoContent",
    "testsContent",
    "playerContent",
    "manifestContent",
    "uploadContent",
  ];

  for (var v = 0; v < options.length; v++) {
    var elmt = document.getElementById(options[v]);
    elmt.style.display = "none";
  }
  var buttons = document.getElementsByClassName("tablinks");

  for (var w = 0; w < buttons.length; w++) {
    buttons[w].className = buttons[w].className.replace(" active", "");
  }

  var elmt = document.getElementById(content);
  elmt.style.display = "block";

  tabButton.className = tabButton.className += " active";
}

//add a test to the info box
export function addIfTest(showTestsTab) {
  const example = document.getElementById("testExample");
  const newTest = example.cloneNode(true);

  const testsContent = document.getElementById("testsContent");
  testsContent.appendChild(newTest);

  const testNameInput = newTest.getElementsByClassName("testNameExample")[0];
  testNameInput.className = "testName";

  testNameInput.addEventListener("input", function () {
    testUpdated(event.target);
  });

  assignNewTestListeners(newTest);
  if (showTestsTab) {
    showBoxContent(document.getElementById("testsTab"), "testsContent");
    infoBox.scrollIntoView();
  }

  refreshTestTips();
  testCreatedTip();
  return newTest;
}

//when a new test is created, assign js listeners for button clicks etc
function assignNewTestListeners(test) {
  test.className = test.className.replace("hidden", "");
  test.id = "test" + testIdCount;
  testIdCount++;
  const closeButton = test.getElementsByClassName("closeButtons")[0];
  if (closeButton) {
    closeButton.addEventListener("click", function () {
      closeButtonPressed(test);
      testUpdated(test);
    });
  }

  let select = test.getElementsByTagName("select")[0];

  for (var i = 0; i < merchants.length; i++) {
    var option = document.createElement("option");
    option.setAttribute("value", merchants[i]);
    option.innerHTML = merchants[i];
    select.appendChild(option);
  }

  select.addEventListener("change", function () {
    updateDynqueryDropdown(test);
    onTestDropdownChange(test);
    //the action select is remade (a new HTML elmt) in update function, so add new event listener everytime first select changes
    var actionSelect = test.getElementsByTagName("select")[1];
    actionSelect.addEventListener("change", function () {
      onTestDropdownChange(test);
    });
  });

  var avInput = test.getElementsByTagName("input")[1];
  avInput.addEventListener("click", function () {
    onTestDropdownChange(test);
  });
}

//when a test is updated, update everything that uses it
export function testUpdated(elmt) {
  var originalValue = elmt.value;

  var names = document.getElementsByClassName("testName");
  tests = [];
  for (var m = 0; m < names.length; m++) {
    tests[m] = names[m].value;
  }
  tests.unshift("Default"); //add default to start
  tests.push("Add test..."); // add add test to end

  var selects = document.getElementsByClassName("select");

  for (var k = 0; k < selects.length; k++) {
    var oldValue = selects[k].value;
    selects[k].options.length = 0;
    for (var j = 0; j < tests.length; j++) {
      var option = document.createElement("option");
      option.setAttribute("value", tests[j]);
      option.innerHTML = tests[j];
      selects[k].appendChild(option);
    }
    selects[k].value = oldValue;
  }
}

function deleteUploadedImage() {
  var imageInput = document.getElementById("imageInput");
  imageInput.className = "";
  imageInput.value = null;

  var uploadedImage = document.getElementById("uploadedImage");
  uploadedImage.className = "hidden";

  var fileNameElmt = document.getElementById("fileName");
  fileNameElmt.innerText = "";
  deleteManifestImageFile();
}

function assignDate() {
  var today = new Date();
  var dd = String(today.getDate()).padStart(2, "0");
  var mm = String(today.getMonth() + 1).padStart(2, "0"); //January is 0
  var yyyy = today.getFullYear();

  today = yyyy + "-" + mm + "-" + dd;

  var dateInput = document.getElementById("phoneDATE");
  dateInput.value = today;
}

function closeInfoBox() {
  infoBox.setAttribute("style", "display:none");
  openInfoButton.setAttribute("style", "display:block");
}

function openInfoBox() {
  openInfoButton.setAttribute("style", "display:none");

  infoBox.setAttribute("style", "display:block");
  var halfScreenWidth = window.innerWidth / 2;

  infoBox.style.left = window.scrollX + halfScreenWidth - 50 + "px";
  infoBox.style.top = window.scrollY + 50 + "px";
}
