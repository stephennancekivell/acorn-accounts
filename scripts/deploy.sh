#!/bin/bash

SERVER=stephenn.com
TARGET=/home/acornaccounts/acorn-accounts
SERVICE=acorn-accounts
INIT_CONF=acorn-accounts.conf
USER=acornaccounts

PLAY="~/bin/play-2.1.0/play"

ssh $SERVER sudo service stop $SERVICE

ssh $USER@$SERVER <<EOF
	cd $TARGET
	git pull
	$PLAY clean compile stage
EOF

ssh $SERVER <<EOF
	cd $TARGET
	sudo cp scripts/$INIT_CONF /etc/init/$INIT_CONF
	sudo service start $SERVICE 
EOF