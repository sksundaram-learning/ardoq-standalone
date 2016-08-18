# Arodq Custom Authentication

This project is a bases for letting you code your specific authentication for Ardoq.

The class *must* be named:
```
ardoq.auth.CustomAuthentication
```

And the metod signature *must* be excactly:
```
public static boolean authenticate(String username, String password)
```


The jar-file is then placed in a folder on the server running the Ardoq API, and configured onto the API classpath.
