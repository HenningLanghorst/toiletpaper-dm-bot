# toiletpaper-dm-bot

## Build

You need [Apache Maven](https://maven.apache.org/) to build this project:
```shell script
mvn install
```

## Run the Telegram bot

Start the built fat jar from target folder. You must set the following properties:

| Property | Description                                                                  | 
| -------- | ---------------------------------------------------------------------------- |
| token    | The token you got from the [Botfather](https://t.me/Botfather) for your bot. |
| places   | Path to locations file from [GeoNames](https://download.geonames.org/). Download [DE.zip](https://download.geonames.org/export/dump/DE.zip), extract it somewhere and reference it.  

  

