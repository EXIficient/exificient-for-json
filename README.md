# Overview 

**_exificient-for-json_** is an implementation of the EXI4JSON specification (see <https://www.w3.org/TR/exi-for-json/>) in Java. EXI4JSON converts JSON to event streams which result in a very processing efficient and compact format. Lossless round-trip conversion back to the original JSON structures is supported. EXI4JSON is based on [EXI](https://www.w3.org/TR/exi/) and uses its [built-in datatypes](https://www.w3.org/TR/exi/#encodingDatatypes).

The project uses MIT license.

[![Build Status](https://travis-ci.org/EXIficient/exificient-for-json.svg?branch=master)](https://travis-ci.org/EXIficient/exificient-for-json)


## Background

Due to its EXI/XML nature EXI4JSON can be serialized/visualized as XML. Most of the use-case do not need this features but in some situation it may turn out very hand.

A JSON example 
```
{
  "keyNumber": 123,
  "keyArrayStrings": [
    "s1",
    "s2"
  ]
}
```

can be transformed to 

```
<j:map xmlns:j="http://www.w3.org/2015/EXI/json">
  <j:keyNumber>
    <j:number>123</j:number>
  </j:keyNumber>
  <j:keyArrayStrings>
    <j:array>
      <j:string>s1</j:string>
      <j:string>s2</j:string>
    </j:array>
  </j:keyArrayStrings>
</j:map>
```


## How to get it

Released with Maven

```
<dependency>
    <groupId>com.siemens.ct.exi</groupId>
    <artifactId>exificient-for-json</artifactId>
    <version>0.9.7</version>
</dependency>
```

or also as snapshot.

```
<dependency>
    <groupId>com.siemens.ct.exi</groupId>
    <artifactId>exificient-for-json</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## How to use the library

### Java API for JSON Processing

```java
// generate by simply providing input and output
EXIforJSONGenerator e4jGenerator = new EXIforJSONGenerator();
e4jGenerator.generate(isJSON, osEXI4JSON);
// Note: one can also fire events like writeStartObject(),  writeKeyName(key) etc

// parse by simply providing input and output again
EXIforJSONParser e4jParser = new EXIforJSONParser();
e4jParser.parse(isEXI4JSON, osJSON);
// Note: one can again read events by readNextEvent(), getKeyName(),  getValueString() etc
```

### [Jackson](https://github.com/FasterXML) 

```java
EXI4JSONFactory fEXI = new EXI4JSONFactory();
ObjectMapper mapperEXI = new ObjectMapper(fEXI);
ObjectMapper mapperJSON = new ObjectMapper();

String carJson =
    "{ \"brand\" : \"Mercedes\", \"doors\" : 5 }";
JsonNode car = mapperJSON.readTree(carJson); 

// generate
OutputStream e4jOS = ...; // output stream
mapperEXI.writeTree(fEXI.createGenerator(e4jOS), car);
// parse
OutputStream e4jIS = ...; // input stream
EXI4JSONParser eparser = fEXI.createParser(e4jIS);
JsonNode carAgain = mapperJSON.readTree(eparser);
```


## Compression Results

See test-data in https://github.com/EXIficient/exificient-for-json/tree/master/src/test/resources.

| TestCase                                              | JSON [Size in Bytes]    | CBOR [Size in Bytes]   | Smile [Size in Bytes]   | EXI4JSON [Size in Bytes] |
|-------------------------------------------------------|----------|---------|---------|----------|
| glossary.json                                         | 605      | 311     | 309     | 322      |
| gpx\sample-set-1\gpx-1-100pts.json                    | 11007    | 7378    | 6592    | 6210     |
| gpx\sample-set-1\gpx-1-10pts.json                     | 1318     | 898     | 832     | 778      |
| gpx\sample-set-1\gpx-1-1pts.json                      | 314      | 248     | 254     | 223      |
| gpx\sample-set-1\gpx-1-200pts.json                    | 21661    | 14578   | 12992   | 12179    |
| gpx\sample-set-1\gpx-1-20pts.json                     | 2424     | 1618    | 1472    | 1395     |
| gpx\sample-set-1\gpx-1-2pts.json                      | 426      | 322     | 320     | 284      |
| gpx\sample-set-1\gpx-1-300pts.json                    | 32452    | 21778   | 19392   | 18216    |
| gpx\sample-set-1\gpx-1-30pts.json                     | 3480     | 2338    | 2112    | 1984     |
| gpx\sample-set-1\gpx-1-3pts.json                      | 537      | 394     | 384     | 346      |
| gpx\sample-set-1\gpx-1-400pts.json                    | 43257    | 28978   | 25792   | 24239    |
| gpx\sample-set-1\gpx-1-40pts.json                     | 4562     | 3058    | 2752    | 2588     |
| gpx\sample-set-1\gpx-1-4pts.json                      | 648      | 466     | 448     | 408      |
| gpx\sample-set-1\gpx-1-500pts.json                    | 53988    | 36178   | 32192   | 30256    |
| gpx\sample-set-1\gpx-1-50pts.json                     | 5634     | 3778    | 3392    | 3191     |
| gpx\sample-set-1\gpx-1-5pts.json                      | 760      | 538     | 512     | 469      |
| gpx\sample-set-1\gpx-1-600pts.json                    | 64758    | 43378   | 38592   | 36284    |
| gpx\sample-set-1\gpx-1-60pts.json                     | 6720     | 4498    | 4032    | 3801     |
| gpx\sample-set-1\gpx-1-6pts.json                      | 872      | 610     | 576     | 531      |
| gpx\sample-set-1\gpx-1-700pts.json                    | 75518    | 50578   | 44992   | 42285    |
| gpx\sample-set-1\gpx-1-70pts.json                     | 7778     | 5218    | 4672    | 4396     |
| gpx\sample-set-1\gpx-1-7pts.json                      | 983      | 682     | 640     | 593      |
| gpx\sample-set-1\gpx-1-800pts.json                    | 86292    | 57778   | 51392   | 48293    |
| gpx\sample-set-1\gpx-1-80pts.json                     | 8859     | 5938    | 5312    | 5000     |
| gpx\sample-set-1\gpx-1-870pts.json                    | 93786    | 62818   | 55872   | 52476    |
| gpx\sample-set-1\gpx-1-8pts.json                      | 1094     | 754     | 704     | 655      |
| gpx\sample-set-1\gpx-1-90pts.json                     | 9942     | 6658    | 5952    | 5609     |
| gpx\sample-set-1\gpx-1-9pts.json                      | 1206     | 826     | 768     | 716      |
| json-generator.com\2015-01-06\01.json                 | 1103353  | 490133  | 351756  | 310012   |
| json-generator.com\2015-01-06\02.json                 | 2203693  | 979469  | 703115  | 606056   |
| json-generator.com\2015-01-06\03.json                 | 3238056  | 1438370 | 1032056 | 881484   |
| json-generator.com\2015-01-06\04.json                 | 4320187  | 1918771 | 1376620 | 1168143  |
| json-generator.com\2015-01-06\05.json                 | 5425885  | 2409849 | 1728913 | 1461451  |
| json-generator.com\2015-01-06\06.json                 | 6509681  | 2890828 | 2073670 | 1749445  |
| json-generator.com\2015-01-06\07.json                 | 7620054  | 3383755 | 2427181 | 2044025  |
| json-generator.com\2015-01-06\08.json                 | 8880856  | 3943588 | 2828669 | 2379084  |
| json-generator.com\2015-01-06\09.json                 | 9982416  | 4433013 | 3179808 | 2672229  |
| json-generator.com\2015-01-06\10.json                 | 11080993 | 4920626 | 3529384 | 2965358  |
| openweathermap.org\sample-set-1\owm-1-1000cities.json | 622540   | 514167  | 311697  | 223442   |
| openweathermap.org\sample-set-1\owm-1-100cities.json  | 62024    | 51074   | 30877   | 23032    |
| openweathermap.org\sample-set-1\owm-1-10cities.json   | 6170     | 5069    | 3205    | 2629     |
| openweathermap.org\sample-set-1\owm-1-1cities.json    | 615      | 502     | 464     | 486      |
| openweathermap.org\sample-set-1\owm-1-200cities.json  | 123811   | 101857  | 61272   | 44954    |
| openweathermap.org\sample-set-1\owm-1-2cities.json    | 1231     | 1011    | 773     | 726      |
| openweathermap.org\sample-set-1\owm-1-300cities.json  | 185699   | 152787  | 91855   | 67573    |
| openweathermap.org\sample-set-1\owm-1-400cities.json  | 247718   | 203877  | 122640  | 90258    |
| openweathermap.org\sample-set-1\owm-1-500cities.json  | 309532   | 254791  | 153303  | 112848   |
| openweathermap.org\sample-set-1\owm-1-50cities.json   | 31048    | 25557   | 15534   | 11820    |
| openweathermap.org\sample-set-1\owm-1-5cities.json    | 3136     | 2587    | 1728    | 1535     |
| openweathermap.org\sample-set-1\owm-1-600cities.json  | 371839   | 306314  | 184489  | 133633   |
| openweathermap.org\sample-set-1\owm-1-700cities.json  | 433746   | 357313  | 215231  | 156390   |
| openweathermap.org\sample-set-1\owm-1-800cities.json  | 498084   | 411365  | 249407  | 179022   |
| openweathermap.org\sample-set-1\owm-1-900cities.json  | 560315   | 462826  | 280620  | 201229   |


On average "EXI4JSON" is about 48% of the original JSON filesize!
On average "EXI4JSON" is about 50% smaller than the CBOR binary encoded file!

![Compression Figure](compression.png)
