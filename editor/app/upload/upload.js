//uploads from uploaded zip file

import { loadEditorFile } from "../editorFile/loadEditorFile.js";

import { loadManifestFile } from "../zip/readManifest.js";

import { setManifestImageFile } from "../zip/tempFiles.js";
import { addTempAudioFile } from "../zip/tempFiles.js";

var zip = new JSZip();

export function uploadZipFile() {
  var zipUpload = document.getElementById("uploadZipInput");
  var file = zipUpload.files[0];
  zip.loadAsync(file)["then"](function(unzipped) {
    var unzippedFiles = unzipped.files;

    if (unzippedFiles["editorFile.json"])
      readEditorFile(unzippedFiles["editorFile.json"]);
    else
      alert(
        "This file could not be opened. This editor can only open .zip files that were created by this editor."
      );

    readManifestFile(unzippedFiles["manifest.json"]);

    if (unzippedFiles["IMAGES/large.jpg"]) readImages(unzippedFiles);
    readAudio(unzippedFiles);
  });
}

function readEditorFile(file) {
  var arry = file["_data"]["compressedContent"];
  var string = new TextDecoder().decode(arry);

  loadEditorFile(JSON.parse(string));
}

function readManifestFile(file) {
  var arry = file["_data"]["compressedContent"];
  var string = new TextDecoder().decode(arry);

  loadManifestFile(JSON.parse(string));
}

//load manifest image into browser
function readImages(unzippedFiles) {
  var imageNameFile = unzippedFiles["IMAGES/manifestImageName"];
  var arry = imageNameFile["_data"]["compressedContent"];

  var imageName = new TextDecoder().decode(arry);

  var imageFile = unzippedFiles["IMAGES/" + imageName];
  var arry = imageFile["_data"]["compressedContent"];
  var blob = new Blob([arry], { type: "image/jpg" });

  var newFile = new File([blob], imageName);
  setManifestImageFile(newFile);
}
//load audio into browser
function readAudio(files) {
  var keys = Object.keys(files);
  for (var i = 0; i < keys.length; i++) {
    if (keys[i].includes("AUDIO/")) {
      var fileName = keys[i].replace("AUDIO/", "");
      if (fileName !== "") {
        var audioFile = getAudioFile(fileName, files);
        addTempAudioFile(audioFile);
      }
    }
  }
}

function getAudioFile(fileName, files) {
  var arry = files["AUDIO/" + fileName]["_data"]["compressedContent"];
  var blob = new Blob([arry], { type: "audio/mpeg" });
  var newFile = new File([blob], fileName, { type: "audio/mpeg" });

  return newFile;
}
