CC := clang++
EMCC := em++
CFLAGS := -c -Wall -std=c++14
EMCCFLAGS := -Wall -std=c++14 --emrun
LDFLAGS := -Wl,-lglut -Wl,-lGLEW -Wl,-lGL
EMLDFLAGS := --emrun
BUILD_DIR := build
ASSET_DIR := assets
SRC_DIR := src
SOURCES := $(wildcard $(SRC_DIR)/*.cpp)
OBJECTS := $(addprefix $(BUILD_DIR)/,$(SOURCES:$(SRC_DIR)/%.cpp=%.o))
BC_OBJECTS := $(OBJECTS:%.o=%.bc)
EXECUTABLE := $(BUILD_DIR)/diamond
JS_EXECUTABLE := $(EXECUTABLE).js
HTML_PAGE := $(EXECUTABLE).html

all: $(EXECUTABLE) $(JS_EXECUTABLE)

$(JS_EXECUTABLE): $(HTML_PAGE)
	@

$(HTML_PAGE): $(BC_OBJECTS)
	$(EMCC) $(BC_OBJECTS) $(EMLDFLAGS) -o $@ 

$(EXECUTABLE): $(OBJECTS)
	$(CC) $(OBJECTS) $(LDFLAGS) -o $@

$(BC_OBJECTS): $(BUILD_DIR)/%.bc: $(SRC_DIR)/%.cpp | $(BUILD_DIR)
	$(EMCC) $(EMCCFLAGS) $< -o $@

$(OBJECTS): $(BUILD_DIR)/%.o: $(SRC_DIR)/%.cpp | $(BUILD_DIR)
	echo $<
	echo $@
	$(CC) $(CFLAGS) $< -o $@

$(BUILD_DIR):
	mkdir $(BUILD_DIR)

clean:
	rm $(OBJECTS) $(EXECUTABLE) $(BC_OBJECTS) $(JS_EXECUTABLE) $(HTML_PAGE)

run: $(EXECUTABLE)
	$(EXECUTABLE)

emrun: $(HTML_PAGE)
	emrun $(HTML_PAGE)
