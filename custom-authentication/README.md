# Arodq Custom Authentication

This project is a basis for letting you code your specific authentication for and On Premise installation of Ardoq.


The class performing the custom authentication must have an empty constructor, and must be named:
```
ardoq.auth.CustomAuthentication
```

The class should also implement the provided interface unmodified [ardoq.auth.ICustomAuthentication](src/main/java/ardoq/auth/ICustomAuthentication.java) to ensure it will be compatible with Ardoq. 

The compiled jar-file is then placed in the ***[Ardoq-installation-dir]/lib*** folder on the server running the Ardoq API, and configured onto the API classpath.

To verify that the jarfile is installed correctly and is picked up by the API, check the API status page:

```
$ curl -XGET http://localhost/api/status | jq '.'

{
  version: "1.38.2",
  build: "3d5d39c66439bb6f4bc4a2c8635a019a59e40c01",
  analytics: "",
  custom-authentication: "ok",
  env: "local",
  status: "Running"
}
```