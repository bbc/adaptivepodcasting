//resize the manifest image to large, medium and small files
import { getManifestImageFile } from "../zip/tempFiles.js";

var large = 1000;
var medium = 500;
var small = 256;

var imageInput = document.getElementById("imageInput");
var smallOutput = document.getElementById("outputImageSmall");
var mediumOutput = document.getElementById("outputImageMedium");
var largeOutput = document.getElementById("outputImageLarge");

export function setOnImageUploaded() {
  document.getElementById("imageInput").onchange = function(e) {
    onImageUploaded(imageInput.files[0]);
  };

  smallOutput.addEventListener("load", function(e) {});
}

export function onImageUploaded(file) {
  refreshOutputs();
  smallOutput = document.getElementById("outputImageSmall");
  mediumOutput = document.getElementById("outputImageMedium");
  largeOutput = document.getElementById("outputImageLarge");

  smallOutput.setAttribute("src", URL.createObjectURL(file));
  mediumOutput.setAttribute("src", URL.createObjectURL(file));
  largeOutput.setAttribute("src", URL.createObjectURL(file));

  setTimeout(resizeOutput, 1000);
}

function refreshOutputs() {
  var spawn = document.getElementById("spawn");
  var sizes = ["Small", "Medium", "Large"];
  var outputs = [smallOutput, mediumOutput, largeOutput];

  for (var i = 0; i < sizes.length; i++) {
    outputs[i].remove();
    var newElmt = document.createElement("img");
    newElmt.id = "outputImage" + sizes[i];
    newElmt.className = "hidden";
    spawn.appendChild(newElmt);
  }
}

function resizeOutput() {
  var HERMITE = new Hermite_class();
  HERMITE.resize_image("outputImageMedium", medium, medium);

  var HERMITE = new Hermite_class();
  HERMITE.resize_image("outputImageLarge", large, large);

  var HERMITE = new Hermite_class();
  HERMITE.resize_image("outputImageSmall", small, small);
}



export function getImages(size) {
  if (getManifestImageFile()) {
    if (size === "small") {
      return urlToPromise(smallOutput.src);
    } else if (size === "medium") {
      return urlToPromise(mediumOutput.src);
    } else if (size === "large") {
      return urlToPromise(largeOutput.src);
    }
  }
}

function urlToPromise(url) {
  return new Promise(function(resolve, reject) {
    JSZipUtils.getBinaryContent(url, function(err, data) {
      if (err) {
        reject(err);
      } else {
        resolve(data);
      }
    });
  });
}
