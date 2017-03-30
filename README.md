# exificient-for-json
EXI for JSON - How EXI can be used to represent JSON data efficiently (see <https://www.w3.org/TR/exi-for-json/>)

[![Build Status](https://travis-ci.org/EXIficient/exificient-for-json.svg?branch=master)](https://travis-ci.org/EXIficient/exificient-for-json)


### Compression

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