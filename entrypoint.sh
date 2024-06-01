#!/bin/sh
set -e

./script/wait-for-it.sh db:5432

bin/rails db:migrate
bin/rails s -b 0.0.0.0
