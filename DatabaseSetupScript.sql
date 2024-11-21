CREATE SCHEMA rateflix ;

CREATE TABLE rateflix.reviews (
  id VARCHAR(200) NOT NULL,
  customer_id VARCHAR(200) NULL,
  rating INT NULL,
  timestamp TIMESTAMP NULL,
  review VARCHAR(500) NULL,
  PRIMARY KEY (id));
  
  CREATE TABLE rateflix.users (
  username VARCHAR(20) NOT NULL,
  password VARCHAR(20) NULL,
  PRIMARY KEY (username));