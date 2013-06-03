#!/bin/bash

SCRIPT_DIR=`dirname $0`

SERVER=stephenn.info
TARGET=/home/acornaccounts/server
SERVICE=password-safe-server
INIT_CONF=password-safe-server.conf

if [[ -z "$1" ]]; then
	echo "usage: deploy.sh dist.zip"
	exit 0
fi

#scp $1 $SERVER:.
DIST_FILE=`basename $1`
scp $SCRIPT_DIR/$INIT_CONF $SERVER:.

ssh stephenn.info <<EOF
	sudo service $SERVICE stop
	sudo mv $INIT_CONF /etc/init/
	rm -r $TARGET/dist/*
	unzip $DIST_FILE -d $TARGET/dist
	mv $TARGET/dist/*/* $TARGET/dist/
	chmod a+x $TARGET/dist/start
	sudo chown -R acornaccounts $TARGET
	sudo service $SERVICE start
	#rm $DIST_FILE
EOF