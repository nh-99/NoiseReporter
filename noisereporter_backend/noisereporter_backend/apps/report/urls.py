from django.conf.urls import url

from rest_framework.urlpatterns import format_suffix_patterns

from . import views
from . import api

urlpatterns = [
]

apipatterns = [
    url(r'^$', api.CreateReport.as_view()),
]

urlpatterns = urlpatterns + format_suffix_patterns(apipatterns)
