#!/usr/bin/env bash

REPO_HOME=~/repositories
REPO_DIRECTORY=$REPO_HOME/holotable
REPO_URI=https://github.com/swccgpc/holotable.git

mkdir -p /env/gemp-swccg/web/images/cards
mkdir -p $REPO_HOME

# Clone image repo if needed
if [ ! -d "$REPO_DIRECTORY" ]; then
    cd $REPO_HOME
    git clone $REPO_URI
fi

cd $REPO_DIRECTORY

echo "Pulling latest images"
git pull

for dir in $REPO_DIRECTORY/Images-HT/starwars/*
do
    if [[ -d $dir ]]; then
        dir_name=$(basename $dir)
        target_dir=/env/gemp-swccg/web/images/cards/$dir_name
        printf "\n"
        echo Copying $dir_name
        echo ------------------------------
        mkdir -p $target_dir
        rsync -av $dir/large/ $target_dir
    fi
done;

echo 'Done Updating Card Images!'
