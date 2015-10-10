CREATE DATABASE `sports`;

use `sports`;

grant all privileges on `sports`.* to rosicky@localhost identified by 'nbb';

CREATE TABLE IF NOT EXISTS `stock_sina`  (
  `name` VARCHAR(45) NOT NULL,
  `code` VARCHAR(45) NOT NULL,
  `price` VARCHAR(45) NOT NULL,
  `float` VARCHAR(15) NOT NULL,
  `time` DATETIME NOT NULL,
  PRIMARY KEY(`code`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `news_baidu` (
  `created` DATETIME NOT NULL,
  `key` VARCHAR(45) NOT NULL,
  `category` VARCHAR(70) NOT NULL,
  `id` INT NOT NULL,
  `title` VARCHAR(100) NOT NULL,
  `author` VARCHAR(30),
  `summary` VARCHAR(300) NOT NULL,
  `s_targetTime` VARCHAR(20) NOT NULL,
  `d_targetTime` DATETIME,
  `follow_num` INT,
  `follow_href` VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
