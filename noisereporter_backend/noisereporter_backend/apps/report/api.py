from rest_framework import status
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import permissions
from rest_framework.parsers import JSONParser

from .models import Report

class CreateReport(APIView):
    """
    Report a noise complaint into the DB
    """
    permission_classes = (permissions.AllowAny,)
    parser_classes = (JSONParser,)
    
    def post(self, request, format=None):
        latitude = request.data.get('lat')
        longitude = request.data.get('long')
        decibel = request.data.get('decibelLevel')
        report = Report.objects.create(lat=latitude, long=longitude, decibelLevel=decibel)
        report.save()
        return Response("done")
