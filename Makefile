
BASEDIR=$(PWD)

all: build-ui build

build-ui:
	cp $(BASEDIR)/resources/public/css/clock.css $(BASEDIR)/src/view/css/clock.css
	#cp $(BASEDIR)/resources/public/index.html $(BASEDIR)/src/view/index.html
	clj -A:release

ui-repl:
	clj -A:build

build:
	cargo build
