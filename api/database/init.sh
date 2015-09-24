# !/bin/bash

user=root

host=localhost

mysql -u$user -e'drop database sports';

mysql -u$user < create.sql

