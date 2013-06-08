#!/bin/bash

SCRIPT_DIR=`dirname $0`

SERVER=stephenn.info
TARGET=/home/acornaccounts/server
SERVICE=acorn-accounts
INIT_CONF=acorn-accounts.conf
USER=acornaccounts
PLAY=~/bin/play-2.1.1/play

cd $SCRIPT_DIR/..
$PLAY clean compile stage


ssh $SERVER sudo service $SERVICE stop

rsync -v --recursive --delete --compress $SCRIPT_DIR/../target/staged/ $USER@$SERVER:/$TARGET/dist/staged
rsync -v --recursive --delete --compress $SCRIPT_DIR/../target/start $USER@$SERVER:/$TARGET/dist/start

scp $SCRIPT_DIR/$INIT_CONF $SERVER:.

ssh stephenn.info <<EOF
	sudo mv $INIT_CONF /etc/init/
	sudo service $SERVICE start
EOF