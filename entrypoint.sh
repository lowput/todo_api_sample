#!/bin/sh
set -e

./script/wait-for-it.sh db:5432

java -jar bin/server-all.jar &
./bin/server.kexe &

bin/rails db:migrate
bin/rails s -b 0.0.0.0
