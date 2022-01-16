CREATE TABLE city_information
(
    name varchar(100) NOT NULL,
    id   int(9) NOT NULL,
    lat  float(10, 5) NOT NULL,
    lon  float(10, 5) NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE city_weather
(
    id      int(9) NOT NULL,
    fxDate  varchar(10) NOT NULL,
    tempMax int(3) NOT NULL,
    tempMin int(3) NOT NULL,
    textDay varchar(10) NOT NULL,
    PRIMARY KEY (id,fxDate)
);
