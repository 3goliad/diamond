#pragma once

using std::vector;

class tile {
    vector<float> vertices[243];
    vector<float> indices[384];
    tile() {
        // generate an 8x8 grid of squares in vertices
        for (int i = 0; i < 9; i++)
        {
            int offset = i * 9 * 3;
            for (int j = 0; j < 9; j++)
            {
                vertices[offset] = (((GLfloat)i) * 0.1f) - 0.4f;
                vertices[offset + 1] = 0.0f;
                vertices[offset + 2] = (((GLfloat)j) * 0.1f) - 0.4f;
                offset += 3;
            }
        }
        // generate two triangles for each square, three vertices to a tri
        for (int i = 0; i < 8; i++)
        {
            int offset = i * 8 * 2 * 3;
            for (int j = 0; j < 8; j++)
            {
                // top tri
                indices[offset] = indices[offset + 1] indices[offset + 2]
                    // bottom tri
                    indices[offset + 3] indices[offset + 4] indices[offset + 5]
            }
        }
    }
}
