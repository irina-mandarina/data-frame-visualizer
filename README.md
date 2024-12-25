DataFrame structure can be hierarchical. It can host essentially any data, including long String, BufferedImage, etc. People might choose to read data from JSON, CSV, Excel, or SQL databases.
To visualize DataFrame, we have toString, schema().toString, toHTML, and a table component in Kotlin Notebook.
Share your thoughts, what do you think is good and bad about these methods? Think of a way you'd improve it, or suggest a different way to visualize DataFrame.
Implement a showcase of this idea in code. Your implementation shouldn't include many features. Focus on specific things you see as important for a use case. For example: working with a bunch of files, parsing data from websites, working with many numerical columns, etc.
Use DataFrame as input. Output can be in Swing, Compose, HTML.

## Analysis 
### toString
What's good: 
* Quick and simple to implement
* Easy to use in the console when debugging when using smaller datasets

What's bad:
* Not particularly useful when working with larger datasets
* Not customizable - lacks features like filtering and sorting after displaying

### schema().toString
What's good:
* Quick and easy implementation of the method
* Useful when the need to visualise the schema in the console is needed 

What's bad:
* Not useful for working with larger datasets - ones with a lot of columns

### toHTML
What's good:
* Can provide a more readable visualization if styled (which can be done easily)
* Suitable for most environment

What's bad:
* Can require additional setup of non-web-based environments

### table component in Kotlin Notebook
What's good:
* Interactive and flexible - provides sorting, pagination, and hiding columns
* Works well with nested data - it can be shown easily
* The features it provides can be used for visualizing large datasets
* The result can be exported into CSV, XML or JSON

What's bad:
* Can require additional setup (installing the Kotlin Notebook plugin)


### Conclusion
The table component is the most flexible when it comes to visualizing dataframes. This aspect is important because it allows for interactivity and handles large datasets effectively, which are key requirements in data exploration and analysis. However, the reliance on the Kotlin Notebook environment can limit its portability.

### Recommendation
I suggest a feature that allows creating graphs easily from the data in the DataFrame. This would be useful for data analysis and visualization, especially when working with numerical data.

Added options:
* searching for a value in a column
* creating graphs 
* sorting by a column
* viewing statistics for a column: mean, median, standard deviation, etc.

Future improvements for my solution:
* adding more graph types
* adding more customization options for the graphs
* using ML to recommend graphs based on the data
* adding more interactivity to the graphs
* a more user-friendly interface. Right now, the UI is very basic with the main purpose of proving the concept.
* adding more data sources like websites, different databases
* improved visualization of hierarchical data


### Notes
* The solution is a proof of concept and is not meant to be a full-fledged application.
* I have added some sample files for testing the solution.

[//]: # (End of README.md)
