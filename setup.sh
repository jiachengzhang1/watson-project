#!/bin/bash

echo "Downloading files..."
wget -q --show-progress -O index_raw.zip https://www.dropbox.com/s/wuib5n64rzmrqml/index_raw.zip?dl=0
wget -q --show-progress -O index.zip https://www.dropbox.com/s/f084mqldxt6l6al/index.zip?dl=0
wget -q --show-progress -O questions.txt https://www.dropbox.com/s/wgmlv8v5h7adjlo/questions.txt?dl=0

echo "Upzip files"
unzip index_raw.zip
unzip index.zip

echo "Clean up"
rm index_raw.zip
rm index.zip

echo "Done"
