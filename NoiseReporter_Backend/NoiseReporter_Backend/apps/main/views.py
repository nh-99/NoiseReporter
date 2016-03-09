from django.shortcuts import render
from django.views.generic import TemplateView

class BaseTemplateView(TemplateView):
    template_name = "base.html"

    def get_context_data(self, *args, **kwargs):
        context = super(BaseTemplateView, self).get_context_data(*args, **kwargs)
        return context