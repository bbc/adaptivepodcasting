//changing buttons text etc, eg play button to stop button

var previewText = document.getElementById("previewText");
var playIcon = document.getElementById("playIcon");
var createFileText = document.getElementById("createFileText");
var createFileButton = document.getElementById("createFileButton");
var skipButton = document.getElementById("skipButton");

export function makeStopPreviewButton() {
  previewText.innerText = "Stop Preview";
  playIcon.innerText = "stop";
}

export function restorePreviewButton() {
  previewText.innerText = "Preview File";
  playIcon.innerText = "play_arrow";
}

export function makeDownloadingProgressButton() {
  createFileText.innerText = "Loading file for download...";
}

export function updateDownloadingProgressButton(progress){
  createFileText.innerText = "Loading file for download - "+progress+"%";
}

export function restoreDownloadButton() {
  createFileText.innerText = "Create File";
}

export function toggleSkipButton(showing) {
  if(showing)
    skipButton.className = skipButton.className.replace(" hidden", "");
  else
    skipButton.className = skipButton.className + " hidden";
}

