"use strict";

var glmatrix = require('gl-matrix');

exports.toRadians = function(degrees) {
  return glmatrix.glMatrix.toRadian(degrees);
};
