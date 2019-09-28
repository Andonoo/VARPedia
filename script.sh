#!/bin/bash
xargs rm -rf <<< $(find ./Creations -mindepth 1 -maxdepth 1 -type d '!' -exec sh -c 'ls -1 "{}"|egrep -i -q "*.(mp4)$"' ';' -print)
