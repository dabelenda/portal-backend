#!/usr/bin/env bash


# Get gateway IP

GATEWAY_IP=$(docker inspect backend-test | grep \"Gateway\":\ \" | sed 's/.*Gateway\":\ \"\([^-]*\)\",/\1/' | head -n 1)

# if [ "$(curl -s $GATEWAY_IP:8080/services/groups)" != "$groups" ]; then
#   echo "failed to load groups"
#   exit 1
# fi

# if [ "$(curl -s $GATEWAY_IP:8080/services/variables)" != "$variables" ]; then
#   echo "failed to load variables"
#   exit 1
# fi

exit 0
