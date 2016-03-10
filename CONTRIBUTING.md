#Setting up development environment
##Cloning
To clone the project, run `git clone http://thefortress.xyz/noah/NoiseReporter` and provide your username and password.
##Getting setup
To get setup, please make sure that you have Vagrant installed. Then run `vagrant up`. It will take a few minutes to provision the machine.
##Running the project
To run the project, type `vagrant ssh`. Once you are in the development machine, run `./manage.py runserver 0:8000` and visit localhost:8000 in your host browser. Any changes you make on your host will be symlinked to your guest Vagrant machine.
