# Arodq Custom Authentication

This project is a basis for letting you code your specific authentication for and On Premise installation of Ardoq.


The class performing the custom authentication must have an empty constructor, and must be named:
```
ardoq.auth.CustomAuthentication
```

The class must also implement the provided interface
```
ardoq.auth.ICustomAuthentication
```

The compiled jar-file is then placed in the ***[Ardoq-installation-dir]/lib*** folder on the server running the Ardoq API, and configured onto the API classpath.
