from django.db import models
from django.contrib.auth.models import User

class Report(models.Model):
    lat = models.FloatField()
    long = models.FloatField()
    decibelLevel = models.FloatField()
    submitter = models.ForeignKey(User, related_name='submitter', default=None, null=True)
    submission_time = models.DateTimeField(auto_now=True)
    
    def __str__(self):
        return self.submission_time.strftime("%Y-%m-%d %H:%M:%S")
