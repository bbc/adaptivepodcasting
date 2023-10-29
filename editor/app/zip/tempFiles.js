//upload audio and images to a tempory holding space to be used by the editor
import { onImageUploaded } from "../zip/imageResizing.js";

var tempImageFile;

var tempAudioFiles = {};

export function getManifestImageFile() {
  var imageInput = document.getElementById("imageInput");

  if (imageInput.files.length > 0) {
    return imageInput.files[0];
  } else if (tempImageFile !== null) {
    return tempImageFile;
  } else return null;
}

export function setManifestImageFile(file) {
  tempImageFile = file;
  onImageUploaded(tempImageFile);
}

export function deleteManifestImageFile() {
  tempImageFile = null;
}

export function addTempAudioFile(file) {
  tempAudioFiles[file.name] = file;
}

//returns the audio file of an audio element. Checks for on uploaded on element input, then for a tempory file
//with the name in the element
export function getAudioElmtFile(elmt) {
  var input = elmt.getElementsByTagName("input")[0];
  var uploadedAudioName =
    elmt.getElementsByClassName("uploadedAudioName")[0].textContent; //inner text removes whitespace
  if (input.files.length > 0) {
    return input.files[0];
  } else if (uploadedAudioName !== "") {
    var tempFile = tempAudioFiles[uploadedAudioName];
    return tempFile;
  } else {
    return null;
  }
}
