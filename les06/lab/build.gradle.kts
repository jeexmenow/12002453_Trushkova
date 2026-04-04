tasks.register("run") {
    group = "application"
    description = "Runs the pet store CLI (delegates to :app:run)"
    dependsOn(":app:run")
}
