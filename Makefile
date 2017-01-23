SRC_DIR := src
BUILD_DIR := build
ASSET_DIR := assets
SOURCES := $(wildcard $(SRC_DIR)/*.cpp)

CC := clang++
CFLAGS := -c -g -Wall -std=c++14
LDFLAGS := -Wl,-lGL -Wl,-lGLEW -Wl,-lglfw
OBJECTS := $(addprefix $(BUILD_DIR)/,$(SOURCES:$(SRC_DIR)/%.cpp=%.o))

EXECUTABLE := $(BUILD_DIR)/diamond

EMCC := em++
EMCCFLAGS := -c -g -Wall -std=c++14
EMLDFLAGS := -s USE_GLFW=3
EMRUNFLAGS := --emrun --preload-file $(ASSET_DIR)/favicon.ico
BC_OBJECTS := $(OBJECTS:%.o=%.bc)

HTML_PAGE := $(EXECUTABLE).html
EMRUN_FILES:= $(EXECUTABLE).js $(HTML_PAGE) $(EXECUTABLE).data

all: $(EXECUTABLE) webexec

.PHONY: webexec
webexec: $(BC_OBJECTS) $(DATADIRS)
	$(EMCC) $(EMLDFLAGS) $(EMRUNFLAGS) $(BC_OBJECTS) -o $(HTML_PAGE)

$(BC_OBJECTS): $(BUILD_DIR)/%.bc: $(SRC_DIR)/%.cpp | $(BUILD_DIR)
	$(EMCC) $(EMCCFLAGS) $< -o $@

$(EXECUTABLE): $(OBJECTS)
	$(CC) $(LDFLAGS) $(OBJECTS) -o $@

$(OBJECTS): $(BUILD_DIR)/%.o: $(SRC_DIR)/%.cpp | $(BUILD_DIR)
	$(CC) $(CFLAGS) $< -o $@

$(BUILD_DIR):
	mkdir $(BUILD_DIR)

$(ASSET_DIR):
	@

clean:
	rm $(OBJECTS) $(EXECUTABLE) $(BC_OBJECTS) $(EMRUN_FILES)

run: $(EXECUTABLE)
	$(EXECUTABLE)

emrun: $(HTML_PAGE)
	emrun $(HTML_PAGE)
