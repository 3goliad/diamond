CC := clang++
EMCC := tools/emsdk/emscripten/master/em++
CFLAGS := -c -Wall -std=c++14
EMCCFLAGS := -Wall --emrun
LDFLAGS := -Wl,-lglfw
BUILD_DIR := build
ASSET_DIR := assets
SRC_DIR := src
SOURCES := $(wildcard $(SRC_DIR)/*.cpp)
OBJECTS := $(addprefix $(BUILD_DIR)/,$(SOURCES:$(SRC_DIR)/%.cpp=%.o))
EXECUTABLE := $(BUILD_DIR)/diamond

all: $(EXECUTABLE)

$(EXECUTABLE): $(OBJECTS) | $(ASSETS); echo $(SOURCES); echo $(OBJECTS)
	$(CC) $(OBJECTS) $(LDFLAGS) -o $@

$(OBJECTS): $(BUILD_DIR)/%.o: $(SRC_DIR)/%.cpp | $(BUILD_DIR)
	$(CC) $(CFLAGS) $(SOURCES) -o $@

#$(ASSETS):
#	cp -r $(ASSET_DIR) $(BUILD_DIR)/$(ASSET_DIR)

$(BUILD_DIR):
	mkdir $(BUILD_DIR)

clean:
	rm $(OBJECTS) $(EXECUTABLE)

run:
	$(EXECUTABLE)
