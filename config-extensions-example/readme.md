This is an example of how vendor components can extend configuration and use it in decorators of dcm4che archive.

Steps a nutshell:

1. Create a class that extends `CommonAEExtension`/`CommonDeviceExtension`/`CommonHL7AppExtension` and
annotate it with `ConfigurableClass` and `ConfigurableProperty` annotations. Don't forget properly named getters/setters.
(more guidelines are here https://github.com/dcm4che/dcm4che/tree/master/dcm4che-conf/dcm4che-conf-core)

2. Make sure at least one instance of this class is available as a CDI managed bean (config engine will use .getClass() on the `javax.inject.Instance` of the bean to register the class as an extra extension),
i.e. having the class defined in a jar with empty beans.xml inside the `lib` folder of the archive ear is enough.

3. In the decorator/elsewhere in the external codebase, use `DicomConfiguration` to access device and underlying extensions,
application entities and their extension, etc.



### Modularity
This sample maven artifact contains both config extension and a decorator for simplicity reason. It is not recommended to
do such coupling on practice. Always try to keep configuration classes in a separate jar artifact so it could be then used separately
for supporting the migration mechanisms.