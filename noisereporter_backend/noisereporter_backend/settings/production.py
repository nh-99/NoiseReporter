from noisereporter_backend.settings import *

# Database
# https://docs.djangoproject.com/en/1.8/ref/settings/#databases

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'writ',
        'USER': os.environ.get("NOISEREPORT_DB_USER", ''),
        'PASSWORD': os.environ.get("NOISEREPORT_DB_PASSWORD", ''),
    }
}
