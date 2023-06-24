import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2023.05"

project {

    buildType(Build)
}

object Build : BuildType({
    name = "Build"

    params {
        select("user.configuration.name", "", label = "Parameter Name", description = "Insurance Now Configuration Parameter", display = ParameterDisplay.PROMPT,
                options = listOf("AUTH_NET_API_KEY", "BLACK_BOOK_API_KEY", "ISO_API_KEY", "ISO_PASSWORD"))
        //password("user.configuration.value", "credentialsJSON:313af02f-06ea-4894-bb6f-cfe5c023af5e", label = "Parameter value", description = "user configuration value", display = ParameterDisplay.PROMPT)
        password("user.configuration.value", "", label = "Parameter value", description = "user configuration value", display = ParameterDisplay.PROMPT)

    }

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "TestBuildParamCMDLIne"
            scriptContent = """
                echo %user.configuration.name%
                echo %user.configuration.value%
            """.trimIndent()
        }
        maven {
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})
