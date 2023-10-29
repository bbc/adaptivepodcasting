//creates a zip file to be downloaded

import { createSMILFile } from "../smil/smilCreation.js";
import { checkAllConnected } from "../smil/canBuild.js";

import { makeManifest } from "../zip/manifestBuilding.js";

import { getImages } from "../zip/imageResizing.js";

import { getManifestImageFile } from "../zip/tempFiles.js";
import { getAudioElmtFile } from "../zip/tempFiles.js";

import { makeEditorFile } from "../editorFile/makeEditorFile.js";

import { makeDownloadingProgressButton, updateDownloadingProgressButton, restoreDownloadButton } from "../interface/changingButtons.js";

export var currentlyDownloading = false;

export function makeZipFile() {
  if (!checkAllConnected()) return;
  currentlyDownloading = true;
  makeDownloadingProgressButton();

  var zip = new JSZip();

  var title = document.getElementById("podcastTitleInput").value;

  var smil = createSMILFile();
  zip.file("smil.xml", smil);

  var audioFolder = zip.folder("AUDIO");
  audioFolder = getAudioFiles(audioFolder);

  var imageFolder = zip.folder("IMAGES");
  imageFolder = getImageFiles(imageFolder);

  var manifest = makeManifest();
  zip.file("manifest.json", manifest);

  var editorFile = makeEditorFile();
  zip.file("editorFile.json", editorFile);

  zip.generateAsync({ type: "blob" },function updateCallback(metadata) {
     
    updateDownloadingProgressButton(metadata.percent.toFixed(2));
    }).then(function (content) {
    currentlyDownloading = false;
    restoreDownloadButton();
    saveAs(content, title + ".zip");
  });
}

function getAudioFiles(audioFolder) {
  var audios = document.querySelectorAll(".block.audio:not(.hidden)");

  for (var i = 0; i < audios.length; i++) {
    var file = getAudioElmtFile(audios[i]);
    audioFolder.file(file.name, file);
  }

  return audioFolder;
}

function getImageFiles(imageFolder) {
  var imageFile = getManifestImageFile();

  if (imageFile) {
    imageFolder.file("small.jpg", getImages("small"));
    imageFolder.file("large.jpg", getImages("large"));
    imageFolder.file("medium.jpg", getImages("medium"));

    imageFolder.file(imageFile.name, imageFile);

    imageFolder.file("manifestImageName", imageFile.name);
  }
}
