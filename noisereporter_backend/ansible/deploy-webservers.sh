#!/bin/bash
ansible-playbook deploy.yml -i hosts -u root -v --ask-vault-pass
