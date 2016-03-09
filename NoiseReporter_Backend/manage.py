#!/usr/bin/env python
import os
import sys

if __name__ == "__main__":
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "NoiseReporter_Backend.settings.active")
    
    BASE_DIR = os.path.dirname(os.path.dirname(__file__))
    sys.path.insert(0, os.path.join(BASE_DIR, 'NoiseReporter_Backend/apps'))

    from django.core.management import execute_from_command_line

    execute_from_command_line(sys.argv)
