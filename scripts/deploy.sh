#!/bin/bash

SCRIPT_DIR=`dirname $0`

SERVER=stephenn.info
TARGET=/home/acornaccounts/server
SERVICE=acorn-accounts
INIT_CONF=acorn-accounts.conf
USER=acornaccounts

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
	sudo chown -R $USER $TARGET
	sudo service $SERVICE start
	#rm $DIST_FILE
EOF