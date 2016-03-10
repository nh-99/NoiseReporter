#!/usr/bin/env python3
import os
import sys

if __name__ == "__main__":
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "noisereporter_backend.settings.active")
    
#    BASE_DIR = os.path.dirname(os.path.dirname(__file__))
    BASE_DIR = "./"
    sys.path.insert(0, os.path.join(BASE_DIR, 'noisereporter_backend/apps'))

    from django.core.management import execute_from_command_line

    execute_from_command_line(sys.argv)
