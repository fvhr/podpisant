#!/bin/sh
cd /podpisant.api/app || { echo "ERROR: Cannot cd to /podpisant.api"; exit 1; }
python3 main.py