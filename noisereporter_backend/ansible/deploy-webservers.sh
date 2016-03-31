#!/bin/bash
ansible-playbook deploy.yml -i hosts -u root -vvv --ask-vault-pass
