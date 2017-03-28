'use strict';

var g ={
  SCALE: 1.0,
  SIZE: 3.0,
};

var m4 = twgl.m4;
var v3 = twgl.v3;

class Point {
  constructor(x, y, z) {
    this.v = v3.create(x, y, z);
  }
}

class Tile {
  constructor(size, corner_x, corner_y) {
    this.size = size;
    let hmap = new Array(size);
    for(let i = 0; i < size; i++) {
      let col = new Array(size);
      for(let j = 0; j < size; j++) {
        let p = new Point(corner_x + i, 0.0, corner_y + j);
        p.v[1] = g.SCALE * noise.simplex2(p.v[0], p.v[2]);
        col[j] = p;
      }
      hmap[i] = col;
    }
    this.hmap = hmap;
  }
}

class Terrain {
  constructor(size) {
    this.size = size;
    let tiles = new Array(size);
    for(let i = 0; i < size; i++) {
      let col = new Array(size);
      for(let j = 0; j < size; j++) {
        col[j] = new Tile(size, i * size, j * size);
      }
      tiles[i] = col;
    }
    this.tiles = tiles;
  }

  toMesh() {
    let vertices = new Array();
    let indices = new Array();
    let normals = new Array();
    let size = this.size;
    let tiles = this.tiles;
    let tileSize = Math.pow(this.size, 2);
    let tileRowSize = Math.pow(this.size, 3);
    let index = 0;
    //vertices
    for(let i = 0; i < size; i++) {
      for(let j = 0; j < size; j++) {
        for(let k = 0; k < size; k++) {
          for(let l = 0; l < size; l++) {
            let p = tiles[i][j].hmap[k][l];
            vertices.push(p.v[0]);
            vertices.push(p.v[1]);
            vertices.push(p.v[2]);
            p.i = index++;
          }
        }
      }
    }
    //tile-internal tris
    for(let i = 0; i < size; i++) {
      for(let j = 0; j < size; j++) {
        for(let k = 0; k < size - 1; k++) {
          for(let l = 0; l < size - 1; l++) {
            let tile = this.tiles[i][j].hmap;
            let p1 = tile[k + 0][l + 0];
            let p2 = tile[k + 0][l + 1];
            let p3 = tile[k + 1][l + 0];
            indices.push(p1.i);
            indices.push(p2.i);
            indices.push(p3.i);
            let p4 = tile[k + 0][l + 1];
            let p5 = tile[k + 1][l + 1];
            let p6 = tile[k + 1][l + 0];
            indices.push(p4.i);
            indices.push(p5.i);
            indices.push(p6.i);
          }
        }
      }
    }
    //bottoms
    for(let i = 0; i < size; i++) {
      for(let j = 0; j < size - 1; j++) {
        for(let k = 0; k < size - 1; k++) {
          let p1 = tiles[i][j + 0].hmap[k + 0][size - 1];
          let p2 = tiles[i][j + 1].hmap[k + 0][0];
          let p3 = tiles[i][j + 0].hmap[k + 1][size - 1];
          indices.push(p1.i);
          indices.push(p2.i);
          indices.push(p3.i);
          let p4 = tiles[i][j + 1].hmap[k + 0][0];
          let p5 = tiles[i][j + 1].hmap[k + 1][0];
          let p6 = tiles[i][j + 0].hmap[k + 1][size - 1];
          indices.push(p4.i);
          indices.push(p5.i);
          indices.push(p6.i);
        }
      }
    }
    //sides
    for(let i = 0; i < size - 1; i++) {
      for(let j = 0; j < size; j++) {
        for(let k = 0; k < size - 1; k++) {
          let p1 = tiles[i][j].hmap[size - 1][k];
          let p2 = tiles[i][j].hmap[size - 1][k + 1];
          let p3 = tiles[i + 1][j].hmap[0][k];
          indices.push(p1.i);
          indices.push(p2.i);
          indices.push(p3.i);
          let p4 = tiles[i][j].hmap[size - 1][k + 1];
          let p5 = tiles[i + 1][j].hmap[0][k + 1];
          let p6 = tiles[i + 1][j].hmap[0][k];
          indices.push(p4.i);
          indices.push(p5.i);
          indices.push(p6.i);
        }
      }
    }
    //far corner
    for(let i = 0; i < size - 1; i++) {
      for(let j = 0; j < size - 1; j++) {
        let p1 = tiles[i + 0][j + 0].hmap[size - 1][size - 1];
        let p2 = tiles[i + 0][j + 1].hmap[size - 1][0];
        let p3 = tiles[i + 1][j + 0].hmap[0][size - 1];
        indices.push(p1.i);
        indices.push(p2.i);
        indices.push(p3.i);
        let p4 = tiles[i + 0][j + 1].hmap[size - 1][0];
        let p5 = tiles[i + 1][j + 1].hmap[0][0];
        let p6 = tiles[i + 1][j + 0].hmap[0][size - 1];
        indices.push(p4.i);
        indices.push(p5.i);
        indices.push(p6.i);
      }
    }
    return {position: {numComponents: 3, data: vertices}, indices: {numComponents: 3, data: indices}};
  }
}

function main() {
  noise.seed(16);
  let gl = twgl.getWebGLContext(document.getElementById("glcanvas"));
  let program_info = twgl.createProgramInfo(gl, ["vshader", "fshader"]);
  gl.clearColor(0.0, 0.0, 0.0, 1.0);

  let unifs = {
    u_model: m4.identity(),
    u_view: m4.identity(),
    u_persp: m4.identity(),
  };

  //load a chunk
  let chunk = new Terrain(3);

  let mesh = chunk.toMesh();
  let vertex_buffer = gl.createBuffer();
  gl.bindBuffer(gl.ARRAY_BUFFER, vertex_buffer);
  gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(mesh.position.data), gl.STATIC_DRAW);
  let index_buffer = gl.createBuffer();
  gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, index_buffer);
  gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(mesh.indices.data), gl.STATIC_DRAW);

  console.log(chunk.toMesh());
  console.log(program_info);
  let center_offset = -1 * (Math.pow(g.SIZE, 2) / 2);
  unifs.u_model = m4.translation([center_offset, 0.0, center_offset]);

  gl.enableVertexAttribArray(0);
  gl.vertexAttribPointer(0, 3, gl.FLOAT, false, 0, 0);
  gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, index_buffer);

  let draw = time => {
    time *= 0.001;
    twgl.resizeCanvasToDisplaySize(gl.canvas);
    gl.viewport(0, 0, gl.canvas.width, gl.canvas.height);
    gl.enable(gl.DEPTH_TEST);
    gl.enable(gl.CULL_FACE);
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);

    //[Math.cos(angle) * 5, 0.0, Math.sin(angle) * 5],
    let angle = time % (Math.PI * 2);
    m4.perspective(90.0 * (Math.PI / 180), gl.canvas.clientWidth / gl.canvas.clientHeight, 0.1, 100.0, unifs.u_persp);
    let camera = m4.lookAt(
        [1.0, 7.0, 1.0],
        [0.0, 0.0, 0.0],
        [0.0, 1.0, 0.0]);
    m4.inverse(camera, unifs.u_view);

    gl.useProgram(program_info.program);
    twgl.setUniforms(program_info, unifs);
    gl.drawElements(gl.TRIANGLES, mesh.indices.data.length, gl.UNSIGNED_SHORT, 0);
    if(gl.getError() != 0) {
      console.log(gl.getError());
    }
    requestAnimationFrame(draw);
  }
  requestAnimationFrame(draw);
}

function calculateFaceNormal(p1, p2, p3) {
  let u = v3.subtract(p2, p1);
  let v = v3.subtract(p3, p1);
  return v3.cross(u, v);
}
