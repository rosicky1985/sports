CREATE DATABASE `sports`;

use `sports`;

grant all privileges on `sports`.* to rosicky@localhost identified by 'nbb';

CREATE TABLE `stock_sina` (
  `name` varchar(45) NOT NULL,
  `code` varchar(45) NOT NULL,
  `price` varchar(45) NOT NULL,
  `float` varchar(15) NOT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY(`code`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;