# jasperruner

Running jasper reports from a command line program using the jasperreports libraries.


## Rationale

Sometimes it is needed to have some more control to your jasper reports mainly to execute external code for your parameters or to create different report for each database row (instead of a large one that combines all rows). You can use this project as a base/template for such work. 

## Usage

Either open the project in Intellij Idea or directly compile and run it with maven using the instructions provided in ``runme.bat`` file.

Copy `config.properties.template` to `config.properties` and fill it accordingly. You can use different config files by passing them as an option when running the program for example like `mvn exec:java -Dexec.args="config1.properties"`

Add the parameters of your report(s) in the params.json file in the form:

```$json
[
  {"param1":  "p1v1", "param2": "p1v2},
  {"param1":  "p1v3", "param2": "p1v4},
]
```

Each of the objects in the JSON array will be used to generate a different report.

## Jasperrepots version

Notice that I'm using an old jasperreports version because of compatibility with the reports I need to compile. You can change the `pom.xml` and `Mai.java` accordingly to work with the latest jasperreport version. 